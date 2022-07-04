# 模板模块开发

在开始 JPress 模板的模块开发之前，需要了解 3 个定义：
- 1、模块容器：就是可以拖动来存放的地方
- 2、模块：一段 html 片段，定义在以 `block_` 开头的 html 文件里 
- 3、模块设置：可以设置模块里的 html 内容。


在模板的 html 中，模块容器是通过 `#blockContainer('容器名称')` 的方式来定义。例如：

index.html

```html
<div class="div1">#blockContainer('容器1')</div>
<div class="div2">#blockContainer('容器2')</div>
```

此时，我们在 `index.html` 中定义了两个容器，名称分别为：容器1、容器2。