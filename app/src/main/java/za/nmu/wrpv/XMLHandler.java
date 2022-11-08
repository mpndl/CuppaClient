package za.nmu.wrpv;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

public class XMLHandler {

    public static void modifyXML(History history, String fileName, String elementName) throws IOException, TransformerException, ParserConfigurationException, XPathExpressionException, SAXException, XPathExpressionException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(ServerHandler.activity.openFileInput(fileName));
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
            /*element.getElementsByTagName("id").item(0).setTextContent(history.id + "");
            element.getElementsByTagName("date").item(0).setTextContent(history.date);
            element.getElementsByTagName("time").item(0).setTextContent(history.time);
            element.getElementsByTagName("items").item(0).setTextContent(history.items);
            element.getElementsByTagName("telephoneNumber").item(0).setTextContent(history.tel);
            element.getElementsByTagName("total").item(0).setTextContent(history.total);*/
            element.getElementsByTagName("acknowledged").item(0).setTextContent(history.acknowledged + "");
            element.getElementsByTagName("ready").item(0).setTextContent(history.ready + "");
        }

        writeToXML(document, ServerHandler.activity.openFileOutput(fileName, MODE_PRIVATE));
    }

    public static void loadFromXML(String fileName, Run run) {
        if (fileExists(ServerHandler.activity,fileName)) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(ServerHandler.activity.openFileInput(fileName));

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

                    run.run(item);
                }
            } catch (XPathExpressionException | SAXException | ParserConfigurationException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadHistoryFromXML(String fileName, Run run) {
        if (fileExists(ServerHandler.activity,fileName)) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(ServerHandler.activity.openFileInput(fileName));

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
                    History history = new History(date, time, items, tel, total);
                    history.acknowledged = Boolean.parseBoolean(acknowledged);
                    history.id = Integer.parseInt(id);
                    history.ready = Boolean.parseBoolean(ready);
                    run.run(history);
                }
            } catch (XPathExpressionException | SAXException | ParserConfigurationException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void appendToXML(Order order, String fileName, String elementName) throws TransformerException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            if (fileExists(ServerHandler.activity, fileName)) {
                document = builder.parse(ServerHandler.activity.openFileInput(fileName));
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

            /*Log.i("cuppano", "appendToXML: ---------------------------------------------");
            Log.i("cuppano", "appendToXML: root -> " + root.getNodeName());
            Log.i("cuppano", "appendToXML: element -> " + element.getNodeName());
            Log.i("cuppano", "appendToXML: fileName -> " + fileName);
            Log.i("cuppano", "appendToXML: fileName -> " + root.getChildNodes().getLength());*/

            writeToXML(document, ServerHandler.activity.openFileOutput(fileName, MODE_PRIVATE));
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

    public static Element createOrderElement(Document doc, int id, Date date, String telNum, List<Item> items, double total) {
        @SuppressLint("SimpleDateFormat") String t = new SimpleDateFormat("HH:mm").format(date);
        @SuppressLint("SimpleDateFormat") String d = new SimpleDateFormat("yyyy-MM-dd").format(date);

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

        orderElement.appendChild(idText);
        orderElement.appendChild(dateText);
        orderElement.appendChild(timeText);
        orderElement.appendChild(telNumText);
        orderElement.appendChild(itemsElement);
        orderElement.appendChild(totalText);
        orderElement.appendChild(acknowledgedText);
        orderElement.appendChild(readyText);

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



    public static boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        return file != null && file.exists();
    }

}
