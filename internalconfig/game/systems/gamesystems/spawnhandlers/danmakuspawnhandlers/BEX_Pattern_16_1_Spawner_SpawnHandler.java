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

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_16_1_SPAWNER;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_16_1_SPAWNER_PATTERN_1;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_16_1_SPAWNER_PATTERN_2;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_16_1_Spawner_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final double SPEED = 3;

    private static final InstructionNode<?, ?>[] PROGRAM = ProgramBuilder.linearLink(
            new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
            SET_COLLIDABLE,
            new InstructionNode<>(SET_SPAWN, BEX_PATTERN_16_1_SPAWNER_PATTERN_1),
            new InstructionNode<>(TIMER, 1),
            new InstructionNode<>(ADD_SPAWN, BEX_PATTERN_16_1_SPAWNER_PATTERN_2)
    ).compile();

    public BEX_Pattern_16_1_Spawner_SpawnHandler(SpawnBuilder spawnBuilder,
                                                 AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_16_1_SPAWNER, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

        Random pseudoRandom = GameUtil.getPseudoRandomBasedOnPosition(globalBoard, pos);
        double baseAngle = pseudoRandom.nextDouble() * 360;

        AbstractVector baseVelocity = new PolarVector(SPEED, baseAngle);
        DoublePoint basePos = new PolarVector(2, baseAngle).add(pos);

        SpawnUtil.ringFormation(pos, basePos, baseVelocity, 6, (p, v) -> spawnSpawner(p, v, sliceBoard));

    }

    private void spawnSpawner(DoublePoint pos,
                              AbstractVector velocity,
                              AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, LARGE, BLUE, LARGE_OUTBOUND, -1)
                        .setProgram(PROGRAM)
                        .packageAsMessage()
        );
    }
}

