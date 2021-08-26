package org.smartregister.extension.model;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;

@ResourceDef(name = "LocationHierarchy", profile = "http://hl7.org/fhir/profiles/custom-resource")
public class LocationHierarchy extends Location {

	@Child(
			name = "locationId",
			type = { StringType.class },
			order = 5,
			min = 0,
			max = 1,
			modifier = false,
			summary = true
	)
	@Description(
			shortDefinition = "Unique id to the location",
			formalDefinition = "Id of the location whose location hierarchy will be displayed."
	)
	protected StringType locationId;

	@Child(name = "LocationHierarchyTree", type = { LocationHierarchyTree.class })
	@Description(
			shortDefinition = "Complete Location Hierarchy Tree",
			formalDefinition = "Consists of Location Hierarchy Tree and Parent Child Identifiers List"
	)
	private LocationHierarchyTree locationHierarchyTree;

	@Override
	public Location copy() {
		Location dst = new Location();
		Bundle bundle = new Bundle();
		List<Bundle.BundleEntryComponent> theEntry = new ArrayList<>();
		Bundle.BundleEntryComponent entryComponent = new Bundle.BundleEntryComponent();
		entryComponent.setResource(new Bundle());
		theEntry.add(entryComponent);
		bundle.setEntry(theEntry);
		this.copyValues(dst);
		return dst;
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.Bundle;
	}

	public StringType getLocationId() {
		return locationId;
	}

	public void setLocationId(StringType locationId) {
		this.locationId = locationId;

	}

	public LocationHierarchyTree getLocationHierarchyTree() {
		return locationHierarchyTree;
	}

	public void setLocationHierarchyTree(LocationHierarchyTree locationHierarchyTree) {
		this.locationHierarchyTree = locationHierarchyTree;
	}

}
