package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractStageDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.StageSpawns.STAGE_ENDER;

public class StageEnderHandlerProvider extends AbstractStageDependentSpawnHandlerProvider {

    public StageEnderHandlerProvider(SpawnBuilder spawnBuilder,
                                     AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler get1() {
        return makeStageEndSpawnHandler(spawnBuilder, makeMiddleStageProgram("1_post"));
    }

    @Override
    protected AbstractSpawnHandler get2() {
        return makeStageEndSpawnHandler(spawnBuilder, makeMiddleStageProgram("2_post"));
    }

    @Override
    protected AbstractSpawnHandler get3() {
        return makeStageEndSpawnHandler(spawnBuilder, makeMiddleStageProgram("3_post"));
    }

    @Override
    protected AbstractSpawnHandler get4() {
        return makeStageEndSpawnHandler(spawnBuilder, makeMiddleStageProgram("4_post"));
    }

    @Override
    protected AbstractSpawnHandler get5() {
        return makeStageEndSpawnHandler(spawnBuilder, makeMiddleStageProgram("5_post"));
    }

    @Override
    protected AbstractSpawnHandler get6() {
        return makeStageEndSpawnHandler(spawnBuilder, S6_PROGRAM);
    }

    @Override
    protected AbstractSpawnHandler getEX() {
        return makeStageEndSpawnHandler(spawnBuilder, EXTRA_STAGE_PROGRAM);
    }

    private InstructionNode<?, ?>[] makeMiddleStageProgram(String dialogue){
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, 60),
                new InstructionNode<>(SHOW_DIALOGUE, dialogue),
                WAIT_UNTIL_DIALOGUE_OVER,
                NEXT_STAGE
        ).compile();
    }

    private final InstructionNode<?, ?>[] EXTRA_STAGE_PROGRAM = ProgramBuilder.linearLink(
            new InstructionNode<>(TIMER, 60),
            new InstructionNode<>(SHOW_DIALOGUE, "ex_post"),
            WAIT_UNTIL_DIALOGUE_OVER,
            GAME_OVER
    ).compile();

    private final InstructionNode<?, ?>[] S6_PROGRAM = ProgramBuilder.linearLink(
            new InstructionNode<>(TIMER, 120),
            GAME_WIN
    ).compile();

    private AbstractSpawnHandler makeStageEndSpawnHandler(SpawnBuilder spawnBuilder, InstructionNode<?, ?>[] program) {
        return new AbstractSpawnHandler() {
            @Override
            public Spawns getSpawn() {
                return STAGE_ENDER;
            }

            @Override
            public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
                AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                sliceBoard.publishMessage(
                        spawnBuilder.makeEntity()
                                .setProgram(program)
                                .packageAsMessage()
                );
            }
        };
    }
}
