package internalconfig.game.systems.gamesystems.spawnhandlers.danmakuspawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.SpawnComponent;
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

import java.util.Random;

import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.B5_PATTERN_4_1_SPAWNER;
import static internalconfig.game.components.spawns.DanmakuSpawns.B5_PATTERN_4_1_SPAWNER_PATTERN_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.*;

public class B5_Pattern_4_1_Spawner_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public B5_Pattern_4_1_Spawner_HandlerProvider(SpawnBuilder spawnBuilder,
                                                  AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(6,
                30,
                3,
                80,
                2.1,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(5,
                25,
                4,
                110,
                2.1,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(4,
                20,
                5,
                130,
                2.1,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(3,
                15,
                6,
                150,
                2.1,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    protected class Template extends AbstractPositionSpawnHandler {

        private final InstructionNode<?, ?>[] SPAWNER_PROGRAM = ProgramBuilder.linearLink(
                WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                REMOVE_ENTITY
        ).compile();

        private final int spawnerMod;
        private final int barrierMod;

        private final int spawns;
        private final double totalWidth;
        private final double speed;

        private Template(int spawnerMod,
                         int barrierMod,
                         int spawns,
                         double totalWidth,
                         double speed,
                         SpawnBuilder spawnBuilder) {
            super(B5_PATTERN_4_1_SPAWNER, spawnBuilder, componentTypeContainer);
            this.spawnerMod = spawnerMod;
            this.barrierMod = barrierMod;
            this.spawns = spawns;
            this.totalWidth = totalWidth;
            this.speed = speed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            boolean spawnSpawner = tickMod(tick, spawnerMod);
            boolean spawnBarrier = tickMod(tick, barrierMod);
            if (spawnSpawner || spawnBarrier) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

                Random random = GameUtil.getRandom(globalBoard);

                if (spawnSpawner) {
                    double x = RandomUtil.randDoubleInclusive(0, WIDTH, random);
                    double y = RandomUtil.randDoubleInclusive(TOP_OUT + 1, 0, random);
                    DoublePoint pos = new DoublePoint(x, y);

                    spawnSpawner(pos, sliceBoard);
                }
                if (spawnBarrier) {
                    double x = RandomUtil.randDoubleInclusive(0, WIDTH, random);
                    double y = RandomUtil.randDoubleInclusive(TOP_OUT + 1, 0, random);
                    DoublePoint pos = new DoublePoint(x, y);

                    SpawnUtil.blockFormation(pos, totalWidth, spawns, (p) -> spawnBarrier(p, new PolarVector(speed, -90), sliceBoard));
                }
            }
        }

        private void spawnSpawner(DoublePoint pos, AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makePosition(pos)
                            .setSpawnComponent(new SpawnComponent().addSpawnUnit(B5_PATTERN_4_1_SPAWNER_PATTERN_1))
                            .setProgram(SPAWNER_PROGRAM)
                            .packageAsMessage()
            );
        }

        private void spawnBarrier(DoublePoint pos,
                                  AbstractVector velocity,
                                  AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeBarrier(pos, velocity, NORMAL_OUTBOUND, 0)
                            .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                            .packageAsMessage()
            );
        }
    }
}
