package com.example.j4rr.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.j4rr.model.Product;
import com.example.j4rr.model.Sale;
import com.example.j4rr.model.StoreSettings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocalStorageManager {
    private static final String PREF_NAME = "PetShopPOSPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_PRODUCTS = "products";
    private static final String KEY_SALES = "sales";
    private static final String KEY_SETTINGS = "settings";

    private final SharedPreferences prefs;

    public LocalStorageManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setLoggedIn(boolean loggedIn) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply();
    }

    public List<Product> getProducts() {
        List<Product> list = new ArrayList<>();
        String jsonStr = prefs.getString(KEY_PRODUCTS, null);
        if (jsonStr != null) {
            try {
                JSONArray arr = new JSONArray(jsonStr);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Product p = Product.fromJSONObject(obj);
                    if (p != null) {
                        list.add(p);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public void saveProducts(List<Product> products) {
        JSONArray arr = new JSONArray();
        for (Product p : products) {
            arr.put(p.toJSONObject());
        }
        prefs.edit().putString(KEY_PRODUCTS, arr.toString()).apply();
    }

    public List<Sale> getSales() {
        List<Sale> list = new ArrayList<>();
        String jsonStr = prefs.getString(KEY_SALES, null);
        if (jsonStr != null) {
            try {
                JSONArray arr = new JSONArray(jsonStr);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Sale s = Sale.fromJSONObject(obj);
                    if (s != null) {
                        list.add(s);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public void saveSales(List<Sale> sales) {
        JSONArray arr = new JSONArray();
        for (Sale s : sales) {
            arr.put(s.toJSONObject());
        }
        prefs.edit().putString(KEY_SALES, arr.toString()).apply();
    }

    public StoreSettings getSettings() {
        String jsonStr = prefs.getString(KEY_SETTINGS, null);
        if (jsonStr != null) {
            try {
                JSONObject obj = new JSONObject(jsonStr);
                return StoreSettings.fromJSONObject(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return StoreSettings.fromJSONObject(null);
    }

    public void saveSettings(StoreSettings settings) {
        prefs.edit().putString(KEY_SETTINGS, settings.toJSONObject().toString()).apply();
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
