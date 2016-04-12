# ************************************************************
# JPress SQL
# Version 0.1
#
# http://www.JPress.io
# ************************************************************


# Dump of table attachment
# ------------------------------------------------------------

DROP TABLE IF EXISTS `{table_prefix}attachment`;

CREATE TABLE `{table_prefix}attachment` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `title` text,
  `user_id` int(11) unsigned DEFAULT '0',
  `content_id` int(11) DEFAULT '0',
  `path` varchar(512) DEFAULT NULL,
  `mime_type` varchar(128) DEFAULT NULL,
  `suffix` varchar(32) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `created` (`created`),
  KEY `suffix` (`suffix`),
  KEY `mime_type` (`mime_type`)
) ENGINE=InnoDB DEFAULT CHARSET={charset};



# Dump of table comment
# ------------------------------------------------------------

DROP TABLE IF EXISTS `{table_prefix}comment`;

CREATE TABLE `{table_prefix}comment` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) unsigned DEFAULT '0',
  `content_id` int(11) unsigned DEFAULT '0',
  `content_module` varchar(32) DEFAULT NULL,
  `comment_count` int(11) DEFAULT '0',
  `user_id` int(11) unsigned DEFAULT '0',
  `ip` varchar(64) DEFAULT NULL,
  `author` varchar(11) DEFAULT NULL,
  `type` varchar(32) DEFAULT NULL,
  `text` longtext,
  `agent` varchar(11) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `slug` varchar(128) DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL,
  `vote_up` int(11) DEFAULT NULL,
  `vote_down` int(11) DEFAULT NULL,
  `lat` decimal(20,16) DEFAULT NULL,
  `lng` decimal(20,16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`),
  KEY `content_id` (`content_id`),
  KEY `user_id` (`user_id`),
  KEY `email` (`email`),
  KEY `created` (`created`),
  KEY `parent_id` (`parent_id`),
  KEY `content_module` (`content_module`),
  KEY `comment_count` (`comment_count`)
) ENGINE=InnoDB DEFAULT CHARSET={charset};


# Dump of table content
# ------------------------------------------------------------

DROP TABLE IF EXISTS `{table_prefix}content`;

CREATE TABLE `{table_prefix}content` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `title` text,
  `text` longtext,
  `thumbnail` varchar(128) DEFAULT NULL,
  `module` varchar(32) DEFAULT NULL,
  `style` varchar(32) DEFAULT NULL,
  `user_id` int(11) unsigned DEFAULT '0',
  `parent_id` int(11) unsigned DEFAULT '0',
  `object_id` int(11) unsigned DEFAULT '0',
  `order_id` int(11) DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL,
  `vote_up` int(11) unsigned DEFAULT '0',
  `vote_down` int(11) unsigned DEFAULT '0',
  `comment_status` varchar(32) DEFAULT NULL,
  `comment_count` int(11) unsigned DEFAULT '0',
  `view_count` int(11) unsigned DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `created_gmt` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `modified_gmt` datetime DEFAULT NULL,
  `slug` varchar(128) DEFAULT NULL,
  `flag` varchar(256) DEFAULT NULL,
  `lat` decimal(20,16) DEFAULT NULL,
  `lng` decimal(20,16) DEFAULT NULL,
  `meta_keywords` varchar(256) DEFAULT NULL,
  `meta_description` varchar(256) DEFAULT NULL,
  `remarks` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`),
  KEY `user_id` (`user_id`),
  KEY `parent_id` (`parent_id`),
  KEY `content_module` (`module`),
  KEY `created` (`created`),
  KEY `vote_down` (`vote_down`),
  KEY `vote_up` (`vote_up`),
  KEY `view_count` (`view_count`)
) ENGINE=InnoDB DEFAULT CHARSET={charset};



# Dump of table mapping
# ------------------------------------------------------------

DROP TABLE IF EXISTS `{table_prefix}mapping`;

CREATE TABLE `{table_prefix}mapping` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `content_id` int(11) unsigned NOT NULL DEFAULT '0',
  `taxonomy_id` int(11) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `taxonomy_id` (`taxonomy_id`),
  KEY `content_id` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET={charset};


# Dump of table metadata
# ------------------------------------------------------------

DROP TABLE IF EXISTS `{table_prefix}metadata`;

CREATE TABLE `{table_prefix}metadata` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `meta_key` varchar(255) DEFAULT NULL,
  `meta_value` text,
  `object_type` varchar(32) DEFAULT NULL,
  `object_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET={charset};


# Dump of table option
# ------------------------------------------------------------

DROP TABLE IF EXISTS `{table_prefix}option`;

CREATE TABLE `{table_prefix}option` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `option_key` varchar(32) DEFAULT NULL,
  `option_value` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET={charset};


# Dump of table taxonomy
# ------------------------------------------------------------

DROP TABLE IF EXISTS `{table_prefix}taxonomy`;

CREATE TABLE `{table_prefix}taxonomy` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(512) DEFAULT NULL,
  `text` text,
  `slug` varchar(128) DEFAULT NULL,
  `type` varchar(32) DEFAULT NULL,
  `content_module` varchar(32) DEFAULT NULL,
  `content_count` int(11) DEFAULT '0',
  `parent_id` int(11) unsigned DEFAULT '0',
  `template` varchar(32) DEFAULT NULL,
  `object_id` int(11) unsigned DEFAULT '0',
  `lat` decimal(20,16) DEFAULT NULL,
  `lng` decimal(20,16) DEFAULT NULL,
  `meta_keywords` varchar(256) DEFAULT NULL,
  `meta_description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`),
  KEY `parent_id` (`parent_id`),
  KEY `object_id` (`object_id`),
  KEY `content_module` (`content_module`)
) ENGINE=InnoDB DEFAULT CHARSET={charset};


# Dump of table user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `{table_prefix}user`;

CREATE TABLE `{table_prefix}user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `salt` varchar(32) DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  `cell_number` varchar(32) DEFAULT NULL,
  `nickname` varchar(64) DEFAULT NULL,
  `balance` decimal(10,4) unsigned DEFAULT '0.0000',
  `gender` varchar(16) DEFAULT NULL,
  `role` varchar(32) DEFAULT 'visitor',
  `signature` varchar(2048) DEFAULT NULL,
  `qq` varchar(16) DEFAULT NULL,
  `wechat` varchar(32) DEFAULT NULL,
  `weibo` varchar(64) DEFAULT NULL,
  `avatar` varchar(256) DEFAULT NULL,
  `status` varchar(32) DEFAULT 'normal',
  `created` datetime DEFAULT NULL,
  `create_source` varchar(128) DEFAULT NULL,
  `logged` datetime DEFAULT NULL,
  `activated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `cell_number` (`cell_number`),
  KEY `status` (`status`),
  KEY `created` (`created`)
) ENGINE=InnoDB DEFAULT CHARSET={charset};

