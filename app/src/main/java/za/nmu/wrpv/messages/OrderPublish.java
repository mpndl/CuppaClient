package za.nmu.wrpv.messages;

import static za.nmu.wrpv.Helpers.getDefaultFormattedDate;
import static za.nmu.wrpv.Helpers.getDefaultFormattedTime;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import za.nmu.wrpv.HistoryFragment;
import za.nmu.wrpv.MainActivity;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void apply(Object handler) {
        topic = key;

        Order order = new Order();
        order.dateTime = LocalDateTime.now();
        order.telNum = "034 948 3331";
        order.items = MenuItems.adapter.items.stream().filter(item -> item.quantity > 0).collect(Collectors.toList());
        order.items.forEach(item -> order.total += item.cost * item.quantity);

        Order.id++;

        MainActivity.runLater(activity -> {
            SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
            preferences.edit().putInt("orderID", Order.id).apply();
            System.out.println("--------------------------------------------- SAVED ORDER_ID -> " + Order.id);

            try {
                XMLHandler.appendToXML(order, fileName, "orders", activity);
            } catch (TransformerException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        });

        String tel = order.telNum;
        String items = order.items.stream().map(item -> "\t\t\t" + item.name + "\n\t\t\t" + item.description
                + "\n\t\t\t" + item.imageName + "\n\t\t\t" + item.cost + "\n\t\t\t" + item.quantity).collect(Collectors.joining());
        String total = order.total + "";

        History history = new History(getDefaultFormattedDate(order.dateTime), getDefaultFormattedTime(order.dateTime), items, tel, total);
        history.id = Order.id;

        HistoryFragment.runLater(fragment -> fragment.requireActivity().runOnUiThread(() -> fragment.adapter.add(history)));

        OrderPublish message = new OrderPublish(Order.id, topic, Map.of(key, order));
        ServerHandler.publish(message);
    }
}
