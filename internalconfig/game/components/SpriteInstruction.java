package internalconfig.game.components;

import util.image.ImageUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.CartesianVector;

public class SpriteInstruction {

    public static final double DEFAULT_SCALE = 1.0;
    public static final double DEFAULT_ROTATION = 0.0;
    public static final double DEFAULT_TRANSPARENCY = 1.0;

    protected String image;
    protected AbstractVector offset;
    protected double scale;
    protected Angle rotation;
    //1 = fully opaque, 0 = fully transparent
    protected double transparency;

    protected boolean updated = false;

    public SpriteInstruction(String image) {
        this.image = image;
        offset = new CartesianVector();
        scale = DEFAULT_SCALE;
        rotation = new Angle();
        transparency = DEFAULT_TRANSPARENCY;
    }

    public SpriteInstruction(String image, AbstractVector offset){
        this.image = image;
        this.offset = offset;
        scale = DEFAULT_SCALE;
        rotation = new Angle();
        transparency = DEFAULT_TRANSPARENCY;
    }

    public SpriteInstruction(String image, AbstractVector offset, double scale, double rotation, double transparency) {
        ImageUtil.throwIfInvalidTransparency(transparency);
        this.image = image;
        this.offset = offset;
        this.scale = scale;
        this.rotation = new Angle(rotation);
        this.transparency = transparency;
    }

    public static SpriteInstruction makeScaled(String image, AbstractVector offset, double scale){
        return new SpriteInstruction(image, offset, scale, DEFAULT_ROTATION, DEFAULT_TRANSPARENCY);
    }
    public static SpriteInstruction makeRotated(String image, AbstractVector offset, double rotation){
        return new SpriteInstruction(image, offset, DEFAULT_SCALE, rotation, DEFAULT_TRANSPARENCY);
    }
    public static SpriteInstruction makeTransparent(String image, AbstractVector offset, double transparency){
        return new SpriteInstruction(image, offset, DEFAULT_SCALE, DEFAULT_ROTATION, transparency);
    }
    public static SpriteInstruction makeScaledAndRotated(String image, AbstractVector offset,
                                                         double scale, double rotation){
        return new SpriteInstruction(image, offset, scale, rotation, DEFAULT_TRANSPARENCY);
    }
    public static SpriteInstruction makeScaledAndTransparent(String image, AbstractVector offset,
                                                             double scale, double transparency){
        return new SpriteInstruction(image, offset, scale, DEFAULT_ROTATION, transparency);
    }
    public static SpriteInstruction makeRotatedAndTransparent(String image, AbstractVector offset,
                                                              double rotation, double transparency){
        return new SpriteInstruction(image, offset, DEFAULT_SCALE, rotation, transparency);
    }

    public static SpriteInstruction makeScaled(String image , double scale){
        return new SpriteInstruction(image, new CartesianVector(), scale, DEFAULT_ROTATION, DEFAULT_TRANSPARENCY);
    }
    public static SpriteInstruction makeRotated(String image , double rotation){
        return new SpriteInstruction(image, new CartesianVector(), DEFAULT_SCALE, rotation, DEFAULT_TRANSPARENCY);
    }
    public static SpriteInstruction makeTransparent(String image , double transparency){
        return new SpriteInstruction(image, new CartesianVector(), DEFAULT_SCALE, DEFAULT_ROTATION, transparency);
    }
    public static SpriteInstruction makeScaledAndRotated(String image , double scale, double rotation){
        return new SpriteInstruction(image, new CartesianVector(), scale, rotation, DEFAULT_TRANSPARENCY);
    }
    public static SpriteInstruction makeScaledAndTransparent(String image, double scale, double transparency){
        return new SpriteInstruction(image, new CartesianVector(), scale, DEFAULT_ROTATION, transparency);
    }
    public static SpriteInstruction makeRotatedAndTransparent(String image, double rotation, double transparency){
        return new SpriteInstruction(image, new CartesianVector(), DEFAULT_SCALE, rotation, transparency);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        updated = false;
    }

    public AbstractVector getOffset() {
        return offset;
    }

    public void setOffset(AbstractVector offset) {
        this.offset = offset;
        updated = false;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        updated = false;
    }

    public Angle getRotation() {
        return rotation;
    }

    public void setRotation(Angle rotation) {
        this.rotation = rotation;
        updated = false;
    }

    public double getTransparency() {
        return transparency;
    }

    public void setTransparency(double transparency) {
        ImageUtil.throwIfInvalidTransparency(transparency);
        this.transparency = transparency;
        updated = false;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void flagForUpdate(){
        updated = false;
    }

    public void update(){
        updated = true;
    }
}
