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
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S3_WAVE_5_VIRION_TILTED_RING;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S3_Wave_5_Virion_Tilted_Ring_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S3_Wave_5_Virion_Tilted_Ring_HandlerProvider(SpawnBuilder spawnBuilder,
                                                        AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                7,
                4,
                2,
                4,
                30,
                2,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                8,
                4,
                2,
                4,
                50,
                2,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                10,
                4,
                2,
                4,
                70,
                2,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                12,
                4,
                2,
                4,
                90,
                3,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private static final int SLOW_DURATION = 50;
        private static final int WAIT_TIMER = 13;

        private final int symmetry;
        private final double initSpeed;
        private final double finalSpeedLow;
        private final double finalSpeedHigh;
        private final double angleOffset;
        private final int rows;

        private Template(int symmetry,
                         double initSpeed,
                         double finalSpeedLow,
                         double finalSpeedHigh,
                         double angleOffset,
                         int rows,
                         SpawnBuilder spawnBuilder) {
            super(S3_WAVE_5_VIRION_TILTED_RING, spawnBuilder, componentTypeContainer);
            this.symmetry = symmetry;
            this.initSpeed = initSpeed;
            this.finalSpeedLow = finalSpeedLow;
            this.finalSpeedHigh = finalSpeedHigh;
            this.angleOffset = angleOffset;
            this.rows = rows;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            Random random = GameUtil.getRandom(ecsInterface.getGlobalBoard());
            double angle = RandomUtil.randDoubleInclusive(0, 360, random);
            boolean direction = random.nextBoolean();

            DoublePoint basePos = new PolarVector(10, angle).add(pos);
            SpawnUtil.columnFormation(finalSpeedLow, finalSpeedHigh, rows, (finalSpeed) -> SpawnUtil.ringFormation(pos, basePos, new PolarVector(initSpeed, angle), symmetry, (p, v) -> {
                EnemyProjectileColors color;
                Angle finalAngle;
                if(direction){
                    color = CYAN;
                    finalAngle = v.getAngle().add(angleOffset);
                }
                else{
                    color = GREEN;
                    finalAngle = v.getAngle().subtract(angleOffset);
                }
                AbstractVector finalVelocity = new PolarVector(finalSpeed, finalAngle);
                spawnBullet(p, v, finalVelocity, color, sliceBoard);
            }));
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 AbstractVector finalVelocity,
                                 EnemyProjectileColors color,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, color, -100, 20)
                            .setProgram(makeProgram(finalVelocity))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeProgram(AbstractVector finalVelocity) {
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE
            ).linkInject(
                    ProgramBuilder.linearLink(
                            new InstructionNode<>(SLOW_TO_HALT, SLOW_DURATION),
                            new InstructionNode<>(TIMER, WAIT_TIMER),
                            new InstructionNode<>(SET_VELOCITY, finalVelocity)
                    )
            ).compile();
        }
    }
}