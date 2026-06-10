package util.observer;

import java.util.ArrayList;
import java.util.List;

public abstract class PushSubjectTemplate<T> implements AbstractPushSubject<T> {
    private final List<AbstractPushObserver<T>> observers;
    public PushSubjectTemplate(){
        observers = new ArrayList<>();
    }
    @Override
    public void attach(AbstractPushObserver<T> observer){
        observers.add(observer);
    }
    @Override
    public void detach(AbstractPushObserver<T> observer){
        observers.remove(observer);
    }
    @Override
    public final void broadcast(){
        for(AbstractPushObserver<T> observer : observers){
            observer.update(getPushData());
        }
    }
    protected abstract T getPushData();
}