package internalconfig.game.components;

import java.util.HashMap;
import java.util.Map;

public class InstructionDataMap {

    private final Map<InstructionNode<?, ?>, Object> innerMap;

    InstructionDataMap() {
        innerMap = new HashMap<>();
    }

    public <V> void put(InstructionNode<?, V> instructionNode, V data) {
        innerMap.put(instructionNode, data);
    }
    @SuppressWarnings("unchecked")
    public <V> V get(InstructionNode<?, V> instructionNode) {
        return (V)innerMap.get(instructionNode);
    }
    public void remove(InstructionNode<?, ?> instructionNode) {
        innerMap.remove(instructionNode);
    }

    public boolean containsKey(InstructionNode<?, ?> instructionNode) {
        return innerMap.containsKey(instructionNode);
    }
}