package ecs.system.criticalorders;

import ecs.component.TypeComponentTuple;

public class AddEntityOrder  {
    private final TypeComponentTuple<?>[] components;
    private final String name;

    public AddEntityOrder(TypeComponentTuple<?>[] components) {
        this.components = components;
        name = null;
    }

    public AddEntityOrder(TypeComponentTuple<?>[] components, String name) {
        this.components = components;
        this.name = name;
    }

    public TypeComponentTuple<?>[] getComponents() {
        return components;
    }

    public String getName() {
        return name;
    }
}
