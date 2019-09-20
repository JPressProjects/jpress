package io.jpress.module.product.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.commons.Copyer;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.service.ProductCategoryService;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Bean
public class ProductCategoryServiceProvider extends JbootServiceBase<ProductCategory> implements ProductCategoryService {


    @Override
    public Page<ProductCategory> paginateByType(int page, int pagesize, String type) {
        return DAO.paginateByColumn(page, pagesize, Column.create("type", type), "order_number asc,id desc");
    }

    @Override
    public List<ProductCategory> findListByProductId(long articleId) {
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
    public List<ProductCategory> findCategoryListByProductId(long productId) {
        return findListByProductId(productId,ProductCategory.TYPE_CATEGORY);
    }

    @Override
    public List<ProductCategory> findTagListByProductId(long productId) {
        return findListByProductId(productId,ProductCategory.TYPE_TAG);
    }

    @Override
    public List<ProductCategory> findListByProductId(long productId, String type) {
            List<ProductCategory> categoryList = findListByProductId(productId);
            if (categoryList == null || categoryList.isEmpty()) {
                return null;
            }
            return categoryList
                    .stream()
                    .filter(category -> type.equals(category.getType()))
                    .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategory> findListByType(String type) {
        return Copyer.copy(findListByTypeInDb(type));
    }



    @Cacheable(name = "productCategory", key = "type:#(type)")
    public List<ProductCategory> findListByTypeInDb(String type) {
        return DAO.findListByColumns(Columns.create("type", type), "order_number asc,id desc");
    }

    @Override
    public ProductCategory findFirstByTypeAndSlug(String type, String slug) {
        return DAO.findFirstByColumns(Columns.create("type", type).eq("slug", slug));
    }

    @Override
    public Long[] findCategoryIdsByArticleId(long articleId) {
        List<Record> records = Db.find("select * from product_category_mapping where product_id = ?", articleId);
        if (records == null || records.isEmpty())
            return null;

        return ArrayUtils.toObject(records.stream().mapToLong(record -> record.get("category_id")).toArray());
    }



    @Override
    @CacheEvict(name = "productCategory", key = "*")
    public void shouldUpdateCache(int action, Object data) {
        super.shouldUpdateCache(action, data);
    }
}