$(document).ready(function () {

    //设置layer的默认样式
    initlayer();

    // 设置当前选中菜单
    initMenu();

    initOptionSubmit();

    initImageBrowserButton();

});


function initImageBrowserButton() {
    $(".jp-image-browser").on("click", function () {
        var imgBrowserBtn = $(this);
        layer.open({
            type: 2,
            title: '选择图片',
            anim: 2,
            shadeClose: true,
            shade: 0.5,
            area: ['90%', '90%'],
            content: jpress.cpath + '/admin/attachment/browse',
            end: function () {
                if (layer.data.src != null) {
                    var img = imgBrowserBtn.attr("for-src");
                    var input = imgBrowserBtn.attr("for-input");
                    $("#" + img).attr("src", jpress.cpath + layer.data.src);
                    $("#" + input).val(layer.data.src);
                }
            }
        });
    })
}


/**
 * 设置当前选中菜单
 */
function initMenu() {

    var pathName = location.pathname;
    if (jpress.cpath + "/admin" == pathName || jpress.cpath + "/admin/" == pathName) {
        pathName = jpress.cpath + "/admin/index"
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

function getSelectedIds() {
    var selectedIds = "";
    $(".dataItem").each(function () {
        if ($(this).prop('checked')) {
            selectedIds += $(this).val() + ",";
        }
    })

    return selectedIds;
}


function initlayer() {
    layer.data = {}
    layer.config({
        extend: 'jpress/style.css', //使用JPress皮肤
        skin: 'layer-ext-jpress'
    });
}

var _dialogShowEvent;

function initEditor(editor, height, type) {
    height = height || 467;
    type = type || 'html'; //默认用ckeditor

    if (type == 'html') {
        return initCkEdtior(editor, height);
    }

    else if (type == 'markdown') {
        return initMarkdownEditor(editor, height);
    }
}

function initCkEdtior(editor, height) {
    CKEDITOR.config.toolbar =
        [
            ['Bold', 'Italic', 'Underline', 'Strike', 'RemoveFormat'],
            ['Blockquote', 'CodeSnippet', 'Image', 'Html5audio', 'Html5video', 'Flash', 'Table', 'HorizontalRule'],
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

    CKEDITOR.config.wordcount = {
        showCharCount: true,
    };

    CKEDITOR.config.disallowedContent = 'img{width,height};img[width,height]';


    var ed = CKEDITOR.replace(editor, {
        autoUpdateElement: true,
        removePlugins: 'easyimage,cloudservices',
        extraPlugins: 'entities,codesnippet,uploadimage,flash,image,wordcount,notification,html5audio,html5video,widget,widgetselection,clipboard,lineutils',
        codeSnippet_theme: 'monokai_sublime',
        height: height,
        uploadUrl: jpress.cpath + '/commons/ckeditor/upload',
        imageUploadUrl: jpress.cpath + '/commons/ckeditor/upload',
        filebrowserUploadUrl: jpress.cpath + '/commons/ckeditor/upload',
        filebrowserBrowseUrl: jpress.cpath + '/admin/attachment/browse',
        language: 'zh-cn'
    });


    ed.on('instanceReady',function () {
        ed.setKeystroke( CKEDITOR.CTRL + 83, 'save' ); //  Ctrl+s
        // 扩展CKEditor的 ctrl + s 保存命令,方便全屏编辑时快捷保存
        ed.addCommand('save', {
            exec: function() {
                var ds = window.doSubmit;
                ds && ds();
            }
        });
    });

    ed.on("dialogShow", function (event) {

        // 方便调试
        _dialogShowEvent = event;

        var infoEle = event.data.getContentElement("info", "browse");
        if (infoEle) infoEle.removeAllListeners();

        var linkEle = event.data.getContentElement("Link", "browse");
        if (linkEle) linkEle.removeAllListeners();

        $(".cke_dialog_ui_button").each(function () {
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

    return ed;
}

function initMarkdownEditor(editor, height) {
    return new SimpleMDE({
        element: $(editor)[0],
        autoDownloadFontAwesome: false,
        spellChecker: false,
        styleSelectedText: false,
        forceSync: true,
        renderingConfig: {
            codeSyntaxHighlighting: true
        },
        toolbar: [
            "heading", "bold", "italic", "|"
            , "quote", "unordered-list", "ordered-list", "|"
            , "code", "table", "horizontal-rule", "|"
            , "link", {
                name: "image",
                action: function customFunction(editor) {
                    openlayerfForSimplemde(editor);
                },
                className: "fa fa-picture-o",
                title: "插入图片",
            }, "|"
            , "preview", "side-by-side", "fullscreen"
        ]

    });
}

function openlayerfForSimplemde(editor) {
    layer.data.src = null;
    layer.open({
        type: 2,
        title: '选择图片',
        anim: 2,
        shadeClose: true,
        shade: 0.5,
        area: ['90%', '90%'],
        content: jpress.cpath + '/admin/attachment/browse',
        end: function () {
            if (layer.data.src != null) {
                editor.codemirror.replaceSelection('![](' + jpress.cpath + layer.data.src + ')')
            }
        }
    });
}


function openlayer(event) {
    layer.data.src = null;
    layer.open({
        type: 2,
        title: '选择图片',
        anim: 2,
        shadeClose: true,
        shade: 0.5,
        area: ['90%', '90%'],
        content: jpress.cpath + '/admin/attachment/browse',
        end: function () {
            if (layer.data.src != null) {
                var src = jpress.cpath + layer.data.src;

                var infoTxtUrlEle = event.data.getContentElement('info', 'txtUrl');
                if (infoTxtUrlEle) infoTxtUrlEle.setValue(src);

                var infoUrlEle = event.data.getContentElement('info', 'url');
                if (infoUrlEle) infoUrlEle.setValue(src);

                var infoSrcEle = event.data.getContentElement('info', 'src');
                if (infoSrcEle) infoSrcEle.setValue(src);

                var linkTxtUrlEle = event.data.getContentElement('Link', 'txtUrl')
                if (linkTxtUrlEle) linkTxtUrlEle.setValue(src);
            }
        }
    });
}

function initOptionSubmit() {

    $('#optionForm').on('submit', function () {
        $(this).ajaxSubmit({
            type: "post",
            url: jpress.cpath + "/admin/option/doSave",
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








