package internalconfig.game.components;

import java.util.ArrayList;

public class ProgramBuilder {

    public static InstructionList linearLink(ProgramBuilderPassable<?, ?>... instructionsOrNodes){
        if(instructionsOrNodes.length <= 0){
            throw new IllegalArgumentException("passing 0 (or less?) elements into linearLink!");
        }
        InstructionList toRet = new InstructionList();
        for(int i = 0; i < instructionsOrNodes.length - 1; ++i){
            InstructionNode<?, ?> node = asNode(instructionsOrNodes[i]);
            node.setNextIndex(i + 1);
            toRet.add(node);
        }
        toRet.add(asNode(instructionsOrNodes[instructionsOrNodes.length - 1]));

        return toRet;
    }

    public static InstructionList circularLink(ProgramBuilderPassable<?, ?>... instructionsOrNodes){
        InstructionList toRet = linearLink(instructionsOrNodes);
        toRet.get(toRet.size() - 1).setNextIndex(0);
        return toRet;
    }

    private static <T, V> InstructionNode<T, V> asNode(ProgramBuilderPassable<T, V> instructionOrNode){
        if(instructionOrNode instanceof InstructionNode){
            return (InstructionNode<T, V>) instructionOrNode;
        }
        else if(instructionOrNode instanceof Instructions){
            return new InstructionNode<>((Instructions<T, V>) instructionOrNode);
        }
        throw new IllegalArgumentException("passing neither an instruction nor a node into asNode");
    }

    public interface ProgramBuilderPassable<T, V>{}

    @SuppressWarnings("UnusedReturnValue")
    public static final class InstructionList extends ArrayList<InstructionNode<?, ?>> {

        private InstructionList noLinkAppend(InstructionList other){
            other.shiftAllIndices(this.size());
            addAll(other);
            other.clear();
            return this;
        }

        public InstructionList linkAppend(InstructionList other){
            InstructionNode<?, ?> lastNode = get(size() - 1);
            if(lastNode.hasNext()){
                throw new RuntimeException("trying to linkAppend when last node already has next!");
            }
            lastNode.setNextIndex(size());
            return noLinkAppend(other);
        }

        public InstructionList linkAppendExtraNode(InstructionList other, int skipFromBack){
            InstructionNode<?, ?> lastNode = get(size() - 1);
            if(lastNode.hasNext()){
                throw new RuntimeException("trying to linkAppendSkipping when last node already has next!");
            }
            lastNode.setNextIndex(size());
            InstructionNode<?, ?> nodeToAppendTo = get(size() - 1 - skipFromBack);
            if(nodeToAppendTo.hasNext()){
                throw new RuntimeException("trying to linkAppendSkipping when specified node already has next!");
            }
            nodeToAppendTo.setNextIndex(size());
            return noLinkAppend(other);
        }

        private InstructionList noLinkAppend(InstructionNode<?, ?> node){
            add(node);
            return this;
        }

        public InstructionList linkAppend(InstructionNode<?, ?> node){
            InstructionNode<?, ?> lastNode = get(size() - 1);
            if(lastNode.hasNext()){
                throw new RuntimeException("trying to linkAppend when last node already has next!");
            }
            lastNode.setNextIndex(size());
            add(node);
            return this;
        }

        public InstructionList linkAppendExtraNode(InstructionNode<?, ?> node, int skipFromBack){
            InstructionNode<?, ?> lastNode = get(size() - 1);
            if(lastNode.hasNext()){
                throw new RuntimeException("trying to linkAppendSkipping when last node already has next!");
            }
            lastNode.setNextIndex(size());

            InstructionNode<?, ?> nodeToAppendTo = get(size() - 1 - skipFromBack);
            if(nodeToAppendTo.hasNext()){
                throw new RuntimeException("trying to linkAppendSkipping when specified node already has next!");
            }
            nodeToAppendTo.setNextIndex(size());
            add(node);
            return this;
        }

        public InstructionList noLinkInject(InstructionList other){
            injectSizeAsIndex();
            return noLinkAppend(other);
        }

        public InstructionList linkInject(InstructionList other){
            injectSizeAsIndex();
            return linkAppend(other);
        }

        public InstructionList noLinkInject(InstructionList other, InstructionNode<?, ?> predicateNode){
            injectSizeAsIndex();
            noLinkAppend(predicateNode);
            return linkAppend(other);
        }

        public InstructionList linkInject(InstructionList other, InstructionNode<?, ?> predicateNode){
            injectSizeAsIndex();
            linkAppend(predicateNode);
            return linkAppend(other);
        }

        public InstructionList noLinkInjectIfPossible(InstructionList other){
            injectSizeAsIndexIfPossible();
            return noLinkAppend(other);
        }

        public InstructionList linkInjectIfPossible(InstructionList other){
            injectSizeAsIndexIfPossible();
            return linkAppend(other);
        }

        public InstructionList noLinkInjectIfPossible(InstructionList other, InstructionNode<?, ?> predicateNode){
            injectSizeAsIndexIfPossible();
            noLinkAppend(predicateNode);
            return linkAppend(other);
        }

        public InstructionList linkInjectIfPossible(InstructionList other, InstructionNode<?, ?> predicateNode){
            injectSizeAsIndexIfPossible();
            linkAppend(predicateNode);
            return linkAppend(other);
        }

        public InstructionList linkBackToFront(){
            InstructionNode<?, ?> lastNode = get(size() - 1);
            if(lastNode.hasNext()){
                throw new RuntimeException("trying to linkBackToFront when last node already has next!");
            }
            lastNode.setNextIndex(0);
            return this;
        }

        private void injectSizeAsIndex(){
            for(InstructionNode<?, ?> node : this){
                if(node.hasInjectedInstruction()){
                    throw new RuntimeException("trying to inject twice!");
                }
                node.setInjectedInstructionIndex(size());
            }
        }

        private void injectSizeAsIndexIfPossible(){
            for(InstructionNode<?, ?> node : this){
                if(!node.hasInjectedInstruction()){
                    node.setInjectedInstructionIndex(size());
                }
            }
        }

        private void shiftAllIndices(int shift){
            for(InstructionNode<?, ?> node : this){
                if(node.hasNext()) {
                    node.setNextIndex(node.getNextIndex() + shift);
                }
                if(node.hasInjectedInstruction()){
                    node.setInjectedInstructionIndex(node.getInjectedInstructionIndex() + shift);
                }
            }
        }

        public InstructionNode<?, ?>[] compile(){
            return toArray(new InstructionNode[0]);
        }
    }
}
