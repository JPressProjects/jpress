/**
 * 对 Swal 进行全局配置
 * @type {{}}
 */
var mySwal = typeof (Swal) != "undefined" ? Swal.mixin({
    title: '您确定如此操作吗？',
    text: '确定后无法恢复，请谨慎操作！',
    showClass: {
        popup: 'animated fadeInDown faster'
    },
    hideClass: {
        popup: 'animated fadeOutUp faster'
    },
    customClass: {
        confirmButton: 'btn btn-primary',
        cancelButton: 'btn btn-danger'
    },
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    cancelButtonText: ' 取消 ',
    showLoaderOnConfirm: true,
    confirmButtonText: ' 确定操作! ',
}) : {};


function getContextPath() {
    if (typeof jpress == 'undefined') {
        return ""
    } else {
        return jpress.cpath;
    }
}


function getTableSelectedRowData() {
    var retData = null;
    $('[name="tableItem"]').each(function () {
        if ($(this).prop('checked')) {
            retData = {};
            $(this).closest('tr').children().each(function () {
                for (var attr in this.dataset) {
                    retData[attr] = this.dataset[attr];
                }
            })

            var trDatas = $(this).closest('tr').data();
            for (var attr in trDatas) {
                retData[attr] = trDatas[attr];
            }
        }
    });
    return retData;
}

function getTableSelectedIds() {
    var selectedIds = "";
    $('[name="tableItem"]').each(function () {
        if ($(this).prop('checked')) {
            selectedIds += $(this).val() + ",";
        }
    });
    return selectedIds.substring(0, selectedIds.length - 1);
}

/**
 * 获取 url 的参数内容
 * @returns {string}
 */
function getPara(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair[0] === variable) {
            return pair[1];
        }
    }
    return "";
}

/**
 * 查看当前页面是不是手机页面
 * @returns {boolean}
 */
function isMobileBrowser() {
    if (window.navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i)) {
        return true; // 移动端
    } else {
        return false; // PC端
    }
}

/**
 * 进行 get 请求
 * @param url
 * @param okFunction
 * @param failFunction
 */
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

    $.ajax({
        type: 'GET',
        url: url,
        async: true,
        success: function (result) {
            if (result.state == 'ok') {
                okFunction(result);
            } else {
                failFunction(result);
            }
        },
        error: function (e) {
            toastr.error("系统发生错误...", '操作失败');
        }
    });

}


/**
 * 进行 ajax 请求
 * @param url
 * @param data
 * @param okFunction
 * @param failFunction
 */
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

    $.ajax({
        url: url,
        type: 'POST',
        data: data,
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function (result) {
            if (result.state == 'ok') {
                okFunction(result);
            } else {
                failFunction(result);
            }
        },
        error: function (arg1) {
            showErrorMessage("系统发生错误...");
        }
    });
}


function jsonPost(url, data, okFunction, failFunction) {

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

    $.ajax({
        url: url,
        type: 'POST',
        data: typeof data === "string" ? data : JSON.stringify(data),
        contentType: "application/json; charset=utf-8",
        success: function (result) {
            if (result.state == 'ok') {
                okFunction(result);
            } else {
                failFunction(result);
            }
        },
        error: function (arg1) {
            showErrorMessage("系统发生错误...");
        }
    });
}

/**
 * 对某个 form 进行 ajax 提交
 * @param form
 * @param okFunction
 * @param failFunction
 */
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

/**
 * 弹出消息
 * @param msg
 * @param url
 */
function showMessage(msg, url) {
    if (typeof toastr != "undefined") {
        toastr.options.onHidden = function () {
            reloadOrRedirect(url);
        };
        toastr.success(msg);
    } else {
        alert(msg);
        reloadOrRedirect(url);
    }
}

/**
 * 弹出错误消息
 * @param msg
 * @param url
 */
function showErrorMessage(msg, url) {
    if (typeof toastr != "undefined") {
        toastr.options.onHidden = function () {
            reloadOrRedirect(url);
        };
        toastr.error(msg, '操作失败');
    } else {
        alert(msg);
        reloadOrRedirect(url);
    }
}


function reloadOrRedirect(url) {
    if (url) {
        if ("reload" == url) {
            location.reload();
        } else {
            location.href = url;
        }
    }
}

/**
 * 获取表格的 选中的 id
 * @returns {string}
 */
function getTableSelectedIds() {
    var selectedIds = "";
    $('[name="tableItem"]').each(function () {
        if ($(this).prop('checked')) {
            selectedIds += $(this).val() + ",";
        }
    });
    return selectedIds.substring(0, selectedIds.length - 1);
}


function getTableSelectedRowData() {
    var retData = null;
    $('[name="tableItem"]').each(function () {
        if ($(this).prop('checked')) {
            retData = {};
            $(this).closest('tr').children().each(function () {
                for (var attr in this.dataset) {
                    retData[attr] = this.dataset[attr];
                }
            })

            var trDatas = $(this).closest('tr').data();
            for (var attr in trDatas) {
                retData[attr] = trDatas[attr];
            }
        }
    });
    return retData;
}


/**
 * 弹出 Alert 确认框
 * @param title
 * @param btnText
 */
function sweetAlert(title, btnText) {
    Swal.fire({
        title: title,
        confirmButtonText: btnText || '  好的 ',
        showClass: {
            popup: 'animated fadeInDown faster'
        },
        hideClass: {
            popup: 'animated fadeOutUp faster'
        }
    });
}

/**
 * 弹出确认对话框
 * @param title
 * @param text
 * @param btnText
 * @param actionUrl
 * @param successTitle
 * @param successText
 */
function sweetConfirm(title, text, btnText, actionUrl, successTitle, successText, actionComponent) {
    mySwal.fire({
        title: title || '您确定如此操作吗？',
        icon: 'question',
        text: text || '确定后无法恢复，请谨慎操作！',
        confirmButtonText: btnText || ' 确定操作! ',
        preConfirm: () => {

            var token = getCookie("csrf_token");
            if (token) {
                if (actionUrl.indexOf("?") == -1) {
                    actionUrl = actionUrl + "?csrf_token=" + token;
                } else {
                    if (actionUrl.indexOf("csrf_token=") == -1) {
                        actionUrl = actionUrl + "&csrf_token=" + token;
                    }
                }
            }

            return fetch(actionUrl, {
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(response.statusText)
                    }
                    return response.json()
                })
                .catch(error => {
                    Swal.showValidationMessage(
                        `Request failed: ${error}`
                    )
                });
        }
    }).then((result) => {

        var successFun = actionComponent.attr('data-success-function');
        var successGoto = actionComponent.attr('data-success-goto');
        var successMsg = actionComponent.attr('data-success-message');

        var failFun = actionComponent.attr('data-fail-function');
        var failMsg = actionComponent.attr('data-fail-message');

        if (result.value && result.value.state == 'ok') {
            mySwal.fire({
                title: successTitle || '操作成功!',
                text: successText || '',
                icon: 'success',
                showCancelButton: false,
                confirmButtonColor: '#3085d6',
                confirmButtonText: '  确定 ',
                showLoaderOnConfirm: false,
                timer: 2000,
                timerProgressBar: true,
            }).then(() => {
                if (successFun) {
                    eval(successFun)(result);
                    return;
                }

                if (successMsg) {
                    showMessage(successMsg, successGoto);
                    return;
                }

                if (successGoto) {
                    location.href = successGoto;
                    return
                }

                if (result.value && result.value.message) {
                    showMessage(result.value.message);
                    return;
                }

                window.location.reload();
            })
        } else if ("cancel" === result.dismiss) {
            //用户点击了取消按钮，什么都不操作
        } else {
            if (failFun) {
                eval(failFun)(result);
                return;
            }

            if (failMsg) {
                showErrorMessage(failMsg);
                return;
            }

            if (result.value.message) {
                showErrorMessage(result.value.message);
            } else {
                showErrorMessage('操作失败。')
            }

        }
    })
}


/**
 * 弹出删除确认框
 * @param title
 * @param text
 * @param btnText
 * @param actionUrl
 * @param successTitle
 * @param successText
 */
function sweetConfirmDel(title, text, btnText, actionUrl, successTitle, successText, actionComponent) {
    this.sweetConfirm(title || '您确定要删除吗？',
        text || '删除后无法恢复，请谨慎操作！',
        btnText || ' 确定删除! ',
        actionUrl,
        successTitle || '删除成功!',
        successText || '您已经成功删除该数据！',
        actionComponent);
}

/**
 * 获取 cookie 信息
 * @param name
 * @returns {string|null}
 */
function getCookie(name) {
    var cookieString = document.cookie;
    var cookies = cookieString.split("; ");
    for (var i = 0; i < cookies.length; i++) {
        var arr = cookies[i].split("=");
        if (arr[0] == name) {
            return arr[1];
        }
    }
    return null;
}

function doActivateEmail(userId) {
    ajaxGet(jpress.cpath + "/commons/emailactivate?userId=" + userId, function (result) {
        alert(result.message);
    })
}

function closeLayerAndRefresh() {
    if (typeof parent != "undefined" && parent.layer) {
        parent.layer.data.needRefresh = true;
        parent.layer.closeAll();
    }
}


function closeLayer() {
    if (typeof parent != "undefined" && parent.layer) {
        parent.layer.closeAll();
    }
}

