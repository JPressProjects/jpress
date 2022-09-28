
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

/*table delete start*/

DROP TABLE IF EXISTS `coupon`;

DROP TABLE IF EXISTS `coupon_code`;

DROP TABLE IF EXISTS `coupon_product`;

DROP TABLE IF EXISTS `coupon_used_record`;

DROP TABLE IF EXISTS `member`;

DROP TABLE IF EXISTS `member_dist_amount`;

DROP TABLE IF EXISTS `member_group`;

DROP TABLE IF EXISTS `member_joined_record`;

DROP TABLE IF EXISTS `member_price`;

DROP TABLE IF EXISTS `payment_record`;

DROP TABLE IF EXISTS `user_address`;

DROP TABLE IF EXISTS `user_amount`;

DROP TABLE IF EXISTS `user_amount_payout`;

DROP TABLE IF EXISTS `user_amount_statement`;

DROP TABLE IF EXISTS `user_cart`;

DROP TABLE IF EXISTS `user_favorite`;

DROP TABLE IF EXISTS `user_order`;

DROP TABLE IF EXISTS `user_order_delivery`;

DROP TABLE IF EXISTS `user_order_invoice`;

DROP TABLE IF EXISTS `user_order_item`;

/*table delete end*/

/*table created start*/

DROP TABLE IF EXISTS `attachment_category`;
CREATE TABLE `attachment_category`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `count` int(11) UNSIGNED NULL DEFAULT 0 COMMENT '该分类的内容数量',
  `order_number` int(11) NULL DEFAULT 0 COMMENT '排序编码',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `modified` datetime(0) NULL DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '附件分类表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `attachment_video`;
CREATE TABLE `attachment_video`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `uuid` varchar(32) DEFAULT NULL COMMENT '视频uuid',
  `video_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'vod、live、code',
  `cloud_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频云',
  `category_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '分类ID',
  `cover` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频封面',
  `vod_vid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频云端ID',
  `vod_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频名称',
  `vod_size` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '视频大小',
  `vod_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频状态',
  `vod_duration` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '视频播放时长，单位秒',
  `live_domain` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '直播播放域名',
  `live_app` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '直播应用名称',
  `live_stream` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '直接流名称',
  `live_push_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '直播推流地址',
  `live_start_time` datetime(0) NULL DEFAULT NULL COMMENT '开始直播时间',
  `live_end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束直播时间',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'iframe 代码',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '其他配置',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modified` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uuid` (`uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '视频附件' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `attachment_video_category`;
CREATE TABLE `attachment_video_category`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `count` int(11) UNSIGNED NULL DEFAULT 0 COMMENT '该分类的内容数量',
  `order_number` int(11) NULL DEFAULT 0 COMMENT '排序编码',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `modified` datetime(0) NULL DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '附件分类表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `form_datasource`;
CREATE TABLE `form_datasource`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据源名称',
  `import_type` tinyint(2) NULL DEFAULT NULL COMMENT '导入类型 1 一行一个内容  2 json 内容 3 动态 json URL',
  `import_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '导入内容',
  `headers` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '动态导入时，配置的 headers',
  `with_cascade` tinyint(1) NULL DEFAULT NULL COMMENT '是否级联',
  `with_static` tinyint(1) NULL DEFAULT NULL COMMENT '是否是静态内容',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modified` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'json 扩展',
  `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `site_id`(`site_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '表单数据字典' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `form_datasource_item`;
CREATE TABLE `form_datasource_item`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `pid` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '上级ID，级联数据源的时候用到',
  `datasource_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '归属的数据源ID',
  `value` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'value 值',
  `text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文本内容',
  `image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片 URL 地址',
  `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `datasource_id`(`datasource_id`) USING BTREE,
  INDEX `site_id`(`site_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `form_info`;
CREATE TABLE `form_info`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `uuid` varchar(32) DEFAULT NULL COMMENT 'UUID',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单名称',
  `title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单标题（前台）',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '表单描述（前台）',
  `flag` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单标识',
  `bg_image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单背景图',
  `header_image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表单头图',
  `builder_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '表单构建 JSON',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'json扩展配置',
  `version` int(11) NULL DEFAULT NULL COMMENT '版本号',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态',
  `data_count` int(11) NOT NULL DEFAULT 0 COMMENT '数据量',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `data_created` datetime(0) NULL DEFAULT NULL COMMENT '数据最后添加时间',
  `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uuid` (`uuid`) USING BTREE,
  INDEX `site_id`(`site_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义表单' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `job`;
CREATE TABLE `job`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '职位名称或者标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述',
  `department` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对应部门',
  `category_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '对应分类id',
  `address_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '工作地点',
  `age_limit_start` int(11) NULL DEFAULT NULL COMMENT '年龄开始',
  `age_limit_end` int(11) NULL DEFAULT NULL COMMENT '年龄结束',
  `education` tinyint(2) NULL DEFAULT NULL COMMENT '学历',
  `years_limit_type` tinyint(2) NULL DEFAULT NULL COMMENT '工作年限',
  `with_notify` tinyint(1) NULL DEFAULT NULL COMMENT '有建立申请时，是否通知',
  `notify_emails` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知的邮箱',
  `notify_title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知的邮件标题',
  `notify_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '通知的邮件内容',
  `with_remote` tinyint(1) NULL DEFAULT NULL COMMENT '是否属于远程工作',
  `with_apply` tinyint(1) NULL DEFAULT NULL COMMENT '是否允许在线投递',
  `with_hurry` tinyint(1) NULL DEFAULT NULL COMMENT '急招',
  `work_type` tinyint(2) NULL DEFAULT NULL COMMENT '工作类型',
  `recruit_type` tinyint(2) NULL DEFAULT NULL COMMENT '招聘类型',
  `recruit_numbers` int(11) NULL DEFAULT NULL COMMENT '岗位招聘人数',
  `expired_to` datetime(0) NULL DEFAULT NULL COMMENT '岗位有效时间',
  `meta_title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SEO标题',
  `meta_keywords` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SEO关键字',
  `meta_description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SEO描述信息',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `modified` datetime(0) NULL DEFAULT NULL COMMENT '最后更新日期',
  `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `site_id`(`site_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '工作岗位表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `job_apply`;
CREATE TABLE `job_apply`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `job_id` int(10) UNSIGNED NULL DEFAULT NULL COMMENT '申请的职位ID',
  `user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名称',
  `mobile` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `mobile_area` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机区号，中国 +86',
  `email` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `gender` tinyint(2) NULL DEFAULT NULL COMMENT '性别',
  `birthday` datetime(0) NULL DEFAULT NULL COMMENT '出生日期',
  `work_years` int(11) NULL DEFAULT NULL COMMENT '工作年限',
  `education` tinyint(4) NULL DEFAULT NULL COMMENT '最高学历',
  `last_company` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最近工作的公司',
  `cv_path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上传的简历',
  `attachment` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上传的其他附件',
  `referral_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '推荐码',
  `with_viewed` tinyint(1) NULL DEFAULT NULL COMMENT '是否已经查看',
  `with_disposed` tinyint(1) NULL DEFAULT NULL COMMENT '是否已经处理',
  `disposed_content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '处理意见，处理内容',
  `disposed_time` datetime(0) NULL DEFAULT NULL COMMENT '处理时间',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `job_id`(`job_id`) USING BTREE,
  INDEX `site_id`(`site_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '职位申请信息' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `job_category`;
CREATE TABLE `job_category`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类的类型：category、address',
  `pid` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '父id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类名称',
  `summary` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '摘要',
  `count` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '该分类下的岗位数量',
  `order_number` int(11) NULL DEFAULT 0 COMMENT '排序编码',
  `flag` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标识',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `modified` datetime(0) NULL DEFAULT NULL COMMENT '修改日期',
  `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `site_id`(`site_id`) USING BTREE,
  INDEX `type`(`type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位分类表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `single_page_category`;
CREATE TABLE `single_page_category`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述',
  `style` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '页面默认情况下使用的模板样式',
  `icon` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类 icon',
  `ornament` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '装饰图',
  `count` int(11) UNSIGNED NULL DEFAULT 0 COMMENT '该分类的内容数量',
  `order_number` int(11) NULL DEFAULT 0 COMMENT '排序编码',
  `flag` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标识',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `modified` datetime(0) NULL DEFAULT NULL COMMENT '修改日期',
  `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `site_id`(`site_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '页面分类' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `site_info`;
CREATE TABLE `site_info`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '自定义站点ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '站点名称',
  `bind_domain` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绑定域名',
  `bind_path` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绑定二级目录',
  `bind_langs` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '绑定语言',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '其他扩展字段',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modified` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '站点信息' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `template_block_option`;
CREATE TABLE `template_block_option`  (
  `template_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板ID',
  `site_id` int(11) UNSIGNED NOT NULL COMMENT '站点 ID',
  `options` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '配置内容',
  PRIMARY KEY (`template_id`, `site_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模块配置' ROW_FORMAT = Dynamic;

/*table created end*/

/*table update start*/

ALTER TABLE `article` DROP COLUMN `remarks`;

ALTER TABLE `article` DROP INDEX `flag`;

ALTER TABLE `article` ROW_FORMAT = Dynamic;

ALTER TABLE `article` ADD COLUMN `author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作者' AFTER `title`;

ALTER TABLE `article` MODIFY COLUMN `flag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标识，通常用于对某几篇文章进行标识，从而实现单独查询' AFTER `modified`;

ALTER TABLE `article` ADD COLUMN `meta_title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SEO标题' AFTER `flag`;

ALTER TABLE `article` ADD COLUMN `with_recommend` tinyint(1) NULL DEFAULT NULL COMMENT '是否推荐' AFTER `meta_description`;

ALTER TABLE `article` ADD COLUMN `with_top` tinyint(1) NULL DEFAULT NULL COMMENT '是否置顶' AFTER `with_recommend`;

ALTER TABLE `article` ADD COLUMN `with_hot` tinyint(1) NULL DEFAULT NULL COMMENT '是否热门' AFTER `with_top`;

ALTER TABLE `article` ADD COLUMN `with_lead_news` tinyint(1) NULL DEFAULT NULL COMMENT '是否是头条' AFTER `with_hot`;

ALTER TABLE `article` ADD COLUMN `with_allow_search` tinyint(1) NULL DEFAULT NULL COMMENT '是否允许被搜索' AFTER `with_lead_news`;

ALTER TABLE `article` ADD COLUMN `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'json 扩展' AFTER `with_allow_search`;

ALTER TABLE `article` ADD COLUMN `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID' AFTER `options`;

ALTER TABLE `article` ADD flag`(`flag`) USING BTREE;

ALTER TABLE `article` ADD site_id`(`site_id`) USING BTREE;

ALTER TABLE `article_category` ROW_FORMAT = Dynamic;

ALTER TABLE `article_category` MODIFY COLUMN `slug` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'slug' AFTER `user_id`;

ALTER TABLE `article_category` ADD COLUMN `meta_title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SEO标题' AFTER `flag`;

ALTER TABLE `article_category` ADD COLUMN `with_recommend` tinyint(1) NULL DEFAULT NULL COMMENT '是否推荐' AFTER `icon`;

ALTER TABLE `article_category` ADD COLUMN `with_top` tinyint(1) NULL DEFAULT NULL COMMENT '是否置顶' AFTER `with_recommend`;

ALTER TABLE `article_category` ADD COLUMN `ornament` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '装饰图' AFTER `with_top`;

ALTER TABLE `article_category` ADD COLUMN `thumbnail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '缩略图' AFTER `ornament`;

ALTER TABLE `article_category` ADD COLUMN `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID' AFTER `modified`;

ALTER TABLE `article_category` ADD site_id`(`site_id`) USING BTREE;

ALTER TABLE `article_category_mapping` ROW_FORMAT = Dynamic;

ALTER TABLE `article_comment` ROW_FORMAT = Dynamic;

ALTER TABLE `article_comment` ADD COLUMN `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID' AFTER `created`;

ALTER TABLE `article_comment` ADD site_id`(`site_id`) USING BTREE;

ALTER TABLE `attachment` ROW_FORMAT = Dynamic;

ALTER TABLE `attachment` ADD COLUMN `category_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '分类ID' AFTER `user_id`;

ALTER TABLE `attachment` MODIFY COLUMN `flag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标示' AFTER `type`;

ALTER TABLE `menu` ROW_FORMAT = Dynamic;

ALTER TABLE `menu` MODIFY COLUMN `flag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单标识' AFTER `icon`;

ALTER TABLE `menu` ADD COLUMN `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID' AFTER `modified`;

ALTER TABLE `menu` ADD INDEX `site_id`(`site_id`) USING BTREE;

ALTER TABLE `option` DROP INDEX `unique_key`;

ALTER TABLE `option` ROW_FORMAT = Dynamic;

ALTER TABLE `option` ADD COLUMN `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID' AFTER `value`;

ALTER TABLE `option` ADD UNIQUE INDEX `site`(`site_id`, `key`) USING BTREE;

ALTER TABLE `permission` ROW_FORMAT = Dynamic;

ALTER TABLE `product` DROP INDEX `flag`;

ALTER TABLE `product` DROP INDEX `user_id`;

ALTER TABLE `product` DROP COLUMN `specs`;

ALTER TABLE `product` DROP COLUMN `video`;

ALTER TABLE `product` DROP COLUMN `video_cover`;

ALTER TABLE `product` DROP COLUMN `user_id`;

ALTER TABLE `product` DROP COLUMN `user_divide_type`;

ALTER TABLE `product` DROP COLUMN `user_divide_ratio`;

ALTER TABLE `product` DROP COLUMN `limited_time`;

ALTER TABLE `product` DROP COLUMN `dist_enable`;

ALTER TABLE `product` DROP COLUMN `dist_amount`;

ALTER TABLE `product` DROP COLUMN `real_view_count`;

ALTER TABLE `product` DROP COLUMN `real_sales_count`;

ALTER TABLE `product` DROP COLUMN `stock`;

ALTER TABLE `product` ROW_FORMAT = Dynamic;

ALTER TABLE `product` ADD COLUMN `buy_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '购买链接' AFTER `sales_count`;

ALTER TABLE `product` ADD COLUMN `video_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频ID' AFTER `buy_link`;

ALTER TABLE `product` MODIFY COLUMN `flag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标识，通常用于对某几个商品进行标识，从而实现单独查询' AFTER `modified`;

ALTER TABLE `product` ADD COLUMN `meta_title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SEO标题' AFTER `flag`;

ALTER TABLE `product` MODIFY COLUMN `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'json 扩展' AFTER `remarks`;

ALTER TABLE `product` ADD COLUMN `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID' AFTER `options`;

ALTER TABLE `product` ADD INDEX `flag`(`flag`) USING BTREE;

ALTER TABLE `product` ADD INDEX `site_id`(`site_id`) USING BTREE;

ALTER TABLE `product_category` DROP INDEX `flag`;

ALTER TABLE `product_category` ROW_FORMAT = Dynamic;

ALTER TABLE `product_category` ADD COLUMN `meta_title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SEO标题' AFTER `flag`;

ALTER TABLE `product_category` ADD COLUMN `with_recommend` tinyint(1) NULL DEFAULT NULL COMMENT '是否推荐' AFTER `icon`;

ALTER TABLE `product_category` ADD COLUMN `with_top` tinyint(1) NULL DEFAULT NULL COMMENT '是否置顶' AFTER `with_recommend`;

ALTER TABLE `product_category` ADD COLUMN `ornament` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '装饰图' AFTER `with_top`;

ALTER TABLE `product_category` ADD COLUMN `thumbnail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '缩略图' AFTER `ornament`;

ALTER TABLE `product_category` MODIFY COLUMN `flag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标识' AFTER `order_number`;

ALTER TABLE `product_category` ADD COLUMN `site_id` int(10) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID' AFTER `modified`;

ALTER TABLE `product_category` ADD INDEX `flag`(`flag`) USING BTREE;

ALTER TABLE `product_category` ADD INDEX `site_id`(`site_id`) USING BTREE;

ALTER TABLE `product_category_mapping` ROW_FORMAT = Dynamic;

ALTER TABLE `product_comment` ROW_FORMAT = Dynamic;

ALTER TABLE `product_comment` ADD COLUMN `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID' AFTER `created`;

ALTER TABLE `product_comment` ADD INDEX `site_id`(`site_id`) USING BTREE;

ALTER TABLE `product_image` COLLATE = utf8mb4_general_ci, ROW_FORMAT = Dynamic;

ALTER TABLE `product_image` MODIFY COLUMN `src` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL AFTER `product_id`;

ALTER TABLE `role` ROW_FORMAT = Dynamic;

ALTER TABLE `role` MODIFY COLUMN `created` datetime(0) NULL AFTER `flag`;

ALTER TABLE `role_permission_mapping` ROW_FORMAT = Dynamic;

ALTER TABLE `single_page` ROW_FORMAT = Dynamic;

ALTER TABLE `single_page` ADD COLUMN `category_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '分类ID' AFTER `id`;

ALTER TABLE `single_page` ADD COLUMN `ornament` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '装饰图' AFTER `thumbnail`;

ALTER TABLE `single_page` MODIFY COLUMN `flag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标识' AFTER `style`;

ALTER TABLE `single_page` ADD COLUMN `meta_title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SEO标题' AFTER `flag`;

ALTER TABLE `single_page` ADD COLUMN `comment_status` tinyint(1) NULL DEFAULT 1 COMMENT '评论状态，默认允许评论' AFTER `status`;

ALTER TABLE `single_page` ADD COLUMN `comment_count` int(11) UNSIGNED NULL DEFAULT 0 COMMENT '评论总数' AFTER `comment_status`;

ALTER TABLE `single_page` ADD COLUMN `comment_time` datetime(0) NULL DEFAULT NULL COMMENT '最后评论时间' AFTER `comment_count`;

ALTER TABLE `single_page` ADD COLUMN `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID' AFTER `remarks`;

ALTER TABLE `single_page` ADD INDEX `site_id`(`site_id`) USING BTREE;

ALTER TABLE `single_page_comment` ROW_FORMAT = Dynamic;

ALTER TABLE `single_page_comment` ADD COLUMN `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID' AFTER `created`;

ALTER TABLE `single_page_comment` ADD INDEX `site_id`(`site_id`) USING BTREE;

ALTER TABLE `user` ROW_FORMAT = Dynamic;

ALTER TABLE `user_openid` COLLATE = utf8mb4_general_ci, ROW_FORMAT = Dynamic;

ALTER TABLE `user_openid` MODIFY COLUMN `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方类型：wechat，dingding，qq...' AFTER `user_id`;

ALTER TABLE `user_openid` MODIFY COLUMN `value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方的openId的值' AFTER `type`;

ALTER TABLE `user_openid` MODIFY COLUMN `access_token` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '可能用不到' AFTER `value`;

ALTER TABLE `user_openid` MODIFY COLUMN `nickname` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称' AFTER `expired_time`;

ALTER TABLE `user_openid` MODIFY COLUMN `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像' AFTER `nickname`;

ALTER TABLE `user_openid` MODIFY COLUMN `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL AFTER `avatar`;

ALTER TABLE `user_role_mapping` ROW_FORMAT = Dynamic;

ALTER TABLE `user_tag` ROW_FORMAT = Dynamic;

ALTER TABLE `user_tag_mapping` ROW_FORMAT = Dynamic;

ALTER TABLE `utm` ROW_FORMAT = Dynamic;

ALTER TABLE `wechat_menu` ROW_FORMAT = Dynamic;

ALTER TABLE `wechat_menu` ADD COLUMN `site_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '站点ID' AFTER `modified`;

ALTER TABLE `wechat_menu` ADD INDEX `site_id`(`site_id`) USING BTREE;

ALTER TABLE `wechat_reply` ROW_FORMAT = Dynamic;

ALTER TABLE `wechat_reply` DROP INDEX `keyword`;

ALTER TABLE `wechat_reply` ADD COLUMN `site_id` int(11) NULL DEFAULT NULL COMMENT '站点ID' AFTER `modified`;

ALTER TABLE `wechat_reply` ADD UNIQUE INDEX `keyword` (`keyword`,`site_id`) USING BTREE;


/*table update end*/

update `article` set `site_id` = 0;
update `article_category` set `site_id` = 0;
update `article_comment` set `site_id` = 0;
update `form_datasource` set `site_id` = 0;
update `form_datasource_item` set `site_id` = 0;
update `form_info` set `site_id` = 0;
update `job` set `site_id` = 0;
update `job_apply` set `site_id` = 0;
update `job_category` set `site_id` = 0;
update `menu` set `site_id` = 0;
update `option` set `site_id` = 0;
update `product` set `site_id` = 0;
update `product_category` set `site_id` = 0;
update `product_comment` set `site_id` = 0;
update `single_page` set `site_id` = 0;
update `single_page_category` set `site_id` = 0;
update `single_page_comment` set `site_id` = 0;
update `site_info` set `site_id` = 0;
update `template_block_option` set `site_id` = 0;
update `wechat_menu` set `site_id` = 0;
update `wechat_reply` set `site_id` = 0;
