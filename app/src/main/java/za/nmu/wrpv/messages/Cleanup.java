package za.nmu.wrpv.messages;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import za.nmu.wrpv.HistoryFragment;
import za.nmu.wrpv.MainActivity;
import za.nmu.wrpv.XMLHandler;

public class Cleanup extends Message {
    @Override
    public void apply(Object handler) {
        HistoryFragment.runLater(fragment -> {
            for (History history: fragment.adapter.histories) {
                System.out.println("------------------------" + history.ready + " " + history.acknowledged + " " + history.cancelled);
                if (!history.ready && !history.acknowledged) {
                    history.cancelled = true;
                    System.out.println("---------------------- ORDER CANCELLED " + history.id);
                    try {
                        XMLHandler.modifyXML(history, OrderPublish.fileName, "orders", fragment.requireActivity());
                    } catch (IOException | TransformerException | ParserConfigurationException | XPathExpressionException | SAXException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
