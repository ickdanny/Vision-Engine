package util.observer;

import java.util.ArrayList;
import java.util.List;

public class Subject implements AbstractSubject {
    private final List<AbstractObserver> observers;
    public Subject(){
        observers = new ArrayList<>();
    }
    @Override
    public void attach(AbstractObserver observer){
        observers.add(observer);
    }
    @Override
    public void detach(AbstractObserver observer){
        observers.remove(observer);
    }
    @Override
    public void broadcast(){
        for(AbstractObserver observer : observers){
            observer.update();
        }
    }
}