package ecs.component.componentstorage;

import ecs.component.componentset.AbstractComponentSet;
import ecs.component.AbstractComponentType;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

class Group extends AbstractGroupTemplate {

    Group(AbstractComponentSet componentKey) {
        super(componentKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ComponentIterator<T> getComponentIterator(AbstractComponentType<T> type) {
        assertValidType(type);

        ComponentIterator<T>[] innerIterators = (ComponentIterator<T>[])new ComponentIterator[archetypes.size()];
        int i = 0;
        for(AbstractArchetype archetype : archetypes){
            innerIterators[i++] = archetype.getComponentIterator(type);
        }
        return new Itr<>(innerIterators);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ComponentIterator<T> getComponentIteratorExcludingTypes(AbstractComponentType<T> type,
                                                                       AbstractComponentType<?>... excludedTypes) {
        assertValidType(type);

        List<ComponentIterator<T>> innerIteratorList = new ArrayList<>();
        for(AbstractArchetype archetype : archetypes){
            if(!archetype.getComponentKey().containsAnyComponent(excludedTypes)){
                innerIteratorList.add(archetype.getComponentIterator(type));
            }
        }
        return new Itr<>(innerIteratorList.toArray((ComponentIterator<T>[]) new ComponentIterator[0]));
    }

    private static class Itr<T> implements ComponentIterator<T>{
        private static final int INVALID_LAST_ID = -1;
        private final ComponentIterator<T>[] innerIterators;
        private int itrPos;
        private int lastEntityID;

        private Itr(ComponentIterator<T>[] innerIterators){
            this.innerIterators = innerIterators;
            itrPos = 0;
            lastEntityID = INVALID_LAST_ID;
        }

        @Override
        public int entityIDOfPreviousComponent() {
            return lastEntityID;
        }

        @Override
        public boolean hasNext() {
            if(itrPos >= innerIterators.length){
                return false;
            }
            if(innerIterators[itrPos].hasNext()){
                return true;
            }
            ++itrPos;
            return hasNext();
        }

        @Override
        public T next() {
            if(itrPos >= innerIterators.length){
                throw new NoSuchElementException();
            }
            ComponentIterator<T> inner = innerIterators[itrPos];
            if(!inner.hasNext()){
                ++itrPos;
                return next();
            }
            T toRet = inner.next();
            lastEntityID = inner.entityIDOfPreviousComponent();
            return toRet;
        }
    }
}