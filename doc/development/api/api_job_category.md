# 岗位分类相关API文档



## 岗位分类详情
#### 接口信息：
- 访问路径： `/api/job/category/detail`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| id | 岗位分类ID | `Long` | 否 | * |  |  

> id不能为空
#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| detail | `JobCategory` | 岗位分类详情 |  

JobCategory

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| type | `String` | 分类的类型：category、address |  
| pid | `Long` | 父id |  
| title | `String` | 分类名称 |  
| summary | `String` | 摘要 |  
| count | `Long` | 该分类下的岗位数量 |  
| orderNumber | `Integer` | 排序编码 |  
| flag | `String` | 标识 |  
| created | `Date` | 创建日期 |  
| modified | `Date` | 修改日期 |  
| siteId | `Long` | 站点ID |  

**JSON 示例：**
```json
{
	"state":"ok",
	"detail":{
		"id":100,
		"type":"分类的类型：category、address",
		"pid":100,
		"title":"分类名称",
		"summary":"摘要",
		"count":100,
		"orderNumber":100,
		"flag":"标识",
		"created":"2022-08-30 09:20:32",
		"modified":"2022-08-30 09:20:32",
		"siteId":100
	}
}
```


## 创建新的岗位分类
#### 接口信息：
- 访问路径： `/api/job/category/doCreate`
- 数据类型： `application/json`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| jobCategory | 岗位分类json | `JobCategory` | 否 | POST |  |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| id | `Long` | 岗位分类ID |  

**JSON 示例：**
```json
{
	"state":"ok",
	"id":123
}
```


## 删除岗位分类
#### 接口信息：
- 访问路径： `/api/job/category/doDelete`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| id | 岗位分类ID | `Long` | 是 | * |  |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  

**JSON 示例：**
```json
{
	"state":"ok"
}
```


## 更新岗位分类
#### 接口信息：
- 访问路径： `/api/job/category/doUpdate`
- 数据类型： `application/json`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| jobCategory | 岗位分类json | `JobCategory` | 否 | POST |  |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  

**JSON 示例：**
```json
{
	"state":"ok"
}
```


## 根据自定义条件查找岗位分类列表
#### 接口信息：
- 访问路径： `/api/job/category/listByColumns`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| pid | 分类父ID | `Long` | 否 | * |  |  
| userId | 分类创建用户ID | `Long` | 否 | * |  |  
| orderBy | 排序属性 | `String` | 否 | * | 默认值：order_number asc |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| list | `List<JobCategory>` | 岗位分类列表 |  

**JSON 示例：**
```json
{
	"state":"ok",
	"list":[
		{
			"id":100,
			"type":"分类的类型：category、address",
			"pid":100,
			"title":"分类名称",
			"summary":"摘要",
			"count":100,
			"orderNumber":100,
			"flag":"标识",
			"created":"2022-08-30 09:20:32",
			"modified":"2022-08-30 09:20:32",
			"siteId":100
		},
		{
			"id":100,
			"type":"分类的类型：category、address",
			"pid":100,
			"title":"分类名称",
			"summary":"摘要",
			"count":100,
			"orderNumber":100,
			"flag":"标识",
			"created":"2022-08-30 09:20:32",
			"modified":"2022-08-30 09:20:32",
			"siteId":100
		}
	]
}
```
