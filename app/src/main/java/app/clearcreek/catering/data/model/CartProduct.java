package app.clearcreek.catering.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class CartProduct implements Parcelable {
    private String id;
    private String path;
    private String name;
    private String image;
    private double price;
    private String variant;
    private String topping;
    private String sauce;
    private String bread;
    private String comments;
    private int quantity;

    public CartProduct(String id, String path, String name, String image, double price, String variant, String toppings, String sauce, String bread, String comments) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.image = image;
        this.price = price;
        this.variant = variant;
        this.topping = toppings;
        this.sauce = sauce;
        this.bread = bread;
        this.comments = comments;
        this.quantity = 1;
    }

    protected CartProduct(Parcel in) {
        id = in.readString();
        path = in.readString();
        name = in.readString();
        image = in.readString();
        price = in.readDouble();
        variant = in.readString();
        topping = in.readString();
        sauce = in.readString();
        bread = in.readString();
        comments = in.readString();
        quantity = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(path);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeDouble(price);
        dest.writeString(variant);
        dest.writeString(topping);
        dest.writeString(sauce);
        dest.writeString(bread);
        dest.writeString(comments);
        dest.writeInt(quantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CartProduct> CREATOR = new Creator<CartProduct>() {
        @Override
        public CartProduct createFromParcel(Parcel in) {
            return new CartProduct(in);
        }

        @Override
        public CartProduct[] newArray(int size) {
            return new CartProduct[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public double getPrice() {
        return price;
    }

    public String getComments() {
        return comments;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        if (variant != null) {
            builder.append(variant.equals("null") ? "" : variant + ", ");
        }

        if (topping != null) {
            builder.append(topping.equals("null") ? "" : topping + ", ");
        }

        if (sauce != null) {
            builder.append(sauce.equals("null") ? "" : sauce + ", ");
        }

        if (bread != null) {
            builder.append(bread.equals("null") ? "" : bread + ", ");
        }

        String description = builder.toString().trim();
        if (!description.isEmpty() && description.lastIndexOf(",") == description.length() - 1) {
            description = description.substring(0, description.length() - 1);
        }

        return description;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("path", path);
        map.put("name", name);
        map.put("image", image);
        map.put("price", price);
        map.put("variant", variant);
        map.put("topping", topping);
        map.put("sauce", sauce);
        map.put("bread", bread);
        map.put("comments", comments);
        map.put("quantity", quantity);

        return map;
    }

    public static CartProduct fromMap(Map<String, Object> map) {
        CartProduct product = new CartProduct(
                String.valueOf(map.get("id")),
                String.valueOf(map.get("path")),
                String.valueOf(map.get("name")),
                String.valueOf(map.get("image")),
                Double.parseDouble(String.valueOf(map.get("price"))),
                map.containsKey("variant") ? String.valueOf(map.get("variant")) : null,
                map.containsKey("topping") ? String.valueOf(map.get("topping")) : null,
                map.containsKey("sauce") ?String.valueOf(map.get("sauce")) : null,
                map.containsKey("bread") ?String.valueOf(map.get("bread")) : null,
                String.valueOf(map.get("comments"))
        );
        product.setQuantity(Integer.parseInt(String.valueOf(map.get("quantity"))));

        return product;
    }
}
