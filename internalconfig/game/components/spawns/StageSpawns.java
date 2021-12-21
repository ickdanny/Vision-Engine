package internalconfig.game.components.spawns;

public enum StageSpawns implements Spawns_PP{
    STAGE_SPAWNER(StageSpawns.STAGE_DURATION),

    STAGE_ENDER(1),

    S1_WAVE_2_SMOKEBALL_SPAWNER(60 * 6 + 45),
    S1_WAVE_3_BAT_WHEEL_SPAWNER(60 * 11),
    S1_WAVE_4_FAIRY_WING_SPAWNER(329),
    S1_WAVE_6_BAT_SPAWNER(90 * 4),
    S1_WAVE_9_WHEEL_SPAWNER(360 * 4),

    S2_WAVE_1_FAIRY_SPAWNER(9 * 13),
    S2_WAVE_2_SMOKEBALL_SPAWNER((60 * 11) + 40),
    S2_WAVE_3_ROBO_SPAWNER(4 * 55),
    S2_WAVE_4_WHEEL_SMOKEBALL_SPAWNER(62 * 6),
    S2_WAVE_6_SMOKEBALL_SPAWNER(60 * 13),

    S3_WAVE_1_SMOKEBALL_SPAWNER(60 * 16 + 30),
    S3_WAVE_2_FAIRY_SPAWNER(60),
    S3_WAVE_3_FAIRY_SPAWNER(80),
    S3_WAVE_5_VIRION_SPAWNER(60 * 15),
    S3_WAVE_7_FAIRY_SPAWNER(60),
    S3_WAVE_9_FAIRY_SPAWNER(80),
    S3_WAVE_11_SMOKEBALL_VIRION_SPAWNER(60 * 16),
    S3_WAVE_16_FAIRY_SPAWNER(2 * 60),
    S3_WAVE_17_FAIRY_SPAWNER(2 * 60),

    S4_WAVE_1_FLAME_SPAWNER(60 * 6),
    S4_WAVE_2_FLAME_SPAWNER(60 * 6),
    S4_WAVE_3_FLAME_SPAWNER(60 * 2),
    S4_WAVE_4_FLAME_SPAWNER(60 * 2),
    S4_WAVE_6_SPIKE_SPAWNER(60 * 16),
    S4_WAVE_7_APPARITION_SPAWNER( 4 * 24),
    S4_WAVE_8_APPARITION_SPAWNER(5 * 20),
    S4_WAVE_9_APPARITION_SPAWNER(60 * 6),

    S5_WAVE_1_FLAME_SPAWNER(120),
    S5_WAVE_5_FLAME_SPAWNER(140),
    S5_WAVE_7_FLAME_SPAWNER(60 * 15),

    S6_WAVE_1_FLAME_SPAWNER(60 * 9),

    SEX_WAVE_1_FAIRY_SPAWNER(60 * 2),
    SEX_WAVE_2_FAIRY_SPAWNER(60 * 2),
    SEX_WAVE_3_FAIRY_SPAWNER(60 * 2),
    SEX_WAVE_5_FAIRY_SPAWNER(4 * 20),
    SEX_WAVE_6_FAIRY_SPAWNER( 60 * 17 + 30),
    SEX_WAVE_8_SMOKEBALL_SPAWNER(60 * 19),
    SEX_WAVE_9_SMOKEBALL_SPAWNER(60 * 9),
    SEX_WAVE_10_FAIRY_SPAWNER(8 * 20),
    SEX_WAVE_11_FAIRY_SPAWNER(8 * 20),
    SEX_WAVE_14_FAIRY_SPAWNER(60 * 4),
    SEX_WAVE_15_FAIRY_SPAWNER(60 * 4)
    ;

    public static final int STAGE_DURATION = 60 * 60 * 4;

    private final int duration;
    private int index;

    StageSpawns(int duration){
        this.duration = duration;
        index = INVALID_INDEX;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public boolean loop() {
        return false;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        if(this.index != INVALID_INDEX){
            throw new RuntimeException("cannot set index twice!");
        }
        this.index = index;
    }
}
