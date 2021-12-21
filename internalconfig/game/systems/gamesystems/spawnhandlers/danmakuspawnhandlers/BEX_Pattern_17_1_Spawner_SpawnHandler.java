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
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_17_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class BEX_Pattern_17_1_Spawner_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int MOD = 4;

    private static final double DISTANCE = 50;

    private static final double SPAWN_ANGULAR_VELOCITY = -.6 * (3d/7);

    private static final double SINE_TICK_MULTI = 1d/83.56321;

    private static final int SYMMETRY = 2;
    private static final double ANGULAR_VELOCITY = .6 * (5d/2);

    private static final double SPEED_LOW = 1.4;
    private static final double SPEED_HIGH = 3.613;
    private static final double SPEED_AVERAGE = (SPEED_HIGH + SPEED_LOW)/2;
    private static final double SPEED_BOUND = SPEED_HIGH - SPEED_AVERAGE;

    public BEX_Pattern_17_1_Spawner_SpawnHandler(SpawnBuilder spawnBuilder,
                                         AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_17_1, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)){
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);
            pseudoRandom.nextDouble();

            double baseSpawnAngle = pseudoRandom.nextDouble() * 360;
            double baseAngle = pseudoRandom.nextDouble() * 360;

            double speed = SPEED_AVERAGE + (Math.sin(tick * SINE_TICK_MULTI * 2 * Math.PI) * SPEED_BOUND);

            SpawnUtil.spiralFormation(tick, 342153, baseSpawnAngle, SPAWN_ANGULAR_VELOCITY, (spawnAngle) -> {
                DoublePoint baseSpawnPos = new PolarVector(DISTANCE, spawnAngle).add(pos);
                AtomicInteger atomicInteger = new AtomicInteger(0);

                SpawnUtil.ringFormation(pos, baseSpawnPos, 6, (spawnPos) -> {
                    Angle spawnerBaseAngle = GeometryUtil.angleFromAToB(pos, spawnPos).add(baseAngle);

                    SpawnUtil.spiralFormation(tick, 344211, spawnerBaseAngle.getAngle(), ANGULAR_VELOCITY, (spiralAngle) -> {

                        AbstractVector baseVelocity = new PolarVector(speed, spiralAngle);
                        DoublePoint basePos = new PolarVector(3, spiralAngle).add(spawnPos);
                        SpawnUtil.ringFormation(spawnPos, basePos, baseVelocity, SYMMETRY, (p, v) -> spawnBullet(p, v, getColor(atomicInteger.get()), sliceBoard));
                    });

                    atomicInteger.incrementAndGet();
                });
            });
        }
    }

    private EnemyProjectileColors getColor(int index){
        switch (index){
            case 0:
                return RED;
            case 1:
                return ORANGE;
            case 2:
                return YELLOW;
            case 3:
                return GREEN;
            case 4:
                return BLUE;
            case 5:
                return VIOLET;
            default:
                throw new RuntimeException("bad index: " + index);
        }
    }

    private void spawnBullet(DoublePoint pos,
                              AbstractVector velocity,
                              EnemyProjectileColors color,
                              AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, color, NORMAL_OUTBOUND, 0)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}