package ecs.component;

public class TypeComponentTuple<T> {

    private final AbstractComponentType<T> type;
    private final T component;

    public TypeComponentTuple(AbstractComponentType<T> type){
        if(type.isMarker()){
            this.type = type;
            component = null;
        }
        else{
            throw new RuntimeException("Tried to use 1 arg constructor of TypeComponentTuple for non-marker type!");
        }
    }

    public TypeComponentTuple(AbstractComponentType<T> type, T component) {
        this.type = type;
        this.component = component;
    }

    public AbstractComponentType<T> getType() {
        return type;
    }

    public T getComponent() {
        return component;
    }
}