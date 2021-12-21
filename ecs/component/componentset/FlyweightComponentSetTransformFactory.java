package ecs.component.componentset;

import ecs.component.AbstractComponentType;
import util.ConstTransform;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
class FlyweightComponentSetTransformFactory implements AbstractComponentSetTransformFactory {

    private final Map<AbstractComponentType<?>, ConstTransform<AbstractComponentSet_PP>> addSingleComponentMap;
    private final Map<AbstractComponentType<?>, ConstTransform<AbstractComponentSet_PP>> removeSingleComponentMap;
    private final Map<List<AbstractComponentType<?>>, ConstTransform<AbstractComponentSet_PP>> addMultipleComponentsMap;
    private final Map<List<AbstractComponentType<?>>, ConstTransform<AbstractComponentSet_PP>> removeMultipleComponentsMap;
    //we use List here for value equality - as opposed to arrays

    public FlyweightComponentSetTransformFactory(){
        addSingleComponentMap = new HashMap<>();
        removeSingleComponentMap = new HashMap<>();
        addMultipleComponentsMap = new HashMap<>();
        removeMultipleComponentsMap = new HashMap<>();
    }

    @Override
    public ConstTransform<AbstractComponentSet_PP> addComponent(AbstractComponentType<?> type) {
        ConstTransform<AbstractComponentSet_PP> storedTransform = addSingleComponentMap.get(type);
        if(storedTransform == null){
            storedTransform = new SingleComponentSetTransform(true, type);
            addSingleComponentMap.put(type, storedTransform);
        }
        return storedTransform;
    }

    @Override
    public ConstTransform<AbstractComponentSet_PP> removeComponent(AbstractComponentType<?> type) {
        ConstTransform<AbstractComponentSet_PP> storedTransform = removeSingleComponentMap.get(type);
        if(storedTransform == null){
            storedTransform = new SingleComponentSetTransform(false, type);
            removeSingleComponentMap.put(type, storedTransform);
        }
        return storedTransform;
    }

    @Override
    public ConstTransform<AbstractComponentSet_PP> addComponents(AbstractComponentType<?>... types) {
        if(types.length == 0){
            throw new IllegalArgumentException("passing less than one componentType");
        }
        if(types.length == 1){
            return addComponent(types[0]);
        }

        List<AbstractComponentType<?>> typeList = Arrays.asList(types);
        ConstTransform<AbstractComponentSet_PP> storedTransform = addMultipleComponentsMap.get(typeList);
        if(storedTransform == null){
            storedTransform = new MultiComponentSetTransform(true, types);
            addMultipleComponentsMap.put(typeList, storedTransform);
        }
        return storedTransform;
    }

    @Override
    public ConstTransform<AbstractComponentSet_PP> removeComponents(AbstractComponentType<?>... types) {
        if(types.length == 0){
            throw new IllegalArgumentException("passing less than one componentType");
        }
        if(types.length == 1){
            return removeComponent(types[0]);
        }
        List<AbstractComponentType<?>> typeList = Arrays.asList(types);
        ConstTransform<AbstractComponentSet_PP> storedTransform = removeMultipleComponentsMap.get(typeList);
        if(storedTransform == null){
            storedTransform = new MultiComponentSetTransform(false, types);
            removeMultipleComponentsMap.put(typeList, storedTransform);
        }
        return storedTransform;
    }
}