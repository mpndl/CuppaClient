package za.nmu.wrpv.messages;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import za.nmu.wrpv.HistoryFragment;
import za.nmu.wrpv.ServerHandler;
import za.nmu.wrpv.XMLHandler;

public class Stop extends Message {
    private static final long serialVersionUID = 4L;

    @Override
    public void apply(Object handler) {
        ServerHandler.stop();
    }
}