package za.nmu.wrpv;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import za.nmu.wrpv.messages.Cleanup;
import za.nmu.wrpv.messages.Message;
import za.nmu.wrpv.messages.Publish;
import za.nmu.wrpv.messages.Stop;
import za.nmu.wrpv.messages.Subscribe;

public class ServerHandler implements Serializable {
    private static final Map<String, BlockingQueue<Message>> messages = new HashMap<>();
    private static final String serverAddress = "192.168.43.152";

    public static ObjectOutputStream ous;
    public static ObjectInputStream ois;
    private static ServerReader serverReader;
    private static ServerWriter serverWriter;
    private static final String TAG = "cuppano";

    public static void start() {
        if (!running()) {
            serverReader = new ServerReader();
            serverReader.start();
        }
    }

    public static boolean running() {
        return serverReader != null;
    }

    private static class ServerWriter extends Thread implements Serializable {
        @Override
        public void run() {
            try {
                do {
                    Message msg = messages.computeIfAbsent(Message.key, m -> new LinkedBlockingDeque<>()).take();
                    ous.writeObject(msg);
                    ous.flush();
                }while (true);
            }catch (InterruptedException | IOException e) {
                e.printStackTrace();
                serverWriter = null;
            }finally {
                ServerHandler.stop();
            }
        }
    }

    private static class ServerReader extends Thread implements Serializable{
        @Override
        public void run() {
            try {
                Socket connection = new Socket(serverAddress, 5051);
                ois = new ObjectInputStream(connection.getInputStream());
                ous = new ObjectOutputStream(connection.getOutputStream());
                ous.flush();

                serverWriter = new ServerWriter();
                serverWriter.start();

                Message msg;
                do {
                    msg = (Message) ois.readObject();
                    msg.apply(null);
                }while (true);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }finally {
                serverReader = null;
                ServerHandler.stop();
                new Cleanup().apply(null);
            }
        }
    }

    public static void publish(Object publisher,String topic, Map<String, Object> params) {
        Message message = new Publish(publisher, topic, params);
        messages.computeIfAbsent(Message.key, m -> new LinkedBlockingDeque<>()).add(message);
    }

    public static void publish(Publish message) {
        messages.computeIfAbsent(Message.key, m -> new LinkedBlockingDeque<>()).add(message);
    }

    public static void subscribe(String topic) {
        Message message = new Subscribe(topic, null);
        messages.computeIfAbsent(Message.key, m -> new LinkedBlockingDeque<>()).add(message);
    }

    public static void subscribe(Subscribe subscribe) {
        messages.computeIfAbsent(Message.key, m -> new LinkedBlockingDeque<>()).add(subscribe);
    }

    public static void stop() {
        messages.computeIfAbsent(Message.key, m -> new LinkedBlockingDeque<>()).add(new Stop());
    }
}
