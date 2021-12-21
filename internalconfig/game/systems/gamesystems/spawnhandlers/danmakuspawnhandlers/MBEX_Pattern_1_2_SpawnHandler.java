package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.MBEX_PATTERN_1_2;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class MBEX_Pattern_1_2_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int MOD = 2;

    private static final double SPEED_LOW = 1.7;
    private static final double SPEED_HIGH = 7;

    private static final double ANGULAR_VELOCITY = .34;

    private static final int SYMMETRY = 14;

    private static final int DIVISIONS = 6;

    private static final int MAX_TICK = MBEX_PATTERN_1_2.getDuration()/DIVISIONS;

    public MBEX_Pattern_1_2_SpawnHandler(SpawnBuilder spawnBuilder,
                                         AbstractComponentTypeContainer componentTypeContainer) {
        super(MBEX_PATTERN_1_2, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if(tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            Random pseudoRandom = GameUtil.getPseudoRandomBasedOnPosition(globalBoard, pos);

            boolean side = ((tick - 1) / MAX_TICK) % 2 == 0;

            double angularVelocity;
            EnemyProjectileColors color;
            if(side){
                angularVelocity = ANGULAR_VELOCITY;
                color = VIOLET;
            } else {
                angularVelocity = -ANGULAR_VELOCITY;
                color = MAGENTA;
            }

            double baseAngle = pseudoRandom.nextDouble() * 360;

            int innerTick = (tick - 1) % MAX_TICK;
            SpawnUtil.whipFormation(innerTick, MAX_TICK, SPEED_LOW, SPEED_HIGH, (speed) -> {
                int spiralTick = tick + ((int)Math.pow( 1 + (((double)innerTick) / MAX_TICK), 7));
                SpawnUtil.spiralFormation(spiralTick, MBEX_PATTERN_1_2.getDuration(), baseAngle, angularVelocity, (spiralAngle) -> {
                    AbstractVector baseVelocity = new PolarVector(speed, spiralAngle);
                    DoublePoint basePos = new PolarVector(10, spiralAngle).add(pos);
                    SpawnUtil.ringFormation(pos, basePos, baseVelocity, SYMMETRY, (p, v) -> spawnBullet(p, v, color, sliceBoard));
                });
            });
        }
    }

    private void spawnBullet(DoublePoint pos,
                             AbstractVector velocity,
                             EnemyProjectileColors color,
                             AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, color, NORMAL_OUTBOUND, 0)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}