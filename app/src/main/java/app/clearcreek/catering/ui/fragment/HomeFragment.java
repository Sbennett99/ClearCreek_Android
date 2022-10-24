package app.clearcreek.catering.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import app.clearcreek.catering.AppController;
import app.clearcreek.catering.R;
import app.clearcreek.catering.data.model.Product;
import app.clearcreek.catering.data.model.ProductCategory;
import app.clearcreek.catering.data.model.ProductItem;
import app.clearcreek.catering.data.model.ProductOffer;
import app.clearcreek.catering.databinding.FragmentHomeBinding;
import app.clearcreek.catering.ui.activity.ProductDetailsActivity;
import app.clearcreek.catering.ui.activity.SearchActivity;
import app.clearcreek.catering.ui.adapter.HomeProductAdapter;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private HomeProductAdapter adapter;

    private Queue<ProductCategory> productCategories = new LinkedList<>();
    private List<ProductItem> productItems = new LinkedList<>();

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentHomeBinding.bind(view);

        adapter = new HomeProductAdapter();

        binding.search.setInputType(InputType.TYPE_NULL);
        binding.search.setFocusable(false);
        binding.search.setOnClickListener(v -> openSearchActivity());
        binding.search.setOnClickListener(v -> openSearchActivity());


        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position) == 1) {
                    return 2;
                }
                return 1;
            }
        });
        binding.list.setLayoutManager(layoutManager);
        binding.list.setAdapter(adapter);

        binding.nextButton.setOnClickListener(v -> {
            Product product = adapter.getSelectedProduct();
            if (product != null) {
                Intent intent = new Intent(requireContext(), ProductDetailsActivity.class);
                intent.putExtra(ProductDetailsActivity.EXTRA_PRODUCT, product);
                startActivity(intent);
            }
        });

        loadHomePage();
        loadProducts();
    }

    private void openSearchActivity() {
        Intent intent = new Intent(requireContext(), SearchActivity.class);
        intent.putExtra("products", new ArrayList<>(productItems));
        startActivity(intent);
    }

    private void updateUi(List<String> slides, List<ProductOffer> offers) {
        List<SlideModel> slideList = new ArrayList<>();
        for (String slide : slides) {
            slideList.add(new SlideModel(slide, ScaleTypes.CENTER_CROP));
        }
        binding.imageSlider.setImageList(slideList);

        List<SlideModel> offerSlideList = new ArrayList<>();
        for (ProductOffer offer : offers) {
            offerSlideList.add(new SlideModel(offer.getImage(), offer.getName(), ScaleTypes.CENTER_CROP));
        }
        binding.offerSlider.setImageList(offerSlideList);
        binding.offerSlider.setItemClickListener(i -> {
            ProductOffer productOffer = offers.get(i);
            loadProduct(productOffer.getReference());
        });

        binding.progress.setVisibility(View.GONE);
        binding.content.setVisibility(View.VISIBLE);
    }

    private void loadHomePage() {
        binding.progress.setVisibility(View.VISIBLE);

        AppController.getDb().collection("pages")
                .document("home")
                .get()
                .addOnSuccessListener(document -> {
                    List<String> slides = (List<String>) document.get("slides");

                    List<Map<String, Object>> offers = (List<Map<String, Object>>) document.get("offers");
                    List<ProductOffer> list = new ArrayList<>();
                    for (Map<String, Object> map : offers) {
                        DocumentReference reference = (DocumentReference) map.get("productReference");
                        ProductOffer productOffer = ProductOffer.fromMap(map, reference);
                        list.add(productOffer);
                    }

                    updateUi(slides, list);
                })
                .addOnFailureListener(e -> {
                    binding.progress.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadProduct(DocumentReference reference) {
        reference.get()
                .addOnSuccessListener(document -> {
                    Product product = Product.fromDocumentSnapShot(document);
                    if (product != null) {
                        Intent intent = new Intent(requireContext(), ProductDetailsActivity.class);
                        intent.putExtra(ProductDetailsActivity.EXTRA_PRODUCT, product);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    private void loadProducts() {
        AppController.getDb().collection("categories")
                .get()
                .addOnSuccessListener(task -> {
                    List<ProductCategory> list = new LinkedList<>();
                    for (DocumentSnapshot documentSnapshot : task.getDocuments()) {
                        ProductCategory category = new ProductCategory(
                                documentSnapshot.getId(),
                                documentSnapshot.get("name", String.class),
                                documentSnapshot.getReference().getPath()
                        );

                        productCategories.add(category);
                    }

                    loadCategoryProduct();
                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadCategoryProduct() {
        binding.progress.setVisibility(View.VISIBLE);

        ProductCategory productCategory = productCategories.poll();
        if (productCategory == null) {
            binding.progress.setVisibility(View.GONE);
            binding.searchLayout.setVisibility(View.VISIBLE);
            adapter.addProducts(productItems);
            return;
        }

        AppController.getDb().collection(productCategory.getPath() + "/products")
                .get()
                .addOnSuccessListener(task -> {
                    List<Product> products = new LinkedList<>();

                    for (DocumentSnapshot snapshot : task.getDocuments()) {
                        Product product = Product.fromDocumentSnapShot(snapshot);
                        if (product != null) {
                            products.add(product);
                        }
                    }

                    if (!products.isEmpty()) {
                        productItems.add(productCategory);
                        productItems.addAll(products);
                    }

                    loadCategoryProduct();
                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                    binding.progress.setVisibility(View.GONE);
                    binding.list.setVisibility(View.GONE);
                });
    }

}
