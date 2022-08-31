# 文章评论相关API



## 创建新的评论
#### 接口信息：
- 访问路径： `/api/article/comment/doCreate`
- 数据类型： `application/json`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| articleComment | 评论的 json | `ArticleComment` | 否 | POST |  |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| id | `Long` | 评论的ID |  

**JSON 示例：**
```json
{
	"state":"ok",
	"id":123
}
```


## 删除评论
#### 接口信息：
- 访问路径： `/api/article/comment/doDelete`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| id | 评论ID | `Long` | 是 | * |  |  


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


## 更新评论信息
#### 接口信息：
- 访问路径： `/api/article/comment/doUpdate`
- 数据类型： `application/json`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| articleComment | 评论的 json | `ArticleComment` | 否 | POST |  |  


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


## 分页查询谋篇文章的评论
#### 接口信息：
- 访问路径： `/api/article/comment/paginateByArticleId`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| articleId | 文章ID | `Long` | 是 | * |  |  
| pageNumber | 分页页码 | `int` | 否 | * | 默认值：1 |  
| pageSize | 每页数据量 | `int` | 否 | * | 默认值：10 |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| page | `Page<ArticleComment>` | 文章评论分页数据 |  

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
				"articleId":100,
				"userId":100,
				"author":"评论的作者",
				"email":"邮箱",
				"wechat":"微信号",
				"qq":"qq号",
				"content":"评论的内容",
				"replyCount":100,
				"orderNumber":100,
				"voteUp":100,
				"voteDown":100,
				"status":"评论的状态",
				"created":"2022-08-30 09:20:32",
				"siteId":100
			},
			{
				"id":100,
				"pid":100,
				"articleId":100,
				"userId":100,
				"author":"评论的作者",
				"email":"邮箱",
				"wechat":"微信号",
				"qq":"qq号",
				"content":"评论的内容",
				"replyCount":100,
				"orderNumber":100,
				"voteUp":100,
				"voteDown":100,
				"status":"评论的状态",
				"created":"2022-08-30 09:20:32",
				"siteId":100
			}
		]
	}
}
```
