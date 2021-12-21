package internalconfig.game.systems.gamesystems.spawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.Difficulty;
import internalconfig.game.systems.GameConfigObject;
import internalconfig.game.systems.ShotType;
import internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers.*;
import internalconfig.game.systems.gamesystems.spawnhandlers.deathspawnhandlers.*;
import internalconfig.game.systems.gamesystems.spawnhandlers.graphicspawnhandlers.*;
import internalconfig.game.systems.gamesystems.spawnhandlers.othergamespawnhandlers.*;
import internalconfig.game.systems.gamesystems.spawnhandlers.pickupspawnhandlers.*;
import internalconfig.game.systems.gamesystems.spawnhandlers.playerspawnhandlers.*;
import internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers.*;

public class SpawnHandlers {
    private final AbstractSpawnHandler[] handlers;

    public SpawnHandlers(AbstractComponentTypeContainer componentTypeContainer, GameConfigObject gameConfigObject){
        handlers = new AbstractSpawnHandler[Spawns.values().length];
        makeHandlers(componentTypeContainer, gameConfigObject);
        throwIfHandlersIncludesNull();
    }

    private void makeHandlers(AbstractComponentTypeContainer componentTypeContainer, GameConfigObject gameConfigObject){
        SpawnBuilder spawnBuilder = new SpawnBuilder(componentTypeContainer);

        int stage = gameConfigObject.getStage();
        ShotType shotType = gameConfigObject.getPlayerData().getShotType();
        Difficulty difficulty = gameConfigObject.getDifficulty();

        for(AbstractSpawnHandler handler : new AbstractSpawnHandler[]{
                new StageSpawnHandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForStage(stage),
                new StageEnderHandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForStage(stage),
                new S1_Wave_2_Smokeball_Spawner_SpawnHandler(spawnBuilder),
                new S1_Wave_3_Bat_Wheel_Spawner_SpawnHandler(spawnBuilder),
                new S1_Wave_4_Fairy_Wing_Spawner_SpawnHandler(spawnBuilder),
                new S1_Wave_6_Bat_Spawner_SpawnHandler(spawnBuilder),
                new S1_Wave_9_Wheel_Spawner_SpawnHandler(spawnBuilder),

                new S2_Wave_1_Fairy_Spawner_SpawnHandler(spawnBuilder),
                new S2_Wave_2_Smokeball_Spawner_SpawnHandler(spawnBuilder),
                new S2_Wave_3_Robo_Spawner_SpawnHandler(spawnBuilder),
                new S2_Wave_4_Wheel_Smokeball_Spawner_SpawnHandler(spawnBuilder),
                new S2_Wave_6_Smokeball_Spawner_SpawnHandler(spawnBuilder),

                new S3_Wave_1_Smokeball_Spawner_SpawnHandler(spawnBuilder),
                new S3_Wave_2_Fairy_Spawner_SpawnHandler(spawnBuilder),
                new S3_Wave_3_Fairy_Spawner_SpawnHandler(spawnBuilder),
                new S3_Wave_5_Virion_Spawner_SpawnHandler(spawnBuilder),
                new S3_Wave_7_Fairy_Spawner_SpawnHandler(spawnBuilder),
                new S3_Wave_9_Fairy_Spawner_SpawnHandler(spawnBuilder),
                new S3_Wave_11_Smokeball_Virion_Spawner_SpawnHandler(spawnBuilder),
                new S3_Wave_16_Fairy_Spawner_SpawnHandler(spawnBuilder),
                new S3_Wave_17_Fairy_Spawner_SpawnHandler(spawnBuilder),

                new S4_Wave_1_Flame_Spawner_SpawnHandler(spawnBuilder),
                new S4_Wave_2_Flame_Spawner_SpawnHandler(spawnBuilder),
                new S4_Wave_3_Flame_Spawner_SpawnHandler(spawnBuilder),
                new S4_Wave_4_Flame_Spawner_SpawnHandler(spawnBuilder),
                new S4_Wave_6_Spike_Spawner_SpawnHandler(spawnBuilder),
                new S4_Wave_7_Apparition_Spawner_SpawnHandler(spawnBuilder),
                new S4_Wave_8_Apparition_Spawner_SpawnHandler(spawnBuilder),
                new S4_Wave_9_Apparition_Spawner_SpawnHandler(spawnBuilder),

                new S5_Wave_1_Flame_Spawner_SpawnHandler(spawnBuilder),
                new S5_Wave_5_Flame_Spawner_SpawnHandler(spawnBuilder),
                new S5_Wave_7_Flame_Spawner_SpawnHandler(spawnBuilder),

                new S6_Wave_1_Flame_Spawner_SpawnHandler(spawnBuilder),

                new SEX_Wave_1_Fairy_Spawner_SpawnHandler(spawnBuilder),
                new SEX_Wave_2_Fairy_Spawner_SpawnHandler(spawnBuilder),
                new SEX_Wave_3_Fairy_Spawner_SpawnHandler(spawnBuilder),
                new SEX_Wave_5_Fairy_Spawner_SpawnHandler(spawnBuilder),
                new SEX_Wave_6_Fairy_Spawner_SpawnHandler(spawnBuilder),
                new SEX_Wave_8_Smokeball_Spawner_SpawnHandler(spawnBuilder),
                new SEX_Wave_9_Smokeball_Spawner_SpawnHandler(spawnBuilder),
                new SEX_Wave_10_Fairy_Spawner_SpawnHandler(spawnBuilder),
                new SEX_Wave_11_Fairy_Spawner_SpawnHandler(spawnBuilder),
                new SEX_Wave_14_Fairy_Spawner_SpawnHandler(spawnBuilder),
                new SEX_Wave_15_Fairy_Spawner_SpawnHandler(spawnBuilder),

                new ShotSpawnHandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForShotType(shotType),
                new BombSpawnHandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForShotType(shotType),
                new BombAExplosionSpawnHandler(spawnBuilder, componentTypeContainer),
                new PlayerBulletBlockerSpawnHandler(spawnBuilder),
                new PlayerDeathPickupSpawnHandler(spawnBuilder, componentTypeContainer),

                new S1_Wave_1_Fairy_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S1_Wave_1_Fairy_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S1_Wave_1_Fairy_3_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S1_Wave_2_Smokeball_Arc_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S1_Wave_2_Smokeball_Ring_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S1_Wave_2_Smokeball_Columns_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S1_Wave_3_Bat_Shot_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S1_Wave_3_Wheel_Spiral_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S1_Wave_4_Fairy_Shot_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S1_Wave_4_Wing_Mirror_Arcs_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S1_Wave_5_Fairy_Accelerating_Rings_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S1_Wave_7_Fairy_Accelerating_Rings_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S1_Wave_9_Wheel_Spiral_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S1_Wave_9_Wheel_Spiral_Reverse_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),

                new B1_Pattern_1_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B1_Pattern_2_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B1_Pattern_3_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B1_Pattern_4_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),

                new S2_Wave_1_Fairy_Shot_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S2_Wave_2_Smokeball_Ring_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S2_Wave_3_Robo_Rings_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S2_Wave_4_Wheel_Columns_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new MB2_Pattern_1_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new MB2_Death_Spiral_Spawner_SpawnHandler(spawnBuilder, componentTypeContainer),
                new MB2_Death_Spiral_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),

                new B2_Pattern_1_1_Spawner_SpawnHandler(spawnBuilder, componentTypeContainer),
                new B2_Pattern_1_1_Spawner_Pattern_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B2_Pattern_1_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B2_Pattern_2_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B2_Pattern_3_1_Spawner_SpawnHandler(spawnBuilder, componentTypeContainer),
                new B2_Pattern_3_1_Spawner_Pattern_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B2_Pattern_3_1_Spawner_Pattern_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B2_Pattern_3_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B2_Pattern_4_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B2_Pattern_5_1_Spawner_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B2_Pattern_5_1_Spawner_Pattern_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B2_Pattern_5_1_Spawner_Pattern_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B2_Pattern_6_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),

                new S3_Wave_1_Smokeball_Spray_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S3_Wave_2_Fairy_Rings_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S3_Wave_4_Wing_Down_Whip_Arc_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S3_Wave_4_Wing_Shotgun_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S3_Wave_4_Wing_Mirror_Arcs_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S3_Wave_5_Virion_Tilted_Ring_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S3_Wave_6_Wing_Arcs_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S3_Wave_7_Fairy_Rings_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S3_Wave_8_Fairy_Spray_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new MB3_Pattern_1_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),

                new B3_Pattern_1_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B3_Pattern_2_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B3_Pattern_3_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B3_Pattern_4_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B3_Pattern_5_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B3_Pattern_6_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B3_Pattern_6_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),

                new S4_Wave_1_Flame_Arc_Spawner_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S4_Wave_1_Flame_Arc_Spawner_Pattern_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new S4_Wave_1_Flame_Arc_Spawner_Pattern_2_SpawnHandler(spawnBuilder, componentTypeContainer),
                new S4_Wave_5_Spike_Ring_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S4_Wave_6_Spike_Spiral_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S4_Wave_6_Spike_Spiral_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S4_Wave_7_Apparition_Arcs_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S4_Wave_7_Apparition_Rings_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S4_Wave_8_Apparition_Columns_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S4_Wave_8_Apparition_Rings_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S4_Wave_9_Apparition_Blocks_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S4_Wave_10_Apparition_Pattern_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S4_Wave_10_Apparition_Pattern_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S4_Wave_10_Apparition_Pattern_3_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S4_Wave_15_Spike_Ring_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),

                new B4_Pattern_1_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B4_Pattern_1_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B4_Pattern_2_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B4_Pattern_2_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B4_Pattern_3_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B4_Pattern_3_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B4_Pattern_4_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B4_Pattern_5_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B4_Pattern_5_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B4_Pattern_6_1_Spawner_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B4_Pattern_6_1_Spawner_Pattern_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B4_Pattern_6_1_Spawner_Pattern_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B4_Pattern_6_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B4_Pattern_7_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),

                new S5_Wave_1_Flame_Shot_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S5_Wave_2_Spike_Rings_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S5_Wave_3_Spike_Shot_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new MB5_Pattern_1_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S5_Wave_7_Flame_Spray_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),

                new B5_Pattern_1_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B5_Pattern_2_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B5_Pattern_2_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B5_Pattern_3_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B5_Pattern_4_1_Spawner_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B5_Pattern_4_1_Spawner_Pattern_1_HandlerProvider(spawnBuilder,componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B5_Pattern_4_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B5_Pattern_5_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B5_Pattern_6_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B5_Pattern_7_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B5_Pattern_8_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),

                new S6_Wave_1_Flame_Shot_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new S6_Wave_2_Apparition_Columns_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new MB6_Pattern_1_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),

                new B6_Pattern_1_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B6_Pattern_2_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B6_Pattern_3_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B6_Pattern_4_1_Spawner_SpawnHandler(spawnBuilder, componentTypeContainer),
                new B6_Pattern_4_1_Spawner_Pattern_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B6_Pattern_4_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B6_Pattern_5_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B6_Pattern_6_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B6_Pattern_6_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B6_Pattern_7_1_Spawner_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B6_Pattern_8_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B6_Pattern_8_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B6_Pattern_8_3_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B6_Pattern_9_1_Spawner_SpawnHandler(spawnBuilder, componentTypeContainer),
                new B6_Pattern_9_1_Spawner_Pattern_1_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new B6_Pattern_9_2_HandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),

                new SEX_Wave_1_Fairy_Shot_SpawnHandler(spawnBuilder, componentTypeContainer),
                new SEX_Wave_4_Fairy_Spray_SpawnHandler(spawnBuilder, componentTypeContainer),
                new SEX_Wave_5_Fairy_Ring_SpawnHandler(spawnBuilder, componentTypeContainer),
                new SEX_Wave_6_Fairy_Shot_SpawnHandler(spawnBuilder, componentTypeContainer),
                new SEX_Wave_7_Wheel_Spiral_SpawnHandler(spawnBuilder, componentTypeContainer),
                new SEX_Wave_8_Smokeball_Spiral_SpawnHandler(spawnBuilder, componentTypeContainer),
                new SEX_Wave_9_Smokeball_Spiral_SpawnHandler(spawnBuilder, componentTypeContainer),
                new MBEX_Pattern_1_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new MBEX_Pattern_1_2_SpawnHandler(spawnBuilder, componentTypeContainer),
                new SEX_Wave_10_Fairy_Ring_SpawnHandler(spawnBuilder, componentTypeContainer),
                new SEX_Wave_12_Fairy_Spray_SpawnHandler(spawnBuilder, componentTypeContainer),
                new SEX_Wave_16_Fairy_Spray_SpawnHandler(spawnBuilder, componentTypeContainer),

                new BEX_Pattern_1_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_2_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_2_2_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_3_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_4_1_Spawner_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_4_1_Spawner_Pattern_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_4_2_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_5_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_6_1_Spawner_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_6_1_Spawner_Pattern_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_6_2_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_7_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_8_1_SpawnHandler(spawnBuilder),
                new BEX_Pattern_9_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_10_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_11_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_12_1_Spawner_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_12_1_Spawner_Pattern_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_12_1_Spawner_Pattern_2_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_12_2_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_12_3_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_13_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_14_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_14_2_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_15_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_16_1_Spawner_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_16_1_Spawner_Pattern_1_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_16_1_Spawner_Pattern_2_SpawnHandler(spawnBuilder, componentTypeContainer),
                new BEX_Pattern_17_1_Spawner_SpawnHandler(spawnBuilder, componentTypeContainer),

                new BossExplodeSpawnHandler(spawnBuilder, componentTypeContainer),

                new DropSmallPowerSpawnHandler(spawnBuilder, componentTypeContainer),
                new DropSmallPowerHalfSpawnHandler(spawnBuilder, componentTypeContainer),
                new DropSmallPowerThirdSpawnHandler(spawnBuilder, componentTypeContainer),
                new DropSmallPowerFourthSpawnHandler(spawnBuilder, componentTypeContainer),
                new DropSmallPowerFifthSpawnHandler(spawnBuilder, componentTypeContainer),
                new DropSmallPowerSixthSpawnHandler(spawnBuilder, componentTypeContainer),
                new DeathShotAndDropSmallPowerHandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new DeathShotAndDropSmallPowerThirdSpawnHandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new DeathShotAndDropSmallPowerFourthSpawnHandlerProvider(spawnBuilder, componentTypeContainer).getSpawnHandlerForDifficulty(difficulty),
                new DropLargePowerSpawnHandler(spawnBuilder, componentTypeContainer),
                new DropBombSpawnHandler(spawnBuilder, componentTypeContainer),
                new DropLifeSpawnHandler(spawnBuilder, componentTypeContainer),
                new DropLifeAndClearSpawnHandler(spawnBuilder, componentTypeContainer),

                new Power10SpawnHandler(spawnBuilder, componentTypeContainer),
                new Power12SpawnHandler(spawnBuilder, componentTypeContainer),
                new Power14SpawnHandler(spawnBuilder, componentTypeContainer),
                new Power16SpawnHandler(spawnBuilder, componentTypeContainer),
                new LifePickupSpawnHandler(spawnBuilder, componentTypeContainer),
                new BombPickupSpawnHandler(spawnBuilder, componentTypeContainer),

                new BulletClearSpawnHandler(spawnBuilder),
                new MediumBulletClearSpawnHandler(spawnBuilder),
                new LongBulletClearSpawnHandler(spawnBuilder),

                new BasicExplodeSpawnHandler(spawnBuilder, componentTypeContainer),
                new SpecialNormalExplodeSpawnHandler(spawnBuilder, componentTypeContainer),
                new SpecialMediumExplodeSpawnHandler(spawnBuilder, componentTypeContainer),
                new SpecialHighExplodeSpawnHandler(spawnBuilder, componentTypeContainer),

                new SmallExplodeRedSpawnHandler(spawnBuilder, componentTypeContainer),
                new SmallExplodeOrangeSpawnHandler(spawnBuilder, componentTypeContainer),
                new SmallExplodeYellowSpawnHandler(spawnBuilder, componentTypeContainer),
                new SmallExplodeChartreuseSpawnHandler(spawnBuilder, componentTypeContainer),
                new SmallExplodeGreenSpawnHandler(spawnBuilder, componentTypeContainer),
                new SmallExplodeSpringSpawnHandler(spawnBuilder, componentTypeContainer),
                new SmallExplodeCyanSpawnHandler(spawnBuilder, componentTypeContainer),
                new SmallExplodeAzureSpawnHandler(spawnBuilder, componentTypeContainer),
                new SmallExplodeBlueSpawnHandler(spawnBuilder, componentTypeContainer),
                new SmallExplodeVioletSpawnHandler(spawnBuilder, componentTypeContainer),
                new SmallExplodeMagentaSpawnHandler(spawnBuilder, componentTypeContainer),
                new SmallExplodeRoseSpawnHandler(spawnBuilder, componentTypeContainer),

                new MediumExplodeRedSpawnHandler(spawnBuilder, componentTypeContainer),
                new MediumExplodeOrangeSpawnHandler(spawnBuilder, componentTypeContainer),
                new MediumExplodeYellowSpawnHandler(spawnBuilder, componentTypeContainer),
                new MediumExplodeChartreuseSpawnHandler(spawnBuilder, componentTypeContainer),
                new MediumExplodeGreenSpawnHandler(spawnBuilder, componentTypeContainer),
                new MediumExplodeSpringSpawnHandler(spawnBuilder, componentTypeContainer),
                new MediumExplodeCyanSpawnHandler(spawnBuilder, componentTypeContainer),
                new MediumExplodeAzureSpawnHandler(spawnBuilder, componentTypeContainer),
                new MediumExplodeBlueSpawnHandler(spawnBuilder, componentTypeContainer),
                new MediumExplodeVioletSpawnHandler(spawnBuilder, componentTypeContainer),
                new MediumExplodeMagentaSpawnHandler(spawnBuilder, componentTypeContainer),
                new MediumExplodeRoseSpawnHandler(spawnBuilder, componentTypeContainer),

                new LargeExplodeRedSpawnHandler(spawnBuilder, componentTypeContainer),
                new LargeExplodeOrangeSpawnHandler(spawnBuilder, componentTypeContainer),
                new LargeExplodeYellowSpawnHandler(spawnBuilder, componentTypeContainer),
                new LargeExplodeChartreuseSpawnHandler(spawnBuilder, componentTypeContainer),
                new LargeExplodeGreenSpawnHandler(spawnBuilder, componentTypeContainer),
                new LargeExplodeSpringSpawnHandler(spawnBuilder, componentTypeContainer),
                new LargeExplodeCyanSpawnHandler(spawnBuilder, componentTypeContainer),
                new LargeExplodeAzureSpawnHandler(spawnBuilder, componentTypeContainer),
                new LargeExplodeBlueSpawnHandler(spawnBuilder, componentTypeContainer),
                new LargeExplodeVioletSpawnHandler(spawnBuilder, componentTypeContainer),
                new LargeExplodeMagentaSpawnHandler(spawnBuilder, componentTypeContainer),
                new LargeExplodeRoseSpawnHandler(spawnBuilder, componentTypeContainer),

        }){
            handlers[handler.getSpawn().getIndex()] = handler;
        }
    }

    private void throwIfHandlersIncludesNull(){
        for(Spawns spawn : Spawns.values()){
            if(handlers[spawn.getIndex()] == null){
                throw new RuntimeException("handler for " + spawn + " is null");
            }
        }
    }

    public void handleSpawn(AbstractECSInterface ecsInterface, Spawns spawn, int tick, int entityID){
        AbstractSpawnHandler handler = handlers[spawn.getIndex()];
        if(handler == null){
            throw new RuntimeException("cannot find handler for " + spawn);
        }
        handler.handleSpawn(ecsInterface, tick, entityID);
    }
}
