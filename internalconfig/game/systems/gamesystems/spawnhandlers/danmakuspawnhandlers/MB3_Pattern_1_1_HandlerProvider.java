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
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;
import util.tuple.Tuple3;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.DanmakuSpawns.MB3_PATTERN_1_1;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static util.math.Constants.PHI;

public class MB3_Pattern_1_1_HandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    public MB3_Pattern_1_1_HandlerProvider(SpawnBuilder spawnBuilder,
                                           AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new Template(
                7,
                PHI * 7,
                11,
                270,
                2,
                3.5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new Template(
                7,
                PHI * 7,
                14,
                270,
                2,
                3.5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new Template(
                7,
                PHI * 7,
                17,
                270,
                2,
                3.5,
                spawnBuilder
        );
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new Template(
                7,
                PHI * 7,
                20,
                270,
                2,
                3.5,
                spawnBuilder
        );
    }

    @SuppressWarnings("SameParameterValue")
    private class Template extends AbstractPositionSpawnHandler {

        private static final int SLOW_DURATION = 50;
        private static final int WAIT_TIMER = 5;
        private static final int SPEED_DURATION = 30;

        private static final double SLOW_SPEED = .5;

        private final int mod;
        private final double angularVelocity;
        private final int symmetry;
        private final double totalAngle;
        private final double initSpeed;
        private final double finalSpeed;

        private Template(int mod,
                         double angularVelocity,
                         int symmetry,
                         double totalAngle,
                         double initSpeed,
                         double finalSpeed,
                         SpawnBuilder spawnBuilder) {
            super(MB3_PATTERN_1_1, spawnBuilder, componentTypeContainer);
            this.mod = mod;
            this.angularVelocity = angularVelocity;
            this.symmetry = symmetry;
            this.totalAngle = totalAngle;
            this.initSpeed = initSpeed;
            this.finalSpeed = finalSpeed;
        }

        @Override
        public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
            if (tickMod(tick, mod)) {
                AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                AbstractDataStorage dataStorage = ecsInterface.getSliceData();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                EnemyProjectileColors color = tick % 2 == 0 ? ORANGE : ROSE;

                double baseAngle = GameUtil.getPseudoRandomBasedOnEntity(globalBoard, dataStorage, entityID).nextDouble() * 360;
                SpawnUtil.spiralFormation(tick, MB3_PATTERN_1_1.getDuration(), baseAngle, angularVelocity,
                        (spiralAngle) -> {
                            AbstractVector baseVelocity = new PolarVector(initSpeed, spiralAngle);
                            DoublePoint basePos = new PolarVector(10, spiralAngle).add(pos);
                            SpawnUtil.arcFormation(pos, basePos, baseVelocity, symmetry, totalAngle, (p, initVelocity) -> {
                                Angle finalAngle = spiralAngle.add(spiralAngle.getAngle() - initVelocity.getAngle().getAngle());
                                spawnBullet(
                                        p,
                                        initVelocity,
                                        new PolarVector(finalSpeed, finalAngle),
                                        color,
                                        sliceBoard
                                );
                            });
                        });
            }
        }

        private void spawnBullet(DoublePoint pos,
                                 AbstractVector initVelocity,
                                 AbstractVector finalVelocity,
                                 EnemyProjectileColors color,
                                 AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightSlowableEnemyBullet(pos, initVelocity, SHARP, color, -100, 20)
                            .setProgram(makeProgram(initVelocity.getAngle(), finalVelocity))
                            .packageAsMessage()
            );
        }

        private InstructionNode<?, ?>[] makeProgram(Angle initAngle, AbstractVector finalVelocity) {
            return ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE
            ).linkInject(
                    ProgramBuilder.linearLink(
                            new InstructionNode<>(SLOW_DOWN_TO_VELOCITY, new Tuple2<>(new PolarVector(SLOW_SPEED, initAngle), SLOW_DURATION)),
                            new InstructionNode<>(TIMER, WAIT_TIMER),
                            new InstructionNode<>(SPEED_UP_AND_TURN_TO_VELOCITY, new Tuple3<>(finalVelocity, initAngle, SPEED_DURATION))
                    )
            ).compile();
        }
    }
}