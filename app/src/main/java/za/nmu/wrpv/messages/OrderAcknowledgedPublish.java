package za.nmu.wrpv.messages;

import java.io.Serializable;
import java.util.Map;

import za.nmu.wrpv.ServerHandler;

public class OrderAcknowledgedPublish extends Publish implements Serializable {
    private final static long serialVersionUID = 51L;
    public static final String key = "acknowledge";
    public OrderAcknowledgedPublish(Object publisher, String topic, Map<String, Object> params) {
        super(publisher, topic, params);
    }
}
