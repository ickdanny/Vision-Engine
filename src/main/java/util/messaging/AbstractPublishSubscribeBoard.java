package util.messaging;

import java.util.List;

public interface AbstractPublishSubscribeBoard {
    <T> void publishMessage(Message<T> message);
    <T> List<Message<T>> getMessageList(Topic<T> topic);
    boolean hasTopicalMessages(Topic<?> topic);
    void ageAndCullMessages();
}