# 购物车相关API



## 创建购物车产品
- 访问路径: `/api/userCart/doCreate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | userCart | 购物车产品 JSON 信息 | 否 | POST |  |  



## 删除购物车产品
- 访问路径: `/api/userCart/doDelete`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 购物车产品ID | 是 | * |  |  



## 更新购物车产品
- 访问路径: `/api/userCart/doUpdate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | userCart | 购物车产品 JSON 信息 | 否 | POST |  |  



## 某个用户的购物车列表
- 访问路径: `/api/userCart/listByUserId`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | userId | 用户ID | 是 | * |  |  

