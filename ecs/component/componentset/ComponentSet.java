package ecs.component.componentset;

import ecs.component.AbstractComponentType;
import ecs.component.componentstorage.AbstractArchetype;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
class ComponentSet implements AbstractComponentSet_PP {

    private static final Map<AbstractComponentType<?>[], ComponentSetKey> setKeys = new HashMap<>();

    private AbstractArchetype archetype;

    private final BitSet bitSet;
    private final ComponentSetKey key;
    private final int size;
    private int[] presentIndices; //this is lazy initialized

    ComponentSet(AbstractComponentType<?>[] componentTypes){
        key = getKey(componentTypes);
        bitSet = new BitSet(getNumTotalComponentTypes());
        size = 0;
    }

    ComponentSet(AbstractComponentType<?>[] componentTypes, AbstractComponentType<?>[] presentTypes){
        key = getKey(componentTypes);
        bitSet = new BitSet(getNumTotalComponentTypes());
        for (AbstractComponentType<?> presentType : presentTypes) {
            bitSet.set(presentType.getIndex());
        }
        size = presentTypes.length;
    }

    private ComponentSet(ComponentSetKey key, int size){
        this.key = key;
        bitSet = new BitSet(getNumTotalComponentTypes());
        this.size = size;
    }

    static ComponentSetKey getKey(AbstractComponentType<?>[] componentTypes){
        ComponentSetKey key = setKeys.get(componentTypes);
        if(key != null){
            return key;
        }
        key = new ComponentSetKey(componentTypes);
        setKeys.put(componentTypes, key);
        return key;
    }

    @Override
    public boolean containsComponent(AbstractComponentType<?> type) {
        return bitSet.get(type.getIndex());
    }

    @Override
    public boolean containsComponent(int typeIndex) {
        return bitSet.get(typeIndex);
    }

    @Override
    public boolean containsAllComponents(AbstractComponentType<?>... types) {
        if(types == null || types.length == 0){
            throw new IllegalArgumentException("ComponentSet.containsComponents() bad varargs input");
        }
        for(AbstractComponentType<?> type : types){
            if(!containsComponent(type)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAllComponents(int... typeIndices) {
        if(typeIndices == null || typeIndices.length == 0){
            throw new IllegalArgumentException("ComponentSet.containsComponents() (int ver) bad varargs input");
        }
        for(int index : typeIndices){
            if(!containsComponent(index)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isContainedIn(AbstractComponentSet other) {
        if(other instanceof ComponentSet) {
            if (key != ((ComponentSet)other).key) {
                throw new RuntimeException("Mismatching ComponentSetKeys.");
            }
            BitSet temp = new BitSet(bitSet.length());
            temp.or(bitSet);//copy our bitSet into our temp
            temp.and(((ComponentSet)other).bitSet);//our temp is now A&B
            return bitSet.equals(temp);
        }
        throw new RuntimeException("Mismatching ComponentSet classes");
    }

    @Override
    public int getNumComponentsPresent() {
        if(size != bitSet.cardinality()){
            throw new RuntimeException("size != bitSet.cardinality(), " +
                    "size: " + size +
                    ", cardinality: " + bitSet.cardinality());
        }
        return size;
    }

    @Override
    public int getNumTotalComponentTypes(){
        return key.getNumComponentTypes();
    }

    @Override
    public ComponentSet addComponent(AbstractComponentType<?> type) {
        int index = type.getIndex();
        if (!bitSet.get(index)) { //if it is off in this one
            ComponentSet toRet = new ComponentSet(key, getNumComponentsPresent() + 1);
            copyBitSet(this, toRet);

            toRet.bitSet.set(index);

            return toRet;
        }
        return this;
    }

    @Override
    public ComponentSet removeComponent(AbstractComponentType<?> type) {
        int index = type.getIndex();
        if (bitSet.get(index)) { //if it is on in this one
            ComponentSet toRet = new ComponentSet(key, getNumComponentsPresent() - 1);
            copyBitSet(this, toRet);

            toRet.bitSet.clear(index);

            return toRet;
        }
        return this;
    }

    @Override
    public AbstractComponentSet_PP addComponents(AbstractComponentType<?>... types) {
        if(types == null || types.length == 0){
            throw new IllegalArgumentException("ComponentSet.addComponents() bad varargs input");
        }
        if(!containsAllComponents(types)){ //if this does already not contain every component listed
            ComponentSet toRet = new ComponentSet(key, getNumComponentsPresent() + types.length);
            copyBitSet(this, toRet);

            for(AbstractComponentType<?> type : types){
                toRet.bitSet.set(type.getIndex());
            }

            return toRet;
        }
        return this;
    }

    @Override
    public AbstractComponentSet_PP removeComponents(AbstractComponentType<?>... types) {
        if(types == null || types.length == 0){
            throw new IllegalArgumentException("ComponentSet.removeComponents() bad varargs input");
        }
        int componentsToRemove;
        if((componentsToRemove = componentsToRemove(types)) > 0){
            ComponentSet toRet = new ComponentSet(key, getNumComponentsPresent() - componentsToRemove);
            copyBitSet(this, toRet);

            for(AbstractComponentType<?> type : types){
                toRet.bitSet.clear(type.getIndex());
            }

            return toRet;
        }
        return this;
    }

    private int componentsToRemove(AbstractComponentType<?>... types){
        int componentsToRemove = 0;
        for(AbstractComponentType<?> type : types){
            if(containsComponent(type)){
                ++componentsToRemove;
            }
        }
        return componentsToRemove;
    }

    @Override
    public int[] getPresentIndices() {
        if(presentIndices == null){
            presentIndices = makePresentIndices();
        }
        return presentIndices;
    }

    private int[] makePresentIndices(){
        if(size == 0){
            return new int[0];
        }

        int[] toRet = new int[size];
        int index = 0;
        for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i+1)) { //taken from docs
            toRet[index++] = i;
        }
        return toRet;
    }

    private static void copyBitSet(ComponentSet from, ComponentSet to){
        to.bitSet.or(from.bitSet);
    }

    public void associateArchetype(AbstractArchetype archetype){
        this.archetype = archetype;
    }

    public AbstractArchetype getAssociatedArchetype(){
        return archetype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentSet that = (ComponentSet) o;
        return Objects.equals(bitSet, that.bitSet) &&
                Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitSet, key);
    }

    private static class ComponentSetKey{
        private final int numComponentTypes;
        private final int typeHash;

        ComponentSetKey(AbstractComponentType<?>[] componentTypes){
            numComponentTypes = componentTypes.length;
            typeHash = Arrays.hashCode(componentTypes);
        }

        int getNumComponentTypes() {
            return numComponentTypes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ComponentSetKey that = (ComponentSetKey) o;
            return numComponentTypes == that.numComponentTypes &&
                    typeHash == that.typeHash;
        }

        @Override
        public int hashCode() {
            return Objects.hash(numComponentTypes, typeHash);
        }
    }

    @Override
    public String toString() {
        return "ComponentSet: " + bitSet;
    }
}