package ecs.component.componentset;

import ecs.component.AbstractComponentType;
import util.ConstTransform;

import java.util.Arrays;
import java.util.Objects;

class MultiComponentSetTransform implements ConstTransform<AbstractComponentSet_PP> {
    private final AbstractComponentType<?>[] types;
    private final boolean transformState;

    public MultiComponentSetTransform(boolean transformState, AbstractComponentType<?>... types) {
        this.types = types;
        this.transformState = transformState;
    }

    @Override
    public AbstractComponentSet_PP transform(AbstractComponentSet_PP set) {
        return transformState ? set.addComponents(types) : set.removeComponents(types);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiComponentSetTransform that = (MultiComponentSetTransform) o;
        return transformState == that.transformState &&
                Arrays.equals(types, that.types);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(transformState);
        result = 31 * result + Arrays.hashCode(types);
        return result;
    }
}