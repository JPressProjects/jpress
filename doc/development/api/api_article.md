# 文章相关API文档



## 文章详情
#### 接口信息：
- 访问路径： `/api/article/detail`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| id | 文章ID | `Long` | 否 | * |  |  
| slug | 文章固定连接 | `String` | 否 | * |  |  

> id 或者 slug 必须传入一个值
#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| detail | `Article` | 文章详情 |  

Article

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| id | `Long` | 主键ID |  
| pid | `Integer` | 子版本的文章id |  
| slug | `String` | slug |  
| title | `String` | 标题 |  
| author | `String` | 作者 |  
| content | `String` | 内容 |  
| editMode | `String` | 编辑模式，默认为html，其他可选项包括html，markdown .. |  
| summary | `String` | 摘要 |  
| linkTo | `String` | 连接到(常用于谋文章只是一个连接) |  
| thumbnail | `String` | 缩略图 |  
| style | `String` | 样式 |  
| userId | `Long` | 用户ID |  
| orderNumber | `Integer` | 排序编号 |  
| status | `String` | 状态 |  
| commentStatus | `Boolean` | 评论状态，默认允许评论 |  
| commentCount | `Long` | 评论总数 |  
| commentTime | `Date` | 最后评论时间 |  
| viewCount | `Long` | 访问量 |  
| created | `Date` | 创建日期 |  
| modified | `Date` | 最后更新日期 |  
| flag | `String` | 标识，通常用于对某几篇文章进行标识，从而实现单独查询 |  
| metaTitle | `String` | SEO标题 |  
| metaKeywords | `String` | SEO关键字 |  
| metaDescription | `String` | SEO描述信息 |  
| withRecommend | `Boolean` | 是否推荐 |  
| withTop | `Boolean` | 是否置顶 |  
| withHot | `Boolean` | 是否热门 |  
| withLeadNews | `Boolean` | 是否是头条 |  
| withAllowSearch | `Boolean` | 是否允许被搜索 |  
| options | `String` | json 扩展 |  
| siteId | `Long` | 站点ID |  

**JSON 示例：**
```json
{
	"state":"ok",
	"detail":{
		"id":100,
		"pid":100,
		"slug":"slug",
		"title":"标题",
		"author":"作者",
		"content":"内容",
		"editMode":"编辑模式，默认为html，其他可选项包括html，markdown ..",
		"summary":"摘要",
		"linkTo":"连接到(常用于谋文章只是一个连接)",
		"thumbnail":"缩略图",
		"style":"样式",
		"userId":100,
		"orderNumber":100,
		"status":"状态",
		"commentStatus":true,
		"commentCount":100,
		"commentTime":"2022-08-30 09:20:32",
		"viewCount":100,
		"created":"2022-08-30 09:20:32",
		"modified":"2022-08-30 09:20:32",
		"flag":"标识，通常用于对某几篇文章进行标识，从而实现单独查询",
		"metaTitle":"SEO标题",
		"metaKeywords":"SEO关键字",
		"metaDescription":"SEO描述信息",
		"withRecommend":true,
		"withTop":true,
		"withHot":true,
		"withLeadNews":true,
		"withAllowSearch":true,
		"options":"json 扩展",
		"siteId":100
	}
}
```


## 创建新文章
#### 接口信息：
- 访问路径： `/api/article/doCreate`
- 数据类型： `application/json`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| article | 文章的 JSON 信息 | `Article` | 否 | POST |  |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| id | `Long` | 文章ID |  

**JSON 示例：**
```json
{
	"state":"ok",
	"id":123
}
```


## 删除文章
#### 接口信息：
- 访问路径： `/api/article/doDelete`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| id | 文章ID | `Long` | 是 | * |  |  


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


## 更新文章
#### 接口信息：
- 访问路径： `/api/article/doUpdate`
- 数据类型： `application/json`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| article | 文章的 JSON 信息 | `Article` | 否 | POST |  |  


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


## 根据文章分的ID查找文章列表
#### 接口信息：
- 访问路径： `/api/article/listByCategoryId`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| categoryId | 文章分类ID | `Long` | 是 | * |  |  
| count | 查询数量 | `int` | 否 | * | 默认值：10 |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| list | `List<Article>` | 文章列表 |  

**JSON 示例：**
```json
{
	"state":"ok",
	"list":[
		{
			"id":100,
			"pid":100,
			"slug":"slug",
			"title":"标题",
			"author":"作者",
			"content":"内容",
			"editMode":"编辑模式，默认为html，其他可选项包括html，markdown ..",
			"summary":"摘要",
			"linkTo":"连接到(常用于谋文章只是一个连接)",
			"thumbnail":"缩略图",
			"style":"样式",
			"userId":100,
			"orderNumber":100,
			"status":"状态",
			"commentStatus":true,
			"commentCount":100,
			"commentTime":"2022-08-30 09:20:32",
			"viewCount":100,
			"created":"2022-08-30 09:20:32",
			"modified":"2022-08-30 09:20:32",
			"flag":"标识，通常用于对某几篇文章进行标识，从而实现单独查询",
			"metaTitle":"SEO标题",
			"metaKeywords":"SEO关键字",
			"metaDescription":"SEO描述信息",
			"withRecommend":true,
			"withTop":true,
			"withHot":true,
			"withLeadNews":true,
			"withAllowSearch":true,
			"options":"json 扩展",
			"siteId":100
		},
		{
			"id":100,
			"pid":100,
			"slug":"slug",
			"title":"标题",
			"author":"作者",
			"content":"内容",
			"editMode":"编辑模式，默认为html，其他可选项包括html，markdown ..",
			"summary":"摘要",
			"linkTo":"连接到(常用于谋文章只是一个连接)",
			"thumbnail":"缩略图",
			"style":"样式",
			"userId":100,
			"orderNumber":100,
			"status":"状态",
			"commentStatus":true,
			"commentCount":100,
			"commentTime":"2022-08-30 09:20:32",
			"viewCount":100,
			"created":"2022-08-30 09:20:32",
			"modified":"2022-08-30 09:20:32",
			"flag":"标识，通常用于对某几篇文章进行标识，从而实现单独查询",
			"metaTitle":"SEO标题",
			"metaKeywords":"SEO关键字",
			"metaDescription":"SEO描述信息",
			"withRecommend":true,
			"withTop":true,
			"withHot":true,
			"withLeadNews":true,
			"withAllowSearch":true,
			"options":"json 扩展",
			"siteId":100
		}
	]
}
```


## 根据文章分类的固定连接查找文章列表
#### 接口信息：
- 访问路径： `/api/article/listByCategorySlug`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| categorySlug | 分类的固定连接 | `String` | 是 | * |  |  
| count | 查询数量 | `int` | 否 | * | 默认值：10 |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| list | `List<Article>` | 文章列表 |  

**JSON 示例：**
```json
{
	"state":"ok",
	"list":[
		{
			"id":100,
			"pid":100,
			"slug":"slug",
			"title":"标题",
			"author":"作者",
			"content":"内容",
			"editMode":"编辑模式，默认为html，其他可选项包括html，markdown ..",
			"summary":"摘要",
			"linkTo":"连接到(常用于谋文章只是一个连接)",
			"thumbnail":"缩略图",
			"style":"样式",
			"userId":100,
			"orderNumber":100,
			"status":"状态",
			"commentStatus":true,
			"commentCount":100,
			"commentTime":"2022-08-30 09:20:32",
			"viewCount":100,
			"created":"2022-08-30 09:20:32",
			"modified":"2022-08-30 09:20:32",
			"flag":"标识，通常用于对某几篇文章进行标识，从而实现单独查询",
			"metaTitle":"SEO标题",
			"metaKeywords":"SEO关键字",
			"metaDescription":"SEO描述信息",
			"withRecommend":true,
			"withTop":true,
			"withHot":true,
			"withLeadNews":true,
			"withAllowSearch":true,
			"options":"json 扩展",
			"siteId":100
		},
		{
			"id":100,
			"pid":100,
			"slug":"slug",
			"title":"标题",
			"author":"作者",
			"content":"内容",
			"editMode":"编辑模式，默认为html，其他可选项包括html，markdown ..",
			"summary":"摘要",
			"linkTo":"连接到(常用于谋文章只是一个连接)",
			"thumbnail":"缩略图",
			"style":"样式",
			"userId":100,
			"orderNumber":100,
			"status":"状态",
			"commentStatus":true,
			"commentCount":100,
			"commentTime":"2022-08-30 09:20:32",
			"viewCount":100,
			"created":"2022-08-30 09:20:32",
			"modified":"2022-08-30 09:20:32",
			"flag":"标识，通常用于对某几篇文章进行标识，从而实现单独查询",
			"metaTitle":"SEO标题",
			"metaKeywords":"SEO关键字",
			"metaDescription":"SEO描述信息",
			"withRecommend":true,
			"withTop":true,
			"withHot":true,
			"withLeadNews":true,
			"withAllowSearch":true,
			"options":"json 扩展",
			"siteId":100
		}
	]
}
```


## 根据文章的 flag 查找文章列表
#### 接口信息：
- 访问路径： `/api/article/listByFlag`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| flag | 文章标识 | `String` | 是 | * |  |  
| hasThumbnail | 是否必须要图片 | `Boolean` | 否 | * | true 必须有图片，false 必须无图片 |  
| orderBy | 排序方式 | `String` | 否 | * |  |  
| count | 查询数量 | `int` | 否 | * | 默认值：10 |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| list | `List<Article>` | 文章列表 |  

**JSON 示例：**
```json
{
	"state":"ok",
	"list":[
		{
			"id":100,
			"pid":100,
			"slug":"slug",
			"title":"标题",
			"author":"作者",
			"content":"内容",
			"editMode":"编辑模式，默认为html，其他可选项包括html，markdown ..",
			"summary":"摘要",
			"linkTo":"连接到(常用于谋文章只是一个连接)",
			"thumbnail":"缩略图",
			"style":"样式",
			"userId":100,
			"orderNumber":100,
			"status":"状态",
			"commentStatus":true,
			"commentCount":100,
			"commentTime":"2022-08-30 09:20:32",
			"viewCount":100,
			"created":"2022-08-30 09:20:32",
			"modified":"2022-08-30 09:20:32",
			"flag":"标识，通常用于对某几篇文章进行标识，从而实现单独查询",
			"metaTitle":"SEO标题",
			"metaKeywords":"SEO关键字",
			"metaDescription":"SEO描述信息",
			"withRecommend":true,
			"withTop":true,
			"withHot":true,
			"withLeadNews":true,
			"withAllowSearch":true,
			"options":"json 扩展",
			"siteId":100
		},
		{
			"id":100,
			"pid":100,
			"slug":"slug",
			"title":"标题",
			"author":"作者",
			"content":"内容",
			"editMode":"编辑模式，默认为html，其他可选项包括html，markdown ..",
			"summary":"摘要",
			"linkTo":"连接到(常用于谋文章只是一个连接)",
			"thumbnail":"缩略图",
			"style":"样式",
			"userId":100,
			"orderNumber":100,
			"status":"状态",
			"commentStatus":true,
			"commentCount":100,
			"commentTime":"2022-08-30 09:20:32",
			"viewCount":100,
			"created":"2022-08-30 09:20:32",
			"modified":"2022-08-30 09:20:32",
			"flag":"标识，通常用于对某几篇文章进行标识，从而实现单独查询",
			"metaTitle":"SEO标题",
			"metaKeywords":"SEO关键字",
			"metaDescription":"SEO描述信息",
			"withRecommend":true,
			"withTop":true,
			"withHot":true,
			"withLeadNews":true,
			"withAllowSearch":true,
			"options":"json 扩展",
			"siteId":100
		}
	]
}
```


## 查询某一篇文章的 相关文章
#### 接口信息：
- 访问路径： `/api/article/listByRelevant`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| articleId | 文章ID | `Long` | 是 | * |  |  
| count | 查询数量 | `int` | 否 | * | 默认值：3 |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| list | `List<Article>` | 文章列表 |  

**JSON 示例：**
```json
{
	"state":"ok",
	"list":[
		{
			"id":100,
			"pid":100,
			"slug":"slug",
			"title":"标题",
			"author":"作者",
			"content":"内容",
			"editMode":"编辑模式，默认为html，其他可选项包括html，markdown ..",
			"summary":"摘要",
			"linkTo":"连接到(常用于谋文章只是一个连接)",
			"thumbnail":"缩略图",
			"style":"样式",
			"userId":100,
			"orderNumber":100,
			"status":"状态",
			"commentStatus":true,
			"commentCount":100,
			"commentTime":"2022-08-30 09:20:32",
			"viewCount":100,
			"created":"2022-08-30 09:20:32",
			"modified":"2022-08-30 09:20:32",
			"flag":"标识，通常用于对某几篇文章进行标识，从而实现单独查询",
			"metaTitle":"SEO标题",
			"metaKeywords":"SEO关键字",
			"metaDescription":"SEO描述信息",
			"withRecommend":true,
			"withTop":true,
			"withHot":true,
			"withLeadNews":true,
			"withAllowSearch":true,
			"options":"json 扩展",
			"siteId":100
		},
		{
			"id":100,
			"pid":100,
			"slug":"slug",
			"title":"标题",
			"author":"作者",
			"content":"内容",
			"editMode":"编辑模式，默认为html，其他可选项包括html，markdown ..",
			"summary":"摘要",
			"linkTo":"连接到(常用于谋文章只是一个连接)",
			"thumbnail":"缩略图",
			"style":"样式",
			"userId":100,
			"orderNumber":100,
			"status":"状态",
			"commentStatus":true,
			"commentCount":100,
			"commentTime":"2022-08-30 09:20:32",
			"viewCount":100,
			"created":"2022-08-30 09:20:32",
			"modified":"2022-08-30 09:20:32",
			"flag":"标识，通常用于对某几篇文章进行标识，从而实现单独查询",
			"metaTitle":"SEO标题",
			"metaKeywords":"SEO关键字",
			"metaDescription":"SEO描述信息",
			"withRecommend":true,
			"withTop":true,
			"withHot":true,
			"withLeadNews":true,
			"withAllowSearch":true,
			"options":"json 扩展",
			"siteId":100
		}
	]
}
```


## 文章分页读取
#### 接口信息：
- 访问路径： `/api/article/paginate`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| categoryId | 文章分类ID | `Long` | 否 | * |  |  
| orderBy | 排序方式 | `String` | 否 | * |  |  
| pageNumber | 分页的页码 | `int` | 否 | * | 默认值：1 |  
| pageSize | 每页的数据数量 | `int` | 否 | * | 默认值：10 |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| page | `Page<Article>` | 文章分页数据 |  

Page

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| totalRow | `int` | 总行数 |  
| pageNumber | `int` | 当前页码 |  
| firstPage | `boolean` | 是否是第一页 |  
| lastPage | `boolean` | 是否是最后一页 |  
| totalPage | `int` | 总页数 |  
| pageSize | `int` | 每页数据量 |  
| list | `List` | 数据列表 |  

**JSON 示例：**
```json
{
	"state":"ok",
	"page":{
		"pageSize":10,
		"totalPage":1,
		"totalRow":2,
		"firstPage":true,
		"lastPage":true,
		"pageNumber":1,
		"list":[
			{
				"id":100,
				"pid":100,
				"slug":"slug",
				"title":"标题",
				"author":"作者",
				"content":"内容",
				"editMode":"编辑模式，默认为html，其他可选项包括html，markdown ..",
				"summary":"摘要",
				"linkTo":"连接到(常用于谋文章只是一个连接)",
				"thumbnail":"缩略图",
				"style":"样式",
				"userId":100,
				"orderNumber":100,
				"status":"状态",
				"commentStatus":true,
				"commentCount":100,
				"commentTime":"2022-08-30 09:20:32",
				"viewCount":100,
				"created":"2022-08-30 09:20:32",
				"modified":"2022-08-30 09:20:32",
				"flag":"标识，通常用于对某几篇文章进行标识，从而实现单独查询",
				"metaTitle":"SEO标题",
				"metaKeywords":"SEO关键字",
				"metaDescription":"SEO描述信息",
				"withRecommend":true,
				"withTop":true,
				"withHot":true,
				"withLeadNews":true,
				"withAllowSearch":true,
				"options":"json 扩展",
				"siteId":100
			},
			{
				"id":100,
				"pid":100,
				"slug":"slug",
				"title":"标题",
				"author":"作者",
				"content":"内容",
				"editMode":"编辑模式，默认为html，其他可选项包括html，markdown ..",
				"summary":"摘要",
				"linkTo":"连接到(常用于谋文章只是一个连接)",
				"thumbnail":"缩略图",
				"style":"样式",
				"userId":100,
				"orderNumber":100,
				"status":"状态",
				"commentStatus":true,
				"commentCount":100,
				"commentTime":"2022-08-30 09:20:32",
				"viewCount":100,
				"created":"2022-08-30 09:20:32",
				"modified":"2022-08-30 09:20:32",
				"flag":"标识，通常用于对某几篇文章进行标识，从而实现单独查询",
				"metaTitle":"SEO标题",
				"metaKeywords":"SEO关键字",
				"metaDescription":"SEO描述信息",
				"withRecommend":true,
				"withTop":true,
				"withHot":true,
				"withLeadNews":true,
				"withAllowSearch":true,
				"options":"json 扩展",
				"siteId":100
			}
		]
	}
}
```


## 文章搜索
#### 接口信息：
- 访问路径： `/api/article/search`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| keyword | 关键词 | `String` | 否 | * |  |  
| pageNumber | 分页页码 | `int` | 否 | * | 默认值：1 |  
| pageSize | 每页数量 | `int` | 否 | * | 默认值：10 |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| page | `Page<Article>` | 文章分页数据 |  

Page

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| totalRow | `int` | 总行数 |  
| pageNumber | `int` | 当前页码 |  
| firstPage | `boolean` | 是否是第一页 |  
| lastPage | `boolean` | 是否是最后一页 |  
| totalPage | `int` | 总页数 |  
| pageSize | `int` | 每页数据量 |  
| list | `List` | 数据列表 |  

**JSON 示例：**
```json
{
	"state":"ok",
	"page":{
		"pageSize":10,
		"totalPage":1,
		"totalRow":2,
		"firstPage":true,
		"lastPage":true,
		"pageNumber":1,
		"list":[
			{
				"id":100,
				"pid":100,
				"slug":"slug",
				"title":"标题",
				"author":"作者",
				"content":"内容",
				"editMode":"编辑模式，默认为html，其他可选项包括html，markdown ..",
				"summary":"摘要",
				"linkTo":"连接到(常用于谋文章只是一个连接)",
				"thumbnail":"缩略图",
				"style":"样式",
				"userId":100,
				"orderNumber":100,
				"status":"状态",
				"commentStatus":true,
				"commentCount":100,
				"commentTime":"2022-08-30 09:20:32",
				"viewCount":100,
				"created":"2022-08-30 09:20:32",
				"modified":"2022-08-30 09:20:32",
				"flag":"标识，通常用于对某几篇文章进行标识，从而实现单独查询",
				"metaTitle":"SEO标题",
				"metaKeywords":"SEO关键字",
				"metaDescription":"SEO描述信息",
				"withRecommend":true,
				"withTop":true,
				"withHot":true,
				"withLeadNews":true,
				"withAllowSearch":true,
				"options":"json 扩展",
				"siteId":100
			},
			{
				"id":100,
				"pid":100,
				"slug":"slug",
				"title":"标题",
				"author":"作者",
				"content":"内容",
				"editMode":"编辑模式，默认为html，其他可选项包括html，markdown ..",
				"summary":"摘要",
				"linkTo":"连接到(常用于谋文章只是一个连接)",
				"thumbnail":"缩略图",
				"style":"样式",
				"userId":100,
				"orderNumber":100,
				"status":"状态",
				"commentStatus":true,
				"commentCount":100,
				"commentTime":"2022-08-30 09:20:32",
				"viewCount":100,
				"created":"2022-08-30 09:20:32",
				"modified":"2022-08-30 09:20:32",
				"flag":"标识，通常用于对某几篇文章进行标识，从而实现单独查询",
				"metaTitle":"SEO标题",
				"metaKeywords":"SEO关键字",
				"metaDescription":"SEO描述信息",
				"withRecommend":true,
				"withTop":true,
				"withHot":true,
				"withLeadNews":true,
				"withAllowSearch":true,
				"options":"json 扩展",
				"siteId":100
			}
		]
	}
}
```
