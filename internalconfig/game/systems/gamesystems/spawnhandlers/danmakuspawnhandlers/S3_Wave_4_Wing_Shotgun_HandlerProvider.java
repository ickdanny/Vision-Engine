package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S3_WAVE_4_WING_SHOTGUN;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S3_Wave_4_Wing_Shotgun_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S3_Wave_4_Wing_Shotgun_HandlerProvider(SpawnBuilder spawnBuilder,
                                                        AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                9,
                3,
                5,
                2,
                4,
                35,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                11,
                4,
                5.5,
                2,
                4,
                35,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                13,
                4,
                6,
                2,
                4,
                35,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                15,
                5,
                7,
                2,
                4,
                35,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private final EnemyProjectileColors COLOR = ROSE;

        private final int MOD = S3_WAVE_4_WING_SHOTGUN.getDuration()/3;

        private final int symmetry;
        private final int spawns;
        private final double speed;
        private final double speedVariation;
        private final double angleVariation;
        private final double spawnDistVariation;

        private Template(int symmetry,
                         int spawns,
                         double speed,
                         double speedVariation,
                         double angleVariation,
                         double spawnDistVariation,
                         SpawnBuilder spawnBuilder) {
            super(S3_WAVE_4_WING_SHOTGUN, spawnBuilder, componentTypeContainer);
            this.symmetry = symmetry;
            this.spawns = spawns;
            this.speed = speed;
            this.speedVariation = speedVariation;
            this.angleVariation = angleVariation;
            this.spawnDistVariation = spawnDistVariation;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, MOD)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();
                Random random = GameUtil.getRandom(ecsInterface.getGlobalBoard());

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double angle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

                DoublePoint basePos = new PolarVector(10, angle).add(pos);

                SpawnUtil.ringFormation(pos, basePos, new PolarVector(speed, angle), symmetry, (p, v) -> {
                    spawnLargeBullet(p, v, sliceBoard);
                    SpawnUtil.randomAngles(0, 360, spawns, random, (medSpawnAngle) -> {
                        double spawnDist = RandomUtil.randDoubleInclusive(0, spawnDistVariation, random);
                        DoublePoint mediumSpawn = new PolarVector(spawnDist, medSpawnAngle).add(pos);
                        double mediumSpeed = speed - RandomUtil.randDoubleInclusive(0, speedVariation, random);
                        Angle mediumAngle = v.getAngle().add(RandomUtil.randDoubleInclusive(-angleVariation, angleVariation, random));
                        AbstractVector mediumVelocity = new PolarVector(mediumSpeed, mediumAngle);
                        spawnMediumBullet(mediumSpawn, mediumVelocity, sliceBoard);
                    });
                });
            }
        }

        private void spawnLargeBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, LARGE, COLOR, LARGE_OUTBOUND, 10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

        private void spawnMediumBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard){
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, COLOR, NORMAL_OUTBOUND, -2)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}