package com.example.j4rr.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Sale {
    private String transactionNumber;
    private String dateTime;
    private String itemsSummary;
    private double subtotal;
    private double discount;
    private double total;
    private double cash;
    private double change;

    public Sale(String transactionNumber, String dateTime, String itemsSummary, double subtotal, double discount, double total, double cash, double change) {
        this.transactionNumber = transactionNumber;
        this.dateTime = dateTime;
        this.itemsSummary = itemsSummary;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
        this.cash = cash;
        this.change = change;
    }

    public String getTransactionNumber() { return transactionNumber; }
    public String getDateTime() { return dateTime; }
    public String getItemsSummary() { return itemsSummary; }
    public double getSubtotal() { return subtotal; }
    public double getDiscount() { return discount; }
    public double getTotal() { return total; }
    public double getCash() { return cash; }
    public double getChange() { return change; }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("transactionNumber", transactionNumber);
            obj.put("dateTime", dateTime);
            obj.put("itemsSummary", itemsSummary);
            obj.put("subtotal", subtotal);
            obj.put("discount", discount);
            obj.put("total", total);
            obj.put("cash", cash);
            obj.put("change", change);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static Sale fromJSONObject(JSONObject obj) {
        try {
            String tn = obj.optString("transactionNumber");
            String dt = obj.optString("dateTime");
            String items = obj.optString("itemsSummary");
            double sub = obj.optDouble("subtotal", 0.0);
            double disc = obj.optDouble("discount", 0.0);
            double tot = obj.optDouble("total", 0.0);
            double csh = obj.optDouble("cash", 0.0);
            double chg = obj.optDouble("change", 0.0);
            return new Sale(tn, dt, items, sub, disc, tot, csh, chg);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
