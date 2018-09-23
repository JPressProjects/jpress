全新 JPress 正在重构...

旧版本请移步：https://gitee.com/fuhai/jpress/tree/alpha/

#### 新版本运行步骤：

* 1、通过 db.sql 脚本创建数据库表
* 2、编辑 starter 模块下的 jboot.properties 文件， 配置上数据库信息
* 3、运行 starter 模块下的 Starter 的 `main()` 方法



#### 管理员的账号、密码、及角色添加

```sql
INSERT INTO `user` 
(`id`, `username`, `nickname`, `realname`, `identity`, `password`, `salt`)
VALUES 
(1, 'admin', 'michael', '海哥', '程序员', 
'f672ff8d263e89b313d2ab8ee88fec3d58e4c28d21939c858aa44f3bc6da7197', 'NYXvReOTBfBTh-vIhMz5_OazXk_nZs5V');


INSERT INTO `role` (`id`, `name`, `description`, `flag`, `created`, `modified`)
VALUES
	(1, '角色名称', '描述', 'jpsa', '2016-00-00 00:00:00', '2018-09-23 10:56:03');


INSERT INTO `user_role_mapping` (`user_id`, `role_id`)
VALUES
	(1, 1);

```

#### 后台登录地址及账号信息

* 地址：http://127.0.0.1:8080/admin
* 账号：admin
* 密码：111111
