package com.example.j4rr.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.j4rr.R;
import com.example.j4rr.model.Product;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private final List<Product> productList;
    private final OnAdjustStockListener listener;

    public interface OnAdjustStockListener {
        void onAdjust(Product product);
    }

    public InventoryAdapter(List<Product> productList, OnAdjustStockListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvStock.setText("Stock: " + product.getStock());

        int stock = product.getStock();
        int minStock = product.getMinStock();

        if (stock == 0) {
            holder.tvStatus.setText("Out of Stock");
            holder.tvStatus.setTextColor(Color.parseColor("#DC3545"));
        } else if (stock <= minStock) {
            holder.tvStatus.setText("Low Stock");
            holder.tvStatus.setTextColor(Color.parseColor("#FFC107"));
        } else {
            holder.tvStatus.setText("In Stock");
            holder.tvStatus.setTextColor(Color.parseColor("#28A745"));
        }

        holder.btnAdjust.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAdjust(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStock, tvStatus;
        Button btnAdjust;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvInvProductName);
            tvStock = itemView.findViewById(R.id.tvInvStock);
            tvStatus = itemView.findViewById(R.id.tvInvStatus);
            btnAdjust = itemView.findViewById(R.id.btnAdjustStock);
        }
    }
}
