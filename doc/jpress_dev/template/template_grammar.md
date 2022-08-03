# JPress 的模板语法

在学习 JPress 之前，有必要了解下 JPress 自带的模板语法功能，因为 JPress 使用的是 JFinal Enjoy 模板引擎，更多的模板语法也可以参考 JFinal 的官方文档：[https://www.jfinal.com/doc/6-1](https://www.jfinal.com/doc/6-1)

[[toc]]

## 输出内容到网页

```
#(key)
```
输出 JPress 后台内容 `key` 变量的值。此时，如果当 `key` 为 null 的时候，会出现模板异常。

## 安全输出内容到网页

```
#(key ??)
```
当 `key` 为 null 的时候，不输出任何内容。


## 默认值输出

```
#(key ?? "my_default_value")
```
当 `key` 为 null 的时候，输出 `my_default_value`。


## 三目运算值输出

```
#(key ? "value1" : "value2")
```
当 `key` 为 null 或者 false 的时候，输出 `value1`，否则会出输出 `value2`。

## 日期输出

```
#date(account.createAt)
#date(account.createAt, "yyyy-MM-dd HH:mm:ss")
```
上面的第一行代码只有一个参数，那么会按照默认日期格式进行输出，默认日期格式为：`yyyy-MM-dd HH:mm`。
上面第二行代码则会按第二个参数指定的格式进行输出。


## if 判断

```
#if( cond )
  ...
#end
```
if 指令需要一个 `cond` 表达式作为参数，并且以 `#end` 为结尾符，当 `cond` 求值为 `true` 时，执行 if 分支之中的代码。

>备注：当 cond 为 null 的时候，等同于 cond = false


## if...else...判断
```
#if(cond1)
  ...
#elseif(cond2)
  ...
#elseif (cond3)
  ...
#else
  ...
#end
```

## switch 判断

```
#switch (month)
  #case (1, 3, 5, 7, 8, 10, 12)
    #(month) 月有 31 天
  #case (2)
    #(month) 月平年有28天，闰年有29天
  #default
    月份错误: #(month ?? "null")
#end
```

## for 循环

Enjoy Template Engine 对 for 指令进行了极为人性化的扩展，可以对任意类型数据进行迭代输出，包括支持 null 值迭代。以下是代码示例：

```
#for(x : list)
  #(x.field)
#end
 
#for(x : map)
  #(x.key)
  #(x.value)
#end
```
以上代码中展示了for指令迭代输出。第一个for指令是对list进行迭代输出；第二个for指令是对map进行迭代，取值方式为item.key与item.value。

**获取 for 循环的位置**

```
#for(x : listAaa)
   #(for.size)    被迭代对象的 size 值
   #(for.index)   从 0 开始的下标值
   #(for.count)   从 1 开始的记数值
   #(for.first)   是否为第一次迭代
   #(for.last)    是否为最后一次迭代
   #(for.odd)     是否为奇数次迭代
   #(for.even)    是否为偶数次迭代
   #(for.outer)   引用上层 #for 指令状态
#end
```

## 更多
关于**模板语法**更多的指令和用法，请参考 JFinal 的官方文档 ： [https://www.jfinal.com/doc/6-4](https://www.jfinal.com/doc/6-4) 。

		





