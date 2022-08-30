# 用户相关API



## 创建新的用户
#### 接口信息：
- 访问路径： `/api/user/create`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| user | 用户 json 信息 | `User` | 是 | POST |  |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| userId | `Long` | 用户ID，用户创建成功后返回此数据 |  

**JSON 示例：**
```json
{
	"state":"ok",
	"userId":""
}
```


## 用户详情
#### 接口信息：
- 访问路径： `/api/user/detail`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| id | 用户ID | `Long` | 是 | * |  |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| user | `User` | 用户信息 |  

User

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| id | `Long` | 主键ID |  
| username | `String` | 登录名 |  
| nickname | `String` | 昵称 |  
| realname | `String` | 实名 |  
| identity | `String` | 身份 |  
| password | `String` | 密码 |  
| salt | `String` | 盐 |  
| anonym | `String` | 匿名ID |  
| email | `String` | 邮件 |  
| emailStatus | `String` | 邮箱状态（是否认证等） |  
| mobile | `String` | 手机电话 |  
| mobileStatus | `String` | 手机状态（是否认证等） |  
| gender | `String` | 性别 |  
| signature | `String` | 签名 |  
| birthday | `Date` | 生日 |  
| company | `String` | 公司 |  
| occupation | `String` | 职位、职业 |  
| address | `String` | 地址 |  
| zipcode | `String` | 邮政编码 |  
| site | `String` | 个人网址 |  
| graduateschool | `String` | 毕业学校 |  
| education | `String` | 学历 |  
| avatar | `String` | 头像 |  
| idcardtype | `String` | 证件类型：身份证 护照 军官证等 |  
| idcard | `String` | 证件号码 |  
| remark | `String` | 备注 |  
| status | `String` | 状态 |  
| created | `Date` | 创建日期 |  
| createSource | `String` | 用户来源（可能来之oauth第三方） |  
| logged | `Date` | 最后的登录时间 |  
| activated | `Date` | 激活时间 |  

**JSON 示例：**
```json
{
	"state":"ok",
	"user":{
		"id":100,
		"username":"登录名",
		"nickname":"昵称",
		"realname":"实名",
		"identity":"身份",
		"password":"密码",
		"salt":"盐",
		"anonym":"匿名ID",
		"email":"邮件",
		"emailStatus":"邮箱状态（是否认证等）",
		"mobile":"手机电话",
		"mobileStatus":"手机状态（是否认证等）",
		"gender":"性别",
		"signature":"签名",
		"birthday":"2022-08-30 09:20:32",
		"company":"公司",
		"occupation":"职位、职业",
		"address":"地址",
		"zipcode":"邮政编码",
		"site":"个人网址",
		"graduateschool":"毕业学校",
		"education":"学历",
		"avatar":"头像",
		"idcardtype":"证件类型：身份证 护照 军官证等",
		"idcard":"证件号码",
		"remark":"备注",
		"status":"状态",
		"created":"2022-08-30 09:20:32",
		"createSource":"用户来源（可能来之oauth第三方）",
		"logged":"2022-08-30 09:20:32",
		"activated":"2022-08-30 09:20:32"
	}
}
```


## 用户登录
#### 接口信息：
- 访问路径： `/api/user/login`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| loginAccount | 登录账户 | `String` | 是 | POST | 可以是邮箱 |  
| password | 登录密码 | `String` | 是 | POST |  |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| Jwt | `String` | Jwt 的 token 信息 |  

**JSON 示例：**
```json
{
	"Jwt":"ey1NiJ9.eyJpYX0ifQ.Y3p4akomy4",
	"state":"ok"
}
```


## 更新用户信息
#### 接口信息：
- 访问路径： `/api/user/update`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| user | 用户 json 信息 | `User` | 是 | POST |  |  


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


## 更新用户密码
#### 接口信息：
- 访问路径： `/api/user/updatePassword`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| userId | 用户ID | `Long` | 是 | POST |  |  
| newPassword | 用户新密码 | `String` | 是 | POST |  |  
| oldPassowrd | 用户旧密码 | `String` | 否 | POST | 如果登录用户是超级管理员，则可以不输入密码 |  


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
