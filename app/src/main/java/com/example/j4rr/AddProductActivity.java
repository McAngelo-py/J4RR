package com.example.j4rr;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.j4rr.data.LocalStorageManager;
import com.example.j4rr.model.Product;
import com.example.j4rr.util.AppUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilPrice, tilStock, tilMinStock, tilCategory;
    private TextInputEditText etName, etPrice, etStock, etMinStock;
    private AutoCompleteTextView actvCategory;
    private Button btnSave;
    private LocalStorageManager storageManager;
    private String editingProductId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_add_product);

        storageManager = new LocalStorageManager(this);

        TextView tvTitle = findViewById(R.id.tvAddProductTitle);
        tilName = findViewById(R.id.tilProductName);
        tilCategory = findViewById(R.id.tilProductCategory);
        tilPrice = findViewById(R.id.tilProductPrice);
        tilStock = findViewById(R.id.tilProductStock);
        tilMinStock = findViewById(R.id.tilProductMinStock);

        etName = findViewById(R.id.etProductName);
        actvCategory = findViewById(R.id.actvCategory);
        etPrice = findViewById(R.id.etProductPrice);
        etStock = findViewById(R.id.etProductStock);
        etMinStock = findViewById(R.id.etProductMinStock);
        btnSave = findViewById(R.id.btnSaveProduct);

        String[] categories = new String[]{"Dog", "Cat", "Bird", "Fish", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(adapter);

        if (getIntent() != null && getIntent().hasExtra("productId")) {
            editingProductId = getIntent().getStringExtra("productId");
            tvTitle.setText("Edit Product");
            btnSave.setText("UPDATE PRODUCT");
            loadProductData(editingProductId);
        }

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void loadProductData(String id) {
        List<Product> list = storageManager.getProducts();
        for (Product p : list) {
            if (p.getId().equals(id)) {
                etName.setText(p.getName());
                actvCategory.setText(p.getCategory(), false);
                etPrice.setText(String.valueOf(p.getPrice()));
                etStock.setText(String.valueOf(p.getStock()));
                etMinStock.setText(String.valueOf(p.getMinStock()));
                break;
            }
        }
    }

    private void saveProduct() {
        if (tilName != null) tilName.setError(null);
        if (tilCategory != null) tilCategory.setError(null);
        if (tilPrice != null) tilPrice.setError(null);
        if (tilStock != null) tilStock.setError(null);
        if (tilMinStock != null) tilMinStock.setError(null);

        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String category = actvCategory.getText() != null ? actvCategory.getText().toString().trim() : "";
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        String stockStr = etStock.getText() != null ? etStock.getText().toString().trim() : "";
        String minStockStr = etMinStock.getText() != null ? etMinStock.getText().toString().trim() : "";

        boolean hasError = false;

        if (AppUtils.isEmpty(name)) {
            if (tilName != null) tilName.setError("Product name is required.");
            hasError = true;
        }

        if (AppUtils.isEmpty(category)) {
            if (tilCategory != null) tilCategory.setError("Please select a category.");
            hasError = true;
        }

        double price = 0.0;
        if (AppUtils.isEmpty(priceStr)) {
            if (tilPrice != null) tilPrice.setError("Price is required.");
            hasError = true;
        } else {
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    if (tilPrice != null) tilPrice.setError("Price must be greater than ₱0.");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                if (tilPrice != null) tilPrice.setError("Enter a valid price.");
                hasError = true;
            }
        }

        int stock = 0;
        if (AppUtils.isEmpty(stockStr)) {
            if (tilStock != null) tilStock.setError("Stock quantity is required.");
            hasError = true;
        } else {
            try {
                stock = Integer.parseInt(stockStr);
                if (stock < 0) {
                    if (tilStock != null) tilStock.setError("Stock cannot be negative.");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                if (tilStock != null) tilStock.setError("Enter a valid stock quantity.");
                hasError = true;
            }
        }

        int minStock = 5;
        if (AppUtils.isEmpty(minStockStr)) {
            if (tilMinStock != null) tilMinStock.setError("Minimum stock is required.");
            hasError = true;
        } else {
            try {
                minStock = Integer.parseInt(minStockStr);
                if (minStock < 0) {
                    if (tilMinStock != null) tilMinStock.setError("Minimum stock cannot be negative.");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                if (tilMinStock != null) tilMinStock.setError("Enter a valid minimum stock quantity.");
                hasError = true;
            }
        }

        if (hasError) return;

        // Prevent double clicks
        btnSave.setEnabled(false);

        List<Product> products = storageManager.getProducts();
        if (products == null) products = new ArrayList<>();

        if (editingProductId != null) {
            boolean updated = false;
            for (Product p : products) {
                if (p.getId().equals(editingProductId)) {
                    p.setName(name);
                    p.setCategory(category);
                    p.setPrice(price);
                    p.setStock(stock);
                    p.setMinStock(minStock);
                    updated = true;
                    break;
                }
            }
            if (updated) {
                storageManager.saveProducts(products);
                Toast.makeText(this, "Product updated successfully.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                btnSave.setEnabled(true);
                Toast.makeText(this, "Unable to update product. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            String id = UUID.randomUUID().toString();
            Product newProduct = new Product(id, name, category, price, stock, minStock);
            products.add(newProduct);
            storageManager.saveProducts(products);
            Toast.makeText(this, "Product added successfully.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
