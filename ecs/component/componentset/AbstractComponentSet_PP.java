package ecs.component.componentset;

import ecs.component.AbstractComponentType;

interface AbstractComponentSet_PP extends AbstractComponentSet{
    AbstractComponentSet_PP addComponent(AbstractComponentType<?> type);
    AbstractComponentSet_PP removeComponent(AbstractComponentType<?> type);
    AbstractComponentSet_PP addComponents(AbstractComponentType<?>... types);
    AbstractComponentSet_PP removeComponents(AbstractComponentType<?>... types);
}
