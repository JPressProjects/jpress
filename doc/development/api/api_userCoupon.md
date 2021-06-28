# 优惠券相关API



## 创建新的优惠码
- 访问路径: `/api/userCoupon/doCreate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | couponCode | 优惠码 JSON 信息 | 否 | POST |  |  



## 删除优惠码
- 访问路径: `/api/userCoupon/doDelete`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | id | 优惠码ID | 否 | * |  |  



## 改变优惠码信息
- 访问路径: `/api/userCoupon/doUpdate`
- 数据类型: `application/json`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | couponCode | 优惠码 JSON 信息 | 否 | * |  |  



## 查询可用的优惠券
- 访问路径: `/api/userCoupon/listByUserId`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | userId | 用户ID | 是 | * |  |  



## 查询可用的优惠券
- 访问路径: `/api/userCoupon/listByUserIdAndPrice`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数 | 名称 | 是否必须 | 提交方式 | 描述 |  
  | --- | --- | --- | --- | --- |
  | userId | 用户ID | 是 | * |  |  
  | price | 消费的订单价格 | 是 | * |  |  

