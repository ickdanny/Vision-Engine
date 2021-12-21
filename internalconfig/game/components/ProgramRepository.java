package internalconfig.game.components;

import util.tuple.Tuple2;

import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.ProgramBuilder.InstructionList;

public enum ProgramRepository {
    REMOVE_AFTER_EMPTY_SPAWN(
            ProgramBuilder.linearLink(
                    WAIT_UNTIL_SPAWN_COMPONENT_EMPTY,
                    REMOVE_ENTITY
            ).compile()
    ),
    LIFETIME_1(
            ProgramUtil.makeLifetimeProgram(1).compile()
    ),
    LIFETIME_2(
            ProgramUtil.makeLifetimeProgram(2).compile()
    ),
    LIFETIME_3(
            ProgramUtil.makeLifetimeProgram(3).compile()
    ),
    LIFETIME_10(
            ProgramUtil.makeLifetimeProgram(10).compile()
    ),

    PICKUP_ACCELERATE_DOWN(makePickupDecelerationInstruction().compile()),

    MARK_ENEMY_BULLET_COLLIDABLE_TIMER(
            ProgramBuilder.linearLink(
                    new InstructionNode<>(TIMER, ENEMY_BULLET_COLLIDABLE_TIME),
                    SET_COLLIDABLE
            ).compile()
    ),

    ;
    private final InstructionNode<?, ?>[] program;

    ProgramRepository(InstructionNode<?, ?>[] program) {
        this.program = program;
    }

    public InstructionNode<?, ?>[] getProgram() {
        return program;
    }



    private static InstructionList makePickupDecelerationInstruction(){
        return ProgramBuilder.linearLink(
                new InstructionNode<>(DECELERATE_TO_SPEED, new Tuple2<>(PICKUP_FINAL_SPEED, PICKUP_DECELERATION))
        );
    }
}
