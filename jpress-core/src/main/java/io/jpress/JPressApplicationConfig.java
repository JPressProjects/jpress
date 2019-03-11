package io.jpress;


import io.jboot.app.config.annotation.ConfigModel;

@ConfigModel(prefix = "jpress")
public class JPressApplicationConfig {

    private String adminLoginPage = "/admin/login";         //登录的页面
    private String adminLoginAction = "/admin/doLogin";     //登录的方法

    public String getAdminLoginPage() {
        return adminLoginPage;
    }

    public void setAdminLoginPage(String adminLoginPage) {
        this.adminLoginPage = adminLoginPage;
    }

    public String getAdminLoginAction() {
        return adminLoginAction;
    }

    public void setAdminLoginAction(String adminLoginAction) {
        this.adminLoginAction = adminLoginAction;
    }
}
