package io.jpress.module.product.service.search;


import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.product.model.Product;

public interface ProductSearcher {

    String HIGH_LIGHT_CLASS = "search-highlight";

    void addProduct(Product article);

    void deleteProduct(Object id);

    void updateProduct(Product article);

    Page<Product> search(String keyword, int pageNum, int pageSize);
}
