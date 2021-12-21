package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
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
import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S4_WAVE_10_APPARITION_PATTERN_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S4_Wave_10_Apparition_Pattern_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S4_Wave_10_Apparition_Pattern_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    //SAME AS BELOW

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                22,
                9,
                210,
                .4,
                4.233,
                5.03,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                17,
                9,
                210,
                .4,
                4.233,
                5.03,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                14,
                9,
                210,
                .4,
                4.233,
                5.03,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                11,
                9,
                210,
                .4,
                4.233,
                5.03,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final double BASE_ANGLE = -185;
        private static final double TURN_ANGLE_OFFSET = 90;

        private static final double FINAL_ANGLE_OFFSET_RANGE = 10;

        private static final double STRAIGHT_ANGLE_OFFSET = -12;

        private static final int WAIT_TIME = 15;
        private static final int LONG_TURN_TIME = 42;
        private static final int FINAL_WAIT_TIME = 30;
        private static final int SHORT_TURN_TIME = 40;

        private final int mod;
        private final int turnSymmetry;
        private final double turnTotalAngle;
        private final double turnAngularVelocity;
        private final double turnSpeed;

        private final double straightSpeed;

        private Template(int mod,
                         int turnSymmetry,
                         double turnTotalAngle,
                         double turnAngularVelocity,
                         double turnSpeed,
                         double straightSpeed,
                         SpawnBuilder spawnBuilder) {
            super(S4_WAVE_10_APPARITION_PATTERN_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.turnSymmetry = turnSymmetry;
            this.turnTotalAngle = turnTotalAngle;
            this.turnAngularVelocity = turnAngularVelocity;
            this.turnSpeed = turnSpeed;
            this.straightSpeed = straightSpeed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                SpawnUtil.spiralFormation(tick, S4_WAVE_10_APPARITION_PATTERN_1.getDuration(), BASE_ANGLE, turnAngularVelocity, (spiralAngle) -> {

                    AbstractVector baseVelocity = new PolarVector(turnSpeed, spiralAngle);
                    DoublePoint basePos = new PolarVector(15, spiralAngle).add(pos);

                    SpawnUtil.arcFormation(pos, basePos, baseVelocity, turnSymmetry, turnTotalAngle, (p, v) -> {
                        Angle angle = v.getAngle();
                        Angle turnAngle = angle.add(TURN_ANGLE_OFFSET);
                        spawnTurnBullet(p, v, turnAngle, sliceBoard, random);

                        spawnStraightBullet(p, new PolarVector(straightSpeed, v.getAngle().add(STRAIGHT_ANGLE_OFFSET)), sliceBoard);
                    });
                });
            }
        }

        private void spawnStraightBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, VIOLET, NORMAL_OUTBOUND, 10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

        private void spawnTurnBullet(DoublePoint pos,
                                     AbstractVector velocity,
                                     Angle turnAngle,
                                     AbstractPublishSubscribeBoard sliceBoard,
                                     Random random) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, RED,-200, 0)
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
