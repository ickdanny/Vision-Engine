package internalconfig.game.systems.gamesystems.spawnhandlers.playerspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractShotTypeDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class ShotSpawnHandlerProvider extends AbstractShotTypeDependentSpawnHandlerProvider {

    public ShotSpawnHandlerProvider(SpawnBuilder spawnBuilder,
                                    AbstractComponentTypeContainer componentTypeContainer){
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getA() {
        return new ShotASpawnHandler(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getB() {
        return new ShotBSpawnHandler(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getC() {
        return new ShotCSpawnHandler(spawnBuilder, componentTypeContainer);
    }
}