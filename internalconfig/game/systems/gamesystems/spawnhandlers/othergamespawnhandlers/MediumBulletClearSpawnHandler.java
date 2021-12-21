package internalconfig.game.systems.gamesystems.spawnhandlers.othergamespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.components.spawns.OtherGameSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.GameConfig.COLLISION_BOUNDS;

public class MediumBulletClearSpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int LIFETIME = 5;
    private static final InstructionNode<?, ?>[] PROGRAM = ProgramUtil.makeLifetimeProgram(LIFETIME).compile();

    public MediumBulletClearSpawnHandler(SpawnBuilder spawnBuilder) {
        super(OtherGameSpawns.MEDIUM_BULLET_CLEAR, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        sliceBoard.publishMessage(
                spawnBuilder.makeStationaryCollidable(new DoublePoint(0, 0), COLLISION_BOUNDS)
                        .markAsBulletBlocker()
                        .setProgram(PROGRAM)
                        .packageAsMessage()
        );
    }
}
