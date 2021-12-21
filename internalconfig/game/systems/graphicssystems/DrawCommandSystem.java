package internalconfig.game.systems.graphicssystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.DrawOrder;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.components.VelocityComponent;
import util.math.geometry.TwoFramePosition;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;
import util.messaging.Topic;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ecs.ECSTopics.REMOVED_ENTITIES;
import static internalconfig.game.systems.Topics.DRAW_COMMANDS;

public class DrawCommandSystem implements AbstractSystem<Double> {
    protected final AbstractComponentType<BufferedImage> spriteComponentType;
    protected final AbstractComponentType<DrawOrder> drawOrderComponentType;
    protected final AbstractComponentType<TwoFramePosition> positionComponentType;
    protected final AbstractComponentType<VelocityComponent> velocityComponentType;

    protected final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
    protected final AbstractComponentType<Void> visibleMarker;

    public DrawCommandSystem(AbstractComponentTypeContainer componentTypeContainer){
        spriteComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.SpriteComponentType.class);
        drawOrderComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.DrawOrderComponentType.class);
        positionComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.PositionComponentType.class);
        velocityComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.VelocityComponentType.class);

        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.SpriteInstructionComponentType.class);
        visibleMarker = componentTypeContainer.getTypeInstance(ComponentTypes.VisibleMarker.class);
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    protected class Instance implements AbstractSystemInstance<Double> {
        private final Map<Integer, DrawCommand> drawCommandMap;

        protected Instance(){
            drawCommandMap = new HashMap<>();
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            removeIneligibleCommands(sliceBoard);
            updateCommands(dataStorage, sliceBoard);
            sortAndBroadcastCommands(sliceBoard, dataStorage.getMessageLifetime());
            sliceBoard.ageAndCullMessages();
        }

        private void removeIneligibleCommands(AbstractPublishSubscribeBoard sliceBoard){
            removeForTopic(sliceBoard, REMOVED_ENTITIES);
            removeForTopic(sliceBoard, spriteInstructionComponentType.getRemoveComponentTopic());
            removeForTopic(sliceBoard, spriteComponentType.getRemoveComponentTopic());
            removeForTopic(sliceBoard, positionComponentType.getRemoveComponentTopic());
            removeForTopic(sliceBoard, visibleMarker.getRemoveComponentTopic());
            removeForTopic(sliceBoard, drawOrderComponentType.getRemoveComponentTopic());
        }

        private void removeForTopic(AbstractPublishSubscribeBoard sliceBoard, Topic<EntityHandle> topic){
            for(Message<EntityHandle> message : sliceBoard.getMessageList(topic)){
                drawCommandMap.remove(message.getMessage().getEntityID());
            }
        }

        public void updateCommands(AbstractDataStorage dataStorage, AbstractPublishSubscribeBoard sliceBoard) {
            updateVisibles(dataStorage, sliceBoard);
            updateSprites(dataStorage, sliceBoard);
            updateDrawOrders(dataStorage, sliceBoard);
            updatePositions(dataStorage, sliceBoard);
            updateVelocities(dataStorage, sliceBoard);
        }

        private void updateVisibles(AbstractDataStorage dataStorage, AbstractPublishSubscribeBoard sliceBoard){
            for(Message<EntityHandle> message : sliceBoard.getMessageList(visibleMarker.getSetComponentTopic())){
                EntityHandle handle = message.getMessage();
                int id = handle.getEntityID();
                DrawCommand drawCommand = drawCommandMap.get(id);

                if(drawCommand == null && isEligible(dataStorage, handle)){
                    drawCommandMap.put(id, makeCommand(dataStorage, handle));
                }
            }
        }

        private void updateSprites(AbstractDataStorage dataStorage, AbstractPublishSubscribeBoard sliceBoard){
            for(Message<EntityHandle> message : sliceBoard.getMessageList(spriteComponentType.getSetComponentTopic())){
                EntityHandle handle = message.getMessage();
                int id = handle.getEntityID();
                DrawCommand drawCommand = drawCommandMap.get(id);

                if(drawCommand == null && isEligible(dataStorage, handle)){
                    drawCommandMap.put(id, makeCommand(dataStorage, handle));
                }
                else if(drawCommand != null) {
                    drawCommand.setImage(dataStorage.getComponent(handle, spriteComponentType));
                }
            }
        }
        private boolean isEligible(AbstractDataStorage dataStorage, EntityHandle handle){
            return dataStorage.isAlive(handle)
                    && dataStorage.containsAllComponents(handle,
                    spriteComponentType,
                    drawOrderComponentType,
                    positionComponentType,
                    spriteInstructionComponentType,
                    visibleMarker);
        }
        protected DrawCommand makeCommand(AbstractDataStorage dataStorage, EntityHandle handle){
            DrawOrder order = dataStorage.getComponent(handle, drawOrderComponentType);
            BufferedImage image = dataStorage.getComponent(handle, spriteComponentType);
            TwoFramePosition position = dataStorage.getComponent(handle, positionComponentType);

            return dataStorage.containsComponent(handle, velocityComponentType)
                    ? new DrawCommand(order, image, position, dataStorage.getComponent(handle, velocityComponentType))
                    : new DrawCommand(order, image, position);
        }

        private void updateDrawOrders(AbstractDataStorage dataStorage, AbstractPublishSubscribeBoard sliceBoard){
            for(Message<EntityHandle> message : sliceBoard.getMessageList(drawOrderComponentType.getSetComponentTopic())){
                EntityHandle handle = message.getMessage();
                DrawCommand drawCommand = drawCommandMap.get(handle.getEntityID());
                if(drawCommand != null){
                    drawCommand.setOrder(dataStorage.getComponent(handle, drawOrderComponentType));
                }
            }
        }

        private void updatePositions(AbstractDataStorage dataStorage, AbstractPublishSubscribeBoard sliceBoard){
            for(Message<EntityHandle> message : sliceBoard.getMessageList(positionComponentType.getSetComponentTopic())){
                EntityHandle handle = message.getMessage();
                DrawCommand drawCommand = drawCommandMap.get(handle.getEntityID());
                if(drawCommand != null){
                    drawCommand.setPosition(dataStorage.getComponent(handle, positionComponentType));
                }
            }
        }

        private void updateVelocities(AbstractDataStorage dataStorage, AbstractPublishSubscribeBoard sliceBoard){
            for(Message<EntityHandle> message : sliceBoard.getMessageList(velocityComponentType.getRemoveComponentTopic())){
                EntityHandle handle = message.getMessage();
                DrawCommand drawCommand = drawCommandMap.get(handle.getEntityID());
                if(drawCommand != null){
                    drawCommand.setVelocity(null);
                }
            }

            for(Message<EntityHandle> message : sliceBoard.getMessageList(velocityComponentType.getSetComponentTopic())){
                EntityHandle handle = message.getMessage();
                DrawCommand drawCommand = drawCommandMap.get(handle.getEntityID());
                if(drawCommand != null){
                    drawCommand.setVelocity(dataStorage.getComponent(handle, velocityComponentType));
                }
            }
        }

        private void sortAndBroadcastCommands(AbstractPublishSubscribeBoard sliceBoard, int messageLifetime){
            List<DrawCommand> list = new ArrayList<>(drawCommandMap.values());
            Collections.sort(list);
            DrawCommand[] sortedCommands = list.toArray(new DrawCommand[0]);

            for(DrawCommand drawCommand : sortedCommands) {
                sliceBoard.publishMessage(new Message<>(DRAW_COMMANDS, drawCommand, messageLifetime));
            }
        }
    }
}