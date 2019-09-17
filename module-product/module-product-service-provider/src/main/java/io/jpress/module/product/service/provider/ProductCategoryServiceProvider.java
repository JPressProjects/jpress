package io.jpress.module.product.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.service.JbootServiceBase;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.service.ProductCategoryService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Bean
public class ProductCategoryServiceProvider extends JbootServiceBase<ProductCategory> implements ProductCategoryService {

    @Override
    public List<ProductCategory> findListByArticleId(long articleId) {
        List<Record> mappings = Db.find("select * from product_category_mapping where product_id = ?", articleId);
        if (mappings == null || mappings.isEmpty()) {
            return null;
        }

        return mappings
                .stream()
                .map(record -> DAO.findById(record.get("category_id")))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategory> findListByProductId(long articleId, String type) {
            List<ProductCategory> categoryList = findListByArticleId(articleId);
            if (categoryList == null || categoryList.isEmpty()) {
                return null;
            }
            return categoryList
                    .stream()
                    .filter(category -> type.equals(category.getType()))
                    .collect(Collectors.toList());
    }
}