package com.liang.goldtracker.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.liang.goldtracker.R;
import com.liang.goldtracker.viewmodel.SharedViewModel;

import java.io.IOException;

public class DashboardFragment extends Fragment {

    private SharedViewModel viewModel;
    private GoldPriceAdapter adapter;

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout layoutError;
    private TextView tvError;
    private Button btnRetry;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        recyclerView = view.findViewById(R.id.rv_gold_prices);
        progressBar  = view.findViewById(R.id.progress_bar);
        layoutError  = view.findViewById(R.id.layout_error);
        tvError      = view.findViewById(R.id.tv_error);
        btnRetry     = view.findViewById(R.id.btn_retry);

        adapter = new GoldPriceAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        adapter.setPinnedKeys(viewModel.getPinnedKeys());
        adapter.setPinListener((key, isPinned) -> {
            viewModel.togglePin(key);
            adapter.setPinnedKeys(viewModel.getPinnedKeys());
            if (viewModel.getGoldPrices().getValue() != null) {
                adapter.submitData(viewModel.getGoldPrices().getValue());
            }
        });

        viewModel.getGoldPrices().observe(getViewLifecycleOwner(), prices -> {
            if (prices != null && !prices.isEmpty()) {
                adapter.setPinnedKeys(viewModel.getPinnedKeys());
                adapter.submitData(prices);
                recyclerView.setVisibility(View.VISIBLE);
                layoutError.setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (loading) {
                if (adapter.getItemCount() == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            } else {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error == null) return;

            layoutError.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            btnRetry.setVisibility(View.VISIBLE);

            if (error instanceof IOException) {
                tvError.setText("Không có kết nối mạng.\nVui lòng kiểm tra lại và thử lại.");
            } else {
                tvError.setText("Không thể tải dữ liệu:\n" + error.getMessage());
            }

            viewModel.clearError();
        });

        swipeRefresh.setOnRefreshListener(() -> viewModel.fetchAllPrices());

        btnRetry.setOnClickListener(v -> {
            layoutError.setVisibility(View.GONE);
            viewModel.fetchAllPrices();
        });
    }
}
