package internalconfig.game.systems;

import static internalconfig.game.GameConfig.*;

@SuppressWarnings("SameParameterValue")
public enum PlayerInitValues {
    STORY(INIT_LIVES, INIT_BOMBS, INIT_CONTINUES, 0),

    PRACTICE_1(INIT_LIVES, INIT_BOMBS, 0, 128),
    PRACTICE_2(INIT_LIVES, INIT_BOMBS, 0, MAX_POWER/2),
    PRACTICE_3(INIT_LIVES, INIT_BOMBS, 0, MAX_POWER),
    PRACTICE_4(PRACTICE_3),
    PRACTICE_5(PRACTICE_4),
    PRACTICE_6(PRACTICE_5),

    EXTRA(PRACTICE_1)
    ;
    private final int lives;
    private final int bombs;
    private final int continues;
    private final int power;

    PlayerInitValues(int lives, int bombs, int continues, int power){
        this.lives = lives;
        this.bombs = bombs;
        this.continues = continues;
        this.power = power;
    }

    PlayerInitValues(PlayerInitValues other){
        this.lives = other.lives;
        this.bombs = other.bombs;
        this.continues = other.continues;
        this.power = other.power;
    }

    public void setPlayerData(PlayerData playerData){
        playerData.setLives(lives);
        playerData.setBombs(bombs);
        playerData.setContinues(continues);
        playerData.setPower(power);
    }
}
