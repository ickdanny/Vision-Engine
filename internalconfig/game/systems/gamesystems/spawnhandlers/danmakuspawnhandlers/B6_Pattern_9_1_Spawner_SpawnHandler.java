package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
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
import static internalconfig.game.components.spawns.DanmakuSpawns.B6_PATTERN_9_1_SPAWNER;
import static internalconfig.game.components.spawns.DanmakuSpawns.B6_PATTERN_9_1_SPAWNER_PATTERN_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class B6_Pattern_9_1_Spawner_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int MOD = 30;

    private static final double MIN_X = 30;
    private static final double MAX_X = WIDTH - MIN_X;

    private static final double SPEED = 4.6;

    private static final double ANGLE_BOUND = 3;

    public B6_Pattern_9_1_Spawner_SpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(B6_PATTERN_9_1_SPAWNER, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if(tickMod(tick, MOD)){
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            Random random = GameUtil.getRandom(globalBoard);

            double x = RandomUtil.randDoubleInclusive(MIN_X, MAX_X, random);

            DoublePoint pos = new DoublePoint(x, TOP_OUT - 40);

            double angle = -90 + RandomUtil.randDoubleInclusive(-ANGLE_BOUND, ANGLE_BOUND, random);

            AbstractVector velocity = new PolarVector(SPEED, angle);
            spawnSpawner(pos, velocity, sliceBoard, random);
        }
    }

    private void spawnSpawner(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard, Random random) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, LARGE, YELLOW, -100, -10)
                        .setProgram(makeProgram(random))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(Random random){
        int delay = RandomUtil.randIntInclusive(0, 20, random);
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE,
                new InstructionNode<>(TIMER, delay),
                new InstructionNode<>(SET_SPAWN, B6_PATTERN_9_1_SPAWNER_PATTERN_1)
        ).compile();
    }
}
