package app.clearcreek.catering.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import app.clearcreek.catering.data.model.CartProduct;
import app.clearcreek.catering.databinding.ItemOrderSummaryProductBinding;
import app.clearcreek.catering.utils.StringUtils;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder> {

    private final List<CartProduct> list;

    public OrderSummaryAdapter(List<CartProduct> products) {
        list = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderSummaryProductBinding binding = ItemOrderSummaryProductBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemOrderSummaryProductBinding binding;

        public ViewHolder(@NonNull ItemOrderSummaryProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void onBind(int position) {
            CartProduct product = list.get(position);
            binding.productName.setText(String.format(Locale.getDefault(), "%dx %s", product.getQuantity(), product.getName()));
            binding.productDescription.setText(product.getDescription());
            binding.productPrice.setText(StringUtils.formatCurrency(product.getPrice()));
        }
    }
}
