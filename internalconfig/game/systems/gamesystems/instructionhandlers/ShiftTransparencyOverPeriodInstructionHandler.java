package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import internalconfig.game.components.SpriteInstruction;
import util.image.ImageUtil;
import util.tuple.Tuple2;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.components.Instructions.SHIFT_TRANSPARENCY_OVER_PERIOD;

class ShiftTransparencyOverPeriodInstructionHandler
        implements AbstractInstructionHandler<Tuple2<Double, Integer>, Double> {

    private static final double TRANSPARENCY_TOLERANCE = 0.005;

    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;

    ShiftTransparencyOverPeriodInstructionHandler(AbstractComponentTypeContainer componentTypeContainer){
        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(SpriteInstructionComponentType.class);
    }

    @Override
    public Instructions<Tuple2<Double, Integer>, Double> getInstruction() {
        return SHIFT_TRANSPARENCY_OVER_PERIOD;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Tuple2<Double, Integer>, Double> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);

        if(dataStorage.containsComponent(handle, spriteInstructionComponentType)) {
            SpriteInstruction spriteInstruction = dataStorage.getComponent(handle, spriteInstructionComponentType);

            double initTransparency = spriteInstruction.getTransparency();
            double targetTransparency = node.getData().a;
            ImageUtil.throwIfInvalidTransparency(targetTransparency);

            int ticks = node.getData().b;
            double transparencyIncrement = retrieveOrCreateTransparencyIncrement(dataMap, node, initTransparency, targetTransparency, ticks);

            double nextTransparency = initTransparency + transparencyIncrement;
            if(Math.abs(nextTransparency - targetTransparency) < TRANSPARENCY_TOLERANCE){
                nextTransparency = targetTransparency;
            }
            else{
                nextTransparency = ImageUtil.VALID_TRANSPARENCY_INTERVAL.makeIntoInterval(nextTransparency);
            }

            spriteInstruction.setTransparency(nextTransparency);
            return nextTransparency == targetTransparency;
        }
        else {
            return true;
        }
    }

    private double retrieveOrCreateTransparencyIncrement(InstructionDataMap dataMap,
                                                         InstructionNode<Tuple2<Double, Integer>, Double> node,
                                                         double initTransparency,
                                                         double targetTransparency,
                                                         int ticks){
        if(dataMap.containsKey(node)){
            return dataMap.get(node);
        }
        else{
            double transparencyIncrement = ticks > 1 ? (targetTransparency - initTransparency)/ticks
                                                     : targetTransparency - initTransparency;
            dataMap.put(node, transparencyIncrement);
            return transparencyIncrement;
        }
    }
}