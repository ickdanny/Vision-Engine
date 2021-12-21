package internalconfig.game.systems.gamesystems.spawnhandlers;

import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.Difficulty;

public abstract class AbstractDifficultyDependentSpawnHandlerProvider extends AbstractSpawnHandlerProvider{

    public AbstractDifficultyDependentSpawnHandlerProvider(SpawnBuilder spawnBuilder,
                                                           AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    public final AbstractSpawnHandler getSpawnHandlerForDifficulty(Difficulty difficulty){
        switch(difficulty){
            case EASY:
                return getEasy();
            case MEDIUM:
                return getMedium();
            case HARD:
                return getHard();
            case LUNATIC:
            case EXTRA:
                return getLunatic();
            default:
                throw new IllegalStateException("Unexpected value: " + difficulty);
        }
    }

    protected abstract AbstractSpawnHandler getEasy();
    protected abstract AbstractSpawnHandler getMedium();
    protected abstract AbstractSpawnHandler getHard();
    protected abstract AbstractSpawnHandler getLunatic();
}