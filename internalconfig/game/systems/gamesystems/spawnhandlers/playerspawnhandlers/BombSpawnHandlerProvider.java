package internalconfig.game.systems.gamesystems.spawnhandlers.playerspawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractShotTypeDependentSpawnHandlerProvider;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;

public class BombSpawnHandlerProvider extends AbstractShotTypeDependentSpawnHandlerProvider {

    public BombSpawnHandlerProvider(SpawnBuilder spawnBuilder,
                                    AbstractComponentTypeContainer componentTypeContainer){
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getA() {
        return new BombASpawnHandler(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getB() {
        return new BombBSpawnHandler(spawnBuilder, componentTypeContainer);
    }

    @Override
    protected AbstractSpawnHandler getC() {
        return new BombCSpawnHandler(spawnBuilder, componentTypeContainer);
    }
}