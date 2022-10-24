package app.clearcreek.catering.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Product extends ProductItem implements Parcelable {

    private String bread;
    private String sauce;
    private double price;
    private double perToppingPrice;
    private List<String> toppings;
    private List<ProductVariant> variants;

    private String image;

    public Product() {
        super();
    }

    public Product(String id, String name, String path) {
        super(id, name, path);
        bread = null;
        sauce = null;
        price = 0.0;
        perToppingPrice = 0.0;
        toppings = new ArrayList<>();
        variants = new ArrayList<>();
        image = null;
    }

    protected Product(Parcel in) {
        super(in);
        bread = in.readString();
        sauce = in.readString();
        price = in.readDouble();
        perToppingPrice = in.readDouble();
        toppings = in.createStringArrayList();
        variants = in.createTypedArrayList(ProductVariant.CREATOR);
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(bread);
        dest.writeString(sauce);
        dest.writeDouble(price);
        dest.writeDouble(perToppingPrice);
        dest.writeStringList(toppings);
        dest.writeTypedList(variants);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getBread() {
        return bread;
    }

    public void setBread(String bread) {
        this.bread = bread;
    }

    public String getSauce() {
        return sauce;
    }

    public void setSauce(String sauce) {
        this.sauce = sauce;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPerToppingPrice() {
        return perToppingPrice;
    }

    public void setPerToppingPrice(double perToppingPrice) {
        this.perToppingPrice = perToppingPrice;
    }

    public List<String> getToppings() {
        return toppings;
    }

    public void setToppings(List<String> toppings) {
        this.toppings = toppings;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        if (!variants.isEmpty()) {
            builder.append(variants.size());
            if (variants.size() == 1) {
                builder.append(" variant");
            } else {
                builder.append(" variants");
            }
            builder.append(", ");
        }
        if (!toppings.isEmpty()) {
            builder.append(toppings.size());
            if (variants.size() == 1) {
                builder.append(" topping");
            } else {
                builder.append(" toppings");
            }
            builder.append(", ");
        }

        if (sauce != null) {
            builder.append(sauce);
            builder.append(", ");
        }

        if (bread != null) {
            builder.append(bread);
            builder.append(", ");
        }

        String description = builder.toString().trim();
        if (!description.isEmpty() && description.lastIndexOf(",") == description.length() - 1) {
            description = description.substring(0, description.length() - 1);
        }

        return description;
    }

    @Nullable
    public static Product fromDocumentSnapShot(DocumentSnapshot documentSnapshot) {
        try {
            Product product = new Product(
                    documentSnapshot.getId(),
                    documentSnapshot.getString("name"),
                    documentSnapshot.getReference().getPath()
            );

            Map<String, Object> data = documentSnapshot.getData();

            if (data != null) {
                if (data.containsKey("image")) {
                    product.setImage(String.valueOf(data.get("image")));
                }

                if (data.containsKey("bread")) {
                    product.setBread(String.valueOf(data.get("bread")));
                }

                if (data.containsKey("perToppingPrice")) {
                    product.setPerToppingPrice(Double.parseDouble(String.valueOf(data.get("perToppingPrice"))));
                }

                if (data.containsKey("price")) {
                    product.setPrice(Double.parseDouble(String.valueOf(data.get("price"))));
                }

                if (data.containsKey("toppings")) {
                    product.setToppings((List<String>) data.get("toppings"));
                }

                if (data.containsKey("variants")) {
                    List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("variants");
                    List<ProductVariant> variants = new ArrayList<>();
                    for (Map<String, Object> map : list) {
                        ProductVariant variant = ProductVariant.fromMap(map);
                        variants.add(variant);
                    }
                    product.setVariants(variants);
                }

                if (data.containsKey("sauce")) {
                    product.setSauce(data.get("sauce").toString());
                }
            }

            return product;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
