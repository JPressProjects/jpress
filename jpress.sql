# ************************************************************
# Sequel Ace SQL dump
# 版本号： 20033
#
# https://sequel-ace.com/
# https://github.com/Sequel-Ace/Sequel-Ace
#
# 主机: 192.168.1.2 (MySQL 5.7.31)
# 数据库: jpress
# 生成时间: 2022-07-20 02:37:33 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# 转储表 article
# ------------------------------------------------------------

DROP TABLE IF EXISTS `article`;

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
  `flag` varchar(256) DEFAULT NULL COMMENT '标识，通常用于对某几篇文章进行标识，从而实现单独查询',
  `meta_keywords` varchar(512) DEFAULT NULL COMMENT 'SEO关键字',
  `meta_description` varchar(512) DEFAULT NULL COMMENT 'SEO描述信息',
  `with_recommend` tinyint(1) DEFAULT NULL COMMENT '是否推荐',
  `with_top` tinyint(1) DEFAULT NULL COMMENT '是否置顶',
  `with_hot` tinyint(1) DEFAULT NULL COMMENT '是否热门',
  `with_lead_news` tinyint(1) DEFAULT NULL COMMENT '是否是头条',
  `with_allow_search` tinyint(1) DEFAULT NULL COMMENT '是否允许被搜索',
  `options` text COMMENT 'json 扩展',
  `site_id` int(10) unsigned DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `slug` (`slug`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `created` (`created`) USING BTREE,
  KEY `view_count` (`view_count`) USING BTREE,
  KEY `order_number` (`order_number`) USING BTREE,
  KEY `status` (`status`) USING BTREE,
  KEY `flag` (`flag`(191)) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='文章表';

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;

INSERT INTO `article` (`id`, `pid`, `slug`, `title`, `author`, `content`, `edit_mode`, `summary`, `link_to`, `thumbnail`, `style`, `user_id`, `order_number`, `status`, `comment_status`, `comment_count`, `comment_time`, `view_count`, `created`, `modified`, `flag`, `meta_keywords`, `meta_description`, `with_recommend`, `with_top`, `with_hot`, `with_lead_news`, `with_allow_search`, `options`, `site_id`)
VALUES
	(1,NULL,NULL,'欢迎使用JPress',NULL,'<p>欢迎使用 JPress，这是一篇 JPress 自动为您创建的测试文章，您可以进入 JPress 的后台，在文章管理里进行修改或者删除。</p>','html',NULL,NULL,NULL,NULL,1,0,'draft',1,0,NULL,10,'2021-06-01 12:00:00','2022-07-14 10:03:53',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	(2,NULL,NULL,'不许人间见白头',NULL,'<p>在亿万人海相遇，是一种奇迹。</p>','html','相遇',NULL,'/attachment/20220413/6933d238a8944c2f9a39926e0a33b861.jpg',NULL,1,0,'draft',1,2,NULL,22,'2022-04-13 11:26:36','2022-07-04 15:40:16','000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	(3,NULL,'ces','上帝笑了99次',NULL,'<p>&nbsp;</p>\n<p>腾讯云视频点播</p>\n<p>\n <video class=\"jpress-video\" style=\"height:240px;width:425px;\" poster=\"/attachment/20220706/13c36dcfb4794890a266805c4baefbec.png\" id=\"qcloud_video_player49764\"></video></p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>阿里云视频点播</p>\n<p>\n <video class=\"jpress-video\" style=\"height:240px;width:425px;\" id=\"video_player11679\" poster=\"/attachment/20220705/56a1c33c92164862839c0e52fce14714.png\"></video></p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>','html',NULL,NULL,NULL,NULL,1,0,'draft',1,1,NULL,32,'2022-04-15 15:18:55','2022-07-04 15:40:15',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);

/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 article_category
# ------------------------------------------------------------

DROP TABLE IF EXISTS `article_category`;

CREATE TABLE `article_category` (
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
  `with_recommend` tinyint(1) DEFAULT NULL COMMENT '是否推荐',
  `with_top` tinyint(1) DEFAULT NULL COMMENT '是否置顶',
  `ornament` varchar(512) DEFAULT NULL COMMENT '装饰图',
  `thumbnail` varchar(512) DEFAULT NULL COMMENT '缩略图',
  `count` int(11) unsigned DEFAULT '0' COMMENT '该分类的内容数量',
  `order_number` int(11) DEFAULT '0' COMMENT '排序编码',
  `flag` varchar(256) DEFAULT NULL COMMENT '标识',
  `meta_keywords` varchar(256) DEFAULT NULL COMMENT 'SEO关键字',
  `meta_description` varchar(256) DEFAULT NULL COMMENT 'SEO描述内容',
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL COMMENT '修改日期',
  `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `typeslug` (`type`,`slug`) USING BTREE,
  KEY `order_number` (`order_number`) USING BTREE,
  KEY `type` (`type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='文章分类表。标签、专题、类别等都属于category。';

LOCK TABLES `article_category` WRITE;
/*!40000 ALTER TABLE `article_category` DISABLE KEYS */;

INSERT INTO `article_category` (`id`, `pid`, `user_id`, `slug`, `title`, `content`, `summary`, `style`, `type`, `icon`, `with_recommend`, `with_top`, `ornament`, `thumbnail`, `count`, `order_number`, `flag`, `meta_keywords`, `meta_description`, `created`, `modified`, `site_id`)
VALUES
	(1,0,NULL,'guwen','古文',NULL,NULL,NULL,'category',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2022-04-15 15:53:05','2022-06-09 17:34:23',NULL),
	(2,1,NULL,'shici','诗词',NULL,NULL,NULL,'category',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,'2022-04-15 15:53:21','2022-06-09 17:34:23',NULL),
	(3,1,NULL,'sanwen','散文',NULL,NULL,NULL,'category','&lt;i class=&quot;bi bi-apple&quot;&gt;&lt;/i&gt;',0,0,NULL,NULL,2,2,NULL,NULL,NULL,'2022-04-15 16:40:22','2022-06-20 13:42:04',NULL),
	(4,1,NULL,'cipaiming','词牌名',NULL,NULL,NULL,'category','&lt;i class=&quot;bi bi-apple&quot;&gt;&lt;/i&gt;',0,1,'/attachment/20220617/e88f8593f64947d0bfec744bb3d749b7.png','/attachment/20220617/58f750d10dcd4f40888cb56fb118a844.png',1,NULL,NULL,NULL,NULL,'2022-04-15 16:41:13','2022-06-23 14:47:55',NULL),
	(5,0,NULL,'wenz','wenz','012010','0120',NULL,'category','201',NULL,NULL,NULL,NULL,0,1,'02101',NULL,NULL,'2022-06-09 15:04:39','2022-06-09 16:39:53',NULL),
	(6,0,NULL,'11.com','11',NULL,NULL,NULL,'category',NULL,0,0,NULL,NULL,0,NULL,NULL,NULL,NULL,'2022-06-22 16:25:01','2022-06-22 16:25:01',NULL),
	(7,0,NULL,'a111','111',NULL,NULL,NULL,'category',NULL,0,0,NULL,NULL,0,0,NULL,NULL,NULL,'2022-07-18 10:00:08','2022-07-18 10:10:27',0),
	(8,0,NULL,'a22','222',NULL,NULL,NULL,'category',NULL,0,0,NULL,NULL,0,1,NULL,NULL,NULL,'2022-07-18 10:05:25','2022-07-18 10:09:52',0),
	(9,8,NULL,'a333','333',NULL,NULL,NULL,'category',NULL,0,0,NULL,NULL,0,2,NULL,NULL,NULL,'2022-07-18 10:05:40','2022-07-18 13:50:28',0),
	(10,7,NULL,'a1111aa','111111111',NULL,NULL,NULL,'category',NULL,0,0,NULL,NULL,0,1,NULL,NULL,NULL,'2022-07-18 10:05:53','2022-07-18 10:11:05',0),
	(11,7,NULL,'aaa00','00000',NULL,NULL,NULL,'category',NULL,0,0,NULL,NULL,0,10,NULL,NULL,NULL,'2022-07-18 10:11:40','2022-07-18 10:11:53',0);

/*!40000 ALTER TABLE `article_category` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 article_category_mapping
# ------------------------------------------------------------

DROP TABLE IF EXISTS `article_category_mapping`;

CREATE TABLE `article_category_mapping` (
  `article_id` int(11) unsigned NOT NULL COMMENT '文章ID',
  `category_id` int(11) unsigned NOT NULL COMMENT '分类ID',
  PRIMARY KEY (`article_id`,`category_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='文章和分类的多对多关系表';

LOCK TABLES `article_category_mapping` WRITE;
/*!40000 ALTER TABLE `article_category_mapping` DISABLE KEYS */;

INSERT INTO `article_category_mapping` (`article_id`, `category_id`)
VALUES
	(1,4),
	(2,3),
	(3,3);

/*!40000 ALTER TABLE `article_category_mapping` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 article_comment
# ------------------------------------------------------------

DROP TABLE IF EXISTS `article_comment`;

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
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='文章评论表';

LOCK TABLES `article_comment` WRITE;
/*!40000 ALTER TABLE `article_comment` DISABLE KEYS */;

INSERT INTO `article_comment` (`id`, `pid`, `article_id`, `user_id`, `author`, `email`, `wechat`, `qq`, `content`, `reply_count`, `order_number`, `vote_up`, `vote_down`, `status`, `created`, `site_id`)
VALUES
	(1,NULL,2,1,'admin',NULL,NULL,NULL,'总有人间一缕风，填我十万八千梦。',1,0,0,0,'normal','2022-04-13 13:42:13',NULL),
	(2,1,2,1,'admin',NULL,NULL,NULL,'回复 @admin ：梦里寻她千百度',0,0,0,0,'normal','2022-04-13 13:43:50',NULL),
	(3,NULL,3,1,'admin',NULL,NULL,NULL,'<p>我们只是在救自己.</p>',0,0,0,0,'normal','2022-04-15 15:25:00',NULL);

/*!40000 ALTER TABLE `article_comment` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 attachment
# ------------------------------------------------------------

DROP TABLE IF EXISTS `attachment`;

CREATE TABLE `attachment` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '上传附件的用户ID',
  `category_id` int(11) DEFAULT NULL COMMENT '分类ID',
  `title` text COMMENT '标题',
  `description` text COMMENT '附件描述',
  `path` varchar(512) DEFAULT NULL COMMENT '路径',
  `mime_type` varchar(128) DEFAULT NULL COMMENT 'mime',
  `suffix` varchar(32) DEFAULT NULL COMMENT '附件的后缀',
  `type` varchar(32) DEFAULT NULL COMMENT '类型',
  `flag` varchar(256) DEFAULT NULL COMMENT '标示',
  `order_number` int(11) DEFAULT '0' COMMENT '排序字段',
  `accessible` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可以被访问',
  `created` datetime DEFAULT NULL COMMENT '上传时间',
  `modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `created` (`created`) USING BTREE,
  KEY `suffix` (`suffix`) USING BTREE,
  KEY `mime_type` (`mime_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='附件表，用于保存用户上传的附件内容。';

LOCK TABLES `attachment` WRITE;
/*!40000 ALTER TABLE `attachment` DISABLE KEYS */;

INSERT INTO `attachment` (`id`, `user_id`, `category_id`, `title`, `description`, `path`, `mime_type`, `suffix`, `type`, `flag`, `order_number`, `accessible`, `created`, `modified`)
VALUES
	(1,1,NULL,'wb21.jpg','这个杀手不太冷','/attachment/20220413/6933d238a8944c2f9a39926e0a33b861.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-04-13 13:40:57','2022-04-15 15:14:20'),
	(2,1,NULL,'avatar.jpg','这是一御姐','/attachment/20220415/868a0c20975c40a3b22f5f9bded05ce7.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-04-15 15:14:49','2022-04-15 15:15:44'),
	(3,1,NULL,'wb22.jpg',NULL,'/attachment/20220415/49251cf9155145e1bc39af77a662d98e.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-04-15 15:42:07',NULL),
	(4,1,NULL,'avatar.jpg',NULL,'/attachment/20220609/d87d2e10c0cf4c88b4714bb29b56106c.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-06-09 15:09:25',NULL),
	(5,1,NULL,'select2.jpg',NULL,'/attachment/20220613/51810b8b82f54b9a93032263744f4f57.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-06-13 11:12:27',NULL),
	(6,1,NULL,'2.jpg',NULL,'/attachment/20220613/196ddeeb59274964bbfbd4141181d3ed.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-06-13 11:13:00',NULL),
	(7,1,NULL,'video.mp4',NULL,'/attachment/20220613/cac42f06ecb04cca963b16d16e219143.mp4','video/mp4','.mp4',NULL,NULL,0,1,'2022-06-13 11:14:16',NULL),
	(8,1,NULL,'student.png',NULL,'/attachment/20220613/977e1e781bde4ef78525d17a6c910fe2.png','image/png','.png',NULL,NULL,0,1,'2022-06-13 11:51:43',NULL),
	(9,1,NULL,'people.png',NULL,'/attachment/20220613/85df36ddcab64662b2a112d68f1af4fe.png','image/png','.png',NULL,NULL,0,1,'2022-06-13 15:01:58',NULL),
	(10,1,1,'avatar.png',NULL,'/attachment/20220615/08b68f97c615443c9a73fe39d83e7e9e.png','image/png','.png',NULL,NULL,0,1,'2022-06-15 09:14:18',NULL),
	(30,1,1,'1.jpg',NULL,'/attachment/20220615/40d6792d855449d4bcc88fdd34fd5c7c.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-06-15 11:19:13',NULL),
	(31,1,1,'btn_r.png',NULL,'/attachment/20220615/c9a0b2e5c8854aad9a807f028eaa11c8.png','image/png','.png',NULL,NULL,0,1,'2022-06-15 11:19:27',NULL),
	(32,1,3,'ad2.jpg',NULL,'/attachment/20220615/cea94cb30aa74505b2fcb5fa71dd688e.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-06-15 11:22:47',NULL),
	(33,1,3,'book1.jpg',NULL,'/attachment/20220615/b8e603f8f35d47efa09c5d0cf384e4e3.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-06-15 11:23:06',NULL),
	(34,1,1,'poster-video.jpg',NULL,'/attachment/20220615/e381ac7645c74f3a8df3384982908701.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-06-15 11:23:34',NULL),
	(35,1,1,'wb6.png',NULL,'/attachment/20220615/b532cea1770b45e196792ff46ab25c5a.png','image/png','.png',NULL,NULL,0,1,'2022-06-15 11:23:54',NULL),
	(37,1,NULL,'video.mp4',NULL,'/attachment/20220617/ba930ecaabc7420d80f1db9341fce7b4.mp4','video/mp4','.mp4',NULL,NULL,0,1,'2022-06-17 10:11:38',NULL),
	(38,1,NULL,'people.png',NULL,'/attachment/20220617/58f750d10dcd4f40888cb56fb118a844.png','image/png','.png',NULL,NULL,0,1,'2022-06-17 16:51:54',NULL),
	(39,1,NULL,'student.png',NULL,'/attachment/20220617/e88f8593f64947d0bfec744bb3d749b7.png','image/png','.png',NULL,NULL,0,1,'2022-06-17 16:54:31',NULL),
	(40,1,NULL,'wb22.jpg',NULL,'/attachment/20220620/d3425c33fde44ea2aba631068737e241.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-06-20 15:50:56',NULL),
	(42,1,1,'wb22.jpg',NULL,'/attachment/20220621/71f525e65a004b38aec9da429776ab3f.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-06-21 10:14:39',NULL),
	(43,1,NULL,'student.png',NULL,'/attachment/20220621/fcbfbd77088746099b2a2cebbd9bdf38.png','image/png','.png',NULL,NULL,0,1,'2022-06-21 15:24:20',NULL),
	(44,1,NULL,'6.png',NULL,'/attachment/20220621/12c21971d7b342f5882efddf85b5451d.png','image/png','.png',NULL,NULL,0,1,'2022-06-21 17:30:40',NULL),
	(45,1,NULL,'people.png',NULL,'/attachment/20220622/ebebc447f6e647d9b5e8f709a50d40e0.png','image/png','.png',NULL,NULL,0,1,'2022-06-22 10:38:48',NULL),
	(46,1,NULL,'1.jpg',NULL,'/attachment/20220622/424199b9e83345b2b2528c0d3ef672ce.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-06-22 16:16:15',NULL),
	(47,1,NULL,'2.jpg',NULL,'/attachment/20220622/2f4b702288c64b39b62afe3240299678.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-06-22 17:05:09',NULL),
	(48,1,NULL,'people.png',NULL,'/attachment/20220623/9f54019415ec4d52a147a4c9dd44f651.png','image/png','.png',NULL,NULL,0,1,'2022-06-23 09:38:47',NULL),
	(49,1,NULL,'studyroom.png',NULL,'/attachment/20220623/cef50fb521b24270939caf0b6492441a.png','image/png','.png',NULL,NULL,0,1,'2022-06-23 09:48:01',NULL),
	(50,1,NULL,'class.png',NULL,'/attachment/20220623/93f2daaaed994908bf352f2ef5d50dac.png','image/png','.png',NULL,NULL,0,1,'2022-06-23 10:43:11',NULL),
	(51,1,NULL,'avatar.jpg',NULL,'/attachment/20220623/7a76450f75a9416ebfc8dfa67bb408f8.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-06-23 10:43:51',NULL),
	(52,1,NULL,'2.jpg',NULL,'/attachment/20220623/10a422b68f4249ef9aa37bb494912231.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-06-23 13:46:47',NULL),
	(53,1,NULL,'people.png',NULL,'/attachment/20220623/abb7826df3254978b2ef01976f04b1ed.png','image/png','.png',NULL,NULL,0,1,'2022-06-23 15:39:00',NULL),
	(54,1,NULL,'student.png',NULL,'/attachment/20220628/2d71703ff4f8402db3425c0852242466.png','image/png','.png',NULL,NULL,0,1,'2022-06-28 14:01:12',NULL),
	(55,1,NULL,'1.jpg',NULL,'/attachment/20220629/2bbe54d79cab4cd89cd548dd995896cd.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-06-29 09:56:21',NULL),
	(56,1,NULL,'video.mp4',NULL,'/attachment/20220701/d2f3e920d2c947169509bdcbff437a30.mp4','video/mp4','.mp4',NULL,NULL,0,1,'2022-07-01 09:19:10',NULL),
	(57,1,NULL,'3123111.jpeg',NULL,'/attachment/20220701/2f628e5429c04643a2b0e4060f996245.jpeg','image/jpeg','.jpeg',NULL,NULL,0,1,'2022-07-01 09:21:10',NULL),
	(58,1,NULL,'studyroom.png',NULL,'/attachment/20220701/9666269f399545fb81cd1cb2b383ddb0.png','image/png','.png',NULL,NULL,0,1,'2022-07-01 10:04:56',NULL),
	(59,1,NULL,'student.png',NULL,'/attachment/20220701/cc04bfeecf2a4c3b8d5c93fded30cd7d.png','image/png','.png',NULL,NULL,0,1,'2022-07-01 16:31:21',NULL),
	(60,1,NULL,'course2.png',NULL,'/attachment/20220701/6e9b0e86116646ba8528047979bfea48.png','image/png','.png',NULL,NULL,0,1,'2022-07-01 16:31:34',NULL),
	(61,1,NULL,'people.png',NULL,'/attachment/20220701/9589653c891345778e8a7d88fa42f8dd.png','image/png','.png',NULL,NULL,0,1,'2022-07-01 16:31:46',NULL),
	(62,1,NULL,'course.png',NULL,'/attachment/20220701/e2c20ed766f24fef83c872cb9027d679.png','image/png','.png',NULL,NULL,0,1,'2022-07-01 16:31:58',NULL),
	(63,1,NULL,'avatar.jpg',NULL,'/attachment/20220701/b60ff6b23b7b46b7bebd407f663d3979.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-07-01 16:32:16',NULL),
	(64,1,NULL,'back-right.png',NULL,'/attachment/20220701/52fd9e1c6f4346febd6b8c93d765679e.png','image/png','.png',NULL,NULL,0,1,'2022-07-01 16:32:37',NULL),
	(65,1,NULL,'1.jpg',NULL,'/attachment/20220701/db831e21fdf34726aa6b26a8908e5dac.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-07-01 16:32:52',NULL),
	(66,1,NULL,'school2.png',NULL,'/attachment/20220701/4fc867a3c6c94cd594537f25103414a1.png','image/png','.png',NULL,NULL,0,1,'2022-07-01 16:33:05',NULL),
	(67,1,NULL,'select1.jpg',NULL,'/attachment/20220701/091a5bdf2ed143d6838a902628c11cfd.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-07-01 16:33:40',NULL),
	(68,1,NULL,'recommend.png',NULL,'/attachment/20220701/01ee54382747410ba68a27e88d64e594.png','image/png','.png',NULL,NULL,0,1,'2022-07-01 16:33:58',NULL),
	(69,1,NULL,'title.jpg',NULL,'/attachment/20220701/f803e9e20a8448919e3c16feb0698ff7.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-07-01 16:34:17',NULL),
	(70,1,NULL,'people.png',NULL,'/attachment/20220704/e3b0ac35cf9a4c938775b4145b5fb110.png','image/png','.png',NULL,NULL,0,1,'2022-07-04 11:14:18',NULL),
	(71,1,NULL,'video.mp4',NULL,'/attachment/20220704/9726d3e6b35a453293cebc778e529a08.mp4','video/mp4','.mp4',NULL,NULL,0,1,'2022-07-04 15:29:08',NULL),
	(72,1,NULL,'video.mp4',NULL,'/attachment/20220705/866319d9d0c5498096609df5ead08725.mp4','video/mp4','.mp4',NULL,NULL,0,1,'2022-07-05 08:49:27',NULL),
	(73,1,NULL,'student.png',NULL,'/attachment/20220705/bc49f3e4204d4b3e80c2c16d3ec92e70.png','image/png','.png',NULL,NULL,0,1,'2022-07-05 15:50:16',NULL),
	(74,1,NULL,'studyroom.png',NULL,'/attachment/20220705/56a1c33c92164862839c0e52fce14714.png','image/png','.png',NULL,NULL,0,1,'2022-07-05 16:49:09',NULL),
	(75,1,NULL,'avatar.jpg',NULL,'/attachment/20220705/7b14c2c52c3a4f58a2c7c6175cec2e7e.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-07-05 17:04:12',NULL),
	(76,1,NULL,'video.mp4',NULL,'/attachment/20220705/314c0b1730f54d28be7ec25c2d780544.mp4','video/mp4','.mp4',NULL,NULL,0,1,'2022-07-05 17:23:08',NULL),
	(77,1,NULL,'course2.png',NULL,'/attachment/20220706/13c36dcfb4794890a266805c4baefbec.png','image/png','.png',NULL,NULL,0,1,'2022-07-06 10:33:44',NULL),
	(78,1,NULL,'course.png',NULL,'/attachment/20220706/16c145a722ee421193a05cae39fd21c2.png','image/png','.png',NULL,NULL,0,1,'2022-07-06 11:14:30',NULL),
	(79,1,NULL,'video.mp4',NULL,'/attachment/20220706/f230798a27794a64a4046f3ef610f2d3.mp4','video/mp4','.mp4',NULL,NULL,0,1,'2022-07-06 14:20:20',NULL),
	(80,1,NULL,'student.png',NULL,'/attachment/20220707/4e6bbecf5ade4c86915439176ff94a73.png','image/png','.png',NULL,NULL,0,1,'2022-07-07 08:57:04',NULL),
	(81,1,NULL,'course2.png',NULL,'/attachment/20220707/ec3e44685c64477497fc3a495f1ced83.png','image/png','.png',NULL,NULL,0,1,'2022-07-07 09:01:07',NULL),
	(82,1,NULL,'course.png',NULL,'/attachment/20220707/4f4b13f30b65423aaee4dde98dba8c23.png','image/png','.png',NULL,NULL,0,1,'2022-07-07 09:01:19',NULL),
	(83,1,NULL,'studyroom.png',NULL,'/attachment/20220707/3eb1ef75fca44c01965717e96cd8695c.png','image/png','.png',NULL,NULL,0,1,'2022-07-07 11:50:17',NULL),
	(84,1,NULL,'course.png',NULL,'/attachment/20220707/7989e8a4b4044fdea638de1b5478d908.png','image/png','.png',NULL,NULL,0,1,'2022-07-07 11:51:45',NULL),
	(85,1,NULL,'course2.png',NULL,'/attachment/20220707/cbd0d563e68448c39bac78ad70a77e75.png','image/png','.png',NULL,NULL,0,1,'2022-07-07 11:51:56',NULL),
	(86,1,NULL,'video.png',NULL,'/attachment/20220707/e7799ed053ec470399f9ec20ec044a2a.png','image/png','.png',NULL,NULL,0,1,'2022-07-07 16:00:23',NULL),
	(87,1,NULL,'2.jpg',NULL,'/attachment/20220711/fcf6fe85ebb049f6988370cde62b4f61.jpg','image/jpeg','.jpg',NULL,NULL,0,1,'2022-07-11 15:09:28',NULL),
	(88,1,NULL,'course.png',NULL,'/attachment/20220712/b1cdbdef0968417da31b68cdf196f5a9.png','image/png','.png',NULL,NULL,0,1,'2022-07-12 16:35:58',NULL),
	(89,1,NULL,'course2.png',NULL,'/attachment/20220712/dc5b0af6e5b041458ab95aafe9c2dd81.png','image/png','.png',NULL,NULL,0,1,'2022-07-12 16:43:03',NULL),
	(90,1,NULL,'video.mp4',NULL,'/attachment/20220713/a01cc06232504566a334a76a0cce428b.mp4','video/mp4','.mp4',NULL,NULL,0,1,'2022-07-13 14:13:39',NULL),
	(91,1,NULL,'video.mp4',NULL,'/attachment/20220713/eff549d146444a0192ceed6a0605a597.mp4','video/mp4','.mp4',NULL,NULL,0,1,'2022-07-13 14:15:26',NULL),
	(92,1,NULL,'3123111.jpeg',NULL,'/attachment/20220717/bceb825c9d224f8fb1316f4bab29d13d.jpeg','image/jpeg','.jpeg',NULL,NULL,0,1,'2022-07-17 17:03:15',NULL),
	(93,1,NULL,'111123123213123.jpeg',NULL,'/attachment/20220717/7b9cb0991458403b80397f01debe3006.jpeg','image/jpeg','.jpeg',NULL,NULL,0,1,'2022-07-17 17:08:54',NULL);

/*!40000 ALTER TABLE `attachment` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 attachment_category
# ------------------------------------------------------------

DROP TABLE IF EXISTS `attachment_category`;

CREATE TABLE `attachment_category` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(512) DEFAULT NULL COMMENT '标题',
  `count` int(11) unsigned DEFAULT '0' COMMENT '该分类的内容数量',
  `order_number` int(11) DEFAULT '0' COMMENT '排序编码',
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='附件分类表';

LOCK TABLES `attachment_category` WRITE;
/*!40000 ALTER TABLE `attachment_category` DISABLE KEYS */;

INSERT INTO `attachment_category` (`id`, `title`, `count`, `order_number`, `created`, `modified`)
VALUES
	(1,'附件分类-13',6,1,'2022-06-13 14:19:16','2022-07-07 18:38:03'),
	(3,'附件分类-2',2,2,'2022-06-13 14:27:56','2022-07-07 18:38:10');

/*!40000 ALTER TABLE `attachment_category` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 attachment_video
# ------------------------------------------------------------

DROP TABLE IF EXISTS `attachment_video`;

CREATE TABLE `attachment_video` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `video_type` varchar(32) DEFAULT NULL COMMENT 'vod、live、code',
  `cloud_type` varchar(32) DEFAULT NULL COMMENT '视频云',
  `category_id` int(11) DEFAULT NULL COMMENT '分类ID',
  `cover` varchar(512) DEFAULT NULL COMMENT '视频封面',
  `vod_vid` varchar(64) DEFAULT NULL COMMENT '视频云端ID',
  `vod_name` varchar(256) DEFAULT NULL COMMENT '视频名称',
  `vod_size` int(11) unsigned DEFAULT NULL COMMENT '视频大小',
  `vod_status` varchar(32) DEFAULT NULL COMMENT '视频状态',
  `vod_duration` int(11) unsigned DEFAULT NULL COMMENT '视频播放时长，单位秒',
  `live_domain` varchar(64) DEFAULT NULL COMMENT '直播播放域名',
  `live_app` varchar(64) DEFAULT NULL COMMENT '直播应用名称',
  `live_stream` varchar(64) DEFAULT NULL COMMENT '直接流名称',
  `live_push_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '直播推流地址',
  `live_start_time` datetime DEFAULT NULL COMMENT '开始直播时间',
  `live_end_time` datetime DEFAULT NULL COMMENT '结束直播时间',
  `content` text COMMENT 'iframe 代码',
  `options` text COMMENT '其他配置',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频附件';

LOCK TABLES `attachment_video` WRITE;
/*!40000 ALTER TABLE `attachment_video` DISABLE KEYS */;

INSERT INTO `attachment_video` (`id`, `video_type`, `cloud_type`, `category_id`, `cover`, `vod_vid`, `vod_name`, `vod_size`, `vod_status`, `vod_duration`, `live_domain`, `live_app`, `live_stream`, `live_push_url`, `live_start_time`, `live_end_time`, `content`, `options`, `created`, `modified`)
VALUES
	(1,'vod',NULL,5,NULL,NULL,'扣篮大赛',NULL,'9',NULL,NULL,NULL,NULL,NULL,'2022-06-21 10:03:00','2022-06-21 10:03:00',NULL,NULL,'2022-06-17 11:41:39','2022-06-21 10:04:59'),
	(4,'live',NULL,6,'/attachment/20220617/58f750d10dcd4f40888cb56fb118a844.png',NULL,'十佳球',NULL,'9',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2022-06-17 16:48:53','2022-06-17 16:59:19'),
	(5,'live',NULL,4,'/attachment/20220620/d3425c33fde44ea2aba631068737e241.jpg',NULL,'三分球大赛',NULL,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2022-06-20 15:50:59',NULL),
	(6,'vod','2',4,'/attachment/20220701/f803e9e20a8448919e3c16feb0698ff7.jpg','7bd0eb84085e4b19af6d017280f1dc50','总决赛',409258,'1',10000,NULL,NULL,NULL,NULL,'2022-06-20 16:23:00','2022-06-30 16:23:00','10101010140420',NULL,'2022-06-20 16:23:48','2022-07-01 16:34:19'),
	(7,'vod','2',6,'/attachment/20220701/01ee54382747410ba68a27e88d64e594.png','1398dcd7d9ce42ee8f15d047be44e36d','乔丹教学',409258,'1',1000,NULL,NULL,NULL,NULL,'2022-06-21 09:59:00','2022-06-21 09:59:00',NULL,NULL,'2022-06-21 09:59:51','2022-07-01 16:33:59'),
	(15,'vod','2',6,'/attachment/20220701/091a5bdf2ed143d6838a902628c11cfd.jpg','c8a197ce2dd147b29924808fc8707930','第一个视频0',409258,'9',7,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2022-06-21 15:59:02','2022-07-01 16:33:41'),
	(68,'vod','2',5,'/attachment/20220701/4fc867a3c6c94cd594537f25103414a1.png','d7d47f0f55d64e32937ce026fb8c3cea','什么视频',409258,'9',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2022-06-22 17:18:41','2022-07-01 16:33:07'),
	(70,'vod','2',NULL,'/attachment/20220701/db831e21fdf34726aa6b26a8908e5dac.jpg',NULL,'4552',NULL,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2022-06-23 14:23:08','2022-07-01 16:32:53'),
	(74,'vod','2',5,'/attachment/20220701/52fd9e1c6f4346febd6b8c93d765679e.png','1226b19b21a44040aec5436ebcabefaa','一个视频',NULL,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2022-06-23 15:39:35','2022-07-01 16:32:38'),
	(75,'live','2',NULL,'/attachment/20220701/b60ff6b23b7b46b7bebd407f663d3979.jpg',NULL,'直播吗111',NULL,'1',NULL,'http://live.jpress.cn','cgzj','13fddd1dbcf147f7909b6f2d71ca7a5e','rtmp://pushlive.jpress.cn/cgzj/13fddd1dbcf147f7909b6f2d71ca7a5e?auth_key=1655980987-0-0-82d171fd1fd363ee8b1db2d8033f4763','2022-06-23 16:42:00','2022-06-23 16:42:00',NULL,NULL,'2022-06-23 16:43:18','2022-07-01 16:32:18'),
	(79,'vod','2',NULL,'/attachment/20220701/e2c20ed766f24fef83c872cb9027d679.png',NULL,'www',NULL,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2022-06-23 17:20:58','2022-07-01 16:32:00'),
	(80,'vod','2',6,'/attachment/20220701/9589653c891345778e8a7d88fa42f8dd.png','387702302489451763','测试11212',NULL,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2022-06-27 16:24:07','2022-07-01 16:31:48'),
	(81,'vod','2',5,'/attachment/20220701/6e9b0e86116646ba8528047979bfea48.png','387702302490459125','ces ',NULL,'1',NULL,'ypf-play.jpress.cn','live','liveteststream','rtmp://170100.livepush.myqcloud.com/live/liveteststream?txSecret=de2456f7850646cc3ddda9b15446ef4d&amp;txTime=62BD0019','2022-06-28 16:03:00','2022-06-28 16:03:00',NULL,NULL,'2022-06-28 14:01:22','2022-07-01 16:31:35'),
	(82,'vod','1',NULL,'/attachment/20220707/e7799ed053ec470399f9ec20ec044a2a.png','38bab6eea9a44b0abd85a64099382fc8','阿里云-2',NULL,'1',NULL,'ypf-play.jpress.cn','live','liveteststream','rtmp://170100.livepush.myqcloud.com/live/liveteststream?txSecret=de2456f7850646cc3ddda9b15446ef4d&amp;txTime=62BD0019',NULL,NULL,NULL,NULL,'2022-06-29 14:27:44','2022-07-13 15:08:30'),
	(83,'vod','2',4,'/attachment/20220707/cbd0d563e68448c39bac78ad70a77e75.png','387702303148726927','腾讯云-2',NULL,'1',NULL,'ypf-play.jpress.cn','live','liveteststream','rtmp://170100.livepush.myqcloud.com/live/liveteststream?txSecret=408f30ced9e374b3edc206a58ecad4f7&amp;txTime=62BE59B0',NULL,NULL,NULL,NULL,'2022-06-30 10:26:55','2022-07-13 16:06:31'),
	(84,'vod','2',NULL,'/attachment/20220712/dc5b0af6e5b041458ab95aafe9c2dd81.png','387702303148321756','腾讯云视频',409258,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2022-07-01 16:35:34','2022-07-13 16:04:16'),
	(85,'vod','2',NULL,'/attachment/20220712/b1cdbdef0968417da31b68cdf196f5a9.png','4d3871e9edc0430485d44222dff9e124','阿里云视频',NULL,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2022-07-04 14:40:44','2022-07-14 15:53:36'),
	(86,'live','2',5,NULL,NULL,'腾讯云-直播',NULL,'1',NULL,'ypf-play.jpress.cn','live','4e05c174541d4b719df2ca33dbba8e18','rtmp://170100.livepush.myqcloud.com/live/4e05c174541d4b719df2ca33dbba8e18?txSecret=360d20d09fbf106d371b60ad9ef63b77&amp;txTime=62D76005','2022-07-18 17:18:00','2022-07-19 17:18:00',NULL,NULL,'2022-07-18 17:19:27','2022-07-19 10:04:26'),
	(87,'live','2',6,NULL,NULL,'腾讯云  ---直播1',NULL,'1',NULL,'ypf-play.jpress.cn','live','fcaf27c4eda74a7c95b1a499371178c7','rtmp://170100.livepush.myqcloud.com/live/fcaf27c4eda74a7c95b1a499371178c7?txSecret=3b76b8ef7bbecccc9783843f0d5caedb&amp;txTime=62D7664C','2022-07-19 10:18:00','2022-07-20 10:18:00',NULL,NULL,'2022-07-19 10:18:04','2022-07-19 10:20:41'),
	(88,'vod','2',NULL,NULL,NULL,'101',NULL,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2022-07-19 11:01:55',NULL);

/*!40000 ALTER TABLE `attachment_video` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 attachment_video_category
# ------------------------------------------------------------

DROP TABLE IF EXISTS `attachment_video_category`;

CREATE TABLE `attachment_video_category` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(512) DEFAULT NULL COMMENT '标题',
  `count` int(11) unsigned DEFAULT '0' COMMENT '该分类的内容数量',
  `order_number` int(11) DEFAULT '0' COMMENT '排序编码',
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='附件分类表';

LOCK TABLES `attachment_video_category` WRITE;
/*!40000 ALTER TABLE `attachment_video_category` DISABLE KEYS */;

INSERT INTO `attachment_video_category` (`id`, `title`, `count`, `order_number`, `created`, `modified`)
VALUES
	(4,'精彩回顾',3,1,'2022-06-17 11:54:00','2022-07-13 16:06:31'),
	(5,'采访',5,2,'2022-06-17 11:54:40','2022-07-19 10:04:26'),
	(6,'十佳球',5,3,'2022-06-17 16:44:22','2022-07-19 10:20:41');

/*!40000 ALTER TABLE `attachment_video_category` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 form_data_3_14
# ------------------------------------------------------------

DROP TABLE IF EXISTS `form_data_3_14`;

CREATE TABLE `form_data_3_14` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ssss` varchar(11) DEFAULT NULL COMMENT '姓名',
  `bbbb` text COMMENT '描述',
  `v_enable` int(1) DEFAULT NULL COMMENT '是否启用',
  `s1` varchar(11) DEFAULT NULL COMMENT '单选框',
  `s2` varchar(11) DEFAULT NULL COMMENT '复选框',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `form_data_3_14` WRITE;
/*!40000 ALTER TABLE `form_data_3_14` DISABLE KEYS */;

INSERT INTO `form_data_3_14` (`id`, `ssss`, `bbbb`, `v_enable`, `s1`, `s2`)
VALUES
	(1,'aaa','bbb',NULL,'value1','value2'),
	(2,'ccc','ddd',NULL,'value2','value1'),
	(3,'zhangsan','xxxxx',NULL,'value1','value2');

/*!40000 ALTER TABLE `form_data_3_14` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 form_datasource
# ------------------------------------------------------------

DROP TABLE IF EXISTS `form_datasource`;

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单数据字典';

LOCK TABLES `form_datasource` WRITE;
/*!40000 ALTER TABLE `form_datasource` DISABLE KEYS */;

INSERT INTO `form_datasource` (`id`, `name`, `import_type`, `import_text`, `headers`, `with_cascade`, `with_static`, `created`, `modified`, `options`, `site_id`)
VALUES
	(3,'字典1',0,NULL,NULL,0,1,'2022-07-04 00:00:00','2022-07-15 16:14:06',NULL,0),
	(4,'字典2',2,'nnnnnnnnnn',NULL,0,0,'2022-07-04 00:00:00','2022-07-15 16:12:21',NULL,0),
	(16,'测试5',1,'[{&#39;value&#39;:&#39;张三&#39;,&#39;text&#39;:20}]','xxxxxxx',1,1,'2022-07-12 00:00:00','2022-07-15 16:12:27',NULL,0),
	(17,'4S店数据源',2,NULL,NULL,0,1,'2022-07-19 14:02:40','2022-07-19 14:07:21',NULL,0);

/*!40000 ALTER TABLE `form_datasource` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 form_datasource_item
# ------------------------------------------------------------

DROP TABLE IF EXISTS `form_datasource_item`;

CREATE TABLE `form_datasource_item` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `pid` int(11) unsigned DEFAULT NULL COMMENT '上级ID，级联数据源的时候用到',
  `datasource_id` int(11) unsigned DEFAULT NULL COMMENT '归属的数据源ID',
  `value` varchar(64) DEFAULT NULL COMMENT 'value 值',
  `text` text COMMENT '文本内容',
  `image` varchar(512) DEFAULT NULL COMMENT '图片 URL 地址',
  `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `form_datasource_item` WRITE;
/*!40000 ALTER TABLE `form_datasource_item` DISABLE KEYS */;

INSERT INTO `form_datasource_item` (`id`, `pid`, `datasource_id`, `value`, `text`, `image`, `site_id`)
VALUES
	(43,0,16,'张三','20',NULL,0),
	(46,0,17,'111','11111',NULL,0);

/*!40000 ALTER TABLE `form_datasource_item` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 form_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `form_info`;

CREATE TABLE `form_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL COMMENT '表单名称',
  `title` varchar(256) DEFAULT NULL COMMENT '表单标题（前台）',
  `summary` text COMMENT '表单描述（前台）',
  `flag` varchar(32) DEFAULT NULL COMMENT '表单标识',
  `bg_image` varchar(512) DEFAULT NULL COMMENT '表单背景图',
  `header_image` varchar(512) DEFAULT NULL COMMENT '表单头图',
  `builder_json` text COMMENT '表单构建 JSON',
  `options` text COMMENT 'json扩展配置',
  `version` int(11) DEFAULT NULL COMMENT '版本号',
  `status` varchar(32) DEFAULT NULL COMMENT '状态',
  `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义表单';

LOCK TABLES `form_info` WRITE;
/*!40000 ALTER TABLE `form_info` DISABLE KEYS */;

INSERT INTO `form_info` (`id`, `name`, `title`, `summary`, `flag`, `bg_image`, `header_image`, `builder_json`, `options`, `version`, `status`, `site_id`)
VALUES
	(3,'表单1','aaaa','aaaaaabbbb','表单1的标识',NULL,'/attachment/20220717/7b9cb0991458403b80397f01debe3006.jpeg','[{\"id\":\"a42a599a2d\",\"tag\":\"input\",\"label\":\"姓名\",\"name\":\"input_1\",\"field_type\":\"varchar\",\"field_lenth\":11,\"show_list\":true,\"index\":0,\"field\":\"ssss\",\"with_search\":true},{\"id\":\"76fa74784b\",\"tag\":\"textarea\",\"label\":\"描述\",\"name\":\"textarea_2\",\"field_type\":\"text\",\"field_lenth\":11,\"show_list\":true,\"rows\":3,\"index\":1,\"field\":\"bbbb\"},{\"id\":\"c6e5eb1ca3\",\"tag\":\"switch\",\"label\":\"是否启用\",\"name\":\"switch_1\",\"field_type\":\"boolean\",\"field_lenth\":11,\"show_list\":true,\"with_search\":false,\"index\":2,\"field\":\"v_enable\"},{\"id\":\"9934434e9d\",\"tag\":\"grid\",\"label\":\"等分栅格\",\"name\":\"grid_1\",\"grid\":2,\"index\":3,\"children\":{\"0\":[{\"id\":\"ee59439959\",\"tag\":\"radio\",\"label\":\"单选框\",\"name\":\"radio_2\",\"field_type\":\"varchar\",\"field_lenth\":11,\"show_list\":true,\"options\":[{\"text\":\"选项1\",\"value\":\"value1\"},{\"text\":\"选项2\",\"value\":\"value2\"}],\"optionsTitle\":\"选项\",\"parentDataId\":\"9934434e9d\",\"parentDataIndex\":0,\"index\":0,\"field\":\"s1\"}],\"1\":[{\"id\":\"c0a76b0e53\",\"tag\":\"checkbox\",\"label\":\"复选框\",\"name\":\"checkbox_3\",\"field_type\":\"varchar\",\"field_lenth\":11,\"show_list\":true,\"options\":[{\"text\":\"选项1\",\"value\":\"value1\"},{\"text\":\"选项2\",\"value\":\"value2\"}],\"optionsTitle\":\"选项\",\"parentDataId\":\"9934434e9d\",\"parentDataIndex\":1,\"index\":0,\"field\":\"s2\"}]}}]',NULL,14,'published',0),
	(4,'表单2',NULL,NULL,'表单2标识',NULL,NULL,NULL,NULL,1,'init',0);

/*!40000 ALTER TABLE `form_info` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 job
# ------------------------------------------------------------

DROP TABLE IF EXISTS `job`;

CREATE TABLE `job` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(128) DEFAULT NULL COMMENT '职位名称或者标题',
  `content` text COMMENT '描述',
  `department` varchar(256) DEFAULT NULL COMMENT '对应部门',
  `category_id` int(11) unsigned DEFAULT NULL COMMENT '对应分类id',
  `address_id` int(11) unsigned DEFAULT NULL COMMENT '工作地点',
  `age_limit_start` int(11) DEFAULT NULL COMMENT '年龄开始',
  `age_limit_end` int(11) DEFAULT NULL COMMENT '年龄结束',
  `education` tinyint(2) DEFAULT NULL COMMENT '学历',
  `years_limit_type` tinyint(2) DEFAULT NULL COMMENT '工作年限',
  `with_notify` tinyint(1) DEFAULT NULL COMMENT '有建立申请时，是否通知',
  `notify_emails` varchar(512) DEFAULT NULL COMMENT '通知的邮箱',
  `notify_title` varchar(256) DEFAULT NULL COMMENT '通知的邮件标题',
  `notify_content` text COMMENT '通知的邮件内容',
  `with_remote` tinyint(1) DEFAULT NULL COMMENT '是否属于远程工作',
  `with_apply` tinyint(1) DEFAULT NULL COMMENT '是否允许在线投递',
  `with_hurry` tinyint(1) DEFAULT NULL COMMENT '急招',
  `work_type` tinyint(2) DEFAULT NULL COMMENT '工作类型',
  `recruit_type` tinyint(2) DEFAULT NULL COMMENT '招聘类型',
  `recruit_numbers` int(11) DEFAULT NULL COMMENT '岗位招聘人数',
  `expired_to` datetime DEFAULT NULL COMMENT '岗位有效时间',
  `meta_keywords` varchar(512) DEFAULT NULL COMMENT 'SEO关键字',
  `meta_description` varchar(512) DEFAULT NULL COMMENT 'SEO描述信息',
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL COMMENT '最后更新日期',
  `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作岗位表';

LOCK TABLES `job` WRITE;
/*!40000 ALTER TABLE `job` DISABLE KEYS */;

INSERT INTO `job` (`id`, `title`, `content`, `department`, `category_id`, `address_id`, `age_limit_start`, `age_limit_end`, `education`, `years_limit_type`, `with_notify`, `notify_emails`, `notify_title`, `notify_content`, `with_remote`, `with_apply`, `with_hurry`, `work_type`, `recruit_type`, `recruit_numbers`, `expired_to`, `meta_keywords`, `meta_description`, `created`, `modified`, `site_id`)
VALUES
	(32,'岗位1','&lt;p&gt;xxxxxxxxxxxxxxxxxxxx&lt;/p&gt;','研发部',10,6,20,35,5,2,0,'1871225044@qq.com','这是测试用的邮件标题','这是测试用的邮件内容',0,1,NULL,0,0,10,'2022-07-06 00:00:00','xxxxx','xxxxxx','2022-07-05 00:00:00','2022-07-07 16:38:40',0),
	(33,'岗位2','&lt;p&gt;vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv&lt;/p&gt;','运维部',9,6,18,35,5,3,1,'1871225044@qq.com','测试通知邮件','测试通知邮件内容',1,1,NULL,0,1,2,'2022-07-07 00:00:00','vvvvv','vvvvv','2022-07-01 00:00:00','2022-07-08 15:10:33',0),
	(34,'岗位3','&lt;p&gt;33333333333333333333333333333333333333&lt;/p&gt;','研发部',19,6,20,35,6,4,0,'2822115980@qq.com','测试邮件','测试邮件内容',1,1,0,0,0,10,'2022-07-09 00:00:00','嘻嘻嘻嘻嘻','xxxxxx','2022-07-07 00:00:00','2022-07-18 15:28:50',0),
	(38,'岗位1','&lt;p&gt;xxxxxxxxxxxxx&lt;/p&gt;',NULL,18,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,NULL,NULL,'2022-07-18 10:46:17',NULL,0);

/*!40000 ALTER TABLE `job` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 job_apply
# ------------------------------------------------------------

DROP TABLE IF EXISTS `job_apply`;

CREATE TABLE `job_apply` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `job_id` int(10) unsigned DEFAULT NULL COMMENT '申请的职位ID',
  `user_name` varchar(32) DEFAULT NULL COMMENT '用户名称',
  `mobile` varchar(32) DEFAULT NULL COMMENT '手机号',
  `mobile_area` varchar(16) DEFAULT NULL COMMENT '手机区号，中国 +86',
  `email` varchar(32) DEFAULT NULL COMMENT '邮箱',
  `gender` tinyint(2) DEFAULT NULL COMMENT '性别',
  `birthday` datetime DEFAULT NULL COMMENT '出生日期',
  `work_years` int(11) DEFAULT NULL COMMENT '工作年限',
  `education` tinyint(4) DEFAULT NULL COMMENT '最高学历',
  `last_company` varchar(256) DEFAULT NULL COMMENT '最近工作的公司',
  `cv_path` varchar(256) DEFAULT NULL COMMENT '上传的简历',
  `attachment` varchar(256) DEFAULT NULL COMMENT '上传的其他附件',
  `referral_code` varchar(32) DEFAULT NULL COMMENT '推荐码',
  `with_viewed` tinyint(1) DEFAULT NULL COMMENT '是否已经查看',
  `with_disposed` tinyint(1) DEFAULT NULL COMMENT '是否已经处理',
  `disposed_content` varchar(512) DEFAULT NULL COMMENT '处理意见，处理内容',
  `disposed_time` datetime DEFAULT NULL COMMENT '处理时间',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位申请信息';

LOCK TABLES `job_apply` WRITE;
/*!40000 ALTER TABLE `job_apply` DISABLE KEYS */;

INSERT INTO `job_apply` (`id`, `job_id`, `user_name`, `mobile`, `mobile_area`, `email`, `gender`, `birthday`, `work_years`, `education`, `last_company`, `cv_path`, `attachment`, `referral_code`, `with_viewed`, `with_disposed`, `disposed_content`, `disposed_time`, `created`, `site_id`)
VALUES
	(2,33,'小潘','17684251552','+86','1871225044@qq.com',0,'2022-07-06 16:16:00',1,5,'贵州小码科技','/attachment/20220706/412105e591b54ac0b0f74c14cedeb8ff.png','/attachment/20220706/d9f427876ce44b55bb45e8bc3fabf242.png',NULL,1,1,'不错啊 进入面试','2022-07-07 16:42:19','2022-07-06 16:17:14',NULL),
	(15,35,'小王','18212840981','+86','1871225044@qq.com',0,'2022-07-23 15:15:00',6,5,'贵州小码科技',NULL,NULL,'2244',1,1,'可以','2022-07-13 15:20:44','2022-07-13 15:20:21',0);

/*!40000 ALTER TABLE `job_apply` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 job_category
# ------------------------------------------------------------

DROP TABLE IF EXISTS `job_category`;

CREATE TABLE `job_category` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(32) DEFAULT NULL COMMENT '分类的类型：category、address',
  `pid` int(11) unsigned DEFAULT NULL COMMENT '父id',
  `title` varchar(255) DEFAULT NULL COMMENT '分类名称',
  `summary` varchar(255) DEFAULT NULL COMMENT '摘要',
  `count` int(11) unsigned DEFAULT NULL COMMENT '该分类下的岗位数量',
  `order_number` int(11) DEFAULT '0' COMMENT '排序编码',
  `flag` varchar(256) DEFAULT NULL COMMENT '标识',
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL COMMENT '修改日期',
  `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位分类表';

LOCK TABLES `job_category` WRITE;
/*!40000 ALTER TABLE `job_category` DISABLE KEYS */;

INSERT INTO `job_category` (`id`, `type`, `pid`, `title`, `summary`, `count`, `order_number`, `flag`, `created`, `modified`, `site_id`)
VALUES
	(14,'address',0,'地址2','bbbbb',NULL,2,'2','2022-07-13 09:45:14',NULL,0),
	(15,'address',0,'地址3','xx',NULL,9,'1','2022-07-13 15:04:53','2022-07-18 15:29:29',0),
	(18,'category',0,'分类1','分类1摘要',1,1,'1','2022-07-18 10:45:49','2022-07-18 14:45:51',0),
	(19,'category',18,'分类2','分类2摘要',1,8,'1','2022-07-18 10:57:53','2022-07-18 15:28:50',0),
	(20,'category',0,'分类3',NULL,0,3,'1','2022-07-18 13:54:58','2022-07-18 15:02:49',0),
	(21,'category',20,'分类4',NULL,0,4,'1','2022-07-18 13:55:42','2022-07-18 14:46:06',0),
	(22,'category',18,'分类5',NULL,0,6,'1','2022-07-18 15:02:08',NULL,0);

/*!40000 ALTER TABLE `job_category` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 menu
# ------------------------------------------------------------

DROP TABLE IF EXISTS `menu`;

CREATE TABLE `menu` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `pid` int(11) unsigned DEFAULT NULL COMMENT '父级ID',
  `text` varchar(128) DEFAULT NULL COMMENT '文本内容',
  `url` varchar(512) DEFAULT NULL COMMENT '链接的url',
  `target` varchar(32) DEFAULT NULL COMMENT '打开的方式',
  `icon` varchar(64) DEFAULT NULL COMMENT '菜单的icon',
  `flag` varchar(32) DEFAULT NULL COMMENT '菜单标识',
  `type` varchar(32) DEFAULT '' COMMENT '菜单类型：主菜单、顶部菜单、底部菜单',
  `order_number` int(11) DEFAULT '0' COMMENT '排序字段',
  `relative_table` varchar(32) DEFAULT NULL COMMENT '该菜单是否和其他表关联',
  `relative_id` int(11) unsigned DEFAULT NULL COMMENT '关联的具体数据id',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `modified` datetime DEFAULT NULL COMMENT '修改时间',
  `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `order_number` (`order_number`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='菜单表';

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;

INSERT INTO `menu` (`id`, `pid`, `text`, `url`, `target`, `icon`, `flag`, `type`, `order_number`, `relative_table`, `relative_id`, `created`, `modified`, `site_id`)
VALUES
	(9,0,'散文','/article/category/sanwen',NULL,NULL,NULL,'main',2,'article_category',3,'2022-06-09 17:34:13','2022-06-20 13:42:04',NULL);

/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 option
# ------------------------------------------------------------

DROP TABLE IF EXISTS `option`;

CREATE TABLE `option` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
  `key` varchar(128) DEFAULT NULL COMMENT '配置KEY',
  `value` text COMMENT '配置内容',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `site` (`site_id`,`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='配置信息表，用来保存网站的所有配置信息。';

LOCK TABLES `option` WRITE;
/*!40000 ALTER TABLE `option` DISABLE KEYS */;

INSERT INTO `option` (`id`, `site_id`, `key`, `value`)
VALUES
	(159,1,'web_subtitle',NULL),
	(160,1,'web_ipc_no',NULL),
	(161,1,'web_title',NULL),
	(162,1,'web_domain',NULL),
	(163,1,'web_mater_mobile',NULL),
	(164,1,'web_config_code',NULL),
	(165,1,'web_mater_wxopenid',NULL),
	(166,1,'web_mater_email',NULL),
	(167,1,'web_name','测试站点'),
	(168,1,'web_copyright',NULL),
	(169,0,'web_template','cn.jeanstudio.calmlog'),
	(170,0,'web_subtitle',NULL),
	(171,0,'web_ipc_no',NULL),
	(172,0,'web_title',NULL),
	(173,0,'web_domain','http://test.jpress.cn:8080'),
	(174,0,'web_mater_mobile',NULL),
	(175,0,'web_config_code',NULL),
	(176,0,'web_mater_wxopenid',NULL),
	(177,0,'web_mater_email',NULL),
	(178,0,'web_name','主站点'),
	(179,0,'web_copyright',NULL),
	(180,0,'attachment_watermark_position','1'),
	(181,0,'attachment_qcloudlive_streamname',NULL),
	(182,0,'attachment_watermark_transparency',NULL),
	(183,0,'attachment_aliyunoss_endpoint',NULL),
	(184,0,'attachment_qcloudlive_playdomain','ypf-play.jpress.cn'),
	(185,0,'attachment_img_maxsize',NULL),
	(186,0,'attachment_watermark_enable','false'),
	(187,0,'attachment_other_maxsize',NULL),
	(188,0,'attachment_aliyunvideo_accesskeyid',NULL),
	(189,0,'attachment_aliyunlive_playdomain',NULL),
	(190,0,'attachment_aliyunoss_bucketname',NULL),
	(191,0,'attachment_aliyunoss_accesskeysecret',NULL),
	(192,0,'attachment_qcloudvideo_secretkey','IL9lAcw5KPHwWrwGNI0ThpjPtAEft1Vz'),
	(193,0,'attachment_aliyunlive_accesskeysecret',NULL),
	(194,0,'attachment_aliyunlive_accesskeyid',NULL),
	(195,0,'attachment_aliyunlive_pushauthstring',NULL),
	(196,0,'attachment_qcloudlive_play_txtime','2022-07-20 10:19:56'),
	(197,0,'attachment_aliyunoss_del','false'),
	(198,0,'attachment_qcloudlive_appname','live'),
	(199,0,'attachment_cloud_type','3'),
	(200,0,'attachment_qcloudlive_push_key','iJfDTGWwGdNDy7cCDmmG'),
	(201,0,'attachment_aliyunlive_appname',NULL),
	(202,0,'attachment_qcloudvideo_appid',NULL),
	(203,0,'attachment_aliyunlive_regionid',NULL),
	(204,0,'attachment_qcloudlive_play_key','7GpbpNmEkjdDHZj6aZ7s'),
	(205,0,'attachment_qcloudlive_txtime','2022-07-20 10:19:56'),
	(206,0,'attachment_aliyunvideo_accesskeysecret','0ZTnS3vegB3wZA5eqOEzdj2QGgBSeO'),
	(207,0,'attachment_aliyunlive_pushdomain',NULL),
	(208,0,'attachment_aliyunoss_enable','false'),
	(209,0,'attachment_aliyunoss_accesskeyid',NULL),
	(210,0,'attachment_watermark_img',NULL),
	(211,0,'attachment_qcloudvideo_secretid','AKIDOefYO5YXO1PXQXdeXUEsLabVDqPozuZ1'),
	(212,0,'attachment_qcloudlive_pushdomain','170100.livepush.myqcloud.com'),
	(213,0,'attachment_aliyunlive_playauthstring',NULL),
	(214,0,'connection_sms_appid',NULL),
	(215,0,'connection_email_password','TZTQDVLOPLAPDSWD'),
	(216,0,'connection_sms_enable','true'),
	(217,0,'connection_email_ssl_enable','true'),
	(218,0,'connection_sms_type','aliyun'),
	(219,0,'connection_email_enable','true'),
	(220,0,'connection_email_account','y_2586283122@163.com'),
	(221,0,'connection_sms_appsecret',NULL),
	(222,0,'connection_email_smtp','smtp.163.com'),
	(223,0,'job_mobile_enable','false'),
	(224,0,'job_email_enable','false'),
	(225,0,'csrf_token','68ed59e60549ae1aab889afb2572d03a'),
	(226,0,'connection_sms_template','SMS_154950909'),
	(227,0,'connection_sms_sign',NULL),
	(228,0,'login_wechat_enable','false'),
	(229,0,'login_qq_appkey',NULL),
	(230,0,'reg_email_validate_enable','true'),
	(231,0,'login_weibo_enable','false'),
	(232,0,'login_github_appsecret',NULL),
	(233,0,'login_weibo_appkey',NULL),
	(234,0,'login_captcha_enable','true'),
	(235,0,'login_dingding_appsecret',NULL),
	(236,0,'reg_email_validate_title','欢迎注册成为JPress会员'),
	(237,0,'reg_users_is_not_activate','false'),
	(238,0,'login_qq_enable','false'),
	(239,0,'login_dingding_appkey',NULL),
	(240,0,'login_qq_appsecret',NULL),
	(241,0,'login_github_appkey',NULL),
	(242,0,'reg_sms_validate_enable','false'),
	(243,0,'reg_enable','true'),
	(244,0,'reg_sms_validate_sign',NULL),
	(245,0,'login_weibo_appsecret',NULL),
	(246,0,'reg_sms_validate_template',NULL),
	(247,0,'login_wechat_appsecret',NULL),
	(248,0,'login_gitee_appsecret',NULL),
	(249,0,'login_gitee_appkey',NULL),
	(250,0,'login_github_enable','false'),
	(251,0,'login_goto_url',NULL),
	(252,0,'login_gitee_enable','false'),
	(253,0,'login_dingding_enable','false'),
	(254,0,'login_wechat_appkey',NULL),
	(255,0,'reg_email_validate_template','这是邮件模板'),
	(256,22,'web_template','cn.jeanstudio.jpressportal'),
	(257,0,'reg_email_reset_pwd_template','你好，你如果想要遗忘了密码，请点击链接 {url} 进行密码重置...'),
	(258,0,'reg_email_reset_pwd_title','JPress重置密码，请点击下方链接'),
	(259,0,'browser_lang_automatic_jump','false'),
	(260,0,'reg_sms_reset_pwd_template',NULL),
	(261,0,'attachment_huaweivideo_ak',NULL),
	(262,0,'attachment_huaweivideo_sk',NULL);

/*!40000 ALTER TABLE `option` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 permission
# ------------------------------------------------------------

DROP TABLE IF EXISTS `permission`;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='权限表';



# 转储表 product
# ------------------------------------------------------------

DROP TABLE IF EXISTS `product`;

CREATE TABLE `product` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `slug` varchar(128) DEFAULT NULL COMMENT 'slug',
  `title` varchar(256) DEFAULT '' COMMENT '标题',
  `content` longtext COMMENT '内容',
  `summary` text COMMENT '摘要',
  `usp` text COMMENT '产品卖点',
  `thumbnail` varchar(512) DEFAULT NULL COMMENT '缩略图',
  `style` varchar(32) DEFAULT NULL COMMENT '样式',
  `order_number` int(11) DEFAULT '0' COMMENT '排序编号',
  `price` decimal(10,2) DEFAULT NULL COMMENT '商品价格',
  `origin_price` decimal(10,2) DEFAULT NULL COMMENT '原始价格',
  `limited_price` decimal(10,2) DEFAULT NULL COMMENT '限时优惠价（早鸟价）',
  `status` tinyint(2) DEFAULT NULL COMMENT '状态',
  `comment_status` tinyint(1) DEFAULT '1' COMMENT '评论状态，默认允许评论',
  `comment_count` int(11) unsigned DEFAULT '0' COMMENT '评论总数',
  `comment_time` datetime DEFAULT NULL COMMENT '最后评论时间',
  `view_count` int(11) unsigned DEFAULT '0' COMMENT '访问量',
  `sales_count` int(11) unsigned DEFAULT '0' COMMENT '销售量，用于放在前台显示',
  `buy_link` varchar(255) DEFAULT NULL COMMENT '购买链接',
  `vod_vid` varchar(64) DEFAULT NULL COMMENT '视频云端ID',
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL COMMENT '最后更新日期',
  `flag` varchar(256) DEFAULT NULL COMMENT '标识，通常用于对某几个商品进行标识，从而实现单独查询',
  `meta_keywords` varchar(512) DEFAULT NULL COMMENT 'SEO关键字',
  `meta_description` varchar(512) DEFAULT NULL COMMENT 'SEO描述信息',
  `remarks` text COMMENT '备注信息',
  `options` text COMMENT 'json 扩展',
  `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `slug` (`slug`) USING BTREE,
  KEY `created` (`created`) USING BTREE,
  KEY `view_count` (`view_count`) USING BTREE,
  KEY `order_number` (`order_number`) USING BTREE,
  KEY `sales_count` (`sales_count`) USING BTREE,
  KEY `status` (`status`) USING BTREE,
  KEY `flag` (`flag`(191)) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='商品表';

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;

INSERT INTO `product` (`id`, `slug`, `title`, `content`, `summary`, `usp`, `thumbnail`, `style`, `order_number`, `price`, `origin_price`, `limited_price`, `status`, `comment_status`, `comment_count`, `comment_time`, `view_count`, `sales_count`, `buy_link`, `vod_vid`, `created`, `modified`, `flag`, `meta_keywords`, `meta_description`, `remarks`, `options`, `site_id`)
VALUES
	(1,NULL,'产品1','<p>哈哈哈哈哈哈哈哈哈</p>','10','10','/attachment/20220629/2bbe54d79cab4cd89cd548dd995896cd.jpg',NULL,0,10.00,10.00,10.00,1,1,0,NULL,0,NULL,'www.baidu.com','85','2022-07-14 10:33:29','2022-07-14 10:33:29','1','10','10',NULL,NULL,0),
	(2,NULL,'测试视频商品','<p>xxxxxx</p>',NULL,NULL,NULL,NULL,0,20.00,NULL,NULL,1,1,0,NULL,0,NULL,NULL,'82','2022-07-15 14:30:50','2022-07-15 14:30:50',NULL,NULL,NULL,NULL,NULL,0);

/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 product_category
# ------------------------------------------------------------

DROP TABLE IF EXISTS `product_category`;

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
  `with_recommend` tinyint(1) DEFAULT NULL COMMENT '是否推荐',
  `with_top` tinyint(1) DEFAULT NULL COMMENT '是否置顶',
  `ornament` varchar(512) DEFAULT NULL COMMENT '装饰图',
  `thumbnail` varchar(512) DEFAULT NULL COMMENT '缩略图',
  `count` int(11) unsigned DEFAULT '0' COMMENT '该分类的内容数量',
  `order_number` int(11) DEFAULT '0' COMMENT '排序编码',
  `flag` varchar(256) DEFAULT NULL COMMENT '标识',
  `meta_keywords` varchar(256) DEFAULT NULL COMMENT 'SEO关键字',
  `meta_description` varchar(256) DEFAULT NULL COMMENT 'SEO描述内容',
  `options` text,
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL COMMENT '修改日期',
  `site_id` int(10) unsigned DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `typeslug` (`type`,`slug`) USING BTREE,
  KEY `order_number` (`order_number`) USING BTREE,
  KEY `pid` (`pid`) USING BTREE,
  KEY `flag` (`flag`(191)) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='商品分类表。标签、专题、类别等都属于category。';

LOCK TABLES `product_category` WRITE;
/*!40000 ALTER TABLE `product_category` DISABLE KEYS */;

INSERT INTO `product_category` (`id`, `pid`, `user_id`, `slug`, `title`, `content`, `summary`, `style`, `type`, `icon`, `with_recommend`, `with_top`, `ornament`, `thumbnail`, `count`, `order_number`, `flag`, `meta_keywords`, `meta_description`, `options`, `created`, `modified`, `site_id`)
VALUES
	(1,0,NULL,'ccc','010',NULL,NULL,NULL,'category',NULL,1,1,'/attachment/20220617/58f750d10dcd4f40888cb56fb118a844.png','/attachment/20220617/e88f8593f64947d0bfec744bb3d749b7.png',0,NULL,NULL,NULL,NULL,NULL,'2022-06-15 11:57:00','2022-07-14 10:33:29',NULL);

/*!40000 ALTER TABLE `product_category` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 product_category_mapping
# ------------------------------------------------------------

DROP TABLE IF EXISTS `product_category_mapping`;

CREATE TABLE `product_category_mapping` (
  `product_id` int(11) unsigned NOT NULL COMMENT '商品ID',
  `category_id` int(11) unsigned NOT NULL COMMENT '分类ID',
  PRIMARY KEY (`product_id`,`category_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='商品和分类的多对多关系表';



# 转储表 product_comment
# ------------------------------------------------------------

DROP TABLE IF EXISTS `product_comment`;

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
  `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `product_id` (`product_id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `status` (`status`) USING BTREE,
  KEY `pid` (`pid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='商品评论表';



# 转储表 product_image
# ------------------------------------------------------------

DROP TABLE IF EXISTS `product_image`;

CREATE TABLE `product_image` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `product_id` int(11) unsigned NOT NULL,
  `src` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `order_number` int(11) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `productid` (`product_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=COMPACT COMMENT='产品图片表';

LOCK TABLES `product_image` WRITE;
/*!40000 ALTER TABLE `product_image` DISABLE KEYS */;

INSERT INTO `product_image` (`id`, `product_id`, `src`, `order_number`, `created`)
VALUES
	(1,1,'/attachment/20220629/2bbe54d79cab4cd89cd548dd995896cd.jpg',0,'2022-07-01 09:10:07');

/*!40000 ALTER TABLE `product_image` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '角色名称',
  `description` text COMMENT '角色的描述',
  `flag` varchar(64) DEFAULT '' COMMENT '角色标识，全局唯一，jpsa 为超级管理员',
  `created` datetime NOT NULL,
  `modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='角色表';

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;

INSERT INTO `role` (`id`, `name`, `description`, `flag`, `created`, `modified`)
VALUES
	(1,'默认角色','这个是系统自动创建的默认角色','jpsa','2022-04-13 11:26:01','2022-06-09 11:26:49'),
	(2,'test',NULL,NULL,'2022-06-20 15:37:43',NULL);

/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 role_permission_mapping
# ------------------------------------------------------------

DROP TABLE IF EXISTS `role_permission_mapping`;

CREATE TABLE `role_permission_mapping` (
  `role_id` int(11) unsigned NOT NULL COMMENT '角色ID',
  `permission_id` int(11) unsigned NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`role_id`,`permission_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='角色和权限的多对多映射表';



# 转储表 single_page
# ------------------------------------------------------------

DROP TABLE IF EXISTS `single_page`;

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
  `flag` varchar(32) DEFAULT NULL COMMENT '标识',
  `status` varchar(32) NOT NULL DEFAULT '0' COMMENT '状态',
  `comment_status` tinyint(1) DEFAULT '1' COMMENT '评论状态，默认允许评论',
  `comment_count` int(11) unsigned DEFAULT '0' COMMENT '评论总数',
  `comment_time` datetime DEFAULT NULL COMMENT '最后评论时间',
  `view_count` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '访问量',
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL COMMENT '最后更新日期',
  `meta_keywords` varchar(256) DEFAULT NULL COMMENT 'SEO关键字',
  `meta_description` varchar(256) DEFAULT NULL COMMENT 'SEO描述信息',
  `remarks` text COMMENT '备注信息',
  `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `slug` (`slug`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='单页表';

LOCK TABLES `single_page` WRITE;
/*!40000 ALTER TABLE `single_page` DISABLE KEYS */;

INSERT INTO `single_page` (`id`, `category_id`, `slug`, `title`, `content`, `edit_mode`, `link_to`, `summary`, `thumbnail`, `ornament`, `style`, `flag`, `status`, `comment_status`, `comment_count`, `comment_time`, `view_count`, `created`, `modified`, `meta_keywords`, `meta_description`, `remarks`, `site_id`)
VALUES
	(1,3,NULL,'ces页面02','<p>这是页面001</p>','html',NULL,NULL,'/attachment/20220609/d87d2e10c0cf4c88b4714bb29b56106c.jpg',NULL,NULL,'我是展示的标识','normal',1,0,NULL,24,'2022-06-09 11:49:42','2022-06-10 17:49:50','我是页面SEO关键字','我是SEO描述',NULL,NULL),
	(2,1,'cess','页面11','<p>0120101201012</p>','html',NULL,NULL,'/attachment/20220613/196ddeeb59274964bbfbd4141181d3ed.jpg',NULL,NULL,NULL,'normal',1,0,NULL,13,'2022-06-09 17:20:11','2022-06-13 11:37:39',NULL,NULL,NULL,NULL),
	(3,1,NULL,'测试页','<p>VScccc</p>','html',NULL,NULL,'/attachment/20220613/51810b8b82f54b9a93032263744f4f57.jpg',NULL,NULL,NULL,'normal',1,0,NULL,3,'2022-06-13 10:08:47','2022-06-13 11:14:44',NULL,NULL,NULL,NULL),
	(4,3,NULL,'yemain 0000','<p>dadadadadadavdfv</p>','html',NULL,NULL,NULL,NULL,NULL,NULL,'normal',1,0,NULL,5,'2022-06-13 10:34:31','2022-06-13 11:22:30',NULL,NULL,NULL,NULL),
	(5,4,NULL,'测试水水水水','<p>打法测试v阿强 000</p>','html',NULL,NULL,NULL,NULL,NULL,NULL,'normal',1,0,NULL,0,'2022-06-13 11:40:18','2022-06-13 11:42:56',NULL,NULL,NULL,NULL),
	(6,1,NULL,'0101','<p>\n <video style=\"max-width:100%;\" controls=\"controls\" src=\"/attachment/20220617/ba930ecaabc7420d80f1db9341fce7b4.mp4\"></video>01012</p>','html',NULL,NULL,'/attachment/20220613/977e1e781bde4ef78525d17a6c910fe2.png',NULL,NULL,NULL,'normal',1,0,NULL,2,'2022-06-13 11:48:22','2022-06-17 10:12:08',NULL,NULL,NULL,NULL),
	(7,5,'ces','测试哦','<p><img src=\"/attachment/20220623/cef50fb521b24270939caf0b6492441a.png\"></p>','html',NULL,NULL,'/attachment/20220623/9f54019415ec4d52a147a4c9dd44f651.png','/attachment/20220623/93f2daaaed994908bf352f2ef5d50dac.png','leftbar',NULL,'normal',0,0,NULL,2,'2022-06-23 09:48:53','2022-06-23 10:43:14',NULL,'大大大',NULL,NULL),
	(8,3,NULL,'侧式','<p>哒哒哒</p>','html',NULL,NULL,'/attachment/20220623/9f54019415ec4d52a147a4c9dd44f651.png','/attachment/20220623/7a76450f75a9416ebfc8dfa67bb408f8.jpg',NULL,NULL,'normal',0,0,NULL,0,'2022-06-23 10:44:08','2022-06-23 10:44:22',NULL,NULL,NULL,NULL),
	(9,NULL,NULL,'2010','<p>101012</p>','html',NULL,NULL,NULL,NULL,NULL,NULL,'normal',0,0,NULL,0,'2022-06-23 10:45:57','2022-06-23 11:39:46',NULL,NULL,NULL,NULL),
	(10,5,NULL,'大大1111aaa','<p>\n <video class=\"jpress-video\" style=\"with:100%;\" preload=\"auto\" controls=\"true\" id=\"qcloud_video_player\" poster=\"/attachment/20220705/7b14c2c52c3a4f58a2c7c6175cec2e7e.jpg\"></video>大大从VS</p>','html',NULL,NULL,NULL,NULL,NULL,NULL,'normal',0,0,NULL,6,'2022-06-23 11:40:18','2022-07-05 18:04:58',NULL,NULL,NULL,NULL),
	(12,NULL,'ces_1','dad','<p>aliyun</p>\n<p>&nbsp;</p>\n<p>\n <video class=\"jpress-video\" style=\"height:240px;width:425px;\" data-vid=\"bd77e9572bfa4ab896d3c911e9521bd9\" data-play-auth=\"eyJTZWN1cml0eVRva2VuIjoiQ0FJU2h3TjFxNkZ0NUIyeWZTaklyNWJGQU83ZGxLbFhoWStTTVd2VjBHTTJPODF1bjQzOGl6ejJJSDlJZEhWb0FPOGZ2dlUwbTJ0WTdQc1psck1xRU1FWkd4R1pNcElzczhzTHJGNzlKcExGc3QySjZyOEpqc1VWb3ZsTS9rYXBzdlhKYXNEVkVmbDJFNVhFTWlJUi8wMGU2TC8rY2lyWXBUWEhWYlNDbFo5Z2FQa09Rd0M4ZGtBb0xkeEtKd3hrMnQxNFVtWFdPYVNDUHdMU2htUEJMVXhtdldnR2wyUnp1NHV5M3ZPZDVoZlpwMXI4eE80YXhlTDBQb1AyVjgxbExacGxlc3FwM0k0U2M3YmFnaFpVNGdscjhxbHg3c3BCNVN5Vmt0eVdHVWhKL3phTElvaXQ3TnBqZmlCMGVvUUFQb3BGcC9YNmp2QWF3UExVbTliWXhncGhCOFIrWGo3RFpZYXV4N0d6ZW9XVE84MCthS3p3TmxuVXo5bUxMZU9WaVE0L1ptOEJQdzQ0RUxoSWFGMElVRTF5R21DQ2QvWDRvZ3VSUDF6N0VwTG9pdjltamNCSHFIeno1c2VQS2xTMVJMR1U3RDBWSUpkVWJUbHphRUpHZ1RTNExmWldJbGNUS0FNOVd1MlBNYXgzYlFGRHI1M3ZzVGJiWHpaYjBtcHR1UG56ZDE0Sk9CS2cxMUtVR29BQmZTNkhPT3QvcjBodU1UdEo4UE1hU2t6VitQWDF5RmZLRHl2Q09BQlFKY3pyeS9PTUc3T0lKK3N3Z0Q2UzZoZVBDMUdONnBBMzdrSGlkUTBxbG5BUmRqcThCMzJWaGlOR0JlTDROVzl1WHZWNUI3TWpZankxZlNNTXBDcGpRZ1lhSW5Zd1BnbXFGQjFaSTltU0JxaEtnZjZicmhBNVpPVDRCS1JoSHZCVGQ0az0iLCJBdXRoSW5mbyI6IntcIkNJXCI6XCIyOXpsWlYrYnV5WlFaZFI0Zi9RWWZ4VU9MTGFrOG9WdlViRXlMcHEwcDhXb2FmczMwRGErVjZpdVg5R3hMdStvXCIsXCJDYWxsZXJcIjpcIkxQbzJEcnlWRFp4TCtFQjhheUQvakhmRkFIdDEvUGJlQzQ0RW13TnBMQU09XCIsXCJFeHBpcmVUaW1lXCI6XCIyMDIyLTA3LTA3VDAzOjU0OjA1WlwiLFwiTWVkaWFJZFwiOlwiYmQ3N2U5NTcyYmZhNGFiODk2ZDNjOTExZTk1MjFiZDlcIixcIlNpZ25hdHVyZVwiOlwiaHNGR0o2d0xHcVYzY1dSaHFORkloYzJzRXhZPVwifSIsIlZpZGVvTWV0YSI6eyJTdGF0dXMiOiJOb3JtYWwiLCJWaWRlb0lkIjoiYmQ3N2U5NTcyYmZhNGFiODk2ZDNjOTExZTk1MjFiZDkiLCJUaXRsZSI6IumYv+mHjOS6keinhumikSIsIkNvdmVyVVJMIjoiaHR0cDovL291dGluLWVhNmNjYTUzZjJjNTExZWNiOGE5MDAxNjNlMWEzYjRhLm9zcy1jbi1zaGFuZ2hhaS5hbGl5dW5jcy5jb20vYmQ3N2U5NTcyYmZhNGFiODk2ZDNjOTExZTk1MjFiZDkvc25hcHNob3RzL2JmYzBkM2JjNjkyODQ2NTVhMTRmYzIzMzU5ZWM0MjExLTAwMDAxLmpwZz9FeHBpcmVzPTE2NTcxNjk1NDUmT1NTQWNjZXNzS2V5SWQ9TFRBSXJrd2IyMUt5R2pKbCZTaWduYXR1cmU9eGw4TzNscThHS2FSdXNVaTMxeTZWJTJCeGE2eXMlM0QiLCJEdXJhdGlvbiI6Ny42Njd9LCJBY2Nlc3NLZXlJZCI6IlNUUy5OVXBLVGl5dnYyTXkzTWQ0Y2M3QkJwSFdpIiwiQWNjZXNzS2V5U2VjcmV0IjoiR01oZGtCRnlhdml5Z2d3WmlLeUxkekR2UTZUeE56ajVUZ3hyaTdtdDExcEwiLCJSZWdpb24iOiJjbi1zaGFuZ2hhaSIsIkN1c3RvbWVySWQiOjEzNjQ2NTU3MTQ1NzQ1NTR9\" data-cloud=\"1\" poster=\"/attachment/20220707/3eb1ef75fca44c01965717e96cd8695c.png\" id=\"video_player41051\"></video></p>\n<p>&nbsp;</p>\n<p>\n <video class=\"jpress-video\" style=\"height:240px;width:425px;\" data-vid=\"a58ebb82a1a748cfa368c172f8b37619\" data-play-auth=\"eyJTZWN1cml0eVRva2VuIjoiQ0FJU2h3TjFxNkZ0NUIyeWZTaklyNURBS083ODI2ZHR4SkcvVkhQbnJuaGdlc29VbkwrYXJEejJJSDlJZEhWb0FPOGZ2dlUwbTJ0WTdQc1psck1xRU1FWkd4R1pNcElzczhzTHJGNzlKcExGc3QySjZyOEpqc1VnMTdoRS9rYXBzdlhKYXNEVkVmbDJFNVhFTWlJUi8wMGU2TC8rY2lyWXBUWEhWYlNDbFo5Z2FQa09Rd0M4ZGtBb0xkeEtKd3hrMnQxNFVtWFdPYVNDUHdMU2htUEJMVXhtdldnR2wyUnp1NHV5M3ZPZDVoZlpwMXI4eE80YXhlTDBQb1AyVjgxbExacGxlc3FwM0k0U2M3YmFnaFpVNGdscjhxbHg3c3BCNVN5Vmt0eVdHVWhKL3phTElvaXQ3TnBqZmlCMGVvUUFQb3BGcC9YNmp2QWF3UExVbTliWXhncGhCOFIrWGo3RFpZYXV4N0d6ZW9XVE84MCthS3p3TmxuVXo5bUxMZU9WaVE0L1ptOEJQdzQ0RUxoSWFGMElVRTF5R21DQ2QvWDRvZ3VSUDF6N0VwTG9pdjltamNCSHFIeno1c2VQS2xTMVJMR1U3RDBWSUpkVWJUbHphRUpHZ1RTNExmWldJbGNUS0FNOVd1MlBNYXgzYlFGRHI1M3ZzVGJiWHpaYjBtcHR1UG56ZDE0Sk9CS2cxMUtVR29BQmJ4eVgva0JBb3ZZV0lIMkhFR0ZkOVRtYlE4dC9xMUhHUnRvdU84WGNNcGFSZEJGbzZ0ZUg0NkhSMUNrMmhiNjJhTkxkM1V3OFBHY0lkSUNoVlBiem5YNXF2NUVwd2tKalprbUxOWEpZMXFjZ2ZESDhzcDRYRXpscGJwNEVGTHZkeVduQWtlL3JaSEtwVjZtZUprcEhCZ1I4bW1TVndLb1NIV3BNMzVjdmFkUT0iLCJBdXRoSW5mbyI6IntcIkNJXCI6XCJFSFhuaWU1Q1JoeGRMMXF1NHRiSUpyUFUzSTdvbFlxNEhScGM2WmVYdnc4cXBFNkhIZnFmRXdlSWNVMllYTU9KXCIsXCJDYWxsZXJcIjpcIkFiUFAvWmZYY2JyenlzRFBkdVVKc09CbVEyNXVjS09mcnhQdnRQU2FBaFk9XCIsXCJFeHBpcmVUaW1lXCI6XCIyMDIyLTA3LTA3VDA4OjIzOjEwWlwiLFwiTWVkaWFJZFwiOlwiYTU4ZWJiODJhMWE3NDhjZmEzNjhjMTcyZjhiMzc2MTlcIixcIlNpZ25hdHVyZVwiOlwiQmF5RGpBYmoza25PL2VpU3Qyd0U2cXdMaFp3PVwifSIsIlZpZGVvTWV0YSI6eyJTdGF0dXMiOiJOb3JtYWwiLCJWaWRlb0lkIjoiYTU4ZWJiODJhMWE3NDhjZmEzNjhjMTcyZjhiMzc2MTkiLCJUaXRsZSI6IumYv+mHjOS6kS0yIiwiQ292ZXJVUkwiOiJodHRwOi8vb3V0aW4tZWE2Y2NhNTNmMmM1MTFlY2I4YTkwMDE2M2UxYTNiNGEub3NzLWNuLXNoYW5naGFpLmFsaXl1bmNzLmNvbS9hNThlYmI4MmExYTc0OGNmYTM2OGMxNzJmOGIzNzYxOS9zbmFwc2hvdHMvNmIxY2U1NDVlYzZmNDUxYmI2ZTY2MDgzNGU0NzJmMWItMDAwMDEuanBnP0V4cGlyZXM9MTY1NzE4NTY5MCZPU1NBY2Nlc3NLZXlJZD1MVEFJcmt3YjIxS3lHakpsJlNpZ25hdHVyZT1uZVd4Q252QU1sS2E5RHlYQ1ElMkYwOWV1SGRYdyUzRCIsIkR1cmF0aW9uIjo3LjY2N30sIkFjY2Vzc0tleUlkIjoiU1RTLk5TdWNUSDZ4THNTVFZVVkp4NXZFOHN6MU4iLCJBY2Nlc3NLZXlTZWNyZXQiOiJEZHRFbXFZS1pNeVQzeWtnRWRrcERjeDFzb3I4anh5Znlvc1pnUlJSZmltZiIsIlJlZ2lvbiI6ImNuLXNoYW5naGFpIiwiQ3VzdG9tZXJJZCI6MTM2NDY1NTcxNDU3NDU1NH0=\" data-cloud=\"1\" poster=\"/attachment/20220707/e7799ed053ec470399f9ec20ec044a2a.png\" id=\"video_player04181\"></video>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>qcloud &nbsp;&nbsp;</p>\n<p>&nbsp;</p>\n<p>\n <video class=\"jpress-video\" data-vid=\"387702302843641596\" data-app-id=\"1312635314\" data-cloud=\"2\" poster=\"/attachment/20220707/cbd0d563e68448c39bac78ad70a77e75.png\" id=\"qcloud_video_player69678\"></video></p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>\n <video class=\"jpress-video\" data-vid=\"387702302810414136\" data-app-id=\"1312635314\" data-cloud=\"2\" poster=\"/attachment/20220707/7989e8a4b4044fdea638de1b5478d908.png\" id=\"qcloud_video_player50343\"></video></p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>','html',NULL,NULL,NULL,NULL,NULL,NULL,'normal',1,0,NULL,324,'2022-07-06 09:32:43','2022-07-07 16:24:17',NULL,NULL,NULL,NULL),
	(14,NULL,NULL,'adadaaaa','<p>&nbsp;</p>\n<p>腾讯云视频点播</p>\n<p>&nbsp;</p>\n<p>\n <video class=\"jpress-video\" style=\"height:240px;width:425px;\" id=\"qcloud_video_player29501\" poster=\"/attachment/20220707/ec3e44685c64477497fc3a495f1ced83.png\" data-cloud=\"2\" data-app-id=\"1312635314\" data-vid=\"387702302810414136\"></video></p>\n<p>&nbsp;</p>\n<p>阿里云视频点播</p>\n<p>\n <video class=\"jpress-video\" style=\"height:240px;width:425px;\" id=\"video_player15037\" poster=\"/attachment/20220707/4e6bbecf5ade4c86915439176ff94a73.png\" data-cloud=\"1\" data-play-auth=\"eyJTZWN1cml0eVRva2VuIjoiQ0FJU2h3TjFxNkZ0NUIyeWZTaklyNURZQWM3bWdMUmlqcVBaWnhEYWhrUXdTTjBkaG82Y3BEejJJSDlJZEhWb0FPOGZ2dlUwbTJ0WTdQc1psck1xRU1FWkd4R1pNcElzczhzTHJGNzlKcExGc3QySjZyOEpqc1VTMy85Si9rYXBzdlhKYXNEVkVmbDJFNVhFTWlJUi8wMGU2TC8rY2lyWXBUWEhWYlNDbFo5Z2FQa09Rd0M4ZGtBb0xkeEtKd3hrMnQxNFVtWFdPYVNDUHdMU2htUEJMVXhtdldnR2wyUnp1NHV5M3ZPZDVoZlpwMXI4eE80YXhlTDBQb1AyVjgxbExacGxlc3FwM0k0U2M3YmFnaFpVNGdscjhxbHg3c3BCNVN5Vmt0eVdHVWhKL3phTElvaXQ3TnBqZmlCMGVvUUFQb3BGcC9YNmp2QWF3UExVbTliWXhncGhCOFIrWGo3RFpZYXV4N0d6ZW9XVE84MCthS3p3TmxuVXo5bUxMZU9WaVE0L1ptOEJQdzQ0RUxoSWFGMElVRTF5R21DQ2QvWDRvZ3VSUDF6N0VwTG9pdjltamNCSHFIeno1c2VQS2xTMVJMR1U3RDBWSUpkVWJUbHphRUpHZ1RTNExmWldJbGNUS0FNOVd1MlBNYXgzYlFGRHI1M3ZzVGJiWHpaYjBtcHR1UG56ZDE0Sk9CS2cxMUtVR29BQnJtVkRER2N0Rk1ZR2gxWWtRUlVETUJjODdLRUx0NU8yNkxoNXovVS9kVWQrV1YxcUFmWnd5bjJ5NFBnMWtUakJXSUlscXR2UkpodDI1TlFObFA1OFpRWXcxS21icGpuZ21xb3ZxajM3ekxWZUQ3RnN1NVF5ME82ZC9lUTFyWlpyR0dVY1poNFozUVNYTmZsS2xFUmdQMENCUjQydDhEaDcxaVhmT2tsdkp6WT0iLCJBdXRoSW5mbyI6IntcIkNJXCI6XCIyOXpsWlYrYnV5WlFaZFI0Zi9RWWZ4VU9MTGFrOG9WdlViRXlMcHEwcDhXb2FmczMwRGErVjZpdVg5R3hMdStvXCIsXCJDYWxsZXJcIjpcIkxQbzJEcnlWRFp4TCtFQjhheUQvakhmRkFIdDEvUGJlQzQ0RW13TnBMQU09XCIsXCJFeHBpcmVUaW1lXCI6XCIyMDIyLTA3LTA3VDAxOjA2OjM3WlwiLFwiTWVkaWFJZFwiOlwiYmQ3N2U5NTcyYmZhNGFiODk2ZDNjOTExZTk1MjFiZDlcIixcIlNpZ25hdHVyZVwiOlwiWkFZZjA5cTY2OWtPdHpNUGt6N0N2L21vejI4PVwifSIsIlZpZGVvTWV0YSI6eyJTdGF0dXMiOiJOb3JtYWwiLCJWaWRlb0lkIjoiYmQ3N2U5NTcyYmZhNGFiODk2ZDNjOTExZTk1MjFiZDkiLCJUaXRsZSI6IumYv+mHjOS6keinhumikSIsIkNvdmVyVVJMIjoiaHR0cDovL291dGluLWVhNmNjYTUzZjJjNTExZWNiOGE5MDAxNjNlMWEzYjRhLm9zcy1jbi1zaGFuZ2hhaS5hbGl5dW5jcy5jb20vYmQ3N2U5NTcyYmZhNGFiODk2ZDNjOTExZTk1MjFiZDkvc25hcHNob3RzL2JmYzBkM2JjNjkyODQ2NTVhMTRmYzIzMzU5ZWM0MjExLTAwMDAxLmpwZz9FeHBpcmVzPTE2NTcxNTk0OTcmT1NTQWNjZXNzS2V5SWQ9TFRBSXJrd2IyMUt5R2pKbCZTaWduYXR1cmU9bWpQQTVDMlR0WW1CZnZRTWg0cGJ1JTJGVm1JQUElM0QiLCJEdXJhdGlvbiI6Ny42Njd9LCJBY2Nlc3NLZXlJZCI6IlNUUy5OU21KdFJta0M5YTJlNmtiRGVEUjFpSzdGIiwiQWNjZXNzS2V5U2VjcmV0IjoiMlVFQ05DdGhKN0R2WUY2V0FEVzVITXFaWGZGQ0pybnhOcmJOMVJjR1BGYkEiLCJSZWdpb24iOiJjbi1zaGFuZ2hhaSIsIkN1c3RvbWVySWQiOjEzNjQ2NTU3MTQ1NzQ1NTR9\" data-vid=\"bd77e9572bfa4ab896d3c911e9521bd9\"></video></p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>','html',NULL,NULL,NULL,NULL,NULL,NULL,'normal',1,0,NULL,77,'2022-07-06 14:27:13','2022-07-07 09:05:04',NULL,NULL,NULL,NULL),
	(17,1,NULL,'主站点的页面','<p>xxxxxx</p>','html',NULL,NULL,NULL,NULL,NULL,NULL,'normal',1,0,NULL,1,'2022-07-08 17:14:35','2022-07-08 17:15:42',NULL,NULL,NULL,0),
	(18,NULL,NULL,'测试站点的页面','<p>测试站点的页面测试站点的页面测试站点的页面测试站点的页面</p>','html',NULL,NULL,NULL,NULL,NULL,NULL,'normal',1,0,NULL,0,'2022-07-08 17:15:26','2022-07-08 17:15:26',NULL,NULL,NULL,1),
	(19,NULL,NULL,'aaaa','<p>aa</p>\n<p>aaaaa</p>\n<p>&nbsp;</p>\n<p>阿里云</p>\n<p>\n <video class=\"jpress-video\" style=\"height:240px;width:425px;\" data-vid=\"c711d4fcc6714c64b4dda1afe1f3f457\" data-play-auth=\"eyJTZWN1cml0eVRva2VuIjoiQ0FJU2h3TjFxNkZ0NUIyeWZTaklyNWJ0UGR6VG9aeEo3WU9oWVhUSmltWURidUVaaXFERGxEejJJSDlJZEhWb0FPOGZ2dlUwbTJ0WTdQc1psck1xRU1FWkd4R1pNcElzczhzTHJGNzlKcExGc3QySjZyOEpqc1YwenBCMS9FYXBzdlhKYXNEVkVmbDJFNVhFTWlJUi8wMGU2TC8rY2lyWXBUWEhWYlNDbFo5Z2FQa09Rd0M4ZGtBb0xkeEtKd3hrMnQxNFVtWFdPYVNDUHdMU2htUEJMVXhtdldnR2wyUnp1NHV5M3ZPZDVoZlpwMXI4eE80YXhlTDBQb1AyVjgxbExacGxlc3FwM0k0U2M3YmFnaFpVNGdscjhxbHg3c3BCNVN5Vmt0eVdHVWhKL3phTElvaXQ3TnBqZmlCMGVvUUFQb3BGcC9YNmp2QWF3UExVbTliWXhncGhCOFIrWGo3RFpZYXV4N0d6ZW9XVE84MCthS3p3TmxuVXo5bUxMZU9WaVE0L1ptOEJQdzQ0RUxoSWFGMElVRTF5R21DQ2QvWDRvZ3VSUDF6N0VwTG9pdjltamNCSHFIeno1c2VQS2xTMVJMR1U3RDBWSUpkVWJUbHphRUpHZ1RTNExmWldJbGNUS0FNOVd1MlBNYXgzYlFGRHI1M3ZzVGJiWHpaYjBtcHR1UG56ZDE0Sk9CS2cxMUtVR29BQlBzTlhheEVIM3crL0lZTWZYakZ6T3VCOFovaFBmVUJXdEFIeUZtRzRPaVIyM3g3akd4TFZsZ1huNXdGZDdPKzE4OEE0Vjl0L01XaFFIcktQWTY2bjBjK2xqVkRhMnRQQzdsNmw3cXJNWmUwNzdZdHNteDN1RG4wOUhtQ1d5NHpJY2FBb2hmQUdHaERHdzVvR2lNMDNtL3UxRWwvdEhrTDF6eFQyZTlLdVdTST0iLCJBdXRoSW5mbyI6IntcIkNJXCI6XCJEMTY2N21icEFCWlM4WFFiMm42a3d0dTkzODJORlpBaldlcml4d0FSc0FudGt5T0VLZjZxR0k0Zmo1VWkrdHVWXCIsXCJDYWxsZXJcIjpcIjNaVFFKZFVIQ0xTTUprUFdtcWQ1K2R6RndpUmxmdldDRUVHd0ZoRy9zVms9XCIsXCJFeHBpcmVUaW1lXCI6XCIyMDIyLTA3LTEyVDA4OjQwOjAyWlwiLFwiTWVkaWFJZFwiOlwiYzcxMWQ0ZmNjNjcxNGM2NGI0ZGRhMWFmZTFmM2Y0NTdcIixcIlNpZ25hdHVyZVwiOlwidFNxbU03SHNwTnFsUmVZbGVZTGM5VWtKODJBPVwifSIsIlZpZGVvTWV0YSI6eyJTdGF0dXMiOiJOb3JtYWwiLCJWaWRlb0lkIjoiYzcxMWQ0ZmNjNjcxNGM2NGI0ZGRhMWFmZTFmM2Y0NTciLCJUaXRsZSI6IumYv+mHjOS6keinhumikSIsIkNvdmVyVVJMIjoiaHR0cDovL291dGluLWVhNmNjYTUzZjJjNTExZWNiOGE5MDAxNjNlMWEzYjRhLm9zcy1jbi1zaGFuZ2hhaS5hbGl5dW5jcy5jb20vYzcxMWQ0ZmNjNjcxNGM2NGI0ZGRhMWFmZTFmM2Y0NTcvc25hcHNob3RzLzBkOTBiOWM2NjJjMzQ3MmQ4NzE2NjA1ZmVmNzUyYzlkLTAwMDAxLmpwZz9FeHBpcmVzPTE2NTc2MTg3MDImT1NTQWNjZXNzS2V5SWQ9TFRBSXJrd2IyMUt5R2pKbCZTaWduYXR1cmU9dCUyQlF1aW45cE5YeSUyQkhlYmdPJTJGUUxOSkFEekNFJTNEIiwiRHVyYXRpb24iOjcuNjY3fSwiQWNjZXNzS2V5SWQiOiJTVFMuTlVYdmZnTENoWkFKY1J4bmZWYm41ZWVodiIsIkFjY2Vzc0tleVNlY3JldCI6IjdGbU1DNExnWURGOWhacTVZU2Q4NTJiaEgzQzh4WFJrVWlZQlRFcUdadzRkIiwiUmVnaW9uIjoiY24tc2hhbmdoYWkiLCJDdXN0b21lcklkIjoxMzY0NjU1NzE0NTc0NTU0fQ==\" data-cloud=\"1\" poster=\"/attachment/20220712/b1cdbdef0968417da31b68cdf196f5a9.png\" id=\"video_player60421\"></video></p>\n<p>&nbsp;</p>\n<p>腾讯云</p>\n<p>\n <video class=\"jpress-video\" id=\"qcloud_video_player08681\" poster=\"/attachment/20220712/dc5b0af6e5b041458ab95aafe9c2dd81.png\" data-cloud=\"2\" data-app-id=\"1312635314\" data-vid=\"387702303107862376\"></video></p>\n<p>&nbsp;</p>\n<p>\n <video class=\"jpress-video\" id=\"qcloud_video_player25056\" poster=\"/attachment/20220712/dc5b0af6e5b041458ab95aafe9c2dd81.png\" data-cloud=\"2\" data-app-id=\"1312635314\" data-vid=\"387702303107862376\"></video></p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>\n<p>&nbsp;</p>','html',NULL,NULL,NULL,NULL,NULL,NULL,'normal',1,0,NULL,12,'2022-07-12 11:05:58','2022-07-12 16:48:43',NULL,NULL,NULL,0);

/*!40000 ALTER TABLE `single_page` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 single_page_category
# ------------------------------------------------------------

DROP TABLE IF EXISTS `single_page_category`;

CREATE TABLE `single_page_category` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(512) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COMMENT '描述',
  `style` varchar(32) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '模板样式',
  `icon` varchar(128) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '图标',
  `ornament` varchar(512) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '装饰图',
  `count` int(11) unsigned DEFAULT '0' COMMENT '该分类的内容数量',
  `order_number` int(11) DEFAULT '0' COMMENT '排序编码',
  `flag` varchar(256) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '标识',
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL COMMENT '修改日期',
  `site_id` int(11) unsigned DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=COMPACT;

LOCK TABLES `single_page_category` WRITE;
/*!40000 ALTER TABLE `single_page_category` DISABLE KEYS */;

INSERT INTO `single_page_category` (`id`, `title`, `content`, `style`, `icon`, `ornament`, `count`, `order_number`, `flag`, `created`, `modified`, `site_id`)
VALUES
	(1,'页分类01','212e1d',NULL,'图标',NULL,4,1,'大大大大大大大力出奇迹','2022-06-09 14:22:04','2022-07-08 17:15:42',NULL),
	(3,'页分类02','这是内容这是内容这是内容这是内容',NULL,'这是图标','/attachment/20220623/9f54019415ec4d52a147a4c9dd44f651.png',3,2,'这是标识','2022-06-09 15:26:21','2022-06-23 10:44:22',NULL),
	(4,'页分类03',NULL,NULL,NULL,NULL,1,3,NULL,'2022-06-13 10:34:16','2022-06-13 13:37:16',NULL),
	(5,'页面分类4',NULL,NULL,NULL,'/attachment/20220623/cef50fb521b24270939caf0b6492441a.png',2,4,NULL,'2022-06-23 09:48:03','2022-07-05 18:04:58',NULL);

/*!40000 ALTER TABLE `single_page_category` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 single_page_comment
# ------------------------------------------------------------

DROP TABLE IF EXISTS `single_page_comment`;

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
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='页面评论表';



# 转储表 site_info
# ------------------------------------------------------------

DROP TABLE IF EXISTS `site_info`;

CREATE TABLE `site_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `site_id` int(11) unsigned DEFAULT NULL COMMENT '自定义站点ID',
  `name` varchar(64) DEFAULT NULL COMMENT '站点名称',
  `bind_domain` varchar(64) DEFAULT NULL COMMENT '绑定域名',
  `bind_path` varchar(64) DEFAULT NULL COMMENT '绑定二级目录',
  `bind_lang` varchar(11) DEFAULT NULL COMMENT '绑定语言',
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `site_info` WRITE;
/*!40000 ALTER TABLE `site_info` DISABLE KEYS */;

INSERT INTO `site_info` (`id`, `site_id`, `name`, `bind_domain`, `bind_path`, `bind_lang`, `created`, `modified`)
VALUES
	(23,6,'5','5','5','5','2022-07-14 16:33:05','2022-07-14 16:37:48'),
	(24,6,'6','6','6','6','2022-07-14 16:35:27','2022-07-15 09:29:08'),
	(25,7,'7','7','7','7','2022-07-15 09:30:14',NULL),
	(26,2,'xxx','www.baidu.com','/aaaaa','zh_HK','2022-07-15 09:45:33','2022-07-15 14:09:07');

/*!40000 ALTER TABLE `site_info` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 site_lang_mapping
# ------------------------------------------------------------

DROP TABLE IF EXISTS `site_lang_mapping`;

CREATE TABLE `site_lang_mapping` (
  `site_id` int(11) unsigned NOT NULL,
  `lang` varchar(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `site_lang_mapping` WRITE;
/*!40000 ALTER TABLE `site_lang_mapping` DISABLE KEYS */;

INSERT INTO `site_lang_mapping` (`site_id`, `lang`)
VALUES
	(26,'zh_CN'),
	(26,'zh_HK'),
	(26,'zh_TW');

/*!40000 ALTER TABLE `site_lang_mapping` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 site_role_mapping
# ------------------------------------------------------------

DROP TABLE IF EXISTS `site_role_mapping`;

CREATE TABLE `site_role_mapping` (
  `site_id` int(11) unsigned NOT NULL COMMENT '站点ID',
  `role_id` int(11) DEFAULT NULL COMMENT '站点绑定的角色ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站点绑定的角色，必须是该角色用户才能操作该站点';

LOCK TABLES `site_role_mapping` WRITE;
/*!40000 ALTER TABLE `site_role_mapping` DISABLE KEYS */;

INSERT INTO `site_role_mapping` (`site_id`, `role_id`)
VALUES
	(4,1),
	(5,2),
	(7,1),
	(7,2),
	(13,1),
	(13,2),
	(1,1),
	(23,1),
	(24,1),
	(24,2),
	(22,1),
	(20,2),
	(26,1);

/*!40000 ALTER TABLE `site_role_mapping` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 template_block_option
# ------------------------------------------------------------

DROP TABLE IF EXISTS `template_block_option`;

CREATE TABLE `template_block_option` (
  `template_id` varchar(64) NOT NULL,
  `site` int(11) DEFAULT NULL COMMENT '站点',
  `options` mediumtext COMMENT '配置内容',
  PRIMARY KEY (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模块配置';

LOCK TABLES `template_block_option` WRITE;
/*!40000 ALTER TABLE `template_block_option` DISABLE KEYS */;

INSERT INTO `template_block_option` (`template_id`, `site`, `options`)
VALUES
	('cn.jeanstudio.calmlog',NULL,'[{\"children\":{\"0\":[{\"parentDataId\":\"右边栏\",\"name\":\"search_4\",\"index\":0,\"id\":\"2940117c11\",\"tag\":\"search\",\"label\":\"search\",\"parentDataIndex\":0},{\"parentDataId\":\"右边栏\",\"name\":\"wechat_2\",\"index\":1,\"id\":\"9516dcd8fc\",\"tag\":\"wechat\",\"label\":\" 微信\",\"parentDataIndex\":0,\"微信图片\":\"/attachment/20220717/bceb825c9d224f8fb1316f4bab29d13d.jpeg\"},{\"parentDataId\":\"右边栏\",\"排序方式\":\"created\",\"name\":\"system_articles_1\",\"文章数量\":\"10\",\"index\":2,\"id\":\"a95c66feee\",\"tag\":\"system_articles\",\"label\":\"文章列表\",\"标题\":\"最新文章\",\"parentDataIndex\":0}]},\"id\":\"右边栏\",\"tag\":\"container\",\"templateFile\":\"_rightbar.html\"},{\"children\":{\"0\":[{\"parentDataId\":\"右边栏2\",\"name\":\"tags_1\",\"index\":0,\"id\":\"a0ab6661fe\",\"tag\":\"tags\",\"label\":\" 文章标签\",\"parentDataIndex\":0}]},\"id\":\"右边栏2\",\"tag\":\"container\",\"templateFile\":\"_rightbar.html\"}]');

/*!40000 ALTER TABLE `template_block_option` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user`;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='用户信息表，保存用户信息。';

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;

INSERT INTO `user` (`id`, `username`, `nickname`, `realname`, `identity`, `password`, `salt`, `anonym`, `email`, `email_status`, `mobile`, `mobile_status`, `gender`, `signature`, `birthday`, `company`, `occupation`, `address`, `zipcode`, `site`, `graduateschool`, `education`, `avatar`, `idcardtype`, `idcard`, `remark`, `status`, `created`, `create_source`, `logged`, `activated`)
VALUES
	(1,'admin','admin','admin',NULL,'789b622f5fe881287240270bda53c0668c1f0fcb9757c3b0440018f763578084','ASCkepSxWewRo2BtadGVEWZgZJRPvpoP',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ok','2022-04-13 11:26:01','web_register','2022-07-19 13:16:28','2022-04-13 11:26:01'),
	(3,'ces','ces','ces',NULL,'3455d6c1e122cc18563c26245152cd32da280f915fa2b5bee0b5926453e179d4','ADdfmU11ydFSIIPOruTa0GxA2MuHWNLy','4a66432623f34e15831eb5e88ec63dba','2586283122@qq.com',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'registered','2022-07-01 14:20:48','web_register','2022-07-18 15:02:44',NULL),
	(4,'ces6666','ces6666','ces6666',NULL,'f67914ede72b47d70a78014e8e743da8ab012b22a0f9a82253ab25dda7cd1b0a','lSE7BmgVxiHmi1gkdLM9ZP3zsJhkhKlK','4a66432623f34e15831eb5e88ec63dba','',NULL,'18984961014',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'registered','2022-07-13 10:53:45','web_register','2022-07-18 14:46:54',NULL),
	(5,'qqq','qqq','qqq',NULL,'887fbe506044d90fa29da466951ebbc7cd5f3accae020e6272dc86086d64d127','qmky3ggxL9n47pM4SWDJs38JyTnOgdaE','4a66432623f34e15831eb5e88ec63dba','3470879513@qq.com',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'registered','2022-07-18 16:39:18','web_register','2022-07-18 16:43:08',NULL);

/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 user_openid
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_openid`;

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
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `type_value` (`type`,`value`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=COMPACT COMMENT='账号绑定信息表';



# 转储表 user_role_mapping
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_role_mapping`;

CREATE TABLE `user_role_mapping` (
  `user_id` int(11) unsigned NOT NULL COMMENT '用户ID',
  `role_id` int(11) unsigned NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='用户和角色的多对多映射表';

LOCK TABLES `user_role_mapping` WRITE;
/*!40000 ALTER TABLE `user_role_mapping` DISABLE KEYS */;

INSERT INTO `user_role_mapping` (`user_id`, `role_id`)
VALUES
	(1,1);

/*!40000 ALTER TABLE `user_role_mapping` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 user_tag
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_tag`;

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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='用户标签。';



# 转储表 user_tag_mapping
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_tag_mapping`;

CREATE TABLE `user_tag_mapping` (
  `user_id` int(11) unsigned NOT NULL COMMENT '用户ID',
  `tag_id` int(11) unsigned NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`user_id`,`tag_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='用户和标签的多对多关系表';



# 转储表 utm
# ------------------------------------------------------------

DROP TABLE IF EXISTS `utm`;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='用户行为记录表';



# 转储表 wechat_menu
# ------------------------------------------------------------

DROP TABLE IF EXISTS `wechat_menu`;

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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='微信公众号菜单表';



# 转储表 wechat_reply
# ------------------------------------------------------------

DROP TABLE IF EXISTS `wechat_reply`;

CREATE TABLE `wechat_reply` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `keyword` varchar(128) DEFAULT NULL COMMENT '关键字',
  `content` text COMMENT '回复内容',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `modified` datetime DEFAULT NULL COMMENT '修改时间',
  `site_id` int(11) DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `keyword` (`keyword`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='用户自定义关键字回复表';

LOCK TABLES `wechat_reply` WRITE;
/*!40000 ALTER TABLE `wechat_reply` DISABLE KEYS */;

INSERT INTO `wechat_reply` (`id`, `keyword`, `content`, `created`, `modified`, `site_id`)
VALUES
	(1,'01010','{\"rtype\":\"text\",\"text\":\"0101010\"}','2022-06-16 10:45:27',NULL,NULL);

/*!40000 ALTER TABLE `wechat_reply` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
