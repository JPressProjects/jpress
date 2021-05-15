$(document).ready(function () {

    initlayer();
    initTableActions();
    initToastr();
    initSwitchery();
    initDomainSpan();
    initSlugSpan();
    initDatePicker();
    initDatetimePicker();
    initAutoAjaxSubmit();

});

function initlayer() {
    if (typeof layer != "undefined") {
        layer.data = {}
        layer.config({
            extend: 'jpress/style.css', //使用JPress皮肤
            skin: 'layer-ext-jpress'
        });
    }
}


function closeLayerAndRefresh() {
    if ( typeof parent != "undefined" && parent.layer){
        parent.layer.data.needRefresh = true;
        parent.layer.closeAll();
    }
}


function initDatePicker() {
    if ($('').datepicker) {
        $('.datepicker').datepicker({
            language: 'zh-CN',
            format: 'yyyy-mm-dd',
            autoclose: true
        });
    }
}

function initDatetimePicker() {
    if ($('').datetimepicker) {
        $('.datetimepicker').datetimepicker({
            format: 'YYYY-MM-DD HH:mm:ss',
            useCurrent:true,
            locale:'zh-cn'
        });
    }
}

function initTableActions() {
    $("tr").mouseover(function () {
        $(this).find(".jp-action-body").show();
    }).mouseout(function () {
        $(".jp-action-body").hide()
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



var switcheries = {};

function initSwitchery(elements) {
    if (typeof Switchery == "undefined") {
        return;
    }

    elements = elements || document.querySelectorAll('.switchery');

    var elems = Array.prototype.slice.call(elements);
    elems.forEach(function (elem) {
        var switchery = new Switchery(elem, {size: 'small'});

        switcheries[elem.getAttribute('id')] = switchery;

        var datafor = elem.getAttribute("data-for");
        if (datafor) {
            $("#" + datafor).val(elem.checked);
        }

        var ctrl = elem.getAttribute("data-ctrl");
        if (ctrl) {
            $("." + ctrl).toggle(elem.checked)
        }

        var close = elem.getAttribute("data-close-sync");
        var open = elem.getAttribute("data-open-sync");

        var onchangeFunction = elem.getAttribute("data-change-function");

        elem.onchange = function () {
            if (datafor) {
                $("#" + datafor).val(elem.checked);

            }

            if (ctrl) {
                $("." + ctrl).toggle(elem.checked)
            }

            if (close && !elem.checked) {
                var closeSwitchery = switcheries[close];
                setSwitchery(closeSwitchery, false);
            }

            if (open && elem.checked) {
                var openSwitchery = switcheries[close];
                setSwitchery(openSwitchery, true);
            }

            if (onchangeFunction) {
                eval(onchangeFunction)(elem, switchery);
            }
        }
    });

}

//https://stackoverflow.com/questions/21931133/changing-a-switchery-checkbox-state-from-code
function setSwitchery(switchElement, checkedBool) {
    if ((checkedBool && !switchElement.isChecked()) || (!checkedBool && switchElement.isChecked())) {
        switchElement.setPosition(true);
        switchElement.handleOnchange(true);
    }
}


function setSwitcheryByIdString(idString, checkedBool) {
    var switchery = switcheries[idString];
    setSwitchery(switchery, checkedBool);
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


function isMobileBrowser(){
    if(window.navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i)) {
        return true; // 移动端
    }else{
        return false; // PC端
    }
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

function ajaxPost(url, data, okFunction, failFunction) {
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

    $.post(url, data,function (result) {
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
                        showMessage(okMessage);
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


function showMessage(msg) {
    if (typeof toastr != "undefined") {
        toastr.success(msg);
    } else {
        alert(msg);
    }
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