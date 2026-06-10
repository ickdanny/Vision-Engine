package util.messaging;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PublishSubscribeBoard implements AbstractPublishSubscribeBoard {

    private final Topic<?>[] topics;
    private final Map<Topic<?>, AbstractMessageList<?>> messageLists;

    public PublishSubscribeBoard(Topic<?>[] topics){
        this.topics = topics;
        messageLists = new HashMap<>();
        for(Topic<?> topic : topics){
            addMessageList(topic);
        }
    }

    private <T> void addMessageList(Topic<T> topic){
        messageLists.put(topic, new ArrayListMessageList<T>()); //AbstractMessageList allows this
    }

    @Override
    public <T> void publishMessage(Message<T> message){
        Topic<T> topic = message.getTopic();
        List<Message<T>> messageList = getMessageList(topic);
        messageList.add(message);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<Message<T>> getMessageList(Topic<T> topic){
        AbstractMessageList<T> toRet =  (AbstractMessageList<T>) messageLists.get(topic);
        if(toRet == null){
            throw new RuntimeException("Topic " + topic + " is not present in " + this);
        }
        return toRet;
    }

    @Override
    public boolean hasTopicalMessages(Topic<?> topic) {
        AbstractMessageList<?> messageList = messageLists.get(topic);
        return !messageList.isEmpty();
    }

    @Override
    public void ageAndCullMessages() {
        for(Topic<?> topic : topics){
            AbstractMessageList<?> messageList = messageLists.get(topic);
            Iterator<? extends Message<?>> iterator = messageList.iterator();
            while(iterator.hasNext()){
                Message<?> message = iterator.next();
                message.ageMessage();
                if(!message.isAlive()){
                    iterator.remove();
                }
            }
        }
    }
}