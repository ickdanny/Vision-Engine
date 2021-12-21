package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramUtil;
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

import static internalconfig.game.components.spawns.DanmakuSpawns.B3_PATTERN_6_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B3_Pattern_6_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B3_Pattern_6_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    //MOD MUST BE ODD

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                13,
                1,
                3,
                20,
                25,
                50,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                11,
                1,
                3.3,
                23,
                26,
                70,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                9,
                1,
                3.6,
                24,
                28,
                90,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                7,
                1,
                3.9,
                25,
                29,
                120,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int WAIT_TIME = 25;
        private static final int TURN_DURATION = 30;


        private final int mod;
        private final double speedLow;
        private final double speedHigh;
        private final int symmetryLow;
        private final int symmetryHigh;

        private final double turnOffset;

        private Template(int mod,
                         double speedLow,
                         double speedHigh,
                         int symmetryLow,
                         int symmetryHigh,
                         double turnOffset,
                         SpawnBuilder spawnBuilder) {
            super(B3_PATTERN_6_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.symmetryLow = symmetryLow;
            this.symmetryHigh = symmetryHigh;
            this.turnOffset = turnOffset;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if(tickMod(tick, mod)){
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random random = GameUtil.getRandom(globalBoard);
                double angle = random.nextDouble() * 360;
                DoublePoint basePos = new PolarVector(15, angle).add(pos);
                double speed = RandomUtil.randDoubleInclusive(speedLow, speedHigh, random);

                int symmetry = RandomUtil.randIntInclusive(symmetryLow, symmetryHigh, random);

                AbstractVector baseVelocity = new PolarVector(speed, angle);
                double angleOffset = tick % 2 == 0 ? -turnOffset : turnOffset;
                EnemyProjectileColors color = tick % 2 == 0 ? BLUE : ROSE;
                SpawnUtil.ringFormation(pos, basePos, baseVelocity, symmetry, (p, v) -> {

                    Angle finalAngle = v.getAngle().add(angleOffset);
                    spawnBullet(p, v, finalAngle, color, sliceBoard);
                });
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 Angle finalAngle,
                                 EnemyProjectileColors color,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, color, -100, 10)
                            .setProgram(ProgramUtil.makeShortTurnBulletProgram(WAIT_TIME, finalAngle, TURN_DURATION).compile())
                            .packageAsMessage()
            );
        }
    }
}
