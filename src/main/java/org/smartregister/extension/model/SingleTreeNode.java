package org.smartregister.extension.model;

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.util.ElementUtil;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;

@DatatypeDef(name = "SingleTreeNode")
public class SingleTreeNode extends Type implements ICompositeType {

    @Child(name = "treeNodeId", type = {StringType.class}, order = 0)
    private StringType treeNodeId;

    @Child(name = "treeNode", type = {TreeNode.class},
            order = 1,
            min = 0,
            max = -1,
            modifier = false,
            summary = false)
    private TreeNode treeNode;

    @Override
    public Type copy() {
        SingleTreeNode singleTreeNode = new SingleTreeNode();
        copyValues(singleTreeNode);
        return singleTreeNode;
    }

    @Override
    public boolean isEmpty() {
        return ElementUtil.isEmpty(treeNodeId, treeNode);
    }

    @Override
    protected Type typedCopy() {
        return copy();
    }

    public StringType getTreeNodeId() {
        return treeNodeId;
    }

    public SingleTreeNode setTreeNodeId(StringType treeNodeId) {
        this.treeNodeId = treeNodeId;
        return this;
    }

    public TreeNode getTreeNode() {
        return treeNode;
    }

    public SingleTreeNode setTreeNode(TreeNode treeNode) {
        this.treeNode = treeNode;
        return this;
    }
}
