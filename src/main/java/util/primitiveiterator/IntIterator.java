package util.primitiveiterator;

import java.util.Objects;
import java.util.function.Consumer;

public interface IntIterator {
    boolean hasNext();
    int next();
    default void remove(){
        throw new UnsupportedOperationException();
    }

    //copied from Iterator
    default void forEachRemaining(Consumer<? super Integer> action) {
        Objects.requireNonNull(action);
        while (hasNext())
            action.accept(next());
    }
}

//there exists a primitiveIterator functionality in the java default library - but what is a spliterator?