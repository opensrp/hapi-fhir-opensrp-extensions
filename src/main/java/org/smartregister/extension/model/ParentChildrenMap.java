package org.smartregister.extension.model;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.util.ElementUtil;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;

import java.util.List;

@DatatypeDef(name = "ParentChildrenMap")
public class ParentChildrenMap extends Type implements ICompositeType {

	@Child(name = "identifier", type = { StringType.class }, order = 0, min = 1, max = 1, modifier = false, summary = false)
	private StringType identifier;

	@Child(name = "childIdentifiers", type = { StringType.class },
			order = 1,
			min = 0,
			max = -1,
			modifier = false,
			summary = false)
	private List<StringType> childIdentifiers;

	public StringType getIdentifier() {
		return identifier;
	}

	public ParentChildrenMap setIdentifier(StringType identifier) {
		this.identifier = identifier;
		return this;
	}

	public List<StringType> getChildIdentifiers() {
		return childIdentifiers;
	}

	public ParentChildrenMap setChildIdentifiers(List<StringType> childIdentifiers) {
		this.childIdentifiers = childIdentifiers;
		return this;
	}

	@Override
	public Type copy() {
		ParentChildrenMap parentChildrenMap = new ParentChildrenMap();
		copyValues(parentChildrenMap);
		return parentChildrenMap;
	}

	@Override
	public boolean isEmpty() {
		return ElementUtil.isEmpty(identifier);
	}

	@Override
	protected Type typedCopy() {
		return copy();
	}

}
