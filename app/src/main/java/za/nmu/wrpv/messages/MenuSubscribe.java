package za.nmu.wrpv.messages;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

import za.nmu.wrpv.Item;
import za.nmu.wrpv.MenuItems;
import za.nmu.wrpv.ServerHandler;
import za.nmu.wrpv.Subscriber;

public class MenuSubscribe extends Subscribe implements Serializable {
    private final static long serialVersionUID = 21L;
    public static final String key = "menu";

    public MenuSubscribe(String topic, Subscriber subscriber) {
        super(topic, subscriber);
    }

    @NonNull
    @Override
    public String toString() {
        return key;
    }
}
