package com.example.j4rr.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.j4rr.R;
import com.example.j4rr.data.LocalStorageManager;
import com.example.j4rr.model.Sale;

import java.util.List;

public class SalesFragment extends Fragment {

    private LocalStorageManager storageManager;
    private RecyclerView recyclerView;
    private TextView tvNoSales;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales, container, false);

        storageManager = new LocalStorageManager(requireContext());
        recyclerView = view.findViewById(R.id.recyclerViewSales);
        tvNoSales = view.findViewById(R.id.tvNoSales);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadSales();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSales();
    }

    private void loadSales() {
        List<Sale> sales = storageManager.getSales();
        if (sales.isEmpty()) {
            tvNoSales.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoSales.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            SaleAdapter adapter = new SaleAdapter(sales);
            recyclerView.setAdapter(adapter);
        }
    }
}
