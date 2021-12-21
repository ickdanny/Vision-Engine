package ecs.datastorage;

import ecs.component.AbstractComponentType;
import ecs.component.TypeComponentTuple;
import ecs.component.componentset.AbstractComponentSet;
import ecs.component.componentset.AbstractFlyweightComponentSetFactory;
import ecs.component.componentset.HashFlyweightComponentSetFactory;
import ecs.component.componentstorage.AbstractComponentStorage;
import ecs.component.componentstorage.AbstractGroup;
import ecs.component.componentstorage.ComponentStorage;
import ecs.entity.EntityHandle;
import ecs.entity.NamedEntityHandle;
import ecs.entity.entitymetadatastorage.AbstractEntityMetadataStorage;
import ecs.entity.entitymetadatastorage.DynamicEntityMetadataStorage;
import ecs.entity.EntityMetadata;
import ecs.entity.entitymetadatastorage.StaticEntityMetadataStorage;
import ecs.system.criticalorders.AddComponentOrder;
import ecs.system.criticalorders.AddEntityOrder;
import ecs.system.criticalorders.RemoveComponentOrder;
import ecs.system.criticalorders.RemoveEntityOrder;
import ecs.system.criticalorders.SetComponentOrder;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.Iterator;
import java.util.List;

import static ecs.ECSTopics.ADD_COMPONENT_ORDERS;
import static ecs.ECSTopics.ADD_ENTITY_ORDERS;
import static ecs.ECSTopics.NEW_ANONYMOUS_ENTITIES;
import static ecs.ECSTopics.NEW_NAMED_ENTITIES;
import static ecs.ECSTopics.REMOVED_ENTITIES;
import static ecs.ECSTopics.REMOVE_COMPONENT_ORDERS;
import static ecs.ECSTopics.REMOVE_ENTITY_ORDERS;
import static ecs.ECSTopics.SET_COMPONENT_ORDERS;

@SuppressWarnings("unused")
public class DataStorage implements AbstractDataStorage_PP {

    private final AbstractPublishSubscribeBoard pubSubBoard;
    private final int messageLifetime;
    private final AbstractEntityMetadataStorage entityMetadataStorage;
    private final AbstractFlyweightComponentSetFactory componentSetFactory;
    private final AbstractComponentStorage componentStorage;

    public DataStorage(AbstractDataStorageConfig config, AbstractPublishSubscribeBoard pubSubBoard){
        this.pubSubBoard = pubSubBoard;
        messageLifetime = config.baseMessageLifetime();

        boolean isEntityCountStatic = config.isEntityCountStatic();
        int entityCount = config.entityCount();
        if(isEntityCountStatic){
            entityMetadataStorage = new StaticEntityMetadataStorage(entityCount);
        }
        else{
            entityMetadataStorage = new DynamicEntityMetadataStorage(entityCount);
        }
        AbstractComponentType<?>[] types = config.getTypes();
        componentSetFactory = new HashFlyweightComponentSetFactory(types);
        componentStorage = new ComponentStorage(componentSetFactory, types, isEntityCountStatic, entityCount);
    }

    @Override
    public AbstractGroup createGroup(AbstractComponentType<?>... types){
        return componentStorage.createGroup(componentSetFactory.makeSet(types));
    }

    @Override
    public boolean isAlive(EntityHandle entityHandle) {
        return entityMetadataStorage.isAlive(entityHandle);
    }

    @Override
    public boolean isDead(EntityHandle entityHandle) {
        return entityMetadataStorage.isDead(entityHandle);
    }

    @Override
    public boolean containsComponent(EntityHandle entityHandle, AbstractComponentType<?> type) {
        if(isAlive(entityHandle)){
            int entityID = entityHandle.getEntityID();
            return entityMetadataStorage.getMetadata(entityID).getComponentSet().containsComponent(type);
        }
        return false;
    }

    @Override
    public boolean containsAllComponents(EntityHandle entityHandle, AbstractComponentType<?>... types) {
        if(isAlive(entityHandle)){
            int entityID = entityHandle.getEntityID();
            return entityMetadataStorage.getMetadata(entityID).getComponentSet().containsAllComponents(types);
        }
        return false;
    }

    @Override
    public boolean containsAnyComponent(EntityHandle entityHandle, AbstractComponentType<?>... types) {
        if(isAlive(entityHandle)){
            int entityID = entityHandle.getEntityID();
            return entityMetadataStorage.getMetadata(entityID).getComponentSet().containsAnyComponent(types);
        }
        return false;
    }

    @Override
    public <T> T getComponent(EntityHandle entityHandle, AbstractComponentType<T> type) {
        if(isAlive(entityHandle)) {
            if (containsComponent(entityHandle, type)) {
                int entityID = entityHandle.getEntityID();
                return componentStorage.getComponent(entityID, type, getComponentSet(entityID));
            }
        }
        return null;
    }

    @Override
    public EntityHandle makeHandle(int entityID) {
        if(entityMetadataStorage.isAlive(entityID)){
            return new EntityHandle(entityID, getMetadata(entityID).getGeneration());
        }
        return null;
    }

    private NamedEntityHandle makeNamedEntityHandle(int entityID, String name){
        if(entityMetadataStorage.isAlive(entityID)){
            return new NamedEntityHandle(entityID, getMetadata(entityID).getGeneration(), name);
        }
        return null;
    }

    private AbstractComponentSet getComponentSet(int entityID){
        return getMetadata(entityID).getComponentSet();
    }

    private EntityMetadata getMetadata(int entityID){
        return entityMetadataStorage.getMetadata(entityID);
    }

    public int getMessageLifetime() {
        return messageLifetime;
    }

    @Override
    public void carryOutCriticalOrders() {
        removeEntities();
        removeComponents();
        addComponents();
        setComponents();
        addEntities();
    }

    private void removeEntities(){
        List<Message<RemoveEntityOrder>> orders = pubSubBoard.getMessageList(REMOVE_ENTITY_ORDERS);
        Iterator<Message<RemoveEntityOrder>> itr = orders.iterator();
        while(itr.hasNext()) {
            RemoveEntityOrder order = itr.next().getMessage();
            itr.remove();
            if (removeEntity(order)) {
                pubSubBoard.publishMessage(new Message<>(REMOVED_ENTITIES, order.getEntityHandle(), messageLifetime));
            }
        }
    }
    private void removeComponents(){
        List<Message<RemoveComponentOrder>> orders = pubSubBoard.getMessageList(REMOVE_COMPONENT_ORDERS);
        Iterator<Message<RemoveComponentOrder>> itr = orders.iterator();
        while(itr.hasNext()){
            RemoveComponentOrder order = itr.next().getMessage();
            itr.remove();
            if(removeComponent(order)){
                pubSubBoard.publishMessage(new Message<>(
                        order.getType().getRemoveComponentTopic(), order.getEntityHandle(), messageLifetime));
            }
        }
    }
    private void addComponents(){
        List<Message<AddComponentOrder<?>>> orders = pubSubBoard.getMessageList(ADD_COMPONENT_ORDERS);
        Iterator<Message<AddComponentOrder<?>>> itr = orders.iterator();
        while(itr.hasNext()){
            AddComponentOrder<?> order = itr.next().getMessage();
            itr.remove();
            if(addComponent(order)){
                pubSubBoard.publishMessage(new Message<>(
                        order.getType().getSetComponentTopic(), order.getEntityHandle(), messageLifetime));
            }
        }
    }
    private void setComponents(){
        List<Message<SetComponentOrder<?>>> orders = pubSubBoard.getMessageList(SET_COMPONENT_ORDERS);
        Iterator<Message<SetComponentOrder<?>>> itr = orders.iterator();
        while(itr.hasNext()){
            SetComponentOrder<?> order = itr.next().getMessage();
            itr.remove();
            if(setComponent(order)){
                pubSubBoard.publishMessage(new Message<>(
                        order.getType().getSetComponentTopic(), order.getEntityHandle(), messageLifetime));
            }
        }
    }
    
    private void addEntities(){
        List<Message<AddEntityOrder>> orders = pubSubBoard.getMessageList(ADD_ENTITY_ORDERS);
        Iterator<Message<AddEntityOrder>> itr = orders.iterator();
        while(itr.hasNext()){
            AddEntityOrder order = itr.next().getMessage();
            itr.remove();

            int newEntityID = addEntity(order);
            if(order.getName() != null){
                pubSubBoard.publishMessage(new Message<>(
                        NEW_NAMED_ENTITIES, makeNamedEntityHandle(newEntityID, order.getName()), messageLifetime));
            }
            else{
                pubSubBoard.publishMessage(new Message<>(
                        NEW_ANONYMOUS_ENTITIES, makeHandle(newEntityID), messageLifetime));
            }
        }
    }

    private boolean removeEntity(RemoveEntityOrder removeEntityOrder){
        EntityHandle entityHandle = removeEntityOrder.getEntityHandle();
        if(isAlive(entityHandle)) {
            int entityID = entityHandle.getEntityID();
            componentStorage.removeEntity(removeEntityOrder, getComponentSet(entityID));
            entityMetadataStorage.reclaimEntity(entityID);
            return true;
        }
        return false;
    }
    private boolean removeComponent(RemoveComponentOrder removeComponentOrder){
        EntityHandle entityHandle = removeComponentOrder.getEntityHandle();
        if(isAlive(entityHandle)) {
            int entityID = entityHandle.getEntityID();
            AbstractComponentType<?> type = removeComponentOrder.getType();
            AbstractComponentSet oldComponentSet = getComponentSet(entityID);
            AbstractComponentSet newComponentSet = componentSetFactory.removeComponent(oldComponentSet, type);
            if(!(oldComponentSet == newComponentSet)) {
                componentStorage.removeComponent(removeComponentOrder, oldComponentSet, newComponentSet);
                setComponentSet(entityID, newComponentSet);
            }
            //we allow entities to live with zero components
            return true;
        }
        return false;
    }
    private boolean addComponent(AddComponentOrder<?> addComponentOrder){
        EntityHandle entityHandle = addComponentOrder.getEntityHandle();
        if(isAlive(entityHandle)) {
            int entityID = entityHandle.getEntityID();
            AbstractComponentType<?> type = addComponentOrder.getType();
            AbstractComponentSet oldComponentSet = getComponentSet(entityID);
            AbstractComponentSet newComponentSet = componentSetFactory.addComponent(oldComponentSet, type);
            if(oldComponentSet == newComponentSet){
                throw new RuntimeException("Trying to add an existing component to entity : " + entityHandle +
                        ", of type " + type);
            }
            componentStorage.addComponent(addComponentOrder, oldComponentSet, newComponentSet);
            setComponentSet(entityID, newComponentSet);
            return true;
        }
        return false;
    }
    private boolean setComponent(SetComponentOrder<?> setComponentOrder){
        EntityHandle entityHandle = setComponentOrder.getEntityHandle();
        if(isAlive(entityHandle)) {
            int entityID = entityHandle.getEntityID();
            AbstractComponentType<?> type = setComponentOrder.getType();
            AbstractComponentSet oldComponentSet = getComponentSet(entityID);
            AbstractComponentSet newComponentSet = componentSetFactory.addComponent(oldComponentSet, type);
            componentStorage.setComponent(setComponentOrder, oldComponentSet, newComponentSet);
            if(!(oldComponentSet == newComponentSet)) {
                setComponentSet(entityID, newComponentSet);
            }
            return true;
        }
        return false;
    }
    private int addEntity(AddEntityOrder addEntityOrder){
        int newEntityID = entityMetadataStorage.createEntity();
        AbstractComponentSet componentSet = makeComponentSetForNewEntity(addEntityOrder);
        componentStorage.addEntity(addEntityOrder, newEntityID, componentSet);
        setComponentSet(newEntityID, componentSet);
        return newEntityID;
    }

    private AbstractComponentSet makeComponentSetForNewEntity(AddEntityOrder addEntityOrder){
        TypeComponentTuple<?>[] typeComponentTuples = addEntityOrder.getComponents();
        AbstractComponentType<?>[] types = new AbstractComponentType[typeComponentTuples.length];
        for(int i = 0; i < typeComponentTuples.length; ++i){
            types[i] = typeComponentTuples[i].getType();
        }
        return componentSetFactory.makeSet(types);
    }

    private void setComponentSet(int entityID, AbstractComponentSet componentSet){
        if(entityMetadataStorage.isDead(entityID)){
            throw new RuntimeException("setComponentSet should not be called on a dead entity : " + entityID);
        }
        entityMetadataStorage.getMetadata(entityID).setComponentSet(componentSet);
    }
}