package io.jpress.module.product.service.search;


import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.product.model.Product;

public interface ProductSearcher {

    String HIGH_LIGHT_CLASS = "search-highlight";

    public void addProduct(Product article);

    public void deleteProduct(Object id);

    public void updateProduct(Product article);

    public Page<Product> search(String keyword, int pageNum, int pageSize);
}
