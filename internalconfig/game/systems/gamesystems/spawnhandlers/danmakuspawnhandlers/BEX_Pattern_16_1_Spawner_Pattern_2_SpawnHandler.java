package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionVelocitySpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_16_1_SPAWNER_PATTERN_1;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_16_1_SPAWNER_PATTERN_2;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_16_1_Spawner_Pattern_2_SpawnHandler extends AbstractPositionVelocitySpawnHandler {

    //SAME SPEED AS BELOW

    private static final double SPEED = 3;

    private static final int LIFETIME = 30;
    private static final double ANGLE_INCREMENT = 120;

    private static final InstructionNode<?, ?>[] PROGRAM = ProgramBuilder.linearLink(
            new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
            SET_COLLIDABLE,
            new InstructionNode<>(SET_SPAWN, BEX_PATTERN_16_1_SPAWNER_PATTERN_1),
            new InstructionNode<>(TIMER, LIFETIME - ENEMY_BULLET_COLLIDABLE_TIME),
            DIE
    ).compile();

    public BEX_Pattern_16_1_Spawner_Pattern_2_SpawnHandler(SpawnBuilder spawnBuilder,
                                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_16_1_SPAWNER_PATTERN_2, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if(tick == 75 || tick == 35){ //fuck this
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
            AbstractVector spawnerVelocity = GameUtil.getVelocity(dataStorage, entityID, velocityComponentType);

            Angle spawnerAngle = spawnerVelocity.getAngle();

            AbstractVector baseVelocity = new PolarVector(SPEED, spawnerAngle);

            SpawnUtil.arcFormationIncrement(pos, pos, baseVelocity, 2, ANGLE_INCREMENT, (p, v) -> spawnSpawner(p, v, sliceBoard));
        }
    }

    private void spawnSpawner(DoublePoint pos,
                              AbstractVector velocity,
                              AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, BLUE, NORMAL_OUTBOUND, 0)
                        .setProgram(PROGRAM)
                        .packageAsMessage()
        );
    }
}