package ecs.component.componentstorage;

import ecs.component.componentset.AbstractComponentSet;
import ecs.component.AbstractComponentType;
import util.datastructure.lookuptable.AbstractIntLookupTable;
import util.datastructure.lookuptable.DynamicArrayIntLookupTable;
import util.datastructure.lookuptable.DynamicValuesArrayIntLookupTable;
import util.datastructure.lookuptable.LookupTableIterator;

@SuppressWarnings("unused")
class Archetype extends AbstractArchetype{
    private static final int NO_NON_MARKER_COMPONENT_TYPES = -1;
    private static final int LOOKUP_TABLE_INIT_VALUE_CAPACITY = 10;

    private final AbstractComponentSet componentKey;
    private final AbstractIntLookupTable<?>[] componentStorages;
    private int nonMarkerComponentTypeIndex = NO_NON_MARKER_COMPONENT_TYPES; //hacky way to iterate marker types

    public Archetype(AbstractComponentType<?>[] types, AbstractComponentSet componentKey, boolean isEntityCountFixed,
                     int entityCount) {

        this.componentKey = componentKey;
        componentStorages = new AbstractIntLookupTable[componentKey.getNumTotalComponentTypes()];
        for(int i : componentKey.getPresentIndices()) {
            AbstractComponentType<?> type = types[i];
            if(!type.isMarker()) {
                if(nonMarkerComponentTypeIndex == NO_NON_MARKER_COMPONENT_TYPES){
                    nonMarkerComponentTypeIndex = i;
                }
                if (isEntityCountFixed) {
                    componentStorages[i] = makeStaticLookupTable(type, entityCount);
                } else {
                    componentStorages[i] = makeDynamicLookupTable(type, entityCount);
                }
            }
        }
    }

    @SuppressWarnings("unused") //type provides generic
    private <T> AbstractIntLookupTable<T> makeDynamicLookupTable(AbstractComponentType<T> type, int initEntityCount){
        return new DynamicArrayIntLookupTable<>(initEntityCount, LOOKUP_TABLE_INIT_VALUE_CAPACITY);
    }

    @SuppressWarnings("unused")
    private <T> AbstractIntLookupTable<T> makeStaticLookupTable(AbstractComponentType<T> type, int maxEntityCount){
        return new DynamicValuesArrayIntLookupTable<>(maxEntityCount, LOOKUP_TABLE_INIT_VALUE_CAPACITY);
    }

    @Override
    @SuppressWarnings("unused")
    public <T> T getComponent(int entityID, AbstractComponentType<T> type) {
        return getComponentStorage(type).get(entityID);
    }

    @Override
    public <T> boolean setComponent(int entityID, AbstractComponentType<T> type, T component) {
        return getComponentStorage(type).set(entityID, component);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> boolean setComponent(int entityID, int typeIndex, T component) {
        return ((AbstractIntLookupTable<T>)componentStorages[typeIndex]).set(entityID, component);
    }

    @Override
    public void moveEntity(int entityID, AbstractArchetype newArchetype) {
        for(int i : newArchetype.getComponentKey().getPresentIndices()){
            AbstractIntLookupTable<?> storage = componentStorages[i];
            if(storage != null){
                newArchetype.setComponent(entityID, i, storage.get(entityID));
            }
        }
        removeEntity(entityID);
    }

    @Override
    public boolean removeEntity(int entityID) {
        boolean wasAnyComponentRemoved = false;
        for(int i : componentKey.getPresentIndices()){
            AbstractIntLookupTable<?> storage = componentStorages[i];
            if(storage != null && storage.remove(entityID)){
                wasAnyComponentRemoved = true;
            }
        }
        return wasAnyComponentRemoved;
    }

    @SuppressWarnings("unchecked")
    private <T> AbstractIntLookupTable<T> getComponentStorage(AbstractComponentType<T> type){
        return (AbstractIntLookupTable<T>) componentStorages[type.getIndex()];
    }

    @Override
    public AbstractComponentSet getComponentKey(){
        return componentKey;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ComponentIterator<T> getComponentIterator(AbstractComponentType<T> type) {
        if(!componentKey.containsComponent(type)){
            throw new RuntimeException("Cannot create iterator for type " + type + " in archetype " + this);
        }
        AbstractIntLookupTable<T> componentStorage = (AbstractIntLookupTable<T>) componentStorages[type.getIndex()];
        if(componentStorage != null) {
            return new ComponentItr<>(componentStorage.iterator());
        }
        else{
            if(nonMarkerComponentTypeIndex != NO_NON_MARKER_COMPONENT_TYPES){
                AbstractIntLookupTable<?> realComponentStorage = componentStorages[nonMarkerComponentTypeIndex];
                return new MarkerItr<>(realComponentStorage.iterator());
            }
            else{
                return new NullItr<>();
            }
        }
    }

    private static class ComponentItr<E> implements ComponentIterator<E>{
        private final LookupTableIterator<E> innerItr;
        private ComponentItr(LookupTableIterator<E> innerItr){
            this.innerItr = innerItr;
        }

        @Override
        public boolean hasNext() {
            return innerItr.hasNext();
        }
        @Override
        public E next() {
            return innerItr.next();
        }
        @Override
        public int entityIDOfPreviousComponent() {
            return innerItr.indexOfPreviousValue();
        }
    }

    private static class MarkerItr<E> implements ComponentIterator<E>{
        private final LookupTableIterator<?> innerItr;
        private MarkerItr(LookupTableIterator<?> innerItr){
            this.innerItr = innerItr;
        }

        @Override
        public boolean hasNext() {
            return innerItr.hasNext();
        }
        @Override
        public E next() {
            innerItr.next();
            return null;
        }
        @Override
        public int entityIDOfPreviousComponent() {
            return innerItr.indexOfPreviousValue();
        }
    }

    private static class NullItr<E> implements ComponentIterator<E>{
        @Override
        public int entityIDOfPreviousComponent() {
            throw new RuntimeException("Cannot operate on iterator for purely market archetype: " + this);
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public E next() {
            throw new RuntimeException("Cannot operate on iterator for purely market archetype: " + this);
        }
    }

    @Override
    public String toString() {
        return "Archetype: " + componentKey;
    }
}