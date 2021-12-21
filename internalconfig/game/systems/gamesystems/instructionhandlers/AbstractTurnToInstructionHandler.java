package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import internalconfig.game.components.VelocityComponent;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.PolarVector;
import util.tuple.Tuple2;

abstract class AbstractTurnToInstructionHandler implements AbstractInstructionHandler<Tuple2<Angle, Integer>, Double> {

    private final static double ANGLE_EQUALITY_BUFFER = .5;

    private final Instructions<Tuple2<Angle, Integer>, Double> instruction;
    private final AbstractComponentType<VelocityComponent> velocityComponentType;

    AbstractTurnToInstructionHandler(Instructions<Tuple2<Angle, Integer>, Double> instruction,
                                     AbstractComponentTypeContainer componentTypeContainer){
        this.instruction = instruction;
        velocityComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.VelocityComponentType.class);
    }

    @Override
    public final Instructions<Tuple2<Angle, Integer>, Double> getInstruction() {
        return instruction;
    }

    @Override
    public final boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Tuple2<Angle, Integer>, Double> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {

        AbstractDataStorage dataStorage = ecsInterface.getSliceData();
        EntityHandle handle = dataStorage.makeHandle(entityID);
        if(dataStorage.containsComponent(handle, velocityComponentType)) {

            VelocityComponent velocityComponent = dataStorage.getComponent(handle, velocityComponentType);
            AbstractVector velocity = velocityComponent.getVelocity();
            Angle initAngle = velocity.getAngle();

            Angle finalAngle = node.getData().a;
            int ticks = node.getData().b;

            double angleIncrement;
            if(dataMap.containsKey(node)){
                angleIncrement = dataMap.get(node);
            }
            else{
                double wholeAngleShift = getWholeAngleShift(initAngle, finalAngle);
                angleIncrement = ticks > 1 ? wholeAngleShift/ticks : wholeAngleShift;
                dataMap.put(node, angleIncrement);
            }

            boolean hasReachedAngle = false;

            Angle newAngle = initAngle.add(angleIncrement);
            if(Math.abs(newAngle.smallerDifference(finalAngle)) <= ANGLE_EQUALITY_BUFFER){
                newAngle = finalAngle;
                hasReachedAngle = true;
            }

            velocityComponent.setVelocity(new PolarVector(velocity.getMagnitude(), newAngle));

            if(!hasReachedAngle){
                return false;
            }
        }
        dataMap.remove(node);
        return true;
    }

    protected abstract double getWholeAngleShift(Angle initAngle, Angle finalAngle);
}