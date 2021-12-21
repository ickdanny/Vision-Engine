package gameloop;

public abstract class AbstractThreadedGameLoop implements AbstractGameLoop{
    @Override
    public final void begin() {
        Thread thread = new Thread(threadName()){
            @Override
            public void run() {
                threadAction();
            }
        };
        thread.start();
    }

    protected abstract void threadAction();

    @SuppressWarnings("SameReturnValue")
    protected String threadName(){
        return "gameloop";
    }
}