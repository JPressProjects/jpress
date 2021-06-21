# 系统相关的  API

[[toc]]

## 配置相关

## 读取配置

- 访问路径: `/api/option/query`
- 数据类型: `application/x-www-form-urlencoded`
- 参数：
  
  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | key | 是 | GET | 要读取参数配置，多个配置用因为逗号隔开 |  


- 返回数据

    - 当 key 是单个配置的时候：

    ```json
    {"state":"ok","value":"myValue"}
    ```
  
    - 当 key 是多个配置的时候：
  
    ```json
    {"values":{"myKey1":"myValue1","myKey2":"myValue2"},"state":"ok"}
    ```

## 配置设置

- 访问路径: `/api/option/set`
- 数据类型: `application/json`
- 参数：

  | 参数名称 | 是否必须| 提交方式 |描述 |  
  | --- | --- | --- | --- |
  | keyAndValues | 是 | POST | 必须通过 JsonObject 的方式提交 |  

  keyAndValues 的内容如下

  ```json
  {"myKey1":"myValue1","myKey2":"myValue2"}
  ```


- 返回数据

  ```json
  {"state":"ok"}
  ```
