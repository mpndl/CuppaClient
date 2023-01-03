package za.nmu.wrpv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.BlockingDeque;

import za.nmu.wrpv.messages.OrderAcknowledgedSubscribe;
import za.nmu.wrpv.messages.OrderPublish;
import za.nmu.wrpv.messages.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static double total = -1;

    public static MainFragment newInstance(double total) {
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle(1);
        bundle.putDouble("total", total);
        mainFragment.setArguments(bundle);
        return mainFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null)
            total = getArguments().getDouble("total");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton ibSendOrder = requireView().findViewById(R.id.ib_send_order);
        if (total != -1) {
                ibSendOrder.setVisibility(View.VISIBLE);

                TextView tvOrderCost = requireView().findViewById(R.id.tv_order_cost);
                tvOrderCost.setText(total + "");
        }
        else
            ibSendOrder.setVisibility(View.GONE);

        onCreateModifyOrder();
        onSendOrder();
    }

    public void onSendOrder() {
        ImageButton ibSendOrder = requireView().findViewById(R.id.ib_send_order);
        ibSendOrder.setOnClickListener(view -> {
            new OrderPublish(null, null, null).apply(null);
            Snackbar snackbar = Snackbar.make(view, getResources().getString(R.string.orderSent), Snackbar.LENGTH_INDEFINITE);
            snackbar.setTextColor(requireActivity().getColor(R.color.green));
            snackbar.setAction("OK", view1 -> {
                snackbar.dismiss();
            });
            snackbar.setBackgroundTint(Color.BLACK);
            snackbar.show();
            TextView tvOrderCost = requireView().findViewById(R.id.tv_order_cost);

            ibSendOrder.setVisibility(View.GONE);

            tvOrderCost.setText(null);
        });
    }

    public void onCreateModifyOrder() {
        ImageButton ibCreateModifyOrder = requireView().findViewById(R.id.ib_create_modify_order);
        ibCreateModifyOrder.setOnClickListener(view -> {

            Intent intent = new Intent(getContext(), MenuActivity.class);
            startActivity(intent);
        });
    }
}