package com.liang.goldtracker.data.model;

public class GoldPrice {
    private String typeCode;
    private String name;
    private double buyPrice;
    private double sellPrice;
    private double buyChange;
    private double sellChange;
    private String currency;
    private String unit;
    private long updatedAt;

    public GoldPrice(String typeCode, String name, double buyPrice, double sellPrice, double buyChange, double sellChange, String currency, String unit, long updatedAt) {
        this.typeCode = typeCode;
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.buyChange = buyChange;
        this.sellChange = sellChange;
        this.currency = currency;
        this.unit = unit;
        this.updatedAt = updatedAt;
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

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public double getBuyChange() {
        return buyChange;
    }

    public void setBuyChange(double buyChange) {
        this.buyChange = buyChange;
    }

    public double getSellChange() {
        return sellChange;
    }

    public void setSellChange(double sellChange) {
        this.sellChange = sellChange;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
