package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractStageDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class StageSpawnHandlerProvider extends AbstractStageDependentSpawnHandlerProvider {

    public StageSpawnHandlerProvider(SpawnBuilder spawnBuilder,
                                     AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler get1() {
        return new Stage1SpawnHandler(spawnBuilder);
    }

    @Override
    protected AbstractSpawnHandler get2() {
        return new Stage2SpawnHandler(spawnBuilder);
    }

    @Override
    protected AbstractSpawnHandler get3() {
        return new Stage3SpawnHandler(spawnBuilder);
    }

    @Override
    protected AbstractSpawnHandler get4() {
        return new Stage4SpawnHandler(spawnBuilder);
    }

    @Override
    protected AbstractSpawnHandler get5() {
        return new Stage5SpawnHandler(spawnBuilder);
    }

    @Override
    protected AbstractSpawnHandler get6() {
        return new Stage6SpawnHandler(spawnBuilder);
    }

    @Override
    protected AbstractSpawnHandler getEX() {
        return new StageEXSpawnHandler(spawnBuilder);
    }
}
