package com.liang.goldtracker.data.model;

import com.google.gson.annotations.SerializedName;

public class VangTodaySingleResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("timestamp")
    private long timestamp;

    @SerializedName("time")
    private String time;

    @SerializedName("date")
    private String date;

    @SerializedName("type")
    private String type;

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

    public boolean isSuccess() { return success; }
    public long getTimestamp() { return timestamp; }
    public String getTime() { return time; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public String getName() { return name; }
    public double getBuy() { return buy; }
    public double getSell() { return sell; }
    public double getChangeBuy() { return changeBuy; }
    public double getChangeSell() { return changeSell; }
}
