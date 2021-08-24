package org.smartregister.extension.rest;

import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.*;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.smartregister.extension.model.LocationHierarchy;
import org.smartregister.extension.model.LocationHierarchyTree;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class LocationHierarchyResourceProvider implements IResourceProvider {

    @Autowired
    IFhirResourceDao<Location> locationIFhirResourceDao;

    private static Logger logger = LogManager.getLogger(LocationHierarchyResourceProvider.class.toString());

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return LocationHierarchy.class;
    }

    @Search
    public LocationHierarchy getLocationHierarchy(HttpServletRequest request,
                                                  @OptionalParam(name = "identifier") TokenParam identifier) {

        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add("identifier", identifier);

        IBundleProvider locationBundle = locationIFhirResourceDao.search(paramMap);
        List<IBaseResource> locations = locationBundle != null ?
                locationBundle.getResources(0, locationBundle.size()) : new ArrayList<>();
        Long id = null;
        if (locations.size() > 0) {
            id = locations.get(0).getIdElement().getIdPartAsLong();
        }

        LocationHierarchyTree locationHierarchyTree = new LocationHierarchyTree();
        LocationHierarchy locationHierarchy = new LocationHierarchy();
        if (id != null && locations.size() > 0) {
            logger.info("Building Location Hierarchy of Location Id : " + id);
            locationHierarchyTree.buildTreeFromList(getLocationHierarchy(id, locations.get(0)));
            StringType locationIdString = new StringType().setId(id.toString()).getIdElement();
            locationHierarchy.setLocationId(locationIdString);
            locationHierarchy.setId("Location Resource:" + id);

            locationHierarchy.setLocationHierarchyTree(locationHierarchyTree);
        }
        else {
            locationHierarchy.setId("Location Resource : Not Found");
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
        partOf.setValue("Location/" + id);
        ReferenceOrListParam referenceOrListParam = new ReferenceOrListParam();
        referenceOrListParam.add(partOf);
        thePartOf.addValue(referenceOrListParam);
        paramMap.add("partof", thePartOf);

        IBundleProvider childLocationBundle = locationIFhirResourceDao.search(paramMap);
        List<Location> allLocations = new ArrayList<>();
        if (parentLocation != null) {
            allLocations.add((Location) parentLocation);
        }
        for (IBaseResource childLocation : childLocationBundle.getResources(0, childLocationBundle.size())) {
            Location childLocationEntity = (Location) childLocation;
            allLocations.add(childLocationEntity);
            allLocations.addAll(descendants(childLocation.getIdElement().getIdPartAsLong(), null));
        }
        return allLocations;
    }

    public void setLocationIFhirResourceDao(IFhirResourceDao<Location> locationIFhirResourceDao) {
        this.locationIFhirResourceDao = locationIFhirResourceDao;
    }
}
