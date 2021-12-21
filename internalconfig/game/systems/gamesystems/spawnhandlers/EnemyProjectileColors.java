package internalconfig.game.systems.gamesystems.spawnhandlers;

public enum EnemyProjectileColors {
    RED("_red"),
    ORANGE("_orange"),
    YELLOW("_yellow"),
    CHARTREUSE("_chartreuse"),
    GREEN("_green"),
    SPRING("_spring"),
    CYAN("_cyan"),
    AZURE("_azure"),
    BLUE("_blue"),
    VIOLET("_violet"),
    MAGENTA("_magenta"),
    ROSE("_rose"),
    ;
    private final String suffix;

    EnemyProjectileColors(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }
}
