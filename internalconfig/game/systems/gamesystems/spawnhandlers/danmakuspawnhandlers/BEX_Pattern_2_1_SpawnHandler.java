package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.BEX_PATTERN_2_1;

public class BEX_Pattern_2_1_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final int MOD = 3;

    private static final double SPEED_LOW = 2.2;
    private static final double SPEED_HIGH = 4.4;

    private static final double ANGLE_RANGE = 3;

    public BEX_Pattern_2_1_SpawnHandler(SpawnBuilder spawnBuilder,
                                        AbstractComponentTypeContainer componentTypeContainer) {
        super(BEX_PATTERN_2_1, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            Random random = GameUtil.getRandom(globalBoard);

            SpawnUtil.randomTopOutPosition(0, WIDTH, random, (pos) -> SpawnUtil.randomVelocity(SPEED_LOW, SPEED_HIGH, new Angle(-90), ANGLE_RANGE, random, (velocity) -> spawnBullet(pos, velocity, sliceBoard)));
        }
    }

    private void spawnBullet(DoublePoint pos,
                             AbstractVector velocity,
                             AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, BLUE, ENEMY_OUTBOUND, 0)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}