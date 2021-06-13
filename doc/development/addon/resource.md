# JPress 的插件资源

一个完整的 JPress 除了包含 Java 代码以外，还会有很多的资源文件，比如 html、css、js、图片等等。

在 html 模板中，我们可能引用了插件下某个 js 、css、或者图片，然而，当插件被安装之后，其路径是不确定的，所以 JPress 提供了一个全局变量 `APATH`，
用来输出当前插件 webapp 的资源目录。

所以，插件里，html 如果要引用插件自身的 css、js、图片的静态资源的时候，代码应该如下：

```html
<script src="#(CPATH)#(APATH)/static/js/your-addon.js"></script>
<script src="#(CPATH)#(APATH)/static/js/your-addon.min.js"></script>
<link href="#(CPATH)#(APATH)/static/css/your-addon.min.css" rel="stylesheet" type="text/css">
```

- CPATH 指的是 JPress 安装在 tomcat 时的二级目录
- APATH 指的是插件被安装的静态资源目录

> 图片的 src 属性也应该添加上 `#(CPATH)#(APATH)` 前缀。




