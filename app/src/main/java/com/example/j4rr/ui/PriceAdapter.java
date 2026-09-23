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

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.PriceViewHolder> {

    private final List<Product> productList;
    private final OnEditPriceListener listener;

    public interface OnEditPriceListener {
        void onEditPrice(Product product);
    }

    public PriceAdapter(List<Product> productList, OnEditPriceListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_price, parent, false);
        return new PriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvCurrentPrice.setText(String.format(Locale.getDefault(), "₱%.2f", product.getPrice()));

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditPrice(product);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class PriceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCurrentPrice;
        Button btnEdit;

        public PriceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPriceProductName);
            tvCurrentPrice = itemView.findViewById(R.id.tvPriceCurrent);
            btnEdit = itemView.findViewById(R.id.btnEditPrice);
        }
    }
}
