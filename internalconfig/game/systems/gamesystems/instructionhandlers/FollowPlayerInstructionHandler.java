package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.criticalorders.SetComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import internalconfig.game.components.VelocityComponent;
import internalconfig.game.systems.gamesystems.GameUtil;
import util.math.geometry.DoublePoint;
import util.math.geometry.TwoFramePosition;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.components.Instructions.FOLLOW_PLAYER;

class FollowPlayerInstructionHandler implements AbstractInstructionHandler<Void, Void> {

    private final AbstractComponentType<TwoFramePosition> positionComponentType;
    private final AbstractComponentType<VelocityComponent> velocityComponentType;

    FollowPlayerInstructionHandler(AbstractComponentTypeContainer componentTypeContainer){
        positionComponentType = componentTypeContainer.getTypeInstance(PositionComponentType.class);
        velocityComponentType = componentTypeContainer.getTypeInstance(VelocityComponentType.class);
    }

    @Override
    public Instructions<Void, Void> getInstruction() {
        return FOLLOW_PLAYER;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Void, Void> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        EntityHandle player = GameUtil.getPlayer(sliceBoard);
        EntityHandle handle = dataStorage.makeHandle(entityID);

        if (dataStorage.containsAllComponents(player, positionComponentType, velocityComponentType)
                && dataStorage.containsComponent(handle, positionComponentType)) {
            followPlayer(sliceBoard, dataStorage, player, handle);
            return false;
        }
        else{ //some component is missing - continue to next instruction
            return true;
        }
    }

    private void followPlayer(AbstractPublishSubscribeBoard sliceBoard,
                              AbstractDataStorage dataStorage,
                              EntityHandle player,
                              EntityHandle handle){

        DoublePoint currentPos = GameUtil.getPos(dataStorage, handle, positionComponentType);
        DoublePoint playerCurrentPos = GameUtil.getPos(dataStorage, player, positionComponentType);

        if(!currentPos.equals(playerCurrentPos)){
            resetPosition(sliceBoard, playerCurrentPos, handle);
        }

        VelocityComponent currentVelocityComponent = dataStorage.getComponent(handle, velocityComponentType);
        VelocityComponent playerVelocityComponent = dataStorage.getComponent(player, velocityComponentType);

        if(currentVelocityComponent != playerVelocityComponent){ //use the literal component
            setVelocity(sliceBoard, playerVelocityComponent, handle);
        }
    }

    private void resetPosition(AbstractPublishSubscribeBoard sliceBoard,
                               DoublePoint playerCurrentPos,
                               EntityHandle handle){
        TwoFramePosition newPositionComponent = new TwoFramePosition(playerCurrentPos);
        SetComponentOrder<TwoFramePosition> resetPositionOrder = new SetComponentOrder<>(
                handle, positionComponentType, newPositionComponent
        );
        sliceBoard.publishMessage(ECSUtil.makeSetComponentMessage(resetPositionOrder));
    }

    private void setVelocity(AbstractPublishSubscribeBoard sliceBoard,
                             VelocityComponent playerVelocityComponent,
                             EntityHandle handle){
        SetComponentOrder<VelocityComponent> setVelocityOrder = new SetComponentOrder<>(
                handle, velocityComponentType, playerVelocityComponent
        );
        sliceBoard.publishMessage(ECSUtil.makeSetComponentMessage(setVelocityOrder));
    }
}
