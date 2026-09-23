package com.example.j4rr;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.j4rr.data.LocalStorageManager;
import com.example.j4rr.model.Product;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private TextInputEditText etName, etPrice, etStock, etMinStock;
    private AutoCompleteTextView actvCategory;
    private LocalStorageManager storageManager;
    private String editingProductId = null;

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

        setContentView(R.layout.activity_add_product);

        storageManager = new LocalStorageManager(this);

        TextView tvTitle = findViewById(R.id.tvAddProductTitle);
        etName = findViewById(R.id.etProductName);
        actvCategory = findViewById(R.id.actvCategory);
        etPrice = findViewById(R.id.etProductPrice);
        etStock = findViewById(R.id.etProductStock);
        etMinStock = findViewById(R.id.etProductMinStock);
        Button btnSave = findViewById(R.id.btnSaveProduct);

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
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String category = actvCategory.getText() != null ? actvCategory.getText().toString().trim() : "";
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        String stockStr = etStock.getText() != null ? etStock.getText().toString().trim() : "";
        String minStockStr = etMinStock.getText() != null ? etMinStock.getText().toString().trim() : "";

        if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || minStockStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        int stock, minStock;
        try {
            price = Double.parseDouble(priceStr);
            stock = Integer.parseInt(stockStr);
            minStock = Integer.parseInt(minStockStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Product> products = storageManager.getProducts();

        if (products == null) return;

        if (editingProductId != null) {
            for (Product p : products) {
                if (p.getId().equals(editingProductId)) {
                    p.setName(name);
                    p.setCategory(category);
                    p.setPrice(price);
                    p.setStock(stock);
                    p.setMinStock(minStock);
                    break;
                }
            }
            Toast.makeText(this, "Product Updated", Toast.LENGTH_SHORT).show();
        } else {
            String id = UUID.randomUUID().toString();
            Product newProduct = new Product(id, name, category, price, stock, minStock);
            products.add(newProduct);
            Toast.makeText(this, "Product Saved", Toast.LENGTH_SHORT).show();
        }

        storageManager.saveProducts(products);
        finish();
    }
}
