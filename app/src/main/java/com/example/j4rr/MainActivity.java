package com.example.j4rr;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;

import com.example.j4rr.data.LocalStorageManager;
import com.example.j4rr.model.StoreSettings;
import com.example.j4rr.ui.DashboardFragment;
import com.example.j4rr.ui.PosFragment;
import com.example.j4rr.ui.ProductsFragment;
import com.example.j4rr.ui.SalesFragment;
import com.example.j4rr.ui.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Auto-hide system bars (immersive mode)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        LocalStorageManager storageManager = new LocalStorageManager(this);
        StoreSettings settings = storageManager.getSettings();
        if (settings.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new DashboardFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new DashboardFragment();
            } else if (itemId == R.id.nav_pos) {
                selectedFragment = new PosFragment();
            } else if (itemId == R.id.nav_products) {
                selectedFragment = new ProductsFragment();
            } else if (itemId == R.id.nav_sales) {
                selectedFragment = new SalesFragment();
            } else if (itemId == R.id.nav_more) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }

    public void navigateToTab(int menuId) {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(menuId);
    }
}
