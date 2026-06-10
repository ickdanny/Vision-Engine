package util.observer;

public interface AbstractSubject {
    void attach(AbstractObserver observer);
    void detach(AbstractObserver observer);
    void broadcast();
}