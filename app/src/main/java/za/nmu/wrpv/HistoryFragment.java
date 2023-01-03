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
import java.util.function.Consumer;
import java.util.function.Predicate;

import za.nmu.wrpv.messages.History;
import za.nmu.wrpv.messages.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    public HistoryAdapter adapter;
    private static final Runner<HistoryFragment> RUNNER = new Runner<>();

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        adapter = new HistoryAdapter(new ArrayList<>(), requireActivity());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();

        Predicate<HistoryFragment> runWhen = fragment ->
                fragment.getActivity() != null && fragment.isAdded() && fragment.getView() != null && fragment.isVisible();
        RUNNER.setParam(this);
        RUNNER.setRunWhen(runWhen);
        RUNNER.start();

        //Notification.cancel(requireContext());
    }

    public void setupRecyclerView() {
        RecyclerView rvHistory = requireView().findViewById(R.id.rv_history);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        rvHistory.setAdapter(adapter);
        rvHistory.setLayoutManager(manager);

        rvHistory.addItemDecoration(new MaterialDividerItemDecoration( requireContext(), null, MaterialDividerItemDecoration.VERTICAL));
        if (adapter.getItemCount() > 0)
            rvHistory.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    public static void runLater(Consumer<HistoryFragment> consumer) {
        RUNNER.runLater(consumer);
    }
}