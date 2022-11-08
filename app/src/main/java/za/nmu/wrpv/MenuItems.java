package za.nmu.wrpv;


import java.util.ArrayList;
import java.util.List;


public class MenuItems {
    public static MenuItemAdapter adapter = new MenuItemAdapter(new ArrayList<>());
    public final static String fileName = "tOrder.xml";
    public final static  String elementName = "orders";
    public static void replace(List<Item> list) {
        deleteExistingImages(list);
        adapter.items.clear();
        adapter.items.addAll(list);
        XMLHandler.loadFromXML(fileName, item -> MenuItems.updateQuantity((Item)item));
        adapter.notifyDataSetChanged();
    }

    private static void deleteExistingImages(List<Item> list) {
        list.stream().forEach(item -> {
            if (item.image != null)
                ServerHandler.activity.deleteFile(item.imageName);
        });
    }

    public static void updateQuantity(Item item) {
        for (Item i: MenuItems.adapter.items) {
            if (i.name.equals(item.name)) {
                i.quantity = item.quantity;
            }
        }
    }
}
