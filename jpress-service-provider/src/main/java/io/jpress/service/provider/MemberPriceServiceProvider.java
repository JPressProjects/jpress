package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StrUtil;
import io.jpress.model.Member;
import io.jpress.model.MemberGroup;
import io.jpress.model.MemberPrice;
import io.jpress.service.MemberGroupService;
import io.jpress.service.MemberPriceService;
import io.jpress.service.MemberService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Bean
public class MemberPriceServiceProvider extends JbootServiceBase<MemberPrice> implements MemberPriceService {

    @Inject
    private MemberService memberService;

    @Inject
    private MemberGroupService memberGroupService;

    @Override
    public MemberPrice findByPorductAndGroup(String productType, Object productId, Object groupId) {
        return findFirstByColumns(Columns.create("product_type", productType).eq("product_id", productId).eq("group_id", groupId));
    }


    @Override
    public void saveOrUpdateByProduct(String productType, Long productId, String[] memberGroupIds, String[] memberGroupPrices) {
        if (memberGroupIds == null || memberGroupPrices == null || memberGroupIds.length == 0) {
            Db.update("delete from member_price where product_id = ?", productId);
            return;
        }

        //这种情况应该不可能出现
        if (memberGroupIds.length != memberGroupPrices.length) {
            return;
        }


        for (int i = 0; i < memberGroupIds.length; i++) {
            String memberGroupId = memberGroupIds[i];
            String memberGroupPrice = memberGroupPrices[i];

            MemberPrice existModel = findByPorductAndGroup(productType, productId, memberGroupId);

            //删除之前的数据
            if (existModel != null && StrUtil.isBlank(memberGroupPrice)) {
                delete(existModel);
                continue;
            }

            //更新之前的数据
            else if (existModel != null && StrUtil.isNotBlank(memberGroupPrice)) {
                existModel.setPrice(new BigDecimal(memberGroupPrice));
                update(existModel);
                continue;
            }

            //创建新的数据
            else if (existModel == null && StrUtil.isNotBlank(memberGroupPrice)) {
                existModel = new MemberPrice();
                existModel.setProductType(productType);
                existModel.setProductId(productId);
                existModel.setGroupId(Long.valueOf(memberGroupId));
                existModel.setPrice(new BigDecimal(memberGroupPrice));
                existModel.setCreated(new Date());
                save(existModel);
            }

//            else if (existModel == null && StrUtil.isBlank(memberGroupPrice)) {}


        }
    }

    @Override
    public BigDecimal queryPrice(String productType, Long productId, Long userId) {
        List<Member> userMembers = memberService.findListByUserId(userId);
        if (userMembers == null || userMembers.isEmpty()) {
            return null;
        }

        BigDecimal productPrice = null;
        for (Member userMember : userMembers) {
            if (!userMember.isNormal()) {
                continue;
            }

            MemberGroup group = memberGroupService.findById(userMember.getGroupId());
            if (!group.isNormal()) {
                continue;
            }

            MemberPrice memberPrice = findByPorductAndGroup(productType, productId, userMember.getGroupId());
            if (memberPrice != null && memberPrice.getPrice() != null && memberPrice.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                if (productPrice == null || productPrice.compareTo(memberPrice.getPrice()) > 0) {
                    productPrice = memberPrice.getPrice();
                }
            }
        }
        return productPrice;
    }
}