package app.clearcreek.catering.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class ProductVariant implements Parcelable {
    private String name;
    private double price;

    public ProductVariant(String name, double price) {
        this.name = name;
        this.price = price;
    }

    protected ProductVariant(Parcel in) {
        name = in.readString();
        price = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductVariant> CREATOR = new Creator<ProductVariant>() {
        @Override
        public ProductVariant createFromParcel(Parcel in) {
            return new ProductVariant(in);
        }

        @Override
        public ProductVariant[] newArray(int size) {
            return new ProductVariant[size];
        }
    };

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public static ProductVariant fromMap(Map<String, Object> map) {
        return new ProductVariant(
                String.valueOf(map.get("name")),
                Double.parseDouble(String.valueOf(map.get("price")))
        );
    }
}
