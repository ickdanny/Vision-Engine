package internalconfig.game.systems.dialoguesystems;

import util.tuple.Tuple2;

import java.util.Objects;

public class CommandDataTuple{

    private final Tuple2<DialogueCommands, String> tuple;

    public CommandDataTuple(DialogueCommands command, String data) {
        tuple = new Tuple2<>(command, data);
    }

    public DialogueCommands getCommand(){
        return tuple.a;
    }

    public String getData(){
        return tuple.b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandDataTuple)) return false;
        CommandDataTuple that = (CommandDataTuple) o;
        return tuple.equals(that.tuple);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tuple);
    }

    @Override
    public String toString() {
        return "[" + getCommand() + "][" + getData() + "]";
    }
}
