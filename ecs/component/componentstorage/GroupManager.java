package ecs.component.componentstorage;

import ecs.component.componentset.AbstractComponentSet;
import ecs.component.AbstractComponentType;
import ecs.component.componentset.AbstractFlyweightComponentSetFactory;
import util.observer.AbstractPushObserver;

import java.util.HashMap;
import java.util.Map;

class GroupManager implements AbstractGroupManager {
    private final GroupFactory groupFactory;
    private final AbstractPushObserver<AbstractArchetype> newArchetypeReceiver;

    private final Map<AbstractComponentSet, AbstractGroupTemplate> keyToGroupMap;
    private AbstractGroupTemplate zeroGroup;

    public GroupManager(){
        groupFactory = new GroupFactory();
        newArchetypeReceiver = makeNewArchetypeReceiver();
        keyToGroupMap = new HashMap<>();
    }

    public void init(AbstractComponentType<?>[] types, AbstractFlyweightComponentSetFactory componentSetFactory){
        AbstractComponentSet zeroSet = componentSetFactory.makeSet();
        keyToGroupMap.put(zeroSet, zeroGroup = groupFactory.makeGroup(componentSetFactory.makeSet()));
        initSingleComponentGroups(types, componentSetFactory);
    }

    private AbstractPushObserver<AbstractArchetype> makeNewArchetypeReceiver(){
        return data -> {
            if(zeroGroup != null) {
                zeroGroup.receiveNewArchetype(data);
            }
        };
    }

    private void initSingleComponentGroups(
            AbstractComponentType<?>[] types,
            AbstractFlyweightComponentSetFactory componentSetManager){

        for(AbstractComponentType<?> type : types){
            AbstractComponentSet set = componentSetManager.makeSet(type);
            AbstractGroupTemplate singleGroup = groupFactory.makeGroup(set);
            if(!zeroGroup.addNewGroup(singleGroup)){
                throw new RuntimeException();
            }
            keyToGroupMap.put(set, singleGroup);
        }
    }

    @Override
    public AbstractGroupTemplate createGroup(AbstractComponentSet key){
        AbstractGroupTemplate stored = keyToGroupMap.get(key);
        if(stored == null) {
            stored = groupFactory.makeGroup(key);
            keyToGroupMap.put(key, stored);
            if(!zeroGroup.addNewGroup(stored)){
                throw new RuntimeException();
            }
        }
        return stored;
    }

    @Override
    public AbstractPushObserver<AbstractArchetype> getNewArchetypeReceiver() {
        return newArchetypeReceiver;
    }
}