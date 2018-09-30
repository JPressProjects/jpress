$(document).ready(function () {


    // 设置当前选中菜单
    initMenu();



});







/**
 * 设置当前选中菜单
 */
function initMenu() {

    var pathName = location.pathname;
    if ("/ucenter" == pathName || "/ucenter/" == pathName) {
        pathName = "/ucenter/index"
    }

    var activeTreeview, activeLi;

    $("#sidebar-menu").children().each(function () {
        var li = $(this);
        li.find('a').each(function () {
            var href = $(this).attr("href");
            if (pathName == href) {
                activeTreeview = li;
                activeLi = $(this).parent();
                return false;
            } else if (pathName.indexOf(href) == 0) {
                // li.addClass("active");
                // $(this).parent().addClass("active");
                // return false;
                activeTreeview = li;
                activeLi = $(this).parent();
            }
        });
    });

    if (activeTreeview) {
        activeTreeview.addClass("active");
    }
    if (activeLi) {
        activeLi.addClass("active");
    }
}









