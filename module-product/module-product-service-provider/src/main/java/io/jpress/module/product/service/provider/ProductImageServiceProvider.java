package io.jpress.module.product.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jpress.module.product.model.base.BaseProductImage;
import io.jpress.module.product.service.ProductImageService;
import io.jpress.module.product.model.ProductImage;
import io.jboot.service.JbootServiceBase;

import java.util.Comparator;
import java.util.List;

@Bean
public class ProductImageServiceProvider extends JbootServiceBase<ProductImage> implements ProductImageService {

    @Override
    public List<ProductImage> findListByProductId(Object productId) {
        List<ProductImage> list = DAO.findListByColumn(Column.create("product_id", productId));
        if (list != null) list.sort(Comparator.comparingInt(BaseProductImage::getOrderNumber));
        return list;
    }

    @Override
    public void saveOrUpdateByProductId(Long productId, String[] imageIds, String[] imageSrcs) {
        if (imageIds == null || imageSrcs == null) {
            return;
        }

        //这种情况应该不可能出现
        if (imageIds.length != imageIds.length){
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