package com.liang.goldtracker.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class VangTodayHistoryResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("days")
    private int days;

    @SerializedName("type")
    private String type;

    @SerializedName("history")
    private List<DayEntry> history;

    public boolean isSuccess() { return success; }
    public int getDays() { return days; }
    public String getType() { return type; }
    public List<DayEntry> getHistory() { return history; }

    public static class DayEntry {
        @SerializedName("date")
        private String date;

        @SerializedName("prices")
        private Map<String, PriceItem> prices;

        public String getDate() { return date; }
        public Map<String, PriceItem> getPrices() { return prices; }
    }

    public static class PriceItem {
        @SerializedName("name")
        private String name;

        @SerializedName("buy")
        private double buy;

        @SerializedName("sell")
        private double sell;

        @SerializedName("day_change_buy")
        private double dayChangeBuy;

        @SerializedName("day_change_sell")
        private double dayChangeSell;

        @SerializedName("updates")
        private int updates;

        public String getName() { return name; }
        public double getBuy() { return buy; }
        public double getSell() { return sell; }
        public double getDayChangeBuy() { return dayChangeBuy; }
        public double getDayChangeSell() { return dayChangeSell; }
        public int getUpdates() { return updates; }
    }
}
