package com.example.j4rr.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.j4rr.model.Product;
import com.example.j4rr.model.Sale;
import com.example.j4rr.model.StoreSettings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocalStorageManager {
    private static final String TAG = "LocalStorageManager";
    private static final String PREF_NAME = "PetShopPOSPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_PRODUCTS = "products";
    private static final String KEY_SALES = "sales";
    private static final String KEY_SETTINGS = "settings";
    private static final String KEY_PASSWORD = "admin_password";

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

    public String getPassword() {
        return prefs.getString(KEY_PASSWORD, "admin123");
    }

    public void setPassword(String password) {
        if (password != null && !password.trim().isEmpty()) {
            prefs.edit().putString(KEY_PASSWORD, password.trim()).apply();
        }
    }

    public List<Product> getProducts() {
        List<Product> list = new ArrayList<>();
        String jsonStr = prefs.getString(KEY_PRODUCTS, null);
        if (jsonStr != null) {
            try {
                JSONArray arr = new JSONArray(jsonStr);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.optJSONObject(i);
                    if (obj != null) {
                        Product p = Product.fromJSONObject(obj);
                        if (p != null) {
                            list.add(p);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to parse saved products: " + e.getMessage());
            }
        }

        // Auto-seed pre-determined sample products on first launch or if data is missing/empty
        if (list.isEmpty()) {
            list.add(new Product("p1", "Royal Canin Dog Food", "Dog", 450.0, 15, 5));
            list.add(new Product("p2", "Whiskas Cat Treats", "Cat", 120.0, 20, 5));
            list.add(new Product("p3", "Premium Bird Seed Mix", "Bird", 180.0, 8, 3));
            list.add(new Product("p4", "Tropical Fish Flakes", "Fish", 95.0, 2, 5));
            list.add(new Product("p5", "Rubber Chew Toy", "Dog", 250.0, 0, 3));
            saveProducts(list);
        }

        return list;
    }

    public void saveProducts(List<Product> products) {
        if (products == null) return;
        JSONArray arr = new JSONArray();
        for (Product p : products) {
            if (p != null) {
                arr.put(p.toJSONObject());
            }
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
                    JSONObject obj = arr.optJSONObject(i);
                    if (obj != null) {
                        Sale s = Sale.fromJSONObject(obj);
                        if (s != null) {
                            list.add(s);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to parse saved sales: " + e.getMessage());
            }
        }
        return list;
    }

    public void saveSales(List<Sale> sales) {
        if (sales == null || sales.isEmpty()) {
            prefs.edit().remove(KEY_SALES).apply();
            return;
        }
        JSONArray arr = new JSONArray();
        for (Sale s : sales) {
            if (s != null) {
                arr.put(s.toJSONObject());
            }
        }
        prefs.edit().putString(KEY_SALES, arr.toString()).apply();
    }

    public StoreSettings getSettings() {
        String jsonStr = prefs.getString(KEY_SETTINGS, null);
        if (jsonStr != null) {
            try {
                JSONObject obj = new JSONObject(jsonStr);
                StoreSettings settings = StoreSettings.fromJSONObject(obj);
                if (settings != null) {
                    return settings;
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to parse saved settings: " + e.getMessage());
            }
        }
        return StoreSettings.fromJSONObject(null);
    }

    public void saveSettings(StoreSettings settings) {
        if (settings != null) {
            prefs.edit().putString(KEY_SETTINGS, settings.toJSONObject().toString()).apply();
        }
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
