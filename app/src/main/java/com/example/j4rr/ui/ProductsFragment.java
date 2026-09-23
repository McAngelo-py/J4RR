package com.example.j4rr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.j4rr.AddProductActivity;
import com.example.j4rr.InventoryActivity;
import com.example.j4rr.ManagePricesActivity;
import com.example.j4rr.R;
import com.example.j4rr.data.LocalStorageManager;
import com.example.j4rr.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ProductsFragment extends Fragment implements ProductAdapter.OnProductListener {

    private LocalStorageManager storageManager;
    private RecyclerView recyclerView;
    private TextView tvNoProducts;
    private ProductAdapter adapter;
    private List<Product> productList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        storageManager = new LocalStorageManager(requireContext());
        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        tvNoProducts = view.findViewById(R.id.tvNoProducts);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddProduct);

        Button btnInventoryNav = view.findViewById(R.id.btnInventoryNav);
        Button btnManagePricesNav = view.findViewById(R.id.btnManagePricesNav);

        btnInventoryNav.setOnClickListener(v -> startActivity(new Intent(requireContext(), InventoryActivity.class)));
        btnManagePricesNav.setOnClickListener(v -> startActivity(new Intent(requireContext(), ManagePricesActivity.class)));

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddProductActivity.class);
            startActivity(intent);
        });

        loadProducts();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProducts();
    }

    private void loadProducts() {
        productList = storageManager.getProducts();
        if (productList.isEmpty()) {
            tvNoProducts.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoProducts.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new ProductAdapter(productList, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onEdit(Product product) {
        Intent intent = new Intent(requireContext(), AddProductActivity.class);
        intent.putExtra("productId", product.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(Product product) {
        productList.remove(product);
        storageManager.saveProducts(productList);
        loadProducts();
        Toast.makeText(requireContext(), "Product deleted", Toast.LENGTH_SHORT).show();
    }
}
