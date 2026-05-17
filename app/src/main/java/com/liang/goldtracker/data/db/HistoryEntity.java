package com.liang.goldtracker.data.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "history")
public class HistoryEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String goldType;
    private String unit;
    private String currency;
    private double quantity;
    private double buyPricePerUnit;
    private double sellPricePerUnit;
    private double totalVndBuy;
    private double totalVndSell;
    private long timestamp;

    public HistoryEntity() {}

    @Ignore
    public HistoryEntity(String goldType, String unit, String currency,
                         double quantity,
                         double buyPricePerUnit, double sellPricePerUnit,
                         double totalVndBuy, double totalVndSell,
                         long timestamp) {
        this.goldType = goldType;
        this.unit = unit;
        this.currency = currency;
        this.quantity = quantity;
        this.buyPricePerUnit = buyPricePerUnit;
        this.sellPricePerUnit = sellPricePerUnit;
        this.totalVndBuy = totalVndBuy;
        this.totalVndSell = totalVndSell;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public String getGoldType() { return goldType; }
    public String getUnit() { return unit; }
    public String getCurrency() { return currency; }
    public double getQuantity() { return quantity; }
    public double getBuyPricePerUnit() { return buyPricePerUnit; }
    public double getSellPricePerUnit() { return sellPricePerUnit; }
    public double getTotalVndBuy() { return totalVndBuy; }
    public double getTotalVndSell() { return totalVndSell; }
    public long getTimestamp() { return timestamp; }

    void setId(int id) { this.id = id; }
    public void setGoldType(String goldType) { this.goldType = goldType; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public void setBuyPricePerUnit(double buyPricePerUnit) { this.buyPricePerUnit = buyPricePerUnit; }
    public void setSellPricePerUnit(double sellPricePerUnit) { this.sellPricePerUnit = sellPricePerUnit; }
    public void setTotalVndBuy(double totalVndBuy) { this.totalVndBuy = totalVndBuy; }
    public void setTotalVndSell(double totalVndSell) { this.totalVndSell = totalVndSell; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}