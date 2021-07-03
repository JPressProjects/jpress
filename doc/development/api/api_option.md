# 系统配置相关 API 



## 查询配置
#### 接口信息：
- 访问路径： `/api/option/query`
- 数据类型： `application/x-www-form-urlencoded`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| key | 查询的 key | `String` | 是 | * | 多个 key 用引文逗号隔开 |  


#### 数据响应：`Ret`

Ret

| 字段  | 数据类型 | 描述 |  
| --- | --- | --- | 
| state | `String` | 状态，成功 ok，失败 fail |  
| value | `String` | 查询单个配置的时候返回的数据 |  
| values | `Map` | 查询多个key的时候返回数据 |  

**JSON 示例：**
```json
{
	"values":{
		"key1":"value1",
		"key2":"value2"
	},
	"state":"ok",
	"value":"..."
}
```


## 更新配置
#### 接口信息：
- 访问路径： `/api/option/set`
- 数据类型： `application/json`
#### 请求参数：

| 参数 | 名称 | 数据类型 | 是否必须 | 提交方式 | 描述 |  
| --- | --- | --- | --- | --- | --- |
| keyValues | 要更新的数据 map | `Map<String,String>` | 是 | POST |  |  


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
