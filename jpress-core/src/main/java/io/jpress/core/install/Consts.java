package io.jpress.core.install;


import com.google.common.collect.Lists;

import java.util.List;

class Consts {

    public static List<String> V2_TABLES = Lists.newArrayList("article", "article_category", "article_category_mapping", "article_comment"
            , "attachment", "menu", "option", "payment_record", "permission", "role", "role_permission_mapping"
            , "single_page"
            , "user", "user_role_mapping", "utm", "wechat_menu", "wechat_reply");


    public static List<String> V3_TABLES = Lists.newArrayList("article", "article_category", "article_category_mapping", "article_comment"
            , "attachment", "coupon", "coupon_code", "coupon_product", "coupon_used_record", "member", "member_dist_amount"
            , "member_group", "member_joined_record", "member_price", "menu", "option", "payment_record", "permission", "product"
            , "product_category", "product_category_mapping", "product_comment", "product_image", "role", "role_permission_mapping"
            , "single_page"
            , "user", "user_address", "user_amount", "user_amount_payout", "user_amount_statement", "user_cart", "user_favorite", "user_openid"
            , "user_order", "user_order_item", "user_role_mapping", "utm", "wechat_menu", "wechat_reply");
}
