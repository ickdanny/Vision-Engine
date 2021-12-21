package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B5_PATTERN_7_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B5_Pattern_7_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B5_Pattern_7_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(5,
                2,
                1.5,
                2.5,
                .6 * 9,
                50,
                60,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(4,
                3,
                1,
                2.5,
                .6 * 11,
                50,
                70,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(4,
                4,
                1,
                2.5,
                .6 * 11,
                50,
                80,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(3,
                4,
                1,
                2.5,
                .6 * 12,
                50,
                90,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int SPIRAL_FORMATION_MAX_TICK = 413;

        private static final int TURN_TIME = 30;

        private final int mod;

        private final int rows;
        private final double angularVelocity;
        private final double speedLow;
        private final double speedHigh;
        private final int waitTime;
        private final double turnAngle;

        private Template(int mod,
                         int rows,
                         double speedLow,
                         double speedHigh,
                         double angularVelocity,
                         int waitTime,
                         double turnAngle,
                         SpawnBuilder spawnBuilder) {
            super(B5_PATTERN_7_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.rows = rows;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.angularVelocity = angularVelocity;
            this.waitTime = waitTime;
            this.turnAngle = turnAngle;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);

                double baseAngle = pseudoRandom.nextDouble() * 360;

                SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle, angularVelocity, (angle) -> SpawnUtil.columnFormation(speedLow, speedHigh, rows, (speed) ->{
                    AbstractVector initVelocity = new PolarVector(speed, angle);
                    DoublePoint basePos = new PolarVector(10, angle).add(pos);
                    spawnBullet(basePos, initVelocity, initVelocity.getAngle().add(turnAngle),sliceBoard);
                    spawnBullet(basePos, initVelocity, initVelocity.getAngle().subtract(turnAngle),sliceBoard);
                }));
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector initVelocity,
                                 Angle finalAngle,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, initVelocity, SHARP, ROSE, -100, 1)
                            .setProgram(makeProgram(finalAngle))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeProgram(Angle finalAngle){
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE,
                    new InstructionNode<>(TIMER, waitTime),
                    new InstructionNode<>(TURN_TO, new Tuple2<>(finalAngle, TURN_TIME))
            ).compile();
        }
    }
}