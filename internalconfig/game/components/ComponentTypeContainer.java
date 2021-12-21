package internalconfig.game.components;

import ecs.component.AbstractComponentType;

import java.util.HashMap;
import java.util.Map;

import static internalconfig.game.components.ComponentTypes.AbstractComponentTypeTemplate;
public class ComponentTypeContainer implements AbstractComponentTypeContainer {

    private final AbstractComponentType<?>[] array;
    private final Map<Class<? extends AbstractComponentType<?>>, AbstractComponentType<?>> map;

    @SuppressWarnings("unchecked")
    public ComponentTypeContainer(AbstractComponentTypeTemplate<?>... types){
        map = new HashMap<>();
        for(int i = 0; i < types.length; ++i){
            types[i].setIndex(i);
            map.put((Class<? extends AbstractComponentType<?>>) types[i].getClass(), types[i]);
        }
        array = types;
    }

    @Override
    public AbstractComponentType<?>[] getArray() {
        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U, T extends AbstractComponentType<U>> AbstractComponentType<U> getTypeInstance(Class<T> typeClass) {
        return (AbstractComponentType<U>) map.get(typeClass);
    }
}
