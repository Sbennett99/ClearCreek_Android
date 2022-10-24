package app.clearcreek.catering.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.clearcreek.catering.data.model.Product;
import app.clearcreek.catering.data.model.ProductCategory;
import app.clearcreek.catering.data.model.ProductItem;
import app.clearcreek.catering.databinding.ItemProductBinding;
import app.clearcreek.catering.databinding.ItemProductCategoryBinding;

public class HomeProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ProductItem> list;
    private int selectedPosition = -1;

    public HomeProductAdapter() {
        list = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new HeaderViewHolder(ItemProductCategoryBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            ));
        } else {
            return new ItemViewHolder(ItemProductBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            ));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).onBind(position);
            return;
        }

        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).onBind(position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        ProductItem item = list.get(position);
        if (item instanceof ProductCategory) {
            return 1;
        } else {
            return 2;
        }
    }

    @Nullable
    public Product getSelectedProduct() {
        return (selectedPosition > -1 && selectedPosition < list.size()) ? (Product) list.get(selectedPosition) : null;
    }

    public void addProduct(ProductItem product) {
        list.add(product);
        notifyItemInserted(getItemCount());
    }

    public void addProducts(List<ProductItem> productItems) {
        for (ProductItem productItem : productItems) {
            addProduct(productItem);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final ItemProductCategoryBinding binding;

        public HeaderViewHolder(@NonNull ItemProductCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void onBind(int position) {
            ProductCategory productCategory = (ProductCategory) list.get(position);
            binding.text.setText(productCategory.getName());
        }
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductBinding binding;

        public ItemViewHolder(@NonNull ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void onBind(int position) {
            Product product = (Product) list.get(position);
            binding.productName.setText(product.getName());
            binding.productImage.setContentDescription(product.getName());
            binding.item.setChecked(selectedPosition == position);

            Picasso.get().load(product.getImage()).into(binding.productImage);

            binding.item.setOnClickListener(v -> {
                int lastSelectedPosition = selectedPosition;
                selectedPosition = position;

                if (lastSelectedPosition > -1) {
                    notifyItemChanged(lastSelectedPosition);
                }
                notifyItemChanged(selectedPosition);
            });
        }
    }
}
