package com.liang.goldtracker.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class VangTodayAllResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("timestamp")
    private long timestamp;

    @SerializedName("time")
    private String time;

    @SerializedName("date")
    private String date;

    @SerializedName("count")
    private int count;

    @SerializedName("prices")
    private Map<String, PriceItem> prices;

    public boolean isSuccess() { return success; }
    public long getTimestamp() { return timestamp; }
    public String getTime() { return time; }
    public String getDate() { return date; }
    public int getCount() { return count; }
    public Map<String, PriceItem> getPrices() { return prices; }

    public static class PriceItem {
        @SerializedName("name")
        private String name;

        @SerializedName("buy")
        private double buy;

        @SerializedName("sell")
        private double sell;

        @SerializedName("change_buy")
        private double changeBuy;

        @SerializedName("change_sell")
        private double changeSell;

        @SerializedName("currency")
        private String currency;

        public String getName() { return name; }
        public double getBuy() { return buy; }
        public double getSell() { return sell; }
        public double getChangeBuy() { return changeBuy; }
        public double getChangeSell() { return changeSell; }
        public String getCurrency() { return currency; }
    }
}
