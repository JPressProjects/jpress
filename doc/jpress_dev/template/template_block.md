# 模板模块拖拽

在开始 JPress 模板的模块开发之前，需要了解 3 个定义：
- 1、模块容器：就是可以拖动来存放的地方
- 2、模块：一段 html 片段，html 片段所在的 html 文件以 `block_` 开头
- 3、模块设置：对模块里的 html 内容进行动态设置。


在模板的 html 中，`模块容器` 是通过代码 `#blockContainer('容器名称') 默认显示内容 #end` 来定义。例如：

在任意的 html 中，添加如下内容 

```html
<div class="div1">#blockContainer('容器1')  #end</div>
<div class="div2">#blockContainer('容器2')  #end</div>
```

此时，表示我们在模板中定义了两个容器，容器的名称分别为：容器1、容器2。后台显示如下：