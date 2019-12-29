package io.jpress.module.product.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.components.cache.annotation.CachesEvict;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StrUtil;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.service.ProductCategoryService;
import io.jpress.module.product.service.ProductCommentService;
import io.jpress.module.product.service.ProductService;
import io.jpress.module.product.service.provider.search.ProductSearcherFactory;
import io.jpress.module.product.service.provider.task.ProductCommentsCountUpdateTask;
import io.jpress.module.product.service.provider.task.ProductViewsCountUpdateTask;
import io.jpress.module.product.service.search.ProductSearcher;
import io.jpress.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Bean
public class ProductServiceProvider extends JbootServiceBase<Product> implements ProductService {

    private static final String DEFAULT_ORDER_BY = "order_number desc,id desc";

    @Inject
    private UserService userService;

    @Inject
    private ProductCommentService commentService;

    @Inject
    private ProductCategoryService categoryService;

    @Override
    @CachesEvict({
            @CacheEvict(name = "products", key = "*"),
            @CacheEvict(name = "product-category", key = "#(productId)"),
    })
    public void doUpdateCategorys(long productId, Long[] categoryIds) {
        Db.tx(() -> {
            Db.update("delete from product_category_mapping where product_id = ?", productId);

            if (categoryIds != null && categoryIds.length > 0) {
                List<Record> records = new ArrayList<>();
                for (long categoryId : categoryIds) {
                    Record record = new Record();
                    record.set("product_id", productId);
                    record.set("category_id", categoryId);
                    records.add(record);
                }
                Db.batchSave("product_category_mapping", records, records.size());
            }

            return true;
        });
    }

    @Override
    public void doUpdateCommentCount(long productId) {
        Product product = findById(productId);
        if (product == null) {
            return;
        }

        long count = commentService.findCountByProductId(productId);
        product.setCommentCount(count);
        product.update();
    }

    @Override
    @CacheEvict(name = "products", key = "*")
    public boolean doChangeStatus(long id, int status) {
        Product product = findById(id);
        product.setStatus(status);
        return update(product);
    }

    @Override
    public Page<Product> _paginateByStatus(int page, int pagesize, String title, Long categoryId, int status) {

        return _paginateByBaseColumns(page
                , pagesize
                , Columns.create("p.status", status).likeAppendPercent("p.title", title)
                , categoryId
                , null);
    }

    @Override
    public Page<Product> _paginateWithoutTrash(int page, int pagesize, String title, Long categoryId) {

        return _paginateByBaseColumns(page
                , pagesize
                , Columns.create().ne("p.status", Product.STATUS_TRASH).likeAppendPercent("p.title", title)
                , categoryId
                , null);
    }


    @Override
    @Cacheable(name = "products")
    public Page<Product> paginateInNormal(int page, int pagesize) {
        return paginateInNormal(page, pagesize, null);
    }


    @Override
    @Cacheable(name = "products")
    public Page<Product> paginateInNormal(int page, int pagesize, String orderBy) {
        orderBy = StrUtil.obtainDefaultIfBlank(orderBy, DEFAULT_ORDER_BY);
        Columns columns = new Columns();
        columns.add("status", Product.STATUS_NORMAL);
        Page<Product> dataPage = DAO.paginateByColumns(page, pagesize, columns, orderBy);
        return joinUserInfo(dataPage);
    }


    @Override
    @Cacheable(name = "products")
    public Page<Product> paginateByCategoryIdInNormal(int page, int pagesize, long categoryId, String orderBy) {

        Columns columns = new Columns();
        columns.add("m.category_id", categoryId);
        columns.add("p.status", Product.STATUS_NORMAL);

        return _paginateByBaseColumns(page, pagesize, columns, categoryId, orderBy);
    }


    public Page<Product> _paginateByBaseColumns(int page, int pagesize, Columns baseColumns, Long categoryId, String orderBy) {

        StringBuilder sqlBuilder = new StringBuilder("from product p ");
        if (categoryId != null) {
            sqlBuilder.append(" left join product_category_mapping m on p.id = m.`product_id` ");
        }


        Columns columns = baseColumns;
        columns.add("m.category_id", categoryId);

        sqlBuilder.append(SqlUtils.toWhereSql(columns));

        // 前台走默认排序，但是后台必须走 id 排序，
        // 否当有默认排序的文章很多的时候,发布的新文章可能在后几页
        sqlBuilder.append(" ORDER BY " + StrUtil.obtainDefaultIfBlank(orderBy, DEFAULT_ORDER_BY));

        Page<Product> dataPage = DAO.paginate(page, pagesize, "select * ", sqlBuilder.toString(), columns.getValueArray());
        return joinUserInfo(dataPage);
    }


    @Override
    @Cacheable(name = "products", key = "#(columns.cacheKey)-#(orderBy)-#(count)", liveSeconds = 60 * 60)
    public List<Product> findListByColumns(Columns columns, String orderBy, Integer count) {
        return joinUserInfo(super.findListByColumns(columns, orderBy, count));
    }

    @Override
    @CacheEvict(name = "products", key = "*")
    public void removeCacheById(Object id) {
        DAO.deleteIdCacheById(id);
    }

    @Override
    public Product findFirstBySlug(String slug) {
        return joinUserInfo(DAO.findFirstByColumn(Column.create("slug", slug)));
    }

    @Override
    public long findCountByStatus(int status) {
        return DAO.findCountByColumn(Column.create("status", status));
    }

    @Override
    public boolean deleteByIds(Object... ids) {
        for (Object id : ids) {
            deleteById(id);
        }
        return true;
    }


    @Override
    @CachesEvict({
            @CacheEvict(name = "product-category", key = "#(id)"),
            @CacheEvict(name = "products", key = "*")
    })
    public boolean deleteById(Object id) {
        return super.deleteById(id);
    }

    @Override
    @CachesEvict({
            @CacheEvict(name = "products", key = "*"),
            @CacheEvict(name = "product-category", key = "(id)", unless = "id == null"),
    })
    public void shouldUpdateCache(int action, Model model, Object id) {
        super.shouldUpdateCache(action, model, id);
    }

    @Override
    public void doIncProductViewCount(long productId) {
        ProductViewsCountUpdateTask.recordCount(productId);
    }

    @Override
    public void doIncProductCommentCount(long productId) {
        ProductCommentsCountUpdateTask.recordCount(productId);
    }

    @Override
    @Cacheable(name = "products", liveSeconds = 60 * 60)
    public List<Product> findRelevantListByProductId(Long productId, int status, Integer count) {
        List<ProductCategory> tags = categoryService.findListByProductId(productId, ProductCategory.TYPE_TAG);
        if (tags == null || tags.isEmpty()) {
            return null;
        }

        List<Long> tagIds = tags.stream().map(category -> category.getId()).collect(Collectors.toList());

        Columns columns = Columns.create();
        columns.in("m.category_id", tagIds.toArray());
        columns.ne("p.id", productId);
        columns.eq("p.status", status);

        StringBuilder from = new StringBuilder("select * from product p ");
        from.append(" left join product_category_mapping m on p.id = m.`product_id` ");
        from.append(SqlUtils.toWhereSql(columns));

        if (count != null) {
            from.append(" limit " + count);
        }

        return joinUserInfo(DAO.find(from.toString(), columns.getValueArray()));
    }

    @Override
    @Cacheable(name = "products", liveSeconds = 60 * 60)
    public List<Product> findListByCategoryId(long categoryId, Boolean hasThumbnail, String orderBy, Integer count) {

        StringBuilder from = new StringBuilder("select * from product p ");
        from.append(" left join product_category_mapping m on p.id = m.`product_id` ");
        from.append(" where m.category_id = ? ");
        from.append(" and p.status = ? ");


        if (hasThumbnail != null) {
            if (hasThumbnail == true) {
                from.append(" and p.thumbnail is not null");
            } else {
                from.append(" and p.thumbnail is null");
            }
        }

        from.append(" group by p.id ");

        if (orderBy != null) {
            from.append(" order by " + orderBy);
        }

        if (count != null) {
            from.append(" limit " + count);
        }

        return joinUserInfo(DAO.find(from.toString(), categoryId, Product.STATUS_NORMAL));
    }


    @Override
    public Product findNextById(long id) {
        Columns columns = Columns.create();
        columns.add(Column.create("id", id, Column.LOGIC_GT));
        columns.add(Column.create("status", Product.STATUS_NORMAL));
        return joinUserInfo(DAO.findFirstByColumns(columns));
    }

    @Override
    public Product findPreviousById(long id) {
        Columns columns = Columns.create();
        columns.add(Column.create("id", id, Column.LOGIC_LT));
        columns.add(Column.create("status", Product.STATUS_NORMAL));
        return joinUserInfo(DAO.findFirstByColumns(columns, "id desc"));
    }


    private Page<Product> joinUserInfo(Page<Product> page) {
        userService.join(page, "user_id");
        return page;
    }

    private List<Product> joinUserInfo(List<Product> list) {
        userService.join(list, "user_id");
        return list;
    }

    private Product joinUserInfo(Product product) {
        userService.join(product, "user_id");
        return product;
    }



    @Override
    public Page<Product> search(String queryString, int pageNum, int pageSize) {
        try {
            ProductSearcher searcher = ProductSearcherFactory.getSearcher();
            Page<Product> page = searcher.search(queryString, pageNum, pageSize);
            if (page != null) {
                return page;
            }
        } catch (Exception ex) {
            LogKit.error(ex.toString(), ex);
        }
        return new Page<>(new ArrayList<>(), pageNum, pageSize, 0, 0);
    }

    @Override
    @Cacheable(name = "products")
    public Page<Product> searchIndb(String queryString, int pageNum, int pageSize) {
        Columns columns = Columns.create("status", Product.STATUS_NORMAL)
                .likeAppendPercent("title", queryString);
        return joinUserInfo(paginateByColumns(pageNum, pageSize, columns, "order_number desc,id desc"));
    }
}
