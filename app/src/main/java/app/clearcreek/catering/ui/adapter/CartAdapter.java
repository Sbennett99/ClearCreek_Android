package app.clearcreek.catering.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.clearcreek.catering.data.model.CartProduct;
import app.clearcreek.catering.databinding.ItemCartProductBinding;
import app.clearcreek.catering.utils.StringUtils;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final List<CartProduct> list;
    private OnCartUpdateListener listener;

    public CartAdapter(List<CartProduct> products) {
        list = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartProductBinding binding = ItemCartProductBinding.inflate(
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

    public void setOnCartUpdateListener(OnCartUpdateListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemCartProductBinding binding;

        public ViewHolder(@NonNull ItemCartProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void onBind(int position) {
            CartProduct product = list.get(position);
            binding.productImage.setContentDescription(product.getName());
            binding.productName.setText(product.getName());

            binding.productDescription.setText(product.getDescription());
            binding.productDescription.setSelected(true);

            binding.quantity.setText(String.valueOf(product.getQuantity()));

            binding.productPrice.setText(StringUtils.formatCurrency(product.getPrice()));
            Picasso.get().load(product.getImage()).into(binding.productImage);

            binding.minusButton.setOnClickListener(v -> {
                if (product.getQuantity() == 1) {
                    list.remove(position);
                    notifyItemRemoved(position);
                    listener.onItemRemoved(position);
                } else {
                    int qty = product.getQuantity();
                    product.setQuantity(--qty);
                    notifyItemChanged(position);
                    listener.onQuantityUpdated(qty, position);
                }
            });

            binding.plusButton.setOnClickListener(v -> {
                int qty = product.getQuantity();
                product.setQuantity(++qty);
                notifyItemChanged(position);
                listener.onQuantityUpdated(qty, position);
            });
        }
    }

    public interface OnCartUpdateListener {
        void onQuantityUpdated(int quantity, int position);

        void onItemRemoved(int position);
    }
}
