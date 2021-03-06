package com.prokopchuk.lab_2.ui.visitors;

import com.prokopchuk.lab_2.data_structures.nodes.*;
import com.prokopchuk.lab_2.ui.memento.Snapshot;
import javafx.geometry.Dimension2D;
import javafx.scene.paint.Color;

import java.util.LinkedList;

public class DrawVisitor<T> implements IVisitor<T>{
    private LinkedList<DrawData<T>> nodesData = new LinkedList<>();
    private LinkedList<Dimension2D[]> edgesPoints = new LinkedList<>();
    private double width;
    private double height;
    private final double MARGIN_TOP = 10;
    private double nodeSize = MIN_NODE_SIZE;
    public static double MIN_NODE_SIZE = 30;
    private int length;

    public DrawVisitor(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void setLength(int length) {
        this.length = length;
    }

    public double getNodeSize() {
        return nodeSize;
    }

    public LinkedList<DrawData<T>> getNodesData() {
        return nodesData;
    }

    public LinkedList<Dimension2D[]> getEdgesPoints() {
        return edgesPoints;
    }

    public Snapshot<T> getSnapshot() {
        return new Snapshot<>(nodesData, edgesPoints);
    }

    public void restoreBySnapshot(Snapshot<T> snapshot) {
        this.nodesData = (LinkedList<DrawData<T>>) snapshot.getNodesData().clone();
        this.edgesPoints = (LinkedList<Dimension2D[]>) snapshot.getEdgesPoints().clone();
    }

    @Override
    public void visitTree(AbstractBinaryTreeNode startNode) {
        drawTreeNodes(startNode, 0, width, null, 0);
    }

    @Override
    public void visitList(ListNode<T> startNode) {
        drawListNodes(startNode);
    }

    public void clear() {
        nodesData.clear();
        edgesPoints.clear();
    }

    private void drawTreeNodes(AbstractBinaryTreeNode node, double xStart, double xEnd, Dimension2D parentPoint, int curLevel) {
        if(node.isLeaf()) {
            return;
        }

        double x = (xEnd - xStart) / 2 + xStart;
        double y = curLevel * nodeSize + nodeSize / 2 + MARGIN_TOP;

        Color color;
        if(node instanceof BSTreeNode<?>) {
            color = Color.BLUE;
        } else {
            color = ((RBTreeNode<?>) node).getColor() == RBTreeNodeColor.BLACK ? Color.BLACK : Color.RED;
        }

        Dimension2D point = new Dimension2D(x, y);

        if(parentPoint != null) {
            edgesPoints.add(new Dimension2D[]{parentPoint, point});
        }

        DrawData<T> toPush = new DrawData<T>(point, (T) node.getValue(), color);
        nodesData.add(toPush);


        drawTreeNodes(node.getLeft(), xStart, x, point, curLevel + 1);
        drawTreeNodes(node.getRight(), x, xEnd, point, curLevel + 1);
    }

    private void drawListNodes(ListNode<T> node) {
        if(node == null) {
            return;
        }

        double x = nodeSize / 2;
        double y = height / 2;
        double step = width / length;
        Color color = Color.rgb(0, 204, 102);

        Dimension2D parentPoint = new Dimension2D(x, y);
        DrawData<T> toPush = new DrawData<>(parentPoint, node.getValue(), color);
        nodesData.add(toPush);

        node = node.getNext();

        while(node != null) {
            x += step;
            Dimension2D point = new Dimension2D(x, y);
            edgesPoints.push(new Dimension2D[] {parentPoint, point});
            toPush = new DrawData<>(point, node.getValue(), color);
            nodesData.add(toPush);
            parentPoint = point;
            node = node.getNext();
        }
    }
}
