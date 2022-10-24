package app.clearcreek.catering.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import app.clearcreek.catering.AppController;
import app.clearcreek.catering.R;
import app.clearcreek.catering.data.model.Cart;
import app.clearcreek.catering.data.model.Order;
import app.clearcreek.catering.databinding.FragmentHistoryBinding;
import app.clearcreek.catering.ui.activity.LoginActivity;
import app.clearcreek.catering.ui.activity.OrderSummaryActivity;
import app.clearcreek.catering.ui.activity.ProductDetailsActivity;
import app.clearcreek.catering.ui.adapter.HistoryAdapter;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;

    private HistoryAdapter adapter;
    private List<Order> orders = new ArrayList<>();

    public HistoryFragment() {
        super(R.layout.fragment_history);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentHistoryBinding.bind(view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.list.setLayoutManager(layoutManager);

        adapter = new HistoryAdapter();
        adapter.setOnItemClickListener((item, position) -> {
            Intent intent = new Intent(requireContext(), OrderSummaryActivity.class);
            intent.putExtra(OrderSummaryActivity.EXTRA_ORDER_ID, item.getOrderNumber());
            startActivity(intent);
        });

        binding.list.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finishAffinity();
            return;
        }

        binding.progress.setVisibility(View.VISIBLE);
        AppController.getDb().collection("orders")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(task -> {
                    for (DocumentSnapshot documentSnapshot : task.getDocuments()) {
                        Order order = Order.createFromDocument(documentSnapshot);
                        if (order != null) {
                            orders.add(order);
                        }
                    }

                    binding.progress.setVisibility(View.GONE);

                    if (!orders.isEmpty()) {
                        adapter.addItems(orders);
                        binding.list.setVisibility(View.VISIBLE);
                        binding.empty.setVisibility(View.GONE);
                    } else {
                        binding.list.setVisibility(View.GONE);
                        binding.empty.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
