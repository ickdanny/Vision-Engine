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

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S3_WAVE_4_WING_DOWN_WHIP_ARC;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S3_Wave_4_Wing_Down_Whip_Arc_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S3_Wave_4_Wing_Down_Whip_Arc_HandlerProvider(SpawnBuilder spawnBuilder,
                                                        AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                6,
                9,
                3,
                6.5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                6,
                12,
                3,
                7,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                5,
                14,
                3,
                7,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                4,
                15,
                3,
                7,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private final int mod;
        private final int symmetry;
        private final double speedLow;
        private final double speedHigh;

        private Template(int mod,
                         int symmetry,
                         double speedLow,
                         double speedHigh,
                         SpawnBuilder spawnBuilder) {
            super(S3_WAVE_4_WING_DOWN_WHIP_ARC, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetry = symmetry;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                SpawnUtil.whipFormation(tick, S3_WAVE_4_WING_DOWN_WHIP_ARC.getDuration(), speedLow, speedHigh, (speed) ->{
                    DoublePoint basePos = new DoublePoint(pos.getX(), pos.getY() + 10);
                    AbstractVector baseVelocity = new PolarVector(speed, -90);
                    SpawnUtil.arcFormation(pos, basePos, baseVelocity, symmetry, 180, (p, v) -> spawnBullet(p, v, sliceBoard));
                });
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, BLUE, NORMAL_OUTBOUND, -2)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}