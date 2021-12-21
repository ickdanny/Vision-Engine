package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.ProgramUtil;
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

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B1_PATTERN_3_1;

import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public class B1_Pattern_3_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B1_Pattern_3_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(20,
                1.5,
                5.4,
                7,
                210,
                40,
                15,
                4,
                5,
                16.5,
                2.83,
                98.785,
                65,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(16,
                1.5,
                5.6,
                7,
                210,
                40,
                15,
                3,
                6,
                8.42,
                2.83,
                98.785,
                65,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(13,
                1.5,
                5.8,
                7,
                210,
                40,
                15,
                3,
                7,
                7.352,
                2.83,
                98.785,
                65,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(10,
                1.5,
                6,
                7,
                210,
                40,
                15,
                2,
                8,
                9.853,
                2.83,
                98.785,
                65,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int WHIP_LENGTH = 70;
        private final int WHIP_END = B1_PATTERN_3_1.getDuration() - WHIP_LENGTH; //i cannot make this static ?

        private static final int WHIP_WAIT_TIME = 20;
        private static final int SPIRAL_WAIT_TIME = 30;

        private final int whipMod;
        private final double whipSpeedLow;
        private final double whipSpeedHigh;
        private final int whipArcSymmetry;
        private final double whipTotalAngle;
        private final double whipTurn;
        private final int whipTurnTime;

        private final int spiralMod;
        private final int spiralSymmetry;
        private final double angularVelocity;
        private final double spiralSpeed;
        private final double spiralTurn;
        private final int spiralTurnTime;

        private Template(int whipMod,
                         double whipSpeedLow,
                         double whipSpeedHigh,
                         int whipArcSymmetry,
                         double whipTotalAngle,
                         double whipTurn,
                         int whipTurnTime,
                         int spiralMod,
                         int spiralSymmetry,
                         double angularVelocity,
                         double spiralSpeed,
                         double spiralTurn,
                         int spiralTurnTime,
                         SpawnBuilder spawnBuilder) {
            super(B1_PATTERN_3_1, spawnBuilder, componentTypeContainer);
            this.whipMod = whipMod;
            this.whipSpeedLow = whipSpeedLow;
            this.whipSpeedHigh = whipSpeedHigh;
            this.whipArcSymmetry = whipArcSymmetry;
            this.whipTotalAngle = whipTotalAngle;
            this.whipTurn = whipTurn;
            this.whipTurnTime = whipTurnTime;
            this.spiralMod = spiralMod;
            this.spiralSymmetry = spiralSymmetry;
            this.angularVelocity = angularVelocity;
            this.spiralSpeed = spiralSpeed;
            this.spiralTurn = spiralTurn;
            this.spiralTurnTime = spiralTurnTime;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tick >= WHIP_END && tickMod(tick, whipMod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double baseAngle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
                SpawnUtil.whipFormation(tick - WHIP_END, WHIP_LENGTH, whipSpeedLow, whipSpeedHigh, (speed) -> {
                    DoublePoint basePos = new PolarVector(15, baseAngle).add(pos);
                    SpawnUtil.arcFormation(
                            pos,
                            basePos,
                            new PolarVector(speed, baseAngle),
                            whipArcSymmetry,
                            whipTotalAngle,
                            (p, v) -> {
                                Angle angle = v.getAngle();
                                Angle turnAngle1 = angle.add(whipTurn);
                                Angle turnAngle2 = angle.add(-whipTurn);
                                spawnSmallBullet(
                                        p,
                                        v,
                                        ProgramUtil.makeShortTurnBulletProgram(WHIP_WAIT_TIME, turnAngle1, whipTurnTime)
                                                .compile(),
                                        sliceBoard
                                );
                                spawnSmallBullet(
                                        p,
                                        v,
                                        ProgramUtil.makeShortTurnBulletProgram(WHIP_WAIT_TIME, turnAngle2, whipTurnTime)
                                                .compile(),
                                        sliceBoard
                                );
                                spawnMediumBullet(p, new PolarVector(v.getMagnitude() - .5, v.getAngle()), sliceBoard);
                            });
                });
            }
            else if (tick < WHIP_END && tickMod(tick, spiralMod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double baseAngle = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID).nextDouble() * 360;
                SpawnUtil.spiralFormation(tick, B1_PATTERN_3_1.getDuration(), baseAngle, angularVelocity, (spiralAngle) -> {

                    AbstractVector baseVelocity = new PolarVector(spiralSpeed, spiralAngle);
                    DoublePoint basePos = new PolarVector(15, spiralAngle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, baseVelocity, spiralSymmetry, (p, v) -> {
                        Angle angle = v.getAngle();

                        Angle turnAngle = tickMod(tick, spiralMod * 2) ? angle.add(spiralTurn) : angle.add(-spiralTurn);

                        spawnSharpBullet(
                                p,
                                v,
                                ProgramUtil.makeLongTurnBulletProgram(SPIRAL_WAIT_TIME, turnAngle, spiralTurnTime)
                                        .compile(),
                                sliceBoard
                        );
                    });
                });
            }
        }

        private void spawnSmallBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      InstructionNode<?, ?>[] program,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, ORANGE, -200, 2)
                            .setProgram(program)
                            .packageAsMessage()
            );
        }

        private void spawnSharpBullet(DoublePoint pos,
                                      AbstractVector velocity,
                                      InstructionNode<?, ?>[] program,
                                      AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, RED, -200, 0)
                            .setProgram(program)
                            .packageAsMessage()
            );
        }

        private void spawnMediumBullet(DoublePoint pos,
                                       AbstractVector velocity,
                                       AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, ORANGE, NORMAL_OUTBOUND, -1)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}
