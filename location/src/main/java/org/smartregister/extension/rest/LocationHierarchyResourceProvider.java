/*
 * Copyright 2021 Ona Systems, Inc
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
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.StringType;
import org.smartregister.model.location.LocationHierarchy;
import org.smartregister.model.location.LocationHierarchyTree;
import org.smartregister.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;

public class LocationHierarchyResourceProvider implements IResourceProvider {

    @Autowired IFhirResourceDao<Location> locationIFhirResourceDao;

    private static final Logger logger =
            Logger.getLogger(LocationHierarchyResourceProvider.class.toString());

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return LocationHierarchy.class;
    }

    @Search
    public LocationHierarchy getLocationHierarchy(
            @RequiredParam(name = Constants.ID) TokenParam id) {

        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(Constants.ID, id);

        Location location = locationIFhirResourceDao.read(new IdType(id.getValue()));
        String locationId =
                location != null ? location.getIdElement().getIdPart() : Constants.EMPTY_STRING;
        LocationHierarchyTree locationHierarchyTree = new LocationHierarchyTree();
        LocationHierarchy locationHierarchy = new LocationHierarchy();

        if (StringUtils.isNotBlank(locationId)) {

            logger.info("Building Location Hierarchy of Location Id : " + locationId);
            locationHierarchyTree.buildTreeFromList(getLocationHierarchy(locationId, location));

            StringType locationIdString = new StringType().setId(locationId).getIdElement();
            locationHierarchy.setLocationId(locationIdString);
            locationHierarchy.setId(Constants.LOCATION_RESOURCE + locationId);

            locationHierarchy.setLocationHierarchyTree(locationHierarchyTree);
        } else {
            locationHierarchy.setId(Constants.LOCATION_RESOURCE_NOT_FOUND);
        }
        return locationHierarchy;
    }

    private List<Location> getLocationHierarchy(String locationId, IBaseResource parentLocation) {
        return descendants(locationId, parentLocation);
    }

    public List<Location> descendants(String parentLocationId, IBaseResource parentLocation) {

        SearchParameterMap paramMap = new SearchParameterMap();
        ReferenceAndListParam thePartOf = new ReferenceAndListParam();
        ReferenceParam partOf = new ReferenceParam();
        partOf.setValue(Constants.LOCATION + Constants.FORWARD_SLASH + parentLocationId);
        ReferenceOrListParam referenceOrListParam = new ReferenceOrListParam();
        referenceOrListParam.add(partOf);
        thePartOf.addValue(referenceOrListParam);
        paramMap.add(Constants.PART_OF, thePartOf);

        IBundleProvider childLocationBundle = locationIFhirResourceDao.search(paramMap);
        List<Location> allLocations = Collections.synchronizedList(new ArrayList<>());
        if (parentLocation != null) {
            allLocations.add((Location) parentLocation);
        }
        if (childLocationBundle != null) {
            List<IBaseResource> childLocations =
                    childLocationBundle.getResources(0, childLocationBundle.size());

            childLocations.parallelStream()
                    .forEach(
                            childLocation -> {
                                Location childLocationEntity = (Location) childLocation;
                                allLocations.add(childLocationEntity);
                                allLocations.addAll(
                                        descendants(
                                                childLocation.getIdElement().getIdPart(), null));
                            });
        }

        return allLocations;
    }

    public void setLocationIFhirResourceDao(IFhirResourceDao<Location> locationIFhirResourceDao) {
        this.locationIFhirResourceDao = locationIFhirResourceDao;
    }
}
