package za.nmu.wrpv.messages;

import androidx.annotation.NonNull;

import java.io.Serializable;

public abstract class Message<H> implements Serializable {
    private static final long serialVersionUID = 0L;
    public static final String key = "message";
    public void apply(H handler) {}

    @NonNull
    @Override
    public String toString() {
        return key;
    }
}
