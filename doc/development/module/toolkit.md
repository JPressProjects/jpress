## 工具箱配置
* 1、工具箱
> 可以看到 在后台 系统管理下 有个小工具箱 的选项 在工具箱中 有几个 已经存在了的 小工具

![img.png](../image/module/module_23.png)

* 2、添加小工具
> 那么怎么添加 自己的工具呢?
>
> 首先我们需要有一个对应的 html 文件 然后 需要找到对应的 模块 中的 **JobModuleInitializer 类 -> onRenderToolsBox()方法**
>
> html 示例如下

```html

<div class="col-lg-3 col-xs-6">
    <div class="small-box bg-blue">
        <div class="inner">
            <p>Jpress 岗位导入</p>
        </div>
        <div class="icon">
            <i class="fa fa-fw fa-book"></i>
        </div>
        <a href="#" class="small-box-footer">
            开始使用 <i class="fa fa-arrow-circle-right"></i>
        </a>
    </div>
</div>
```
![img.png](../image/module/module_24.png)

> 如果页面中需要数据的话 可以查询数据并 设置数据 这里就直接返回

![img.png](../image/module/module_25.png)

> 然后重启项目 并进入后台 查看工具箱 可以看到 工具已经添加成功

![img.png](../image/module/module_26.png)