$(document).ready(function () {
    // 设置当前选中菜单
    initMenu();
});


/**
 * 设置当前选中菜单
 */
function initMenu() {

    var pathName = location.pathname;
    if ("/admin" == pathName || "/admin/" == pathName) {
        pathName = "/admin/index"
    }

    $("#sidebar-menu").children().each(function () {
        var li = $(this);
        li.find('a').each(function () {
            var href = $(this).attr("href");
            if (pathName.indexOf(href) == 0) {
                li.addClass("active");
                $(this).parent().addClass("active");
                return;
            }
        });
    });
}

function checkAll(checkbox) {
    $(".dataItem").each(function () {
        // $(this).checked = checkbox.checked;
        $(this).prop('checked',checkbox.checked);
    })
    dataItemChange(checkbox);
}

function dataItemChange(checkbox) {
    $(".checkAction").each(function () {
        checkbox.checked ? $(this).show().css("display","inline-block") : $(this).hide();
    })
}





