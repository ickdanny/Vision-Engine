package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_12_1_SPAWNER;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_12_1_SPAWNER_PATTERN_1;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_12_1_SPAWNER_PATTERN_2;

public class BEX_Pattern_12_1_Spawner_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int MOD = BEX_PATTERN_12_1_SPAWNER.getDuration() / 5;

    private static final double SPEED = 6;

    private static final AbstractVector VELOCITY = new PolarVector(SPEED, -90);

    private static final int MAX_PRETIMER = BEX_PATTERN_12_1_SPAWNER_PATTERN_1.getDuration();

    public BEX_Pattern_12_1_Spawner_SpawnHandler(SpawnBuilder spawnBuilder,
                                                 AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_12_1_SPAWNER, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            Random random = GameUtil.getRandom(globalBoard);

            int preTimer = RandomUtil.randIntInclusive(0, MAX_PRETIMER, random);

            DoublePoint playerPos = GameUtil.getPos(dataStorage, GameUtil.getPlayer(sliceBoard), positionComponentType);
            double playerX = playerPos.getX();

            DoublePoint pos = new DoublePoint(playerX, TOP_OUT);
            spawnSpawner(pos, preTimer, sliceBoard);
        }
    }

    private void spawnSpawner(DoublePoint pos,
                              int preTimer,
                              AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, VELOCITY, LARGE, AZURE, ENEMY_OUTBOUND, -10)
                        .setProgram(makeProgram(preTimer))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(int preTimer){
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE,
                new InstructionNode<>(TIMER, preTimer),
                new InstructionNode<>(SET_SPAWN, BEX_PATTERN_12_1_SPAWNER_PATTERN_1),
                new InstructionNode<>(BOUNDARY_Y_HIGH, HEIGHT + 10d),
                new InstructionNode<>(ADD_SPAWN, BEX_PATTERN_12_1_SPAWNER_PATTERN_2)
        ).compile();
    }
}


