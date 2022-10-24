package app.clearcreek.catering.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import app.clearcreek.catering.AppController;
import app.clearcreek.catering.R;
import app.clearcreek.catering.data.model.Cart;
import app.clearcreek.catering.data.model.Order;
import app.clearcreek.catering.databinding.ActivityOrderSummaryBinding;
import app.clearcreek.catering.ui.adapter.CartAdapter;
import app.clearcreek.catering.ui.adapter.OrderSummaryAdapter;
import app.clearcreek.catering.utils.StringUtils;

public class OrderSummaryActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "orderId";

    private ActivityOrderSummaryBinding binding;
    private OrderSummaryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderSummaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);
        getOrderSummary(orderId);


        binding.homeButton.setOnClickListener(v -> goToMain());
    }

    private void updateUi(Order order) {
        binding.orderNumber.setText(getString(R.string.order_number_label, order.getOrderNumber()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.list.setLayoutManager(layoutManager);

        adapter = new OrderSummaryAdapter(order.getProducts());
        binding.list.setAdapter(adapter);

        binding.tax.setText(StringUtils.formatCurrency(order.getTaxAmount()));
        binding.total.setText(StringUtils.formatCurrency(order.getTotalAmount()));

        binding.progress.setVisibility(View.GONE);
        binding.content.setVisibility(View.VISIBLE);
        binding.homeButton.setVisibility(View.VISIBLE);
    }

    private void getOrderSummary(String orderId) {
        binding.progress.setVisibility(View.VISIBLE);
        binding.content.setVisibility(View.GONE);
        binding.homeButton.setVisibility(View.GONE);
        AppController.getDb().collection("orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(document -> {
                    Order order = Order.createFromDocument(document);
                    if(order != null) {
                        updateUi(order);
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    goToMain();
                });
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
