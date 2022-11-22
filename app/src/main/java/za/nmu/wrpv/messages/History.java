package za.nmu.wrpv.messages;

public class History {
    public int id;
    public String date;
    public String time;
    public String items;
    public String tel;
    public String total;
    public boolean acknowledged = false;
    public boolean ready = false;
    public boolean cancelled = false;

    public History(String date, String time, String items, String tel, String total) {
        this.date = date;
        this.time = time;
        this.items = items;
        this.tel = tel;
        this.total = total;
    }
}
