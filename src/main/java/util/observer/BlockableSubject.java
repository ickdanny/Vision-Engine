package util.observer;

public class BlockableSubject extends Subject implements AbstractBlockableSubject {
    private boolean blocked;

    public BlockableSubject(){
        super();
        blocked = false;
    }

    @Override
    public void broadcast() {
        if(!blocked) {
            super.broadcast();
        }
    }

    @Override
    public void block() {
        blocked = true;
    }

    @Override
    public void unblock() {
        blocked = false;
    }
}