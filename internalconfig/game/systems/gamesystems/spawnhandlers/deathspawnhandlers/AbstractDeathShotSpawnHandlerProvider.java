package internalconfig.game.systems.gamesystems.spawnhandlers.deathspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractDifficultyDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.HEIGHT;

import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

@SuppressWarnings("unused")
abstract class AbstractDeathShotSpawnHandlerProvider extends AbstractDifficultyDependentSpawnHandlerProvider {

    private static final double HIGHEST_HEIGHT = HEIGHT/2d;

    private static final double SLOW_SPEED = 2.5;
    private static final double FAST_SPEED = 3.5;
    private static final double SIDE_SPEED = 2;
    private static final double SIDE_ANGLE = 15;

    private final Spawns spawn;

    AbstractDeathShotSpawnHandlerProvider(Spawns spawn,
                                                 SpawnBuilder spawnBuilder,
                                                 AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
        this.spawn = spawn;
    }

    @Override
    protected AbstractSpawnHandler getEasy() {
        return new AbstractEnemyDeathSpawnHandler(spawn, spawnBuilder, componentTypeContainer) {
            @Override
            public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
                super.handleSpawn(ecsInterface, tick, entityID);
                DoublePoint pos = GameUtil.getPos(ecsInterface.getSliceData(), entityID, positionComponentType);
                furtherSpawn(ecsInterface, tick, entityID, pos);
            }
        };
    }

    @Override
    protected AbstractSpawnHandler getMedium() {
        return new AbstractEnemyDeathSpawnHandler(spawn, spawnBuilder, componentTypeContainer) {
            @Override
            public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
                super.handleSpawn(ecsInterface, tick, entityID);

                AbstractDataStorage dataStorage = ecsInterface.getSliceData();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

                if(pos.getY() < HIGHEST_HEIGHT) {
                    double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
                    spawnBullet(pos, new PolarVector(SLOW_SPEED, angleToPlayer), sliceBoard);
                }

                furtherSpawn(ecsInterface, tick, entityID, pos);
            }
        };
    }

    @Override
    protected AbstractSpawnHandler getHard() {
        return new AbstractEnemyDeathSpawnHandler(spawn, spawnBuilder, componentTypeContainer) {
            @Override
            public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
                super.handleSpawn(ecsInterface, tick, entityID);

                AbstractDataStorage dataStorage = ecsInterface.getSliceData();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                if(pos.getY() < HIGHEST_HEIGHT) {
                    double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
                    spawnBullet(pos, new PolarVector(SLOW_SPEED, angleToPlayer), sliceBoard);
                    spawnBullet(pos, new PolarVector(FAST_SPEED, angleToPlayer), sliceBoard);
                }

                furtherSpawn(ecsInterface, tick, entityID, pos);
            }
        };
    }

    @Override
    protected AbstractSpawnHandler getLunatic() {
        return new AbstractEnemyDeathSpawnHandler(spawn, spawnBuilder, componentTypeContainer) {
            @Override
            public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
                super.handleSpawn(ecsInterface, tick, entityID);

                AbstractDataStorage dataStorage = ecsInterface.getSliceData();
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

                DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                if(pos.getY() < HIGHEST_HEIGHT) {
                    double angleToPlayer = GameUtil.getAngleToPlayer(dataStorage, sliceBoard, positionComponentType, pos);
                    spawnBullet(pos, new PolarVector(SLOW_SPEED, angleToPlayer), sliceBoard);
                    spawnBullet(pos, new PolarVector(FAST_SPEED, angleToPlayer), sliceBoard);
                    spawnBullet(pos, new PolarVector(SIDE_SPEED, angleToPlayer + SIDE_ANGLE), sliceBoard);
                    spawnBullet(pos, new PolarVector(SIDE_SPEED, angleToPlayer - SIDE_ANGLE), sliceBoard);
                }

                furtherSpawn(ecsInterface, tick, entityID, pos);
            }
        };
    }

    private void spawnBullet(DoublePoint pos, AbstractVector velocity, AbstractPublishSubscribeBoard sliceBoard) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightSlowableEnemyBullet(pos, velocity, SMALL, BLUE, -50, 3)
                        .setProgram(ProgramRepository.MARK_ENEMY_BULLET_COLLIDABLE_TIMER.getProgram())
                        .packageAsMessage()
        );
    }

    protected abstract void furtherSpawn(AbstractECSInterface ecsInterface, int tick, int entityID, DoublePoint pos);
}
