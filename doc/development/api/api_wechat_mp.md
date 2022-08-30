# 微信小程序相关API



## Html 转为 Wxml
#### 接口信息：
- 访问路径： `/api/wechat/mp/html2wxml`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| html | html内容 | `String` | 否 | POST |  |  


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


## 微信小程序登录
#### 接口信息：
- 访问路径： `/api/wechat/mp/login`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| code |  | `String` | 是 | * |  |  


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


## 获取当前用户手机号
#### 接口信息：
- 访问路径： `/api/wechat/mp/phoneNumber`
- 数据类型： `application/json`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| json |  | `Map<String,String>` | 是 | POST |  |  


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


## 微信小程序注册当前用户

一般只有登录不成功后进行注册 
#### 接口信息：
- 访问路径： `/api/wechat/mp/register`
- 数据类型： `application/json`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| json |  | `Map<String,String>` | 是 | POST |  |  


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
