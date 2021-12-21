package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.SpawnComponent;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.components.spawns.StageSpawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

import static internalconfig.game.components.spawns.StageSpawns.STAGE_SPAWNER;

abstract class AbstractStageSpawnHandler extends AbstractSpawnHandlerTemplate {

    public AbstractStageSpawnHandler(SpawnBuilder spawnBuilder) {
        super(STAGE_SPAWNER, spawnBuilder);
    }

    protected final int stageTick(int tick){
        return StageSpawns.STAGE_DURATION - tick;
    }

    protected void spawnTrackStarter(AbstractECSInterface ecsInterface, String trackID){
        ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeEntity()
                .setProgram(
                        new InstructionNode<>(Instructions.START_TRACK, trackID),
                        new InstructionNode<>(Instructions.REMOVE_ENTITY)
                )
                .packageAsMessage()
        );
    }

    protected void spawnSpawner(AbstractECSInterface ecsInterface, Spawns spawn){
        SpawnComponent spawnComponent = new SpawnComponent();
        spawnComponent.addSpawnUnit(spawn);
        ecsInterface.getSliceBoard().publishMessage(
                spawnBuilder.makeEntity()
                        .setSpawnComponent(spawnComponent)
                        .setProgram(ProgramRepository.REMOVE_AFTER_EMPTY_SPAWN.getProgram())
                        .packageAsMessage()
        );
    }
}