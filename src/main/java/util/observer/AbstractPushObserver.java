package util.observer;

public interface AbstractPushObserver<T> {
    void update(T data);
}
