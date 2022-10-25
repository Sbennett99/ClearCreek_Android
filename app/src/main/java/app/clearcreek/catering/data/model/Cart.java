package app.clearcreek.catering.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Custom Java Class
 *
 * Allows creation of a java object
 * to keep track of the customers current
 * cart and all information associated with
 * it such as the cost and tax totals
 * as well as the decided upon payment method
 * the customer plans to use
 *
 */

public class Cart implements Parcelable {
    // A list containing all items to be ordered by the customer
    private final List<CartProduct> products;

    private double taxAmount = 0.0;
    private double totalAmount = 0.0;
    private PaymentMethod paymentMethod;

    // Basic empty Constructor
    public Cart() {
        products = new ArrayList<>();
        paymentMethod = PaymentMethod.CASH;
    }

    // Constructor to create a cart object from an input Parcel(Serializable object)
    protected Cart(Parcel in) {
        products = in.createTypedArrayList(CartProduct.CREATOR);
        taxAmount = in.readDouble();
        totalAmount = in.readDouble();
        paymentMethod = PaymentMethod.values()[in.readInt()];
    }

    // Creation/changing Cart parcel objects using the class itself
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(products);
        dest.writeDouble(taxAmount);
        dest.writeDouble(totalAmount);
        dest.writeInt(paymentMethod.ordinal());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters/Setters and a few other functions for object creation
    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
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

    // Basic tax and total calculations
    public void calculate() {
        double subTotal = 0.0;
        for (CartProduct product : products) {
            subTotal += (product.getPrice() * product.getQuantity());
        }

        double taxPercent = 5.0;
        taxAmount = (taxPercent * subTotal) / 100;
        totalAmount = subTotal + taxAmount;
    }
    // creates a map object using the cart object
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
}
