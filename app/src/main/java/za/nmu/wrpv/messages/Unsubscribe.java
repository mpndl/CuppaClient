package za.nmu.wrpv.messages;

import za.nmu.wrpv.Subscriber;

public class Unsubscribe extends Message {
    private static final long serialVersionUID = 3L;

    private final String topic;
    private final Subscriber subscriber;

    public Unsubscribe(String topic, Subscriber subscriber) {
        this.topic = topic;
        this.subscriber = subscriber;
    }
}