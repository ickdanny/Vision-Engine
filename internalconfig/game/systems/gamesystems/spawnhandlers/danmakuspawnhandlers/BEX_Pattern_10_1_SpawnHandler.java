package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_10_1;

public class BEX_Pattern_10_1_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int MOD = 20;
    private static final int SPIRAL_FORMATION_MAX_TICK = 413;

    private static final int SYMMETRY = 18;

    private static final double TOTAL_ANGULAR_VELOCITY = .6 * (1d/12);

    private static final double ANGULAR_VELOCITY_1 = .6 * (4d/12) + TOTAL_ANGULAR_VELOCITY;
    private static final double ANGULAR_VELOCITY_2 = -.6 * (4d/12) + TOTAL_ANGULAR_VELOCITY;

    private static final double SPEED = 1.7;

    private static final double SPAWN_DISTANCE = 400;
    private static final double OUTBOUND = -200;

    private static final int LIFETIME = (int)(SPAWN_DISTANCE/SPEED) - 20;

    private static final InstructionNode<?, ?>[] PROGRAM = ProgramBuilder.linearLink(
            new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
            SET_COLLIDABLE,
            new InstructionNode<>(TIMER, LIFETIME),
            DIE
    ).compile();

    private static final int SIDE_MOD = 150;
    private static final int SIDE_ROWS = 3;
    private static final double SIDE_SPEED_LOW = 1.3;
    private static final double SIDE_SPEED_HIGH = 1.7;

    public BEX_Pattern_10_1_SpawnHandler(SpawnBuilder spawnBuilder,
                                         AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_10_1, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);

            double baseAngle1 = pseudoRandom.nextDouble() * 360;
            double baseAngle2 = pseudoRandom.nextDouble() * 360;

            SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle1, ANGULAR_VELOCITY_1, (spiralAngle) -> {
                DoublePoint basePos1 = new PolarVector(SPAWN_DISTANCE, spiralAngle).add(pos);
                SpawnUtil.ringFormation(pos, basePos1, SYMMETRY, (p) -> {
                    Angle angle = GeometryUtil.angleFromAToB(p, pos);
                    AbstractVector velocity = new PolarVector(SPEED, angle);
                    spawnSharp(p, velocity, ROSE, sliceBoard);
                });
            });

            SpawnUtil.spiralFormation(tick, SPIRAL_FORMATION_MAX_TICK, baseAngle2, ANGULAR_VELOCITY_2, (spiralAngle) -> {
                DoublePoint basePos2 = new PolarVector(SPAWN_DISTANCE, spiralAngle).add(pos);
                SpawnUtil.ringFormation(pos, basePos2, SYMMETRY, (p) -> {
                    Angle angle = GeometryUtil.angleFromAToB(p, pos);
                    AbstractVector velocity = new PolarVector(SPEED, angle);
                    spawnSharp(p, velocity, ORANGE, sliceBoard);
                });
            });
        }
        if(tickMod(tick, SIDE_MOD) && tick != BEX_PATTERN_10_1.getDuration()){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint playerPos = GameUtil.getPos(dataStorage, GameUtil.getPlayer(sliceBoard), positionComponentType);
            double playerY = playerPos.getY();

            SpawnUtil.columnFormation(SIDE_SPEED_LOW, SIDE_SPEED_HIGH, SIDE_ROWS, (speed) -> {
                spawnSideBullet(new DoublePoint(LEFT_OUT, playerY), new PolarVector(speed, 0), sliceBoard);
                spawnSideBullet(new DoublePoint(RIGHT_OUT, playerY), new PolarVector(speed, 180), sliceBoard);
            });

        }
    }

    private void spawnSharp(DoublePoint pos,
                            AbstractVector velocity,
                            EnemyProjectileColors color,
                            AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, color, OUTBOUND, 10)
                        .setProgram(PROGRAM)
                        .packageAsMessage()
        );
    }

    private void spawnSideBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 AbstractPublishSubscribeBoard sliceBoard){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, VIOLET, ENEMY_OUTBOUND, 0)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }

}