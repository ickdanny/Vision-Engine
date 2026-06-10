package util.math.geometry;

public final class GeometryUtil {
    private GeometryUtil() {
    }

    public static AbstractVector vectorAdd(AbstractVector a, AbstractVector b){
        return new PolarVector(new CartesianVector(a.getX() + b.getX(), a.getY() + b.getY()));
    }

    public static AbstractVector vectorFromAToB(DoublePoint a, DoublePoint b) {
        return new CartesianVector(b.getX() - a.getX(), b.getY() - a.getY());
    }

    public static double distanceFromAToB(DoublePoint a, DoublePoint b) {
        return vectorFromAToB(a, b).getMagnitude();
    }

    public static Angle angleFromAToB(DoublePoint a, DoublePoint b) {
        return vectorFromAToB(a, b).getAngle();
    }

    public static double fullAngleDivide(int n) {
        return 360d / n;
    }

    public static boolean AABBCircleCollision(AABB aabb, DoublePoint circleCenter, double circleRadius) {
        DoublePoint aabbCenter = aabb.getCenter();
        double xDist = Math.abs(circleCenter.getX() - aabbCenter.getX());
        double yDist = Math.abs(circleCenter.getY() - aabbCenter.getY());

        double aabbWidth = aabb.getWidth();
        double aabbHeight = aabb.getHeight();

        double halfAABBWidth = aabbWidth/2;
        double halfAABBHeight = aabbHeight/2;

        if (xDist > (halfAABBWidth + circleRadius)) {
            return false;
        }
        if (yDist > (halfAABBHeight + circleRadius)) {
            return false;
        }
        if (xDist <= halfAABBWidth) {
            return true;
        }
        if (yDist <= halfAABBHeight) {
            return true;
        }

        double centerDistanceSquared = Math.pow(xDist - halfAABBWidth, 2) + Math.pow(yDist - halfAABBHeight, 2);

        return (centerDistanceSquared <= Math.pow(circleRadius, 2));
    }

    public static boolean pointAABBCollision(DoublePoint point, AABB bounds){
        double x = point.getX();
        double y = point.getY();
        return !(x < bounds.getXLow() || x > bounds.getXHigh() || y < bounds.getYLow() || y > bounds.getYHigh());
    }
}
