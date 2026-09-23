package com.example.j4rr.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.j4rr.R;
import com.example.j4rr.model.Sale;

import java.util.List;
import java.util.Locale;

public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.SaleViewHolder> {

    private final List<Sale> saleList;

    public SaleAdapter(List<Sale> saleList) {
        this.saleList = saleList;
    }

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale, parent, false);
        return new SaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleViewHolder holder, int position) {
        Sale sale = saleList.get(position);
        holder.tvTxn.setText(sale.getTransactionNumber());
        holder.tvDate.setText(sale.getDateTime());
        holder.tvItems.setText(sale.getItemsSummary());
        holder.tvTotal.setText(String.format(Locale.getDefault(), "Total: ₱%.2f", sale.getTotal()));
        holder.tvCashChange.setText(String.format(Locale.getDefault(), "Cash: ₱%.2f | Change: ₱%.2f", sale.getCash(), sale.getChange()));
    }

    @Override
    public int getItemCount() {
        return saleList.size();
    }

    static class SaleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTxn, tvDate, tvItems, tvTotal, tvCashChange;

        public SaleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTxn = itemView.findViewById(R.id.tvSaleTxn);
            tvDate = itemView.findViewById(R.id.tvSaleDate);
            tvItems = itemView.findViewById(R.id.tvSaleItems);
            tvTotal = itemView.findViewById(R.id.tvSaleTotal);
            tvCashChange = itemView.findViewById(R.id.tvSaleCashChange);
        }
    }
}
