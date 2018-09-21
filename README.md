全新 JPress 正在重构...

旧版本请移步：https://gitee.com/fuhai/jpress/tree/alpha/

#### 新版本部署方法：

* 1、先通过 db.sql 创建数据库表
* 2、在 starter 模块下的文件 jboot.properties 配置数据库信息
* 3、运行 starter 模块下的 Starter 的main方法


#### 其他：

##### 插入数据库管理员账号密码sql

```sql
INSERT INTO `user` (`id`, `username`, `nickname`, `realname`, `identity`, `password`, `salt`, `anonym`, `wx_openid`, `wx_unionid`, `qq_openid`, `email`, `email_status`, `mobile`, `mobile_status`, `gender`, `signature`, `birthday`, `company`, `occupation`, `address`, `zipcode`, `site`, `graduateschool`, `education`, `avatar`, `idcardtype`, `idcard`, `remark`, `status`, `created`, `create_source`, `logged`, `activated`)
VALUES
	(1, 'admin', 'michael', '海哥', '程序员', 'f672ff8d263e89b313d2ab8ee88fec3d58e4c28d21939c858aa44f3bc6da7197', 'NYXvReOTBfBTh-vIhMz5_OazXk_nZs5V', NULL, NULL, NULL, NULL, '啊啊啊', NULL, '18611223344', 'ok', NULL, NULL, NULL, 'cc', 'dd', '阿斯蒂芬', '阿斯蒂芬', '安抚', 'ee', 'xx', '/attachment/20180921/6269bd9f13a6491abec6dabf353f009b.png', NULL, NULL, '啊啊啊', 'ok', NULL, NULL, NULL, NULL);

```
