# 订单相关API



## 创建订单
#### 接口信息：
- 访问路径： `/api/userOrder/doCreate`
- 数据类型： `application/json`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| userOrder | 订单的 JSON 信息 | `UserOrder` | 是 | POST |  |  


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


## 删除订单
#### 接口信息：
- 访问路径： `/api/userOrder/doDelete`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| id | 订单ID | `Long` | 是 | * |  |  


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


## 更新订单
#### 接口信息：
- 访问路径： `/api/userOrder/doUpdate`
- 数据类型： `application/json`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| userOrder | 订单的 JSON 信息 | `UserOrder` | 是 | POST |  |  


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


## 分页查询用户订单
#### 接口信息：
- 访问路径： `/api/userOrder/paginateByUserId`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| userId | 用户ID | `Long` | 是 | * |  |  
| title | 订单标题 | `String` | 否 | * | 一般用户搜索 |  
| ns | 订单号 | `String` | 否 | * | 一般用户搜索 |  
| pageNumber | 页码 | `int` | 否 | * | 默认值：1 |  
| pageSize | 每页数据量 | `int` | 否 | * | 默认值：10 |  


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
