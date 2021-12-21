package internalconfig.game.components;

import java.util.NoSuchElementException;

public class ProgramComponent {

    static final int NO_INSTRUCTIONS = -2;

    private int currentIndex;
    private final InstructionNode<?, ?>[] program;
    private final InstructionDataMap dataMap;

    public ProgramComponent(InstructionNode<?, ?>[] program){
        this.program = program;
        dataMap = new InstructionDataMap();
        currentIndex = this.program.length > 0 ? 0 : NO_INSTRUCTIONS;
    }

    public boolean hasInstructions(){
        return currentIndex != NO_INSTRUCTIONS;
    }

    public InstructionNode<?, ?> getCurrentInstructionNode(){
        if(currentIndex != NO_INSTRUCTIONS){
            return program[currentIndex];
        }
        throw new NoSuchElementException("trying to get instruction when no instructions!");
    }

    public InstructionNode<?, ?> getInjectedInstructionNode(InstructionNode<?, ?> node){
        return program[node.getInjectedInstructionIndex()];
    }

    public InstructionDataMap getDataMap(){
        return dataMap;
    }

    public void moveToNextInstruction(InstructionNode<?, ?> node){
        currentIndex = node.getNextIndex();
    }
}
