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

    CKEDITOR.config.disallowedContent = 'img{width,height};img[width,height]';
    CKEDITOR.addCss('.cke_editable img{max-width: 95%;}');

    return CKEDITOR.replace(editor, {
        autoUpdateElement: true,
        removePlugins: 'easyimage,cloudservices',
        extraPlugins: 'codesnippet,uploadimage,flash,image',
        codeSnippet_theme: 'monokai_sublime',
        height: height,
        uploadUrl: jpress.cpath + '/commons/ckeditor/upload',
        imageUploadUrl: jpress.cpath + '/commons/ckeditor/upload',
        filebrowserUploadUrl: jpress.cpath + '/commons/ckeditor/upload',
        language: 'zh-cn'

    });
}

function initMarkdownEditor(editor, height) {
    return new SimpleMDE({
        element: $(editor)[0],
        autoDownloadFontAwesome: false,
        spellChecker: false,
        styleSelectedText: false,
        forceSync: true,
        renderingConfig: {
            codeSyntaxHighlighting: true,
        },
        toolbar: [
            "heading", "bold", "italic", "|"
            , "quote", "unordered-list", "ordered-list", "|"
            , "code", "table", "horizontal-rule", "|"
            , "link", "image", "|"
            , "preview", "side-by-side", "fullscreen"
        ]

    })
}




