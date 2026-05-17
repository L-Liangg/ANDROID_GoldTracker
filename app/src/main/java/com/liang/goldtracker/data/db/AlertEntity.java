package com.liang.goldtracker.data.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alert")
public class AlertEntity {

    @PrimaryKey
    @NonNull
    private String goldKey;

    private String goldType;
    private String unit;
    private String currency;
    private double buyThreshold;  // 0 = không đặt
    private double sellThreshold; // 0 = không đặt
    private boolean enabled;

    public AlertEntity() { this.goldKey = ""; }

    public AlertEntity(@NonNull String goldKey, String goldType, String unit, String currency,
                       double buyThreshold, double sellThreshold, boolean enabled) {
        this.goldKey = goldKey;
        this.goldType = goldType;
        this.unit = unit;
        this.currency = currency;
        this.buyThreshold = buyThreshold;
        this.sellThreshold = sellThreshold;
        this.enabled = enabled;
    }

    @NonNull public String getGoldKey() { return goldKey; }
    public String getGoldType() { return goldType; }
    public String getUnit() { return unit; }
    public String getCurrency() { return currency; }
    public double getBuyThreshold() { return buyThreshold; }
    public double getSellThreshold() { return sellThreshold; }
    public boolean isEnabled() { return enabled; }

    public void setGoldKey(@NonNull String goldKey) { this.goldKey = goldKey; }
    public void setGoldType(String goldType) { this.goldType = goldType; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setBuyThreshold(double buyThreshold) { this.buyThreshold = buyThreshold; }
    public void setSellThreshold(double sellThreshold) { this.sellThreshold = sellThreshold; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
