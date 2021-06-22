# 首页开发

[[toc]]

## 轮播图

```
#if(option('calmlog_slides'))
    #for(slide : linesOption('calmlog_slides'))
    #end
    
    #for(slide : linesOption('calmlog_slides','\|'))
    #end
#end
```

## 置顶文章

```
#articles(hasThumbnail=true,count=2)
    #for(article : articles)
    
    #end
#end
```

## 文章列表

```
#articlePage()
    #for(article : articlePage.list)
    #end
    
    #articlePaginate()
        #for(page : pages)
        #end
    #end
#end
```
