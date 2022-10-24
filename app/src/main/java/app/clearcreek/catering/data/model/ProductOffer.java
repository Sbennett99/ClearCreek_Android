package app.clearcreek.catering.data.model;

import com.google.firebase.firestore.DocumentReference;

import java.util.Map;

public class ProductOffer {
    private String name;
    private String image;
    private DocumentReference reference;

    public ProductOffer(String name, String image, DocumentReference reference) {
        this.name = name;
        this.image = image;
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public DocumentReference getReference() {
        return reference;
    }

    public static ProductOffer fromMap(Map<String, Object> map, DocumentReference reference) {
        return new ProductOffer(
                String.valueOf(map.get("name")),
                String.valueOf(map.get("image")),
                reference
        );
    }
}
