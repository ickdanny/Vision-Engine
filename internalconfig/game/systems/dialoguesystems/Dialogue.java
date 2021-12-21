package internalconfig.game.systems.dialoguesystems;

public class Dialogue {
    private final CommandDataTuple[] commandList;

    public Dialogue(CommandDataTuple[] commandList) {
        this.commandList = commandList;
    }

    public CommandDataTuple getCommandDataTuple(int index){
        return commandList[index];
    }

    public int size(){
        return commandList.length;
    }
}