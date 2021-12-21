package internalconfig;

import window.input.AbstractInputValue;
import window.input.KeyValues;

public enum InputValues implements AbstractInputValue {
    LEFT(KeyValues.K_LEFT),
    RIGHT(KeyValues.K_RIGHT),
    UP(KeyValues.K_UP),
    DOWN(KeyValues.K_DOWN),
    ESC(KeyValues.K_ESCAPE),
    SHIFT(KeyValues.K_SHIFT),
    CTRL(KeyValues.K_CONTROL),
    Z(KeyValues.K_Z),
    X(KeyValues.K_X)
    ;
    private KeyValues key;
    InputValues(KeyValues key){
        this.key = key;
    }
    @Override
    public KeyValues getKey() {
        return key;
    }
    @Override
    public void setKey(KeyValues key) {
        this.key = key;
    }
    @Override
    public int getIndex() {
        return ordinal();
    }
}
