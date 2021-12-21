package internalconfig.game.components;

import util.Ticker;

public class TrailComponent extends Ticker {

    private final String image;

    public TrailComponent(int tick, String image) {
        super(tick, true);
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}
