package ecs.component.componentset;

import ecs.component.AbstractComponentType;
import util.ConstTransform;

interface AbstractComponentSetTransformFactory {
    ConstTransform<AbstractComponentSet_PP> addComponent(AbstractComponentType<?> type);
    ConstTransform<AbstractComponentSet_PP> addComponents(AbstractComponentType<?>... types);
    ConstTransform<AbstractComponentSet_PP> removeComponent(AbstractComponentType<?> type);
    ConstTransform<AbstractComponentSet_PP> removeComponents(AbstractComponentType<?>... types);
}
