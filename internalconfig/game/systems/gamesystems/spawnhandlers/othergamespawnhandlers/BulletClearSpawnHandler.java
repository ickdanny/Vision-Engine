package internalconfig.game.systems.gamesystems.spawnhandlers.othergamespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.spawns.OtherGameSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.COLLISION_BOUNDS;

public class BulletClearSpawnHandler extends AbstractSpawnHandlerTemplate {

    public BulletClearSpawnHandler(SpawnBuilder spawnBuilder) {
        super(OtherGameSpawns.BULLET_CLEAR, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        sliceBoard.publishMessage(
                spawnBuilder.makeStationaryCollidable(new DoublePoint(0, 0), COLLISION_BOUNDS)
                        .markAsBulletBlocker()
                        .setProgram(ProgramRepository.LIFETIME_2.getProgram())
                        .packageAsMessage()
        );
    }
}