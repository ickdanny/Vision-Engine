package util.messaging;

import java.util.Objects;

public class Message <T> {
    public static final int AGELESS = -1;

    private final Topic<T> topic;
    private final T message;
    private int lifetime;

    public Message(Topic<T> topic, T message, int lifetime) {
        this.topic = topic;
        this.message = message;
        this.lifetime = lifetime;
    }

    public void ageMessage(){
        if(lifetime > 0){
            --lifetime;
        }
    }
    public boolean isAlive(){
        return lifetime != 0;
    }

    public Topic<T> getTopic() {
        return topic;
    }
    public T getMessage() {
        return message;
    }
    public int getLifetime() {
        return lifetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message<?> message1 = (Message<?>) o;
        return lifetime == message1.lifetime &&
                Objects.equals(topic, message1.topic) &&
                Objects.equals(message, message1.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, message, lifetime);
    }
}