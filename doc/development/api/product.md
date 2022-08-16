# 商品模块 API 接口


## 商品详情

- 访问路径: `/api/product/detail`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
    | --- | --- | --- | --- |
  | id | 否 | GET | 商品ID |  
  | slug | 否 | GET | 商品固定连接 |  

> id 或者 slug 必须传入一个值。


## 获取商品分类

- 访问路径: `/api/product/categories`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
    | --- | --- | --- | --- |
  | type | 是 | GET | 分类的类型：`category` 或 `tag` |  
  | pid | 否 | GET | 父级分类 |  

> id 或者 slug 必须传入一个值。

- 返回数据

  ```json
  {"state":"ok",
    "categories":[
      {"pid":0,
        "title":"分类1",
        "id":1,
        "...."
      },
      {"pid":0,
      "title":"分类2",
      "id":2,
      "...."
      }
    ]
  }
  ```



## 获取单个商品分类

- 访问路径: `/api/product/category`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
    | --- | --- | --- | --- |
  | id | 否 | GET | 分类的 id |  
  | slug | 否 | GET | 分类的固定连接 |  
  | type | 否 | GET | 分类的类型：`category` 或 `tag` |  


> id 或者 slug 必须传入一个值。

- 返回数据

  ```json
  {
  "state":"ok",
    "category":{
      "pid":0,
      "title":"分类1",
      "id":1
    }
  }
  ```  

## 分页查询商品

- 访问路径: `/api/product/paginate`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
    | --- | --- | --- | --- |
  | categoryId | 否 | GET | 分类的 id |  
  | orderBy | 否 | GET | 排序的方式，比如：'id desc' |  
  | pageNumber | 否 | GET | 当前页码，当不传入的时候，默认值为：1 |  

> id 或者 slug 必须传入一个值。

- 返回数据

  ```json
  {
    "state":"ok",
    "page":{
      "list":[ 
        {
          "id":100,
          "status":"normal"
        }
      ],
      "pageNumber":1,
      "pageSize":1,
      "totalPage":1,
      "totalRow":1,
      "firstPage":true,
      "lastPage":true
    }
  }
  ```
  > 备注： `page.list` 里的内容是商品内容。

## 根据商品分类获取商品列表

- 访问路径: `/api/product/productsByCategoryId`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
    | --- | --- | --- | --- |
  | categoryId | 是 | GET | 分类的 id |  
  | count | 否 | GET | 默认值 10 |  


## 根据商品分类的 Tag 获取商品列表

- 访问路径: `/api/product/productsByCategoryTag`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
    | --- | --- | --- | --- |
  | categoryTag | 是 | GET | 分类的 id |  
  | count | 否 | GET | 默认值 10 |  


## 根据商品标识获取商品

- 访问路径: `/api/product/productsByFlag`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
    | --- | --- | --- | --- |
  | flag | 否 | GET | 分类的 商品标识 |  
  | hasThumbnail | 否 | GET | 是否必须有缩略图 |  
  | orderBy | 否 | GET | 排序方式 |  
  | count | 否 | GET | 获取数据量，默认值 10 |  


## 获取某一篇商品的相关商品

- 访问路径: `/api/product/productsByRelevant`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
    | --- | --- | --- | --- |
  | productId | 是 | GET | 当前的商品 |  
  | count | 否 | GET | 获取数据量，默认值 3 |  


## 商品搜索

- 访问路径: `/api/product/search`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
    | --- | --- | --- | --- |
  | keyword | 是 | GET | 搜索关键字 |  
  | pageNumber | 否 | GET | 默认值 1 |  
  | pageSize | 否 | GET | 默认值 10 |  


## 根据商品标识获取商品

- 访问路径: `/api/product/productsByFlag`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
      | --- | --- | --- | --- |
  | flag | 否 | GET | 分类的 商品标识 |  
  | hasThumbnail | 否 | GET | 是否必须有缩略图 |  
  | orderBy | 否 | GET | 排序方式 |  
  | count | 否 | GET | 获取数据量，默认值 10 |  


## 发布新商品

- 访问路径: `/api/product/create`
- 数据类型: `application/json`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
    | --- | --- | --- | --- |
  | data | 是 | POST | 商品 JSON |  


## 更新商品

- 访问路径: `/api/product/update`
- 数据类型: `application/json`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
    | --- | --- | --- | --- |
  | data | 是 | POST | 商品 JSON |  



## 获取商品评论

- 访问路径: `/api/product/comment/paginate`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
    | --- | --- | --- | --- |
  | productId | 是 | GET | 商品id |  
  | pageNumber | 否 | GET | 默认值 1 |  
  | pageSize | 否 | GET | 默认值 10 |  




## 发布商品评论

- 访问路径: `/api/product/comment/post`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
    | --- | --- | --- | --- |
  | productId | 是 | POST | 商品 ID |  
  | pid | 是 | POST | 父级评论ID |  
  | content | 是 | POST | 评论内容 |  

