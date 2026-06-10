package util.observer;

public interface AbstractPushSubject<T> {
    void attach(AbstractPushObserver<T> observer);
    void detach(AbstractPushObserver<T> observer);
    void broadcast();
}