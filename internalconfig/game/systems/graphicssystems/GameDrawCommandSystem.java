package internalconfig.game.systems.graphicssystems;

import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.DrawOrder;
import util.math.geometry.TwoFramePosition;

import java.awt.image.BufferedImage;

import static internalconfig.game.GameConfig.*;

public class GameDrawCommandSystem extends DrawCommandSystem{

    private final AbstractComponentType<Void> gamePlaneMarker;

    public GameDrawCommandSystem(AbstractComponentTypeContainer componentTypeContainer) {
        super(componentTypeContainer);
        gamePlaneMarker = componentTypeContainer.getTypeInstance(ComponentTypes.GamePlaneMarker.class);
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance extends DrawCommandSystem.Instance{
        @Override
        protected DrawCommand makeCommand(AbstractDataStorage dataStorage, EntityHandle handle) {
            DrawOrder order = dataStorage.getComponent(handle, drawOrderComponentType);
            BufferedImage image = dataStorage.getComponent(handle, spriteComponentType);
            TwoFramePosition position = dataStorage.getComponent(handle, positionComponentType);

            if(dataStorage.containsComponent(handle, gamePlaneMarker)){
                return dataStorage.containsComponent(handle, velocityComponentType)
                        ? new DrawCommand(order, image, position, OFFSET, dataStorage.getComponent(handle, velocityComponentType))
                        : new DrawCommand(order, image, position, OFFSET);
            }
            else {
                return dataStorage.containsComponent(handle, velocityComponentType)
                        ? new DrawCommand(order, image, position, dataStorage.getComponent(handle, velocityComponentType))
                        : new DrawCommand(order, image, position);
            }
        }
    }
}
