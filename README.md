全新 JPress 正在重构...

旧版本请移步：https://gitee.com/fuhai/jpress/tree/alpha/

#### 新版本部署方法：

* 1、先通过 db.sql 脚本创建数据库表
* 2、在 starter 模块下的文件 jboot.properties 配置数据库信息
* 3、运行 starter 模块下的 Starter 的 `main()` 方法


#### 其他：

##### 管理员账号密码sql

```sql
INSERT INTO `user` 
(`id`, `username`, `nickname`, `realname`, `identity`, `password`, `salt`)
VALUES 
(1, 'admin', 'michael', '海哥', '程序员', 'f672ff8d263e89b313d2ab8ee88fec3d58e4c28d21939c858aa44f3bc6da7197', 'NYXvReOTBfBTh-vIhMz5_OazXk_nZs5V');


INSERT INTO `role` (`id`, `name`, `description`, `flag`, `created`, `modified`)
VALUES
	(1, '角色名称', '描述', 'jpsa', '2016-00-00 00:00:00', '2018-09-23 10:56:03');


INSERT INTO `user_role_mapping` (`user_id`, `role_id`)
VALUES
	(1, 1);

```

##### 后台登录
* 地址：http://127.0.0.1:8080/admin
* 账号：admin
* 密码：111111
