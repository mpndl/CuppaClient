package za.nmu.wrpv.messages;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import za.nmu.wrpv.Item;
import za.nmu.wrpv.MenuItems;
import za.nmu.wrpv.ServerHandler;

public class MenuPublish extends Publish implements Serializable {
    private final static long serialVersionUID = 32L;
    public static final String key = "menu";
    public MenuPublish(Object publisher, String topic, Map<String, Object> params) {
        super(publisher, topic, params);
    }

    @Override
    public void apply(Object handler) {
        ServerHandler.activity.runOnUiThread(() -> MenuItems.replace((List<Item>) params.get(key)));
    }
}
