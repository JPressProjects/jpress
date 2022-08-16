#  页面模块 API 接口


## 页面详情

- 访问路径: `/api/page/detail`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | id | 是 | GET | 页面的 ID |  
  | slug | 否 | GET | 页面的 SLUG |  



## 页面列表

- 访问路径: `/api/page/list`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | flag | 是 | GET | 页面的标识 |  


## 发布新页面

- 访问路径: `/api/page/create`
- 数据类型: `application/json`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | data | 是 | POST | 页面 JSON |  


## 更新页面

- 访问路径: `/api/page/update`
- 数据类型: `application/json`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | data | 是 | POST | 页面 JSON |  
