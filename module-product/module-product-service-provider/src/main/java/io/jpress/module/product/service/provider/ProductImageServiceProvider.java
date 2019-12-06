package io.jpress.module.product.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.db.model.Column;
import io.jboot.service.JbootServiceBase;
import io.jpress.module.product.model.ProductImage;
import io.jpress.module.product.model.base.BaseProductImage;
import io.jpress.module.product.service.ProductImageService;
import org.apache.commons.lang.ArrayUtils;

import java.util.Comparator;
import java.util.List;

@Bean
public class ProductImageServiceProvider extends JbootServiceBase<ProductImage> implements ProductImageService {

    private static final String cacheName = "productimage";



    @Override
    @Cacheable(name = cacheName, key = "productId:#(productId)", nullCacheEnable = true)
    public List<ProductImage> findListByProductId(Object productId) {
        List<ProductImage> list = DAO.findListByColumn(Column.create("product_id", productId));
        if (list != null && !list.isEmpty()) list.sort(Comparator.comparingInt(BaseProductImage::getOrderNumber));
        return list == null || list.isEmpty() ? null : list;
    }

    @Override
    public void saveOrUpdateByProductId(Long productId, String[] imageIds, String[] imageSrcs) {

        if (imageIds == null || imageSrcs == null || imageIds.length == 0) {
            Jboot.getCache().remove(cacheName, "productId:" + productId);
            Db.update("delete from product_image where product_id = ?", productId);
            return;
        }

        //这种情况应该不可能出现
        if (imageIds.length != imageSrcs.length) {
            return;
        }



        List<ProductImage> productImages = findListByProductId(productId);
        if (productImages != null) {
            for (ProductImage image : productImages) {
                if (!ArrayUtils.contains(imageIds, image.getId().toString())) {
                    Db.update("delete from product_image where id = ?", image.getId());
                }
            }
        }

        Jboot.getCache().remove(cacheName, "productId:" + productId);

        for (int i = 0; i < imageIds.length; i++) {
            Long imageId = Long.parseLong(imageIds[i]);

            ProductImage image = new ProductImage();
            image.setOrderNumber(i);
            image.setSrc(imageSrcs[i]);
            image.setProductId(productId);

            if (imageId > 0 ){
                image.setId(imageId);
            }

            saveOrUpdate(image);
        }
    }

    @Override
    public boolean deleteByProductId(Long productId) {
        return  Db.update("delete from product_image where product_id = ?", productId) > 0;
    }
}