package za.nmu.wrpv;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import za.nmu.wrpv.messages.OrderPublish;
import za.nmu.wrpv.messages.R;

public class MainActivity extends AppCompatActivity {
    public List<Fragment> fragments;
    private static final Runner<MainActivity> RUNNER = new Runner<>();
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RUNNER.setParam(this);
        RUNNER.setRunWhen(activity -> true);
        RUNNER.start();

        ServerHandler.start();

        //deleteFile(OrderPublish.fileName);
        //deleteFile(MenuItems.fileName);

        SharedPreferences preferences = this.getPreferences(MODE_PRIVATE);
        Order.id = preferences.getInt("orderID", Order.id);
        System.out.println("--------------------------------------------- RETRIEVED ORDER_ID -> " + Order.id);

        //Retrieves "total", if present, supplied by the MenuActivity
        Optional.of(getIntent())
                .map(Intent::getExtras)
                .map(bundle -> bundle.getDouble("total"))
                .ifPresentOrElse(this::setupViewPager, () -> setupViewPager(-1));

        HistoryFragment.runLater(fragment -> {
            fragment.adapter.histories.clear();
            XMLHandler.loadHistoryFromXML(OrderPublish.fileName, history ->
                    fragment.requireActivity().runOnUiThread(() -> fragment.adapter.add(history)), this);
        });

    }

    private void setupViewPager(double total) {
        ViewPager2 viewPager2 = findViewById(R.id.view_pager2);
        fragments = Arrays.asList(MainFragment.newInstance(total), HistoryFragment.newInstance());
        PagerAdapter adapter = new PagerAdapter(this, fragments);
        viewPager2.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        preferences.edit().putInt("orderID", Order.id).apply();
        System.out.println("--------------------------------------------- SAVED ORDER_ID -> " + Order.id);
    }

    public static void runLater(Consumer<MainActivity> consumer) {
        RUNNER.runLater(consumer);
    }
}