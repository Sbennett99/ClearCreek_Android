package app.clearcreek.catering.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Order implements Parcelable {
    private final List<CartProduct> products;

    private double taxAmount = 0.0;
    private double totalAmount = 0.0;
    private PaymentMethod paymentMethod;
    private String orderNumber;

    public Order() {
        products = new ArrayList<>();
        paymentMethod = PaymentMethod.CASH;
        orderNumber = "";
    }

    protected Order(Parcel in) {
        products = in.createTypedArrayList(CartProduct.CREATOR);
        taxAmount = in.readDouble();
        totalAmount = in.readDouble();
        paymentMethod = PaymentMethod.values()[in.readInt()];
        orderNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(products);
        dest.writeDouble(taxAmount);
        dest.writeDouble(totalAmount);
        dest.writeInt(paymentMethod.ordinal());
        dest.writeString(orderNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public void addProduct(CartProduct product) {
        products.add(product);
        calculate();
    }

    public List<CartProduct> getProducts() {
        return products;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getOrderTitle() {
        if (!products.isEmpty()) {
            return products.get(0).getName();
        }
        return null;
    }

    public String getOrderDescription() {
        if (products.size() >= 2) {
            int count = products.size() - 1;
            String items = count == 1 ? "item" : "items";
            return String.format(Locale.US, "%d more %s", count, items);
        }
        return null;
    }

    public void calculate() {
        double subTotal = 0.0;
        for (CartProduct product : products) {
            subTotal += (product.getPrice() * product.getQuantity());
        }

        double taxPercent = 5.0;
        taxAmount = (taxPercent * subTotal) / 100;
        totalAmount = subTotal + taxAmount;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("payment_method_id", paymentMethod.getId());
        map.put("payment_method_name", paymentMethod.getName());
        map.put("tax_amount", taxAmount);
        map.put("total_amount", totalAmount);

        List<Map<String, Object>> list = new LinkedList<>();
        for (CartProduct product : products) {
            list.add(product.toMap());
        }
        map.put("order_items", list);

        return map;
    }

    public static Order createFromDocument(DocumentSnapshot document) {
        try {
            long paymentMethodId = Long.parseLong(String.valueOf(document.get("payment_method_id")));
            PaymentMethod paymentMethod;
            if (paymentMethodId == 1) {
                paymentMethod = PaymentMethod.CASH;
            } else if (paymentMethodId == 2) {
                paymentMethod = PaymentMethod.CREDIT;
            } else {
                paymentMethod = PaymentMethod.DEBIT;
            }

            Order order = new Order();
            order.setPaymentMethod(paymentMethod);
            order.setOrderNumber(document.getId());

            List<Map<String, Object>> list = (List<Map<String, Object>>) document.get("order_items");
            for (Map<String, Object> productMap : list) {
                CartProduct product = CartProduct.fromMap(productMap);
                order.addProduct(product);
            }

            return order;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
