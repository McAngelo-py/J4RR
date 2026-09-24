package com.example.j4rr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.j4rr.AddProductActivity;
import com.example.j4rr.R;
import com.example.j4rr.data.LocalStorageManager;
import com.example.j4rr.model.Product;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment implements ProductAdapter.OnProductListener {

    private LocalStorageManager storageManager;
    private RecyclerView recyclerView;
    private TextView tvNoProducts;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();
    private EditText etSearch;
    private String selectedCategory = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        storageManager = new LocalStorageManager(requireContext());
        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        tvNoProducts = view.findViewById(R.id.tvNoProducts);
        etSearch = view.findViewById(R.id.etProductsSearch);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddProduct);

        Chip chipAll = view.findViewById(R.id.chipAll);
        Chip chipDog = view.findViewById(R.id.chipDog);
        Chip chipCat = view.findViewById(R.id.chipCat);
        Chip chipBird = view.findViewById(R.id.chipBird);
        Chip chipFish = view.findViewById(R.id.chipFish);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddProductActivity.class);
            startActivity(intent);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        View.OnClickListener chipListener = v -> {
            chipAll.setChecked(v.getId() == R.id.chipAll);
            chipDog.setChecked(v.getId() == R.id.chipDog);
            chipCat.setChecked(v.getId() == R.id.chipCat);
            chipBird.setChecked(v.getId() == R.id.chipBird);
            chipFish.setChecked(v.getId() == R.id.chipFish);

            if (v.getId() == R.id.chipAll) selectedCategory = "All";
            else if (v.getId() == R.id.chipDog) selectedCategory = "Dog";
            else if (v.getId() == R.id.chipCat) selectedCategory = "Cat";
            else if (v.getId() == R.id.chipBird) selectedCategory = "Bird";
            else if (v.getId() == R.id.chipFish) selectedCategory = "Fish";

            filterProducts();
        };

        chipAll.setOnClickListener(chipListener);
        chipDog.setOnClickListener(chipListener);
        chipCat.setOnClickListener(chipListener);
        chipBird.setOnClickListener(chipListener);
        chipFish.setOnClickListener(chipListener);

        loadProducts();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProducts();
    }

    private void loadProducts() {
        allProducts = storageManager.getProducts();
        filterProducts();
    }

    private void filterProducts() {
        String query = etSearch.getText() != null ? etSearch.getText().toString().trim().toLowerCase() : "";
        filteredProducts.clear();

        for (Product p : allProducts) {
            boolean matchesSearch = p.getName().toLowerCase().contains(query) || p.getCategory().toLowerCase().contains(query);
            boolean matchesCategory = selectedCategory.equals("All") || p.getCategory().equalsIgnoreCase(selectedCategory);

            if (matchesSearch && matchesCategory) {
                filteredProducts.add(p);
            }
        }

        if (filteredProducts.isEmpty()) {
            tvNoProducts.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoProducts.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            ProductAdapter adapter = new ProductAdapter(filteredProducts, this);
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
        allProducts.remove(product);
        storageManager.saveProducts(allProducts);
        loadProducts();
        Toast.makeText(requireContext(), "Product deleted", Toast.LENGTH_SHORT).show();
    }
}
