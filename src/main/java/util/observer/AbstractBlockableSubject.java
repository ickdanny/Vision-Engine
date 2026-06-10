package util.observer;

public interface AbstractBlockableSubject extends AbstractSubject{
    void block();
    void unblock();
}
