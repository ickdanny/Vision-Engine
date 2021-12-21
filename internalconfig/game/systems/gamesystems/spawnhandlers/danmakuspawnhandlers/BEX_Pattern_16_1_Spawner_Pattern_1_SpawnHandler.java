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
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_16_1_SPAWNER_PATTERN_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_16_1_Spawner_Pattern_1_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int MOD = 3;

    private static final double ANGULAR_VELOCITY = 5.3;

    private static final int WAIT_TIME = 105;
    private static final int SPEED_TIME = 60;

    private static final double SPEED = 1.97;


    public BEX_Pattern_16_1_Spawner_Pattern_1_SpawnHandler(SpawnBuilder spawnBuilder,
                                                           AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_16_1_SPAWNER_PATTERN_1, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            double baseAngle = pseudoRandom.nextDouble() * 360;

            SpawnUtil.spiralFormation(tick, 48292, baseAngle, ANGULAR_VELOCITY, (angle) -> {
                AbstractVector initVelocity = new PolarVector(0, angle);
                AbstractVector finalVelocity = new PolarVector(SPEED, angle);
                spawnBullet(pos, initVelocity, finalVelocity, sliceBoard);
            });
        }
    }

    private void spawnBullet(DoublePoint pos,
                             AbstractVector initVelocity,
                             AbstractVector finalVelocity,
                             AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, initVelocity, SHARP, CYAN, NORMAL_OUTBOUND, -2)
                        .setProgram(makeProgram(finalVelocity))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(AbstractVector finalVelocity) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE,
                new InstructionNode<>(TIMER, 10),
                new InstructionNode<>(SET_DRAW_ORDER, 310),
                new InstructionNode<>(TIMER, WAIT_TIME - 10),
                new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(finalVelocity, SPEED_TIME))
        ).compile();
    }
}