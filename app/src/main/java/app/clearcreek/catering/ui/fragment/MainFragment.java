package app.clearcreek.catering.ui.fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationBarView;

import app.clearcreek.catering.AppController;
import app.clearcreek.catering.R;
import app.clearcreek.catering.data.model.Cart;
import app.clearcreek.catering.databinding.FragmentMainBinding;

public class MainFragment extends Fragment implements NavigationBarView.OnItemSelectedListener {

    private FragmentMainBinding binding;

    public MainFragment() {
        super(R.layout.fragment_main);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentMainBinding.bind(view);

        binding.bottomNavigation.setOnItemSelectedListener(this);
        binding.bottomNavigation.setSelectedItemId(R.id.action_home);

    }

    @Override
    public void onResume() {
        super.onResume();

        updateCartBadge();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (id) {
            case R.id.action_home:
                fragment = new HomeFragment();
                title = getString(R.string.home_label);
                break;
            case R.id.action_menu:
                fragment = new HistoryFragment();
                title = getString(R.string.menew_label);
                break;
            case R.id.action_location:
                fragment = new LocationsFragment();
                title = getString(R.string.locations_label);
                break;
            case R.id.action_cart:
                fragment = new CartFragment();
                title = getString(R.string.cart_label);
        }

        if (fragment != null) {
            replaceFragment(fragment, title);
            item.setChecked(true);
        }
        return true;
    }

    private void replaceFragment(Fragment fragment, String title) {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        requireActivity().setTitle(title);
    }

    public void updateCartBadge() {
        BadgeDrawable badge = binding.bottomNavigation.getOrCreateBadge(R.id.action_cart);
        Cart cart = AppController.getPreferencesHelper().getCart();
        if (cart != null && !cart.getProducts().isEmpty()) {
            badge.setNumber(cart.getProducts().size());
            badge.setVisible(true);
        } else {
            badge.setVisible(false);
        }
    }
}
