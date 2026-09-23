package com.example.j4rr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.j4rr.InventoryActivity;
import com.example.j4rr.MainActivity;
import com.example.j4rr.R;
import com.example.j4rr.data.LocalStorageManager;
import com.example.j4rr.model.Product;
import com.example.j4rr.model.Sale;

import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private TextView tvSales, tvProducts, tvLowStock, tvTransactions;
    private LocalStorageManager storageManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        storageManager = new LocalStorageManager(requireContext());

        tvSales = view.findViewById(R.id.tvDashboardSales);
        tvProducts = view.findViewById(R.id.tvDashboardProducts);
        tvLowStock = view.findViewById(R.id.tvDashboardLowStock);
        tvTransactions = view.findViewById(R.id.tvDashboardTransactions);

        Button btnNewSale = view.findViewById(R.id.btnQuickNewSale);
        Button btnProducts = view.findViewById(R.id.btnQuickProducts);
        Button btnInventory = view.findViewById(R.id.btnQuickInventory);
        Button btnSales = view.findViewById(R.id.btnQuickSales);

        btnNewSale.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTab(R.id.nav_pos);
            }
        });

        btnProducts.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTab(R.id.nav_products);
            }
        });

        btnInventory.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), InventoryActivity.class));
        });

        btnSales.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTab(R.id.nav_sales);
            }
        });

        loadDashboardData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        List<Product> products = storageManager.getProducts();
        List<Sale> sales = storageManager.getSales();

        double totalSalesAmount = 0.0;
        for (Sale s : sales) {
            totalSalesAmount += s.getTotal();
        }

        int lowStockCount = 0;
        for (Product p : products) {
            if (p.getStock() <= p.getMinStock()) {
                lowStockCount++;
            }
        }

        tvSales.setText(String.format(Locale.getDefault(), "₱%.2f", totalSalesAmount));
        tvProducts.setText(String.valueOf(products.size()));
        tvLowStock.setText(String.valueOf(lowStockCount));
        tvTransactions.setText(String.valueOf(sales.size()));
    }
}
