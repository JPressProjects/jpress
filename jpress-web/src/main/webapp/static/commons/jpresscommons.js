$(document).ready(function () {

    initTableActions();
    initToastr();
    initSwitchery();
    initDomainSpan();
    initSlugSpan();
    initDatepicker();
    initAutoAjaxSubmit();

});

function initDatepicker() {
    if ($('').datepicker) {
        $('.datepicker').datepicker({
            language: 'zh-CN',
            format: 'yyyy-mm-dd',
            autoclose: true
        });
    }
}

function initTableActions() {
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

    $(".slugSpan").each(function () {

        var forInput = $(this).attr("for-input");

        $(this).editable({
            emptytext: "点击可编辑"
        });

        $(this).on('save', function (e, params) {
            $('#' + forInput).attr('value', params.newValue);
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

function doActivateEmail(userId) {
    ajaxGet(jpress.cpath + "/commons/emailactivate?userId=" + userId, function (result) {
        alert(result.message);
    })
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

function initAutoAjaxSubmit() {

    $('.autoAjaxSubmit').on('submit', function () {

        if (typeof (CKEDITOR) != "undefined") {
            for (instance in CKEDITOR.instances) {
                CKEDITOR.instances[instance].updateElement();
            }
        }

        var okFunction = $(this).attr('data-ok-function');
        var okHref = $(this).attr('data-ok-href');
        var okMessage = $(this).attr('data-ok-message');

        var failFunction = $(this).attr('data-fail-function');
        var failMessage = $(this).attr('data-fail-message');

        $(this).ajaxSubmit({
            type: "post",
            success: function (result) {
                if (result.state == "ok") {
                    if (okFunction) {
                        eval(okFunction)(result);
                        return;
                    }

                    if (okHref) {
                        location.href = okHref;
                        return
                    }

                    if (okMessage) {
                        if (typeof toastr != "undefined") {
                            toastr.success(okMessage);
                        } else {
                            alert(okMessage);
                        }
                        return;
                    }

                    location.reload();

                }
                //fail
                else {
                    if (failFunction) {
                        eval(failFunction)(result);
                        return;
                    }

                    if (failMessage) {
                        showErrorMessage(failMessage);
                        return
                    }

                    if (result.message) {
                        showErrorMessage(result.message);
                    } else {
                        showErrorMessage('操作失败。')
                    }
                }
            },
            error: function () {
                showErrorMessage('系统错误，请稍后重试。');
            }
        });

        return false;
    });
}

function showErrorMessage(msg) {
    if (typeof toastr != "undefined") {
        toastr.error(msg, '操作失败');
    } else {
        alert(msg);
    }
}

function ajaxSubmit(form, okFunction, failFunction) {

    if (typeof (CKEDITOR) != "undefined") {
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
            toastr.error('系统错误，请稍后重试。', '操作失败');
        }
    });
}