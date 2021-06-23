# JPress 的模板指令

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

#checkedIf()  主要用在 checkbox 或者 radio 等标签属性里，支持的参数只能有 true 和 false。

例如：

```
<input type="radio" name="..." value="text" #checkedIf(type !=null && type == "text")>
```

此时，当 后端输出的 `type` 值等于 “text” 的时候，该单选框就会被选中。

### #selectedIf() 的使用
其用法与 ` #checkedIf()`  一直，只是  #checkedIf() 用在 `<option><select ...></option>` 里。


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

以上的标签用法请参考：[https://gitee.com/JPressProjects/jpress/tree/master/doc](https://gitee.com/JPressProjects/jpress/tree/master/doc)


## 页面相关指令：

| 指令名称 | 可用页面 |描述 |  
| --- | --- | --- | 
| #page() | 任意 | 用于读取某个页面 |  
| #pages() | 任意 | 用于读取页面列表 | 


## 用户相关指令：

| 指令名称 | 可用页面 |描述 |  
| --- | --- | --- |  
| #users() | 暂不支持 | 用于读取页面列表 | 




