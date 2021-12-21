package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
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

import static internalconfig.game.components.spawns.DanmakuSpawns.B1_PATTERN_2_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public class B1_Pattern_2_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B1_Pattern_2_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(25,
                8,
                1.7,
                3.1,
                3,
                50,
                30,
                15,
                6,
                2,
                4.5,
                4,
                150,
                80,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(23,
                13,
                1.7,
                3.3,
                4,
                50,
                30,
                15,
                8,
                2,
                4.5,
                4,
                150,
                80,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(20,
                15,
                1.7,
                3.5,
                4,
                50,
                30,
                12,
                8,
                2,
                4.5,
                5,
                150,
                80,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(17,
                17,
                1.7,
                3.7,
                5,
                50,
                30,
                12,
                10,
                2,
                4.5,
                6,
                150,
                80,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int RING_LENGTH = 100;
        private final int RING_END = B1_PATTERN_2_1.getDuration() - RING_LENGTH; //i cannot make this static ?

        private final int COLUMNS_START = RING_END - 40;

        private static final int RING_WAIT_TIME = 20;
        private static final int COLUMNS_WAIT_TIME = 15;


        private final int ringMod;
        private final int ringSymmetry;
        private final double ringSpeedLow;
        private final double ringSpeedHigh;
        private final int ringRows;
        private final double ringTurn;
        private final int ringTurnTime;

        private final int columnsMod;
        private final int columnsRingSymmetry;
        private final double columnsSpeedLow;
        private final double columnsSpeedHigh;
        private final int columnsRows;
        private final double columnsTurn;
        private final int columnsTurnTime;

        private Template(int ringMod,
                         int ringSymmetry,
                         double ringSpeedLow,
                         double ringSpeedHigh,
                         int ringRows,
                         double ringTurn,
                         int ringTurnTime,

                         int columnsMod,
                         int columnsRingSymmetry,
                         double columnsSpeedLow,
                         double columnsSpeedHigh,
                         int columnsRows,
                         double columnsTurn,
                         int columnsTurnTime,

                         SpawnBuilder spawnBuilder) {
            super(B1_PATTERN_2_1, spawnBuilder, componentTypeContainer);
            this.ringMod = ringMod;
            this.ringSymmetry = ringSymmetry;
            this.ringSpeedLow = ringSpeedLow;
            this.ringSpeedHigh = ringSpeedHigh;
            this.ringRows = ringRows;
            this.ringTurn = ringTurn;
            this.ringTurnTime = ringTurnTime;

            this.columnsMod = columnsMod;
            this.columnsRingSymmetry = columnsRingSymmetry;
            this.columnsSpeedLow = columnsSpeedLow;
            this.columnsSpeedHigh = columnsSpeedHigh;
            this.columnsRows = columnsRows;
            this.columnsTurn = columnsTurn;
            this.columnsTurnTime = columnsTurnTime;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tick >= RING_END && tickMod(tick, ringMod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double baseAngle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

                SpawnUtil.columnFormation(ringSpeedLow, ringSpeedHigh, ringRows, (speed) -> {
                    DoublePoint basePos = new PolarVector(15, baseAngle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, new PolarVector(speed, baseAngle), ringSymmetry, (p, v) -> {
                        Angle angle = v.getAngle();
                        Angle turnAngle1 = angle.add(ringTurn);
                        Angle turnAngle2 = angle.add(-ringTurn);
                        spawnRingBullet(
                                p,
                                v,
                                ProgramUtil.makeShortTurnBulletProgram(RING_WAIT_TIME, turnAngle1, ringTurnTime)
                                        .compile(),
                                sliceBoard
                        );
                        spawnRingBullet(
                                p,
                                v,
                                ProgramUtil.makeShortTurnBulletProgram(RING_WAIT_TIME, turnAngle2, ringTurnTime)
                                        .compile(),
                                sliceBoard
                        );
                    });
                });

            }
            else if (tick < COLUMNS_START && tickMod(tick, columnsMod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double baseAngle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

                SpawnUtil.columnFormation(columnsSpeedLow, columnsSpeedHigh, columnsRows, (speed) -> {
                    DoublePoint basePos = new PolarVector(15, baseAngle).add(pos);
                    SpawnUtil.ringFormation(
                            pos,
                            basePos,
                            new PolarVector(speed, baseAngle),
                            columnsRingSymmetry,
                            (p, v) -> {
                                Angle angle = v.getAngle();
                                Angle turnAngle1 = angle.add(columnsTurn + (speed * 5));
                                Angle turnAngle2 = angle.add(-(columnsTurn + (speed * 5)));
                                spawnColumnsBullet(
                                        p,
                                        v,
                                        ProgramUtil.makeLongTurnBulletProgram(COLUMNS_WAIT_TIME, turnAngle1, columnsTurnTime)
                                                .compile(),
                                        sliceBoard
                                );
                                spawnColumnsBullet(
                                        p,
                                        v,
                                        ProgramUtil.makeLongTurnBulletProgram(COLUMNS_WAIT_TIME, turnAngle2, columnsTurnTime)
                                                .compile(),
                                        sliceBoard
                                );
                            });
                });
            }
        }

        private void spawnRingBullet(DoublePoint pos,
                                     AbstractVector velocity,
                                     InstructionNode<?, ?>[] program,
                                     AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, AZURE,-50, 1)
                            .setProgram(program)
                            .packageAsMessage()
            );
        }

        private void spawnColumnsBullet(DoublePoint pos,
                                        AbstractVector velocity,
                                        InstructionNode<?, ?>[] program,
                                        AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, BLUE,-200, 0)
                            .setProgram(program)
                            .packageAsMessage()
            );
        }
    }
}
