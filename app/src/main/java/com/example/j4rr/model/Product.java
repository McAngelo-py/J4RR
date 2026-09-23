package com.example.j4rr.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Product {
    private String id;
    private String name;
    private String category;
    private double price;
    private int stock;
    private int minStock;

    public Product(String id, String name, String category, double price, int stock, int minStock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.minStock = minStock;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getMinStock() { return minStock; }
    public void setMinStock(int minStock) { this.minStock = minStock; }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id);
            obj.put("name", name);
            obj.put("category", category);
            obj.put("price", price);
            obj.put("stock", stock);
            obj.put("minStock", minStock);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static Product fromJSONObject(JSONObject obj) {
        try {
            String id = obj.optString("id");
            String name = obj.optString("name");
            String category = obj.optString("category");
            double price = obj.optDouble("price", 0.0);
            int stock = obj.optInt("stock", 0);
            int minStock = obj.optInt("minStock", 5);
            return new Product(id, name, category, price, stock, minStock);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
