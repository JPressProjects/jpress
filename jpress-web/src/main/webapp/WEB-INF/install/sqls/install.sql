# 转储表 article
# ------------------------------------------------------------

CREATE TABLE `article` (
                           `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                           `pid` int(11) DEFAULT NULL COMMENT '子版本的文章id',
                           `slug` varchar(128) DEFAULT NULL COMMENT 'slug',
                           `title` varchar(256) DEFAULT '' COMMENT '标题',
                           `author` varchar(255) DEFAULT NULL COMMENT '作者',
                           `content` longtext COMMENT '内容',
                           `edit_mode` varchar(32) DEFAULT 'html' COMMENT '编辑模式，默认为html，其他可选项包括html，markdown ..',
                           `summary` text COMMENT '摘要',
                           `link_to` varchar(512) DEFAULT NULL COMMENT '连接到(常用于谋文章只是一个连接)',
                           `thumbnail` varchar(512) DEFAULT NULL COMMENT '缩略图',
                           `style` varchar(32) DEFAULT NULL COMMENT '样式',
                           `user_id` int(11) unsigned DEFAULT NULL COMMENT '用户ID',
                           `order_number` int(11) DEFAULT '0' COMMENT '排序编号',
                           `status` varchar(32) DEFAULT NULL COMMENT '状态',
                           `comment_status` tinyint(1) DEFAULT '1' COMMENT '评论状态，默认允许评论',
                           `comment_count` int(11) unsigned DEFAULT '0' COMMENT '评论总数',
                           `comment_time` datetime DEFAULT NULL COMMENT '最后评论时间',
                           `view_count` int(11) unsigned DEFAULT '0' COMMENT '访问量',
                           `created` datetime DEFAULT NULL COMMENT '创建日期',
                           `modified` datetime DEFAULT NULL COMMENT '最后更新日期',
                           `flag` varchar(64) DEFAULT NULL COMMENT '标识，通常用于对某几篇文章进行标识，从而实现单独查询',
                           `meta_title` varchar(512) DEFAULT NULL COMMENT 'SEO标题',
                           `meta_keywords` varchar(512) DEFAULT NULL COMMENT 'SEO关键字',
                           `meta_description` varchar(512) DEFAULT NULL COMMENT 'SEO描述信息',
                           `with_recommend` tinyint(1) DEFAULT NULL COMMENT '是否推荐',
                           `with_top` tinyint(1) DEFAULT NULL COMMENT '是否置顶',
                           `with_hot` tinyint(1) DEFAULT NULL COMMENT '是否热门',
                           `with_lead_news` tinyint(1) DEFAULT NULL COMMENT '是否是头条',
                           `with_allow_search` tinyint(1) DEFAULT NULL COMMENT '是否允许被搜索',
                           `options` text COMMENT 'json 扩展',
                           `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
                           PRIMARY KEY (`id`) USING BTREE,
                           UNIQUE KEY `slug` (`slug`) USING BTREE,
                           KEY `user_id` (`user_id`) USING BTREE,
                           KEY `created` (`created`) USING BTREE,
                           KEY `view_count` (`view_count`) USING BTREE,
                           KEY `order_number` (`order_number`) USING BTREE,
                           KEY `status` (`status`) USING BTREE,
                           KEY `flag` (`flag`) USING BTREE,
                           KEY `site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章表';



# 转储表 article_category
# ------------------------------------------------------------

CREATE TABLE `article_category` (
                                    `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `pid` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '父级分类的ID',
                                    `user_id` int(11) unsigned DEFAULT NULL COMMENT '分类创建的用户ID',
                                    `slug` varchar(64) DEFAULT NULL COMMENT 'slug',
                                    `title` varchar(512) DEFAULT NULL COMMENT '标题',
                                    `content` text COMMENT '内容描述',
                                    `summary` text COMMENT '摘要',
                                    `style` varchar(32) DEFAULT NULL COMMENT '模板样式',
                                    `type` varchar(32) DEFAULT NULL COMMENT '类型，比如：分类、tag、专题',
                                    `icon` varchar(128) DEFAULT NULL COMMENT '图标',
                                    `with_recommend` tinyint(1) DEFAULT NULL COMMENT '是否推荐',
                                    `with_top` tinyint(1) DEFAULT NULL COMMENT '是否置顶',
                                    `ornament` varchar(512) DEFAULT NULL COMMENT '装饰图',
                                    `thumbnail` varchar(512) DEFAULT NULL COMMENT '缩略图',
                                    `count` int(11) unsigned DEFAULT '0' COMMENT '该分类的内容数量',
                                    `order_number` int(11) DEFAULT '0' COMMENT '排序编码',
                                    `flag` varchar(256) DEFAULT NULL COMMENT '标识',
                                    `meta_title` varchar(256) DEFAULT NULL COMMENT 'SEO标题',
                                    `meta_keywords` varchar(256) DEFAULT NULL COMMENT 'SEO关键字',
                                    `meta_description` varchar(256) DEFAULT NULL COMMENT 'SEO描述内容',
                                    `created` datetime DEFAULT NULL COMMENT '创建日期',
                                    `modified` datetime DEFAULT NULL COMMENT '修改日期',
                                    `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    KEY `typeslug` (`type`,`slug`) USING BTREE,
                                    KEY `order_number` (`order_number`) USING BTREE,
                                    KEY `type` (`type`) USING BTREE,
                                    KEY `site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章分类表。标签、专题、类别等都属于category。';



# 转储表 article_category_mapping
# ------------------------------------------------------------

CREATE TABLE `article_category_mapping` (
                                            `article_id` int(11) unsigned NOT NULL COMMENT '文章ID',
                                            `category_id` int(11) unsigned NOT NULL COMMENT '分类ID',
                                            PRIMARY KEY (`article_id`,`category_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章和分类的多对多关系表';



# 转储表 article_comment
# ------------------------------------------------------------

CREATE TABLE `article_comment` (
                                   `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `pid` int(11) unsigned DEFAULT NULL COMMENT '回复的评论ID',
                                   `article_id` int(11) unsigned DEFAULT NULL COMMENT '评论的内容ID',
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
                                   `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   KEY `content_id` (`article_id`) USING BTREE,
                                   KEY `user_id` (`user_id`) USING BTREE,
                                   KEY `site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章评论表';



# 转储表 article_meta_info
# ------------------------------------------------------------

CREATE TABLE `article_meta_info` (
                                     `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                     `field_id` varchar(32) DEFAULT NULL,
                                     `field_name` varchar(64) DEFAULT NULL,
                                     `label` varchar(128) DEFAULT NULL,
                                     `placeholder` varchar(512) DEFAULT NULL,
                                     `help_text` varchar(512) DEFAULT NULL,
                                     `type` varchar(32) DEFAULT NULL,
                                     `value` varchar(512) DEFAULT NULL,
                                     `value_text` varchar(512) DEFAULT NULL,
                                     `attrs` varchar(256) DEFAULT NULL,
                                     `order_no` int(11) DEFAULT NULL,
                                     `enable` tinyint(1) DEFAULT NULL,
                                     `remarks` text,
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# 转储表 article_meta_record
# ------------------------------------------------------------

CREATE TABLE `article_meta_record` (
                                       `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                       `article_id` int(11) unsigned DEFAULT NULL,
                                       `field_name` varchar(64) DEFAULT NULL,
                                       `value` text,
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `article_id_and_field_name` (`article_id`,`field_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# 转储表 attachment
# ------------------------------------------------------------

CREATE TABLE `attachment` (
                              `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
                              `user_id` int(11) unsigned DEFAULT NULL COMMENT '上传附件的用户ID',
                              `category_id` int(11) unsigned DEFAULT NULL COMMENT '分类ID',
                              `title` text COMMENT '标题',
                              `description` text COMMENT '附件描述',
                              `path` varchar(512) DEFAULT NULL COMMENT '路径',
                              `mime_type` varchar(128) DEFAULT NULL COMMENT 'mime',
                              `suffix` varchar(32) DEFAULT NULL COMMENT '附件的后缀',
                              `type` varchar(32) DEFAULT NULL COMMENT '类型',
                              `flag` varchar(64) DEFAULT NULL COMMENT '标示',
                              `order_number` int(11) DEFAULT '0' COMMENT '排序字段',
                              `accessible` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可以被访问',
                              `created` datetime DEFAULT NULL COMMENT '上传时间',
                              `modified` datetime DEFAULT NULL COMMENT '修改时间',
                              PRIMARY KEY (`id`) USING BTREE,
                              KEY `user_id` (`user_id`) USING BTREE,
                              KEY `created` (`created`) USING BTREE,
                              KEY `suffix` (`suffix`) USING BTREE,
                              KEY `mime_type` (`mime_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='附件表，用于保存用户上传的附件内容。';



# 转储表 attachment_category
# ------------------------------------------------------------

CREATE TABLE `attachment_category` (
                                       `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                       `title` varchar(512) DEFAULT NULL COMMENT '标题',
                                       `count` int(11) unsigned DEFAULT '0' COMMENT '该分类的内容数量',
                                       `order_number` int(11) DEFAULT '0' COMMENT '排序编码',
                                       `created` datetime DEFAULT NULL COMMENT '创建日期',
                                       `modified` datetime DEFAULT NULL COMMENT '修改日期',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='附件分类表';



# 转储表 attachment_video
# ------------------------------------------------------------

CREATE TABLE `attachment_video` (
                                    `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                    `uuid` varchar(32) DEFAULT NULL COMMENT '视频uuid',
                                    `video_type` varchar(32) DEFAULT NULL COMMENT 'vod、live、code',
                                    `cloud_type` varchar(32) DEFAULT NULL COMMENT '视频云',
                                    `category_id` int(11) unsigned DEFAULT NULL COMMENT '分类ID',
                                    `cover` varchar(512) DEFAULT NULL COMMENT '视频封面',
                                    `vod_vid` varchar(64) DEFAULT NULL COMMENT '视频云端ID',
                                    `vod_name` varchar(256) DEFAULT NULL COMMENT '视频名称',
                                    `vod_size` int(11) unsigned DEFAULT NULL COMMENT '视频大小',
                                    `vod_status` varchar(32) DEFAULT NULL COMMENT '视频状态',
                                    `vod_duration` int(11) unsigned DEFAULT NULL COMMENT '视频播放时长，单位秒',
                                    `live_domain` varchar(64) DEFAULT NULL COMMENT '直播播放域名',
                                    `live_app` varchar(64) DEFAULT NULL COMMENT '直播应用名称',
                                    `live_stream` varchar(64) DEFAULT NULL COMMENT '直接流名称',
                                    `live_push_url` varchar(256) DEFAULT NULL COMMENT '直播推流地址',
                                    `live_start_time` datetime DEFAULT NULL COMMENT '开始直播时间',
                                    `live_end_time` datetime DEFAULT NULL COMMENT '结束直播时间',
                                    `content` text COMMENT 'iframe 代码',
                                    `options` text COMMENT '其他配置',
                                    `created` datetime DEFAULT NULL COMMENT '创建时间',
                                    `modified` datetime DEFAULT NULL COMMENT '修改时间',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频附件';



# 转储表 attachment_video_category
# ------------------------------------------------------------

CREATE TABLE `attachment_video_category` (
                                             `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                             `title` varchar(512) DEFAULT NULL COMMENT '标题',
                                             `count` int(11) unsigned DEFAULT '0' COMMENT '该分类的内容数量',
                                             `order_number` int(11) DEFAULT '0' COMMENT '排序编码',
                                             `created` datetime DEFAULT NULL COMMENT '创建日期',
                                             `modified` datetime DEFAULT NULL COMMENT '修改日期',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='附件分类表';



# 转储表 form_datasource
# ------------------------------------------------------------

CREATE TABLE `form_datasource` (
                                   `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                   `name` varchar(64) DEFAULT NULL COMMENT '数据源名称',
                                   `import_type` tinyint(2) DEFAULT NULL COMMENT '导入类型 1 一行一个内容  2 json 内容 3 动态 json URL',
                                   `import_text` text COMMENT '导入内容',
                                   `headers` text COMMENT '动态导入时，配置的 headers',
                                   `with_cascade` tinyint(1) DEFAULT NULL COMMENT '是否级联',
                                   `with_static` tinyint(1) DEFAULT NULL COMMENT '是否是静态内容',
                                   `created` datetime DEFAULT NULL COMMENT '创建时间',
                                   `modified` datetime DEFAULT NULL COMMENT '修改时间',
                                   `options` text COMMENT 'json 扩展',
                                   `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
                                   PRIMARY KEY (`id`),
                                   KEY `site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单数据字典';



# 转储表 form_datasource_item
# ------------------------------------------------------------

CREATE TABLE `form_datasource_item` (
                                        `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                        `pid` int(11) unsigned DEFAULT NULL COMMENT '上级ID，级联数据源的时候用到',
                                        `datasource_id` int(11) unsigned DEFAULT NULL COMMENT '归属的数据源ID',
                                        `value` varchar(64) DEFAULT NULL COMMENT 'value 值',
                                        `text` text COMMENT '文本内容',
                                        `image` varchar(512) DEFAULT NULL COMMENT '图片 URL 地址',
                                        `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
                                        PRIMARY KEY (`id`),
                                        KEY `datasource_id` (`datasource_id`),
                                        KEY `site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# 转储表 form_info
# ------------------------------------------------------------

CREATE TABLE `form_info` (
                             `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                             `uuid` varchar(32) DEFAULT NULL COMMENT 'UUID',
                             `name` varchar(128) DEFAULT NULL COMMENT '表单名称',
                             `title` varchar(256) DEFAULT NULL COMMENT '表单标题（前台）',
                             `summary` text COMMENT '表单描述（前台）',
                             `flag` varchar(64) DEFAULT NULL COMMENT '表单标识',
                             `bg_image` varchar(512) DEFAULT NULL COMMENT '表单背景图',
                             `header_image` varchar(512) DEFAULT NULL COMMENT '表单头图',
                             `builder_json` text COMMENT '表单构建 JSON',
                             `options` text COMMENT 'json扩展配置',
                             `version` int(11) DEFAULT NULL COMMENT '版本号',
                             `status` varchar(32) DEFAULT NULL COMMENT '状态',
                             `data_count` int(11) NOT NULL DEFAULT '0' COMMENT '数据量',
                             `data_created` datetime DEFAULT NULL COMMENT '数据最后添加时间',
                             `created` datetime DEFAULT NULL COMMENT '创建时间',
                             `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uuid` (`uuid`),
                             KEY `site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义表单';



# 转储表 menu
# ------------------------------------------------------------

CREATE TABLE `menu` (
                        `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
                        `pid` int(11) unsigned DEFAULT NULL COMMENT '父级ID',
                        `text` varchar(128) DEFAULT NULL COMMENT '文本内容',
                        `url` varchar(512) DEFAULT NULL COMMENT '链接的url',
                        `target` varchar(32) DEFAULT NULL COMMENT '打开的方式',
                        `icon` varchar(64) DEFAULT NULL COMMENT '菜单的icon',
                        `flag` varchar(64) DEFAULT NULL COMMENT '菜单标识',
                        `type` varchar(32) DEFAULT '' COMMENT '菜单类型：主菜单、顶部菜单、底部菜单',
                        `order_number` int(11) DEFAULT '0' COMMENT '排序字段',
                        `relative_table` varchar(32) DEFAULT NULL COMMENT '该菜单是否和其他表关联',
                        `relative_id` int(11) unsigned DEFAULT NULL COMMENT '关联的具体数据id',
                        `created` datetime DEFAULT NULL COMMENT '创建时间',
                        `modified` datetime DEFAULT NULL COMMENT '修改时间',
                        `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
                        PRIMARY KEY (`id`) USING BTREE,
                        KEY `order_number` (`order_number`) USING BTREE,
                        KEY `site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='菜单表';



# 转储表 option
# ------------------------------------------------------------

CREATE TABLE `option` (
                          `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                          `key` varchar(128) DEFAULT NULL COMMENT '配置KEY',
                          `value` text COMMENT '配置内容',
                          `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
                          PRIMARY KEY (`id`) USING BTREE,
                          UNIQUE KEY `site` (`site_id`,`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='配置信息表，用来保存网站的所有配置信息。';



# 转储表 permission
# ------------------------------------------------------------

CREATE TABLE `permission` (
                              `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                              `action_key` varchar(512) NOT NULL DEFAULT '' COMMENT '唯一标识',
                              `node` varchar(512) NOT NULL DEFAULT '' COMMENT '属于大的分类，可能是Controller、大的DIV、或菜单组',
                              `type` varchar(32) NOT NULL DEFAULT '' COMMENT '权限的类型：url、页面元素、菜单',
                              `text` varchar(1024) DEFAULT NULL COMMENT '菜单描述',
                              `created` datetime DEFAULT NULL,
                              `modified` datetime DEFAULT NULL,
                              PRIMARY KEY (`id`) USING BTREE,
                              KEY `node_actionKey` (`node`(191),`action_key`(191)) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='权限表';



# 转储表 role
# ------------------------------------------------------------

CREATE TABLE `role` (
                        `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                        `name` varchar(128) NOT NULL DEFAULT '' COMMENT '角色名称',
                        `description` text COMMENT '角色的描述',
                        `flag` varchar(64) DEFAULT '' COMMENT '角色标识，全局唯一，jpsa 为超级管理员',
                        `created` datetime NOT NULL,
                        `modified` datetime DEFAULT NULL,
                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='角色表';



# 转储表 role_permission_mapping
# ------------------------------------------------------------

CREATE TABLE `role_permission_mapping` (
                                           `role_id` int(11) unsigned NOT NULL COMMENT '角色ID',
                                           `permission_id` int(11) unsigned NOT NULL COMMENT '权限ID',
                                           PRIMARY KEY (`role_id`,`permission_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='角色和权限的多对多映射表';



# 转储表 single_page
# ------------------------------------------------------------

CREATE TABLE `single_page` (
                               `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                               `category_id` int(11) unsigned DEFAULT NULL COMMENT '分类ID',
                               `slug` varchar(128) DEFAULT NULL COMMENT 'slug',
                               `title` text COMMENT '标题',
                               `content` longtext COMMENT '内容',
                               `edit_mode` varchar(32) DEFAULT 'html' COMMENT '编辑模式：html可视化，markdown ..',
                               `link_to` varchar(512) DEFAULT NULL COMMENT '链接',
                               `summary` text COMMENT '摘要',
                               `thumbnail` varchar(128) DEFAULT NULL COMMENT '缩略图',
                               `ornament` varchar(512) DEFAULT NULL COMMENT '装饰图',
                               `style` varchar(32) DEFAULT NULL COMMENT '样式',
                               `flag` varchar(64) DEFAULT NULL COMMENT '标识',
                               `status` varchar(32) NOT NULL DEFAULT '0' COMMENT '状态',
                               `comment_status` tinyint(1) DEFAULT '1' COMMENT '评论状态，默认允许评论',
                               `comment_count` int(11) unsigned DEFAULT '0' COMMENT '评论总数',
                               `comment_time` datetime DEFAULT NULL COMMENT '最后评论时间',
                               `view_count` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '访问量',
                               `created` datetime DEFAULT NULL COMMENT '创建日期',
                               `modified` datetime DEFAULT NULL COMMENT '最后更新日期',
                               `meta_title` varchar(256) DEFAULT NULL COMMENT 'SEO标题',
                               `meta_keywords` varchar(256) DEFAULT NULL COMMENT 'SEO关键字',
                               `meta_description` varchar(256) DEFAULT NULL COMMENT 'SEO描述信息',
                               `remarks` text COMMENT '备注信息',
                               `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE KEY `slug` (`slug`) USING BTREE,
                               KEY `site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='单页表';



# 转储表 single_page_category
# ------------------------------------------------------------

CREATE TABLE `single_page_category` (
                                        `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                        `title` varchar(512) DEFAULT NULL COMMENT '标题',
                                        `content` text COMMENT '描述',
                                        `style` varchar(32) DEFAULT NULL COMMENT '页面默认情况下使用的模板样式',
                                        `icon` varchar(128) DEFAULT NULL COMMENT '分类 icon',
                                        `ornament` varchar(512) DEFAULT NULL COMMENT '装饰图',
                                        `count` int(11) unsigned DEFAULT '0' COMMENT '该分类的内容数量',
                                        `order_number` int(11) DEFAULT '0' COMMENT '排序编码',
                                        `flag` varchar(64) DEFAULT NULL COMMENT '标识',
                                        `created` datetime DEFAULT NULL COMMENT '创建日期',
                                        `modified` datetime DEFAULT NULL COMMENT '修改日期',
                                        `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='页面分类';



# 转储表 single_page_comment
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
                                       `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `page_id` (`page_id`) USING BTREE,
                                       KEY `user_id` (`user_id`) USING BTREE,
                                       KEY `site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='页面评论表';



# 转储表 site_info
# ------------------------------------------------------------

CREATE TABLE `site_info` (
                             `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                             `site_id` int(11) unsigned DEFAULT NULL COMMENT '自定义站点ID',
                             `name` varchar(64) DEFAULT NULL COMMENT '站点名称',
                             `bind_domain` varchar(64) DEFAULT NULL COMMENT '绑定域名',
                             `bind_path` varchar(64) DEFAULT NULL COMMENT '绑定二级目录',
                             `bind_langs` varchar(256) DEFAULT NULL COMMENT '绑定语言',
                             `options` text COMMENT '其他扩展字段',
                             `created` datetime DEFAULT NULL COMMENT '创建时间',
                             `modified` datetime DEFAULT NULL COMMENT '修改时间',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='站点信息';



# 转储表 template_block_option
# ------------------------------------------------------------

CREATE TABLE `template_block_option` (
                                         `template_id` varchar(64) NOT NULL COMMENT '模板ID',
                                         `site_id` int(11) unsigned NOT NULL COMMENT '站点 ID',
                                         `options` mediumtext COMMENT '配置内容',
                                         PRIMARY KEY (`template_id`,`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='模块配置';



# 转储表 user
# ------------------------------------------------------------

CREATE TABLE `user` (
                        `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                        `username` varchar(128) DEFAULT NULL COMMENT '登录名',
                        `nickname` varchar(128) DEFAULT NULL COMMENT '昵称',
                        `realname` varchar(128) DEFAULT NULL COMMENT '实名',
                        `identity` varchar(128) DEFAULT NULL COMMENT '身份',
                        `password` varchar(128) DEFAULT NULL COMMENT '密码',
                        `salt` varchar(32) DEFAULT NULL COMMENT '盐',
                        `anonym` varchar(32) DEFAULT NULL COMMENT '匿名ID',
                        `email` varchar(64) DEFAULT NULL COMMENT '邮件',
                        `email_status` varchar(32) DEFAULT NULL COMMENT '邮箱状态（是否认证等）',
                        `mobile` varchar(32) DEFAULT NULL COMMENT '手机电话',
                        `mobile_status` varchar(32) DEFAULT NULL COMMENT '手机状态（是否认证等）',
                        `gender` varchar(16) DEFAULT NULL COMMENT '性别',
                        `signature` varchar(2048) DEFAULT NULL COMMENT '签名',
                        `birthday` datetime DEFAULT NULL COMMENT '生日',
                        `company` varchar(256) DEFAULT NULL COMMENT '公司',
                        `occupation` varchar(256) DEFAULT NULL COMMENT '职位、职业',
                        `address` varchar(256) DEFAULT NULL COMMENT '地址',
                        `zipcode` varchar(128) DEFAULT NULL COMMENT '邮政编码',
                        `site` varchar(256) DEFAULT NULL COMMENT '个人网址',
                        `graduateschool` varchar(256) DEFAULT NULL COMMENT '毕业学校',
                        `education` varchar(256) DEFAULT NULL COMMENT '学历',
                        `avatar` varchar(256) DEFAULT NULL COMMENT '头像',
                        `idcardtype` varchar(128) DEFAULT NULL COMMENT '证件类型：身份证 护照 军官证等',
                        `idcard` varchar(128) DEFAULT NULL COMMENT '证件号码',
                        `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                        `status` varchar(32) DEFAULT NULL COMMENT '状态',
                        `created` datetime DEFAULT NULL COMMENT '创建日期',
                        `create_source` varchar(128) DEFAULT NULL COMMENT '用户来源（可能来之oauth第三方）',
                        `logged` datetime DEFAULT NULL COMMENT '最后的登录时间',
                        `activated` datetime DEFAULT NULL COMMENT '激活时间',
                        PRIMARY KEY (`id`) USING BTREE,
                        UNIQUE KEY `username` (`username`) USING BTREE,
                        UNIQUE KEY `email` (`email`) USING BTREE,
                        UNIQUE KEY `mobile` (`mobile`) USING BTREE,
                        KEY `created` (`created`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户信息表，保存用户信息。';



# 转储表 user_openid
# ------------------------------------------------------------

CREATE TABLE `user_openid` (
                               `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                               `user_id` int(11) unsigned DEFAULT NULL COMMENT '用户ID',
                               `type` varchar(32) DEFAULT NULL COMMENT '第三方类型：wechat，dingding，qq...',
                               `value` varchar(128) DEFAULT NULL COMMENT '第三方的openId的值',
                               `access_token` varchar(128) DEFAULT NULL COMMENT '可能用不到',
                               `expired_time` datetime DEFAULT NULL COMMENT 'access_token的过期时间',
                               `nickname` varchar(128) DEFAULT NULL COMMENT '昵称',
                               `avatar` varchar(512) DEFAULT NULL COMMENT '头像',
                               `options` text,
                               `created` datetime DEFAULT NULL,
                               `modified` datetime DEFAULT NULL,
                               PRIMARY KEY (`id`) USING BTREE,
                               KEY `user_id` (`user_id`) USING BTREE,
                               KEY `type_value` (`type`,`value`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='账号绑定信息表';



# 转储表 user_role_mapping
# ------------------------------------------------------------

CREATE TABLE `user_role_mapping` (
                                     `user_id` int(11) unsigned NOT NULL COMMENT '用户ID',
                                     `role_id` int(11) unsigned NOT NULL COMMENT '角色ID',
                                     PRIMARY KEY (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户和角色的多对多映射表';



# 转储表 utm
# ------------------------------------------------------------

CREATE TABLE `utm` (
                       `id` varchar(32) NOT NULL DEFAULT '',
                       `user_id` int(11) unsigned DEFAULT NULL COMMENT '用户ID',
                       `anonym` varchar(32) DEFAULT NULL COMMENT '匿名标识',
                       `action_key` varchar(512) DEFAULT NULL COMMENT '访问路径',
                       `action_query` varchar(512) DEFAULT NULL COMMENT '访问参数',
                       `action_name` varchar(128) DEFAULT NULL COMMENT '访问路径名称',
                       `source` varchar(32) DEFAULT NULL COMMENT '渠道',
                       `medium` varchar(32) DEFAULT NULL COMMENT ' 媒介',
                       `campaign` varchar(128) DEFAULT NULL,
                       `content` varchar(128) DEFAULT NULL COMMENT '来源内容',
                       `term` varchar(256) DEFAULT NULL COMMENT '关键词',
                       `ip` varchar(64) DEFAULT NULL COMMENT 'IP',
                       `agent` varchar(1024) DEFAULT NULL COMMENT '浏览器',
                       `referer` varchar(1024) DEFAULT NULL COMMENT '来源的url',
                       `se` varchar(32) DEFAULT NULL COMMENT 'Search Engine 搜索引擎',
                       `sek` varchar(512) DEFAULT NULL COMMENT 'Search Engine Keyword 搜索引擎关键字',
                       `device_id` varchar(128) DEFAULT NULL COMMENT '设备ID',
                       `platform` varchar(128) DEFAULT NULL COMMENT '平台',
                       `system` varchar(128) DEFAULT NULL COMMENT '系统',
                       `brand` varchar(128) DEFAULT NULL COMMENT '硬件平台',
                       `model` varchar(128) DEFAULT NULL COMMENT '硬件型号',
                       `network` varchar(128) DEFAULT NULL COMMENT '网络情况',
                       `created` datetime DEFAULT NULL COMMENT '创建时间',
                       PRIMARY KEY (`id`) USING BTREE,
                       KEY `user_id` (`user_id`) USING BTREE,
                       KEY `created` (`created`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户行为记录表';



# 转储表 wechat_menu
# ------------------------------------------------------------

CREATE TABLE `wechat_menu` (
                               `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                               `pid` int(11) unsigned DEFAULT NULL COMMENT '父级ID',
                               `text` varchar(512) DEFAULT NULL COMMENT '文本内容',
                               `keyword` varchar(128) DEFAULT NULL COMMENT '关键字',
                               `type` varchar(32) DEFAULT '' COMMENT '菜单类型',
                               `order_number` int(11) DEFAULT '0' COMMENT '排序字段',
                               `created` datetime DEFAULT NULL COMMENT '创建时间',
                               `modified` datetime DEFAULT NULL COMMENT '修改时间',
                               `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
                               PRIMARY KEY (`id`) USING BTREE,
                               KEY `site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='微信公众号菜单表';



# 转储表 wechat_reply
# ------------------------------------------------------------

CREATE TABLE `wechat_reply` (
                                `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                `keyword` varchar(128) DEFAULT NULL COMMENT '关键字',
                                `content` text COMMENT '回复内容',
                                `created` datetime DEFAULT NULL COMMENT '创建时间',
                                `modified` datetime DEFAULT NULL COMMENT '修改时间',
                                `site_id` int(11) DEFAULT NULL COMMENT '站点ID',
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE KEY `keyword` (`keyword`,`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户自定义关键字回复表';


