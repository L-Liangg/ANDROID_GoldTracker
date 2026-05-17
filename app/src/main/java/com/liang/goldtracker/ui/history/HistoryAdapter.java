package com.liang.goldtracker.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.liang.goldtracker.R;
import com.liang.goldtracker.data.db.HistoryEntity;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDelete(HistoryEntity item);
    }

    private List<HistoryEntity> items = new ArrayList<>();
    private OnDeleteClickListener deleteListener;

    private static final NumberFormat VND_FORMAT = NumberFormat.getInstance(new Locale("vi", "VN"));
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("HH:mm dd/MM/yyyy", new Locale("vi", "VN"));

    public void setDeleteListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    public void submitList(List<HistoryEntity> data) {
        this.items = data != null ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryEntity item = items.get(position);
        boolean isVnd = "VND".equals(item.getCurrency());

        holder.tvGoldType.setText(item.getGoldType());
        holder.tvUnit.setText(item.getUnit());
        holder.tvCurrency.setText(item.getCurrency());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvBuyPricePerUnit.setText(formatPrice(item.getBuyPricePerUnit(), isVnd));
        holder.tvSellPricePerUnit.setText(formatPrice(item.getSellPricePerUnit(), isVnd));
        holder.tvTotalBuy.setText(formatPrice(item.getTotalVndBuy(), isVnd));
        holder.tvTotalSell.setText(formatPrice(item.getTotalVndSell(), isVnd));
        holder.tvTimestamp.setText(DATE_FORMAT.format(new Date(item.getTimestamp())));

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(item);
        });
    }

    private String formatPrice(double price, boolean isVnd) {
        if (isVnd) return VND_FORMAT.format(price) + " ₫";
        return "$" + NumberFormat.getInstance(Locale.US).format(price);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGoldType, tvUnit, tvCurrency, tvQuantity;
        TextView tvBuyPricePerUnit, tvSellPricePerUnit, tvTotalBuy, tvTotalSell, tvTimestamp;
        MaterialButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGoldType         = itemView.findViewById(R.id.tv_gold_type);
            tvUnit             = itemView.findViewById(R.id.tv_unit);
            tvCurrency         = itemView.findViewById(R.id.tv_currency);
            tvQuantity         = itemView.findViewById(R.id.tv_quantity);
            tvBuyPricePerUnit  = itemView.findViewById(R.id.tv_buy_price_per_unit);
            tvSellPricePerUnit = itemView.findViewById(R.id.tv_sell_price_per_unit);
            tvTotalBuy         = itemView.findViewById(R.id.tv_total_buy);
            tvTotalSell        = itemView.findViewById(R.id.tv_total_sell);
            tvTimestamp        = itemView.findViewById(R.id.tv_timestamp);
            btnDelete          = itemView.findViewById(R.id.btn_delete);
        }
    }
}