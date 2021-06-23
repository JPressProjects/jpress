# setting页面配置

## setting页面升级

#### 概述

JPress 4.X 以前的模板都需要升级才能在JPress 4.x上正常使用。

#### 升级方法

1、把 setting.html 文件复制一份，命名为 setting_v4.html,2个文件都要保存；

2、按照以下步骤修改setting_v4.html，把 `class="box` 批量替换成 `class="card`，下面的批量替换都是类似，如下图：


3、把 `box-` 批量替换成 `card-`

4、把 `col-xs-` 批量替换成 `col-` 

5、删除 `<div class="content-wrapper"></div>`，只保留里面的内容。

6、把 `class="card card-primary"` 修改为 `class="card card-outline card-primary"`，

7、删除这些代码：

```
<div class="card-header">
  <h3 class="card-title"></h3>
</div>
```


8、把这段代码：

```
<h1>
    模板设置
    <small>Template Setting</small>
</h1>
```

换成这段：

```
<div class="container-fluid">
    <div class="row">
        <div class="col-sm-6">

            <div class="row mb-2">
                <div class="col-sm-12">
                    <h1>
                        模板设置
                        <small data-toggle="tooltip" title="" data-placement="right" data-trigger="hover" data-original-title=""><i class="nav-icon far fa-question-circle"></i></small>
                        <small> 首页 / 模板 / 设置</small>
                    </h1>
                </div>
            </div>
        </div>
    </div>
</div>
```

9、把`class="form-group"`批量替换成`class="form-group row"`

10、把`class="col-sm-2 control-label"`  批量替换成 `class="col-sm-2 col-form-label"`  

11、如果本页面有tab，可以参考如下代码结构：

```
<div class="card card-primary card-outline card-outline-tabs">
   <div class="card-header p-0 border-bottom-0">
      <ul class="nav nav-tabs">
         <li class="nav-item"><a class="nav-link active" href="#tab_pubset" data-toggle="tab">tab1</a></li>
         <li class="nav-item"><a class="nav-link" href="#tab_indexset" data-toggle="tab">tab2</a></li>
         <li class="nav-item"><a class="nav-link" href="#tab_moduleset" data-toggle="tab">tab3</a></li>
      </ul>
   </div>
</div>
```