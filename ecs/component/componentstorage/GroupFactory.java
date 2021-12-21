package ecs.component.componentstorage;

import ecs.component.componentset.AbstractComponentSet;
import ecs.component.AbstractComponentType;

class GroupFactory {
    public AbstractGroupTemplate makeGroup(AbstractComponentSet componentKey) {
        if(componentKey.getNumComponentsPresent() == 0){
            return new AbstractGroupTemplate(componentKey) {
                @Override
                public <T> ComponentIterator<T> getComponentIterator(AbstractComponentType<T> type) {
                    throw new RuntimeException("cannot give iterator for no components");
                }

                @Override
                public <T> ComponentIterator<T> getComponentIteratorExcludingTypes(AbstractComponentType<T> type,
                                                                        AbstractComponentType<?>... excludedTypes) {
                    throw new RuntimeException("cannot give iterator for no components");
                }

            };
        }

        return new Group(componentKey);
    }
}
