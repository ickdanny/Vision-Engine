package internalconfig.game.systems.gamesystems.spawnhandlers.playerspawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.components.spawns.PlayerSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.*;

public class PlayerBulletBlockerSpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int LIFETIME = 105;

    public PlayerBulletBlockerSpawnHandler(SpawnBuilder spawnBuilder) {
        super(PlayerSpawns.PLAYER_BULLET_BLOCKER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        sliceBoard.publishMessage(
                spawnBuilder.makeStationaryCollidable(new DoublePoint(0, 0), COLLISION_BOUNDS)
                            .markAsBulletBlocker()
                            .setProgram(ProgramUtil.makeLifetimeProgram(LIFETIME).compile())
                            .packageAsMessage()
        );
    }
}