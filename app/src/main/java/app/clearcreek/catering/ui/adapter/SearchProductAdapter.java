package app.clearcreek.catering.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.clearcreek.catering.data.model.Product;
import app.clearcreek.catering.data.model.ProductItem;
import app.clearcreek.catering.databinding.ItemSearchProductBinding;
import app.clearcreek.catering.listeners.OnAdapterDatasetChangedListener;
import app.clearcreek.catering.listeners.OnItemClickListener;
import app.clearcreek.catering.utils.StringUtils;

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.ItemViewHolder> implements Filterable {

    private final List<ProductItem> list;
    private final List<ProductItem> filterList;

    private OnItemClickListener<ProductItem> listener;
    private OnAdapterDatasetChangedListener datasetChangedListener;

    public SearchProductAdapter() {
        list = new ArrayList<>();
        filterList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(ItemSearchProductBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }


    public void addProduct(ProductItem product) {
        list.add(product);
        filterList.add(product);
        notifyItemInserted(getItemCount());
    }

    public void addProducts(List<ProductItem> productItems) {
        for (ProductItem productItem : productItems) {
            if (productItem instanceof Product) {
                addProduct(productItem);
            }
        }
        if(datasetChangedListener != null) {
            datasetChangedListener.onDataSetChanged(getItemCount());
        }
    }

    @Override
    public Filter getFilter() {
        return new ProductFilter();
    }

    public void setOnAdapterDatasetChangedListener(OnAdapterDatasetChangedListener datasetChangedListener) {
        this.datasetChangedListener = datasetChangedListener;
    }
    public void setOnItemClickListener(OnItemClickListener<ProductItem> listener) {
        this.listener = listener;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemSearchProductBinding binding;

        public ItemViewHolder(@NonNull ItemSearchProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void onBind(int position) {
            Product product = (Product) filterList.get(position);
            binding.productName.setText(product.getName());
            binding.productDescription.setText(product.getDescription());
            if (product.getPrice() > 0.0) {
                binding.productPrice.setText(StringUtils.formatCurrency(product.getPrice()));
            }
            binding.productImage.setContentDescription(product.getName());

            Picasso.get().load(product.getImage()).into(binding.productImage);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(product, position);
                }
            });
        }
    }

    class ProductFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            List<ProductItem> fl = new ArrayList<>();
            if (filterString.isEmpty()) {
                fl.addAll(list);
            } else {
                for (ProductItem item : list) {
                    if (item.getName().toLowerCase().contains(filterString)) {
                        fl.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = fl;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filterList.clear();
            filterList.addAll((ArrayList<ProductItem>) results.values);
            notifyDataSetChanged();
            if(datasetChangedListener != null) {
                datasetChangedListener.onDataSetChanged(getItemCount());
            }
        }
    }
}
