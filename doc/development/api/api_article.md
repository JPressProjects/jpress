# 文章相关API文档



## 文章详情
- 访问路径: `/api/article/detail`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 文章ID | 否 | * |  |  
  | slug | 文章固定连接 | 否 | * |  |  

   > id 或者 slug 必须传入一个值

## 创建新文章
- 访问路径: `/api/article/doCreate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | article | 文章的 JSON 信息 | 否 | POST |  |  



## 删除文章
- 访问路径: `/api/article/doDelete`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 文章ID | 是 | * |  |  



## 更新文章
- 访问路径: `/api/article/doUpdate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | article | 文章的 JSON 信息 | 否 | POST |  |  



## 根据文章分的ID查找文章列表
- 访问路径: `/api/article/listByCategoryId`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | categoryId | 文章分类ID | 是 | * |  |  
  | count | 查询数量 | 否 | * | 默认值：10 |  



## 根据文章分类的固定连接查找文章列表
- 访问路径: `/api/article/listByCategorySlug`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | categorySlug | 分类的固定连接 | 是 | * |  |  
  | count | 查询数量 | 否 | * | 默认值：10 |  



## 根据文章的 flag 查找文章列表
- 访问路径: `/api/article/listByFlag`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | flag | 文章标识 | 是 | * |  |  
  | hasThumbnail | 是否必须要图片 | 否 | * | true 必须有图片，false 必须无图片 |  
  | orderBy | 排序方式 | 否 | * |  |  
  | count | 查询数量 | 否 | * | 默认值：10 |  



## 查询某一篇文章的 相关文章
- 访问路径: `/api/article/listByRelevant`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | articleId | 文章ID | 是 | * |  |  
  | count | 查询数量 | 否 | * | 默认值：3 |  



## 文章分页读取
- 访问路径: `/api/article/paginate`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | categoryId | 文章分类ID | 否 | * |  |  
  | orderBy | 排序方式 | 否 | * |  |  
  | pageNumber | 分页的页码 | 否 | * | 默认值：1 |  
  | pageSize | 每页的数据数量 | 否 | * | 默认值：10 |  



## 文章搜索
- 访问路径: `/api/article/search`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | keyword | 关键词 | 否 | * |  |  
  | pageNumber | 分页页码 | 否 | * | 默认值：1 |  
  | pageSize | 每页数量 | 否 | * | 默认值：10 |  

