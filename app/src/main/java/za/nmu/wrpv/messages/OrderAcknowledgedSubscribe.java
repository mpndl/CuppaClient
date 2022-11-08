package za.nmu.wrpv.messages;

import za.nmu.wrpv.ServerHandler;
import za.nmu.wrpv.Subscriber;

import java.io.Serializable;

public class OrderAcknowledgedSubscribe extends Subscribe implements Serializable {
    private static final long serialVersionUID = 50L;
    public static final String key = "acknowledge";
    public OrderAcknowledgedSubscribe(String topic, Subscriber subscriber) {
        super(topic, subscriber);
    }

    @Override
    public void apply(Object handler) {
        Publish message = new OrderAcknowledgedPublish(((History)handler).id, OrderAcknowledgedPublish.key, null);
        ServerHandler.publish(message);
    }
}
