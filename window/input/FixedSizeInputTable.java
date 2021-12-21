package window.input;

import java.util.Arrays;

class FixedSizeInputTable extends AbstractSyncInputStorage{
    private final boolean[][] table;

    FixedSizeInputTable(int numInputs, int numTurns){
        table = new boolean[numInputs][numTurns];
        initTableValues();
    }
    private void initTableValues(){
        for (boolean[] booleans : table) {
            Arrays.fill(booleans, false);
        }
    }

    public void syncChangeInput(int inputID, boolean inputValue){
        table[inputID][0] = inputValue;
    }

    @SuppressWarnings("ManualArrayCopy")
    public void syncNewTurn(){
        for(int i = 0; i < table.length; i++){
            for(int j = table[i].length - 1; j > 0; j--){
                table[i][j] = table[i][j-1];
            }
        }
    }

    public boolean syncMatchesPattern(int inputID, boolean[] pattern){
        boolean[] inputs = table[inputID];
        if(inputs.length < pattern.length){
            throw new IllegalArgumentException("Pattern is longer than input storage");
        }
        if(inputs.length == pattern.length){
            return Arrays.equals(inputs, pattern);
        }
        else{
            boolean[] truncatedInputs = Arrays.copyOf(inputs, pattern.length);
            return Arrays.equals(truncatedInputs, pattern);
        }
    }
}
//there may be a superior implementation with a rotating array, where each turn we set the array "starting" point
//may be useful if we desire to store many turns worth of window.input data
//as the cost of copying per turn remains the same
//but as I plan to only store two turns there is no benefit