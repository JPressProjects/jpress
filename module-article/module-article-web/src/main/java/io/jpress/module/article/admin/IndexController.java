package io.jpress.module.article.admin;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.admin.menu.annotation.AdminMenu;
import io.jpress.admin.web.base.AdminControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.article.admin
 */
@RequestMapping("/admin/article")
public class IndexController extends AdminControllerBase {

    @AdminMenu(text = "文章管理", groupId = "article")
    public void index(){
        render("article.html");
    }



    @AdminMenu(text = "写文章", groupId = "article")
    public void add(){
        render("add.html");
    }




    @AdminMenu(text = "分类", groupId = "article")
    public void category(){

    }



    @AdminMenu(text = "标签", groupId = "article")
    public void tag(){

    }



    @AdminMenu(text = "评论", groupId = "article")
    public void comment(){

    }


    @AdminMenu(text = "收入", groupId = "article")
    public void pay(){

    }

}
