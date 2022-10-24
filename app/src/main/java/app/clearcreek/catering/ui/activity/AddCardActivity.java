package app.clearcreek.catering.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.view.MenuItem;
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
import app.clearcreek.catering.databinding.ActivityAddCardBinding;
import app.clearcreek.catering.utils.StringUtils;

public class AddCardActivity extends AppCompatActivity {

    public static final String EXTRA_CART = "cart";

    private ActivityAddCardBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Cart cart = getIntent().getParcelableExtra(EXTRA_CART);

        Spanned cardHolderLabel = StringUtils.parseHtml(getString(R.string.cardholder_required_label));
        binding.cardHolderNameLabel.setText(cardHolderLabel);

        Spanned cardNumberLabel = StringUtils.parseHtml(getString(R.string.card_required_label));
        binding.cardNumberLabel.setText(cardNumberLabel);

        Spanned cvvLabel = StringUtils.parseHtml(getString(R.string.cvv_required_label));
        binding.cvvLabel.setText(cvvLabel);

        Spanned expiryLabel = StringUtils.parseHtml(getString(R.string.expiry_required_label));
        binding.expiryLabel.setText(expiryLabel);

        binding.addCardButton.setOnClickListener(v -> {
            Toast.makeText(this, "Card Added Successfully.", Toast.LENGTH_SHORT).show();
            finish();
        });

        binding.payNowButton.setOnClickListener(v -> {
            cart.setPaymentMethod(PaymentMethod.CREDIT);
            payNow(cart);
        });
    }

    private void payNow(Cart cart) {
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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
