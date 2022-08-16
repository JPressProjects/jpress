# JPress 模板的目录结构

[[toc]]

## JPress 模板的目录结构
模板的目录结构如下：

```
mytemplate
├── index.html
├── article.html
├── articles.html
├── artlist.html
├── artsearch.html
├── job.html
├── joblist.html
├── jobeapply.html
├── jobemail.html
├── product.html
├── products.html
├── prolist.html
├── prosearch.html
├── page.html
├── user_login.html
├── user_register.html
├── user_detai.html
├── user_activate.html
├── user_emailactivate.html
├── error.html
├── setting.html
├── xxx.html
├── abc.html
├── css
│   └── xxx.css
├── js
│   └── xxx.js
├── img
│   ├── xxx.png
│   ├── xxx.jpg
├── screenshot.png
└── template.properties
```

| 文件名称 | 是否必须 |描述 |
| :-----|  ---- |---- |
| index.html | ✅ | 首页模板 |
| article.html | ✅ | 文章详情页，用于显示文章的详情。可以扩展为 `article_aaa.html` 、`article_bbb.html` ，当模板有 `article_***.html` 时，我们在后台发布文章的时候，就可以选择使用哪个模板样式进行渲染。 |
| articles.html | ❌ | 未指定分类的文章列表页，当 `articles.html` 不存在的时候，默认使用 `artlist.html` 渲染 |
| artlist.html | ✅ | 文章列表页，用于根据文章分类、文章标签等显示文章列表。可以扩展为 `artlist_aaa.html` 、`artlist_bbb.html` ，当模板有 `artlist_***.html` 时，我们在后台创建文章分类的时候，就可以选择使用哪个模板样式进行渲染。 |
| artsearch.html | ✅ | 文章搜索结果页 |
| job.html | ✅ | 招聘岗位详情页 |
| joblist.html | ✅ | 招聘岗位列表页 |
| jobapply.html | ❌ | 用于岗位的申请岗位的页面|
| jobemail.html | ❌ | 当岗位开启邮箱验证时， 在申请岗位时 ，需要进行邮箱验证，此 html 为发送的邮件模板|
| product.html | ✅ | 产品详情页，可以扩展为 `product_aaa.html` 、`product_bbb.html` ，当模板有 `product_***.html` 时，我们在后台发布产品的时候，就可以选择使用哪个模板样式进行渲染。 |
| products.html | ❌ | 未指定分类的产品列表页，当 `products.html` 不存在的时候，默认使用 `prolist.html` 渲染 |
| prolist.html | ✅ | 产品列表页，用于根据产品分类、产品标签等显示产品列表。可以扩展为 `prolist_aaa.html` 、`prolist_bbb.html` ，当模板有 `prolist_***.html` 时，我们在后台创建产品分类的时候，就可以选择使用哪个模板样式进行渲染。  |
| prosearch.html | ✅ | 产品搜索结果页 |
| page.html | ✅ | 页面模板，可以扩展为 `page_aaa.html` 、`page_bbbb.html` ，当模板有 `page_***.html` 时，我们在后台发布页面的时候，就可以选择使用哪个模板样式进行渲染。 |
| user_login.html | ❌ | 用户登录模板，若此 html 文件存在，当用户登录时，用此模板渲染 |
| user_register.html | ❌ | 用户注册模板，若此 html 文件存在，当用户注册时，用此模板渲染 |
| user_activate.html | ❌ | 用户激活模板，当开启用户激活才能使用，系统会自动给用户发送激活邮件，用户通过邮件可以进入到的网页通过此模板渲染 |
| user_emailactivate.html | ❌ | 邮件激活模板，当用户激活自己的邮件时，用此模板渲染。（默认情况下，用户填写的邮箱属于 "未激活" 的状态） |
| error.html | ❌ | 错误页面，当系统出错时，用此模板渲染。比如 404 错误，500 错误等。 可以扩展为 `error_404.html`（当发生404错误的时候优先使用此文件） 和 `error_500.html` （当系统发生500错误的时候调用此文件渲染）。|
| setting.html | ❌ | 该模板的后台设置页面 |
| 其他的 html | ❌ | 假设模板目录下存在 `xxx.html` 时，可以通过 `http://your-host.com/xxx.html` 进行访问。 |
| screenshot.png | ✅ | 模板缩略图，用于在后台的模板列表里显示的图片 |
| template.properties | ✅ | 模板信息描述文件 |



## 手机独立模板扩展

所有的模板文件都可以扩展出专门用于渲染手机浏览器的模板。

例如：首页的渲染模板是 `index.html` ，如果当前目录下有 `index_h5.html`，那么，当用户通过手机访问网站的时候，JPress 会自动使用 `index_h5.html` 去渲染。

page 和 article、artlist、artsearch、product、prolist、job、prosearch 同理，当用户通过手机去访问文章详情的时候，JPress 会自动去找 `aritlce_h5.html` 渲染，当 `article_h5.html` 不存在的时候才会使用 `article.html` 进行渲染。

## 自定义模板页扩展

JPress 的自定义模板页扩展有 2 中方式：

**第一种方式：直接访问该模板文件。**

例如：当模板目录下存在 `abc.html` 文件时，我们通过 URL 地址 `http://127.0.0.1:8080/abc.html` 即可访问到该文件。


**第二种方式：通过参数指定**

一般情况下，JPress 首页会使用 `index.html` 进行渲染（手机浏览器使用 `index_h5.html`）进行渲染，但是，我们可以通过 url 的参数 v （view 的意思）指定使用别的模板页。


例如：

1、当我们访问 `http://127.0.0.1:8080?v=abc` 的时候，JPress 会去当前模板下寻找 `abc.html` 进行渲染。当模板下 `abc.html` 不存在的时候，才会自动使用 `index.html` 进行渲染。

2、当我们访问 `http://127.0.0.1:8080/article/123?v=xxx` 的时候，JPress 会去当前模板下寻找 `xxx.html` 进行渲染。当模板下 `xxx.html` 不存在的时候，才会自动使用 `article.html` 进行渲染。








