package za.nmu.wrpv.messages;

import za.nmu.wrpv.ServerHandler;

public class Stop extends Message {
    private static final long serialVersionUID = 4L;

    @Override
    public void apply(Object handler) {
        ServerHandler.stop();
    }
}