$(document).ready(function () {

    initJPActions();
    initToastr();
    initSwitchery();
    initDomainSpan();
    initSlugSpan();
});

function initJPActions() {
    $(".jp-actiontr").mouseover(function () {
        $(this).find(".jp-actionitem").show();
    }).mouseout(function () {
        $(".jp-actionitem").hide()
    })
}

function initToastr() {
    if (typeof toastr != "undefined") {
        toastr.options.progressBar = true;
        toastr.options.closeButton = true;
        toastr.options.timeOut = 2000;
        toastr.options.positionClass = "toast-top-center";
    }
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

function initDomainSpan() {
    $(".domainSpan").each(function () {
        if ($(this).text() == "") {
            $(this).text(window.location.protocol + "//" + window.location.host)
        }
    })
}


function initSlugSpan() {

    var reg = /([\s|\。|\，|\？|\、|\"|\“|\”|\‘|\'|\（|\）|\《|\》|\… |\～|\￥|\&|\*|\@|\#|\$||\%|\`|\.|\【|\】|\-|\<|\>|\=])+/ig;

    String.prototype.endWith = function (s) {
        if (s == null || s == "" || this.length == 0 || s.length > this.length)
            return false;
        if (this.substring(this.length - s.length) == s)
            return true;
        else
            return false;
    }

    $(".slugSpan").each(function () {

        var that = $(this);

        var forListener = that.attr("for-listener");
        var forInput = that.attr("for-input");

        $(this).editable({
            emptytext: "标题"
        });

        $(this).on('save', function (e, params) {
            $('#' + forInput).attr('value', params.newValue);
        });


        $("#" + forListener).keyup(function () {

            if ($('#' + forInput).val() == "") {

                var value = this.value.replace(reg, "_");

                if (value.endWith("_")) {
                    value = value.substring(0, value.length - 1);
                }

                if(value == ""){
                    that.text(value);
                    that.editable('setValue', value);
                    return;
                }
                ajaxGet(jpress.cpath + "/commons/pinyin/doGetPinyin/" + value, function (result) {
                    var pinyin = result.data;
                    that.text(pinyin);
                    that.editable('setValue', pinyin);
                })
            }
        });
    })
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

function ajaxGet(url, okFunction, failFunction) {
    if (url == null || "" == url) {
        alert("url 不能为空 ");
        return
    }

    okFunction = okFunction || function (result) {
            location.reload();
        };

    failFunction = failFunction || function (result) {
            toastr.error(result.message, '操作失败');
        };

    $.get(url, function (result) {
        if (result.state == 'ok') {
            okFunction(result);
        } else {
            failFunction(result);
        }
    });
}

function ajaxSubmit(form, okFunction, failFunction) {

    if (typeof(CKEDITOR) != "undefined") {
        for (instance in CKEDITOR.instances) {
            CKEDITOR.instances[instance].updateElement();
        }
    }

    okFunction = okFunction || function (result) {
            location.reload();
        };

    failFunction = failFunction || function (result) {
            toastr.error(result.message, '操作失败');
        };

    $(form).ajaxSubmit({
        type: "post",
        success: function (result) {
            if (result.state == "ok") {
                okFunction(result);
            } else {
                failFunction(result);
            }
        },
        error: function () {
            toastr.error('网络错误，请稍后重试。', '操作失败');
        }
    });
}