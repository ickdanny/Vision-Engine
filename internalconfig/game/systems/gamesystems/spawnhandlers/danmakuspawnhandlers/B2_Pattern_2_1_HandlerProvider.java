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
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B2_PATTERN_2_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B2_Pattern_2_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B2_Pattern_2_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                           AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(10,
                2,
                2,
                2,
                2.35,
                3.2,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(8,
                2,
                2,
                2,
                2.35,
                3.2,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(6,
                2,
                2,
                3,
                2.35,
                3.2,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(6,
                3,
                2,
                3,
                2.35,
                3.2,
                4,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int SPIRAL_FORMATION_MAX_TICK = 273;

        private static final double DISTANCE_1 = 50;
        private static final double DISTANCE_2 = 100;

        private static final double SPAWN_ANGULAR_VELOCITY_1 = 1.5;
        private static final double SPAWN_ANGULAR_VELOCITY_2 = -1.5;

        private static final int WAIT_TIME = 30;
        private static final int SPEED_TIME = 90;

        private final int mod;
        private final int spawnSymmetry1;
        private final int spawnSymmetry2;
        private final int symmetry;
        private final double angularVelocity;
        private final double speed1;
        private final double speed2;

        private Template(int mod,
                         int spawnSymmetry1,
                         int spawnSymmetry2,
                         int symmetry,
                         double angularVelocity,
                         double speed1,
                         double speed2,
                         SpawnBuilder spawnBuilder) {
            super(B2_PATTERN_2_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.spawnSymmetry1 = spawnSymmetry1;
            this.spawnSymmetry2 = spawnSymmetry2;
            this.symmetry = symmetry;
            this.angularVelocity = angularVelocity;
            this.speed1 = speed1;
            this.speed2 = speed2;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);
                double spiralSpawnBaseAngle = pseudoRandom.nextDouble() * 360 + 44;
                double baseAngle = pseudoRandom.nextDouble() * 360 + 12;

                SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, spiralSpawnBaseAngle, SPAWN_ANGULAR_VELOCITY_1,
                        (spawnAngle1) -> SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle, angularVelocity, (initAngle) -> {

                            DoublePoint spiralSpawnBasePos = new PolarVector(DISTANCE_1, spawnAngle1).add(pos);

                            SpawnUtil.ringFormation(pos, spiralSpawnBasePos, spawnSymmetry1, (spawnPos) -> {

                                DoublePoint basePos = new PolarVector(3, initAngle).add(spawnPos);
                                AbstractVector baseVelocity = new PolarVector(speed1, initAngle);

                                SpawnUtil.ringFormation(spawnPos, basePos, baseVelocity, symmetry,
                                        (p, v) -> spawnBullet1(p, v, sliceBoard)
                                );
                            });
                        })
                );

                SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, spiralSpawnBaseAngle, SPAWN_ANGULAR_VELOCITY_2,
                        (spawnAngle1) -> SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle, angularVelocity, (initAngle) -> {

                            DoublePoint spiralSpawnBasePos = new PolarVector(DISTANCE_2, spawnAngle1).add(pos);

                            SpawnUtil.ringFormation(pos, spiralSpawnBasePos, spawnSymmetry2, (spawnPos) -> {

                                DoublePoint basePos = new PolarVector(3, initAngle).add(spawnPos);
                                AbstractVector baseVelocity = new PolarVector(speed2, initAngle);

                                SpawnUtil.ringFormation(spawnPos, basePos, baseVelocity, symmetry,
                                        (p, v) -> spawnBullet2(p, v, sliceBoard)
                                );
                            });
                        })
                );
            }
        }

        private void spawnBullet1(DoublePoint pos,
                                  AbstractVector velocity,
                                  AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, makeInitVelocity(velocity), MEDIUM, VIOLET, -100, -1)
                            .setProgram(makeProgram(velocity))
                            .packageAsMessage()
            );
        }

        private void spawnBullet2(DoublePoint pos,
                                  AbstractVector velocity,
                                  AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, makeInitVelocity(velocity), SHARP, CYAN, -100, 1)
                            .setProgram(makeProgram(velocity))
                            .packageAsMessage()
            );
        }

        private AbstractVector makeInitVelocity(AbstractVector velocity){
            return new PolarVector(.5, velocity.getAngle());
        }

        private InstructionNode<?, ?>[] makeProgram(AbstractVector velocity){
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE
            ).linkInject(
                    ProgramBuilder.linearLink(
                            new InstructionNode<>(TIMER, WAIT_TIME),
                            new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(velocity, SPEED_TIME))
                    )
            ).compile();
        }
    }
}
