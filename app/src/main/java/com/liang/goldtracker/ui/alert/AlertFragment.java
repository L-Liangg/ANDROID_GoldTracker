package com.liang.goldtracker.ui.alert;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.liang.goldtracker.R;
import com.liang.goldtracker.data.db.AlertEntity;
import com.liang.goldtracker.data.model.GoldPrice;
import com.liang.goldtracker.data.repository.AlertRepository;
import com.liang.goldtracker.util.AlarmScheduler;
import com.liang.goldtracker.viewmodel.SharedViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AlertFragment extends Fragment {

    private SharedViewModel viewModel;
    private AlertAdapter adapter;
    private TextView tvEmpty, tvScheduleInfo;
    private Map<String, GoldPrice> currentPrices;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_alerts);
        tvEmpty        = view.findViewById(R.id.tv_empty);
        tvScheduleInfo = view.findViewById(R.id.tv_schedule_info);
        MaterialButton btnAdd      = view.findViewById(R.id.btn_add);
        MaterialButton btnSchedule = view.findViewById(R.id.btn_schedule);

        adapter = new AlertAdapter();
        adapter.setDeleteListener(item ->
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Xóa theo dõi?")
                        .setMessage(item.getGoldKey())
                        .setPositiveButton("Xóa", (d, w) -> AlertRepository.delete(item.getGoldKey()))
                        .setNegativeButton("Hủy", null)
                        .show()
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe alerts
        AlertRepository.getAll().observe(getViewLifecycleOwner(), alerts -> {
            adapter.submitList(alerts);
            boolean isEmpty = alerts == null || alerts.isEmpty();
            tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });

        // Observe giá hiện tại để hiển thị trong card
        viewModel.getGoldPrices().observe(getViewLifecycleOwner(), prices -> {
            currentPrices = prices;
            adapter.setCurrentPrices(prices);
        });

        // Hiện giờ đã đặt
        updateScheduleInfo();

        // Nút cài giờ
        btnSchedule.setOnClickListener(v -> showTimePickerDialog());

        // Nút thêm
        btnAdd.setOnClickListener(v -> showAddAlertDialog());
    }

    private void updateScheduleInfo() {
        int hour   = AlarmScheduler.getSavedHour(requireContext());
        int minute = AlarmScheduler.getSavedMinute(requireContext());
        tvScheduleInfo.setText(String.format(Locale.getDefault(),
                "Tự động kiểm tra lúc %02d:%02d mỗi ngày", hour, minute));
    }

    private void showTimePickerDialog() {
        int hour   = AlarmScheduler.getSavedHour(requireContext());
        int minute = AlarmScheduler.getSavedMinute(requireContext());

        new TimePickerDialog(requireContext(), (timePicker, h, m) -> {
            AlarmScheduler.schedule(requireContext(), h, m);
            updateScheduleInfo();
        }, hour, minute, true).show();
    }

    private void showAddAlertDialog() {
        if (currentPrices == null || currentPrices.isEmpty()) return;

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_alert, null);

        AutoCompleteTextView dropdown = dialogView.findViewById(R.id.dropdown_gold_type);
        TextInputEditText etBuy  = dialogView.findViewById(R.id.et_buy_threshold);
        TextInputEditText etSell = dialogView.findViewById(R.id.et_sell_threshold);

        // Sort giống PriceFragment
        List<String> keys = new ArrayList<>(currentPrices.keySet());
        keys.sort((a, b) -> {
            GoldPrice pa = currentPrices.get(a);
            GoldPrice pb = currentPrices.get(b);
            boolean aNoData = pa == null || (pa.getBuyPrice() == 0 && pa.getSellPrice() == 0);
            boolean bNoData = pb == null || (pb.getBuyPrice() == 0 && pb.getSellPrice() == 0);
            if (aNoData != bNoData) return aNoData ? 1 : -1;
            String ua = pa != null ? pa.getUnit() : "";
            String ub = pb != null ? pb.getUnit() : "";
            int orderA = ua.equals("Ounce") ? 0 : ua.equals("Lượng") ? 1 : 2;
            int orderB = ub.equals("Ounce") ? 0 : ub.equals("Lượng") ? 1 : 2;
            if (orderA != orderB) return orderA - orderB;
            return a.compareTo(b);
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, keys);
        dropdown.setAdapter(adapter);

        final String[] selectedKey = {null};
        dropdown.setOnItemClickListener((parent, v, position, id) ->
                selectedKey[0] = keys.get(position));

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Thêm theo dõi")
                .setView(dialogView)
                .setPositiveButton("Thêm", (d, w) -> {
                    if (selectedKey[0] == null) return;

                    GoldPrice price = currentPrices.get(selectedKey[0]);
                    if (price == null) return;

                    String buyStr  = etBuy.getText()  != null ? etBuy.getText().toString().trim()  : "";
                    String sellStr = etSell.getText() != null ? etSell.getText().toString().trim() : "";

                    double buyThreshold  = buyStr.isEmpty()  ? 0 : Double.parseDouble(buyStr);
                    double sellThreshold = sellStr.isEmpty() ? 0 : Double.parseDouble(sellStr);

                    if (buyThreshold == 0 && sellThreshold == 0) return;

                    AlertEntity alert = new AlertEntity(
                            selectedKey[0],
                            price.getName(),
                            price.getUnit(),
                            price.getCurrency(),
                            buyThreshold,
                            sellThreshold,
                            true
                    );
                    AlertRepository.insert(alert);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
