package za.nmu.wrpv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import za.nmu.wrpv.messages.MenuSubscribe;
import za.nmu.wrpv.messages.OrderPublish;
import za.nmu.wrpv.messages.R;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ServerHandler.activity = this;
        ServerHandler.start();
        ServerHandler.subscribe(new MenuSubscribe(MenuSubscribe.key,null));

        RecyclerView rvMenuItems = findViewById(R.id.rv_menu_items);
        rvMenuItems.setAdapter(MenuItems.adapter);
        rvMenuItems.setLayoutManager(new LinearLayoutManager(this));
        rvMenuItems.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    public void onConfirmOrder(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        double total;
        List<Item> items = MenuItems.adapter.items.stream().filter(item -> item.quantity > 0).collect(Collectors.toList());
        total = items.stream().mapToDouble(item -> item.cost * item.quantity).sum();
        if (total != 0D)
            intent.putExtra("total", total);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            deleteFile(MenuItems.fileName);

            Order order = new Order();
            order.dateTime = new Date();
            order.telNum = "034 948 3331";
            order.items = MenuItems.adapter.items.stream().filter(item -> item.quantity > 0).collect(Collectors.toList());
            order.items.stream().forEach(item -> {
                order.total += item.cost * item.quantity;
            });

            XMLHandler.appendToXML(order, MenuItems.fileName, MenuItems.elementName);
        } catch (TransformerException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServerHandler.stop();
    }
}