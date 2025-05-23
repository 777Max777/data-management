package com.lma.core.task.api;

public class TaskOutput {
    private Consumers neighbors;
    private Consumers children;
    private ExecutingType type = ExecutingType.ALL;

    public enum ExecutingType {
        ALL, CHILDREN, NEIGHBORS
    }

    public TaskOutput(Consumers neighbors, Consumers children) {
        this.neighbors = neighbors;
        this.children = children;
    }

    public void invoke(Object masterData, Object attributes, Object newAttributes) {
        if (type == ExecutingType.ALL) {
            children.accept(masterData, newAttributes);
            neighbors.accept(masterData, attributes);
        } else if (type == ExecutingType.CHILDREN) {
            children.accept(masterData, newAttributes);
        } else if (type == ExecutingType.NEIGHBORS) {
            neighbors.accept(masterData, attributes);
        }
    }

    public Consumers getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(Consumers neighbors) {
        this.neighbors = neighbors;
    }

    public Consumers getChildren() {
        return children;
    }

    public void setChildren(Consumers children) {
        this.children = children;
    }

    public ExecutingType getType() {
        return type;
    }

    public void setType(ExecutingType type) {
        this.type = type;
    }
}
