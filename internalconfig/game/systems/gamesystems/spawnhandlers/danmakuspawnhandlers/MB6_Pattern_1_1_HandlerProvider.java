package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.components.spawns.DanmakuSpawns.MB6_PATTERN_1_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class MB6_Pattern_1_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public MB6_Pattern_1_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                           AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                20,
                20,
                3.27135,
                30,
                30,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                17,
                25,
                3.27135,
                30,
                30,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                14,
                30,
                3.27135,
                30,
                30,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                10,
                35,
                3.27135,
                40,
                40,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private static final double SINE_TICK_MULTI = 1/121d;

        private final int mod;
        private final int symmetry;
        private final double speed;
        private final int waitTime;
        private final int turnTime;

        private Template(int mod,
                         int symmetry,
                         double speed,
                         int waitTime,
                         int turnTime,
                         SpawnBuilder spawnBuilder) {
            super(MB6_PATTERN_1_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.symmetry = symmetry;
            this.speed = speed;
            this.waitTime = waitTime;
            this.turnTime = turnTime;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random pseudoRandom = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID);

                double baseAngle = -90;
                int sinePhaseShift = pseudoRandom.nextInt();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                DoublePoint basePos = new PolarVector(10, baseAngle).add(pos);

                SpawnUtil.sineFormation(tick + sinePhaseShift, 0, SINE_TICK_MULTI, 179, (finalAngleOffset) -> SpawnUtil.ringFormation(pos, basePos, new PolarVector(speed, baseAngle), symmetry, (p, v) -> {
                    Angle finalAngle = v.getAngle().add(finalAngleOffset);
                    spawnBullet(p, v, finalAngle, sliceBoard);
                }));
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 Angle finalAngle,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SHARP, SPRING, -100, 5)
                            .setProgram(ProgramUtil.makeShortTurnBulletProgram(waitTime, finalAngle, turnTime).compile())
                            .packageAsMessage()
            );
        }
    }
}