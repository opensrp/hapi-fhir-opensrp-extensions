package org.smartregister.extension.model;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.util.ElementUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.smartregister.extension.rest.LocationHierarchyResourceProvider;

import java.util.*;

@DatatypeDef(name = "Tree")
public class Tree extends Type implements ICompositeType {

	@Child(name = "listOfNodes", type = { SingleTreeNode.class })
	private SingleTreeNode listOfNodes;

	@Child(name = "parentChildren",
			type = { ParentChildrenMap.class },
			order = 1,
			min = 0,
			max = -1,
			modifier = false,
			summary = false)
	private List<ParentChildrenMap> parentChildren;

	private static Logger logger = LogManager.getLogger(Tree.class.toString());

	public SingleTreeNode getTree() {
		return listOfNodes;
	}

	public Tree() {
		listOfNodes = new SingleTreeNode();
		parentChildren = new ArrayList<>();
	}

	private void addToParentChildRelation(String parent, String id) {
		if (parentChildren == null) {
			parentChildren = new ArrayList<>();
		}
		List<StringType> kids = null;
		if (parentChildren != null) {
			for (int i = 0; i < parentChildren.size(); i++) {
				kids = parentChildren.get(i) != null && parentChildren.get(i).getIdentifier() != null &&
						parentChildren.get(i).getIdentifier().getValue() != null &&
						parentChildren.get(i).getIdentifier().getValue().equals(parent) ?
						parentChildren.get(i).getChildIdentifiers() :
						null;
				logger.info("Kids are : " + kids);
				if (kids != null) {
					break;
				}
			}
		}

		if (kids == null) {
			kids = new ArrayList<>();
		}
		StringType idStringType = new StringType();
		String idString = id;
		if (idString.contains("/_")) {
			idString = idString.substring(0, idString.indexOf("/_"));
		}
		idStringType.setValue(idString);

		StringType parentStringType = new StringType();
		parentStringType.setValue(parent);
		kids.add(idStringType);
		Boolean setParentChildMap = false;
		for (int i = 0; i < parentChildren.size(); i++) {
			if (parentChildren.get(i) != null && parentChildren.get(i).getIdentifier() != null
					&& parentChildren.get(i).getIdentifier().getValue() != null &&
					parentChildren.get(i).getIdentifier().getValue().equals(parent)) {
				parentChildren.get(i).setChildIdentifiers(kids);
				setParentChildMap = true;
			}
		}

		if (!setParentChildMap) {
			ParentChildrenMap parentChildrenMap = new ParentChildrenMap();
			parentChildrenMap.setIdentifier(parentStringType);
			parentChildrenMap.setChildIdentifiers(kids);
			parentChildren.add(parentChildrenMap);
		}

	}

	public void addNode(String id, String label, Location node, String parentId) {
		if (listOfNodes == null) {
			listOfNodes = new SingleTreeNode();
		}

		// if node exists we should break since user should write optimized code and also tree can not have duplicates
		if (hasNode(id)) {
			throw new IllegalArgumentException("Node with ID " + id + " already exists in tree");
		}

		TreeNode n = makeNode(id, label, node, parentId);

		if (parentId != null) {
			addToParentChildRelation(parentId, id);

			TreeNode p = getNode(parentId);

			//if parent exists add to it otherwise add as root for now
			if (p != null) {
				p.addChild(n);
			} else {
				// if no parent exists add it as root node
				String idString = (String) id;
				if (idString.contains("/_")) {
					idString = idString.substring(0, idString.indexOf("/_"));
				}
				SingleTreeNode singleTreeNode = new SingleTreeNode();
				StringType treeNodeId = new StringType();
				treeNodeId.setValue(idString);
				singleTreeNode.setTreeNodeId(treeNodeId);
				singleTreeNode.setTreeNode(n);
				listOfNodes = singleTreeNode;
			}
		} else {
			// if no parent add it as root node
			String idString = id;
			if (idString.contains("/_")) {
				idString = idString.substring(0, idString.indexOf("/_"));
			}

			SingleTreeNode singleTreeNode = new SingleTreeNode();
			StringType treeNodeId = new StringType();
			treeNodeId.setValue(idString);
			singleTreeNode.setTreeNodeId(treeNodeId);
			singleTreeNode.setTreeNode(n);
			listOfNodes = singleTreeNode;
		}
	}

	private TreeNode makeNode(String id, String label, Location node, String parentId) {
		TreeNode n = getNode(id);
		if (n == null) {
			n = new TreeNode();
			StringType nodeId = new StringType();
			String idString = (String) id;
			if (idString.contains("/_")) {
				idString = idString.substring(0, idString.indexOf("/_"));
			}
			nodeId.setValue((String) idString);
			n.setNodeId(nodeId);
			StringType labelString = new StringType();
			labelString.setValue(label);
			n.setLabel(labelString);
			n.setNode(node);
			StringType parentIdString = new StringType();
			String parentIdStringVar = parentId;

			if (parentIdStringVar != null && parentIdStringVar.contains("/_")) {
				parentIdStringVar = parentIdStringVar.substring(0, parentIdStringVar.indexOf("/_"));
			}
			parentIdString.setValue(parentIdStringVar);
			n.setParent(parentIdString);
		}
		return n;
	}

	public TreeNode getNode(String id) {
		// Check if id is any root node
		String idString = id;
		if (idString != null && idString.contains("/_")) {
			idString = idString.substring(0, idString.indexOf("/_"));
		}

		if (listOfNodes.getTreeNodeId() != null && listOfNodes.getTreeNodeId().getValue().equals(idString)) {
			return listOfNodes.getTreeNode();

		} else {
			if (listOfNodes != null && listOfNodes.getTreeNode() != null
					&& listOfNodes.getTreeNode().getChildren() != null) {
				return recursivelyFindNode(idString, listOfNodes.getTreeNode().getChildren());
			}
		}
		return null;
	}

	public boolean hasNode(String id) {
		return getNode(id) != null;
	}

	public SingleTreeNode getListOfNodes() {
		return listOfNodes;
	}

	public void setListOfNodes(SingleTreeNode listOfNodes) {
		this.listOfNodes = listOfNodes;
	}

	public List<ParentChildrenMap> getParentChildren() {
		return parentChildren;
	}

	public void setParentChildren(List<ParentChildrenMap> parentChildren) {
		this.parentChildren = parentChildren;
	}

	@Override
	public Type copy() {
		Tree tree = new Tree();
		copyValues(tree);
		return tree;
	}

	@Override
	public boolean isEmpty() {
		return ElementUtil.isEmpty(listOfNodes);
	}

	@Override
	protected Type typedCopy() {
		return copy();
	}

	private TreeNode recursivelyFindNode(String idString, List<ChildTreeNode> childTreeNodeList) {
		for (ChildTreeNode childTreeNode : childTreeNodeList) {
			TreeNode treeNode = childTreeNode.getChildren();
			if (treeNode != null && treeNode.getNodeId() != null && treeNode.getNodeId().getValue().equals(idString)) {
				return treeNode;
			} else {
				if (treeNode != null && treeNode.getChildren() != null && treeNode.getChildren().size() > 0) {
					return recursivelyFindNode(idString, treeNode.getChildren());
				}
			}
		}
		return null;
	}

}
