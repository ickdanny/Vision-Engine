package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.component.componentstorage.AbstractGroup;
import ecs.component.componentstorage.ComponentIterator;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import ecs.system.criticalorders.RemoveEntityOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import util.math.geometry.DoublePoint;
import util.math.geometry.TwoFramePosition;
import util.messaging.AbstractPublishSubscribeBoard;

public class OutboundSystem implements AbstractSystem<Double> {
    protected final AbstractComponentType<TwoFramePosition> positionComponentType;
    private final AbstractComponentType<Double> outboundComponentType;

    public OutboundSystem(AbstractComponentTypeContainer componentTypeContainer) {
        positionComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.PositionComponentType.class);
        outboundComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.OutboundComponentType.class);
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double>{
        private AbstractGroup group;

        private Instance(){
            group = null;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            if(group == null){
                getGroup(dataStorage);
            }

            removeOutboundEntities(dataStorage, sliceBoard);

            sliceBoard.ageAndCullMessages();
        }

        private void removeOutboundEntities(AbstractDataStorage dataStorage, AbstractPublishSubscribeBoard sliceBoard){
            ComponentIterator<TwoFramePosition> posItr = group.getComponentIterator(positionComponentType);
            ComponentIterator<Double> boundItr = group.getComponentIterator(outboundComponentType);

            while(posItr.hasNext() && boundItr.hasNext()){
                TwoFramePosition twoFramePos = posItr.next();
                DoublePoint pos = twoFramePos.getPos();
                double bound = boundItr.next();

                if(GameUtil.isOutOfBounds(pos, bound)) {
                    EntityHandle handle = dataStorage.makeHandle(posItr.entityIDOfPreviousComponent());
                    RemoveEntityOrder order = new RemoveEntityOrder(handle);
                    sliceBoard.publishMessage(ECSUtil.makeRemoveEntityMessage(order));
                }
            }
            if(posItr.hasNext() || boundItr.hasNext()){
                throw new RuntimeException("do not expect posItr or velItr or boundItr to have any remaining");
            }
        }

        private void getGroup(AbstractDataStorage dataStorage){
            group = dataStorage.createGroup(positionComponentType, outboundComponentType);
        }
    }
}
