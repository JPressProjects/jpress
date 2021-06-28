# 用户相关API



## 创建新的用户
- 访问路径: `/api/user/create`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | user | 用户 json 信息 | 是 | POST |  |  



## 用户详情
- 访问路径: `/api/user/detail`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 用户ID | 是 | * |  |  



## 用户登录
- 访问路径: `/api/user/login`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | loginAccount | 登录账户 | 是 | POST | 可以是邮箱 |  
  | password | 登录密码 | 是 | POST |  |  



## 更新用户信息
- 访问路径: `/api/user/update`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | user | 用户 json 信息 | 是 | POST |  |  



## 更新用户密码
- 访问路径: `/api/user/updatePassword`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | userId | 用户ID | 是 | POST |  |  
  | newPassword | 用户新密码 | 是 | POST |  |  
  | oldPassowrd | 用户旧密码 | 否 | POST | 如果登录用户是超级管理员，则可以不输入密码 |  

