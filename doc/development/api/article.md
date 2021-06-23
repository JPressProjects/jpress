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
    "article":{
      "id":1,
      "title":"文章标题",
      "...."
    }
  }
  ```

## 获取文章分类

- 访问路径: `/api/article/categories`
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



## 获取单个文章分类

- 访问路径: `/api/article/category`
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