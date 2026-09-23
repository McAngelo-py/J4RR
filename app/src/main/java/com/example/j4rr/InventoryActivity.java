package com.example.j4rr;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.j4rr.data.LocalStorageManager;
import com.example.j4rr.model.Product;
import com.example.j4rr.ui.InventoryAdapter;

import java.util.List;

public class InventoryActivity extends AppCompatActivity implements InventoryAdapter.OnAdjustStockListener {

    private LocalStorageManager storageManager;
    private RecyclerView recyclerView;
    private TextView tvNoInventory;
    private InventoryAdapter adapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        storageManager = new LocalStorageManager(this);
        recyclerView = findViewById(R.id.recyclerViewInventory);
        tvNoInventory = findViewById(R.id.tvNoInventory);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadInventory();
    }

    private void loadInventory() {
        productList = storageManager.getProducts();
        if (productList.isEmpty()) {
            tvNoInventory.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoInventory.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new InventoryAdapter(productList, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onAdjust(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adjust Stock: " + product.getName());

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(product.getStock()));
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String valStr = input.getText().toString().trim();
            if (!valStr.isEmpty()) {
                try {
                    int newStock = Integer.parseInt(valStr);
                    product.setStock(newStock);
                    storageManager.saveProducts(productList);
                    loadInventory();
                    Toast.makeText(this, "Stock updated", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
