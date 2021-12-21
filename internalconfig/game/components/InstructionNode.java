package internalconfig.game.components;

import static internalconfig.game.components.ProgramComponent.NO_INSTRUCTIONS;

public class InstructionNode<T, V> implements ProgramBuilder.ProgramBuilderPassable<T, V> {

    private static final int NO_INDEX = NO_INSTRUCTIONS;

    private final Instructions<T, V> instruction;
    private T data;
    private int nextIndex;
    private int injectedInstructionIndex;

    public InstructionNode(Instructions<T, V> instruction) {
        this.instruction = instruction;
        data = null;
        nextIndex = NO_INDEX;
        injectedInstructionIndex = NO_INDEX;
    }

    public InstructionNode(Instructions<T, V> instruction, T data) {
        this.instruction = instruction;
        this.data = data;
        nextIndex = NO_INDEX;
        injectedInstructionIndex = NO_INDEX;
    }

    public Instructions<T, V> getInstruction() {
        return instruction;
    }

    public T getData() {
        return data;
    }

    int getNextIndex() {
        return nextIndex;
    }

    int getInjectedInstructionIndex() {
        return injectedInstructionIndex;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }

    public void setInjectedInstructionIndex(int injectedInstructionIndex) {
        this.injectedInstructionIndex = injectedInstructionIndex;
    }

    public boolean hasNext(){
        return nextIndex != NO_INDEX;
    }

    public boolean hasInjectedInstruction(){
        return injectedInstructionIndex != NO_INDEX;
    }
}
