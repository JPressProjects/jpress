package io.jpress.module.product.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.AopCache;
import io.jboot.components.cache.CacheTime;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.components.cache.annotation.CachesEvict;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StrUtil;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.service.ProductCategoryService;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Bean
public class ProductCategoryServiceProvider extends JbootServiceBase<ProductCategory> implements ProductCategoryService {


    @Override
    public Page<ProductCategory> paginateByType(int page, int pagesize, String type) {
        return DAO.paginateByColumn(page, pagesize, Column.create("type", type), "order_number asc,id desc");
    }

    /**
     * @param productId
     * @return
     */
    @Override
    @Cacheable(name = "product-category", key = "#(productId)", liveSeconds = 2 * CacheTime.HOUR, nullCacheEnable = true)
    public List<ProductCategory> findListByProductId(long productId) {
        List<Record> mappings = Db.find("select * from product_category_mapping where product_id = ?", productId);
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
        return findListByProductId(productId, ProductCategory.TYPE_CATEGORY);
    }

    @Override
    public List<ProductCategory> findTagListByProductId(long productId) {
        return findListByProductId(productId, ProductCategory.TYPE_TAG);
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
    @Cacheable(name = "productCategory", key = "type:#(type)", returnCopyEnable = true)
    public List<ProductCategory> findListByType(String type) {
        return DAO.findListByColumns(Columns.create("type", type), "order_number asc,id desc");
    }

    @Override
    @Cacheable(name = "productCategory")
    public List<ProductCategory> findListByType(String type, String orderBy, Integer count) {
        return DAO.findListByColumns(Columns.create("type", type), StrUtil.isNotBlank(orderBy) ? orderBy : "id desc", count);
    }

    @Override
    public List<ProductCategory> findOrCreateByTagString(String[] tags) {
        if (tags == null || tags.length == 0) {
            return null;
        }

        List<ProductCategory> productCategories = new ArrayList<>();

        boolean needClearCache = false;

        for (String tag : tags) {

            if (StrUtil.isBlank(tag)) {
                continue;
            }

            //slug不能包含字符串点 " . "，否则url不能被访问
            String slug = tag.contains(".")
                    ? tag.replace(".", "_")
                    : tag;

            Columns columns = Columns.create("type", ProductCategory.TYPE_TAG);
            columns.add(Column.create("slug", slug));

            ProductCategory productCategory = DAO.findFirstByColumns(columns);

            if (productCategory == null) {
                productCategory = new ProductCategory();
                productCategory.setTitle(tag);
                productCategory.setSlug(slug);
                productCategory.setType(ProductCategory.TYPE_TAG);
                productCategory.save();
                needClearCache = true;
            }

            productCategories.add(productCategory);
        }

        if (needClearCache) {
            AopCache.removeAll("productCategory");
        }

        return productCategories;
    }


    @Override
    public ProductCategory findFirstByTypeAndSlug(String type, String slug) {
        return DAO.findFirstByColumns(Columns.create("type", type).eq("slug", slug));
    }

    @Override
    public Long[] findCategoryIdsByProductId(long productId) {
        List<Record> records = Db.find("select * from product_category_mapping where product_id = ?", productId);
        if (records == null || records.isEmpty()) {
            return null;
        }

        return ArrayUtils.toObject(records.stream().mapToLong(record -> record.get("category_id")).toArray());
    }

    @Override
    public void doUpdateProductCount(long categoryId) {
        long productCount = Db.queryLong("select count(*) from product_category_mapping where category_id = ? ", categoryId);
        ProductCategory category = findById(categoryId);
        if (category != null) {
            category.setCount(productCount);
            update(category);
        }
    }

    @Override
    public ProductCategory findFirstByFlag(String flag) {
        return findFirstByColumns(Columns.create("flag", flag));
    }


    @Override
    @CachesEvict({
            @CacheEvict(name = "productCategory", key = "*"),
            @CacheEvict(name = "product-category", key = "*"),
    })
    public void shouldUpdateCache(int action, Model model, Object id) {
        super.shouldUpdateCache(action, model, id);
    }
}