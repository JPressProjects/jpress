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

**option支持的参数：**

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

**linesOption支持的参数：**

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

**para支持的参数：**

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

**numberPara支持的参数：**

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

**contains支持的参数：**

- 参数一：需要判断的集合
- 参数二：需要判断的元素

------

## hasPermission

* 判断是否拥有权限

**示例代码**

```html

<div>
    <!-- 可传入 role 对象 和 permissionId 进行查询判断-->
    #(hasPermission(role,permissionId))

    <!-- 可传入 user 对象 和 permissionId 进行查询判断-->
    #(hasPermission(user,permissionId))

    <!-- 也可以只传入 actionKey 进行查询判断-->
    #(hasPermission(actionKey))
</div>
```

输出： `<div> false </div>`

**hasPermission支持的参数：**

- 参数一：可传入 role 对象 或者 user 对象 或者直接传入 actionKey
- 参数二：permissionId 当参数一为 actionKey 时 参数二为空

------

## hasRole

* 判断是否拥有角色

**示例代码**

```html

<div>

    <!-- 可传入 roleId 进行查询判断-->
    #(hasRole(roleId))

    <!-- 可传入 userId 和 roleId 进行查询判断-->
    #(hasRole(userId,roleId))
    
    <!-- 也可以传入 role 的标识 进行查询判断-->
    #(hasRole(roleFlag))
    
</div>
```
输出： `<div> false </div>`

**hasRole支持的参数：**

- 参数一：可传入 roleId  或者 userId 或者 roleFlag
- 参数二：当参数一为 userId 时 需要传入参数二 参数二为 roleId

------

## isSupperAdmin

* 判断当前用户是否是超级管理员

**示例代码**

```html

<div>
    
    #(isSupperAdmin())
    
</div>
```
输出： `<div> true </div>`

------

## blockOption

* 根据 key 读取后台模板 -> 板块里的配置
* 只能使用在 block_ 开头的 html 文件里。

**示例代码**

```html

<div>
    
    #(blockOption(key))
    
</div>
```
输出： `<div> null </div>`

------