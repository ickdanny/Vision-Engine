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
import ecs.system.criticalorders.RemoveComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramComponent;
import internalconfig.game.systems.gamesystems.instructionhandlers.InstructionHandlers;
import resource.Resource;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Properties;

import static internalconfig.game.components.ComponentTypes.*;

public class ProgramSystem implements AbstractSystem<Double> {

    private final AbstractComponentType<ProgramComponent> programComponentType;

    private final AbstractComponentTypeContainer componentTypeContainer;

    private final Resource<Properties> propertiesResource;

    public ProgramSystem(AbstractComponentTypeContainer componentTypeContainer,
                         Resource<Properties> propertiesResource) {
        programComponentType = componentTypeContainer.getTypeInstance(ProgramComponentType.class);
        this.componentTypeContainer = componentTypeContainer;
        this.propertiesResource = propertiesResource;
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double> {

        private AbstractGroup group;
        private final InstructionHandlers instructionHandlers;

        private Instance() {
            group = null;
            instructionHandlers = new InstructionHandlers(componentTypeContainer, propertiesResource);
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            if (group == null) {
                getGroup(dataStorage);
            }

            ComponentIterator<ProgramComponent> programItr = group.getComponentIterator(programComponentType);
            while (programItr.hasNext()) {
                ProgramComponent programComponent = programItr.next();
                int entityID = programItr.entityIDOfPreviousComponent();
                runProgram(ecsInterface, programComponent, entityID);
            }

            sliceBoard.ageAndCullMessages();
        }

        @SuppressWarnings("StatementWithEmptyBody")
        private void runProgram(AbstractECSInterface ecsInterface, ProgramComponent programComponent, int entityID) {
            while (programComponent.hasInstructions() && runInstruction(
                    ecsInterface,
                    programComponent,
                    entityID
            )) ;
            removeProgramComponentIfNoInstructions(ecsInterface, programComponent, entityID);
        }

        private boolean runInstruction(AbstractECSInterface ecsInterface,
                                       ProgramComponent programComponent,
                                       int entityID) {
            return runInstruction(ecsInterface,
                    programComponent.getCurrentInstructionNode(),
                    programComponent,
                    programComponent.getDataMap(),
                    entityID);
        }

        private boolean runInstruction(AbstractECSInterface ecsInterface,
                                       InstructionNode<?, ?> node,
                                       ProgramComponent programComponent,
                                       InstructionDataMap dataMap,
                                       int entityID) {
            //if any downstream injected instruction passes do not run the current instruction
            if (node.hasInjectedInstruction()) {
                InstructionNode<?, ?> injectedNode = programComponent.getInjectedInstructionNode(node);
                if (runInstruction(ecsInterface, injectedNode, programComponent, dataMap, entityID)) {
                    return true;
                }
            }
            if (instructionHandlers.handleInstruction(ecsInterface, node, dataMap, entityID)) {
                programComponent.moveToNextInstruction(node);
                return true;
            }
            return false;
        }

        private void removeProgramComponentIfNoInstructions(AbstractECSInterface ecsInterface,
                                                            ProgramComponent programComponent,
                                                            int entityID) {
            if (!programComponent.hasInstructions()) {
                removeProgramComponent(ecsInterface, entityID);
            }
        }

        private void removeProgramComponent(AbstractECSInterface ecsInterface, int entityID) {
            EntityHandle handle = ecsInterface.getSliceData().makeHandle(entityID);
            RemoveComponentOrder order = new RemoveComponentOrder(handle, programComponentType);
            ecsInterface.getSliceBoard().publishMessage(ECSUtil.makeRemoveComponentMessage(order));
        }

        private void getGroup(AbstractDataStorage dataStorage) {
            group = dataStorage.createGroup(programComponentType);
        }
    }
}
