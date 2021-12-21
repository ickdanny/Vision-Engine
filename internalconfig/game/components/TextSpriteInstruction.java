package internalconfig.game.components;

import util.math.geometry.AbstractVector;
import util.math.geometry.CartesianVector;

public class TextSpriteInstruction extends SpriteInstruction {

    protected int charsPerLine;

    public TextSpriteInstruction(String text, int charsPerLine) {
        super(text);
        this.charsPerLine = charsPerLine;
    }

    public TextSpriteInstruction(String text, int charsPerLine, AbstractVector offset) {
        super(text, offset);
        this.charsPerLine = charsPerLine;
    }

    public TextSpriteInstruction(String text, int charsPerLine, AbstractVector offset, double scale, double rotation, double transparency) {
        super(text, offset, scale, rotation, transparency);
        this.charsPerLine = charsPerLine;
    }

    public static TextSpriteInstruction makeScaled(String text, int charsPerLine, AbstractVector offset, double scale){
        return new TextSpriteInstruction(text, charsPerLine, offset, scale, DEFAULT_ROTATION, DEFAULT_TRANSPARENCY);
    }
    public static TextSpriteInstruction makeRotated(String text, int charsPerLine, AbstractVector offset, double rotation){
        return new TextSpriteInstruction(text, charsPerLine, offset, DEFAULT_SCALE, rotation, DEFAULT_TRANSPARENCY);
    }
    public static TextSpriteInstruction makeTransparent(String text, int charsPerLine, AbstractVector offset, double transparency){
        return new TextSpriteInstruction(text, charsPerLine, offset, DEFAULT_SCALE, DEFAULT_ROTATION, transparency);
    }
    public static TextSpriteInstruction makeScaledAndRotated(String text, int charsPerLine, AbstractVector offset,
                                                         double scale, double rotation){
        return new TextSpriteInstruction(text, charsPerLine, offset, scale, rotation, DEFAULT_TRANSPARENCY);
    }
    public static TextSpriteInstruction makeScaledAndTransparent(String text, int charsPerLine, AbstractVector offset,
                                                             double scale, double transparency){
        return new TextSpriteInstruction(text, charsPerLine, offset, scale, DEFAULT_ROTATION, transparency);
    }
    public static TextSpriteInstruction makeRotatedAndTransparent(String text, int charsPerLine, AbstractVector offset,
                                                              double rotation, double transparency){
        return new TextSpriteInstruction(text, charsPerLine, offset, DEFAULT_SCALE, rotation, transparency);
    }

    public static TextSpriteInstruction makeScaled(String text , int charsPerLine, double scale){
        return new TextSpriteInstruction(text, charsPerLine, new CartesianVector(), scale, DEFAULT_ROTATION, DEFAULT_TRANSPARENCY);
    }
    public static TextSpriteInstruction makeRotated(String text , int charsPerLine, double rotation){
        return new TextSpriteInstruction(text, charsPerLine, new CartesianVector(), DEFAULT_SCALE, rotation, DEFAULT_TRANSPARENCY);
    }
    public static TextSpriteInstruction makeTransparent(String text , int charsPerLine, double transparency){
        return new TextSpriteInstruction(text, charsPerLine, new CartesianVector(), DEFAULT_SCALE, DEFAULT_ROTATION, transparency);
    }
    public static TextSpriteInstruction makeScaledAndRotated(String text , int charsPerLine, double scale, double rotation){
        return new TextSpriteInstruction(text, charsPerLine, new CartesianVector(), scale, rotation, DEFAULT_TRANSPARENCY);
    }
    public static TextSpriteInstruction makeScaledAndTransparent(String text, int charsPerLine, double scale, double transparency){
        return new TextSpriteInstruction(text, charsPerLine, new CartesianVector(), scale, DEFAULT_ROTATION, transparency);
    }
    public static TextSpriteInstruction makeRotatedAndTransparent(String text, int charsPerLine, double rotation, double transparency){
        return new TextSpriteInstruction(text, charsPerLine, new CartesianVector(), DEFAULT_SCALE, rotation, transparency);
    }

    public int getCharsPerLine() {
        return charsPerLine;
    }

    public void setCharsPerLine(int charsPerLine) {
        this.charsPerLine = charsPerLine;
    }
}
