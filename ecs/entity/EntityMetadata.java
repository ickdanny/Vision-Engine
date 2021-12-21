package ecs.entity;

import ecs.component.componentset.AbstractComponentSet;

public class EntityMetadata {
    private AbstractComponentSet componentSet;
    private int generation;

    public EntityMetadata() {
        this.componentSet = null;
        this.generation = 0;
    }

    public AbstractComponentSet getComponentSet() {
        return componentSet;
    }
    public void setComponentSet(AbstractComponentSet componentSet) {
        this.componentSet = componentSet;
    }

    public int getGeneration() {
        return generation;
    }

    public void newGeneration(){
        componentSet = null;
        ++generation;
    }
}