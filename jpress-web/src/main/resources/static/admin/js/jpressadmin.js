$(document).ready(function () {

    //设置layer的默认样式
    initlayer();

    // 设置当前选中菜单
    initMenu();

    // layer.open({
    //     type: 1,
    //     // skin: 'layui-layer-demo',
    //     closeBtn: false,
    //     area: '350px',
    //     anim: 1,
    //     shadeClose: true,
    //     content: '<div style="padding:20px;">aaaa<br>bbbb<br><br><br>cccc</div>'
    // });
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
    layer.config({
        extend: 'jpress/style.css', //使用JPress皮肤
        skin: 'layer-ext-jpress'
    });
}


function initTinymce() {
    tinymce.init({
        selector: '#textarea',
        height: 365,
        language: 'zh_CN',
        themes: "mobile",
        menubar: false,
        automatic_uploads: true,
        paste_data_images: true,
        convert_urls: false,
        relative_urls: false,
        imagetools_toolbar: "rotateleft rotateright | flipv fliph | editimage imageoptions",
        imagetools_proxy: '${CPATH}/admin/tinymce/image/proxy',
        images_upload_url: '${CPATH}/admin/tinymce/image/upload',
        wordcount_countregex: /[\u4e00-\u9fa5_a-zA-Z0-9]/g,
        setup: function (ed) {
            // tab键
            ed.on('keydown', function (evt) {
                if (evt.keyCode == 9) {
                    if (evt.shiftKey) {
                        ed.execCommand('Outdent');
                    } else {
                        ed.execCommand('Indent');
                    }

                    evt.preventDefault();
                    evt.stopPropagation();
                }
            });
        },
        file_picker_callback: function (callback, value, meta) {
            layer.open({
                type: 2,
                title: '选择图片',
                anim: 2,
                shadeClose: true,
                shade: 0.5,
                area: ['80%', '70%'],
                content: '/admin/menu/menu',
                end: function () {
                    if ('' != data.url && null != data.url) {
                        callback(data.url, {alt: data.alt});
                    }
                }
            });
        },
        plugins: [
            "advlist autolink autosave link image media imagetools lists charmap print preview hr anchor pagebreak spellchecker",
            "searchreplace wordcount visualblocks visualchars code codesample fullscreen insertdatetime media nonbreaking",
            "table contextmenu directionality emoticons template textcolor paste fullpage textcolor colorpicker textpattern"
        ],
        toolbar1: '  bold italic underline strikethrough removeformat | blockquote hr table image media codesample | anchor link   unlink | alignleft aligncenter alignright alignjustify | bullist numlist     ',
        toolbar2: '  formatselect | outdent indent | forecolor backcolor  |  undo redo | code  fullscreen',
    });

}






