package util.spatialpartition;

import util.math.geometry.AABB;
import util.math.geometry.TwoFramePosition;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;

@SuppressWarnings("unused")
class CollisionElement<T> {

    private final T identifier;
    private final AABB hitbox;
    private final AABB twoFrameHitbox;
    private final AABB trueHitbox;
    private final TwoFramePosition twoFramePosition;

    public CollisionElement(T identifier,
                            AABB hitbox,
                            AABB twoFrameHitbox,
                            AABB trueHitbox,
                            TwoFramePosition twoFramePosition) {
        this.identifier = identifier;
        this.hitbox = hitbox;
        this.twoFrameHitbox = twoFrameHitbox;
        this.trueHitbox = trueHitbox;
        this.twoFramePosition = twoFramePosition;
    }

    public T getIdentifier() {
        return identifier;
    }

    public AABB getHitbox() {
        return hitbox;
    }

    public AABB getTwoFrameHitbox() {
        return twoFrameHitbox;
    }

    public AABB getTrueHitbox() {
        return trueHitbox;
    }

    public TwoFramePosition getTwoFramePosition() {
        return twoFramePosition;
    }

    public boolean collides(CollisionElement<T> other){
        return this.twoFrameHitbox.collides(other.twoFrameHitbox) &&
                (this.trueHitbox.collides(other.trueHitbox) || subFrameCollides(other));
    }

    private boolean subFrameCollides(CollisionElement<T> other){

        double largestSpeedRatio = Math.max(speedRatio(this.trueHitbox, this.twoFrameHitbox),
                speedRatio(other.trueHitbox, other.twoFrameHitbox));

        if(largestSpeedRatio > 2){

            DoublePoint pastPos = twoFramePosition.getPastPos();
            DoublePoint otherPastPos = other.twoFramePosition.getPastPos();

            DoublePoint pos = twoFramePosition.getPos();
            DoublePoint otherPos = other.twoFramePosition.getPos();

            AbstractVector velocity = GeometryUtil.vectorFromAToB(pastPos, pos);
            AbstractVector otherVelocity = GeometryUtil.vectorFromAToB(otherPastPos, otherPos);

            int numChecks = (int)(largestSpeedRatio - 1);

            for(int i = 1; i <= numChecks; i++){
                //1 check = split 2, 2 check = split 3, etc
                double baseFraction = 1d/(numChecks + 1);
                double currentFraction = baseFraction * i;

                DoublePoint interpolatedCenter = velocity.lerp(pastPos, currentFraction);
                DoublePoint otherInterpolatedCenter = otherVelocity.lerp(otherPastPos, currentFraction);

                AABB interpolatedHitbox = hitbox.makeTrueHitbox(interpolatedCenter);
                AABB otherInterpolatedHitbox = other.hitbox.makeTrueHitbox(otherInterpolatedCenter);

                if(interpolatedHitbox.collides(otherInterpolatedHitbox)){
                    return true;
                }
            }
        }
        return false;
    }

    //larger = two frame larger than real
    //1 = no movement
    private static double speedRatio(AABB real, AABB two){
        //move to simple area ratio; square root is accurate for diagonal but not vertical
        return two.getArea()/real.getArea();
    }
}
