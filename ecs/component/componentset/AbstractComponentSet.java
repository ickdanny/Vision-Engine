package ecs.component.componentset;

import ecs.component.AbstractComponentType;
import ecs.component.componentstorage.AbstractArchetype;

@SuppressWarnings("unused")
public interface AbstractComponentSet{

    boolean containsComponent(AbstractComponentType<?> type);
    boolean containsComponent(int typeIndex);
    default boolean doesNotContainComponent(AbstractComponentType<?> type){
        return !containsComponent(type);
    }
    default boolean doesNotContainComponent(int typeIndex){
        return !containsComponent(typeIndex);
    }
    boolean containsAllComponents(AbstractComponentType<?>... types);
    boolean containsAllComponents(int... typeIndices);
    default boolean containsAnyComponent(AbstractComponentType<?>... types){
        for(AbstractComponentType<?> type : types){
            if(containsComponent(type)){
                return true;
            }
        }
        return false;
    }
    default boolean containsAnyComponent(int... typeIndices){
        for(int typeIndex : typeIndices){
            if(containsComponent(typeIndex)){
                return true;
            }
        }
        return false;
    }
    boolean isContainedIn(AbstractComponentSet other);
    int getNumComponentsPresent();
    int getNumTotalComponentTypes();
    int[] getPresentIndices();

    void associateArchetype(AbstractArchetype archetype);
    AbstractArchetype getAssociatedArchetype();
}