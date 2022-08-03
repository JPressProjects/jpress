# JPress 的全局方法

[[toc]]

## escape

* 字符转义

**示例代码**

```html
<div>
#(escape("<h1>test</h1>"))
</div>
```
输出： `<div> &lt;h1&gt;test&lt;/h1&gt;</div>`

------

## unescape

* 字符转义

**示例代码**
```html
<div>
#(unescape("&lt;h1&gt;test&lt;/h1&gt;"))
</div>
```
输出： `<div> <h1>test</h1></div>`

------

## hasAddon

* 插件是否安装

**示例代码**

```html
<div>
    <!--可配和其它指令一同使用-->
    #if(hasAddon("xxxx"))
    ...
    #end

    <!--也可这样 直接输出结果-->
    #(hasAddon("xxx"))
</div>
```
输出： `<div> false </div>`

------

## option

* 根据 key 获取 option 的 value 数据

**示例代码**

```html
<div>
    #(option("web_template","defaulValue",1))
</div>
```
输出： `<div> jpress.cn </div>`

### option支持的参数：
- 参数一：key
- 参数二：默认值，当通过key查询不到数据时，使用默认值
- 参数三：站点 ID 可指定站点 ID 进行查询


------

## linesOption

* 根据 key 获取 option 的 多个value 数据
* //TODO

**示例代码**

```html
<div>
    #(linesOption("web_template",","))
</div>
```
输出： `<div> [jpress.cn] </div>`

### linesOption支持的参数：
- 参数一：key
- 参数二：分隔符 可以指定分隔符 返回分割后的 list

------

## isImage

* 根据 路径 判断该路劲下的资源 是否是图片

**示例代码**

```html
<div>
    #(isImage("www.baidu.com"))
</div>
```
输出： `<div> false </div>`

------

## para

* 根据 设定 的 key 获取 对应的 value

**示例代码**

```html
<div>
    #(para("key","defaultValue"))
</div>
```
输出： `<div> value </div>`

### para支持的参数：
- 参数一：key
- 参数二：默认值，当通过 key 查询不到数据时，使用默认值

------

## numberPara

* 根据 设定 的 key 获取 对应的 value 并将 value 输出为 long 类型
* 此处需要注意类型的转换 比如 一个 字符串 并且该字符串 不是 number类型 那么会报转换异常 

**示例代码**

```html
<div>
    #(numberPara("key",5))
</div>
```

输出： `<div> 5 </div>`

### numberPara支持的参数：
- 参数一：key
- 参数二：默认值，当通过 key 查询不到数据时，使用默认值

------

## contains

* 判断一个 集合中 是否包含某个元素

**示例代码**

```html
<div>
    #(contains(array[],value))
</div>
```
输出： `<div> false </div>`

### numberPara支持的参数：
- 参数一：需要判断的集合
- 参数二：需要判断的元素

------

