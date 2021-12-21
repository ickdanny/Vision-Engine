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
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.messaging.AbstractPublishSubscribeBoard;

public class InboundSystem implements AbstractSystem<Double> {

    protected final AbstractComponentType<TwoFramePosition> positionComponentType;
    protected final AbstractComponentType<VelocityComponent> velocityComponentType;
    private final AbstractComponentType<Double> inboundComponentType;

    public InboundSystem(AbstractComponentTypeContainer componentTypeContainer) {
        positionComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.PositionComponentType.class);
        velocityComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.VelocityComponentType.class);
        inboundComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.InboundComponentType.class);
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

            inboundVelocities();

            sliceBoard.ageAndCullMessages();
        }

        private void inboundVelocities(){
            ComponentIterator<TwoFramePosition> posItr = group.getComponentIterator(positionComponentType);
            ComponentIterator<VelocityComponent> velItr = group.getComponentIterator(velocityComponentType);
            ComponentIterator<Double> boundItr = group.getComponentIterator(inboundComponentType);

            while(posItr.hasNext() && velItr.hasNext() && boundItr.hasNext()){
                TwoFramePosition twoFramePos = posItr.next();
                VelocityComponent vel = velItr.next();
                DoublePoint pos = twoFramePos.getPos();
                double bound = boundItr.next();

                DoublePoint nextPos = vel.add(pos);
                if(GameUtil.isOutOfBounds(nextPos, bound)) {
                    DoublePoint inboundPos = GameUtil.inboundPosition(nextPos, bound);
                    AbstractVector inboundVel = GeometryUtil.vectorFromAToB(pos, inboundPos);
                    vel.setTrueVelocity(inboundVel);
                }
            }
            if(posItr.hasNext() || velItr.hasNext() || boundItr.hasNext()){
                throw new RuntimeException("do not expect posItr or velItr or boundItr to have any remaining");
            }
        }

        private void getGroup(AbstractDataStorage dataStorage){
            group = dataStorage.createGroup(positionComponentType, velocityComponentType, inboundComponentType);
        }
    }
}
