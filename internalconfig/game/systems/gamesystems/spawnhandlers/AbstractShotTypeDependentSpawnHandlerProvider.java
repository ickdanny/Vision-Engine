package internalconfig.game.systems.gamesystems.spawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.ShotType;

public abstract class AbstractShotTypeDependentSpawnHandlerProvider extends AbstractSpawnHandlerProvider{

    public AbstractShotTypeDependentSpawnHandlerProvider(SpawnBuilder spawnBuilder,
                                                         AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    public final AbstractSpawnHandler getSpawnHandlerForShotType(ShotType shotType){
        switch(shotType){
            case A:
                return getA();
            case B:
                return getB();
            case C:
                return getC();
            default:
                throw new IllegalStateException("Unexpected value: " + shotType);
        }
    }

    protected abstract AbstractSpawnHandler getA();
    protected abstract AbstractSpawnHandler getB();
    protected abstract AbstractSpawnHandler getC();
}
