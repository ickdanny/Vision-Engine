package internalconfig.game.systems.gamesystems.spawnhandlers.deathspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.COLLISION_BOUNDS;

public class DropLifeAndClearSpawnHandler extends AbstractEnemyDeathSpawnHandler {
    public DropLifeAndClearSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(DeathSpawns.DROP_LIFE_AND_CLEAR, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        super.handleSpawn(ecsInterface, tick, entityID);
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

        SpawnUtil.PickupSpawner.spawnLifePickup(sliceBoard, spawnBuilder, pos);

        sliceBoard.publishMessage(
                spawnBuilder.makeStationaryCollidable(new DoublePoint(0, 0), COLLISION_BOUNDS)
                        .markAsBulletBlocker()
                        .setProgram(ProgramRepository.LIFETIME_10.getProgram())
                        .packageAsMessage()
        );
    }
}
