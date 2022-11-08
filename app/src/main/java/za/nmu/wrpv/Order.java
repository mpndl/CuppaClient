package za.nmu.wrpv;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private final static long serialVersionUID = 42L;
    public static int id = -1;
    public Date dateTime;
    public String telNum;
    public List<Item> items;
    public double total;
    public boolean acknowledged = false;
    public boolean ready = false;
}
