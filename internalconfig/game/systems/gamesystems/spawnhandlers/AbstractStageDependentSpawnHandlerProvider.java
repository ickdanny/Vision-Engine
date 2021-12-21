package internalconfig.game.systems.gamesystems.spawnhandlers;


import internalconfig.game.components.AbstractComponentTypeContainer;

public abstract class AbstractStageDependentSpawnHandlerProvider extends AbstractSpawnHandlerProvider{

    public AbstractStageDependentSpawnHandlerProvider(SpawnBuilder spawnBuilder,
                                                      AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    public final AbstractSpawnHandler getSpawnHandlerForStage(int stage){
        switch(stage){
            case 1:
                return get1();
            case 2:
                return get2();
            case 3:
                return get3();
            case 4:
                return get4();
            case 5:
                return get5();
            case 6:
                return get6();
            default:
                return getEX();
        }
    }

    protected abstract AbstractSpawnHandler get1();
    protected abstract AbstractSpawnHandler get2();
    protected abstract AbstractSpawnHandler get3();
    protected abstract AbstractSpawnHandler get4();
    protected abstract AbstractSpawnHandler get5();
    protected abstract AbstractSpawnHandler get6();
    protected abstract AbstractSpawnHandler getEX();
}
