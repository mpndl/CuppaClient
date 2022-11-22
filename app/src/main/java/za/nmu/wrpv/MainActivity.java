package za.nmu.wrpv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import za.nmu.wrpv.messages.History;
import za.nmu.wrpv.messages.OrderPublish;
import za.nmu.wrpv.messages.R;

public class MainActivity extends AppCompatActivity {
    private Double total = 0D;
    public List<Fragment> fragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ServerHandler.activity = this;
        ServerHandler.start();

        //deleteFile(OrderPublish.fileName);
        //deleteFile(MenuItems.fileName);

        SharedPreferences preferences = this.getPreferences(MODE_PRIVATE);
        Order.id = preferences.getInt("orderID", Order.id);
        System.out.println("--------------------------------------------- RETRIEVED ORDER_ID -> " + Order.id);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                if (bundle.containsKey("total")) {
                    total = bundle.getDouble("total");
                    setupViewPager(total);
                }
                else {
                    setupViewPager(-1);
                    ViewPager2 viewPager2 = findViewById(R.id.view_pager2);
                    viewPager2.setCurrentItem(1);
                }
            }
            else
                setupViewPager(-1);
        }
        else
            setupViewPager(-1);


        HistoryFragment.adapter.histories.clear();
        XMLHandler.loadHistoryFromXML(OrderPublish.fileName, history -> HistoryFragment.adapter.add((History) history));
    }

    private void setupViewPager(double total) {
        ViewPager2 viewPager2 = findViewById(R.id.view_pager2);
        fragments = new ArrayList<>(Arrays.asList(MainFragment.newInstance(total), HistoryFragment.newInstance()));
        PagerAdapter adapter = new PagerAdapter(this, fragments);
        viewPager2.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServerHandler.stop();
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        preferences.edit().putInt("orderID", Order.id).apply();
        System.out.println("--------------------------------------------- SAVED ORDER_ID -> " + Order.id);
    }
}