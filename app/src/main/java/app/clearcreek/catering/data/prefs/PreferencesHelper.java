package app.clearcreek.catering.data.prefs;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import app.clearcreek.catering.data.model.Cart;

public class PreferencesHelper {
    private final SharedPreferences sharedPrefs;

    public PreferencesHelper(Application application) {
        sharedPrefs = application.getSharedPreferences("ccc_prefs", Context.MODE_PRIVATE);
    }

    public long getNewOrderNo() {
        long orderNo = sharedPrefs.getLong("order_no", 0);
        sharedPrefs.edit().putLong("order_no", ++orderNo).apply();
        return orderNo;
    }

    public void clearCart() {
        sharedPrefs.edit().putString("cart", null).apply();
    }

    public void saveCart(Cart cart) {
        String json = new Gson().toJson(cart);
        sharedPrefs.edit().putString("cart", json).apply();
    }

    public Cart getCart() {
        String json = sharedPrefs.getString("cart", null);
        Cart cart = new Gson().fromJson(json, Cart.class);
        if (cart == null) {
            cart = new Cart();
            saveCart(cart);
        }
        return cart;
    }
}
