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

import com.example.j4rr.AddProductActivity;
import com.example.j4rr.InventoryActivity;
import com.example.j4rr.MainActivity;
import com.example.j4rr.R;
import com.example.j4rr.data.LocalStorageManager;
import com.example.j4rr.model.Product;
import com.example.j4rr.model.Sale;

import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private TextView tvSales, tvProducts, tvLowStock, tvTxnCount, tvRecentSales;
    private LocalStorageManager storageManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        storageManager = new LocalStorageManager(requireContext());

        tvSales = view.findViewById(R.id.tvDashboardSales);
        tvProducts = view.findViewById(R.id.tvDashboardProducts);
        tvLowStock = view.findViewById(R.id.tvDashboardLowStock);
        tvTxnCount = view.findViewById(R.id.tvDashboardTxnCount);
        tvRecentSales = view.findViewById(R.id.tvRecentSalesEmpty);

        Button btnNewSale = view.findViewById(R.id.btnQuickNewSale);
        Button btnAddProduct = view.findViewById(R.id.btnQuickAddProduct);
        Button btnInventory = view.findViewById(R.id.btnQuickInventory);
        Button btnSales = view.findViewById(R.id.btnQuickSales);

        btnNewSale.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTab(R.id.nav_pos);
            }
        });

        btnAddProduct.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddProductActivity.class));
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
        tvTxnCount.setText(sales.size() + (sales.size() == 1 ? " transaction" : " transactions"));
        tvLowStock.setText(lowStockCount + (lowStockCount == 1 ? " item low on stock" : " items low on stock"));

        if (!sales.isEmpty()) {
            Sale latest = sales.get(sales.size() - 1);
            tvRecentSales.setText(String.format(Locale.getDefault(), "Latest: %s\nItems: %s\nTotal: ₱%.2f",
                    latest.getTransactionNumber(), latest.getItemsSummary(), latest.getTotal()));
        } else {
            tvRecentSales.setText("No sales yet\n\nYour completed transactions will appear here.");
        }
    }
}
