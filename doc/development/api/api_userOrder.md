# 订单相关API



## 创建订单
- 访问路径: `/api/userOrder/doCreate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | userOrder | 订单的 JSON 信息 | 是 | POST |  |  



## 删除订单
- 访问路径: `/api/userOrder/doDelete`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 订单ID | 是 | * |  |  



## 更新订单
- 访问路径: `/api/userOrder/doUpdate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | userOrder | 订单的 JSON 信息 | 是 | POST |  |  



## 分页查询用户订单
- 访问路径: `/api/userOrder/paginateByUserId`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | userId | 用户ID | 是 | * |  |  
  | title | 订单标题 | 否 | * | 一般用户搜索 |  
  | ns | 订单号 | 否 | * | 一般用户搜索 |  
  | pageNumber | 页码 | 否 | * | 默认值：1 |  
  | pageSize | 每页数据量 | 否 | * | 默认值：10 |  

