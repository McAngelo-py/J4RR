package com.example.j4rr.util;

import android.text.TextUtils;
import java.text.NumberFormat;
import java.util.Locale;

public class AppUtils {

    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str) || str.trim().isEmpty();
    }

    public static boolean isValidPrice(String str) {
        if (isEmpty(str)) return false;
        try {
            double price = Double.parseDouble(str.trim());
            return price > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidInteger(String str) {
        if (isEmpty(str)) return false;
        try {
            int val = Integer.parseInt(str.trim());
            return val >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidPositiveInteger(String str) {
        if (isEmpty(str)) return false;
        try {
            int val = Integer.parseInt(str.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        return formatter.format(amount).replace("PHP", "₱");
    }
}
