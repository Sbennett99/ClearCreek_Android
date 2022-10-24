package app.clearcreek.catering.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductItem implements Parcelable {
    private final String id;
    private final String name;
    private final String path;

    public ProductItem() {
        this.id = "0";
        this.name = "";
        this.path = "";
    }

    public ProductItem(String id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    protected ProductItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        path = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(path);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductItem> CREATOR = new Creator<ProductItem>() {
        @Override
        public ProductItem createFromParcel(Parcel in) {
            return new ProductItem(in);
        }

        @Override
        public ProductItem[] newArray(int size) {
            return new ProductItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
