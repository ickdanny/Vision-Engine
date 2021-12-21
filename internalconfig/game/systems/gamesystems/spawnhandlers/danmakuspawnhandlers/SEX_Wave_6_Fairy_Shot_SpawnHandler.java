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
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.components.spawns.DanmakuSpawns.SEX_WAVE_6_FAIRY_SHOT;
import static internalconfig.game.GameConfig.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class SEX_Wave_6_Fairy_Shot_SpawnHandler extends AbstractPositionSpawnHandler {

    private static final double SPEED = 3.5;

    private static final int SYMMETRY = 3;
    private static final double TOTAL_ANGLE = 75;

    public SEX_Wave_6_Fairy_Shot_SpawnHandler(SpawnBuilder spawnBuilder,
                                              AbstractComponentTypeContainer componentTypeContainer) {
        super(SEX_WAVE_6_FAIRY_SHOT, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tick == SEX_WAVE_6_FAIRY_SHOT.getDuration()) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

            spawnTripleShot(pos, new Angle(-90), sliceBoard);
            spawnTripleShot(pos, new Angle(0), sliceBoard);
            spawnTripleShot(pos, new Angle(180), sliceBoard);
        }
    }

    private void spawnTripleShot(DoublePoint pos, Angle angle, AbstractPublishSubscribeBoard sliceBoard) {
        AbstractVector baseVelocity = new PolarVector(SPEED, angle);
        DoublePoint basePos = new PolarVector(10, angle).add(pos);

        SpawnUtil.arcFormation(pos, basePos, baseVelocity, SYMMETRY, TOTAL_ANGLE, (p, v) -> spawnBullet(p, v, sliceBoard));
    }

    private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, RED, NORMAL_OUTBOUND, 10)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }
}
