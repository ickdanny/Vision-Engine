package internalconfig.game.systems;

public class PlayerData {

    public static final int UNSET_FIELD = -1;

    private int lives;
    private int bombs;
    private int continues;
    private int power;

    private final ShotType shotType;

    public PlayerData(ShotType shotType){
        this.shotType = shotType;
        lives = UNSET_FIELD;
        bombs = UNSET_FIELD;
        continues = UNSET_FIELD;
        power = UNSET_FIELD;
    }

    public PlayerData(ShotType shotType, int lives, int continues, int bombs) {
        this.shotType = shotType;
        this.lives = lives;
        this.bombs = bombs;
        this.continues = continues;
        power = 0;
    }

    public PlayerData(ShotType shotType, int lives, int bombs, int continues, int power){
        this.shotType = shotType;
        this.lives = lives;
        this.bombs = bombs;
        this.continues = continues;
        this.power = power;
    }

    public ShotType getShotType() {
        return shotType;
    }
    public int getLives() {
        return lives;
    }
    public int getBombs() {
        return bombs;
    }
    public int getContinues() {
        return continues;
    }
    public int getPower() {
        return power;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }
    public void setBombs(int bombs) {
        this.bombs = bombs;
    }
    public void setContinues(int continues) {
        this.continues = continues;
    }
    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "lives=" + lives +
                ", bombs=" + bombs +
                ", continues=" + continues +
                ", power=" + power +
                ", shotType=" + shotType +
                '}';
    }
}