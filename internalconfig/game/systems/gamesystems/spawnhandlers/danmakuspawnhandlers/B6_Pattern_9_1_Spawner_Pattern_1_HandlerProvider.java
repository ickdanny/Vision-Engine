package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B6_PATTERN_9_1_SPAWNER_PATTERN_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;

public class B6_Pattern_9_1_Spawner_Pattern_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B6_Pattern_9_1_Spawner_Pattern_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                                            AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                50,
                30,
                1.9,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                40,
                30,
                2,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                30,
                30,
                2.1,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                25,
                30,
                2.2,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private static final double HEIGHT_LIMIT = 40;

        private static final double SPAWN_OUT = 8;

        private static final int WAIT_TIME = 60;
        private static final int SPEED_TIME = 60;

        private final int mod;
        private final double angleBound;
        private final double speed;

        private Template(int mod,
                         double angleBound,
                         double speed,
                         SpawnBuilder spawnBuilder) {
            super(B6_PATTERN_9_1_SPAWNER_PATTERN_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.angleBound = angleBound;
            this.speed = speed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                Random random = GameUtil.getRandom(globalBoard);

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                if(pos.getY() < HEIGHT - HEIGHT_LIMIT) {

                    double angleOffset = RandomUtil.randDoubleInclusive(-angleBound, angleBound, random);

                    double baseAngle = 45 + angleOffset;
                    AbstractVector baseVelocity = new PolarVector(speed, baseAngle);
                    DoublePoint basePos = new PolarVector(SPAWN_OUT, baseAngle).add(pos);

                    SpawnUtil.ringFormation(pos, basePos, baseVelocity, 4, (p, v) -> spawnBullet(p, v, sliceBoard));
                }
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector velocity,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(
                            pos,
                            new PolarVector(0, velocity.getAngle()),
                            SHARP,
                            YELLOW,
                            NORMAL_OUTBOUND,
                            10
                    )
                            .setProgram(makeProgram(velocity))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeProgram(AbstractVector velocity) {
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE,
                    new InstructionNode<>(TIMER, WAIT_TIME),
                    new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(velocity, SPEED_TIME))
            ).compile();
        }
    }
}