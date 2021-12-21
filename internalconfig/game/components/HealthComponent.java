package internalconfig.game.components;

public class HealthComponent {
    private int health;

    public HealthComponent(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
