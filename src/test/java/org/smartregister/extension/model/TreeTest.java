package org.smartregister.extension.model;


import org.hl7.fhir.r4.model.Location;
import org.junit.Test;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class TreeTest {
	
	@Test
	public void testAddingNodeWithOutParent() {
		Tree tree = new Tree();
		Location location = new Location();
		location.setId("testId");
		tree.addNode("Location/1", "test", location, null);
		
		TreeNode treeNode = tree.getNode("Location/1");
		
		assertEquals("Location/1",  treeNode.getNodeId().getValue());
		assertEquals("test", treeNode.getLabel().getValue());
		assertEquals(location,  treeNode.getNode());
		assertNull(treeNode.getParent().getValue());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCannotReAddExistingNode() {
		Tree tree = new Tree();
		Location location = new Location();
		location.setId("testId");
		tree.addNode("Location/1", "test", location, null);
		tree.addNode("Location/1", "test", location, null);
	}

	@Test
	public void testAddingNodeWithValidParent() {
		Tree tree = new Tree();
		Location location = new Location();
		location.setId("testId");
		tree.addNode("Location/1", "test", location, null);
		tree.addNode("Location/2", "test2", location, "Location/1");

		TreeNode childNode = tree.getNode("Location/2");

		assertEquals("Location/2",  childNode.getNodeId().getValue());
		assertEquals("test2", childNode.getLabel().getValue());
		assertEquals(location,  childNode.getNode());
		assertNotNull(childNode.getParent().getValue());

		String parentNodeId = childNode.getParent().getValue();

		assertEquals("Location/1", parentNodeId);
	}
}
