package com.example.j4rr.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.j4rr.R;
import com.example.j4rr.model.CartItem;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems;
    private final OnCartListener listener;

    public interface OnCartListener {
        void onIncrease(CartItem item);
        void onDecrease(CartItem item);
        void onRemove(CartItem item);
    }

    public CartAdapter(List<CartItem> cartItems, OnCartListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvName.setText(item.getProduct().getName());
        holder.tvPriceSubtotal.setText(String.format(Locale.getDefault(), "₱%.0f x %d (Subtotal: ₱%.2f)",
                item.getProduct().getPrice(), item.getQuantity(), item.getSubtotal()));
        holder.tvQty.setText(String.valueOf(item.getQuantity()));

        holder.btnPlus.setOnClickListener(v -> {
            if (listener != null) listener.onIncrease(item);
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (listener != null) listener.onDecrease(item);
        });

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) listener.onRemove(item);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPriceSubtotal, tvQty;
        Button btnPlus, btnMinus;
        ImageButton btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCartProductName);
            tvPriceSubtotal = itemView.findViewById(R.id.tvCartPriceSubtotal);
            tvQty = itemView.findViewById(R.id.tvCartQty);
            btnPlus = itemView.findViewById(R.id.btnCartPlus);
            btnMinus = itemView.findViewById(R.id.btnCartMinus);
            btnRemove = itemView.findViewById(R.id.btnCartRemove);
        }
    }
}
