# 文章评论相关API



## 创建新的评论
- 访问路径: `/api/article/comment/doCreate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | articleComment | 评论的 json | 否 | POST |  |  



## 删除评论
- 访问路径: `/api/article/comment/doDelete`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 评论ID | 是 | * |  |  



## 更新评论信息
- 访问路径: `/api/article/comment/doUpdate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | articleComment | 评论的 json | 否 | POST |  |  



## 分页查询谋篇文章的评论
- 访问路径: `/api/article/comment/paginateByArticleId`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | articleId | 文章ID | 是 | * |  |  
  | pageNumber | 分页页码 | 否 | * | 默认值：1 |  
  | pageSize | 每页数据量 | 否 | * | 默认值：10 |  

