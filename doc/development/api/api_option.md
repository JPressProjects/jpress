# 系统配置相关 API 



## 查询配置
- 访问路径: `/api/option/query`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | key | 查询的 key | 是 | * | 多个 key 用引文逗号隔开 |  



## 更新配置
- 访问路径: `/api/option/set`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | keyValues | 要更新的数据 map | 是 | POST |  |  

