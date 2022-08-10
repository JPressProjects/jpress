/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.core.install;


import com.google.common.collect.Lists;

import java.util.List;

class Consts {

    /**
     * JPress v2 版本的数据库
     */
    public static List<String> V2_TABLES = Lists.newArrayList("article", "article_category", "article_category_mapping", "article_comment"
            , "attachment", "menu", "option", "payment_record", "permission", "role", "role_permission_mapping"
            , "single_page"
            , "user", "user_role_mapping", "utm", "wechat_menu", "wechat_reply");


    /**
     * JPress V3 和 V4 版本数据库表
     */
    public static List<String> V3_TABLES = Lists.newArrayList("article", "article_category", "article_category_mapping", "article_comment"
            , "attachment", "coupon", "coupon_code", "coupon_product", "coupon_used_record", "member", "member_dist_amount"
            , "member_group", "member_joined_record", "member_price", "menu", "option", "payment_record", "permission", "product"
            , "product_category", "product_category_mapping", "product_comment", "product_image", "role", "role_permission_mapping"
            , "single_page", "single_page_comment"
            , "user", "user_address", "user_amount", "user_amount_payout", "user_amount_statement", "user_cart", "user_favorite", "user_openid"
            , "user_order", "user_order_delivery", "user_order_invoice", "user_order_item", "user_role_mapping", "user_tag", "user_tag_mapping"
            , "utm", "wechat_menu", "wechat_reply");

    /**
     * JPress V5 版本数据库表
     */
    public static List<String> V5_TABLES = Lists.newArrayList("article", "article_category", "article_category_mapping", "article_comment"
            , "attachment", "attachment_category", "attachment_video", "attachment_video_category", "form_datasource", "form_datasource_item", "form_info"
            , "job", "job_apply", "job_category", "menu", "option", "permission"
            , "product", "product_category", "product_category_mapping", "product_comment", "product_image", "role", "role_permission_mapping"
            , "single_page", "single_page_category", "single_page_comment", "site_info", "template_block_option"
            , "user", "user_openid", "user_role_mapping", "user_tag", "user_tag_mapping"
            , "utm", "wechat_menu", "wechat_reply");
}
