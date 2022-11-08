package za.nmu.wrpv.messages;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import za.nmu.wrpv.HistoryAdapter;
import za.nmu.wrpv.HistoryFragment;
import za.nmu.wrpv.MenuItems;
import za.nmu.wrpv.Order;
import za.nmu.wrpv.ServerHandler;
import za.nmu.wrpv.XMLHandler;

public class OrderPublish extends Publish implements Serializable {
    private final static long serialVersionUID = 41L;
    public static final String key = "order";
    public static final String fileName = "orders.xml";
    public static Map<String, Thread> ackTreads = new ConcurrentHashMap<>();

    public OrderPublish(Object publisher, String topic, Map<String, Object> params) {
        super(publisher, topic, params);
    }

    @Override
    public void apply(Object handler) {
        params = new HashMap<>();
        topic = key;

        Order order = new Order();
        order.dateTime = new Date();
        order.telNum = "034 948 3331";
        order.items = MenuItems.adapter.items.stream().filter(item -> item.quantity > 0).collect(Collectors.toList());
        order.items.stream().forEach(item -> {
            order.total += item.cost * item.quantity;
        });

        Order.id++;

        try {
            XMLHandler.appendToXML(order, fileName, "orders");
        } catch (TransformerException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        @SuppressLint("SimpleDateFormat") String t = new SimpleDateFormat("HH:mm").format(order.dateTime);
        @SuppressLint("SimpleDateFormat") String d = new SimpleDateFormat("yyyy-MM-dd").format(order.dateTime);
        String tel = order.telNum;
        String items = order.items.stream().map(item -> "\t\t\t" + item.name + "\n\t\t\t" + item.description
                + "\n\t\t\t" + item.imageName + "\n\t\t\t" + item.cost + "\n\t\t\t" + item.quantity).collect(Collectors.joining());
        String total = order.total + "";

        History history = new History(d, t, items, tel, total);
        history.id = Order.id;

        HistoryFragment.adapter.add(history);

        params.put(key, order);

        OrderPublish message = new OrderPublish(Order.id, topic, params);
        ServerHandler.publish(message);
    }
}
