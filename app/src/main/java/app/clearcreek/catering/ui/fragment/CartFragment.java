package app.clearcreek.catering.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.clearcreek.catering.AppController;
import app.clearcreek.catering.R;
import app.clearcreek.catering.data.model.Cart;
import app.clearcreek.catering.databinding.FragmentCartBinding;
import app.clearcreek.catering.ui.activity.LoginActivity;
import app.clearcreek.catering.ui.activity.PaymentMethodActivity;
import app.clearcreek.catering.ui.adapter.CartAdapter;
import app.clearcreek.catering.utils.StringUtils;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private CartAdapter adapter;

    private Cart cart;

    public CartFragment() {
        super(R.layout.fragment_cart);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCartBinding.bind(view);

        cart = AppController.getPreferencesHelper().getCart();

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.list.setLayoutManager(layoutManager);

        adapter = new CartAdapter(cart.getProducts());
        adapter.setOnCartUpdateListener(new CartAdapter.OnCartUpdateListener() {
            @Override
            public void onQuantityUpdated(int quantity, int position) {
                cart.calculate();
                AppController.getPreferencesHelper().saveCart(cart);
                updateLayout();
            }

            @Override
            public void onItemRemoved(int position) {
                cart.calculate();
                AppController.getPreferencesHelper().saveCart(cart);
                updateLayout();

                if (getParentFragment() != null && getParentFragment() instanceof MainFragment) {
                    ((MainFragment) getParentFragment()).updateCartBadge();
                }
            }
        });

        binding.list.setAdapter(adapter);

        cart.calculate();
        updateLayout();

        binding.checkOutButton.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.isAnonymous()) {
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(requireContext(), PaymentMethodActivity.class);
                intent.putExtra(PaymentMethodActivity.EXTRA_CART, cart);
                startActivity(intent);
            }
        });
    }

    private void updateLayout() {
        if (!cart.getProducts().isEmpty()) {
            binding.tax.setText(StringUtils.formatCurrency(cart.getTaxAmount()));
            binding.total.setText(StringUtils.formatCurrency(cart.getTotalAmount()));

            binding.list.setVisibility(View.VISIBLE);
            binding.totalsLayout.setVisibility(View.VISIBLE);
            binding.empty.setVisibility(View.GONE);
        } else {
            binding.list.setVisibility(View.GONE);
            binding.totalsLayout.setVisibility(View.GONE);
            binding.empty.setVisibility(View.VISIBLE);
        }
    }
}
