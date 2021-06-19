# JPress API 概述

[[toc]]

## 概述

在开始调用 JPress API 之前，需要在 JPress 后台的 `系统` > `接口` 里填写 `应用ID` 和 `签名秘钥`。并开启 API 功能。

## 通用参数

- jpressAppId ：这个值是在 JPress 后台填写的 `应用ID`，任意填写，但务必要和后台填写的完全一致。
- ct ： 系统的当前时间(current time)，单位：毫秒。
- sign ： URL 数据签名

> 在 JPress 系统中，调用任何 API 都需要传入这 3 个值，而且必须是以 GET 请求的方式进行传入。

## API 签名算法

JPress API 签名是通过 Http 请求的 QueryString 来进行计算的。

例如：`http://127.0.0.1:8080/api/test?jpressAppId=myAppId&ct=123456` 其 queryString 为 `jpressAppId=myAppId&ct=123456`。

- 第一步：将其 queryString 转换为 map 内容如下：

```
jpressAppId:myAppId
ct:123456
```

第二步：对 map 中的 key 进行排序得到的内容为：

```
[ct,jpressAppId]
```

第三步：使用排序后的 key 值和 value 值进行拼接得到的结果为：

```
ct123456jpressAppIdmyAppId
```

第四步：在拼接的字符串结尾添加上秘钥（秘钥在 JPress 的后台设置，假设秘钥为：`mySecret`）：

```
ct123456jpressAppIdmyAppIdmySecret
```

最后一步：对结果 `ct123456jpressAppIdmyAppIdmySecret` 进行 `MD5` 加密，得到的结果为：

```
d6236dd691926803aec8c79101251e29
```

> 注意：如果 URL 的 queryString 中的值为空，则不对其进行签名计算。


Java 代码：

```java
public static String createLocalSign(HttpServletRequest request) {
    String queryString = request.getQueryString();
    Map<String,String> queryParas = StrUtil.queryStringToMap(queryString);
    String[] keys = queryParas.keySet().toArray(new String[0]);;
    Arrays.sort(keys);

    StringBuilder sb = new StringBuilder();
    for (String key : keys){
        if ("sign".equals(key)){
            continue;
        }
    
        String value = queryParas.get(key);
        if(StrUtil.isNotBlank(value)){
            sb.append(key).append(value);
        }
    }

    return HashKit.md5(sb.append(apiSecret).toString());
}
```

JavaScript 代码：

```javascript
const sign = paras => {

    var secret = config.api_secret;

    //生成key升序数组
    var arr = Object.keys(paras);
    arr.sort();

    var str = '';
    for (var i in arr) {
        var value = paras[arr[i]];
        if (value) str += arr[i] + value.toString();
    }

    return md5(str + secret);
}
```