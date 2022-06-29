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

#### //TODO