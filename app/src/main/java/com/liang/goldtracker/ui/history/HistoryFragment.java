package com.liang.goldtracker.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.liang.goldtracker.R;
import com.liang.goldtracker.data.repository.HistoryRepository;

public class HistoryFragment extends Fragment {

    private HistoryAdapter adapter;
    private TextView tvEmpty;
    private ExtendedFloatingActionButton fabDeleteAll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_history);
        tvEmpty      = view.findViewById(R.id.tv_empty);
        fabDeleteAll = view.findViewById(R.id.fab_delete_all);

        adapter = new HistoryAdapter();
        adapter.setDeleteListener(item -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Xóa mục này?")
                    .setMessage(item.getGoldType() + " - " + item.getUnit())
                    .setPositiveButton("Xóa", (d, w) -> HistoryRepository.delete(item))
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        HistoryRepository.getAll().observe(getViewLifecycleOwner(), items -> {
            adapter.submitList(items);

            boolean isEmpty = items == null || items.isEmpty();
            tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            fabDeleteAll.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });

        fabDeleteAll.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Xóa tất cả?")
                    .setMessage("Toàn bộ lịch sử sẽ bị xóa vĩnh viễn.")
                    .setPositiveButton("Xóa tất cả", (d, w) -> HistoryRepository.deleteAll())
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }
}