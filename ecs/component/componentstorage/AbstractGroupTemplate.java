package ecs.component.componentstorage;

import ecs.component.componentset.AbstractComponentSet;
import ecs.component.AbstractComponentType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract class AbstractGroupTemplate implements AbstractGroup{
    protected final AbstractComponentSet componentKey;
    protected final Set<AbstractArchetype> archetypes;

    private final List<AbstractGroupTemplate> childGroups;

    public AbstractGroupTemplate(AbstractComponentSet componentKey) {
        this.componentKey = componentKey;
        archetypes = new HashSet<>();
        childGroups = new ArrayList<>();
        receiveNewArchetype(componentKey.getAssociatedArchetype());
    }

    public void receiveNewArchetype(AbstractArchetype archetype){
        if(doesComponentSetFitIntoGroup(archetype.getComponentKey())){
            archetypes.add(archetype);
            for(AbstractGroupTemplate child : childGroups){
                child.receiveNewArchetype(archetype);
            }
        }
    }

    public boolean addNewGroup(AbstractGroupTemplate group){
        if(group == this){
            throw new RuntimeException("Tried to add group " + group + " to itself!");
        }
        if(group.getComponentKey() == componentKey){
            throw new RuntimeException("Tried to add a group with the same key!");
        }

        if(doesComponentSetFitIntoGroup(group.getComponentKey())){
            for(AbstractGroupTemplate child : childGroups){
                if(child.addNewGroup(group)){
                    return true;
                }
            }
            //if fits this group but no children, add as a direct child
            addChildGroup(group);
            return true;
        }
        return false;
    }

    public boolean doesComponentSetFitIntoGroup(AbstractComponentSet componentSet){
        return componentKey.isContainedIn(componentSet);
    }

    private void addChildGroup(AbstractGroupTemplate group){
        childGroups.add(group);
        for(AbstractArchetype archetype : archetypes){
            group.receiveNewArchetype(archetype);
        }
    }

    protected void assertValidType(AbstractComponentType<?> type){
        if(!componentKey.containsComponent(type)){
            throw new RuntimeException("Component key " + componentKey + " does not contain type " + type);
        }
    }

    AbstractComponentSet getComponentKey() {
        return componentKey;
    }
}
