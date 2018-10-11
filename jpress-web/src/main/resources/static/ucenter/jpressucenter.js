$(document).ready(function () {
    // 设置当前选中菜单
    initMenu();
});


/**
 * 设置当前选中菜单
 */
function initMenu() {

    var pathName = location.pathname;
    if (jpress.cpath + "/ucenter" == pathName || jpress.cpath + "/ucenter/" == pathName) {
        pathName = jpress.cpath + "/ucenter/index"
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


function editorUpdate() {
    for (instance in CKEDITOR.instances)
        CKEDITOR.instances[instance].updateElement();
    if (_simplemde) {
        $("#"+_editor).text(_simplemde.value());
    }
}


var _simplemde;
var _editor;

function initEditor(editor, height, type) {

    height = height || 467;
    type = type || 'html'; //默认用ckeditor
    _editor = editor;

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
        height: height,
        filebrowserImageUploadUrl: jpress.cpath + '/commons/ckeditor/upload',
        language: 'zh-cn'
    });

    return ed;
}

function initMarkdownEditor(editor, height) {
    _simplemde = new SimpleMDE({
        element: $(editor)[0],
        toolbar: [
            "heading", "bold", "italic", "|"
            , "quote", "unordered-list", "ordered-list", "|"
            , "code", "table", "horizontal-rule", "|"
            , "link", "image", "|"
            , "preview", "side-by-side", "fullscreen"
        ]

    });
    return _simplemde;
}




