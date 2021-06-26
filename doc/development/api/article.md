# 文章模块 API 接口



## 文章详情

- 访问路径: `/api/article/detail`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | id | 否 | GET | 文章ID |  
  | slug | 否 | GET | 文章固定连接 |  

> id 或者 slug 必须传入一个值。



- 返回数据

  ```json
  {"state":"ok",
    "detail":{
      "id":1,
      "title":"文章标题",
      "...."
    }
  }
  ```

## 获取文章分类

- 访问路径: `/api/article/category/list`
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
    "list":[
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



## 获取单个文章分类

- 访问路径: `/api/article/category/detail`
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
    "detail":{
      "pid":0,
      "title":"分类1",
      "id":1
    }
  }
  ```  

## 分页查询文章

- 访问路径: `/api/article/paginate`
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
  > 备注： `page.list` 里的内容是文章内容。

## 根据文章分类获取文章列表

- 访问路径: `/api/article/listByCategoryId`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | categoryId | 是 | GET | 分类的 id |  
  | count | 否 | GET | 默认值 10 |  


## 根据文章分类的 Tag 获取文章列表

- 访问路径: `/api/article/listByCategoryTag`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | categoryTag | 是 | GET | 分类的 id |  
  | count | 否 | GET | 默认值 10 |  


## 根据文章标识获取文章

- 访问路径: `/api/article/listByFlag`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | flag | 否 | GET | 分类的 文章标识 |  
  | hasThumbnail | 否 | GET | 是否必须有缩略图 |  
  | orderBy | 否 | GET | 排序方式 |  
  | count | 否 | GET | 获取数据量，默认值 10 |  


## 获取某一篇文章的相关文章

- 访问路径: `/api/article/listByRelevant`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | articleId | 是 | GET | 当前的文章 |  
  | count | 否 | GET | 获取数据量，默认值 3 |  


## 文章搜索

- 访问路径: `/api/article/search`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | keyword | 是 | GET | 搜索关键字 |  
  | pageNumber | 否 | GET | 默认值 1 |  
  | pageSize | 否 | GET | 默认值 10 |  


## 根据文章标识获取文章

- 访问路径: `/api/article/listByFlag`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
    | --- | --- | --- | --- |
  | flag | 否 | GET | 分类的 文章标识 |  
  | hasThumbnail | 否 | GET | 是否必须有缩略图 |  
  | orderBy | 否 | GET | 排序方式 |  
  | count | 否 | GET | 获取数据量，默认值 10 |  


## 发布新文章

- 访问路径: `/api/article/doCreate`
- 数据类型: `application/json`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | data | 是 | POST | 文章 JSON |  


## 更新文章

- 访问路径: `/api/article/doUpdate`
- 数据类型: `application/json`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | data | 是 | POST | 文章 JSON |  



## 获取文章评论

- 访问路径: `/api/article/comment/paginate`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | articleId | 是 | GET | 文章id |  
  | pageNumber | 否 | GET | 默认值 1 |  
  | pageSize | 否 | GET | 默认值 10 |  




## 发布文章评论

- 访问路径: `/api/article/comment/post`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | articleId | 是 | POST | 文章 ID |  
  | pid | 是 | POST | 父级评论ID |  
  | content | 是 | POST | 评论内容 |  

