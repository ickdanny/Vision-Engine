package internalconfig.game.systems.graphicssystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.component.componentstorage.AbstractGroup;
import ecs.component.componentstorage.ComponentIterator;
import ecs.datastorage.AbstractDataStorage;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.SinusoidalSpriteVerticalOffsetComponent;
import internalconfig.game.components.SpriteInstruction;
import util.math.geometry.AbstractVector;
import util.math.geometry.CartesianVector;

import static internalconfig.game.components.ComponentTypes.*;
import static util.math.Constants.TAU;

public class SinusoidalSpriteVerticalOffsetSystem implements AbstractSystem<Double> {

    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
    private final AbstractComponentType<SinusoidalSpriteVerticalOffsetComponent> sinusoidalSpriteVerticalOffsetComponentType;

    public SinusoidalSpriteVerticalOffsetSystem(AbstractComponentTypeContainer componentTypeContainer){
        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(SpriteInstructionComponentType.class);
        sinusoidalSpriteVerticalOffsetComponentType = componentTypeContainer.getTypeInstance(SinusoidalSpriteVerticalOffsetComponentType.class);
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
            ComponentIterator<SinusoidalSpriteVerticalOffsetComponent> sineItr = group.getComponentIterator(sinusoidalSpriteVerticalOffsetComponentType);

            while(spriteItr.hasNext() && sineItr.hasNext()){
                SpriteInstruction spriteInstruction = spriteItr.next();
                SinusoidalSpriteVerticalOffsetComponent sineData = sineItr.next();

                double yOffset = getVerticalOffsetAndUpdateComponent(sineData);
                AbstractVector offset = new CartesianVector(0, yOffset);
                spriteInstruction.setOffset(offset);
            }

            if(spriteItr.hasNext() || sineItr.hasNext()){
                throw new RuntimeException("unexpected extra component in iterator");
            }

            ecsInterface.getSliceBoard().ageAndCullMessages();
        }

        private double getVerticalOffsetAndUpdateComponent(SinusoidalSpriteVerticalOffsetComponent sineData){
            double nextTick = sineData.getCurrentTick() + sineData.getTick();
            if(nextTick > TAU){
                nextTick = nextTick % TAU;
            }
            sineData.setCurrentTick(nextTick);
            return Math.sin(nextTick) * sineData.getAmplitude();
        }


        private void getGroup(AbstractDataStorage dataStorage){
            group = dataStorage.createGroup(spriteInstructionComponentType, sinusoidalSpriteVerticalOffsetComponentType);
        }
    }
}