package za.nmu.wrpv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

import za.nmu.wrpv.messages.History;
import za.nmu.wrpv.messages.MenuSubscribe;
import za.nmu.wrpv.messages.OrderAcknowledgedPublish;
import za.nmu.wrpv.messages.OrderAcknowledgedSubscribe;
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

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                if (bundle.containsKey("total")) {
                    total = bundle.getDouble("total");
                    setupViewPager(total);
                }
                else setupViewPager(-1);
            }
            else
                setupViewPager(-1);
        }
        else
            setupViewPager(-1);


        HistoryFragment.adapter.histories.clear();
        XMLHandler.loadHistoryFromXML(OrderPublish.fileName, history -> {
            HistoryFragment.adapter.add((History) history);
        });
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
    }
}