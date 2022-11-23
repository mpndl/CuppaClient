package za.nmu.wrpv;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.divider.MaterialDividerItemDecoration;

import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import za.nmu.wrpv.messages.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    public static HistoryAdapter adapter;
    private static final BlockingDeque<Run> runs = new LinkedBlockingDeque<>();
    private Thread thread;

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        adapter = new HistoryAdapter(new ArrayList<>());
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        thread = new Thread(() -> {
           do {
               try {
                   if (getActivity() == null || !isAdded() || getView() == null || !isVisible()) {
                       continue;
                   }
                   Run run = runs.take();
                   if (getActivity() == null || !isAdded() || getView() == null || !isVisible()) {
                       runs.add(run);
                       continue;
                   }
                   System.out.println("------------------------------- RAN");
                   getActivity().runOnUiThread(() -> run.run(this));
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }while (true);
        });
        thread.start();

        //Notification.cancel(requireContext());
    }

    public void setupRecyclerView() {
        RecyclerView rvHistory = getView().findViewById(R.id.rv_history);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        rvHistory.setAdapter(adapter);
        rvHistory.setLayoutManager(manager);

        rvHistory.addItemDecoration(new MaterialDividerItemDecoration( getContext(), null, MaterialDividerItemDecoration.VERTICAL));
        if (adapter.getItemCount() > 0)
            rvHistory.smoothScrollToPosition(HistoryFragment.adapter.getItemCount() - 1);
    }

    public static void runLater(Run run) {
        runs.add(run);
        System.out.println("------------------------- ADDED TO RUNS");
    }
}