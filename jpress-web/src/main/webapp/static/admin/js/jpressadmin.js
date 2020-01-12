$(document).ready(function () {

    initMenu();

    initOptionSubmit();

    initImageBrowserButton();

    initListDataItemEvents();

    initComponents();

});


function initComponents() {
    initLayerComponents();
}



function initLayerComponents() {
    $("[data-opentype='layer']").on("click",function (component) {

        var layerOptions = {
            type: component.delegateTarget.dataset.layerType|| 2,
            title: component.delegateTarget.dataset.layerTitle || '选择内容',
            anim: component.delegateTarget.dataset.layerAnim || 2,
            shadeClose: component.delegateTarget.dataset.layerShadeClose ? (/^true$/i).test(component.delegateTarget.dataset.layerShadeClose) : true,
            shade: component.delegateTarget.dataset.layerShade || 0.5,
            area: component.delegateTarget.dataset.layerArea ? eval(component.delegateTarget.dataset.layerArea) : ['90%', '90%'],
            content: component.delegateTarget.dataset.layerContent || component.attr('href'),
            end: function () {
                var endFunction = component.delegateTarget.dataset.layerEnd;
                if ("reload" == endFunction) {
                    location.reload();
                } else if (endFunction) {
                    eval(endFunction)(layer);
                }
            }
        };

        layer.open(layerOptions);
    })
}


function initImageBrowserButton() {
    $(".btn-image-browser").on("click", function () {
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
                    $("#" + img).trigger("srcChanged",jpress.cpath + layer.data.src);
                    $("#" + input).val(layer.data.src);
                    $("#" + input).trigger("valChanged",layer.data.src);
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
    });

    dataItemChange();
}

function dataItemChange() {
    $(".checkAction").each(function () {
        getSelectedIds() != "" ? $(this).show().css("display", "inline-block") : $(this).hide();
    })
}

function initListDataItemEvents() {
    $(":checkbox.dataItem").on('change',function () {
        $(".checkAction").each(function () {
            getSelectedIds() != "" ? $(this).show().css("display", "inline-block") : $(this).hide();
        })
    })
}

function getSelectedIds() {
    var selectedIds = "";
    $(".dataItem").each(function () {
        if ($(this).prop('checked')) {
            selectedIds += $(this).val() + ",";
        }
    });

    return selectedIds == "" ? "" : selectedIds.substring(0,selectedIds.length - 1);
}



var _dialogShowEvent;

function initEditor(editor, height, type) {
    height = height || 467;
    type = type || 'html'; //默认用ckeditor

    if (type == 'html') {
        return initCkEdtior(editor, height);
    } else if (type == 'markdown') {
        return initMarkdownEditor(editor, height);
    }
}

var commandkeydown = false;

function doListenerCtrlsAndCommands(func) {
    $(document).keydown(function (e) {
        if (e.keyCode == 91 || e.keyCode == 224) {
            commandkeydown = true;
        }
        if (commandkeydown && e.keyCode == 83) {
            commandkeydown = false;
            func();
            return false;
        }
        if (e.ctrlKey == true && e.keyCode == 83) {
            console.log('ctrl+s');
            func();
            return false;
        }
    });
    $(document).keyup(function (e) {
        if (e.keyCode == 91 || e.keyCode == 224) {
            commandkeydown = false;
        }
    });
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
    CKEDITOR.addCss('.cke_editable img{max-width: 95%;}');

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


    ed.on('instanceReady', function () {
        ed.setKeystroke(CKEDITOR.ALT.CTRL + 83, 'save'); //  Ctrl+s
        ed.setKeystroke(1114195, 'save'); // mac command +s
        // 扩展CKEditor的 ctrl + s 保存命令,方便全屏编辑时快捷保存
        ed.addCommand('save', {
            exec: function () {
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
    var simpleMDE = new SimpleMDE({
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
                    openlayerBySimplemde(editor);
                },
                className: "fa fa-picture-o",
                title: "插入图片",
            }, "|"
            , "preview", "side-by-side", "fullscreen"
        ]

    });
    // 设置markdown编辑器滚动条高度
    $('.CodeMirror-scroll').css({
        "min-height": height
    });
    return simpleMDE;
}

function openlayerBySimplemde(editor) {
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








