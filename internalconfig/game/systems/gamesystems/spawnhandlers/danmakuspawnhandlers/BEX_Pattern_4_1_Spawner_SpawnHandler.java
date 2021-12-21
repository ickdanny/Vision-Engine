package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_4_1_SPAWNER;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_4_1_SPAWNER_PATTERN_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_4_1_Spawner_SpawnHandler extends AbstractPositionSpawnHandler {

    private final InstructionNode<?, ?>[] SPAWNER_PROGRAM = ProgramBuilder.linearLink(
            new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
            SET_COLLIDABLE,
            new InstructionNode<>(SET_SPAWN, BEX_PATTERN_4_1_SPAWNER_PATTERN_1)
    ).compile();

    private static final double ANGLE_INCREMENT = 70;
    private static final double SPEED = 2.8;

    public BEX_Pattern_4_1_Spawner_SpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_4_1_SPAWNER, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if(tick == BEX_PATTERN_4_1_SPAWNER.getDuration()){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

            AbstractVector baseVelocity = new PolarVector(SPEED, angleToPlayer);
            DoublePoint basePos = new PolarVector(10, angleToPlayer).add(pos);

            spawnSpawner(basePos, baseVelocity, sliceBoard);
        }
        else if(tick == 1) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

            AbstractVector baseVelocity = new PolarVector(SPEED, angleToPlayer);
            DoublePoint basePos = new PolarVector(10, angleToPlayer).add(pos);

            SpawnUtil.arcFormationIncrement(pos, basePos, baseVelocity, 2, ANGLE_INCREMENT, (p, v) -> spawnSpawner(p, v, sliceBoard));
        }
    }

    private void spawnSpawner(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, LARGE, BLUE, LARGE_OUTBOUND, -10)
                        .setProgram(SPAWNER_PROGRAM)
                        .packageAsMessage()
        );
    }
}