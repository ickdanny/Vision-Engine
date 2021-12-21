package internalconfig.game.systems.gamesystems.spawnhandlers;

import ecs.component.AbstractComponentType;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.VelocityComponent;
import internalconfig.game.components.spawns.Spawns;

import static internalconfig.game.components.ComponentTypes.*;

public abstract class AbstractPositionVelocitySpawnHandler extends AbstractPositionSpawnHandler {

    protected final AbstractComponentType<VelocityComponent> velocityComponentType;

    public AbstractPositionVelocitySpawnHandler(Spawns spawn,
                                                SpawnBuilder spawnBuilder,
                                                AbstractComponentTypeContainer componentTypeContainer) {
        super(spawn, spawnBuilder, componentTypeContainer);
        velocityComponentType = componentTypeContainer.getTypeInstance(VelocityComponentType.class);
    }
}
