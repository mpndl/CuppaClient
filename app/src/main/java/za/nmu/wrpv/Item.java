package za.nmu.wrpv;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Item implements Serializable {
    private final static long serialVersionUID = 20L;
    public String name;
    public String description;
    public byte[] image;
    public String currencyCode = "ZAR";
    public String imageName;
    public double cost;
    public int quantity;

    public Item(String name, String description, byte[] image, String imageName, double cost, int quantity) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.imageName = imageName;
        this.cost = cost;
        this.quantity = quantity;
    }

    @NonNull
    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cost=" + cost +
                ", currencyCode='" + currencyCode + '\'' +
                ", quantity=" + quantity +
                '}' + '\n';
    }

    public static List<Item> clone(List<Item> list) {
        List<Item> copy = new ArrayList<>();
        for (Item item: list) {
            Item temp = new Item(item.name, item.description, item.image, item.imageName, item.cost, item.quantity);
            copy.add(temp);
        }
        return copy;
    }
}
