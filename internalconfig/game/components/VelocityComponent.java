package internalconfig.game.components;

import util.math.geometry.AbstractVector;
import util.math.geometry.ConstPolarVector;
import util.math.geometry.DoublePoint;

public class VelocityComponent {
    private AbstractVector velocity;
    private AbstractVector publicVelocity; //speed multi is not shown in the public velocity
    private double speedMulti;

    private AbstractVector trueVelocity;

    public VelocityComponent(AbstractVector velocity) {
        this.velocity = velocity;
        makePublicVelocity();
        speedMulti = 1;

        makeTrueVelocity();
    }

    public VelocityComponent(AbstractVector velocity, double speedMulti) {
        this.velocity = velocity;
        makePublicVelocity();
        this.speedMulti = speedMulti;

        makeTrueVelocity();
    }

    private void makePublicVelocity(){
        publicVelocity = new ConstPolarVector(velocity);
    }
    private void makeTrueVelocity(){
        if(speedMulti == 1){
            trueVelocity = new ConstPolarVector(velocity);
        }
        else{
            trueVelocity = new ConstPolarVector(velocity.getMagnitude() * speedMulti, velocity.getAngle());
        }
    }

    public AbstractVector getVelocity() {
        return publicVelocity;
    }
    public double getSpeedMulti() {
        return speedMulti;
    }
    public AbstractVector getTrueVelocity() {
        return trueVelocity;
    }

    public void setVelocity(AbstractVector velocity) {
        this.velocity = velocity;
        makePublicVelocity();

        makeTrueVelocity();
    }

    public void setSpeedMulti(double speedMulti) {
        this.speedMulti = speedMulti;

        makeTrueVelocity();
    }

    public void setTrueVelocity(AbstractVector velocity){
        setVelocity(new ConstPolarVector(velocity.getMagnitude() / speedMulti, velocity.getAngle()));
    }

    public DoublePoint lerp(DoublePoint pos, double deltaTime) {
        return trueVelocity.lerp(pos, deltaTime);
    }

    public DoublePoint add(DoublePoint pos) {
        return trueVelocity.add(pos);
    }
}
