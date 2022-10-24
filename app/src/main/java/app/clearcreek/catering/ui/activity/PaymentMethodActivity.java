package app.clearcreek.catering.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;
import java.util.Objects;

import app.clearcreek.catering.AppController;
import app.clearcreek.catering.R;
import app.clearcreek.catering.data.model.Cart;
import app.clearcreek.catering.data.model.PaymentMethod;
import app.clearcreek.catering.databinding.ActivityPaymentMethodBinding;

public class PaymentMethodActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_CART = "cart";

    private ActivityPaymentMethodBinding binding;
    private Cart cart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentMethodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        cart = getIntent().getParcelableExtra(EXTRA_CART);

        if (cart == null) {
            Toast.makeText(this, "Unknown Error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        switch (cart.getPaymentMethod()) {
            case CREDIT:
                binding.creditLayout.setChecked(true);
                break;
            case DEBIT:
                binding.debitLayout.setChecked(true);
                break;
            case CASH:
                binding.cashLayout.setChecked(true);
        }

        binding.creditLayout.setOnClickListener(this);
        binding.debitLayout.setOnClickListener(this);
        binding.cashLayout.setOnClickListener(this);
        binding.addCardLayout.setOnClickListener(this);
        binding.payNowButton.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.credit_layout:
                binding.creditLayout.setChecked(true);
                binding.debitLayout.setChecked(false);
                binding.cashLayout.setChecked(false);
                cart.setPaymentMethod(PaymentMethod.CREDIT);
                break;
            case R.id.debit_layout:
                binding.debitLayout.setChecked(true);
                binding.creditLayout.setChecked(false);
                binding.cashLayout.setChecked(false);
                cart.setPaymentMethod(PaymentMethod.DEBIT);
                break;
            case R.id.cash_layout:
                binding.cashLayout.setChecked(true);
                binding.creditLayout.setChecked(false);
                binding.debitLayout.setChecked(false);
                cart.setPaymentMethod(PaymentMethod.CASH);
                break;
            case R.id.add_card_layout:
                addCard();
                break;
            case R.id.pay_now_button:
                payNow();
                break;
        }
    }

    private void addCard() {
        Intent intent = new Intent(this, AddCardActivity.class);
        intent.putExtra(AddCardActivity.EXTRA_CART, cart);
        startActivity(intent);
    }

    private void payNow() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading_message));
        progressDialog.show();

        Map<String, Object> order = cart.toMap();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            order.put("userId", currentUser.getUid());
        }

        String id = AppController.getDb().collection("orders").document().getId();
        AppController.getDb().collection("orders")
                .document(id)
                .set(order)
                .addOnSuccessListener(task -> {
                    progressDialog.hide();
                    AppController.getPreferencesHelper().clearCart();

                    Intent intent = new Intent(this, OrderSummaryActivity.class);
                    intent.putExtra(OrderSummaryActivity.EXTRA_ORDER_ID, id);
                    startActivity(intent);
                    finishAffinity();
                })
                .addOnFailureListener(e -> {
                    progressDialog.hide();
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

