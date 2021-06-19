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