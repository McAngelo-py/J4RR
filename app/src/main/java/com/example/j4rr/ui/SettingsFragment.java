package com.example.j4rr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.j4rr.LoginActivity;
import com.example.j4rr.R;
import com.example.j4rr.data.LocalStorageManager;
import com.example.j4rr.model.StoreSettings;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsFragment extends Fragment {

    private LocalStorageManager storageManager;
    private TextInputEditText etName, etAddress, etContact;
    private SwitchCompat switchDarkMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        storageManager = new LocalStorageManager(requireContext());

        etName = view.findViewById(R.id.etStoreName);
        etAddress = view.findViewById(R.id.etStoreAddress);
        etContact = view.findViewById(R.id.etStoreContact);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);

        Button btnSave = view.findViewById(R.id.btnSaveSettings);
        Button btnClear = view.findViewById(R.id.btnClearData);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        loadSettings();

        btnSave.setOnClickListener(v -> saveStoreSettings());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            StoreSettings settings = storageManager.getSettings();
            settings.setDarkMode(isChecked);
            storageManager.saveSettings(settings);

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        btnClear.setOnClickListener(v -> showClearDataConfirmation());

        btnLogout.setOnClickListener(v -> {
            storageManager.setLoggedIn(false);
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void loadSettings() {
        StoreSettings settings = storageManager.getSettings();
        etName.setText(settings.getStoreName());
        etAddress.setText(settings.getAddress());
        etContact.setText(settings.getContactNumber());
        switchDarkMode.setChecked(settings.isDarkMode());
    }

    private void saveStoreSettings() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String contact = etContact.getText() != null ? etContact.getText().toString().trim() : "";

        StoreSettings settings = storageManager.getSettings();
        settings.setStoreName(name);
        settings.setAddress(address);
        settings.setContactNumber(contact);
        storageManager.saveSettings(settings);

        Toast.makeText(requireContext(), "Store information saved", Toast.LENGTH_SHORT).show();
    }

    private void showClearDataConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Clear Local Data")
                .setMessage("Are you sure you want to delete all products, inventory, and sales data?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    storageManager.clearAll();
                    Toast.makeText(requireContext(), "Local data cleared", Toast.LENGTH_SHORT).show();
                    // Restart app to login
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
