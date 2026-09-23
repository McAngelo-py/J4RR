package com.example.j4rr.ui;

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
import java.util.Locale;

public class PosProductAdapter extends RecyclerView.Adapter<PosProductAdapter.PosViewHolder> {

    private final List<Product> productList;
    private final OnPosProductListener listener;

    public interface OnPosProductListener {
        void onAddToCart(Product product);
    }

    public PosProductAdapter(List<Product> productList, OnPosProductListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pos_product, parent, false);
        return new PosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "₱%.2f", product.getPrice()));
        holder.tvStock.setText("Stock: " + product.getStock());

        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCart(product);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class PosViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvStock;
        Button btnAdd;

        public PosViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPosProductName);
            tvPrice = itemView.findViewById(R.id.tvPosProductPrice);
            tvStock = itemView.findViewById(R.id.tvPosProductStock);
            btnAdd = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
