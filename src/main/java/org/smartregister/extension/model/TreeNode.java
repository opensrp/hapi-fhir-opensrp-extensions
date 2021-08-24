package org.smartregister.extension.model;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.util.ElementUtil;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


@DatatypeDef(name = "TreeNode")
public class TreeNode extends Type implements ICompositeType {

    @Child(name = "name", type = {StringType.class}, order = 0, min = 1, max = 1, modifier = false, summary = false)
    protected StringType name;

    @Child(name = "nodeId", type = {StringType.class}, order = 2)
    private StringType nodeId;

    @Child(name = "label", type = {StringType.class}, order = 3)
    private StringType label;

    @Child(name = "node", type = {Location.class}, order = 4)
    private Location node;

    @Child(name = "parent", type = {StringType.class}, order = 5)
    private StringType parent;

    @Child(name = "children", type = {ChildTreeNode.class},
            order = 6,
            min = 0,
            max = -1,
            modifier = false,
            summary = false)
    private List<ChildTreeNode> children;

    public TreeNode() {
        children = new ArrayList<>();
    }

    public StringType getName() {
        if (name == null) {
            name = new StringType();
        }
        return name;
    }

    public TreeNode setName(StringType name) {
        this.name = name;
        return this;
    }


    public StringType getLabel() {
        return label;
    }

    public TreeNode setLabel(StringType label) {
        this.label = label;
        return this;
    }

    @Override
    public Type copy() {
        TreeNode treeNode = new TreeNode();
        copyValues(treeNode);
        return treeNode;
    }

    @Override
    public boolean isEmpty() {
        return ElementUtil.isEmpty(node);
    }

    @Override
    protected Type typedCopy() {
        return copy();
    }

    public StringType getNodeId() {
        return nodeId;
    }

    public TreeNode setNodeId(StringType nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public Location getNode() {
        return node;
    }

    public TreeNode setNode(Location node) {
        this.node = node;
        return this;
    }

    public StringType getParent() {
        return parent;
    }

    public TreeNode setParent(StringType parent) {
        this.parent = parent;
        return this;
    }

    public List<ChildTreeNode> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public TreeNode setChildren(List<ChildTreeNode> children) {
        this.children = children;
        return this;
    }

    public void addChild(TreeNode node) {
        if (children == null) {
            children = new ArrayList<>();
        }
        ChildTreeNode childTreeNode = new ChildTreeNode();
        childTreeNode.setChildId(node.getNodeId());
        List<TreeNode> treeNodeList = new ArrayList<>();
        TreeNode treeNode = new TreeNode();
        treeNode.setNode(node.getNode());
        treeNode.setNodeId(node.getNodeId());
        treeNodeList.add(treeNode);
        childTreeNode.setChildren(treeNode);
//        childTreeNode.setChildNodes(treeNodeV2List);
        children.add(childTreeNode);
//        childTreeNodes.add(treeNodeV2);
//        children.put(node.getNodeId(), node);
    }



    public TreeNode findChild(String id) {
        String idString = (String) id;
        if (idString.contains("/_")) {
            idString = idString.substring(0, idString.indexOf("/_"));
        }
        if (children != null && children.size() > 0) {
            for (int i = 0; i < children.size(); i++) {
                    if (children.get(i) != null) {
                        for (ChildTreeNode child : children) {
                            if (child != null && child.getChildren() != null
                                    && child.getChildren().getNodeId() != null && child.getChildren().getNodeId().getValue().equals(idString)) {
                                return child.getChildren();
                            } else if (child != null && child != null) {
                                TreeNode node = child.getChildren().findChild(idString);
                                if (node != null)
                                    return node;
                            }
                        }
                    }
            }
        }
        return null;
    }


//    public TreeNode<K, T> findChild(K id) {
//        if (children != null) {
//            for (TreeNode<K, T> child : children.values()) {
//                if (child.getId().equals(id)) {
//                    return child;
//                } else if (child.getChildren() != null) {
//                    TreeNode<K, T> node = child.findChild(id);
//                    if (node != null)
//                        return node;
//                }
//            }
//        }
//        return null;
//    }
}
