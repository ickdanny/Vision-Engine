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
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.S4_WAVE_9_APPARITION_BLOCKS;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class S4_Wave_9_Apparition_Blocks_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public S4_Wave_9_Apparition_Blocks_HandlerProvider(SpawnBuilder spawnBuilder,
                                                       AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                30,
                5,
                2,
                5.2,
                2,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                25,
                7,
                2,
                5.2,
                3,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                25,
                9,
                2,
                5.2,
                3,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                25,
                11,
                2,
                5.2,
                3,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private static final double SPAWN_WIDTH_MULTI = 25;

        private final int mod;
        private final int symmetry;
        private final double speedLow;
        private final double speedHigh;
        private final int spawns;

        private final double totalWidth;


        private Template(int mod,
                         int symmetry,
                         double speedLow,
                         double speedHigh,
                         int spawns,
                         SpawnBuilder spawnBuilder) {
            super(S4_WAVE_9_APPARITION_BLOCKS, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetry = symmetry;
            this.speedLow = speedLow;
            this.speedHigh = speedHigh;
            this.spawns = spawns;
            totalWidth = spawns * SPAWN_WIDTH_MULTI;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                double baseAngle = random.nextDouble() * 360d;

                DoublePoint basePos = new PolarVector(10, baseAngle).add(pos);

                double speed = RandomUtil.randDoubleInclusive(speedLow, speedHigh, random);

                SpawnUtil.ringFormation(pos, basePos, symmetry, (ringPos) -> {
                    double ringAngle = GeometryUtil.angleFromAToB(pos, ringPos).getAngle();
                    SpawnUtil.blockFormation(ringPos, new Angle(ringAngle + 90), totalWidth, spawns,
                            (p) -> spawnBullet(p, new PolarVector(speed, ringAngle), sliceBoard)
                    );
                });
            }
        }

        private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, MEDIUM, VIOLET, NORMAL_OUTBOUND, 0)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}
