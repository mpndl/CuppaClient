package za.nmu.wrpv;

import java.util.Map;

@FunctionalInterface
public interface Subscriber {
    void onPublished(Object publisher, String topic, Map<String,Object> params);
}
