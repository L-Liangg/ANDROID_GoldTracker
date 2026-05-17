package com.liang.goldtracker.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.liang.goldtracker.R;
import com.liang.goldtracker.data.db.AlertEntity;
import com.liang.goldtracker.data.model.GoldPrice;
import com.liang.goldtracker.data.repository.AlertRepository;
import com.liang.goldtracker.data.repository.GoldRepository;
import com.liang.goldtracker.util.AlarmScheduler;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GoldPriceFetchReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "gold_alert_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Fired at: " + new java.util.Date());

        new Thread(() -> {
            try {
                Map<String, GoldPrice> prices = GoldRepository.getAllPrices();

                List<AlertEntity> alerts = AlertRepository.getAllEnabledSync();
                for (AlertEntity alert : alerts) {
                    GoldPrice price = prices.get(alert.getGoldKey());
                    if (price == null) continue;

                    // Giá mua tiệm > ngưỡng mua → bán cho tiệm có lợi
                    if (alert.getBuyThreshold() > 0
                            && price.getBuyPrice() > 0
                            && price.getBuyPrice() > alert.getBuyThreshold()) {
                        sendNotification(context,
                                "Giá mua vượt ngưỡng: " + alert.getGoldKey(),
                                formatPrice(price.getBuyPrice(), alert.getCurrency())
                                        + " > " + formatPrice(alert.getBuyThreshold(), alert.getCurrency()),
                                alert.getGoldKey().hashCode()
                        );
                    }

                    // Giá bán tiệm < ngưỡng bán → mua từ tiệm có lợi
                    if (alert.getSellThreshold() > 0
                            && price.getSellPrice() > 0
                            && price.getSellPrice() < alert.getSellThreshold()) {
                        sendNotification(context,
                                "Giá bán dưới ngưỡng: " + alert.getGoldKey(),
                                formatPrice(price.getSellPrice(), alert.getCurrency())
                                        + " < " + formatPrice(alert.getSellThreshold(), alert.getCurrency()),
                                alert.getGoldKey().hashCode() + 1
                        );
                    }
                }
            } catch (Exception e) {
                // Bỏ qua lỗi network, thử lại ngày hôm sau
            }

            // Reschedule cho ngày hôm sau vì dùng setExactAndAllowWhileIdle
            AlarmScheduler.scheduleFromSaved(context);
        }).start();
    }

    private void sendNotification(Context context, String title, String message, int notifId) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Cảnh báo giá vàng",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        manager.notify(notifId, builder.build());
    }

    private String formatPrice(double price, String currency) {
        if ("VND".equals(currency)) {
            return NumberFormat.getInstance(new Locale("vi", "VN")).format(price) + " ₫";
        }
        return "$" + NumberFormat.getInstance(Locale.US).format(price);
    }
}
