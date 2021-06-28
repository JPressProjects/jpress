# 页面相关的API



## 页面详情
- 访问路径: `/api/page/detail`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 页面ID | 否 | * |  |  
  | slug | 页面固定连接 | 否 | * |  |  

   > id 和 slug 必须有一个不能为空

## 创建新页面
- 访问路径: `/api/page/doCreate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | singlePage | 页面 json 数据 | 否 | POST |  |  



## 删除页面
- 访问路径: `/api/page/doDelete`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 页面id | 是 | * |  |  



## 更新页面
- 访问路径: `/api/page/doUpdate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | singlePage | 页面 json 数据 | 否 | POST |  |  



## 根据 flag 查询页面列表
- 访问路径: `/api/page/listByFlag`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | flag | 页面的 flag 标识 | 是 | * |  |  

