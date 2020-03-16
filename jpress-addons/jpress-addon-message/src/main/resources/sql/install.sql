DROP TABLE IF EXISTS `jpress_addon_message`;
CREATE TABLE `jpress_addon_message` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '名字',
  `phone` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '邮箱',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8 COMMENT '留言内容',
  `show` tinyint(1) DEFAULT NULL COMMENT '是否展示',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;