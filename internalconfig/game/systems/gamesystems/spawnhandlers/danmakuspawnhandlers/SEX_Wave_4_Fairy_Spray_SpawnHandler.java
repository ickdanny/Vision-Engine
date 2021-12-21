package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.components.spawns.DanmakuSpawns.SEX_WAVE_4_FAIRY_SPRAY;
import static internalconfig.game.GameConfig.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class SEX_Wave_4_Fairy_Spray_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final double SPEED = 6;

    private static final int MOD = 2;

    private static final double ANGLE_INCREMENT = 10;

    private static final int MAX_SYMMETRY = SEX_WAVE_4_FAIRY_SPRAY.getDuration()/MOD;

    public SEX_Wave_4_Fairy_Spray_SpawnHandler(SpawnBuilder spawnBuilder,
                                              AbstractComponentTypeContainer componentTypeContainer) {
        super(SEX_WAVE_4_FAIRY_SPRAY, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if(tickMod(tick, MOD)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

            AbstractVector baseVelocity = new PolarVector(SPEED, angleToPlayer);

            DoublePoint basePos = new PolarVector(10, angleToPlayer).add(pos);

            int symmetry = MAX_SYMMETRY - (tick/MOD) + 1;

            SpawnUtil.arcFormationIncrement(pos, basePos, baseVelocity, symmetry, ANGLE_INCREMENT, (p, v) -> spawnBullet(p, v, sliceBoard));
        }
    }

    private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, MAGENTA, NORMAL_OUTBOUND, 10)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}
