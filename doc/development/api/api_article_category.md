# 文章分类相关API文档



## 文章分类详情
- 访问路径: `/api/article/category/detail`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 分类ID | 否 | * |  |  
  | slug | 分类固定连接 | 否 | * |  |  

   > id 或者 slug 必须有一个不能为空

## 创建新的文章分类
- 访问路径: `/api/article/category/doCreate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | articleCategory | 文章分类json | 否 | POST |  |  



## 删除文章分类（Tag）
- 访问路径: `/api/article/category/doDelete`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 分类ID | 是 | * |  |  



## 更新文章分类
- 访问路径: `/api/article/category/doUpdate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | articleCategory | 文章分类json | 否 | POST |  |  



## 根据文章分类的type查询文章分类
- 访问路径: `/api/article/category/listByType`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | type | 分类type | 是 | * |  |  
  | pid | 上级分类ID | 否 | * |  |  

