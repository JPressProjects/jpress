# 用户地址相关API



## 收货地址详情
- 访问路径: `/api/userAddress/detail`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 收货地址ID | 是 | * |  |  



## 新增用户地址
- 访问路径: `/api/userAddress/doCreate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | userAddress | 收货地址ID | 否 | POST |  |  



## 删除用户地址
- 访问路径: `/api/userAddress/doDelete`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 收货地址ID | 是 | * |  |  



## 更新用户地址
- 访问路径: `/api/userAddress/doUpdate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | userAddress | 收货地址ID | 否 | POST |  |  



## 获取用户默认的收货地址
- 访问路径: `/api/userAddress/findUserDefaultAddress`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | userId | 用户ID | 是 | * |  |  



## 某个用户的收货地址列表
- 访问路径: `/api/userAddress/listByUserId`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | userId | 用户ID | 是 | * |  |  

