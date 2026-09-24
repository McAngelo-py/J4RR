package com.example.j4rr.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.j4rr.R;
import com.example.j4rr.model.Product;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final OnProductListener listener;

    public interface OnProductListener {
        void onEdit(Product product);
        void onDelete(Product product);
    }

    public ProductAdapter(List<Product> productList, OnProductListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvCategory.setText(product.getCategory());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "₱%.2f", product.getPrice()));

        // Emoji
        String emoji = "🐾";
        String cat = product.getCategory().toLowerCase();
        if (cat.contains("dog")) emoji = "🐕";
        else if (cat.contains("cat")) emoji = "🐈";
        else if (cat.contains("bird")) emoji = "🐦";
        else if (cat.contains("fish")) emoji = "🐠";
        holder.tvEmoji.setText(emoji);

        // Stock status & colored dot
        int stock = product.getStock();
        int minStock = product.getMinStock();
        GradientDrawable dotDrawable = new GradientDrawable();
        dotDrawable.setShape(GradientDrawable.OVAL);

        if (stock == 0) {
            holder.tvStockStatus.setText("Out of stock");
            holder.tvStockStatus.setTextColor(Color.parseColor("#D32F2F"));
            dotDrawable.setColor(Color.parseColor("#D32F2F"));
        } else if (stock <= minStock) {
            holder.tvStockStatus.setText(stock + " low stock");
            holder.tvStockStatus.setTextColor(Color.parseColor("#FFA000"));
            dotDrawable.setColor(Color.parseColor("#FFA000"));
        } else {
            holder.tvStockStatus.setText(stock + " in stock");
            holder.tvStockStatus.setTextColor(Color.parseColor("#6C757D"));
            dotDrawable.setColor(Color.parseColor("#388E3C"));
        }
        holder.viewStockDot.setBackground(dotDrawable);

        holder.btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenu().add("Edit");
            popup.getMenu().add("Delete");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Edit")) {
                    if (listener != null) listener.onEdit(product);
                    return true;
                } else if (item.getTitle().equals("Delete")) {
                    if (listener != null) listener.onDelete(product);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvPrice, tvStockStatus, tvEmoji;
        View viewStockDot;
        ImageButton btnMenu;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvCategory = itemView.findViewById(R.id.tvProductCategory);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvStockStatus = itemView.findViewById(R.id.tvProductStockStatus);
            tvEmoji = itemView.findViewById(R.id.tvProductEmoji);
            viewStockDot = itemView.findViewById(R.id.viewStockDot);
            btnMenu = itemView.findViewById(R.id.btnProductMenu);
        }
    }
}
