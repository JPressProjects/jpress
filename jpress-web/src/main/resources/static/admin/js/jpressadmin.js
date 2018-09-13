$(document).ready(function () {

    //设置layer的默认样式
    initlayer();

    // 设置当前选中菜单
    initMenu();

    // 设置弹出 toastr 提示内容
    initToastr();

    initJpActions();

    initSwitchery();

    initOptionSubmit();


    initImageBrowserButton();

});


function initImageBrowserButton() {
    $("#jp-image-browser").on("click", function () {
        var imgBrowserBtn = $(this);
        layer.open({
            type: 2,
            title: '选择图片',
            anim: 2,
            shadeClose: true,
            shade: 0.5,
            area: ['80%', '80%'],
            content: '/admin/attachment/browse',
            end: function () {
                if (layer.data.src != null) {
                    var img = imgBrowserBtn.attr("for-src");
                    var input = imgBrowserBtn.attr("for-input");
                    $("#" + img).attr("src", layer.data.src);
                    $("#" + input).val(layer.data.src);
                }
            }
        });
    })
}

function initJpActions() {
    $(".jp-actiontr").mouseover(function () {
        $(this).find(".jp-actionitem").show();
    }).mouseout(function () {
        $(".jp-actionitem").hide()
    })
}


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

function initSwitchery(config) {
    if (typeof Switchery == "undefined") {
        return;
    }

    var elems = Array.prototype.slice.call(document.querySelectorAll('.switchery'));
    elems.forEach(function (elem) {
        var switchery = config ? new Switchery(elem, config) : new Switchery(elem, {size: 'small'});
        var datafor = elem.getAttribute("data-for");
        if (datafor != null && datafor != null) {
            $("#" + datafor).val(elem.checked);
            elem.onchange = function () {
                $("#" + datafor).val(elem.checked);
            }
        }
    });

}

function initToastr() {
    if (typeof toastr != "undefined") {
        toastr.options.progressBar = true;
        toastr.options.closeButton = true;
        toastr.options.timeOut = 2000;
        toastr.options.positionClass = "toast-top-center";
    }
}

function editorUpdate() {
    for (instance in CKEDITOR.instances)
        CKEDITOR.instances[instance].updateElement();
}


var dialogShowEvent;

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
            ['Maximize', 'Source']
        ];


    var ed = CKEDITOR.replace(editor, {
        extraPlugins: 'codesnippet',
        codeSnippet_theme: 'monokai_sublime',
        height: 467,
        filebrowserImageUploadUrl: '/admin/ckeditor/upload',
        filebrowserBrowseUrl: '/admin/attachment/browse',
        language: 'zh-cn'
    });

    ed.on("dialogShow", function (event) {
        if (dialogShowEvent != null) {
            return;
        }

        dialogShowEvent = event;

        event.data.getContentElement("info", "browse").removeAllListeners();
        event.data.getContentElement("Link", "browse").removeAllListeners();

        $(".cke_dialog_ui_button").each(function () {
            //"浏览服务器" == $(this).attr("title") ||
            if ("浏览服务器" == $(this).text()) {
                $(this).off("click");
                $(this).on("click", function (e) {
                    e.stopPropagation();
                    openlayer(event);
                    return false;
                })
            } else {
                $(this).off("click");
            }
        })

    });
}

function openlayer(ed) {
    layer.open({
        type: 2,
        title: '选择图片',
        anim: 2,
        shadeClose: true,
        shade: 0.5,
        area: ['80%', '80%'],
        content: '/admin/attachment/browse',
        end: function () {
            if (layer.data.src != null) {
                ed.data.getContentElement('info', 'txtUrl').setValue(layer.data.src);
                ed.data.getContentElement('Link', 'txtUrl').setValue(layer.data.src);
            }
        }
    });
}

function initOptionSubmit() {

    $('#optionForm').on('submit', function () {
        $(this).ajaxSubmit({
            type: "post",
            url: "/admin/option/save",
            success: function (data) {
                if (data.state == "ok") {
                    toastr.success('保存成功。');
                } else {
                    toastr.error(data.message, '操作失败');
                }
            },
            error: function () {
                alert("信息提交错误");
            }
        });
        return false;
    });

}

function getPara(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair[0] == variable) {
            return pair[1];
        }
    }
    return "";
}







