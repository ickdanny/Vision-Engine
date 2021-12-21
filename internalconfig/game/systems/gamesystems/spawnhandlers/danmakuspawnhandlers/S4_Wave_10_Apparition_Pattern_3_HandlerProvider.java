package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
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
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S4_WAVE_10_APPARITION_PATTERN_3;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S4_Wave_10_Apparition_Pattern_3_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S4_Wave_10_Apparition_Pattern_3_HandlerProvider(SpawnBuilder spawnBuilder,
                                                           AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                25,
                7,
                3,
                .4,
                4.233,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                20,
                7,
                3,
                .4,
                4.233,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                16,
                8,
                4,
                .4,
                4.233,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                12,
                8,
                4,
                .4,
                4.233,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int TOTAL_ANGLE = 180;

        private static final double BASE_ANGLE = 110;
        private static final double TURN_ANGLE_OFFSET = 90;

        private static final double FINAL_ANGLE_OFFSET_RANGE = 10;

        private static final int WAIT_TIME = 15;
        private static final int LONG_TURN_TIME = 42;
        private static final int FINAL_WAIT_TIME = 30;
        private static final int SHORT_TURN_TIME = 40;

        private final int mod;
        private final int symmetry1;
        private final int symmetry2;
        private final double angularVelocity;
        private final double speed;

        private Template(int mod,
                         int symmetry1,
                         int symmetry2,
                         double angularVelocity,
                         double speed,
                         SpawnBuilder spawnBuilder) {
            super(S4_WAVE_10_APPARITION_PATTERN_3, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetry1 = symmetry1;
            this.symmetry2 = symmetry2;
            this.angularVelocity = angularVelocity;
            this.speed = speed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                SpawnUtil.spiralFormation(tick, S4_WAVE_10_APPARITION_PATTERN_3.getDuration(), BASE_ANGLE, angularVelocity, (spiralAngle) -> {

                    AbstractVector baseVelocity = new PolarVector(speed, spiralAngle);
                    DoublePoint basePos = new PolarVector(10, spiralAngle).add(pos);

                    SpawnUtil.arcFormation(pos, basePos, baseVelocity, symmetry1, TOTAL_ANGLE, (p, v) -> {
                        Angle angle = v.getAngle();
                        Angle turnAngle = angle.add(TURN_ANGLE_OFFSET);
                        spawnTurnBullet(p, v, turnAngle, RED, 0, sliceBoard, random);
                    });
                });

                SpawnUtil.spiralFormation(tick, S4_WAVE_10_APPARITION_PATTERN_3.getDuration(), 180-BASE_ANGLE, -angularVelocity, (spiralAngle) -> {

                    AbstractVector baseVelocity = new PolarVector(speed, spiralAngle);
                    DoublePoint basePos = new PolarVector(10, spiralAngle).add(pos);

                    SpawnUtil.arcFormation(pos, basePos, baseVelocity, symmetry1, TOTAL_ANGLE, (p, v) -> {
                        Angle angle = v.getAngle();
                        Angle turnAngle = angle.subtract(TURN_ANGLE_OFFSET);
                        spawnTurnBullet(p, v, turnAngle, BLUE, 1, sliceBoard, random);
                    });
                });

                SpawnUtil.spiralFormation(tick, S4_WAVE_10_APPARITION_PATTERN_3.getDuration(), BASE_ANGLE + 180, angularVelocity, (spiralAngle) -> {

                    AbstractVector baseVelocity = new PolarVector(speed, spiralAngle);
                    DoublePoint basePos = new PolarVector(10, spiralAngle).add(pos);

                    SpawnUtil.arcFormation(pos, basePos, baseVelocity, symmetry2, TOTAL_ANGLE, (p, v) -> {
                        Angle angle = v.getAngle();
                        Angle turnAngle = angle.add(TURN_ANGLE_OFFSET);
                        spawnTurnBullet(p, v, turnAngle, RED, 0, sliceBoard, random);
                    });
                });

                SpawnUtil.spiralFormation(tick, S4_WAVE_10_APPARITION_PATTERN_3.getDuration(), -BASE_ANGLE, -angularVelocity, (spiralAngle) -> {

                    AbstractVector baseVelocity = new PolarVector(speed, spiralAngle);
                    DoublePoint basePos = new PolarVector(10, spiralAngle).add(pos);

                    SpawnUtil.arcFormation(pos, basePos, baseVelocity, symmetry2, TOTAL_ANGLE, (p, v) -> {
                        Angle angle = v.getAngle();
                        Angle turnAngle = angle.subtract(TURN_ANGLE_OFFSET);
                        spawnTurnBullet(p, v, turnAngle, BLUE, 1, sliceBoard, random);
                    });
                });
            }
        }

        private void spawnTurnBullet(DoublePoint pos,
                                     AbstractVector velocity,
                                     Angle turnAngle,
                                     EnemyProjectileColors color,
                                     int drawOrder,
                                     AbstractPublishSubscribeBoard sliceBoard,
                                     Random random) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, color,-200, drawOrder)
                            .setProgram(makeTurnProgram(turnAngle, random))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeTurnProgram(Angle turnAngle, Random random){
            double finalAngleOffset = RandomUtil.randDoubleInclusive(-FINAL_ANGLE_OFFSET_RANGE, FINAL_ANGLE_OFFSET_RANGE, random);
            Angle finalAngle = turnAngle.add(finalAngleOffset);
            return ProgramUtil.makeLongTurnBulletProgram(WAIT_TIME, turnAngle, LONG_TURN_TIME)
                    .linkAppend(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(TIMER, FINAL_WAIT_TIME),
                                    new InstructionNode<>(TURN_TO, new Tuple2<>(finalAngle, SHORT_TURN_TIME))
                            )
                    )
                    .compile();
        }
    }
}
