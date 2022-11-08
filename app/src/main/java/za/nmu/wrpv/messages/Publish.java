package za.nmu.wrpv.messages;

import java.util.Map;

public class Publish extends Message{
    private static final long serialVersionUID = 1L;

    public final Object publisher;
    public String topic;
    public Map<String,Object> params;

    public Publish(Object publisher, String topic, Map<String, Object> params) {
        this.publisher = publisher;
        this.topic = topic;
        this.params = params;
    }
}
