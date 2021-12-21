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
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B1_PATTERN_4_1;

import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public class B1_Pattern_4_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B1_Pattern_4_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                B1_PATTERN_4_1.getDuration() / 3,
                7,
                .8,
                3,
                5,
                90,
                45,
                25,
                3,
                .8,
                2,
                1,
                110,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                B1_PATTERN_4_1.getDuration() / 4,
                8,
                .8,
                3,
                5,
                90,
                45,
                25,
                3,
                .8,
                2,
                1,
                110,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                B1_PATTERN_4_1.getDuration() / 5,
                8,
                .8,
                3,
                6,
                90,
                45,
                25,
                5,
                .8,
                2,
                1,
                140,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                B1_PATTERN_4_1.getDuration() / 6,
                9,
                .8,
                3,
                6,
                90,
                45,
                25,
                5,
                .8,
                2,
                2,
                180,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private static final int COLUMNS_WAIT_TIME = 35;

        private final int columnsMod;
        private final int columnsRingSymmetry;
        private final double columnsSpeedLow;
        private final double columnsSpeedHigh;
        private final int columnsRows;
        private final double columnsTurn;
        private final int columnsTurnTime;
        private final double columnsSpeedAngleMultiplier; //goes up to 30

        private final int arcSymmetry;
        private final double arcSpeedLow;
        private final double arcSpeedHigh;
        private final int arcRows;
        private final int arcTotalAngle;

        private Template(int columnsMod,
                         int columnsRingSymmetry,
                         double columnsSpeedLow,
                         double columnsSpeedHigh,
                         int columnsRows,
                         double columnsTurn,
                         int columnsTurnTime,
                         double columnsSpeedAngleMultiplier,

                         int arcSymmetry,
                         double arcSpeedLow,
                         double arcSpeedHigh,
                         int arcRows,
                         int arcTotalAngle,

                         SpawnBuilder spawnBuilder) {
            super(B1_PATTERN_4_1, spawnBuilder, componentTypeContainer);

            this.columnsMod = columnsMod;
            this.columnsRingSymmetry = columnsRingSymmetry;
            this.columnsSpeedLow = columnsSpeedLow;
            this.columnsSpeedHigh = columnsSpeedHigh;
            this.columnsRows = columnsRows;
            this.columnsTurn = columnsTurn;
            this.columnsTurnTime = columnsTurnTime;
            this.columnsSpeedAngleMultiplier = columnsSpeedAngleMultiplier;

            this.arcSymmetry = arcSymmetry;
            this.arcSpeedLow = arcSpeedLow;
            this.arcSpeedHigh = arcSpeedHigh;
            this.arcRows = arcRows;
            this.arcTotalAngle = arcTotalAngle;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, columnsMod) || tick == (B1_PATTERN_4_1.getDuration() / 3)) {
                Random random = GameUtil.getRandom(ecsInterface.getGlobalBoard());
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                if(tickMod(tick, columnsMod)){
                    double baseAngle = RandomUtil.randDoubleInclusive(0, Math.nextDown(360d), random);

                    SpawnUtil.columnFormation(columnsSpeedLow, columnsSpeedHigh, columnsRows, (speed) -> {
                        DoublePoint basePos = new PolarVector(15, baseAngle).add(pos);
                        SpawnUtil.ringFormation(
                                pos,
                                basePos,
                                new PolarVector(speed, baseAngle),
                                columnsRingSymmetry,
                                (p, v) -> {
                                    Angle angle = v.getAngle();
                                    Angle turnAngle1 = angle.add(columnsTurn + (speed * columnsSpeedAngleMultiplier));
                                    Angle turnAngle2 = angle.add(-(columnsTurn + (speed * columnsSpeedAngleMultiplier)));
                                    spawnColumnsBullet(
                                            p,
                                            v,
                                            ProgramUtil.makeShortTurnBulletProgram(COLUMNS_WAIT_TIME, turnAngle1, columnsTurnTime)
                                                    .compile(),
                                            sliceBoard
                                    );
                                    spawnColumnsBullet(
                                            p,
                                            v,
                                            ProgramUtil.makeShortTurnBulletProgram(COLUMNS_WAIT_TIME, turnAngle2, columnsTurnTime)
                                                    .compile(),
                                            sliceBoard
                                    );
                                });
                    });
                }

                if (tick == (B1_PATTERN_4_1.getDuration() / 3)) {
                    double baseAngle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
                    SpawnUtil.columnFormation(arcSpeedLow, arcSpeedHigh, arcRows, (speed) -> {
                        DoublePoint basePos = new PolarVector(15, baseAngle).add(pos);
                        SpawnUtil.arcFormation(
                                pos,
                                basePos,
                                new PolarVector(speed, baseAngle),
                                arcSymmetry,
                                arcTotalAngle,
                                (p, v) -> spawnArcBullet(p, v, sliceBoard));
                    });
                }
            }
        }

        private void spawnColumnsBullet(DoublePoint pos,
                                        AbstractVector velocity,
                                        InstructionNode<?, ?>[] program,
                                        AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, ROSE,-100, 1)
                            .setProgram(program)
                            .packageAsMessage()
            );
        }

        private void spawnArcBullet(DoublePoint pos,
                                    AbstractVector velocity,
                                    AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, LARGE, VIOLET, LARGE_OUTBOUND, -10)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}