package internalconfig.game.systems.gamesystems.spawnhandlers.deathspawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.SpawnComponent;
import internalconfig.game.components.spawns.DanmakuSpawns;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.DoublePoint;

public class MB2_Death_Spiral_Spawner_SpawnHandler extends AbstractEnemyDeathSpawnHandler {

    public MB2_Death_Spiral_Spawner_SpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(DeathSpawns.MB2_DEATH_SPIRAL_SPAWNER, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        super.handleSpawn(ecsInterface, tick, entityID);
        DoublePoint pos = GameUtil.getPos(ecsInterface.getSliceData(), entityID, positionComponentType);
        SpawnComponent spawnComponent = new SpawnComponent();
        spawnComponent.addSpawnUnit(DanmakuSpawns.MB2_DEATH_SPIRAL);
        ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makePosition(pos)
                        .setSpawnComponent(spawnComponent)
                        .setProgram(ProgramRepository.REMOVE_AFTER_EMPTY_SPAWN.getProgram())
                        .packageAsMessage()
        );
    }
}