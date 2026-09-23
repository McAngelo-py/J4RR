package com.example.j4rr.model;

import org.json.JSONException;
import org.json.JSONObject;

public class StoreSettings {
    private String storeName;
    private String address;
    private String contactNumber;
    private boolean darkMode;

    public StoreSettings(String storeName, String address, String contactNumber, boolean darkMode) {
        this.storeName = storeName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.darkMode = darkMode;
    }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public boolean isDarkMode() { return darkMode; }
    public void setDarkMode(boolean darkMode) { this.darkMode = darkMode; }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("storeName", storeName);
            obj.put("address", address);
            obj.put("contactNumber", contactNumber);
            obj.put("darkMode", darkMode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static StoreSettings fromJSONObject(JSONObject obj) {
        if (obj == null) {
            return new StoreSettings("Paws & Claws Pet Shop", "123 Pet St, Manila", "09123456789", false);
        }
        String storeName = obj.optString("storeName", "Paws & Claws Pet Shop");
        String address = obj.optString("address", "123 Pet St, Manila");
        String contactNumber = obj.optString("contactNumber", "09123456789");
        boolean darkMode = obj.optBoolean("darkMode", false);
        return new StoreSettings(storeName, address, contactNumber, darkMode);
    }
}
