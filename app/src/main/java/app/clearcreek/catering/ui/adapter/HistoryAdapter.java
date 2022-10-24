package app.clearcreek.catering.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.clearcreek.catering.R;
import app.clearcreek.catering.data.model.CartProduct;
import app.clearcreek.catering.data.model.Order;
import app.clearcreek.catering.databinding.ItemHistoryBinding;
import app.clearcreek.catering.listeners.OnItemClickListener;
import app.clearcreek.catering.utils.StringUtils;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<Order> list;

    private OnItemClickListener<Order> listener;

    public HistoryAdapter() {
        list = new ArrayList<>();
    }

    public void addItem(Order item) {
        list.add(item);
        notifyItemInserted(getItemCount());
    }

    public void addItems(List<Order> items) {
        for(Order item : items) {
            addItem(item);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(
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

    public void setOnItemClickListener(OnItemClickListener<Order> listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemHistoryBinding binding;

        public ViewHolder(@NonNull ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void onBind(int position) {
            Context ctx = itemView.getContext();

            Order order = list.get(position);

            String orderNo = ctx.getString(R.string.order_number_label, order.getOrderNumber());
            binding.orderNumber.setText(orderNo);
            binding.orderTitle.setText(order.getOrderTitle());
            binding.orderDescription.setText(order.getOrderDescription());
            binding.orderAmount.setText(StringUtils.formatCurrency(order.getTotalAmount()));

            if(!order.getProducts().isEmpty()){
                CartProduct product = order.getProducts().get(0);
                Picasso.get().load(product.getImage()).into(binding.productImage);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(order, position);
                }
            });
        }
    }
}
