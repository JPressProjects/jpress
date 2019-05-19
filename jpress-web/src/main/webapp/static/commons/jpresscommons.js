$(document).ready(function () {

    initTableActions();
    initToastr();
    initSwitchery();
    initDomainSpan();
    initSlugSpan();
    initDatePicker();
    initDatetimePicker();
    initAutoAjaxSubmit();

});

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

function openWindow(options) {

    if (typeof options.content == "undefined") {
        toastr.error('内容不能为空');
        return ;
    }

    options.title = options.title || '信息';
    options.type = options.type || 2;
    options.offset = options.offset || 'auto';
    options.anim = options.anim || 5;
    options.shadeClose = true;
    options.area = options.area || ['580px', '530px'];
    options.fixed = options.fixed || false;

    layer.open(options);
}

function initParentTable (tableId, url, queryParams, fields, subUrl) {
    tableId = tableId || "_table";

    $(tableId).bootstrapTable({
        url: url,
        method: 'get',
        editable: false,                //开启编辑模式
        clickToSelect: true,
        uniqueId: 'id',
        striped: true,
        detailView: true,//父子表
        classes: 'table-no-bordered',
        cache: false,					// 是否使用缓存
        pagination: true,				// 是否显示分页
        queryParams: queryParams,		// 传递参数
        sidePagination: 'server', 		//分页方式：client客户端分页，server服务端分页（*）
        paginationLoop: false,
        paginationPreText: '上一页',
        paginationNextText: '下一页',
        pageNumber: 1,
        pageSize: 10,
        smartDisplay: false,
        undefinedText: '',
        columns: fields,
        onExpandRow: function (index, row, $detail) {
            initSubTable(index, row, $detail, fields, url);
        }
    });
}

function initSubTable (index, row, $detail, fields, url) {
    var parentId = row.id;
    var sub_table = $detail.html('<table></table>').find('table');
    $(sub_table).bootstrapTable({
        url: url,
        method: 'get',
        clickToSelect: true,
        uniqueId: 'id',
        striped: true,
        detailView: true,                                   //父子表
        //classes: 'table-no-bordered',
        cache: false,                                       // 是否使用缓存
        queryParams: {parentId: parentId, size:100},        // 传递参数
        sidePagination: 'server',                           //分页方式：client客户端分页，server服务端分页（*）
        undefinedText: '',
        columns: fields,
        onExpandRow: function (index, row, $detail) {
            initSubTable(index, row, $detail, fields, url);
        }
    });
}

function initEditTable (options) {

    let url = options.url;
    if (url == null || url == '') {
        toastr.error('URL不能为空');
        return ;
    }

    let fields = options.fields || [];
    let tableId = options.tableId || "_table";
    let queryParams = options.queryParams || {};
    let editSaveFunc = options.editSaveFunc || function () {};
    let clickCellFunc = options.clickCellFunc || function () {};

    $(tableId).bootstrapTable({
        url: url,
        method: 'get',
        editable: true,//开启编辑模式
        clickToSelect: false,
        uniqueId: 'id',
        striped: true,
        silent: true,                   // 静默加载
        classes: 'table-no-bordered',
        cache: false,					// 是否使用缓存
        pagination: true,				// 是否显示分页
        queryParams: queryParams,		// 传递参数
        sidePagination: 'server', 		// 分页方式：client客户端分页，server服务端分页（*）
        paginationLoop: false,
        paginationPreText: '上一页',
        paginationNextText: '下一页',
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 30, 50, 100],
        smartDisplay: false,
        undefinedText: '-',
        columns: fields,
        maintainSelected: true,
        onEditableSave: editSaveFunc,
        onClickCell: clickCellFunc
    });
}