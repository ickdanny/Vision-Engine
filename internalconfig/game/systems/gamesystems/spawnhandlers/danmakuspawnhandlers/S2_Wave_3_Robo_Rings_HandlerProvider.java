package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.NORMAL_OUTBOUND;
import static internalconfig.game.components.spawns.DanmakuSpawns.S2_WAVE_3_ROBO_RINGS;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public class S2_Wave_3_Robo_Rings_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    private static final int MOD = S2_WAVE_3_ROBO_RINGS.getDuration()/3;

    public S2_Wave_3_Robo_Rings_HandlerProvider(SpawnBuilder spawnBuilder,
                                                AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                5,
                3.9,
                2,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                5,
                4.1,
                3,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                6,
                4.3,
                4,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                7,
                4.5,
                5,
                spawnBuilder
        );
    }

    private class Template extends AbstractPositionSpawnHandler {

        private static final double ARC_ANGLE_INCREMENT = 3d;

        private final int symmetry;
        private final double speed;
        private final int arcSymmetry;

        private Template(int symmetry,
                         double speed,
                         int arcSymmetry,
                         SpawnBuilder spawnBuilder) {
            super(S2_WAVE_3_ROBO_RINGS, spawnBuilder, componentTypeContainer);
            this.symmetry = symmetry;
            this.speed = speed;
            this.arcSymmetry = arcSymmetry;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if(tickMod(tick, MOD)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                double angle = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);

                DoublePoint basePos = new PolarVector(10, angle).add(pos);
                if(!tickMod(tick, MOD * 2)){
                    SpawnUtil.ringFormation(pos, basePos, new PolarVector(speed, angle), symmetry * 5,
                            (p, v) -> spawnBullet(p, v, sliceBoard));
                }
                else{
                    SpawnUtil.ringFormation(pos, basePos, new PolarVector(speed, angle), symmetry,
                            (ringPos, ringVel) -> SpawnUtil.arcFormationIncrement(pos, ringPos, ringVel, arcSymmetry, ARC_ANGLE_INCREMENT,
                                    (p, v) -> spawnBullet(p, v, sliceBoard)));
                }
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, RED, NORMAL_OUTBOUND, -2)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }

    }
}