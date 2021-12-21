package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.component.componentstorage.AbstractGroup;
import ecs.component.componentstorage.ComponentIterator;
import ecs.datastorage.AbstractDataStorage;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.VelocityComponent;
import util.math.geometry.TwoFramePosition;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

public class MovePositionSystem implements AbstractSystem<Double> {

    private final AbstractComponentType<TwoFramePosition> positionComponentType;
    private final AbstractComponentType<VelocityComponent> velocityComponentType;

    public MovePositionSystem(AbstractComponentTypeContainer componentTypeContainer) {
        positionComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.PositionComponentType.class);
        velocityComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.VelocityComponentType.class);
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

            movePositions();

            sliceBoard.ageAndCullMessages();
        }

        private void movePositions(){
            ComponentIterator<TwoFramePosition> posItr = group.getComponentIterator(positionComponentType);
            ComponentIterator<VelocityComponent> velItr = group.getComponentIterator(velocityComponentType);

            while(posItr.hasNext() && velItr.hasNext()){
                TwoFramePosition twoFramePos = posItr.next();
                VelocityComponent vel = velItr.next();
                twoFramePos.step();
                DoublePoint pos = twoFramePos.getPos();
                pos.setAs(vel.add(pos));
            }
            if(posItr.hasNext() || velItr.hasNext()){
                throw new RuntimeException("do not expect posItr or velItr to have any remaining");
            }
        }

        private void getGroup(AbstractDataStorage dataStorage){
            group = dataStorage.createGroup(positionComponentType, velocityComponentType);
        }
    }
}
