package com.liang.goldtracker.data.model;

import java.util.List;

public class PriceHistory {
    private List<PriceEntry> history;

    public PriceHistory(List<PriceEntry> history) {
        this.history = history;
    }

    public static class PriceEntry {
        private String date;
        private String typeCode;
        private String name;
        private double buy;
        private double sell;
        private long timestamp;

        public PriceEntry(String date, String typeCode, String name, double buy, double sell, long timestamp) {
            this.date = date;
            this.typeCode = typeCode;
            this.name = name;
            this.buy = buy;
            this.sell = sell;
            this.timestamp = timestamp;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getBuy() {
            return buy;
        }

        public void setBuy(double buy) {
            this.buy = buy;
        }

        public double getSell() {
            return sell;
        }

        public void setSell(double sell) {
            this.sell = sell;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public void setHistory(List<PriceEntry> history) {
        this.history = history;
    }

    public List<PriceEntry> getHistory() {
        return history;
    }
}
