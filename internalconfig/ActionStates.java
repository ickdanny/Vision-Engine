package internalconfig;

@SuppressWarnings("unused")
public enum ActionStates {
    NEW_ACTION("just pressed", new boolean[]{true, false}),
    NEW_INACTION("just released", new boolean[]{false, true}),
    CONTINUING_ACTION("still pressed", new boolean[]{true, true}),
    CONTINUING_INACTION("still released", new boolean[]{false, false}),
    ;
    private static int maxTurns;

    public final String stringName;
    public final boolean[] pattern;
    static{
        maxTurns = 0;
        for(ActionStates actionState : values()){
            maxTurns = Math.max(maxTurns, actionState.pattern.length);
        }
    }

    ActionStates(String stringName, boolean[] pattern){
        this.pattern = pattern;
        this.stringName = stringName;
    }

    public static int getMaxTurns(){
        return maxTurns;
    }
}

//since each state is just a pattern of bits, could use a binary tree
