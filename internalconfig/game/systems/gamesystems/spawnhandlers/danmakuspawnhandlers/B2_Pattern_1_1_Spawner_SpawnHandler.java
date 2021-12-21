package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.spawns.DanmakuSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.ConstPolarVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B2_Pattern_1_1_Spawner_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final double INIT_SPEED = 2;
    private static final int WAIT_TIME = 100;
    private static final int SLOW_TIME = WAIT_TIME - 2;

    private static final InstructionNode<?, ?>[] PROGRAM =
            ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE
            ).linkInject(
                    ProgramBuilder.linearLink(
                            new InstructionNode<>(SLOW_TO_HALT, SLOW_TIME)
                    ).linkInject(
                            ProgramBuilder.linearLink(
                                    new InstructionNode<>(TIMER, WAIT_TIME),
                                    REMOVE_VISIBLE,
                                    new InstructionNode<>(SET_SPAWN, DanmakuSpawns.B2_PATTERN_1_1_SPAWNER_PATTERN_1),
                                    WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                                    REMOVE_ENTITY
                            )
                    )
            ).compile();

    public B2_Pattern_1_1_Spawner_SpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(DanmakuSpawns.B2_PATTERN_1_1_SPAWNER, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        Random random = GameUtil.getRandom(ecsInterface.getGlobalBoard());
        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

        double angle = RandomUtil.randDoubleInclusive(0, 360, random);
        DoublePoint basePos = new ConstPolarVector(10, angle).add(pos);


        SpawnUtil.ringFormation(pos, basePos, new PolarVector(INIT_SPEED, angle), 3, (p, v) -> spawnSpawner(sliceBoard, p, v));
    }

    private void spawnSpawner(AbstractPublishSubscribeBoard sliceBoard, DoublePoint pos, AbstractVector velocity) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, VIOLET, -200, 0)
                        .setProgram(PROGRAM)
                        .packageAsMessage()
        );
    }
}
