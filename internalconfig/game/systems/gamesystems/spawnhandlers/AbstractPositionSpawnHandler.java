package internalconfig.game.systems.gamesystems.spawnhandlers;

import ecs.component.AbstractComponentType;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.spawns.Spawns;
import util.math.geometry.TwoFramePosition;

import static internalconfig.game.components.ComponentTypes.*;

public abstract class AbstractPositionSpawnHandler extends AbstractSpawnHandlerTemplate {

    protected final AbstractComponentType<TwoFramePosition> positionComponentType;

    public AbstractPositionSpawnHandler(Spawns spawn,
                                        SpawnBuilder spawnBuilder,
                                        AbstractComponentTypeContainer componentTypeContainer){
        super(spawn, spawnBuilder);
        positionComponentType = componentTypeContainer.getTypeInstance(PositionComponentType.class);
    }
}