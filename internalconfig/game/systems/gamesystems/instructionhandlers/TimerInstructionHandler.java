package internalconfig.game.systems.gamesystems.instructionhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionDataMap;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.Instructions;
import util.Ticker;

import static internalconfig.game.components.Instructions.TIMER;

class TimerInstructionHandler implements AbstractInstructionHandler<Integer, Ticker> {

    @Override
    public Instructions<Integer, Ticker> getInstruction() {
        return TIMER;
    }

    @Override
    public boolean handleInstruction(AbstractECSInterface ecsInterface,
                                     InstructionNode<Integer, Ticker> node,
                                     InstructionDataMap dataMap,
                                     int entityID) {

        Ticker ticker;
        if(dataMap.containsKey(node)){
            ticker = dataMap.get(node);
        }
        else{
            dataMap.put(node, ticker = new Ticker(node.getData(), false));
        }

        int tick = ticker.stepAndGetTick();
        if(tick <= 0){
            dataMap.remove(node);
            return true;
        }
        return false;
    }
}
