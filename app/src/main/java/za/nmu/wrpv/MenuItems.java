package za.nmu.wrpv;


import android.app.Activity;

import java.util.ArrayList;
import java.util.List;


public class MenuItems {
    public final static String fileName = "tOrder.xml";
    public final static  String elementName = "orders";
    public static void replace(List<Item> list) {
        MenuActivity.runLater(activity -> {
            deleteExistingImages(list, activity);
            activity.adapter.items.clear();
            activity.adapter.items.addAll(list);
            XMLHandler.loadFromXML(fileName, MenuItems::updateQuantity, activity);
            activity.runOnUiThread(() -> activity.adapter.notifyDataSetChanged());
        });
    }

    private static void deleteExistingImages(List<Item> list, Activity activity) {
        list.forEach(item -> {
            if (item.image != null) activity.deleteFile(item.imageName);
        });
    }

    public static void updateQuantity(Item item) {
        MenuActivity.runLater(activity -> {
            activity.adapter.items.stream()
                    .filter((Item i) -> i.name.equals(item.name))
                    .findAny()
                    .ifPresent((Item i) -> i.quantity = item.quantity);
        });
    }
}
