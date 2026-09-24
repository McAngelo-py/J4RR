package com.example.j4rr;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;

import com.example.j4rr.data.LocalStorageManager;
import com.example.j4rr.model.StoreSettings;
import com.example.j4rr.util.AppUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;
    private LocalStorageManager storageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Auto-hide system bars (immersive mode)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        storageManager = new LocalStorageManager(this);

        StoreSettings settings = storageManager.getSettings();
        if (settings != null && settings.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if (storageManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        tvForgotPassword.setOnClickListener(v -> showRealForgotPasswordFlow());

        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        if (tilUsername != null) tilUsername.setError(null);
        if (tilPassword != null) tilPassword.setError(null);

        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        boolean hasError = false;

        if (AppUtils.isEmpty(username)) {
            if (tilUsername != null) {
                tilUsername.setError("Username is required.");
            } else {
                etUsername.setError("Username is required.");
            }
            hasError = true;
        }

        if (AppUtils.isEmpty(password)) {
            if (tilPassword != null) {
                tilPassword.setError("Password is required.");
            } else {
                etPassword.setError("Password is required.");
            }
            hasError = true;
        }

        if (hasError) return;

        // Disable button temporarily to prevent double submission
        btnLogin.setEnabled(false);

        if (username.equals("admin") && password.equals(storageManager.getPassword())) {
            storageManager.setLoggedIn(true);
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            btnLogin.setEnabled(true);
            Toast.makeText(this, "Incorrect username or password.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showRealForgotPasswordFlow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etEmailOrUser = new EditText(this);
        etEmailOrUser.setHint("Enter Admin Username or Email");
        layout.addView(etEmailOrUser);

        builder.setView(layout);

        builder.setPositiveButton("Send Reset Code", (dialog, which) -> {
            String input = etEmailOrUser.getText().toString().trim();
            if (input.equalsIgnoreCase("admin") || input.contains("@")) {
                Toast.makeText(this, "Reset code sent to your email (Demo Code: 1234)", Toast.LENGTH_LONG).show();
                showResetCodeVerificationDialog();
            } else {
                Toast.makeText(this, "User or email not found.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showResetCodeVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verify & Reset Password");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etCode = new EditText(this);
        etCode.setHint("Enter 4-digit Code (e.g. 1234)");
        etCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(etCode);

        final EditText etNewPass = new EditText(this);
        etNewPass.setHint("Enter New Password");
        etNewPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etNewPass);

        builder.setView(layout);

        builder.setPositiveButton("Reset Password", (dialog, which) -> {
            String code = etCode.getText().toString().trim();
            String newPass = etNewPass.getText().toString().trim();

            if (code.equals("1234")) {
                if (!AppUtils.isEmpty(newPass)) {
                    storageManager.setPassword(newPass);
                    Toast.makeText(this, "Password reset successfully. You can now log in.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Invalid verification code.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
