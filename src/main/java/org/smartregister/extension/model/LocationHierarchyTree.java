package org.smartregister.extension.model;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.util.ElementUtil;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@DatatypeDef(name = "LocationHierarchyTree")
public class LocationHierarchyTree extends Type implements ICompositeType {

    @Child(name = "locationsHierarchy")
    private Tree locationsHierarchy;

    public LocationHierarchyTree() {
        this.locationsHierarchy = new Tree();
    }

    public void addLocation(Location l) {
        StringType idString = new StringType();
        idString.setValue(l.getId());
        if (!locationsHierarchy.hasNode(idString.getValue())) {
            if (l.getPartOf() == null || StringUtils.isEmpty(l.getPartOf().getReference())) {
                locationsHierarchy.addNode(idString.getValue(), l.getName(), l, null);
            } else {
                //get Parent Location
                StringType parentId = new StringType();
                parentId.setValue(l.getPartOf().getReference());
                locationsHierarchy.addNode(idString.getValue(), l.getName(), l, parentId.getValue());
            }
        }
    }

    /**
     * WARNING: Overrides existing locations
     *
     * @param locations
     */
    public void buildTreeFromList(List<Location> locations) {
        for (Location location : locations) {
            addLocation(location);
        }
    }

    public Tree getLocationsHierarchy() {
        return locationsHierarchy;
    }

    public LocationHierarchyTree setLocationsHierarchy(Tree locationsHierarchy) {
        this.locationsHierarchy = locationsHierarchy;
        return this;
    }


    @Override
    public Type copy() {
        LocationHierarchyTree locationHierarchyTree = new LocationHierarchyTree();
        copyValues(locationHierarchyTree);
        return locationHierarchyTree;
    }

    @Override
    public boolean isEmpty() {
        return ElementUtil.isEmpty(locationsHierarchy);
    }

    @Override
    protected Type typedCopy() {
        return copy();
    }
}

