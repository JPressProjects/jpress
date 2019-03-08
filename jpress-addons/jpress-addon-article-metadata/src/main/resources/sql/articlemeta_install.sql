DROP TABLE IF EXISTS `article_meta_info`;

CREATE TABLE `article_meta_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `field_id` varchar(32) DEFAULT NULL,
  `field_name` varchar(64) DEFAULT NULL,
  `label` varchar(128) DEFAULT NULL,
  `placeholder` varchar(512) DEFAULT NULL,
  `type` varchar(32) DEFAULT NULL,
  `value` varchar(512) DEFAULT NULL,
  `value_text` varchar(512) DEFAULT NULL,
  `attrs` varchar(256) DEFAULT NULL,
  `order_no` int(11) DEFAULT NULL,
  `remarks` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `article_meta_record`;

CREATE TABLE `article_meta_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `article_id` int(11) unsigned DEFAULT NULL,
  `field_name` varchar(64) DEFAULT NULL,
  `value` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `article_id_and_field_name` (`article_id`,`field_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
