# 产品页面

[[toc]]

## 产品列表页

产品列表

```
#productPage()
    #for(product : productPage.list)
    #end
#end
```

产品分页

```
#productPaginate()
    #for(page : pages)
    #end
#end
```

商品名称

`#(product.title ??)`


商品链接

`#(product.url)`

商品描述

`#maxLength(product.text,100)`

商品价格

`¥ #number(product.price)`

购买人数

`#(product.sales_count ?? 0)`


## 商品详情页

商品头部

```
 #@defaultProductHeader()
```
 
 > 一句代码就可以把幻灯片，商品名称，介绍以及购买按钮显示出来。
 
 
 商品评论

```
#@defaultProductCommentPage()
```

相关推荐

```
#relevantProducts(product)
#for(product : relevantProducts)
#end
#end
```

后台设置图片

`#option('calmlog_qrcode','img/code.jpg')`

商品内容

`#(product.content)`

