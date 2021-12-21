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
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B6_PATTERN_4_1_SPAWNER;
import static internalconfig.game.components.spawns.DanmakuSpawns.B6_PATTERN_4_1_SPAWNER_PATTERN_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B6_Pattern_4_1_Spawner_SpawnHandler extends AbstractPositionSpawnHandler {

    private final InstructionNode<?, ?>[] SPAWNER_PROGRAM = ProgramBuilder.linearLink(
            new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
            SET_COLLIDABLE,
            new InstructionNode<>(SET_SPAWN, B6_PATTERN_4_1_SPAWNER_PATTERN_1)
    ).compile();

    private static final double MIN_Y = 9.2;

    private static final double MAX_Y = 217.52;

    private static final double SPEED = 2.8;

    public B6_Pattern_4_1_Spawner_SpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(B6_PATTERN_4_1_SPAWNER, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if(tick == B6_PATTERN_4_1_SPAWNER.getDuration() || tick == B6_PATTERN_4_1_SPAWNER.getDuration()/2){
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            Random random = GameUtil.getRandom(globalBoard);

            double y1 = RandomUtil.randDoubleInclusive(MIN_Y, MAX_Y, random);
            double y2 = RandomUtil.randDoubleInclusive(MIN_Y, MAX_Y, random);

            DoublePoint start;
            DoublePoint end;

            if(tick == B6_PATTERN_4_1_SPAWNER.getDuration()){
                start = new DoublePoint(LEFT_OUT, y1);
                end = new DoublePoint(RIGHT_OUT, y2);
            }
            else{
                start = new DoublePoint(RIGHT_OUT, y1);
                end = new DoublePoint(LEFT_OUT, y2);
            }

            Angle angle = GeometryUtil.angleFromAToB(start, end);
            AbstractVector velocity = new PolarVector(SPEED, angle);
            spawnSpawner(start, velocity, sliceBoard);
        }
    }

    private void spawnSpawner(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, MAGENTA, -50, 0)
                        .setProgram(SPAWNER_PROGRAM)
                        .packageAsMessage()
        );
    }
}
