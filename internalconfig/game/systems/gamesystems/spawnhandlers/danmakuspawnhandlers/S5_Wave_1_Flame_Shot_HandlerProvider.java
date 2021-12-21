package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S5_WAVE_1_FLAME_SHOT;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S5_Wave_1_Flame_Shot_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S5_Wave_1_Flame_Shot_HandlerProvider(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(60,
                3.1,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(30,
                3.3,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(15,
                3.5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(10,
                3.7,
                spawnBuilder
        );
    }

    private class Template extends AbstractPositionSpawnHandler {

        private static final double ANGLE_BOUND = 50;
        private static final int TURN_DURATION_BASE = 220;

        private final int mod;
        private final double speed;
        private final int turnDuration;

        private Template(int mod,
                         double speed,
                         SpawnBuilder spawnBuilder) {
            super(S5_WAVE_1_FLAME_SHOT, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.speed = speed;
            turnDuration = (int)(TURN_DURATION_BASE/speed);
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if(tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
                double angle = angleToPlayer + RandomUtil.randDoubleInclusive(-ANGLE_BOUND, ANGLE_BOUND, random);

                DoublePoint basePos = new PolarVector(10, angle).add(pos);
                AbstractVector baseVelocity = new PolarVector(speed, angle);

                Angle finalAngle = new Angle((2 * angleToPlayer) - angle);

                spawnBullet(basePos, baseVelocity, finalAngle, sliceBoard);
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, Angle finalAngle, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, BLUE, NORMAL_OUTBOUND, 0)
                            .setProgram(ProgramUtil.makeShortTurnBulletProgram(1, finalAngle, turnDuration).compile())
                            .packageAsMessage()
            );
        }
    }
}
