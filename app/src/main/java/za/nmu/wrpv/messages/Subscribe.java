package za.nmu.wrpv.messages;

import za.nmu.wrpv.Subscriber;

public class Subscribe extends Message {
    private static final long serialVersionUID = 2L;

    private final String topic;
    public final Subscriber subscriber;

    public Subscribe(String topic, Subscriber subscriber) {
        this.topic = topic;
        this.subscriber = subscriber;
    }
}