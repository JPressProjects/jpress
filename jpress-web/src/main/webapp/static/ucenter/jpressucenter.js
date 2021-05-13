$(document).ready(function () {
    // 设置当前选中菜单
    initMenu();
});


/**
 * 设置当前选中菜单
 */
function initMenu() {
    var pathName = location.pathname;
    if (getContextPath() + "/ucenter" == pathName || getContextPath() + "/ucenter/" == pathName) {
        pathName = getContextPath() + "/ucenter/index"
    }

    setActiveMenu(pathName);
}


function setActiveMenu(pathName) {

    var activeTreeview, activeLi;

    $("#sidebar-menu").children().each(function () {
        var li = $(this);
        li.find('a').each(function () {
            var href = $(this).attr("href");
            if (pathName == href) {
                activeTreeview = li;
                activeLi = $(this);
                return false;
            } else if (href.endsWith("/list")) {
                href = href.substr(0, href.indexOf("/list"));
                if (pathName == href) {
                    activeTreeview = li;
                    activeLi = $(this);//.parent();
                    return false;
                }
            }
        });
    });

    if (!activeTreeview) {
        var indexOf = pathName.lastIndexOf("/");
        if (indexOf > 0) {
            pathName = pathName.substr(0, indexOf);
            setActiveMenu(pathName);
        }
    } else {
        if (activeTreeview) {
            activeTreeview.addClass("menu-open");
            activeTreeview.children(":first").addClass("active");
        }
        if (activeLi) {
            activeLi.addClass("active");
        }
    }
}


function getContextPath() {
    if (typeof JbootAdmin == 'undefined') {
        return ""
    } else {
        return JbootAdmin.cpath;
    }
}



