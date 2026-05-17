package com.liang.goldtracker.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.liang.goldtracker.R;
import com.liang.goldtracker.data.model.GoldPrice;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class GoldPriceAdapter extends RecyclerView.Adapter<GoldPriceAdapter.ViewHolder> {

    public interface OnPinClickListener {
        void onPinClick(String key, boolean isPinned);
    }

    private List<Map.Entry<String, GoldPrice>> items = new ArrayList<>();
    private Set<String> pinnedKeys = new HashSet<>();
    private OnPinClickListener pinListener;

    private static final NumberFormat VND_FORMAT = NumberFormat.getInstance(new Locale("vi", "VN"));
    private static final NumberFormat USD_FORMAT = NumberFormat.getInstance(Locale.US);
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("HH:mm dd/MM/yyyy", new Locale("vi", "VN"));

    public void setPinListener(OnPinClickListener listener) {
        this.pinListener = listener;
    }

    public void setPinnedKeys(Set<String> keys) {
        this.pinnedKeys = keys != null ? keys : new HashSet<>();
    }

    public void submitData(Map<String, GoldPrice> data) {
        items = new ArrayList<>(data.entrySet());
        items.sort((a, b) -> {
            // Pinned lên đầu
            boolean aPinned = pinnedKeys.contains(a.getKey());
            boolean bPinned = pinnedKeys.contains(b.getKey());
            if (aPinned != bPinned) return aPinned ? -1 : 1;

            // Không có data xuống cuối
            boolean aNoData = a.getValue().getBuyPrice() == 0 && a.getValue().getSellPrice() == 0;
            boolean bNoData = b.getValue().getBuyPrice() == 0 && b.getValue().getSellPrice() == 0;
            if (aNoData != bNoData) return aNoData ? 1 : -1;

            // Ounce → Lượng → Chỉ
            String ua = a.getValue().getUnit();
            String ub = b.getValue().getUnit();
            int orderA = ua.equals("Ounce") ? 0 : ua.equals("Lượng") ? 1 : 2;
            int orderB = ub.equals("Ounce") ? 0 : ub.equals("Lượng") ? 1 : 2;
            if (orderA != orderB) return orderA - orderB;

            return a.getKey().compareTo(b.getKey());
        });
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gold_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String, GoldPrice> entry = items.get(position);
        String key = entry.getKey();
        GoldPrice price = entry.getValue();
        boolean isPinned = pinnedKeys.contains(key);
        boolean isVnd = "VND".equals(price.getCurrency());

        holder.tvGoldType.setText(price.getName());
        holder.tvUnit.setText(price.getUnit() + " • " + price.getCurrency());

        if (price.getBuyPrice() == 0 && price.getSellPrice() == 0) {
            holder.tvBuyPrice.setText("Không có dữ liệu");
            holder.tvSellPrice.setText("Không có dữ liệu");
            holder.tvBuyChange.setVisibility(View.GONE);
            holder.tvSellChange.setVisibility(View.GONE);
        } else {
            holder.tvBuyPrice.setText(formatPrice(price.getBuyPrice(), isVnd));
            holder.tvSellPrice.setText(formatPrice(price.getSellPrice(), isVnd));

            // Buy change: tăng → xanh, giảm → đỏ
            bindChange(holder.tvBuyChange, price.getBuyChange(), isVnd, true);
            // Sell change: tăng → đỏ, giảm → xanh
            bindChange(holder.tvSellChange, price.getSellChange(), isVnd, false);
        }

        holder.tvUpdatedAt.setText("Cập nhật: " +
                DATE_FORMAT.format(new Date(price.getUpdatedAt() * 1000)));

        holder.btnPin.setIconResource(isPinned
                ? R.drawable.ic_star_filled
                : R.drawable.ic_star_outline);

        holder.btnPin.setOnClickListener(v -> {
            if (pinListener != null) pinListener.onPinClick(key, isPinned);
        });
    }

    private void bindChange(TextView tv, double change, boolean isVnd, boolean buyMode) {
        if (change == 0) {
            tv.setVisibility(View.GONE);
            return;
        }
        tv.setVisibility(View.VISIBLE);
        String prefix = change > 0 ? "+" : "";
        tv.setText(prefix + formatPrice(change, isVnd));

        // buyMode: tăng xanh giảm đỏ / sellMode: tăng đỏ giảm xanh
        boolean isPositive = change > 0;
        boolean showGreen = buyMode ? isPositive : !isPositive;
        tv.setTextColor(tv.getContext().getColor(
                showGreen ? R.color.buy_color : R.color.sell_color));
    }

    private String formatPrice(double price, boolean isVnd) {
        if (isVnd) return VND_FORMAT.format(price) + " ₫";
        return "$" + USD_FORMAT.format(price);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGoldType, tvUnit, tvBuyPrice, tvSellPrice;
        TextView tvBuyChange, tvSellChange, tvUpdatedAt;
        MaterialButton btnPin;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGoldType  = itemView.findViewById(R.id.tv_gold_type);
            tvUnit      = itemView.findViewById(R.id.tv_unit);
            tvBuyPrice  = itemView.findViewById(R.id.tv_buy_price);
            tvSellPrice = itemView.findViewById(R.id.tv_sell_price);
            tvBuyChange  = itemView.findViewById(R.id.tv_buy_change);
            tvSellChange = itemView.findViewById(R.id.tv_sell_change);
            tvUpdatedAt = itemView.findViewById(R.id.tv_updated_at);
            btnPin      = itemView.findViewById(R.id.btn_pin);
        }
    }
}