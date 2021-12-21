package gameloop;

import util.observer.ConfigurablePushSubject;

public class FixedAndInterpTimeLoop extends AbstractThreadedGameLoop {

    private boolean running = true;
    private final int updatesPerSecond;
    private final int maxUpdatesWithoutFrame;
    private int millisBetweenUpdates;

    private final ConfigurablePushSubject<Double> fixedTimeBroadcaster;
    private final ConfigurablePushSubject<Double> interpTimeBroadcaster;

    public FixedAndInterpTimeLoop(int updatesPerSecond, int maxUpdatesWithoutFrame) {
        fixedTimeBroadcaster = new ConfigurablePushSubject<>(1d);// default dt = 1x
        interpTimeBroadcaster = new ConfigurablePushSubject<>();
        this.updatesPerSecond = updatesPerSecond;
        this.maxUpdatesWithoutFrame = maxUpdatesWithoutFrame;
        calcMillisBetweenUpdates();
    }

    private void calcMillisBetweenUpdates(){
        double millisPerSecond = 1000d;
        millisBetweenUpdates = (int)(millisPerSecond/updatesPerSecond);
    }

    @Override
    protected void threadAction() {
        long nextUpdateMillis = getCurrentTimeMillis();
        long timeOfLastUpdate = getCurrentTimeMillis();
        int updatesWithoutFrame = 0;

        while(running) {
            if (updatesWithoutFrame >= maxUpdatesWithoutFrame) {
                interpTimeUpdate(calcDeltaTime(timeOfLastUpdate));
                updatesWithoutFrame = 0;
            }
            if (getCurrentTimeMillis() >= nextUpdateMillis) {
                fixedTimeUpdate();
                nextUpdateMillis += millisBetweenUpdates;
                timeOfLastUpdate = getCurrentTimeMillis();
            }
            if (getCurrentTimeMillis() < nextUpdateMillis) {
                while (getCurrentTimeMillis() < nextUpdateMillis && running) {
                    interpTimeUpdate(calcDeltaTime(timeOfLastUpdate));
                }
            } else {
                ++updatesWithoutFrame;
            }
        }
    }

    private long getCurrentTimeMillis(){
        return System.currentTimeMillis();
    }
    private double calcDeltaTime(long timeOfLastUpdate){
        double  millisSinceLastUpdate = calcMillisSinceLastUpdate(timeOfLastUpdate);
        double deltaTime = millisSinceLastUpdate/millisBetweenUpdates;
        if(deltaTime < 0){
            throw new RuntimeException("invalid deltaTime: " + deltaTime);
        }
        if(deltaTime > 1){
            return 1;
        }
        return deltaTime;
    }
    private long calcMillisSinceLastUpdate(long timeOfLastUpdate){
        return getCurrentTimeMillis() - timeOfLastUpdate;
    }

    @Override
    public void end(){
        running = false;
    }

    private void fixedTimeUpdate(){
        fixedTimeBroadcaster.broadcast();
    }
    private void interpTimeUpdate(double deltaTime){
        interpTimeBroadcaster.setPushData(deltaTime);
        interpTimeBroadcaster.broadcast();
    }

    public ConfigurablePushSubject<Double> getFixedTimeBroadcaster() {
        return fixedTimeBroadcaster;
    }
    public ConfigurablePushSubject<Double> getInterpTimeBroadcaster() {
        return interpTimeBroadcaster;
    }
}