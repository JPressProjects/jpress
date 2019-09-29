package io.jpress.module.product.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jpress.module.product.service.ProductImageService;
import io.jpress.module.product.model.ProductImage;
import io.jboot.service.JbootServiceBase;

import java.util.List;

@Bean
public class ProductImageServiceProvider extends JbootServiceBase<ProductImage> implements ProductImageService {

    @Override
    public List<ProductImage> findListByProductId(Object productId) {
        return DAO.findListByColumn(Column.create("product_id", productId), "order_number desc");
    }

    @Override
    public void saveOrUpdateByProductId(Long productId, String[] imageIds, String[] imageSrcs) {
        if (imageIds == null || imageIds.length == 0) {
            return;
        }

        for (int i = 0; i < imageIds.length; i++) {
            Long imageId = Long.parseLong(imageIds[i]);

            ProductImage image = new ProductImage();
            image.setOrderNumber(i);
            image.setId(imageId > 0 ? imageId : null);
            image.setSrc(imageSrcs[i]);
            image.setProductId(productId);

            saveOrUpdate(image);
        }
    }
}