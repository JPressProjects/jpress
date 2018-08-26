$(document).ready(function () {

    //设置layer的默认样式
    initlayer();

    // 设置当前选中菜单
    initMenu();

    // 设置弹出 toastr 提示内容
    initToastr();

});


/**
 * 设置当前选中菜单
 */
function initMenu() {

    var pathName = location.pathname;
    if ("/admin" == pathName || "/admin/" == pathName) {
        pathName = "/admin/index"
    }

    var activeTreeview, activeLi;

    $("#sidebar-menu").children().each(function () {
        var li = $(this);
        li.find('a').each(function () {
            var href = $(this).attr("href");
            if (pathName.indexOf(href) == 0) {
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

function checkAll(checkbox) {
    $(".dataItem").each(function () {
        $(this).prop('checked', checkbox.checked);
    })
    dataItemChange(checkbox);
}

function dataItemChange(checkbox) {
    $(".checkAction").each(function () {
        checkbox.checked ? $(this).show().css("display", "inline-block") : $(this).hide();
    })
}






function initlayer() {
    layer.data = {}
    layer.config({
        extend: 'jpress/style.css', //使用JPress皮肤
        skin: 'layer-ext-jpress'
    });
}

function initToastr() {
    if (typeof toastr != "undefined") {
        toastr.options.progressBar = true;
    }
}

function editorUpdate() {
    for (instance in CKEDITOR.instances)
        CKEDITOR.instances[instance].updateElement();
}

function initEditor(editor) {
    CKEDITOR.config.toolbar =
        [
            ['Bold', 'Italic', 'Underline', 'Strike', 'RemoveFormat'],
            ['Blockquote', 'CodeSnippet', 'Image', 'Flash', 'Table', 'HorizontalRule'],
            ['Link', 'Unlink', 'Anchor'],
            ['Outdent', 'Indent'],
            ['NumberedList', 'BulletedList'],
            ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'],
            '/',
            ['Format', 'FontSize'],
            ['TextColor', 'BGColor'],
            ['Undo', 'Redo'],
            ['Source']
        ];


    CKEDITOR.replace(editor, {
        extraPlugins: 'codesnippet',
        codeSnippet_theme: 'monokai_sublime',
        height: 467,
        filebrowserImageUploadUrl: '/ckeditor/upload',
        filebrowserBrowseUrl: '/admin/attachment/browse',
        language: 'zh-cn'
    });
}






