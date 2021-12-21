package internalconfig.game.systems.menusystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.component.componentstorage.AbstractGroup;
import ecs.component.componentstorage.ComponentIterator;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.LockConditions;
import internalconfig.game.systems.menusystems.lockconditionhandlers.LockConditionHandlers;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.components.ComponentTypes.*;

public class LockConditionSystem implements AbstractSystem<Double> {

    private final AbstractComponentType<LockConditions> lockConditionComponentType;

    private final LockConditionHandlers handlers;

    public LockConditionSystem(AbstractComponentTypeContainer componentTypeContainer){
        lockConditionComponentType = componentTypeContainer.getTypeInstance(LockConditionComponentType.class);
        handlers = new LockConditionHandlers(componentTypeContainer);
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double>{

        private AbstractGroup group;

        public Instance(){
            group = null;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            if(group == null){
                getGroup(dataStorage);
            }

            ComponentIterator<LockConditions> itr = group.getComponentIterator(lockConditionComponentType);
            while(itr.hasNext()){
                LockConditions lockCondition = itr.next();
                EntityHandle handle = dataStorage.makeHandle(itr.entityIDOfPreviousComponent());
                handlers.handleCondition(ecsInterface, handle, lockCondition);
            }

            sliceBoard.ageAndCullMessages();
        }

        private void getGroup(AbstractDataStorage dataStorage){
            group = dataStorage.createGroup(lockConditionComponentType);
        }
    }
}
