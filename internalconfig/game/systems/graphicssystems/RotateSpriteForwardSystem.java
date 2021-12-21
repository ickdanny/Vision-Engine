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
import internalconfig.game.components.VelocityComponent;
import util.math.geometry.Angle;

import static internalconfig.game.components.ComponentTypes.*;

public class RotateSpriteForwardSystem implements AbstractSystem<Double> {

    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
    private final AbstractComponentType<VelocityComponent> velocityComponentType;
    private final AbstractComponentType<Void> rotateSpriteForwardMarker;

    public RotateSpriteForwardSystem(AbstractComponentTypeContainer componentTypeContainer){
        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(SpriteInstructionComponentType.class);
        velocityComponentType = componentTypeContainer.getTypeInstance(VelocityComponentType.class);
        rotateSpriteForwardMarker = componentTypeContainer.getTypeInstance(RotateSpriteForwardMarker.class);
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

            ComponentIterator<VelocityComponent> velocityItr = group.getComponentIterator(velocityComponentType);
            ComponentIterator<SpriteInstruction> spriteItr = group.getComponentIterator(spriteInstructionComponentType);

            while(velocityItr.hasNext() && spriteItr.hasNext()){
                VelocityComponent velocityComponent = velocityItr.next();
                SpriteInstruction spriteInstruction = spriteItr.next();

                Angle angle = new Angle(90-velocityComponent.getVelocity().getAngle().getAngle());//?
                if(!spriteInstruction.getRotation().equals(angle)){
                    spriteInstruction.setRotation(angle);
                }
            }

            if(velocityItr.hasNext() || spriteItr.hasNext()){
                throw new RuntimeException("unexpected extra component in iterator");
            }

            ecsInterface.getSliceBoard().ageAndCullMessages();
        }


        private void getGroup(AbstractDataStorage dataStorage){
            group = dataStorage.createGroup(spriteInstructionComponentType, velocityComponentType, rotateSpriteForwardMarker);
        }
    }
}
