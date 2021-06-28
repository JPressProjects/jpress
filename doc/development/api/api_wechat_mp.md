# 微信小程序相关API



## Html 转为 Wxml
- 访问路径: `/api/wechat/mp/html2wxml`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | html | html内容 | 否 | POST |  |  



## 微信小程序登录
- 访问路径: `/api/wechat/mp/login`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | code |  | 是 | * |  |  



## 获取当前用户手机号
- 访问路径: `/api/wechat/mp/phoneNumber`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | json |  | 是 | POST |  |  



## 微信小程序注册当前用户
一般只有登录不成功后进行注册 
- 访问路径: `/api/wechat/mp/register`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | json |  | 是 | POST |  |  

