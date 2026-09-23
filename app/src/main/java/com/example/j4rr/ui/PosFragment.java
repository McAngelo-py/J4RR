package com.example.j4rr.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.j4rr.R;
import com.example.j4rr.data.LocalStorageManager;
import com.example.j4rr.model.CartItem;
import com.example.j4rr.model.Product;
import com.example.j4rr.model.Sale;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PosFragment extends Fragment implements PosProductAdapter.OnPosProductListener, CartAdapter.OnCartListener {

    private LocalStorageManager storageManager;
    private RecyclerView rvProducts, rvCart;
    private TextView tvNoProducts, tvCartEmpty, tvSubtotal, tvDiscount, tvTotal, tvChange;
    private EditText etSearch, etCash;
    private Button btnCompleteSale;

    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();
    private List<CartItem> cartItems = new ArrayList<>();

    private PosProductAdapter productAdapter;
    private CartAdapter cartAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pos, container, false);

        storageManager = new LocalStorageManager(requireContext());

        etSearch = view.findViewById(R.id.etPosSearch);
        rvProducts = view.findViewById(R.id.recyclerViewPosProducts);
        rvCart = view.findViewById(R.id.recyclerViewCart);
        tvNoProducts = view.findViewById(R.id.tvPosNoProducts);
        tvCartEmpty = view.findViewById(R.id.tvCartEmpty);
        tvSubtotal = view.findViewById(R.id.tvPosSubtotal);
        tvDiscount = view.findViewById(R.id.tvPosDiscount);
        tvTotal = view.findViewById(R.id.tvPosTotal);
        etCash = view.findViewById(R.id.etPosCash);
        tvChange = view.findViewById(R.id.tvPosChange);
        btnCompleteSale = view.findViewById(R.id.btnCompleteSale);

        rvProducts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadProducts();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etCash.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateChange();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnCompleteSale.setOnClickListener(v -> completeSale());

        updateCartUI();

        return view;
    }

    private void loadProducts() {
        allProducts = storageManager.getProducts();
        filteredProducts = new ArrayList<>(allProducts);
        if (filteredProducts.isEmpty()) {
            tvNoProducts.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
        } else {
            tvNoProducts.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
            productAdapter = new PosProductAdapter(filteredProducts, this);
            rvProducts.setAdapter(productAdapter);
        }
    }

    private void filterProducts(String query) {
        filteredProducts.clear();
        for (Product p : allProducts) {
            if (p.getName().toLowerCase().contains(query.toLowerCase()) ||
                    p.getCategory().toLowerCase().contains(query.toLowerCase())) {
                filteredProducts.add(p);
            }
        }
        if (filteredProducts.isEmpty()) {
            tvNoProducts.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
        } else {
            tvNoProducts.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
            productAdapter = new PosProductAdapter(filteredProducts, this);
            rvProducts.setAdapter(productAdapter);
        }
    }

    @Override
    public void onAddToCart(Product product) {
        if (product.getStock() <= 0) {
            Toast.makeText(requireContext(), "Product is out of stock", Toast.LENGTH_SHORT).show();
            return;
        }

        CartItem existing = null;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                existing = item;
                break;
            }
        }

        if (existing != null) {
            if (existing.getQuantity() < product.getStock()) {
                existing.setQuantity(existing.getQuantity() + 1);
            } else {
                Toast.makeText(requireContext(), "Reached available stock limit", Toast.LENGTH_SHORT).show();
            }
        } else {
            cartItems.add(new CartItem(product, 1));
        }

        updateCartUI();
    }

    @Override
    public void onIncrease(CartItem item) {
        if (item.getQuantity() < item.getProduct().getStock()) {
            item.setQuantity(item.getQuantity() + 1);
            updateCartUI();
        } else {
            Toast.makeText(requireContext(), "Reached available stock limit", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDecrease(CartItem item) {
        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
        } else {
            cartItems.remove(item);
        }
        updateCartUI();
    }

    @Override
    public void onRemove(CartItem item) {
        cartItems.remove(item);
        updateCartUI();
    }

    private void updateCartUI() {
        if (cartItems.isEmpty()) {
            tvCartEmpty.setVisibility(View.VISIBLE);
            rvCart.setVisibility(View.GONE);
        } else {
            tvCartEmpty.setVisibility(View.GONE);
            rvCart.setVisibility(View.VISIBLE);
            cartAdapter = new CartAdapter(cartItems, this);
            rvCart.setAdapter(cartAdapter);
        }

        double subtotal = 0.0;
        for (CartItem item : cartItems) {
            subtotal += item.getSubtotal();
        }

        double discount = 0.0; // No discount for prototype
        double total = subtotal - discount;

        tvSubtotal.setText(String.format(Locale.getDefault(), "₱%.2f", subtotal));
        tvDiscount.setText(String.format(Locale.getDefault(), "₱%.2f", discount));
        tvTotal.setText(String.format(Locale.getDefault(), "₱%.2f", total));
        calculateChange();
    }

    private void calculateChange() {
        double total = getCartTotal();
        String cashStr = etCash.getText() != null ? etCash.getText().toString().trim() : "";
        if (!cashStr.isEmpty()) {
            try {
                double cash = Double.parseDouble(cashStr);
                double change = cash - total;
                if (change >= 0) {
                    tvChange.setText(String.format(Locale.getDefault(), "₱%.2f", change));
                } else {
                    tvChange.setText("₱0.00 (Insufficient)");
                }
            } catch (NumberFormatException e) {
                tvChange.setText("₱0.00");
            }
        } else {
            tvChange.setText("₱0.00");
        }
    }

    private double getCartTotal() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }
        return total;
    }

    private void completeSale() {
        if (cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String cashStr = etCash.getText() != null ? etCash.getText().toString().trim() : "";
        if (cashStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter cash amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = getCartTotal();
        double cash;
        try {
            cash = Double.parseDouble(cashStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid cash amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cash < total) {
            Toast.makeText(requireContext(), "Insufficient cash", Toast.LENGTH_SHORT).show();
            return;
        }

        double change = cash - total;

        // Build items summary
        StringBuilder summary = new StringBuilder();
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem ci = cartItems.get(i);
            summary.append(ci.getProduct().getName()).append(" (x").append(ci.getQuantity()).append(")");
            if (i < cartItems.size() - 1) summary.append(", ");

            // Update product stock in storage
            for (Product p : allProducts) {
                if (p.getId().equals(ci.getProduct().getId())) {
                    p.setStock(p.getStock() - ci.getQuantity());
                    break;
                }
            }
        }

        storageManager.saveProducts(allProducts);

        String txnNumber = "TXN-" + System.currentTimeMillis();
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        Sale sale = new Sale(txnNumber, dateTime, summary.toString(), total, 0.0, total, cash, change);

        List<Sale> sales = storageManager.getSales();
        sales.add(sale);
        storageManager.saveSales(sales);

        Toast.makeText(requireContext(), "Sale Completed! Change: ₱" + String.format(Locale.getDefault(), "%.2f", change), Toast.LENGTH_LONG).show();

        // Clear cart and cash
        cartItems.clear();
        etCash.setText("");
        updateCartUI();
        loadProducts();
    }
}
