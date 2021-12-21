package internalconfig.game.systems;

import java.util.Random;

public class GameConfigObject {
    public static final int INVALID_STAGE = -2;

    private final GameMode gameMode;
    private final Difficulty difficulty;
    private final int stage;
    private final PlayerData playerData;
    private final Random random;
    private final long randomSeed;

    public GameConfigObject() {
        gameMode = null;
        difficulty = null;
        stage = INVALID_STAGE;
        playerData = null;
        random = null;
        randomSeed = 0;
    }

    private GameConfigObject(GameMode gameMode,
                             Difficulty difficulty,
                             int stage,
                             PlayerData playerData,
                             Random random,
                             long randomSeed) {
        this.gameMode = gameMode;
        this.difficulty = difficulty;
        this.stage = stage;
        this.playerData = playerData;
        this.random = random;
        this.randomSeed = randomSeed;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getStage() {
        return stage;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public Random getRandom() {
        return random;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    public boolean isValid(){
        return gameMode != null && difficulty != null && stage != INVALID_STAGE && playerData != null && random != null;
    }

    public GameConfigObject setGameMode(GameMode gameMode){
        return new GameConfigObject(gameMode, difficulty, stage, playerData, random, randomSeed);
    }

    public GameConfigObject setDifficulty(Difficulty difficulty){
        return new GameConfigObject(gameMode, difficulty, stage, playerData, random, randomSeed);
    }

    public GameConfigObject setStage(int stage){
        return new GameConfigObject(gameMode, difficulty, stage, playerData, random, randomSeed);
    }

    public GameConfigObject setShotType(ShotType shotType){
        return new GameConfigObject(gameMode, difficulty, stage, new PlayerData(shotType), random, randomSeed);
    }

    public GameConfigObject setRandom(long seed){
        return new GameConfigObject(gameMode, difficulty, stage, playerData, new Random(seed), seed);
    }

    @SuppressWarnings("ConstantConditions")
    public void loadPlayerData(){
        if(!isValid()){
            throw new IllegalStateException("invalid gameConfigObject to loadPlayerData: " + this);
        }
        switch(gameMode){
            case STORY:
                PlayerInitValues.STORY.setPlayerData(playerData);
                break;
            case EXTRA:
                PlayerInitValues.EXTRA.setPlayerData(playerData);
                break;
            case PRACTICE:
                switch(stage){
                    case 1:
                        PlayerInitValues.PRACTICE_1.setPlayerData(playerData);
                        break;
                    case 2:
                        PlayerInitValues.PRACTICE_2.setPlayerData(playerData);
                        break;
                    case 3:
                        PlayerInitValues.PRACTICE_3.setPlayerData(playerData);
                        break;
                    case 4:
                        PlayerInitValues.PRACTICE_4.setPlayerData(playerData);
                        break;
                    case 5:
                        PlayerInitValues.PRACTICE_5.setPlayerData(playerData);
                        break;
                    case 6:
                        PlayerInitValues.PRACTICE_6.setPlayerData(playerData);
                        break;
                }
                break;
        }
    }

    @Override
    public String toString() {
        return "GameConfigObject{" +
                "gameMode=" + gameMode +
                ", difficulty=" + difficulty +
                ", stage=" + stage +
                ", playerData=" + playerData +
                ", random=" + random +
                ", randomSeed=" + randomSeed +
                '}';
    }
}