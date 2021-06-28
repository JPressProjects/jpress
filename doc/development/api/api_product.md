# 产品相关 API



## 获取商品详情
- 访问路径: `/api/product/detail`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 产品ID | 否 | * |  |  
  | slug | 产品固定连接 | 否 | * |  |  

   > id 和 slug 必须传入一个值

## 新增产品
- 访问路径: `/api/product/doCreate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | product | 产品的 JSON 信息 | 否 | POST |  |  



## 删除产品
- 访问路径: `/api/product/doDelete`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 产品ID | 是 | * |  |  



## 更新产品信息
- 访问路径: `/api/product/doUpdate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | product | 产品的 JSON 信息 | 否 | POST |  |  

