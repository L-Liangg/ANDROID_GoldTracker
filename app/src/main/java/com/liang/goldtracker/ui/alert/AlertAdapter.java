package com.liang.goldtracker.ui.alert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.liang.goldtracker.R;
import com.liang.goldtracker.data.db.AlertEntity;
import com.liang.goldtracker.data.model.GoldPrice;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDelete(AlertEntity item);
    }

    private List<AlertEntity> items = new ArrayList<>();
    private Map<String, GoldPrice> currentPrices;
    private OnDeleteClickListener deleteListener;

    private static final NumberFormat VND_FORMAT = NumberFormat.getInstance(new Locale("vi", "VN"));
    private static final NumberFormat USD_FORMAT = NumberFormat.getInstance(Locale.US);

    public void setDeleteListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    public void submitList(List<AlertEntity> data) {
        this.items = data != null ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setCurrentPrices(Map<String, GoldPrice> prices) {
        this.currentPrices = prices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alert_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlertEntity alert = items.get(position);
        boolean isVnd = "VND".equals(alert.getCurrency());

        holder.tvGoldKey.setText(alert.getGoldKey());
        holder.tvUnit.setText(alert.getUnit() + " • " + alert.getCurrency());

        // Giá hiện tại
        if (currentPrices != null && currentPrices.containsKey(alert.getGoldKey())) {
            GoldPrice price = currentPrices.get(alert.getGoldKey());
            holder.tvCurrentBuy.setText(price.getBuyPrice() == 0
                    ? "Không có dữ liệu"
                    : formatPrice(price.getBuyPrice(), isVnd));
            holder.tvCurrentSell.setText(price.getSellPrice() == 0
                    ? "Không có dữ liệu"
                    : formatPrice(price.getSellPrice(), isVnd));
        } else {
            holder.tvCurrentBuy.setText("--");
            holder.tvCurrentSell.setText("--");
        }

        // Ngưỡng
        holder.tvBuyThreshold.setText(alert.getBuyThreshold() == 0
                ? "Không đặt"
                : formatPrice(alert.getBuyThreshold(), isVnd));
        holder.tvSellThreshold.setText(alert.getSellThreshold() == 0
                ? "Không đặt"
                : formatPrice(alert.getSellThreshold(), isVnd));

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(alert);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    private String formatPrice(double price, boolean isVnd) {
        if (isVnd) return VND_FORMAT.format(price) + " ₫";
        return "$" + USD_FORMAT.format(price);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGoldKey, tvUnit, tvCurrentBuy, tvCurrentSell;
        TextView tvBuyThreshold, tvSellThreshold;
        MaterialButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGoldKey        = itemView.findViewById(R.id.tv_gold_key);
            tvUnit           = itemView.findViewById(R.id.tv_unit);
            tvCurrentBuy     = itemView.findViewById(R.id.tv_current_buy);
            tvCurrentSell    = itemView.findViewById(R.id.tv_current_sell);
            tvBuyThreshold   = itemView.findViewById(R.id.tv_buy_threshold);
            tvSellThreshold  = itemView.findViewById(R.id.tv_sell_threshold);
            btnDelete        = itemView.findViewById(R.id.btn_delete);
        }
    }
}
