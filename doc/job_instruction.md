# job模块指令

#### #jobAddress()指令的用法

此指令是在任何页面,用来读取岗位的地址列表

```html

<div>
    #jobAddress()
    <select>
        #for(item : addressList)
        <option value="#(item.id ??)">#(item.name ??)</option>
        #end
    </select>
    #end
</div>
```

#### #jobCategories()指令的用法

此指令是在任何页面,用来读取岗位的分类列表

```html

<div>
    #jobCategories()
    <select>
        #for(item : categoryList)
        <option value="#(item.id ??)">#(item.name ??)</option>
        #end
    </select>
    #end
</div>
```

#### #jobCategories()中支持的参数有：

* parentId 读取指定父级分类
* isTree 是否以树状的数据格式返回,默认是false,返回全部分类,可通过 item.childs 方式获取子级分类列表

#### #jobDepartments()指令的用法

此指令是在任何页面,用来读取岗位的部门列表

```html

<div>
    #jobDepartments()
    <select>
        #for(item : departmentList)
        <option value="#(item.id ??)">#(item.name ??)</option>
        #end
    </select>
    #end
</div>
```

#### #job()指令的用法

此指令是根据岗位ID来查找岗位信息

```html

<div>
    #job(1)
    <p>#(job.name ??)</p>
    <p>#(job.category.name ??)</p>
    <p>#(job.dept.name ??)</p>
    <p>#(job.address.name ??)</p>
    #end
</div>
```

#### #job()中支持的参数有：

* id：岗位的id，直接输入id即可
* 查询出来的对象中内置了该岗位对应的：
* category 分类对象信息，使用job.category 即可获得
* dept 部门对象信息，使用job.dept 即可获得
* address 地址对象信息，使用job.address 即可获得

#### #jobs()指令的用法

此指令是根据自定义条件来查询岗位列表信息

```html
<div>
    #jobs(categoryId = 1,deptId = 1,addressId = 1,education = 1,workYear = 1,workType = 1,recruitmentType = 1,
    orderBy = "id desc",count = 10)
    #for(item : jobList)
    <p>#(item.name ??)</p>
    <p>#(item.category.name ??)</p>
    <p>#(item.dept.name ??)</p>
    <p>#(item.address.name ??)</p>
    #end
    #end
</div>
```

#### #jobs()中支持的参数有：

* categoryId:岗位分类ID
* deptId：岗位部门ID
* addressId：岗位地区ID
* education：学历 tinyint类型 支持 0~7
* workYear：工作年限 tinyint类型 支持 0~7
* workType：工作类型 tinyint类型 支持 0~3
* recruitmentType：招聘类型 tinyint类型 支持0~3
* orderBy：根据属性进行排序 例如 "id desc"
* count：需要查询的数量
* 此指令查出的对象中，内置了category，dept，address对象

#### #jobPage()指令的用法

此指令 #jobPage() 只能用在岗位列表页

```html

<div>
    #jobPage(pageSize = 1)

    #for(job : jobPage.list)
    <a href="/job/#(job.id ??)">
        岗位标题是：#(job.name ??)
    </a>

    <div>
        岗位的描述是：#maxLength(job.summary,100)
    </div>
    #end

    #jobPaginate()
    #for(page : pages)
    <a class="page-link" href="#(page.url ??)">
        #(page.text ??)
    </a>
    #end
    #end

    #end
</div>
```

> 说明 指令 #jobPage() 内部又包含了另一个指令 #jobPaginate()，#jobPaginate()是用于显示上一页和下一页

#### #jobPage()支持的参数有

* pageSize 用来指定当前页面有多少条数据，默认值为10，也就是不填写这个参数的话，默认为10条数据
* categoryId:岗位分类ID
* deptId：岗位部门ID
* addressId：岗位地区ID
* orderBy 根据属性进行排序 例如 "id desc"
* education：学历 tinyint类型 支持 0~7
* workYear：工作年限 tinyint类型 支持 0~7
* workType：工作类型 tinyint类型 支持 0~3
* recruitmentType：招聘类型 tinyint类型 支持0~3
* 此指令查出的对象中，内置了category，dept，address对象

#### 分页指令#jobPaginate()的参数有

* previousClass ：上一页的样式，默认值：previous
* nextClass ：下一页的样式，默认值：next
* activeClass ：当前页面的样式，默认值：active
* disabledClass ：禁用的样式（当下一页没有数据的时候，会使用此样式），默认值：disabled
* anchor ：锚点链接
* onlyShowPreviousAndNext ：是否只显示上一页和下一页（默认值为false，一般情况下在手机端才会把这个值设置true）
* previousText ：上一页按钮的文本内容，默认值：上一页
* nextText ：下一页按钮的文本内容，默认值：下一页
* firstGotoIndex : 是否让第一页进入首页，默认值：false