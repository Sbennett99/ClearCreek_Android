package app.clearcreek.catering.data.model;

import java.util.LinkedList;
import java.util.List;

//written by Rahul Konda
//Comitted by Rahul Konda

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
