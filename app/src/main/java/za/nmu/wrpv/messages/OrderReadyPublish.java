package za.nmu.wrpv.messages;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import za.nmu.wrpv.HistoryFragment;
import za.nmu.wrpv.MainActivity;
import za.nmu.wrpv.Notification;
import za.nmu.wrpv.XMLHandler;

public class OrderReadyPublish extends Publish implements Serializable {
    private static final long serialVersionUID = 50L;
    public static final String key = "orderReady";
    public OrderReadyPublish(Object publisher, String topic, Map<String, Object> params) {
        super(publisher, topic, params);
    }

    @Override
    public void apply(Object handler) {
        MainActivity.runLater(activity -> {
            Notification.displayNotification(activity);
            History history = new History(null, null, null, null, null);
            history.acknowledged = false;
            history.ready = true;
            history.id = (int) publisher;
            try {
                XMLHandler.modifyXML(history, OrderPublish.fileName, "orders", activity);
                HistoryFragment.runLater(fragment -> Notification.cancel(fragment.requireContext()));
            } catch (IOException | TransformerException | ParserConfigurationException | XPathExpressionException | SAXException e) {
                e.printStackTrace();
            }
        });
    }
}
