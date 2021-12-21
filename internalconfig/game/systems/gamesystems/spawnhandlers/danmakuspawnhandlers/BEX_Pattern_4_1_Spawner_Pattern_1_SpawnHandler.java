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
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_4_1_SPAWNER_PATTERN_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_4_1_Spawner_Pattern_1_SpawnHandler extends AbstractPositionVelocitySpawnHandler {

    private static final int MOD = 11;

    private static final double ANGLE_INCREMENT = 120;

    private static final double ANGLE_RANGE = 3.6;

    private static final double INIT_SPEED = 2.67;
    private static final double SPEED_LOW_MULTI = .5;

    private static final int WAIT_TIME = 2;
    private static final int SLOW_TIME = 25;

    public BEX_Pattern_4_1_Spawner_Pattern_1_SpawnHandler(SpawnBuilder spawnBuilder,
                                                          AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_4_1_SPAWNER_PATTERN_1, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            Random random = GameUtil.getRandom(globalBoard);

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
            AbstractVector spawnerVelocity = GameUtil.getVelocity(dataStorage, entityID, velocityComponentType);

            Angle spawnerAngle = spawnerVelocity.getAngle();
            Angle backAngle = spawnerAngle.add(180);

            AbstractVector baseInitVelocity = new PolarVector(INIT_SPEED, backAngle);
            DoublePoint basePos = new PolarVector(10, backAngle).add(pos);

            SpawnUtil.arcFormationIncrement(pos, basePos, baseInitVelocity, 2, ANGLE_INCREMENT, (p, initVelocity) -> {
                double speedMulti = RandomUtil.randDoubleInclusive(SPEED_LOW_MULTI, 1d, random);
                double angle = initVelocity.getAngle().getAngle() + RandomUtil.randDoubleInclusive(-ANGLE_RANGE, ANGLE_RANGE, random);
                AbstractVector finalVelocity = new PolarVector(initVelocity.getMagnitude() * speedMulti, angle);
                spawnBullet(p, initVelocity, finalVelocity, sliceBoard);
            });
        }
    }

    private void spawnBullet(DoublePoint pos,
                             AbstractVector initVelocity,
                             AbstractVector finalVelocity,
                             AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, initVelocity, SMALL, BLUE, -35, 10)
                        .setProgram(makeProgram(finalVelocity))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeProgram(AbstractVector finalVelocity) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                SET_COLLIDABLE,
                new InstructionNode<>(TIMER, WAIT_TIME),
                new InstructionNode<>(SLOW_DOWN_AND_TURN_TO_VELOCITY, new Tuple2<>(finalVelocity, SLOW_TIME))
        ).compile();
    }
}