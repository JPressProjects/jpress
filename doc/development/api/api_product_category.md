# 产品分类相关 API



## 获取商品分类详情
- 访问路径: `/api/product/category/detail`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 产品ID | 否 | * |  |  
  | slug | 产品固定连接 | 否 | * |  |  

   > id 和 slug 必须传入一个值

## 创建新的产品分类
- 访问路径: `/api/product/category/doCreate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | productCategory | 产品分类的 JSON 信息 | 否 | POST |  |  



## 删除产品分类
- 访问路径: `/api/product/category/doDelete`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 产品分类ID | 是 | * |  |  



## 更新产品分类
- 访问路径: `/api/product/category/doUpdate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | productCategory | 产品分类的 JSON 信息 | 否 | POST |  |  

