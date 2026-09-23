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
import com.example.j4rr.ui.PriceAdapter;

import java.util.List;

public class ManagePricesActivity extends AppCompatActivity implements PriceAdapter.OnEditPriceListener {

    private LocalStorageManager storageManager;
    private RecyclerView recyclerView;
    private TextView tvNoPrices;
    private PriceAdapter adapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_prices);

        storageManager = new LocalStorageManager(this);
        recyclerView = findViewById(R.id.recyclerViewPrices);
        tvNoPrices = findViewById(R.id.tvNoPrices);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadPrices();
    }

    private void loadPrices() {
        productList = storageManager.getProducts();
        if (productList.isEmpty()) {
            tvNoPrices.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoPrices.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new PriceAdapter(productList, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onEditPrice(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Price: " + product.getName());

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(String.valueOf(product.getPrice()));
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String valStr = input.getText().toString().trim();
            if (!valStr.isEmpty()) {
                try {
                    double newPrice = Double.parseDouble(valStr);
                    product.setPrice(newPrice);
                    storageManager.saveProducts(productList);
                    loadPrices();
                    Toast.makeText(this, "Price updated", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
