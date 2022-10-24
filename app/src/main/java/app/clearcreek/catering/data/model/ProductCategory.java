package app.clearcreek.catering.data.model;

import java.util.LinkedList;
import java.util.List;

public class ProductCategory extends ProductItem {
    private final List<Product> products;

    public ProductCategory(String id, String name, String path) {
        super(id, name, path);
        products = new LinkedList<>();
    }

    public void addProduct(Product product) {
        products.add(product);
    }
}
