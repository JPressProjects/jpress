# 模板指令

[[toc]]

## 系统指令：

| 指令名称 | 可用页面 |描述 |  
| --- | --- | --- | 
| #checkedIf() | 任意 | 用于输出 "checked" 字符内容，用于在 checkbox 等标签属性里 |  
| #selectedIf() | 任意 | 用于输出 "selected" 字符内容，用于在 select 下拉菜单属性里 | 
| #maxLength() | 任意 | 用于对字符串内容截取 | 
| #hasAddon() | 任意 | 用于检测是否安装、并启用了某个插件 | 
| #para() | 任意 | 用于接收 url 的参数内容 | 
| #option() | 任意 | 读取后台的配置信息 | 

### #checkedIf() 的使用

* checkedIf()  主要用在 checkbox 或者 radio 等标签属性里，支持的参数只能有 true 和 false。

示例：

```html
<input type="radio" name="..." value="text" #checkedIf(type !=null && type== "text")>
```

* 此时，当 后端输出的 `type` 值等于 “text” 的时候，该单选框就会被选中。

------

### #selectedIf() 的使用

* 其用法与 ` #checkedIf()`  一直，只是 #checkedIf() 用在 `<option><select ...></option>` 里。

------

### #maxLength() 的使用

* maxLength()主要用于限制一些文本的输出长度

例如：

```html

<td>#maxLength(entry.text ??,10)</td>
```

* 此时 不管 entry.text 的文本长度又多少 都只会输出 10 个字符 比如有 100 个字符 但是只会输出 10个字符

------

### #hasAddon() 的使用

* 用于检测是否安装、并启用了某个插件

示例：

```html

<div>

    <!--可以这样直接使用-->
    #hasAddon("key") #end

    <!--可以这样将结果输出-->
    #(hasAddon("key"))

    <!--也可以搭配这其它指令一起使用-->
    #if(hasAddon("key"))
    ...
    #end

</div>

```

------

### #para() 的使用

* 用于接收 url 的参数内容

示例：

```html

<div>

    <p>#para("username")</p>

</div>

```

* 此时 如果当前页面的 URL 是 www.jpress.cn?username="zhangsan" 那么就会获取并输出 zhangsan

------

### #option() 的使用

* 读取后台的配置信息

示例：

```html

<div>

    <p>#option("web_template")</p>

</div>

```

* 此时 如果在后台有配置过 key 为 web_template 的信息 那么就能得到对应的 value

------

## 文章相关指令：

| 指令名称 | 可用页面 |描述 |  
| --- | --- | --- | 
| #article() | 任意 | 用于读取特定的单篇文章 |  
| #articles() | 任意 | 用于读取文章列表，例如：热门文章文章、最新评论文章列表等等 | 
| #tagArticles() | 任意 | 读取某个tag下的文章列表 | 
| #categoryArticles() | 任意 | 读取某个分类下的文章列表 | 
| #articlePage() | 任意 | 用于对文章列表进行的内容和分页进行显示 | 
| #commentPage() | 文章详情：article.html | 用于对文章评论的内容和分页进行显示 | 
| #nextArticle() | 文章详情：article.html | 下一篇文章 | 
| #previousArticle() | 文章详情：article.html | 上一篇文章 | 
| #relevantArticles() | 文章详情：article.html | 相关文章列表，相同标签的的文章 |
| #categories() | 任意 | 读取文章模块的所有分类 | 
| #tags() | 任意 | 用于读取文章标签 |  
| #articleCategories() | 任意 | 用于读取某一篇文章的所属分类，例如：文章的标签、文章的分类等 |  
|#comments()|任意|读取所有评论|
|#userArticles()|任意|读取某个用户的投稿文章内容|
|#articleSearchPage()|搜索结果:artsearch.html|用户显示搜索结果|

### #article() 的使用

* 根据文章 ID 或者 slug 读取文章数据

示例：

```html

<div>

    #article("1")

    #(article.title ??)

    #end

</div>

```

> 此时 会根据 ID 查询文章信息 并返回一个名为 article 的对象

#### #article() 中支持的参数：

* 参数一：文章ID 或者 slug

------

### #articles() 的使用

* 查询文章列表

示例：

```html

<div>
    #articles(flag = "xxx",style = "xxx",hasThumbnail = false,orderBy = "id desc",count = 1)

    #for(article :articles)

    #(article.title ??)

    #end

    #end
</div>
```

> 此时 会根据提供的条件查询文章信息 并返回一个名为 articles 的 list 的对象

#### #article() 中支持的参数：

* flag：文章 flag(标识)
* style：文章 style(样式)
* hasThumbnail：是否有缩略图 boolean类型
* orderBy：根据属性进行排序 例如 "id desc"
* count：需要查询的数量

------

### #tagArticles() 的使用

* 根据文章分类的 slug 查询文章列表

示例：

```html

<div>
    #tagArticles(tag="jpress",hasThumbnail = false,orderBy = "id desc",count = 1)

    #for(article : articles)

    #(article.title ??)

    #end

    #end
</div>
```

> 此时 会根据提供的条件查询文章信息 并返回一个名为 articles 的 list 的对象

#### #article() 中支持的参数：

* tag：文章分类的 slug
* hasThumbnail：是否有缩略图 boolean类型
* orderBy：根据属性进行排序 例如 "id desc"
* count：需要查询的数量

------

### #categoryArticles() 的使用

* 读取某个分类下的文章列表

示例：

```html

<div>
    #categoryArticles(categoryId = 1,categoryFlag = "jpress",hasThumbnail = false,orderBy="id desc",count = 1)

    #for(article : articles)

    #(article.title ??)

    #end

    #end
</div>
```

> 此时 会根据提供的条件查询文章信息 并返回一个名为 articles 的 list 的对象

#### #categoryArticles() 中支持的参数：

* categoryId：文章分类的ID
* categoryFlag：文章分类的标识
* hasThumbnail：是否有缩略图 boolean类型
* orderBy：根据属性进行排序 例如 "id desc"
* count：需要查询的数量

------

### #articlePage() 的使用

* 用于对文章列表进行的内容和分页进行显示

示例：

```html
#articlePage()
#for(article : articlePage.list)

<div class="ll-card">
    <div class="row">
        <div class="col-lg-3 col-md-3 col-sm-12 ll-card-image">
            <a href="#">
                <img src="#(article.thumbnail ?? 'img/default-img.jpg')" class="img-fluid" alt="#(article.title ??)">
            </a>
        </div>
        <div class="col-lg-9 col-md-9 col-sm-12 ll-card-main">
            <a href="#(article.url ??)">
                <div>
                    <h3 class="ll-card-main-title">
                        #(article.title ??)
                    </h3>
                    <p class="ll-card-main-info">
                        #maxLength(article.text,100)
                    </p>
                </div>
                <div class="ll-card-tag">
                    <div><i class="fa fa-clock-o"></i>#date(article.created)</div>
                    <div><i class="fa fa-eye"></i>#(article.view_count)</div>
                    <div><i class="fa fa-commenting-o"></i>#(article.comment_count)</div>
                </div>
            </a>
        </div>
    </div>
</div>
#end


#articlePaginate(firstGotoIndex=true)
<nav aria-label="Page navigation example">
    <ul class="pagination justify-content-center ">
        #for(page : pages)
        <li class="page-item #(page.style ??)">
            <a class="page-link" href="#(page.url ??)">
                #(page.text ??)
            </a>
        </li>
        #end
    </ul>
</nav>
#end

#end
```

> 此时 会根据提供的条件查询文章信息 并返回一个名为 articlePage 的 page 的对象 内部又包含了另一个指令 #articlePaginate()，#articlePaginate()是用于显示上一页和下一页

#### #articlePage() 支持的参数有

* categoryId 文章分类的ID
* pageSize 用来指定当前页面有多少条数据，默认值为10，也就是不填写这个参数的话，默认为10条数据
* orderBy 根据属性进行排序 例如 "id desc"

#### 分页指令 #articlePaginate() 的参数有

* previousClass ：上一页的样式，默认值：previous
* nextClass ：下一页的样式，默认值：next
* activeClass ：当前页面的样式，默认值：active
* disabledClass ：禁用的样式（当下一页没有数据的时候，会使用此样式），默认值：disabled
* anchor ：锚点链接
* onlyShowPreviousAndNext ：是否只显示上一页和下一页（默认值为false，一般情况下在手机端才会把这个值设置true）
* previousText ：上一页按钮的文本内容，默认值：上一页
* nextText ：下一页按钮的文本内容，默认值：下一页
* firstGotoIndex : 是否让第一页进入首页，默认值：false

------

### #commentPage() 的使用

* 用于对文章评论的内容和分页进行显示

> 此指令 用于文章详情：article.html 需在对应的 controller 中传入当前的 article 对象 以获取该文章的评论内容
>
> 用法与 #articlePage() 类似 也同样 内部又包含了另一个指令 #commentPaginate()，#commentPaginate()是用于显示上一页和下一页

#### #commentPage() 支持的参数有

* article：文章对象
* pageSize：用来指定当前页面有多少条数据，默认值为10，也就是不填写这个参数的话，默认为10条数据

#### 分页指令 #commentPaginate() 的参数有

* previousClass ：上一页的样式，默认值：previous
* nextClass ：下一页的样式，默认值：next
* activeClass ：当前页面的样式，默认值：active
* disabledClass ：禁用的样式（当下一页没有数据的时候，会使用此样式），默认值：disabled
* anchor ：锚点链接
* onlyShowPreviousAndNext ：是否只显示上一页和下一页（默认值为false，一般情况下在手机端才会把这个值设置true）
* previousText ：上一页按钮的文本内容，默认值：上一页
* nextText ：下一页按钮的文本内容，默认值：下一页
* firstGotoIndex : 是否让第一页进入首页，默认值：false

------

### #nextArticle() 的使用

* 用于获取下一篇文章

示例：

```html

<div>
    #nextArticle()

    #(next.title ??)

    #end
</div>
```

> 此指令 用于文章详情：article.html 需在对应的 controller 中传入当前的 article 对象 以获取下一篇文章

------

### #previousArticle() 的使用

* 用于获取上一篇文章

示例：

```html

<div>
    #previousArticle()

    #(next.title ??)

    #end
</div>
```

> 此指令 用于文章详情：article.html 需在对应的 controller 中传入当前的 article 对象 以获取上一篇文章

------

### #relevantArticles() 的使用

* 查询相关文章列表，相同标签的的文章

示例：

```html

<div>
    #relevantArticles(article,5)

    #for(article :relevantArticles)

    #(article.title ??)

    #end

    #end
</div>
```

> 此指令 用于文章详情：article.html 此时会根据传入的信息 查询相关文章信息 返回名为 relevantArticles 的 list 对象

#### #relevantArticles() 支持的参数有

* article：文章对象
* count：需要查询的数量

------

### #categories() 的使用

* 读取文章模块的所有分类

示例：

```html

<div>
    #categories(flag = "jpress",parentFlag="parent",parentId = 1,asTree = false)

    #for(category : categories)

    #(category.title ??)

    #end

    #end
</div>
```

> 此时 会根据传入的信息 查询 文章的所有分类信息 返回名为 categories 的 list 对象

#### #categories() 支持的参数有

* flag：文章标识
* parentFlag：父级文章标识
* parentId：父级ID
* isTree 是否以树状的数据格式返回,默认是false,返回全部分类,可通过 item.childs 方式获取子级分类列表

------

### #tags() 的使用

* 用于读取文章标签

示例：

```html

<div>
    #tags(orderBy = "id desc",count = 1)

    #for(tag : tags)

    #(tag.title ??)

    #end

    #end
</div>
```

> 此时 会根据传入的信息 查询 文章的所有分类信息 返回名为 tags 的 list 对象
 
#### #tags() 支持的参数有

* orderBy：根据属性进行排序 例如 `created desc` 默认值为 `id des`
* count：需要查询的数量 默认值为 10

------

### #articleCategories() 的使用

* 用于读取某一篇文章的所属分类，例如：文章的标签、文章的分类等

示例：

```html

<div>
    #articleCategories(1,"category")

    #for(category : categories)

    #(category.title ??)

    #end

    #end
</div>
```

> 此时 会根据传入的信息 查询 文章的分类信息 返回名为 categories 的 list 对象

#### #articleCategories() 支持的参数有

* 参数一：文章ID
* 参数二：分类的 type

------

### #comments() 的使用

* 读取所有评论

示例：

```html

<div>
    #comments(orderBy = "id desc",count = 1)

    #for(comment : comments)

    #(comment.title ??)

    #end

    #end
</div>
```

> 此时 会根据传入的信息 查询所有的评论信息 返回名为 comments 的 list 对象

#### #comments() 支持的参数有

* orderBy：根据属性进行排序 例如 `created desc` 默认值为 `id des`
* count：需要查询的数量 默认值为 10

------

### #userArticles() 的使用

* 读取某个用户的投稿文章内容

示例：

```html

<div>
    #userArticles(userId = 1,orderBy = "id desc",status ="normal",count = 5)

    #for(article : articles)

    #(article.title ??)

    #end

    #end
</div>
```

> 此时 会根据传入的信息 查询所有的评论信息 返回名为 articles 的 list 对象

 #### #userArticles() 支持的参数有

* userId: 用户id 可以直接传入 也可通过 controller 传入 user 对象 会自动获取 user 对象的 id 信息
* status：文章状态 例如 `status ="normal"` 
* orderBy：根据属性进行排序 例如 `created desc` 默认值为 `id des`
* count：需要查询的数量 默认值为 10

------

### #articleSearchPage() 的使用

* 用户显示文章搜索结果

示例：

```html
<div>
#articleSearchPage()
#for(article : articlePage.list)

<div class="ll-card">
    <div class="row">
        <div class="col-lg-3 col-md-3 col-sm-12 ll-card-image">
            <a href="#">
                <img src="#(article.thumbnail ?? 'img/default-img.jpg')" class="img-fluid" alt="#(article.title ??)">
            </a>
        </div>
        <div class="col-lg-9 col-md-9 col-sm-12 ll-card-main">
            <a href="#(article.url ??)">
                <div>
                    <h3 class="ll-card-main-title">
                        #(article.title ??)
                    </h3>
                    <p class="ll-card-main-info">
                        #maxLength(article.text,100)
                    </p>
                </div>
                <div class="ll-card-tag">
                    <div><i class="fa fa-clock-o"></i>#date(article.created)</div>
                    <div><i class="fa fa-eye"></i>#(article.view_count)</div>
                    <div><i class="fa fa-commenting-o"></i>#(article.comment_count)</div>
                </div>
            </a>
        </div>
    </div>
</div>
#end


#articleSearchPaginate(firstGotoIndex=true)
<nav aria-label="Page navigation example">
    <ul class="pagination justify-content-center ">
        #for(page : pages)
        <li class="page-item #(page.style ??)">
            <a class="page-link" href="#(page.url ??)">
                #(page.text ??)
            </a>
        </li>
        #end
    </ul>
</nav>
#end

#end
</div>
```

> 此指令 用于搜索结果页:artsearch.html 需在对应的 controller 中传入 keyword(搜索关键字) 以及 page(页码) 信息
>
> 用法与 #articlePage() 类似 也同样 内部又包含了另一个指令 #articleSearchPaginate()，#articleSearchPaginate()是用于显示上一页和下一页

#### #articleSearchPage() 支持的参数有

* pageSize：用来指定当前页面有多少条数据，默认值为10，也就是不填写这个参数的话，默认为10条数据

#### 分页指令 #articleSearchPaginate() 的参数有

* previousClass ：上一页的样式，默认值：previous
* nextClass ：下一页的样式，默认值：next
* activeClass ：当前页面的样式，默认值：active
* disabledClass ：禁用的样式（当下一页没有数据的时候，会使用此样式），默认值：disabled
* anchor ：锚点链接
* onlyShowPreviousAndNext ：是否只显示上一页和下一页（默认值为false，一般情况下在手机端才会把这个值设置true）
* previousText ：上一页按钮的文本内容，默认值：上一页
* nextText ：下一页按钮的文本内容，默认值：下一页
* firstGotoIndex : 是否让第一页进入首页，默认值：false

------