package internalconfig.game.systems.graphicssystems;

import internalconfig.game.components.DrawOrder;
import internalconfig.game.components.VelocityComponent;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.TwoFramePosition;

import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class DrawCommand implements Comparable<DrawCommand>{
    private DrawOrder order;
    private BufferedImage image;
    private TwoFramePosition position;
    private AbstractVector offset;
    private VelocityComponent velocity;

    public DrawCommand(DrawOrder order, BufferedImage image, TwoFramePosition position) {
        this.order = order;
        this.image = image;
        this.position = position;
        offset = null;
        velocity = null;
    }

    public DrawCommand(DrawOrder order, BufferedImage image, TwoFramePosition position, VelocityComponent velocity) {
        this.order = order;
        this.image = image;
        this.position = position;
        offset = null;
        this.velocity = velocity;
    }

    public DrawCommand(DrawOrder order, BufferedImage image, TwoFramePosition position, AbstractVector offset) {
        this.order = order;
        this.image = image;
        this.position = position;
        this.offset = offset;
        velocity = null;
    }

    public DrawCommand(DrawOrder order, BufferedImage image, TwoFramePosition position, AbstractVector offset, VelocityComponent velocity) {
        this.order = order;
        this.image = image;
        this.position = position;
        this.offset = offset;
        this.velocity = velocity;
    }

    public DrawOrder getOrder() {
        return order;
    }

    public BufferedImage getImage() {
        return image;
    }

    public DoublePoint getPosition() {
        return position.getPos();
    }

    public boolean hasOffset(){
        return offset != null;
    }

    public AbstractVector getOffset() {
        return offset;
    }

    public boolean hasVelocity(){
        return velocity != null;
    }

    public VelocityComponent getVelocity() {
        return velocity;
    }

    public void setOrder(DrawOrder order) {
        this.order = order;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setPosition(TwoFramePosition pos) {
        this.position = pos;
    }

    public void setOffset(AbstractVector offset) {
        this.offset = offset;
    }

    public void setVelocity(VelocityComponent vel) {
        this.velocity = vel;
    }

    @Override
    public int compareTo(DrawCommand other) {
        return this.order.compareTo(other.order);
    }

}
