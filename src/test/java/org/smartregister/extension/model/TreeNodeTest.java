package org.smartregister.extension.model;

import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.StringType;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class TreeNodeTest {

	@Test
	public void testAddingChild() {

        StringType rootNodeName = new StringType();
        rootNodeName.setValue("Root Node");

        StringType rootNodeId = new StringType();
        rootNodeId.setValue("Location/1");

        StringType rootNodeLabel = new StringType();
        rootNodeLabel.setValue("Root Location Node");

        Location location = new Location();
        location.setId("Location/1");

        StringType childNodeName = new StringType();
        childNodeName.setValue("Child Node");

        StringType childNodeId = new StringType();
        childNodeId.setValue("Location/2");

        StringType childNodeLabel = new StringType();
        childNodeLabel.setValue("Child Location Node");

		TreeNode rootNode = new TreeNode(rootNodeName, rootNodeId, rootNodeLabel, location, null);
		TreeNode childNode = new TreeNode(childNodeName, childNodeId, childNodeLabel, location, rootNodeId);
		rootNode.addChild(childNode);

		assertEquals(childNodeId.getValue(),  rootNode.findChild(childNodeId.getValue()).getNodeId().getValue());
		assertEquals(rootNodeId.getValue(),  rootNode.findChild(childNodeId.getValue()).getParent().getValue());
	}

	@Test
	public void findInvalidChildren() {
        StringType rootNodeName = new StringType();
        rootNodeName.setValue("Root Node");

        StringType rootNodeId = new StringType();
        rootNodeId.setValue("Location/1");

        StringType rootNodeLabel = new StringType();
        rootNodeLabel.setValue("Root Location Node");

        Location location = new Location();
        location.setId("Location/1");

        TreeNode rootNode = new TreeNode(rootNodeName, rootNodeId, rootNodeLabel, location, null);
		assertEquals(0, rootNode.getChildren().size());
		assertNull(rootNode.findChild("Location/2"));
	}
}
