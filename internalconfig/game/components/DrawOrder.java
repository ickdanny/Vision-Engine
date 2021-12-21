package internalconfig.game.components;

@SuppressWarnings("unused")
public class DrawOrder implements Comparable<DrawOrder>{
    private DrawPlane plane;
    private int order;

    public DrawOrder(DrawPlane plane, int order) {
        this.plane = plane;
        this.order = order;
    }

    public DrawPlane getPlane() {
        return plane;
    }

    public void setPlane(DrawPlane plane) {
        this.plane = plane;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int compareTo(DrawOrder o) {
        if(this.plane != o.plane){
            return this.plane.compareTo(o.plane);
        }
        return Integer.compare(this.order, o.order);
    }
}