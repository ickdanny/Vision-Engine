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
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.components.spawns.DanmakuSpawns.S5_WAVE_7_FLAME_SPRAY;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S5_Wave_7_Flame_Spray_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S5_Wave_7_Flame_Spray_HandlerProvider(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                60,
                2.1,
                3.9,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                40,
                2.1,
                3.9,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                30,
                2.1,
                3.9,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                20,
                2.1,
                3.9,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private static final double ANGLE_BOUND = 10;
        private static final int TURN_DURATION_BASE = 100;

        private final int mod;
        private final double speedLow;
        private final double speedHigh;
        private final int turnDuration;

        private Template(int mod,
                         double speedLow,
                         double speedHigh,
                         SpawnBuilder spawnBuilder) {
            super(S5_WAVE_7_FLAME_SPRAY, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            turnDuration = (int)(TURN_DURATION_BASE/((speedLow + speedHigh)/2d));
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if(tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double angle = -90 + RandomUtil.randDoubleInclusive(-ANGLE_BOUND, ANGLE_BOUND, random);

                DoublePoint basePos = new PolarVector(10, angle).add(pos);

                double speed = RandomUtil.randDoubleInclusive(speedLow, speedHigh, random);
                AbstractVector baseVelocity = new PolarVector(speed, angle);

                Angle finalAngle = new Angle(180 - angle);

                EnemyProjectileColors color = random.nextBoolean() ? BLUE : RED;
                spawnBullet(basePos, baseVelocity, finalAngle, color, sliceBoard);
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 Angle finalAngle,
                                 EnemyProjectileColors color,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, color, -100, 0)
                            .setProgram(ProgramUtil.makeShortTurnBulletProgram(20, finalAngle, turnDuration).compile())
                            .packageAsMessage()
            );
        }
    }
}
