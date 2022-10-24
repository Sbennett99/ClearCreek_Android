package app.clearcreek.catering.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import app.clearcreek.catering.AppController;
import app.clearcreek.catering.R;
import app.clearcreek.catering.data.model.Cart;
import app.clearcreek.catering.data.model.CartProduct;
import app.clearcreek.catering.data.model.Product;
import app.clearcreek.catering.data.model.ProductVariant;
import app.clearcreek.catering.databinding.ActivityProductDetailsBinding;
import app.clearcreek.catering.utils.StringUtils;

public class ProductDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT = "product";
    public static final String EXTRA_IS_FROM_SEARCH = "is_from_search";

    private ActivityProductDetailsBinding binding;

    private boolean isToppingSelectionRequired = false;
    private boolean isVariantSelectionRequired = false;

    private String selectedTopping = null;
    private ProductVariant selectedVariant = null;
    private boolean isFromSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        isFromSearch = getIntent().getBooleanExtra(EXTRA_IS_FROM_SEARCH, false);
        Product product = getIntent().getParcelableExtra(EXTRA_PRODUCT);

        if (product != null) {
            binding.productImage.setContentDescription(product.getName());
            binding.productName.setText(product.getName());
            binding.productDescription.setText(product.getDescription());

            Picasso.get().load(product.getImage()).into(binding.productImage);

            if (product.getPrice() > 0.0) {
                String price = StringUtils.formatCurrency(product.getPrice());
                binding.totalPrice.setText(price);
                binding.totalPrice.setVisibility(View.VISIBLE);
            } else {
                binding.totalPrice.setVisibility(View.GONE);
            }

            setupToppingsLayout(product.getToppings());
            setupVariantLayout(product.getVariants());

            binding.addToCartButton.setOnClickListener(v -> addToCart(product));
        } else {
            Toast.makeText(this, "Invalid Product Info", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToppingsLayout(List<String> toppings) {

        if (!toppings.isEmpty()) {
            isToppingSelectionRequired = true;

            binding.toppingsLayout.setVisibility(View.VISIBLE);

            for (String topping : toppings) {
                RadioButton button = new RadioButton(this);
                button.setText(topping);
                binding.toppings.addView(button);
            }

            binding.toppings.setOnCheckedChangeListener((group, checkedId) -> {
                RadioButton button = group.findViewById(checkedId);
                if (button != null) {
                    selectedTopping = button.getText().toString();
                }
            });
        } else {
            binding.toppingsLayout.setVisibility(View.GONE);
        }
    }

    private void setupVariantLayout(List<ProductVariant> variants) {

        if (!variants.isEmpty()) {
            isVariantSelectionRequired = true;

            binding.variantsLayout.setVisibility(View.VISIBLE);

            for (ProductVariant variant : variants) {
                RadioButton button = new RadioButton(this);
                button.setText(variant.getName());
                button.setTag(String.valueOf(variant.getPrice()));
                binding.variants.addView(button);
            }

            binding.variants.setOnCheckedChangeListener((group, checkedId) -> {
                RadioButton button = group.findViewById(checkedId);
                if (button != null) {
                    selectedVariant = new ProductVariant(
                            button.getText().toString(),
                            Double.parseDouble(button.getTag().toString())
                    );

                    if (selectedVariant.getPrice() > 0.0) {
                        String price = StringUtils.formatCurrency(selectedVariant.getPrice());
                        binding.totalPrice.setText(price);
                        binding.totalPrice.setVisibility(View.VISIBLE);
                    } else {
                        binding.totalPrice.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            binding.variantsLayout.setVisibility(View.GONE);
        }
    }

    private void addToCart(Product product) {
        if (isToppingSelectionRequired && selectedTopping == null) {
            showToast(R.string.select_topping_error_message);
            return;
        }

        if (isVariantSelectionRequired && selectedVariant == null) {
            showToast(R.string.select_topping_error_message);
            return;
        }

        String comments = binding.productComments.getText().toString().trim();

        Cart cart = AppController.getPreferencesHelper().getCart();

        String variant;
        double price;
        if (selectedVariant != null && isVariantSelectionRequired) {
            price = selectedVariant.getPrice();
            variant = selectedVariant.getName();
        } else {
            price = product.getPrice();
            variant = null;
        }

        String topping = null;
        if (selectedTopping != null && isToppingSelectionRequired) {
            topping = selectedTopping;
        }

        CartProduct cartProduct = new CartProduct(
                product.getId(),
                product.getPath(),
                product.getName(),
                product.getImage(),
                price,
                variant,
                topping,
                product.getSauce(),
                product.getBread(),
                comments
        );

        cart.addProduct(cartProduct);

        AppController.getPreferencesHelper().saveCart(cart);
        showToast(R.string.add_to_cart_success);

        if(isFromSearch){
            setResult(RESULT_OK);
        }
        finish();
    }

    private void showToast(@StringRes int resource) {
        Toast.makeText(this, resource, Toast.LENGTH_SHORT).show();
    }
}
