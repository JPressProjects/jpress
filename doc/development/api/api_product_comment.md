# 文章评论相关API



## 创建新的评论
- 访问路径: `/api/product/comment/doCreate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | productComment | 评论的 json | 否 | POST |  |  



## 删除评论
- 访问路径: `/api/product/comment/doDelete`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 评论ID | 是 | * |  |  



## 更新评论信息
- 访问路径: `/api/product/comment/doUpdate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | productComment | 评论的 json | 否 | POST |  |  



## 分页查询谋个产品的评论
- 访问路径: `/api/product/comment/paginateByProductId`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | productId | 产品ID | 是 | * |  |  
  | pageNumber | 分页页码 | 否 | * | 默认值：1 |  
  | pageSize | 每页数据量 | 否 | * | 默认值：10 |  

