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
import com.example.j4rr.util.AppUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PosFragment extends Fragment implements PosProductAdapter.OnPosProductListener, CartAdapter.OnCartListener {

    private LocalStorageManager storageManager;
    private RecyclerView rvProducts;
    private TextView tvNoProducts;
    private EditText etSearch;
    private ExtendedFloatingActionButton fabCart;

    private List<Product> allProducts = new ArrayList<>();
    private final List<Product> filteredProducts = new ArrayList<>();
    private final List<CartItem> cartItems = new ArrayList<>();

    private PosProductAdapter productAdapter;
    private BottomSheetDialog cartDialog;
    private String selectedCategory = "All";

    // Cart dialog views
    private TextView tvCartEmpty, tvSubtotal, tvDiscount, tvTotal, tvChange;
    private TextInputLayout tilPosCash;
    private EditText etCash;
    private RecyclerView rvCart;
    private Button btnCompleteSale;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pos, container, false);

        storageManager = new LocalStorageManager(requireContext());

        etSearch = view.findViewById(R.id.etPosSearch);
        rvProducts = view.findViewById(R.id.recyclerViewPosProducts);
        tvNoProducts = view.findViewById(R.id.tvPosNoProducts);
        fabCart = view.findViewById(R.id.fabCart);

        Chip chipAll = view.findViewById(R.id.chipPosAll);
        Chip chipDog = view.findViewById(R.id.chipPosDog);
        Chip chipCat = view.findViewById(R.id.chipPosCat);
        Chip chipBird = view.findViewById(R.id.chipPosBird);
        Chip chipFish = view.findViewById(R.id.chipPosFish);

        rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadProducts();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        View.OnClickListener chipListener = v -> {
            chipAll.setChecked(v.getId() == R.id.chipPosAll);
            chipDog.setChecked(v.getId() == R.id.chipPosDog);
            chipCat.setChecked(v.getId() == R.id.chipPosCat);
            chipBird.setChecked(v.getId() == R.id.chipPosBird);
            chipFish.setChecked(v.getId() == R.id.chipPosFish);

            if (v.getId() == R.id.chipPosAll) selectedCategory = "All";
            else if (v.getId() == R.id.chipPosDog) selectedCategory = "Dog";
            else if (v.getId() == R.id.chipPosCat) selectedCategory = "Cat";
            else if (v.getId() == R.id.chipPosBird) selectedCategory = "Bird";
            else if (v.getId() == R.id.chipPosFish) selectedCategory = "Fish";

            filterProducts();
        };

        chipAll.setOnClickListener(chipListener);
        chipDog.setOnClickListener(chipListener);
        chipCat.setOnClickListener(chipListener);
        chipBird.setOnClickListener(chipListener);
        chipFish.setOnClickListener(chipListener);

        fabCart.setOnClickListener(v -> showCartDialog());

        updateCartButtonBadge();

        return view;
    }

    private void loadProducts() {
        allProducts = storageManager.getProducts();
        filterProducts();
    }

    private void filterProducts() {
        String query = etSearch.getText() != null ? etSearch.getText().toString().trim().toLowerCase() : "";
        filteredProducts.clear();

        for (Product p : allProducts) {
            boolean matchesSearch = p.getName().toLowerCase().contains(query) || p.getCategory().toLowerCase().contains(query);
            boolean matchesCategory = selectedCategory.equals("All") || p.getCategory().equalsIgnoreCase(selectedCategory);

            if (matchesSearch && matchesCategory) {
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
            Toast.makeText(requireContext(), "This product is out of stock.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireContext(), "Only " + product.getStock() + " units are available.", Toast.LENGTH_SHORT).show();
            }
        } else {
            cartItems.add(new CartItem(product, 1));
        }

        updateCartButtonBadge();
        if (cartDialog != null && cartDialog.isShowing()) {
            updateCartDialogUI();
        }
        Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onIncrease(CartItem item) {
        if (item.getQuantity() < item.getProduct().getStock()) {
            item.setQuantity(item.getQuantity() + 1);
            updateCartButtonBadge();
            updateCartDialogUI();
        } else {
            Toast.makeText(requireContext(), "Only " + item.getProduct().getStock() + " units are available.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDecrease(CartItem item) {
        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
        } else {
            cartItems.remove(item);
        }
        updateCartButtonBadge();
        updateCartDialogUI();
    }

    @Override
    public void onRemove(CartItem item) {
        cartItems.remove(item);
        updateCartButtonBadge();
        updateCartDialogUI();
    }

    private void updateCartButtonBadge() {
        int totalQty = 0;
        for (CartItem item : cartItems) {
            totalQty += item.getQuantity();
        }
        fabCart.setText("🛒 Cart (" + totalQty + ")");
    }

    private void showCartDialog() {
        cartDialog = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_cart, null, false);
        cartDialog.setContentView(sheetView);

        tvCartEmpty = sheetView.findViewById(R.id.tvCartEmpty);
        rvCart = sheetView.findViewById(R.id.recyclerViewCart);
        tvSubtotal = sheetView.findViewById(R.id.tvPosSubtotal);
        tvDiscount = sheetView.findViewById(R.id.tvPosDiscount);
        tvTotal = sheetView.findViewById(R.id.tvPosTotal);
        tilPosCash = sheetView.findViewById(R.id.tilPosCash);
        etCash = sheetView.findViewById(R.id.etPosCash);
        tvChange = sheetView.findViewById(R.id.tvPosChange);
        btnCompleteSale = sheetView.findViewById(R.id.btnCompleteSale);

        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));

        etCash.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilPosCash != null) tilPosCash.setError(null);
                calculateChange();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnCompleteSale.setOnClickListener(v -> completeSale());

        updateCartDialogUI();
        cartDialog.show();
    }

    private void updateCartDialogUI() {
        if (cartItems.isEmpty()) {
            tvCartEmpty.setVisibility(View.VISIBLE);
            rvCart.setVisibility(View.GONE);
        } else {
            tvCartEmpty.setVisibility(View.GONE);
            rvCart.setVisibility(View.VISIBLE);
            CartAdapter cartAdapter = new CartAdapter(cartItems, this);
            rvCart.setAdapter(cartAdapter);
        }

        double subtotal = 0.0;
        for (CartItem item : cartItems) {
            subtotal += item.getSubtotal();
        }

        double discount = 0.0;
        double total = subtotal - discount;

        if (tvSubtotal != null) tvSubtotal.setText(AppUtils.formatCurrency(subtotal));
        if (tvDiscount != null) tvDiscount.setText(AppUtils.formatCurrency(discount));
        if (tvTotal != null) tvTotal.setText(AppUtils.formatCurrency(total));
        calculateChange();
    }

    private void calculateChange() {
        if (tvChange == null || etCash == null) return;
        double total = getCartTotal();
        String cashStr = etCash.getText() != null ? etCash.getText().toString().trim() : "";
        if (!cashStr.isEmpty()) {
            try {
                double cash = Double.parseDouble(cashStr);
                double change = cash - total;
                if (change >= 0) {
                    tvChange.setText(AppUtils.formatCurrency(change));
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
        if (tilPosCash != null) tilPosCash.setError(null);

        if (cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Your cart is empty. Add at least one product before completing the sale.", Toast.LENGTH_SHORT).show();
            return;
        }

        String cashStr = etCash.getText() != null ? etCash.getText().toString().trim() : "";
        if (AppUtils.isEmpty(cashStr)) {
            if (tilPosCash != null) {
                tilPosCash.setError("Enter a valid cash amount.");
            } else {
                etCash.setError("Enter a valid cash amount.");
            }
            return;
        }

        double total = getCartTotal();
        double cash;
        try {
            cash = Double.parseDouble(cashStr);
            if (cash < 0) {
                if (tilPosCash != null) tilPosCash.setError("Enter a valid cash amount.");
                return;
            }
        } catch (NumberFormatException e) {
            if (tilPosCash != null) {
                tilPosCash.setError("Enter a valid cash amount.");
            } else {
                etCash.setError("Enter a valid cash amount.");
            }
            return;
        }

        if (cash < total) {
            double remaining = total - cash;
            String errMsg = "Insufficient payment. Remaining amount: " + AppUtils.formatCurrency(remaining);
            if (tilPosCash != null) {
                tilPosCash.setError(errMsg);
            } else {
                etCash.setError(errMsg);
            }
            Toast.makeText(requireContext(), errMsg, Toast.LENGTH_SHORT).show();
            return;
        }

        double change = cash - total;

        // Verify stock sufficiency for all cart items before completing sale
        for (CartItem ci : cartItems) {
            for (Product p : allProducts) {
                if (p.getId().equals(ci.getProduct().getId())) {
                    if (p.getStock() < ci.getQuantity()) {
                        Toast.makeText(requireContext(), "Insufficient stock for " + p.getName() + ". Available: " + p.getStock(), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
        }

        StringBuilder summary = new StringBuilder();
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem ci = cartItems.get(i);
            summary.append(ci.getProduct().getName()).append(" (x").append(ci.getQuantity()).append(")");
            if (i < cartItems.size() - 1) summary.append(", ");

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

        Toast.makeText(requireContext(), "Sale completed successfully! Change: " + AppUtils.formatCurrency(change), Toast.LENGTH_LONG).show();

        cartItems.clear();
        updateCartButtonBadge();
        if (cartDialog != null) {
            cartDialog.dismiss();
        }
        loadProducts();
    }
}
