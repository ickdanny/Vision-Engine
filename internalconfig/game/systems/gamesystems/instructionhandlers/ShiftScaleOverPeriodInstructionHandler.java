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
import util.tuple.Tuple2;

import static internalconfig.game.components.Instructions.SHIFT_SCALE_OVER_PERIOD;
import static internalconfig.game.components.ComponentTypes.*;

class ShiftScaleOverPeriodInstructionHandler
        implements AbstractInstructionHandler<Tuple2<Double, Integer>, Double> {

    private static final double SCALE_TOLERANCE = 0.005;

    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;

    ShiftScaleOverPeriodInstructionHandler(AbstractComponentTypeContainer componentTypeContainer){
        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(SpriteInstructionComponentType.class);
    }

    @Override
    public Instructions<Tuple2<Double, Integer>, Double> getInstruction() {
        return SHIFT_SCALE_OVER_PERIOD;
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

            double initScale = spriteInstruction.getScale();
            double targetScale = node.getData().a;

            int ticks = node.getData().b;
            double scaleIncrement = retrieveOrCreateScaleIncrement(dataMap, node, initScale, targetScale, ticks);

            double nextScale = initScale + scaleIncrement;
            if(Math.abs(nextScale - targetScale) < SCALE_TOLERANCE){
                nextScale = targetScale;
            }

            spriteInstruction.setScale(nextScale);
            return nextScale == targetScale;
        }
        else {
            return true;
        }
    }

    private double retrieveOrCreateScaleIncrement(InstructionDataMap dataMap,
                                                  InstructionNode<Tuple2<Double, Integer>, Double> node,
                                                  double initScale,
                                                  double targetScale,
                                                  int ticks){
        if(dataMap.containsKey(node)){
            return dataMap.get(node);
        }
        else{
            double scaleIncrement = ticks > 1 ? (targetScale - initScale)/ticks
                    : targetScale - initScale;
            dataMap.put(node, scaleIncrement);
            return scaleIncrement;
        }
    }
}