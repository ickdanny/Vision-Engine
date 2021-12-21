package ecs.component.componentset;

import ecs.component.AbstractComponentType;
import util.ConstTransform;

import java.util.Objects;

class SingleComponentSetTransform implements ConstTransform<AbstractComponentSet_PP> {
    private final AbstractComponentType<?> type;
    private final boolean transformState;

    public SingleComponentSetTransform(boolean transformState, AbstractComponentType<?> type) {
        this.type = type;
        this.transformState = transformState;
    }

    @Override
    public AbstractComponentSet_PP transform(AbstractComponentSet_PP set) {
        return transformState ? set.addComponent(type) : set.removeComponent(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleComponentSetTransform that = (SingleComponentSetTransform) o;
        return transformState == that.transformState &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, transformState);
    }
}
