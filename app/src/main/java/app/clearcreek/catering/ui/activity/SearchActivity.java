package app.clearcreek.catering.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Objects;

import app.clearcreek.catering.data.model.ProductItem;
import app.clearcreek.catering.databinding.ActivitySearchBinding;
import app.clearcreek.catering.ui.adapter.SearchProductAdapter;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private SearchProductAdapter adapter;

    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    finish();
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ArrayList<ProductItem> productItems = getIntent().getParcelableArrayListExtra("products");
        adapter = new SearchProductAdapter();
        adapter.setOnAdapterDatasetChangedListener(size -> {
            if (size > 0) {
                binding.list.setVisibility(View.VISIBLE);
                binding.empty.setVisibility(View.GONE);
            } else {
                binding.list.setVisibility(View.GONE);
                binding.empty.setVisibility(View.VISIBLE);
            }
        });
        adapter.setOnItemClickListener((item, position) -> {
            Intent intent = new Intent(this, ProductDetailsActivity.class);
            intent.putExtra(ProductDetailsActivity.EXTRA_PRODUCT, item);
            intent.putExtra(ProductDetailsActivity.EXTRA_IS_FROM_SEARCH, true);
            activityResult.launch(intent);
        });
        adapter.addProducts(productItems);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.list.setLayoutManager(layoutManager);
        binding.list.setAdapter(adapter);

        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
