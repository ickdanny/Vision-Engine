package internalconfig.game.systems.dialoguesystems;

import java.util.HashMap;

public enum DialogueCommands {
    SET_LOWER_IMAGE("set_lower_image"),
    SET_UPPER_IMAGE("set_upper_image"),

    SET_LOWER_TEXT("set_lower_text"),
    SET_UPPER_TEXT("set_upper_text"),

    START_TRACK("start_track"),

    STOP("stop"),
    ;

    private static final HashMap<String, DialogueCommands> stringMap;

    static{
        stringMap = new HashMap<>();
        for(DialogueCommands command : values()){
            stringMap.put(command.getCommand(), command);
        }
    }

    final String command;
    DialogueCommands(String command){
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return command;
    }

    public static DialogueCommands getCommand(String commandString){
        return stringMap.get(commandString);
    }
}