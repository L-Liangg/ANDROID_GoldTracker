package com.liang.goldtracker.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.liang.goldtracker.data.api.VangTodayClient;
import com.liang.goldtracker.data.model.GoldPrice;
import com.liang.goldtracker.data.model.PriceHistory;
import com.liang.goldtracker.data.model.VangTodayAllResponse;
import com.liang.goldtracker.data.model.VangTodayHistoryResponse;
import com.liang.goldtracker.data.model.VangTodaySingleResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GoldRepository {

    // [name, unit, currency]
    private static final Map<String, String[]> TYPE_MAP = new HashMap<String, String[]>() {{
        put("XAUUSD",      new String[]{"Vàng thế giới XAU/USD",     "Ounce",  "USD"});
        put("SJL1L10",     new String[]{"Vàng miếng SJC 9999",       "Lượng",  "VND"});
        put("SJ9999",      new String[]{"Nhẫn tròn trơn SJC 9999",   "Chỉ",    "VND"});
        put("DOHNL",       new String[]{"Vàng miếng DOJI Hà Nội",    "Lượng",  "VND"});
        put("DOHCML",      new String[]{"Vàng miếng DOJI HCM",       "Lượng",  "VND"});
        put("DOJINHTV",    new String[]{"Nữ trang DOJI",             "Chỉ",    "VND"});
        put("PQHNVM",      new String[]{"Vàng miếng PNJ Hà Nội",     "Lượng",  "VND"});
        put("PQHN24NTT",   new String[]{"Nữ trang PNJ 24K",          "Chỉ",    "VND"});
        put("BTSJC",       new String[]{"Vàng miếng Bảo Tín SJC",   "Lượng",  "VND"});
        put("BT9999NTT",   new String[]{"Nữ trang Bảo Tín 9999",     "Chỉ",    "VND"});
        put("VNGSJC",      new String[]{"Vàng miếng VN Gold SJC",    "Lượng",  "VND"});
        put("VIETTINMSJC", new String[]{"Vàng miếng Viettin SJC",    "Lượng",  "VND"});
    }};

    public static Map<String, GoldPrice> getAllPrices() throws Exception {
        VangTodayAllResponse response = VangTodayClient.getAllPrices();
        Map<String, VangTodayAllResponse.PriceItem> prices = response.getPrices();
        long timestamp = response.getTimestamp();

        Map<String, GoldPrice> result = new HashMap<>();

        for (Map.Entry<String, String[]> entry : TYPE_MAP.entrySet()) {
            String typeCode  = entry.getKey();
            String[] info  = entry.getValue();
            String name    = info[0];
            String unit    = info[1];
            String currency = info[2];

            double buy = 0, sell = 0, buyChange = 0, sellChange = 0;

            if (prices.containsKey(typeCode)) {
                VangTodayAllResponse.PriceItem item = prices.get(typeCode);
                buy        = item.getBuy();
                sell       = item.getSell();
                buyChange  = item.getChangeBuy();
                sellChange = item.getChangeSell();
                currency   = item.getCurrency();
            }

            if (unit.equals("Chỉ")) {
                buy        = buy        / 10;
                sell       = sell       / 10;
                buyChange  = buyChange  / 10;
                sellChange = sellChange / 10;
            }

            // displayKey = name + " (" + unit + ")"
            String displayKey = name + " (" + unit + ")";

            result.put(displayKey, new GoldPrice(
                    typeCode, name, buy, sell, buyChange, sellChange, currency, unit, timestamp
            ));
        }

        return result;
    }

    public static GoldPrice getSinglePrice(String typeCode) throws Exception {
        VangTodaySingleResponse response = VangTodayClient.getSinglePrice(typeCode);

        String[] info   = TYPE_MAP.get(typeCode);
        String unit     = info != null ? info[1] : "Lượng";
        String currency = info != null ? info[2] : "VND";
        String name     = info != null ? info[0] : response.getName();

        double buy        = response.getBuy();
        double sell       = response.getSell();
        double buyChange  = response.getChangeBuy();
        double sellChange = response.getChangeSell();

        if (unit.equals("Chỉ")) {
            buy        = buy        / 10;
            sell       = sell       / 10;
            buyChange  = buyChange  / 10;
            sellChange = sellChange / 10;
        }

        return new GoldPrice(
                typeCode, name,
                buy, sell, buyChange, sellChange,
                currency, unit,
                response.getTimestamp()
        );
    }

    public static LiveData<PriceHistory> getPriceHistory(String typeCode, int days) {
        MutableLiveData<PriceHistory> liveData = new MutableLiveData<>();

        new Thread(() -> {
            try {
                VangTodayHistoryResponse response =
                        VangTodayClient.getPriceHistory(typeCode, days);

                String[] info = TYPE_MAP.get(typeCode);
                String unit   = info != null ? info[1] : "Lượng";
                boolean isChi = unit.equals("Chỉ");

                SimpleDateFormat sdf =
                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                List<PriceHistory.PriceEntry> entries = new ArrayList<>();

                for (VangTodayHistoryResponse.DayEntry day : response.getHistory()) {
                    VangTodayHistoryResponse.PriceItem item =
                            day.getPrices().get(typeCode);
                    if (item == null) continue;

                    long timestamp = sdf.parse(day.getDate()).getTime();
                    double buy  = isChi ? item.getBuy()  / 10 : item.getBuy();
                    double sell = isChi ? item.getSell() / 10 : item.getSell();

                    entries.add(new PriceHistory.PriceEntry(
                            day.getDate(), typeCode, item.getName(),
                            buy, sell, timestamp
                    ));
                }

                liveData.postValue(new PriceHistory(entries));
            } catch (Exception e) {
                liveData.postValue(new PriceHistory(new ArrayList<>()));
            }
        }).start();

        return liveData;
    }
}
