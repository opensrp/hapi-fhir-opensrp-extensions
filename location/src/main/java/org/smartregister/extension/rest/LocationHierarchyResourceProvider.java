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

import static org.smartregister.utils.Constants.*;

import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.*;
import ca.uhn.fhir.rest.server.IResourceProvider;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.smartregister.model.location.LocationHierarchy;
import org.smartregister.model.location.LocationHierarchyTree;
import org.springframework.beans.factory.annotation.Autowired;

public class LocationHierarchyResourceProvider implements IResourceProvider {

    @Autowired IFhirResourceDao<Location> locationIFhirResourceDao;

    private static final Logger logger =
            LogManager.getLogger(LocationHierarchyResourceProvider.class.toString());

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return LocationHierarchy.class;
    }

    @Search
    public LocationHierarchy getLocationHierarchy(@RequiredParam(name = ID) TokenParam id) {

        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(ID, id);

        Location location = locationIFhirResourceDao.read(new IdType(id.getValue()));
        String locationId = location != null ? location.getIdElement().getIdPart() : EMPTY_STRING;
        LocationHierarchyTree locationHierarchyTree = new LocationHierarchyTree();
        LocationHierarchy locationHierarchy = new LocationHierarchy();

        if (StringUtils.isNotBlank(locationId)) {
            logger.info("Building Location Hierarchy of Location Id : " + locationId);
            locationHierarchyTree.buildTreeFromList(getLocationHierarchy(locationId, location));
            StringType locationIdString = new StringType().setId(locationId).getIdElement();
            locationHierarchy.setLocationId(locationIdString);
            locationHierarchy.setId(LOCATION_RESOURCE + locationId);

            locationHierarchy.setLocationHierarchyTree(locationHierarchyTree);
        } else {
            locationHierarchy.setId(LOCATION_RESOURCE_NOT_FOUND);
        }
        return locationHierarchy;
    }

    private List<Location> getLocationHierarchy(String locationId, IBaseResource parentLocation) {
        return descendants(locationId, parentLocation);
    }

    public List<Location> descendants(String locationId, IBaseResource parentLocation) {

        SearchParameterMap paramMap = new SearchParameterMap();
        ReferenceAndListParam thePartOf = new ReferenceAndListParam();
        ReferenceParam partOf = new ReferenceParam();
        partOf.setValue(LOCATION + FORWARD_SLASH + locationId);
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
                allLocations.addAll(descendants(childLocation.getIdElement().getIdPart(), null));
            }
        }

        return allLocations;
    }

    public void setLocationIFhirResourceDao(IFhirResourceDao<Location> locationIFhirResourceDao) {
        this.locationIFhirResourceDao = locationIFhirResourceDao;
    }
}
