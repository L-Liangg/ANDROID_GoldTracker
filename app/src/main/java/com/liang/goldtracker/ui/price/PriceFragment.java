package com.liang.goldtracker.ui.price;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.liang.goldtracker.R;
import com.liang.goldtracker.data.db.HistoryEntity;
import com.liang.goldtracker.data.model.GoldPrice;
import com.liang.goldtracker.data.model.PriceHistory;
import com.liang.goldtracker.data.repository.GoldRepository;
import com.liang.goldtracker.data.repository.HistoryRepository;
import com.liang.goldtracker.viewmodel.SharedViewModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PriceFragment extends Fragment {

    private SharedViewModel viewModel;

    private AutoCompleteTextView dropdownGoldType;
    private TextInputEditText etQuantity, etPurchasePrice;
    private MaterialButton btnLookup;
    private MaterialCardView cardInfo, cardChart;

    private TextView tvInfoTitle, tvInfoUnit, tvInfoCurrency, tvInfoQuantity;
    private TextView tvInfoBuy, tvInfoBuyChange, tvInfoSell, tvInfoSellChange;
    private TextView tvInfoTotalBuy, tvInfoTotalSell, tvInfoUpdated;
    private View dividerProfit;
    private LinearLayout rowPurchasePrice, rowProfit, rowProfitPercent;
    private TextView tvInfoPurchasePrice, tvInfoProfit, tvInfoProfitPercent;

    private LineChart lineChart;

    private Map<String, GoldPrice> goldPricesData;
    private String selectedKey;
    private LiveData<PriceHistory> currentChartLiveData;

    private static final NumberFormat VND_FORMAT = NumberFormat.getInstance(new Locale("vi", "VN"));
    private static final NumberFormat USD_FORMAT = NumberFormat.getInstance(Locale.US);
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("HH:mm dd/MM/yyyy", new Locale("vi", "VN"));
    private static final SimpleDateFormat CHART_LABEL_FORMAT =
            new SimpleDateFormat("dd/MM", new Locale("vi", "VN"));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_price, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dropdownGoldType  = view.findViewById(R.id.dropdown_gold_type);
        etQuantity        = view.findViewById(R.id.et_quantity);
        etPurchasePrice   = view.findViewById(R.id.et_purchase_price);
        btnLookup         = view.findViewById(R.id.btn_lookup);
        cardInfo          = view.findViewById(R.id.card_info);
        cardChart         = view.findViewById(R.id.card_chart);

        tvInfoTitle         = view.findViewById(R.id.tv_info_title);
        tvInfoUnit          = view.findViewById(R.id.tv_info_unit);
        tvInfoCurrency      = view.findViewById(R.id.tv_info_currency);
        tvInfoQuantity      = view.findViewById(R.id.tv_info_quantity);
        tvInfoBuy           = view.findViewById(R.id.tv_info_buy);
        tvInfoBuyChange     = view.findViewById(R.id.tv_info_buy_change);
        tvInfoSell          = view.findViewById(R.id.tv_info_sell);
        tvInfoSellChange    = view.findViewById(R.id.tv_info_sell_change);
        tvInfoTotalBuy      = view.findViewById(R.id.tv_info_total_buy);
        tvInfoTotalSell     = view.findViewById(R.id.tv_info_total_sell);
        tvInfoUpdated       = view.findViewById(R.id.tv_info_updated);
        dividerProfit       = view.findViewById(R.id.divider_profit);
        rowPurchasePrice    = view.findViewById(R.id.row_purchase_price);
        rowProfit           = view.findViewById(R.id.row_profit);
        rowProfitPercent    = view.findViewById(R.id.row_profit_percent);
        tvInfoPurchasePrice = view.findViewById(R.id.tv_info_purchase_price);
        tvInfoProfit        = view.findViewById(R.id.tv_info_profit);
        tvInfoProfitPercent = view.findViewById(R.id.tv_info_profit_percent);
        lineChart           = view.findViewById(R.id.line_chart);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.getGoldPrices().observe(getViewLifecycleOwner(), prices -> {
            if (prices == null || prices.isEmpty()) return;
            goldPricesData = prices;

            List<String> keys = new ArrayList<>(prices.keySet());
            keys.sort((a, b) -> {
                GoldPrice pa = prices.get(a);
                GoldPrice pb = prices.get(b);
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
            dropdownGoldType.setAdapter(adapter);
            dropdownGoldType.setOnItemClickListener((parent, v, position, id) -> {
                selectedKey = keys.get(position);
                viewModel.setSelectedGoldKey(selectedKey);
            });

            // Restore lựa chọn cũ
            String savedKey = viewModel.getSelectedGoldKey();
            if (savedKey != null && prices.containsKey(savedKey)) {
                selectedKey = savedKey;
                dropdownGoldType.setText(savedKey, false);
                String qtyStr = etQuantity.getText() != null
                        ? etQuantity.getText().toString().trim() : "";
                double qty = qtyStr.isEmpty() ? 1.0 : Double.parseDouble(qtyStr);
                GoldPrice price = prices.get(selectedKey);
                if (price != null) {
                    showInfo(price, qty);
                    loadChart(price.getTypeCode());
                }
            }
        });

        btnLookup.setOnClickListener(v -> {
            if (selectedKey == null || goldPricesData == null) return;

            String qtyStr = etQuantity.getText() != null
                    ? etQuantity.getText().toString().trim() : "";
            double qty = qtyStr.isEmpty() ? 1.0 : Double.parseDouble(qtyStr);

            // Fetch giá mới nhất cho loại đang chọn
            GoldPrice cachedPrice = goldPricesData.get(selectedKey);
            if (cachedPrice == null) return;

            new Thread(() -> {
                try {
                    GoldPrice freshPrice = GoldRepository.getSinglePrice(cachedPrice.getTypeCode());
                    requireActivity().runOnUiThread(() -> {
                        showInfo(freshPrice, qty);
                        loadChart(freshPrice.getTypeCode());
                    });
                } catch (Exception e) {
                    // Fallback dùng data cache nếu fetch lỗi
                    requireActivity().runOnUiThread(() -> {
                        showInfo(cachedPrice, qty);
                        loadChart(cachedPrice.getTypeCode());
                    });
                }
            }).start();
        });
    }

    private void showInfo(GoldPrice price, double qty) {
        cardInfo.setVisibility(View.VISIBLE);
        boolean isVnd = "VND".equals(price.getCurrency());

        tvInfoTitle.setText(price.getName() + " (" + price.getUnit() + ")");
        tvInfoUnit.setText(price.getUnit());
        tvInfoCurrency.setText(price.getCurrency());
        tvInfoQuantity.setText(String.valueOf(qty));

        tvInfoBuy.setText(formatPrice(price.getBuyPrice(), isVnd));
        tvInfoSell.setText(formatPrice(price.getSellPrice(), isVnd));
        tvInfoTotalBuy.setText(formatPrice(price.getBuyPrice() * qty, isVnd));
        tvInfoTotalSell.setText(formatPrice(price.getSellPrice() * qty, isVnd));
        tvInfoUpdated.setText(DATE_FORMAT.format(new Date(price.getUpdatedAt() * 1000)));

        // Buy change: tăng xanh, giảm đỏ
        bindChange(tvInfoBuyChange, price.getBuyChange(), isVnd, true);
        // Sell change: tăng đỏ, giảm xanh
        bindChange(tvInfoSellChange, price.getSellChange(), isVnd, false);

        // Lãi/lỗ
        String purchaseStr = etPurchasePrice.getText() != null
                ? etPurchasePrice.getText().toString().trim() : "";
        if (!purchaseStr.isEmpty() && price.getSellPrice() > 0) {
            double purchasePrice = Double.parseDouble(purchaseStr);
            double profit = (price.getSellPrice() - purchasePrice) * qty;
            double profitPercent = ((price.getSellPrice() - purchasePrice) / purchasePrice) * 100;

            int color = profit >= 0
                    ? getResources().getColor(R.color.buy_color, null)
                    : getResources().getColor(R.color.sell_color, null);

            tvInfoPurchasePrice.setText(formatPrice(purchasePrice, isVnd));
            tvInfoProfit.setText((profit >= 0 ? "+" : "") + formatPrice(profit, isVnd));
            tvInfoProfit.setTextColor(color);
            tvInfoProfitPercent.setText(String.format(Locale.getDefault(), "%+.2f%%", profitPercent));
            tvInfoProfitPercent.setTextColor(color);

            dividerProfit.setVisibility(View.VISIBLE);
            rowPurchasePrice.setVisibility(View.VISIBLE);
            rowProfit.setVisibility(View.VISIBLE);
            rowProfitPercent.setVisibility(View.VISIBLE);
        } else {
            dividerProfit.setVisibility(View.GONE);
            rowPurchasePrice.setVisibility(View.GONE);
            rowProfit.setVisibility(View.GONE);
            rowProfitPercent.setVisibility(View.GONE);
        }

        // Lưu lịch sử
        HistoryRepository.insert(new HistoryEntity(
                price.getName(), price.getUnit(), price.getCurrency(), qty,
                price.getBuyPrice(), price.getSellPrice(),
                price.getBuyPrice() * qty, price.getSellPrice() * qty,
                System.currentTimeMillis()
        ));
    }

    private void bindChange(TextView tv, double change, boolean isVnd, boolean buyMode) {
        if (change == 0) {
            tv.setVisibility(View.GONE);
            return;
        }
        tv.setVisibility(View.VISIBLE);
        String prefix = change > 0 ? "+" : "";
        tv.setText(prefix + formatPrice(change, isVnd));
        boolean isPositive = change > 0;
        boolean showGreen = buyMode == isPositive;
        tv.setTextColor(requireContext().getColor(
                showGreen ? R.color.buy_color : R.color.sell_color));
    }

    private void loadChart(String typeCode) {
        if (currentChartLiveData != null) {
            currentChartLiveData.removeObservers(getViewLifecycleOwner());
        }

        currentChartLiveData = GoldRepository.getPriceHistory(typeCode, 7);
        currentChartLiveData.observe(getViewLifecycleOwner(), history -> {
            if (history == null || history.getHistory().isEmpty()) {
                cardChart.setVisibility(View.GONE);
                return;
            }

            List<Entry> buyEntries  = new ArrayList<>();
            List<Entry> sellEntries = new ArrayList<>();
            List<String> labels     = new ArrayList<>();

            List<PriceHistory.PriceEntry> entries = history.getHistory();
            // API trả về mới nhất trước, đảo lại cho chart từ cũ → mới
            for (int i = entries.size() - 1; i >= 0; i--) {
                PriceHistory.PriceEntry e = entries.get(i);
                int index = entries.size() - 1 - i;
                buyEntries.add(new Entry(index, (float) e.getBuy()));
                sellEntries.add(new Entry(index, (float) e.getSell()));
                labels.add(e.getDate().substring(5)); // MM-dd
            }

            cardChart.setVisibility(View.VISIBLE);
            setupChart(buyEntries, sellEntries, labels);
        });
    }

    private void setupChart(List<Entry> buyEntries, List<Entry> sellEntries, List<String> labels) {
        LineDataSet buySet = new LineDataSet(buyEntries, "Mua");
        buySet.setColor(Color.parseColor("#2E7D32"));
        buySet.setCircleColor(Color.parseColor("#2E7D32"));
        buySet.setLineWidth(2f);
        buySet.setCircleRadius(3f);
        buySet.setDrawValues(false);
        buySet.setMode(LineDataSet.Mode.LINEAR);

        LineDataSet sellSet = new LineDataSet(sellEntries, "Bán");
        sellSet.setColor(Color.parseColor("#C62828"));
        sellSet.setCircleColor(Color.parseColor("#C62828"));
        sellSet.setLineWidth(2f);
        sellSet.setCircleRadius(3f);
        sellSet.setDrawValues(false);
        sellSet.setMode(LineDataSet.Mode.LINEAR);

        lineChart.setData(new LineData(buySet, sellSet));
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(true);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);

        lineChart.animateX(500);
        lineChart.invalidate();
    }

    private String formatPrice(double price, boolean isVnd) {
        if (isVnd) return VND_FORMAT.format(price) + " ₫";
        return "$" + USD_FORMAT.format(price);
    }
}
