# 文章分类相关API文档



## 文章分类详情
#### 接口信息：
- 访问路径： `/api/article/category/detail`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| id | 分类ID | `Long` | 否 | * |  |  
| slug | 分类固定连接 | `String` | 否 | * |  |  

> id 或者 slug 必须有一个不能为空
#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| detail | `ArticleCategory` | 文章分类详情 |  

ArticleCategory

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| id | `Long` | 主键ID |  
| pid | `Long` | 父级分类的ID |  
| userId | `Long` | 分类创建的用户ID |  
| slug | `String` | slug |  
| title | `String` | 标题 |  
| content | `String` | 内容描述 |  
| summary | `String` | 摘要 |  
| style | `String` | 模板样式 |  
| type | `String` | 类型，比如：分类、tag、专题 |  
| icon | `String` | 图标 |  
| count | `Long` | 该分类的内容数量 |  
| orderNumber | `Integer` | 排序编码 |  
| flag | `String` | 标识 |  
| metaKeywords | `String` | SEO关键字 |  
| metaDescription | `String` | SEO描述内容 |  
| created | `Date` | 创建日期 |  
| modified | `Date` | 修改日期 |  

**JSON 示例：**
```json
{
	"state":"ok",
	"detail":{
		"id":100,
		"pid":100,
		"userId":100,
		"slug":"slug",
		"title":"标题",
		"content":"内容描述",
		"summary":"摘要",
		"style":"模板样式",
		"type":"类型，比如：分类、tag、专题",
		"icon":"图标",
		"count":100,
		"orderNumber":100,
		"flag":"标识",
		"metaKeywords":"SEO关键字",
		"metaDescription":"SEO描述内容",
		"created":"2021-07-03 10:04:53",
		"modified":"2021-07-03 10:04:53"
	}
}
```


## 创建新的文章分类
#### 接口信息：
- 访问路径： `/api/article/category/doCreate`
- 数据类型： `application/json`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| articleCategory | 文章分类json | `ArticleCategory` | 否 | POST |  |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| id | `Long` | 文章分类D |  

**JSON 示例：**
```json
{
	"state":"ok",
	"id":"123"
}
```


## 删除文章分类（Tag）
#### 接口信息：
- 访问路径： `/api/article/category/doDelete`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| id | 分类ID | `Long` | 是 | * |  |  


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


## 更新文章分类
#### 接口信息：
- 访问路径： `/api/article/category/doUpdate`
- 数据类型： `application/json`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| articleCategory | 文章分类json | `ArticleCategory` | 否 | POST |  |  


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


## 根据文章分类的type查询文章分类
#### 接口信息：
- 访问路径： `/api/article/category/listByType`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| type | 分类type | `String` | 是 | * |  |  
| pid | 上级分类ID | `Long` | 否 | * |  |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| list | `List<ArticleCategory>` | 文章分类列表 |  

**JSON 示例：**
```json
{
	"state":"ok",
	"list":[
		{
			"id":100,
			"pid":100,
			"userId":100,
			"slug":"slug",
			"title":"标题",
			"content":"内容描述",
			"summary":"摘要",
			"style":"模板样式",
			"type":"类型，比如：分类、tag、专题",
			"icon":"图标",
			"count":100,
			"orderNumber":100,
			"flag":"标识",
			"metaKeywords":"SEO关键字",
			"metaDescription":"SEO描述内容",
			"created":"2021-07-03 10:04:53",
			"modified":"2021-07-03 10:04:53"
		},
		{
			"id":100,
			"pid":100,
			"userId":100,
			"slug":"slug",
			"title":"标题",
			"content":"内容描述",
			"summary":"摘要",
			"style":"模板样式",
			"type":"类型，比如：分类、tag、专题",
			"icon":"图标",
			"count":100,
			"orderNumber":100,
			"flag":"标识",
			"metaKeywords":"SEO关键字",
			"metaDescription":"SEO描述内容",
			"created":"2021-07-03 10:04:53",
			"modified":"2021-07-03 10:04:53"
		}
	]
}
```
