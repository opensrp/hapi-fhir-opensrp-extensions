/*
 * Copyright 2022 Ona Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartregister.extension.rest;

import static org.smartregister.utils.Constants.FORWARD_SLASH;
import static org.smartregister.utils.Constants.IDENTIFIER;
import static org.smartregister.utils.Constants.LOCATION;
import static org.smartregister.utils.Constants.LOCATION_RESOURCE;
import static org.smartregister.utils.Constants.LOCATION_RESOURCE_NOT_FOUND;
import static org.smartregister.utils.Constants.PART_OF;

import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.ReferenceOrListParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.StringType;
import org.smartregister.model.location.LocationHierarchy;
import org.smartregister.model.location.LocationHierarchyTree;
import org.springframework.beans.factory.annotation.Autowired;

public class LocationHierarchyResourceProvider implements IResourceProvider {

	private static final Logger logger =
			LogManager.getLogger(LocationHierarchyResourceProvider.class.toString());

	@Autowired IFhirResourceDao<Location> locationIFhirResourceDao;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return LocationHierarchy.class;
	}

	@Search
	public LocationHierarchy getLocationHierarchy(
			@RequiredParam(name = IDENTIFIER) TokenParam identifier) {

		SearchParameterMap paramMap = new SearchParameterMap();
		paramMap.add(IDENTIFIER, identifier);

		IBundleProvider locationBundle = locationIFhirResourceDao.search(paramMap);
		List<IBaseResource> locations =
				locationBundle != null
						? locationBundle.getResources(0, locationBundle.size())
						: new ArrayList<>();
		Long id = null;
		if (locations.size() > 0) {
			id =
					locations.get(0) != null && locations.get(0).getIdElement() != null
							? locations.get(0).getIdElement().getIdPartAsLong()
							: null;
		}

		LocationHierarchyTree locationHierarchyTree = new LocationHierarchyTree();
		LocationHierarchy locationHierarchy = new LocationHierarchy();
		if (id != null && locations.size() > 0) {
			logger.info("Building Location Hierarchy of Location Id : " + id);
			locationHierarchyTree.buildTreeFromList(getLocationHierarchy(id, locations.get(0)));
			StringType locationIdString = new StringType().setId(id.toString()).getIdElement();
			locationHierarchy.setLocationId(locationIdString);
			locationHierarchy.setId(LOCATION_RESOURCE + id);

			locationHierarchy.setLocationHierarchyTree(locationHierarchyTree);
		} else {
			locationHierarchy.setId(LOCATION_RESOURCE_NOT_FOUND);
		}
		return locationHierarchy;
	}

	private List<Location> getLocationHierarchy(Long id, IBaseResource parentLocation) {
		return descendants(id, parentLocation);
	}

	public List<Location> descendants(Long id, IBaseResource parentLocation) {

		SearchParameterMap paramMap = new SearchParameterMap();
		ReferenceAndListParam thePartOf = new ReferenceAndListParam();
		ReferenceParam partOf = new ReferenceParam();
		partOf.setValue(LOCATION + FORWARD_SLASH + id);
		ReferenceOrListParam referenceOrListParam = new ReferenceOrListParam();
		referenceOrListParam.add(partOf);
		thePartOf.addValue(referenceOrListParam);
		paramMap.add(PART_OF, thePartOf);

		IBundleProvider childLocationBundle = locationIFhirResourceDao.search(paramMap);
		List<Location> allLocations = new ArrayList<>();
		if (parentLocation != null) {
			allLocations.add((Location) parentLocation);
		}
		if (childLocationBundle != null) {
			for (IBaseResource childLocation :
					childLocationBundle.getResources(0, childLocationBundle.size())) {
				Location childLocationEntity = (Location) childLocation;
				allLocations.add(childLocationEntity);
				allLocations.addAll(
						descendants(childLocation.getIdElement().getIdPartAsLong(), null));
			}
		}

		return allLocations;
	}

	public void setLocationIFhirResourceDao(IFhirResourceDao<Location> locationIFhirResourceDao) {
		this.locationIFhirResourceDao = locationIFhirResourceDao;
	}
}
