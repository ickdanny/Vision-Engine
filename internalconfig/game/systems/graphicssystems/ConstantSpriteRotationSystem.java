package internalconfig.game.systems.graphicssystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.component.componentstorage.AbstractGroup;
import ecs.component.componentstorage.ComponentIterator;
import ecs.datastorage.AbstractDataStorage;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.SpriteInstruction;

import static internalconfig.game.components.ComponentTypes.*;

@SuppressWarnings("unused")
public class ConstantSpriteRotationSystem implements AbstractSystem<Double> {

    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
    private final AbstractComponentType<Double> constantSpriteRotationComponentType;

    public ConstantSpriteRotationSystem(AbstractComponentTypeContainer componentTypeContainer){
        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(SpriteInstructionComponentType.class);
        constantSpriteRotationComponentType = componentTypeContainer.getTypeInstance(ConstantSpriteRotationComponentType.class);
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double>{
        private AbstractGroup group;

        private Instance() {
            group = null;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();

            if (group == null) {
                getGroup(dataStorage);
            }

            ComponentIterator<SpriteInstruction> spriteItr = group.getComponentIterator(spriteInstructionComponentType);
            ComponentIterator<Double> rotationItr = group.getComponentIterator(constantSpriteRotationComponentType);

            while(spriteItr.hasNext() && rotationItr.hasNext()){
                SpriteInstruction spriteInstruction = spriteItr.next();
                double rotation = rotationItr.next();

                spriteInstruction.setRotation(spriteInstruction.getRotation().add(rotation));
            }

            if(spriteItr.hasNext() || rotationItr.hasNext()){
                throw new RuntimeException("unexpected extra component in iterator");
            }

            ecsInterface.getSliceBoard().ageAndCullMessages();
        }


        private void getGroup(AbstractDataStorage dataStorage){
            group = dataStorage.createGroup(spriteInstructionComponentType, constantSpriteRotationComponentType);
        }
    }
}