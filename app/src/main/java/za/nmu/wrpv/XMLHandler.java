package za.nmu.wrpv;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import za.nmu.wrpv.messages.History;
import static za.nmu.wrpv.Helpers.*;

public class XMLHandler {

    public static void modifyXML(History history, String fileName, String elementName, Activity activity) throws IOException, TransformerException, ParserConfigurationException, XPathExpressionException, SAXException, XPathExpressionException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(activity.openFileInput(fileName));
        Element root = document.getDocumentElement();
        if (root == null) {
            root = document.createElement(elementName);
            document.appendChild(root);
        }

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        XPathExpression xPathExpression = xPath.compile("//order[id= '"+history.id+"']");
        Element element = (Element) xPathExpression.evaluate(document, XPathConstants.NODE);
        if (element != null) {
            element.getElementsByTagName("acknowledged").item(0).setTextContent(history.acknowledged + "");
            element.getElementsByTagName("ready").item(0).setTextContent(history.ready + "");
            element.getElementsByTagName("cancelled").item(0).setTextContent(history.cancelled + "");
        }

        writeToXML(document, activity.openFileOutput(fileName, MODE_PRIVATE));

        activity.runOnUiThread(() -> HistoryFragment.runLater(fragment -> fragment.requireActivity().runOnUiThread(() -> fragment.adapter.updateState(history))));
    }

    public static void loadFromXML(String fileName, Consumer<Item> consumer, Activity activity) {
        if (fileExists(activity,fileName)) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(activity.openFileInput(fileName));

                XPath xPath = XPathFactory.newInstance().newXPath();
                XPathExpression xPathExpression = xPath.compile("//item");
                NodeList nodeList = (NodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element element = (Element) nodeList.item(i);
                    String name = element.getElementsByTagName("name").item(0).getTextContent();
                    String description = element.getElementsByTagName("description").item(0).getTextContent();
                    String imageName = element.getElementsByTagName("imageName").item(0).getTextContent();
                    String cost = element.getElementsByTagName("cost").item(0).getTextContent();
                    String quantity = element.getElementsByTagName("quantity").item(0).getTextContent();
                    Item item = new Item(name, description, null, imageName, Double.parseDouble(cost), Integer.parseInt(quantity));

                    consumer.accept(item);
                }
            } catch (XPathExpressionException | SAXException | ParserConfigurationException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadHistoryFromXML(String fileName, Consumer<History> consumer, Activity activity) {
        if (fileExists(activity,fileName)) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(activity.openFileInput(fileName));

                XPath xPath = XPathFactory.newInstance().newXPath();
                XPathExpression xPathExpression = xPath.compile("//order");
                NodeList nodeList = (NodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element element = (Element) nodeList.item(i);
                    String id = element.getElementsByTagName("id").item(0).getTextContent();
                    String date = element.getElementsByTagName("date").item(0).getTextContent();
                    String time = element.getElementsByTagName("time").item(0).getTextContent();
                    String items = element.getElementsByTagName("items").item(0).getTextContent();
                    String tel = element.getElementsByTagName("telephoneNumber").item(0).getTextContent();
                    String total = element.getElementsByTagName("total").item(0).getTextContent();
                    String acknowledged = element.getElementsByTagName("acknowledged").item(0).getTextContent();
                    String ready = element.getElementsByTagName("ready").item(0).getTextContent();
                    String cancelled = element.getElementsByTagName("cancelled").item(0).getTextContent();
                    History history = new History(date, time, items, tel, total);
                    history.acknowledged = Boolean.parseBoolean(acknowledged);
                    history.id = Integer.parseInt(id);
                    history.ready = Boolean.parseBoolean(ready);
                    history.cancelled = Boolean.parseBoolean(cancelled);
                    consumer.accept(history);
                }
            } catch (XPathExpressionException | SAXException | ParserConfigurationException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void appendToXML(Order order, String fileName, String elementName, Activity activity) throws TransformerException, ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            if (fileExists(activity, fileName)) {
                document = builder.parse(activity.openFileInput(fileName));
            }
            else {
                document = builder.newDocument();
            }

            Element root = document.getDocumentElement();

            if (root == null) {
                root = document.createElement(elementName);
                document.appendChild(root);
            }

            Element element = createOrderElement(document, Order.id, order.dateTime, order.telNum, order.items, order.total);
            root.appendChild(element);

            writeToXML(document, activity.openFileOutput(fileName, MODE_PRIVATE));
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Element createTextElement(Document doc, String name, String text) {
        Text textNode = doc.createTextNode(text);
        Element element = doc.createElement(name);
        element.appendChild(textNode);
        return element;
    }

    public static Element createItemElement(Document doc, String name, String description, String imageName, double cost, int quantity) {
        Element nameText = createTextElement(doc, "name", name);
        Element descriptionText = createTextElement(doc, "description", description);
        Element imageNameText = createTextElement(doc, "imageName", imageName);
        Element costText = createTextElement(doc, "cost", cost + "");
        Element quantityText = createTextElement(doc, "quantity", quantity + "");

        Element itemElement = doc.createElement("item");
        itemElement.appendChild(nameText);
        itemElement.appendChild(descriptionText);
        itemElement.appendChild(imageNameText);
        itemElement.appendChild(costText);
        itemElement.appendChild(quantityText);
        return itemElement;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Element createOrderElement(Document doc, int id, LocalDateTime dateTime, String telNum, List<Item> items, double total) {

        String d = getDefaultFormattedDate(dateTime);
        String t = getDefaultFormattedTime(dateTime);

        Element orderElement = doc.createElement("order");

        Element itemsElement = doc.createElement("items");
        for (Item item: items) {
            Element itemElement = createItemElement(doc, item.name, item.description, item.imageName, item.cost, item.quantity);
            itemsElement.appendChild(itemElement);
        }
        Element idText = createTextElement(doc, "id", id + "");
        Element dateText = createTextElement(doc, "date", d);
        Element timeText = createTextElement(doc, "time", t);
        Element telNumText = createTextElement(doc, "telephoneNumber", telNum);
        Element totalText = createTextElement(doc, "total", total+ "");
        Element acknowledgedText = createTextElement(doc, "acknowledged", "false");
        Element readyText = createTextElement(doc, "ready", "false");
        Element cancelledText = createTextElement(doc, "cancelled", "false");

        orderElement.appendChild(idText);
        orderElement.appendChild(dateText);
        orderElement.appendChild(timeText);
        orderElement.appendChild(telNumText);
        orderElement.appendChild(itemsElement);
        orderElement.appendChild(totalText);
        orderElement.appendChild(acknowledgedText);
        orderElement.appendChild(readyText);
        orderElement.appendChild(cancelledText);

        return orderElement;
    }

    public static void writeToXML(Document doc, FileOutputStream fos) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), new StreamResult(fos));
    }
}
