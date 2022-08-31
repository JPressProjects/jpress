# 招聘相关API文档



## 岗位详情
#### 接口信息：
- 访问路径： `/api/job/detail`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| id | 岗位ID | `Long` | 否 | * |  |  

> ID必须传入一个值
#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| jobDetail | `Job` | 岗位详情 |  

Job

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| title | `String` | 职位名称或者标题 |  
| content | `String` | 描述 |  
| department | `String` | 对应部门 |  
| categoryId | `Long` | 对应分类id |  
| addressId | `Long` | 工作地点 |  
| ageLimitStart | `Integer` | 年龄开始 |  
| ageLimitEnd | `Integer` | 年龄结束 |  
| education | `Integer` | 学历 |  
| yearsLimitType | `Integer` | 工作年限 |  
| withNotify | `Boolean` | 有建立申请时，是否通知 |  
| notifyEmails | `String` | 通知的邮箱 |  
| notifyTitle | `String` | 通知的邮件标题 |  
| notifyContent | `String` | 通知的邮件内容 |  
| withRemote | `Boolean` | 是否属于远程工作 |  
| withApply | `Boolean` | 是否允许在线投递 |  
| withHurry | `Boolean` | 急招 |  
| workType | `Integer` | 工作类型 |  
| recruitType | `Integer` | 招聘类型 |  
| recruitNumbers | `Integer` | 岗位招聘人数 |  
| expiredTo | `Date` | 岗位有效时间 |  
| metaTitle | `String` | SEO标题 |  
| metaKeywords | `String` | SEO关键字 |  
| metaDescription | `String` | SEO描述信息 |  
| created | `Date` | 创建日期 |  
| modified | `Date` | 最后更新日期 |  
| siteId | `Long` | 站点ID |  

**JSON 示例：**
```json
{
	"jobDetail":{
		"id":100,
		"title":"职位名称或者标题",
		"content":"描述",
		"department":"对应部门",
		"categoryId":100,
		"addressId":100,
		"ageLimitStart":100,
		"ageLimitEnd":100,
		"education":100,
		"yearsLimitType":100,
		"withNotify":true,
		"notifyEmails":"通知的邮箱",
		"notifyTitle":"通知的邮件标题",
		"notifyContent":"通知的邮件内容",
		"withRemote":true,
		"withApply":true,
		"withHurry":true,
		"workType":100,
		"recruitType":100,
		"recruitNumbers":100,
		"expiredTo":"2022-08-30 09:20:32",
		"metaTitle":"SEO标题",
		"metaKeywords":"SEO关键字",
		"metaDescription":"SEO描述信息",
		"created":"2022-08-30 09:20:32",
		"modified":"2022-08-30 09:20:32",
		"siteId":100
	},
	"state":"ok"
}
```


## 创建新岗位
#### 接口信息：
- 访问路径： `/api/job/doCreate`
- 数据类型： `application/json`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| job | 岗位的 JSON 信息 | `Job` | 否 | POST |  |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| id | `Long` | 岗位ID |  

**JSON 示例：**
```json
{
	"state":"ok",
	"id":123
}
```


## 删除岗位
#### 接口信息：
- 访问路径： `/api/job/doDelete`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| id | 岗位ID | `Long` | 是 | * |  |  


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


## 更新岗位
#### 接口信息：
- 访问路径： `/api/job/doUpdate`
- 数据类型： `application/json`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| job | 岗位的 JSON 信息 | `Job` | 否 | POST |  |  


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


## 根据自定义条件查找岗位列表
#### 接口信息：
- 访问路径： `/api/job/listByColumns`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| orderBy | 排序方式 | `String` | 否 | * |  |  
| education | 岗位需要学历 | `Integer` | 否 | * |  |  
| categoryId | 岗位分类ID | `Long` | 否 | * |  |  
| deptId | 岗位部门ID | `Long` | 否 | * |  |  
| addressId | 岗位地区ID | `Long` | 否 | * |  |  
| workYear | 岗位工作年限 | `Integer` | 否 | * |  |  
| workType | 岗位工作类型 | `Integer` | 否 | * |  |  
| recruitmentType | 岗位招聘类型 | `Integer` | 否 | * |  |  
| count | 需要查询的数量 | `Integer` | 否 | * |  |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| list | `List<Job>` | 岗位列表 |  

**JSON 示例：**
```json
{
	"state":"ok",
	"list":[
		{
			"id":100,
			"title":"职位名称或者标题",
			"content":"描述",
			"department":"对应部门",
			"categoryId":100,
			"addressId":100,
			"ageLimitStart":100,
			"ageLimitEnd":100,
			"education":100,
			"yearsLimitType":100,
			"withNotify":true,
			"notifyEmails":"通知的邮箱",
			"notifyTitle":"通知的邮件标题",
			"notifyContent":"通知的邮件内容",
			"withRemote":true,
			"withApply":true,
			"withHurry":true,
			"workType":100,
			"recruitType":100,
			"recruitNumbers":100,
			"expiredTo":"2022-08-30 09:20:32",
			"metaTitle":"SEO标题",
			"metaKeywords":"SEO关键字",
			"metaDescription":"SEO描述信息",
			"created":"2022-08-30 09:20:32",
			"modified":"2022-08-30 09:20:32",
			"siteId":100
		},
		{
			"id":100,
			"title":"职位名称或者标题",
			"content":"描述",
			"department":"对应部门",
			"categoryId":100,
			"addressId":100,
			"ageLimitStart":100,
			"ageLimitEnd":100,
			"education":100,
			"yearsLimitType":100,
			"withNotify":true,
			"notifyEmails":"通知的邮箱",
			"notifyTitle":"通知的邮件标题",
			"notifyContent":"通知的邮件内容",
			"withRemote":true,
			"withApply":true,
			"withHurry":true,
			"workType":100,
			"recruitType":100,
			"recruitNumbers":100,
			"expiredTo":"2022-08-30 09:20:32",
			"metaTitle":"SEO标题",
			"metaKeywords":"SEO关键字",
			"metaDescription":"SEO描述信息",
			"created":"2022-08-30 09:20:32",
			"modified":"2022-08-30 09:20:32",
			"siteId":100
		}
	]
}
```


## 岗位分页读取
#### 接口信息：
- 访问路径： `/api/job/paginate`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| orderBy | 排序方式 | `String` | 否 | * |  |  
| education | 岗位需要学历 | `Integer` | 否 | * |  |  
| categoryId | 岗位分类ID | `Long` | 否 | * |  |  
| deptId | 岗位部门ID | `Long` | 否 | * |  |  
| addressId | 岗位地区ID | `Long` | 否 | * |  |  
| workYear | 岗位工作年限 | `Integer` | 否 | * |  |  
| workType | 岗位工作类型 | `Integer` | 否 | * |  |  
| recruitmentType | 岗位招聘类型 | `Integer` | 否 | * |  |  
| pageNumber | 分页的页码 | `int` | 否 | * | 默认值：1 |  
| pageSize | 每页的数据数量 | `int` | 否 | * | 默认值：10 |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| page | `Page<Job>` | 岗位分页数据 |  

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
				"title":"职位名称或者标题",
				"content":"描述",
				"department":"对应部门",
				"categoryId":100,
				"addressId":100,
				"ageLimitStart":100,
				"ageLimitEnd":100,
				"education":100,
				"yearsLimitType":100,
				"withNotify":true,
				"notifyEmails":"通知的邮箱",
				"notifyTitle":"通知的邮件标题",
				"notifyContent":"通知的邮件内容",
				"withRemote":true,
				"withApply":true,
				"withHurry":true,
				"workType":100,
				"recruitType":100,
				"recruitNumbers":100,
				"expiredTo":"2022-08-30 09:20:32",
				"metaTitle":"SEO标题",
				"metaKeywords":"SEO关键字",
				"metaDescription":"SEO描述信息",
				"created":"2022-08-30 09:20:32",
				"modified":"2022-08-30 09:20:32",
				"siteId":100
			},
			{
				"id":100,
				"title":"职位名称或者标题",
				"content":"描述",
				"department":"对应部门",
				"categoryId":100,
				"addressId":100,
				"ageLimitStart":100,
				"ageLimitEnd":100,
				"education":100,
				"yearsLimitType":100,
				"withNotify":true,
				"notifyEmails":"通知的邮箱",
				"notifyTitle":"通知的邮件标题",
				"notifyContent":"通知的邮件内容",
				"withRemote":true,
				"withApply":true,
				"withHurry":true,
				"workType":100,
				"recruitType":100,
				"recruitNumbers":100,
				"expiredTo":"2022-08-30 09:20:32",
				"metaTitle":"SEO标题",
				"metaKeywords":"SEO关键字",
				"metaDescription":"SEO描述信息",
				"created":"2022-08-30 09:20:32",
				"modified":"2022-08-30 09:20:32",
				"siteId":100
			}
		]
	}
}
```
