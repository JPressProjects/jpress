# Dump of table coupon
# ------------------------------------------------------------

CREATE TABLE `coupon` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '例如：无门槛50元优惠券 | 单品最高减2000元''',
  `icon` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` tinyint(2) DEFAULT NULL COMMENT '1满减券  2叠加满减券  3无门槛券  ',
  `with_amount` decimal(10,2) DEFAULT NULL COMMENT '满多少金额',
  `with_member` tinyint(1) DEFAULT NULL COMMENT '会员可用',
  `with_award` tinyint(1) DEFAULT NULL COMMENT '是否是推广奖励券',
  `with_owner` tinyint(1) DEFAULT NULL COMMENT '是不是只有领取人可用，如果不是，领取人可以随便给其他人用',
  `with_multi` tinyint(1) DEFAULT NULL COMMENT '是否可以多次使用',
  `amount` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '优惠券金额',
  `award_amount` decimal(10,2) unsigned DEFAULT '0.00' COMMENT '奖励金额，大咖可以使用自己的优惠码推广用户，用户获得优惠，大咖获得奖励金额',
  `quota` int(11) unsigned NOT NULL COMMENT '配额：发券数量',
  `take_count` int(11) unsigned DEFAULT '0' COMMENT '已领取的优惠券数量',
  `used_count` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '已使用的优惠券数量',
  `start_time` datetime DEFAULT NULL COMMENT '发放开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '发放结束时间',
  `valid_type` tinyint(2) DEFAULT NULL COMMENT '时效:1绝对时效（XXX-XXX时间段有效）  2相对时效（领取后N天有效）',
  `valid_start_time` datetime DEFAULT NULL COMMENT '使用开始时间',
  `valid_end_time` datetime DEFAULT NULL COMMENT '使用结束时间',
  `valid_days` int(11) DEFAULT NULL COMMENT '自领取之日起有效天数',
  `status` tinyint(2) DEFAULT NULL COMMENT '1 正常  9 不能使用',
  `create_user_id` int(11) unsigned DEFAULT NULL COMMENT '创建用户',
  `options` text COLLATE utf8mb4_unicode_ci,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券';



# Dump of table coupon_code
# ------------------------------------------------------------

CREATE TABLE `coupon_code` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `coupon_id` int(11) unsigned DEFAULT NULL COMMENT '类型ID',
  `title` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '优惠券标题',
  `code` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '优惠码',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '用户ID',
  `status` tinyint(2) DEFAULT NULL COMMENT '状态 1 有人领取、正常使用  2 未有人领取不能使用  3 已经使用，不能被再次使用  9 已经被认为标识不可用',
  `valid_time` datetime DEFAULT NULL COMMENT '领取时间',
  `created` datetime DEFAULT NULL COMMENT '创建时间，创建时可能不会有人领取',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券领取记录';



# Dump of table coupon_product
# ------------------------------------------------------------

CREATE TABLE `coupon_product` (
  `product_type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商品的类型，默认是 product',
  `product_id` int(11) unsigned NOT NULL COMMENT '商品的id',
  `coupon_id` int(11) unsigned NOT NULL COMMENT '优惠券ID',
  PRIMARY KEY (`product_type`,`product_id`,`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券关联商品信息表';



# Dump of table coupon_used_record
# ------------------------------------------------------------

CREATE TABLE `coupon_used_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `used_user_id` int(11) unsigned NOT NULL COMMENT '使用优惠码的用户',
  `used_user_nickname` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '使用优惠码的用户ID',
  `used_order_id` int(11) unsigned DEFAULT NULL COMMENT '订单ID',
  `user_payment_id` int(10) unsigned DEFAULT NULL COMMENT '支付的ID',
  `code_id` int(11) unsigned NOT NULL COMMENT '优惠码ID',
  `code` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '优惠码名称',
  `code_user_id` int(11) unsigned NOT NULL COMMENT '优惠券归属的用户ID',
  `code_user_nickname` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '优惠券归属的用户昵称',
  `coupon_id` int(11) unsigned DEFAULT NULL,
  `created` datetime DEFAULT NULL COMMENT '使用时间',
  PRIMARY KEY (`id`),
  KEY `code_user_id` (`code_user_id`),
  KEY `code_id` (`code_id`),
  KEY `coupon_id` (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券使用记录';



# Dump of table member
# ------------------------------------------------------------

CREATE TABLE `member` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` int(11) unsigned DEFAULT NULL COMMENT '会员组id',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '用户id',
  `duetime` datetime DEFAULT NULL COMMENT '到期时间',
  `remark` text COLLATE utf8mb4_unicode_ci,
  `source` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `options` text COLLATE utf8mb4_unicode_ci,
  `status` tinyint(2) DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userid` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员信息';



# Dump of table member_dist_amount
# ------------------------------------------------------------

CREATE TABLE `member_dist_amount` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` int(11) unsigned NOT NULL COMMENT '会员组',
  `product_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品类型',
  `product_id` int(11) unsigned NOT NULL COMMENT '产品的ID',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '分销的收益',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `modified` int(11) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `proinfo` (`group_id`,`product_type`,`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员分销收益表';



# Dump of table member_group
# ------------------------------------------------------------

CREATE TABLE `member_group` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员名称',
  `title` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `icon` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员ICON',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '会员内容、简介',
  `summary` text COLLATE utf8mb4_unicode_ci COMMENT '摘要',
  `thumbnail` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '缩略图',
  `video` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频',
  `order_number` int(11) DEFAULT '0' COMMENT '排序编号',
  `price` decimal(10,2) DEFAULT NULL COMMENT '加入的会员价格',
  `limited_price` decimal(10,2) DEFAULT NULL COMMENT '限时价格',
  `limited_time` datetime DEFAULT NULL COMMENT '限时价格到期时间',
  `dist_enable` tinyint(1) DEFAULT NULL COMMENT '是否启用分销功能',
  `dist_amount` decimal(10,2) DEFAULT NULL COMMENT '分销收益金额',
  `valid_term` int(11) DEFAULT NULL COMMENT '有效时间（单位天）',
  `flag` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标识',
  `status` tinyint(2) DEFAULT NULL COMMENT '状态',
  `with_ucenter` tinyint(1) DEFAULT NULL COMMENT '是否显示在用户中心',
  `options` text COLLATE utf8mb4_unicode_ci,
  `modified` datetime DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `flag` (`flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员组信息';



# Dump of table member_joined_record
# ------------------------------------------------------------

CREATE TABLE `member_joined_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '用户',
  `group_id` int(11) unsigned DEFAULT NULL COMMENT '加入的会员组',
  `group_name` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员组名称',
  `join_price` decimal(10,2) DEFAULT NULL COMMENT '加入的价格',
  `join_count` int(11) DEFAULT NULL COMMENT '加入份数，可能会一次购买多份（多年）会员。',
  `join_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '加入的类型：付费加入、免费赠送等',
  `join_from` tinyint(2) DEFAULT NULL COMMENT '加入来源：后台手动添加，用户自己购买',
  `valid_term` int(11) DEFAULT NULL COMMENT '加入的有效时间',
  `options` text COLLATE utf8mb4_unicode_ci,
  `created` datetime DEFAULT NULL COMMENT '加入的时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员加入记录';



# Dump of table member_price
# ------------------------------------------------------------

CREATE TABLE `member_price` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `product_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_id` int(11) unsigned NOT NULL,
  `group_id` int(11) unsigned NOT NULL,
  `price` decimal(10,2) DEFAULT NULL COMMENT '会员价',
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `proinfo` (`product_type`,`product_id`,`group_id`),
  KEY `pid` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员价格表';



# Dump of table payment_record
# ------------------------------------------------------------
DROP TABLE IF EXISTS `payment_record`;
CREATE TABLE `payment_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `product_title` varchar(256) DEFAULT '' COMMENT '商品名称',
  `product_summary` varchar(512) DEFAULT NULL COMMENT '产品描述，产品摘要',
  `product_relative_id` varchar(64) DEFAULT NULL COMMENT '相关产品ID',
  `product_relative_type` varchar(32) DEFAULT NULL COMMENT '相关产品类型',
  `product_relative_type_text` varchar(64) DEFAULT NULL,
  `trx_no` varchar(50) NOT NULL COMMENT '支付流水号',
  `trx_type` varchar(30) DEFAULT NULL COMMENT '交易业务类型  ：消费、充值等',
  `trx_nonce_str` varchar(64) DEFAULT NULL COMMENT '签名随机字符串，一般是用来防止重放攻击',
  `payer_user_id` int(11) unsigned DEFAULT NULL COMMENT '付款人编号',
  `payer_name` varchar(256) DEFAULT NULL COMMENT '付款人名称',
  `payer_fee` decimal(20,6) DEFAULT '0.000000' COMMENT '付款方手续费',
  `order_ip` varchar(30) DEFAULT NULL COMMENT '下单ip(客户端ip,从网关中获取)',
  `order_referer_url` varchar(1024) DEFAULT NULL COMMENT '从哪个页面链接过来的(可用于防诈骗)',
  `order_from` varchar(30) DEFAULT NULL COMMENT '订单来源',
  `pay_status` tinyint(2) DEFAULT NULL COMMENT '支付状态：1生成订单未支付（预支付）、 2支付失败、 9自动在线支付成功、 10支付宝转账支付成功、 11微信转账支付成功、 12线下支付支付成功（一般是银行转账等）、 13其他支付方式支付成功',
  `pay_type` varchar(50) DEFAULT NULL COMMENT '支付类型编号',
  `pay_bank_type` varchar(128) DEFAULT NULL COMMENT '支付银行类型',
  `pay_amount` decimal(20,6) DEFAULT '0.000000' COMMENT '订单金额',
  `pay_success_amount` decimal(20,6) DEFAULT NULL COMMENT '成功支付金额',
  `pay_success_time` datetime DEFAULT NULL COMMENT '支付成功时间',
  `pay_success_proof` varchar(256) DEFAULT NULL COMMENT '支付证明，手动入账时需要截图',
  `pay_success_remarks` varchar(256) DEFAULT NULL COMMENT '支付备注',
  `pay_complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `refund_no` varchar(64) DEFAULT NULL COMMENT '退款流水号',
  `refund_amount` int(11) DEFAULT NULL COMMENT '退款金额',
  `refund_desc` varchar(256) DEFAULT NULL COMMENT '退款描述',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `thirdparty_type` varchar(32) DEFAULT NULL COMMENT '第三方支付平台',
  `thirdparty_appid` varchar(32) DEFAULT NULL COMMENT '微信appid 或者 支付宝的appid，thirdparty 指的是支付的第三方比如微信、支付宝、PayPal等',
  `thirdparty_mch_id` varchar(32) DEFAULT NULL COMMENT '商户号',
  `thirdparty_trade_type` varchar(16) DEFAULT NULL COMMENT '交易类型',
  `thirdparty_transaction_id` varchar(32) DEFAULT NULL,
  `thirdparty_user_openid` varchar(64) DEFAULT NULL,
  `remark` text COMMENT '备注',
  `status` tinyint(2) DEFAULT NULL COMMENT 'payment状态：1预支付、 2支付失败、 9支付成功、 11预退款、 12退款中、 13退款失败、 19退款成功',
  `options` text,
  `modified` datetime DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `trx_no` (`trx_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';



# Dump of table product
# ------------------------------------------------------------

CREATE TABLE `product` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `slug` varchar(128) DEFAULT NULL COMMENT 'slug',
  `title` varchar(256) DEFAULT '' COMMENT '标题',
  `content` longtext COMMENT '内容',
  `summary` text COMMENT '摘要',
  `usp` text COMMENT '产品卖点',
  `thumbnail` varchar(512) DEFAULT NULL COMMENT '缩略图',
  `specs` varchar(512) DEFAULT NULL COMMENT '产品规格',
  `video` varchar(512) DEFAULT NULL COMMENT '视频',
  `video_cover` varchar(512) DEFAULT NULL,
  `style` varchar(32) DEFAULT NULL COMMENT '样式',
  `order_number` int(11) DEFAULT '0' COMMENT '排序编号',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '商品的用户ID',
  `user_divide_type` int(11) DEFAULT NULL COMMENT '商品的销售分成类型：0平台所有，1用户所有，2按比例分成',
  `user_divide_ratio` int(11) DEFAULT NULL COMMENT '用户分成比例',
  `price` decimal(10,2) DEFAULT NULL COMMENT '商品价格',
  `origin_price` decimal(10,2) DEFAULT NULL COMMENT '原始价格',
  `limited_price` decimal(10,2) DEFAULT NULL COMMENT '限时优惠价（早鸟价）',
  `limited_time` datetime DEFAULT NULL COMMENT '限时优惠截止时间',
  `dist_enable` tinyint(1) DEFAULT NULL COMMENT '是否启用分销',
  `dist_amount` decimal(10,2) DEFAULT NULL COMMENT '分销收益的金额',
  `status` tinyint(2) DEFAULT NULL COMMENT '状态',
  `comment_status` tinyint(1) DEFAULT '1' COMMENT '评论状态，默认允许评论',
  `comment_count` int(11) unsigned DEFAULT '0' COMMENT '评论总数',
  `comment_time` datetime DEFAULT NULL COMMENT '最后评论时间',
  `view_count` int(11) unsigned DEFAULT '0' COMMENT '访问量',
  `real_view_count` int(11) unsigned DEFAULT '0' COMMENT '真实访问量',
  `sales_count` int(11) unsigned DEFAULT '0' COMMENT '销售量，用于放在前台显示',
  `real_sales_count` int(11) unsigned DEFAULT '0' COMMENT '真实的访问量',
  `stock` int(11) DEFAULT NULL COMMENT '剩余库存',
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL COMMENT '最后更新日期',
  `flag` varchar(256) DEFAULT NULL COMMENT '标识，通常用于对某几篇文章进行标识，从而实现单独查询',
  `meta_keywords` varchar(512) DEFAULT NULL COMMENT 'SEO关键字',
  `meta_description` varchar(512) DEFAULT NULL COMMENT 'SEO描述信息',
  `remarks` text COMMENT '备注信息',
  `options` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`),
  KEY `user_id` (`user_id`),
  KEY `created` (`created`),
  KEY `view_count` (`view_count`),
  KEY `order_number` (`order_number`),
  KEY `sales_count` (`sales_count`),
  KEY `status` (`status`),
  KEY `flag` (`flag`(191))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';



# Dump of table product_category
# ------------------------------------------------------------

CREATE TABLE `product_category` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pid` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '父级分类的ID',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '分类创建的用户ID',
  `slug` varchar(128) DEFAULT NULL COMMENT 'slug',
  `title` varchar(512) DEFAULT NULL COMMENT '标题',
  `content` text COMMENT '内容描述',
  `summary` text COMMENT '摘要',
  `style` varchar(32) DEFAULT NULL COMMENT '模板样式',
  `type` varchar(32) DEFAULT NULL COMMENT '类型，比如：分类、tag、专题',
  `icon` varchar(128) DEFAULT NULL COMMENT '图标',
  `count` int(11) unsigned DEFAULT '0' COMMENT '该分类的内容数量',
  `order_number` int(11) DEFAULT '0' COMMENT '排序编码',
  `flag` varchar(256) DEFAULT NULL COMMENT '标识',
  `meta_keywords` varchar(256) DEFAULT NULL COMMENT 'SEO关键字',
  `meta_description` varchar(256) DEFAULT NULL COMMENT 'SEO描述内容',
  `options` text,
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`),
  KEY `typeslug` (`type`,`slug`),
  KEY `order_number` (`order_number`),
  KEY `pid` (`pid`),
  KEY `flag` (`flag`(191))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表。标签、专题、类别等都属于category。';



# Dump of table product_category_mapping
# ------------------------------------------------------------

CREATE TABLE `product_category_mapping` (
  `product_id` int(11) unsigned NOT NULL COMMENT '文章ID',
  `category_id` int(11) unsigned NOT NULL COMMENT '分类ID',
  PRIMARY KEY (`product_id`,`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品和分类的多对多关系表';



# Dump of table product_comment
# ------------------------------------------------------------

CREATE TABLE `product_comment` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pid` int(11) unsigned DEFAULT NULL COMMENT '回复的评论ID',
  `product_id` int(11) unsigned DEFAULT NULL COMMENT '评论的产品ID',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '评论的用户ID',
  `author` varchar(128) DEFAULT NULL COMMENT '评论的作者',
  `content` text COMMENT '评论的内容',
  `reply_count` int(11) unsigned DEFAULT '0' COMMENT '评论的回复数量',
  `order_number` int(11) DEFAULT '0' COMMENT '排序编号，常用语置顶等',
  `vote_up` int(11) unsigned DEFAULT '0' COMMENT '“顶”的数量',
  `vote_down` int(11) unsigned DEFAULT '0' COMMENT '“踩”的数量',
  `status` tinyint(2) DEFAULT NULL COMMENT '评论的状态',
  `created` datetime DEFAULT NULL COMMENT '评论的时间',
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  KEY `user_id` (`user_id`),
  KEY `status` (`status`),
  KEY `pid` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评论表';



# Dump of table product_image
# ------------------------------------------------------------

CREATE TABLE `product_image` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `product_id` int(11) unsigned NOT NULL,
  `src` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `order_number` int(11) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `productid` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品图片表';



# Dump of table single_page_comment
# ------------------------------------------------------------

CREATE TABLE `single_page_comment` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pid` int(11) unsigned DEFAULT NULL COMMENT '回复的评论ID',
  `page_id` int(11) unsigned DEFAULT NULL COMMENT '评论的内容ID',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '评论的用户ID',
  `author` varchar(128) DEFAULT NULL COMMENT '评论的作者',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
  `wechat` varchar(64) DEFAULT NULL COMMENT '微信号',
  `qq` varchar(32) DEFAULT NULL COMMENT 'qq号',
  `content` text COMMENT '评论的内容',
  `reply_count` int(11) unsigned DEFAULT '0' COMMENT '评论的回复数量',
  `order_number` int(11) DEFAULT '0' COMMENT '排序编号，常用语置顶等',
  `vote_up` int(11) unsigned DEFAULT '0' COMMENT '“顶”的数量',
  `vote_down` int(11) unsigned DEFAULT '0' COMMENT '“踩”的数量',
  `status` varchar(32) DEFAULT NULL COMMENT '评论的状态',
  `created` datetime DEFAULT NULL COMMENT '评论的时间',
  PRIMARY KEY (`id`),
  KEY `page_id` (`page_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='页面评论表';



# Dump of table user_address
# ------------------------------------------------------------

CREATE TABLE `user_address` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `username` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
  `mobile` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `province` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '省',
  `city` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '市',
  `district` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区（县）',
  `detail` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '详细地址到门牌号',
  `zipcode` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮政编码',
  `width_default` tinyint(1) DEFAULT '0' COMMENT '是否默认,1是，0否',
  `options` text COLLATE utf8mb4_unicode_ci,
  `modified` datetime DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收货地址';



# Dump of table user_amount
# ------------------------------------------------------------

CREATE TABLE `user_amount` (
  `user_id` int(11) unsigned NOT NULL,
  `amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `modified` datetime NOT NULL,
  `created` datetime DEFAULT NULL,
  UNIQUE KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户余额';



# Dump of table user_amount_payout
# ------------------------------------------------------------

CREATE TABLE `user_amount_payout` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned NOT NULL COMMENT '申请提现用户',
  `user_real_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户的真实名字',
  `user_idcard` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户的身份证号码',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '提现金额',
  `pay_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '提现类型',
  `pay_to` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '提现账号：可能是微信的openId，可能是支付宝账号，可能是银行账号',
  `pay_success_proof` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '提现成功证明，一般是转账截图',
  `fee` decimal(10,2) DEFAULT NULL COMMENT '提现手续费',
  `statement_id` int(11) unsigned DEFAULT NULL COMMENT '申请提现成功后会生成一个扣款记录',
  `status` tinyint(2) DEFAULT NULL COMMENT '状态',
  `remarks` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户备注',
  `feedback` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回绝提现时给出原因',
  `options` text COLLATE utf8mb4_unicode_ci,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userid` (`user_id`),
  KEY `status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提现申请表';



# Dump of table user_amount_statement
# ------------------------------------------------------------

CREATE TABLE `user_amount_statement` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '用户',
  `action` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '金额变动原因',
  `action_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '金额变动名称',
  `action_desc` text COLLATE utf8mb4_unicode_ci COMMENT '金额变动描述',
  `action_relative_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '相关的表名',
  `action_relative_id` int(11) unsigned DEFAULT NULL COMMENT '相关的id',
  `action_order_id` int(11) unsigned DEFAULT NULL COMMENT '相关的订单ID',
  `action_payment_id` int(11) unsigned DEFAULT NULL COMMENT '相关的支付ID',
  `old_amount` decimal(10,2) NOT NULL COMMENT '用户之前的余额',
  `change_amount` decimal(10,2) NOT NULL COMMENT '变动金额',
  `new_amount` decimal(10,2) NOT NULL COMMENT '变动之后的余额',
  `options` text COLLATE utf8mb4_unicode_ci,
  `created` datetime DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `user_relative` (`user_id`,`action_relative_type`,`action_relative_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户余额流水情况';



# Dump of table user_cart
# ------------------------------------------------------------

CREATE TABLE `user_cart` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL COMMENT '购买的用户',
  `seller_id` int(11) unsigned DEFAULT NULL COMMENT '商品的所属用户',
  `dist_user_id` int(11) unsigned DEFAULT NULL COMMENT '分销用户',
  `product_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品的类别，默认是 product ，但是未来可能是 模板、文件、视频等等...',
  `product_type_text` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_id` int(11) unsigned DEFAULT NULL,
  `product_title` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商品标题',
  `product_summary` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_spec` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品规格',
  `product_thumbnail` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品缩略图',
  `product_link` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品详情页',
  `product_price` decimal(10,2) NOT NULL COMMENT '商品加入购物车时的价格',
  `product_count` int(11) NOT NULL DEFAULT '1' COMMENT '商品数量',
  `with_virtual` tinyint(1) DEFAULT NULL COMMENT '是否是虚拟产品，1是，0不是。虚拟产品支付完毕后立即交易完成。是虚拟产品，虚拟产品支付完毕后立即交易完成',
  `with_refund` tinyint(1) DEFAULT NULL COMMENT '是否支持退款，1支持，0不支持。',
  `view_path` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '查看的网址路径，访问时时，会添加orderid',
  `view_text` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '查看的文章内容，比如：查看、下载',
  `view_effective_time` int(11) unsigned DEFAULT NULL COMMENT '可访问的有效时间，单位秒',
  `comment_path` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `selected` tinyint(1) NOT NULL DEFAULT '0' COMMENT '选中状态',
  `options` text COLLATE utf8mb4_unicode_ci,
  `modified` datetime DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `pinfo` (`product_type`,`product_id`),
  KEY `userselected` (`user_id`,`selected`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车';



# Dump of table user_favorite
# ------------------------------------------------------------

CREATE TABLE `user_favorite` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(10) unsigned NOT NULL COMMENT '收藏用户',
  `type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收藏数据的类型',
  `type_text` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `summary` text COLLATE utf8mb4_unicode_ci COMMENT '摘要',
  `thumbnail` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '缩略图',
  `link` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '详情页',
  `options` text COLLATE utf8mb4_unicode_ci,
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  PRIMARY KEY (`id`),
  KEY `usertype` (`user_id`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏';



# Dump of table user_openid
# ------------------------------------------------------------

CREATE TABLE `user_openid` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '用户ID',
  `type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '第三方类型：wechat，dingding，qq...',
  `value` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '第三方的openId的值',
  `access_token` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可能用不到',
  `expired_time` datetime DEFAULT NULL COMMENT 'access_token的过期时间',
  `nickname` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `options` text COLLATE utf8mb4_unicode_ci,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `type_value` (`type`,`value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号绑定信息表';



# Dump of table user_order
# ------------------------------------------------------------

CREATE TABLE `user_order` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ns` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单号',
  `product_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品的类型',
  `product_title` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '商品的名称',
  `product_summary` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `buyer_id` int(11) unsigned DEFAULT NULL COMMENT '购买人',
  `buyer_nickname` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '购买人昵称',
  `buyer_msg` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户留言',
  `order_total_amount` decimal(10,2) DEFAULT NULL COMMENT '订单总金额，购买人员应该付款的金额',
  `order_real_amount` decimal(10,2) DEFAULT NULL COMMENT '订单的真实金额，销售人员可以在后台修改支付金额，一般情况下 order_real_amount = order_total_amount',
  `coupon_code` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '优惠码',
  `coupon_amount` decimal(10,2) DEFAULT NULL COMMENT '优惠金额',
  `pay_status` tinyint(2) DEFAULT NULL COMMENT '支付状态：1未付款、 2用户标识已经线下付款完成、3用户标识已经通过微信或者支付宝等工具支付完成 、9已经付款（线上支付）、10已经下线支付、11已经通过微信或支付宝等工具支付',
  `pay_success_amount` decimal(10,2) DEFAULT NULL COMMENT '支付成功的金额',
  `pay_success_time` datetime DEFAULT NULL COMMENT '支付时间',
  `pay_success_proof` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付证明，手动入账时需要截图',
  `pay_success_remarks` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付备注',
  `payment_id` int(11) unsigned DEFAULT NULL COMMENT '支付记录',
  `payment_outer_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '第三方订单号',
  `payment_outer_user` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '第三方支付用户，一般情况下是用户的openId',
  `delivery_id` int(11) unsigned DEFAULT NULL COMMENT '发货情况',
  `delivery_type` tinyint(2) DEFAULT NULL,
  `delivery_status` tinyint(2) DEFAULT NULL,
  `delivery_addr_username` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收货人地址',
  `delivery_addr_mobile` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收货人手机号（电话）',
  `delivery_addr_province` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收件人省',
  `delivery_addr_city` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收件人的城市',
  `delivery_addr_district` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收件人的区（县）',
  `delivery_addr_detail` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收件人的详细地址',
  `delivery_addr_zipcode` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收件人地址邮政编码',
  `invoice_id` int(11) DEFAULT NULL COMMENT '发票',
  `invoice_status` tinyint(2) DEFAULT NULL COMMENT '发票开具状态：1 未申请发票、 2 发票申请中、 3 发票开具中、 8 无需开具发票、 9发票已经开具',
  `remarks` text COLLATE utf8mb4_unicode_ci COMMENT '管理员后台憋住',
  `options` text COLLATE utf8mb4_unicode_ci COMMENT 'json字段扩展',
  `trade_status` tinyint(2) DEFAULT NULL COMMENT '交易状态：1交易中、 2交易完成（但是可以申请退款） 、3取消交易 、4申请退款、 5拒绝退款、 6退款中、 7退款完成、 9交易结束',
  `del_status` tinyint(2) DEFAULT NULL COMMENT '删除状态：1 正常 ，2 回收站 3 已经删除',
  `modified` datetime DEFAULT NULL COMMENT '修改时间',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ns` (`ns`),
  KEY `buyer_id` (`buyer_id`),
  KEY `payment_id` (`payment_id`),
  KEY `buyer_status` (`buyer_id`,`trade_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';



# Dump of table user_order_delivery
# ------------------------------------------------------------

CREATE TABLE `user_order_delivery` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `company` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '快递公司',
  `number` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '快递单号',
  `start_time` datetime DEFAULT NULL COMMENT '快递发货时间',
  `finish_time` datetime DEFAULT NULL COMMENT '快递送达时间',
  `addr_username` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收货人地址',
  `addr_mobile` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收货人手机号（电话）',
  `addr_province` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收件人省',
  `addr_city` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收件人的城市',
  `addr_district` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收件人的区（县）',
  `addr_detail` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收件人的详细地址',
  `addr_zipcode` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收件人地址邮政编码',
  `status` tinyint(2) DEFAULT NULL COMMENT '发货状态',
  `options` text COLLATE utf8mb4_unicode_ci COMMENT 'json字段扩展',
  `modified` datetime DEFAULT NULL COMMENT '修改时间',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发货信息表';



# Dump of table user_order_invoice
# ------------------------------------------------------------

CREATE TABLE `user_order_invoice` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发票类型',
  `title` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发票抬头',
  `content` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发票内容',
  `identity` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '纳税人识别号',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单位名称',
  `mobile` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发票收取人手机号',
  `email` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发票收取人邮箱',
  `status` tinyint(2) DEFAULT NULL COMMENT '发票状态',
  `options` text COLLATE utf8mb4_unicode_ci COMMENT 'json字段扩展',
  `modified` datetime DEFAULT NULL COMMENT '修改时间',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发票信息表';



# Dump of table user_order_item
# ------------------------------------------------------------

CREATE TABLE `user_order_item` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `order_id` int(11) unsigned NOT NULL COMMENT '订单id',
  `order_ns` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '订单号',
  `buyer_id` int(11) unsigned NOT NULL COMMENT '购买人',
  `buyer_nickname` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '购买人昵称',
  `buyer_msg` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户留言（备注）',
  `seller_id` int(11) unsigned DEFAULT NULL COMMENT '卖家id',
  `dist_user_id` int(10) unsigned DEFAULT NULL COMMENT '分销员',
  `dist_amount` decimal(10,2) DEFAULT NULL COMMENT '分销金额',
  `product_id` int(11) unsigned DEFAULT NULL COMMENT '产品id',
  `product_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品的类别，默认是 product ，但是未来可能是 模板、文件、视频等等...',
  `product_type_text` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_title` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品标题',
  `product_summary` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_spec` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_thumbnail` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品缩略图',
  `product_link` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品链接',
  `product_price` decimal(10,2) DEFAULT NULL COMMENT '产品价格',
  `product_count` int(11) DEFAULT NULL COMMENT '产品数量',
  `with_virtual` tinyint(1) DEFAULT NULL COMMENT '是否是虚拟产品，1是，0不是。虚拟产品支付完毕后立即交易完成。是虚拟产品，虚拟产品支付完毕后立即交易完成',
  `with_refund` tinyint(1) DEFAULT NULL COMMENT '是否支持退款，1支持，0不支持。',
  `delivery_cost` decimal(10,2) DEFAULT NULL,
  `delivery_id` int(11) unsigned DEFAULT NULL,
  `other_cost` decimal(10,2) DEFAULT NULL,
  `other_cost_remark` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '具体金额 = 产品价格+运费+其他价格 - 分销金额',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额 = 产品价格+运费+其他价格',
  `view_path` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '查看的网址路径，访问时时，会添加orderid',
  `view_text` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '查看的文章内容，比如：查看、下载',
  `view_effective_time` int(11) unsigned DEFAULT NULL COMMENT '可访问的有效时间，单位秒',
  `comment_path` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '评论的路径',
  `invoice_id` int(11) unsigned DEFAULT NULL,
  `invoice_status` tinyint(2) DEFAULT NULL,
  `status` tinyint(2) DEFAULT NULL COMMENT '状态：1交易中、 2交易完成（但是可以申请退款） 、3取消交易 、4申请退款、 5拒绝退款、 6退款中、 7退款完成、 9交易结束',
  `refund_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '退款订单号',
  `refund_amount` decimal(10,2) DEFAULT NULL COMMENT '退款金额',
  `refund_desc` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '退款描述',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `options` text COLLATE utf8mb4_unicode_ci,
  `modified` datetime DEFAULT NULL COMMENT '修改时间',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';



# Dump of table user_tag
# ------------------------------------------------------------

CREATE TABLE `user_tag` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `slug` varchar(128) DEFAULT NULL COMMENT 'slug',
  `title` varchar(512) DEFAULT NULL COMMENT '标题',
  `content` text COMMENT '内容描述',
  `type` varchar(32) DEFAULT NULL COMMENT 'tag类别，用于以后扩展',
  `count` int(11) unsigned DEFAULT '0' COMMENT '该分类的用户数量',
  `order_number` int(11) DEFAULT '0' COMMENT '排序编码',
  `options` text,
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户标签。';



# Dump of table user_tag_mapping
# ------------------------------------------------------------

CREATE TABLE `user_tag_mapping` (
  `user_id` int(11) unsigned NOT NULL COMMENT '用户ID',
  `tag_id` int(11) unsigned NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`user_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和标签的多对多关系表';