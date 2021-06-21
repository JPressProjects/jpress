# 用户相关的 API 文档

## 用户登录

- 访问路径: `/api/user/login`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
    | --- | --- | --- | --- |
  | loginAccount | 是 | POST | 用户名 |  
  | password | 是 | POST | 密码 |  


- 返回数据

  ```json
  {"Jwt":"ey1NiJ9.eyJpYX0ifQ.Y3p4akomy4","state":"ok"}
  ```
  
  以后，凡是涉及到用户登录才能操作的，必须通过传入 Jwt 的值。

## 用户查询

- 访问路径: `/api/user/detail`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | id | 是 | GET | 用户ID |  


- 返回数据

  ```json
  {"state":"ok",
    "user":{
      "emailStatusOk":false,
      "statusLocked":false,
      "mobileStatusOk":false,
      "statusReg":false,
      "id":1,
      "...."
    }
  }
  ```


## 用户信息更新

- 访问路径: `/api/user/update`
- 数据类型: `application/json`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | user | 是 | POST | 用户信息 |  

  user 内容如下：
  
   ```json
  {
      "emailStatusOk":false,
      "statusLocked":false,
      "mobileStatusOk":false,
      "statusReg":false,
      "id":1,
      "...."
  }
  ```


- 返回数据

  ```json
  {"state":"ok"}
  ```

## 用户密码修改

- 访问路径: `/api/user/updatePassword`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | userId | 是 | POST | 用户ID |  
  | newPassword | 是 | POST | 新的密码 |  


- 返回数据

  ```json
  {"state":"ok"}
  ```



## 创建新的用户

- 访问路径: `/api/user/created`
- 数据类型: `application/json`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | user | 是 | POST | 用户信息 |  

  user 内容如下：

   ```json
  {
      "username":"user001",
      "loginname":"password...",
      "...."
  }
  ```


- 返回数据

  ```json
  {"state":"ok","userId": 123}
  ```