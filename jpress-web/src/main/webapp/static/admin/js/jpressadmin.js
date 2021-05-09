/**
 * 扩展 String 类型的方法
 */
function initStringMethods() {
    if (typeof String.prototype.startsWith !== 'function') {
        String.prototype.startsWith = function (prefix) {
            return this.slice(0, prefix.length) === prefix;
        };
    }

    if (typeof String.prototype.endsWith !== 'function') {
        String.prototype.endsWith = function (suffix) {
            return this.indexOf(suffix, this.length - suffix.length) !== -1;
        };
    }
}


/**
 * 设置当前选中菜单
 */
function initSidebarActive() {

    var pathName = location.pathname;
    if (getContextPath() + "/admin" == pathName || getContextPath() + "/admin/" == pathName) {
        pathName = getContextPath() + "/admin/index"
    }

    setActiveMenu(pathName);
}


function setActiveMenu(pathName) {

    var activeTreeview, activeLi;

    $("#sidebar-menu").children().each(function () {
        var li = $(this);
        li.find('a').each(function () {
            var href = $(this).attr("href");
            if (pathName == href) {
                activeTreeview = li;
                activeLi = $(this);
                return false;
            } else if (href.endsWith("/list")) {
                href = href.substr(0, href.indexOf("/list"));
                if (pathName == href) {
                    activeTreeview = li;
                    activeLi = $(this);//.parent();
                    return false;
                }
            }
        });
    });

    if (!activeTreeview) {
        var indexOf = pathName.lastIndexOf("/");
        if (indexOf > 0) {
            pathName = pathName.substr(0, indexOf);
            setActiveMenu(pathName);
        }
    } else {
        if (activeTreeview) {
            activeTreeview.addClass("menu-open");
            activeTreeview.children(":first").addClass("active");
        }
        if (activeLi) {
            activeLi.addClass("active");
        }
    }
}


function getContextPath() {
    if (typeof JbootAdmin == 'undefined') {
        return ""
    } else {
        return JbootAdmin.cpath;
    }
}


/**
 * 设置 layer 组件
 */
function initLayerComponent() {
    if (typeof layer != "undefined") {
        layer.config({
            extend: 'jpress/style.css',
            skin: 'layer-ext-jbootadmin'
        });

        $("[open-type='layer']").each(function () {
            if ($(this).tagName == 'input' || $(this).tagName == 'textarea') {
                $(this).on("focus", function (event) {
                    event.preventDefault();
                    _initLayerByComponent($(this));
                })
            } else {
                $(this).on("click", function (event) {
                    event.preventDefault();
                    _initLayerByComponent($(this));
                })
            }
        });
    }
}

function _initLayerByComponent(component) {
    layer.data = null;
    var dataset = component.data();
    var options = {
        type: dataset.layerType || 2,
        title: dataset.layerTitle || '内容',
        anim: dataset.layerAnim || 2,
        shadeClose: dataset.layerShadeClose ? (/^true$/i).test(dataset.layerShadeClose) : true,
        shade: dataset.layerShade || 0.5,
        area: dataset.layerArea ? eval(dataset.layerArea) : ['80%', '80%'],
        content: dataset.layerContent || component.attr('href'),
        end: function () {

            // 数据绑定
            if (layer.data && dataset.layerBinds) {
                var bindArrays = dataset.layerBinds.split(",");
                var i = 0;
                for (; i < bindArrays.length; i++) {
                    var query = bindArrays[i].split(":")[0].trim();
                    var attr = bindArrays[i].split(":")[1].trim();
                    $(query).val(layer.data[attr]);
                    $(query).valid();

                }
            }

            // 刷新机制
            var endFunction = dataset.layerEnd;
            if ("reload" == endFunction) {
                location.reload();
            } else if (endFunction) {
                eval(endFunction)(layer.data);
            } else {
                var reloadIf = dataset.layerReloadIf;
                if ((reloadIf && layer.data[reloadIf]) || (layer.data && layer.data.reload === true)) {
                    location.reload();
                }
            }
        }
    };
    layer.open(options);
}



/**
 * 设置 tooltip 组件
 */
function initTooltip() {
    $('[data-toggle="tooltip"]').tooltip();
    $('[data-render="tooltip"]').tooltip();
}




/**
 * 设置 表格 的全选按钮
 */
function initDatatableCheckBox() {
    $('.tableBox').on('change', function () {
        var boxChecked = $(this).prop("checked");
        $('[name="tableItem"]').each(function (row) {
            $(this).prop('checked', boxChecked);
            if (boxChecked) {
                $(this).closest('tr').addClass("selected")
                $('.DTFC_RightBodyLiner table tbody tr').eq(row).addClass("selected");
                $('.DTFC_LeftBodyLiner table tbody tr').eq(row).addClass("selected");
            } else {
                $(this).closest('tr').removeClass("selected")
                $('.DTFC_RightBodyLiner table tbody tr').eq(row).removeClass("selected");
                $('.DTFC_LeftBodyLiner table tbody tr').eq(row).removeClass("selected");
            }
        });

        $('.tableBox').each(function () {
            $(this).prop('checked', boxChecked);
        });
    });

    $('[name="tableItem"]').on('change', function () {

        //单选框就需要移除其他选中的列的内容
        if ($(this).attr("type") == "radio") {
            $('.selected').removeClass("selected");
        }

        var boxChecked = $(this).prop("checked");
        if (boxChecked) {
            $(this).closest('tr').addClass("selected");
            var row = $(this).closest('tr').attr('data-dt-row');
            if (row && row.toString() != "") {
                $('[data-render="datatable"] tbody tr').eq(row).addClass("selected");
                $('.DTFC_RightBodyLiner table tbody tr').eq(row).addClass("selected");
                $('.DTFC_LeftBodyLiner table tbody tr').eq(row).addClass("selected");
            }
        } else {
            $('.tableBox').prop("checked", false);
            $(this).closest('tr').removeClass("selected")
            var row = $(this).closest('tr').attr('data-dt-row');
            // if (row && row.toString() === "0") {
            if (row && row.toString() != "") {
                // $('[data-render="datatable"]').children("tbody").children("tr").eq(row).removeClass("selected");
                $('[data-render="datatable"] tbody tr').eq(row).removeClass("selected");
                $('.DTFC_RightBodyLiner table tbody tr').eq(row).removeClass("selected");
                $('.DTFC_LeftBodyLiner table tbody tr').eq(row).removeClass("selected");
            }
        }
    });
}



/**
 * 设置 返回按钮 的动作
 */
function initBackButton() {
    $('.back').on('click', function () {
        if (window.top !== window.self && parent.layer) {
            parent.layer.closeAll();
        } else {
            // window.history.back();
            window.location.href = document.referrer;
        }
    });
}

/**
 * 设置时间选择器
 */
function initDatePicker() {
    // git https://github.com/t1m0n/air-datepicker
    // doc http://t1m0n.name/air-datepicker/docs/
    if ($('').datepicker) {
        $('.date,[data-render="date"]').each(function () {
            var timepicker = $(this).hasClass('time');
            $(this).datepicker({
                language: 'zh',
                timepicker: timepicker
            })
        });
    }
}


/**
 *  设置 select2 组件
 */
function initSelect2() {
    // select2 的属性是可以通过 data-** 来渲染的，详情: https://select2.org/configuration/data-attributes
    if ($('').select2) {
        $('[data-render="select2"]').select2();
    }
}


/**
 * 设置 toastr
 */
function initToastr() {
    if (typeof toastr != "undefined") {
        toastr.options.progressBar = true;
        toastr.options.closeButton = true;
        toastr.options.timeOut = 2000;
        toastr.options.positionClass = "toast-top-center";
    }
}


/**
 * 设置全局 验证器
 */
function initValidate() {
    //https://jqueryvalidation.org/documentation/
    $.validator.setDefaults({
        errorElement: 'span',
        errorPlacement: function (error, element) {
            error.addClass('invalid-feedback');
            element.parent().append(error);
        },
        highlight: function (element, errorClass, validClass) {
            $(element).addClass('is-invalid');
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).removeClass('is-invalid');
            // if ($(element).hasClass('is-invalid')) {
            //     $(element).removeClass('is-invalid');
            //     $(element).addClass('is-valid');
            // }
        }
    });

    $.validator.addMethod("mobile", function (value, element) {
        var length = value.length;
        var mobile = /^(((1[3-9]{1}))+\d{9})$/;
        return this.optional(element) || (length == 11 && mobile.test(value));
    }, "手机号码格式错误");


    // 支持多个相同 name 的验证
    // 参考 https://stackoverflow.com/questions/931687/using-jquery-validate-plugin-to-validate-multiple-form-fields-with-identical-nam/4136430#4136430
    $.validator.prototype.checkForm = function () {
        //overriden in a specific page
        this.prepareForm();
        for (var i = 0, elements = (this.currentElements = this.elements()); elements[i]; i++) {
            if (this.findByName(elements[i].name).length !== undefined && this.findByName(elements[i].name).length > 1) {
                for (var cnt = 0; cnt < this.findByName(elements[i].name).length; cnt++) {
                    try {
                        var checkObj = this.findByName(elements[i].name)[cnt];
                        //hidden 类型无法进行 check，防止开发人员在 hidden 的 input 添加验证
                        this.check(checkObj);
                    } catch (e) {
                    }
                }
            } else {
                this.check(elements[i]);
            }
        }
        return this.valid();
    };
}


/**
 * 设置 form 的 ajax 自动提交
 */
function initAjaxSubmitForms() {
    $('.ajaxSubmit').each(function (key, form) {
        $(form).validate({
            // ignore: ".ignore",
            submitHandler: function (form) {
                if (typeof (CKEDITOR) != "undefined") {
                    for (instance in CKEDITOR.instances) {
                        CKEDITOR.instances[instance].updateElement();
                    }
                }
                __form = $(form);

                var successFun = __form.attr('data-success-function');
                var successGoto = __form.attr('data-success-goto');
                var successMsg = __form.attr('data-success-message');

                var failFun = __form.attr('data-fail-function');
                var failMsg = __form.attr('data-fail-message');

                var binds = __form.attr('data-binds');

                __form.ajaxSubmit({
                    type: "post",
                    success: function (result) {

                        // 数据绑定
                        if (binds) {
                            var bindArrays = binds.split(",");
                            var i = 0;
                            for (; i < bindArrays.length; i++) {
                                var query = bindArrays[i].split(":")[0].trim();
                                var attr = bindArrays[i].split(":")[1].trim();
                                $(query).val(result[attr]);
                                $(query).valid();
                            }
                        }


                        if (result.state == "ok") {

                            if (successFun) {
                                eval(successFun)(result);
                                return;
                            }

                            if (successMsg) {
                                Utils.showMessage(successMsg, successGoto);
                                return;
                            }

                            if (result.message) {
                                Utils.showMessage(result.message, successGoto);
                                return;
                            }

                            if (result.data && result.data.message) {
                                Utils.showMessage(result.data.message, successGoto);
                                return;
                            }

                            if (successGoto) {
                                location.href = successGoto;
                                return
                            }


                        }
                        //fail
                        else {
                            if (failFun) {
                                eval(failFun)(result);
                                return;
                            }

                            if (failMsg) {
                                Utils.showErrorMessage(failMsg);
                                return
                            }

                            if (result.message) {
                                Utils.showErrorMessage(result.message);
                                return;
                            }

                            if (result.data && result.data.message) {
                                Utils.showMessage(result.data.message);
                                return;
                            }

                            Utils.showErrorMessage('操作失败。')
                        }
                    },
                    error: function () {
                        Utils.showErrorMessage('系统错误，请稍后重试。');
                    }
                });
            }
        });
    });
}

/**
 * 设置 reset 按钮的动作
 */
function initResetBtn() {
    $('[type="reset"]').on('click', function (e) {
        $(this).closest('form').find('[type="text"]').val("");
        $(this).closest('form').find('textarea').val("");
        e.preventDefault();
    });
}

/**
 * 批量操作按钮的动作设置
 */
function initBatchExecBtn() {
    $('.batchExec').on('click', function () {
        var action = $('[name="action"]').val();
        if (!action || action == "") {
            alert("请先选择操作类型");
            return;
        } else {
            var tableSelectedIds = Utils.getTableSelectedIds();
            if (!tableSelectedIds || tableSelectedIds == "") {
                Utils.sweetAlert('您没有选择任何的数据');
                return;
            } else {
                if (action === "exportAction") {
                    Utils.datatable.button('.buttons-excel').trigger();
                } else if (action === "printAction") {
                    Utils.datatable.button('.buttons-print').trigger();
                } else {
                    var selectedAction = $('[name="action"] option:selected');
                    var href = action + (action.indexOf('?' > 0) ? "?ids=" : "&ids=") + tableSelectedIds;
                    var title = selectedAction.attr("data-title");
                    var text = selectedAction.attr("data-text");
                    var btnText = selectedAction.attr("data-btn-text");
                    var successTitle = selectedAction.attr("data-success-title");
                    var successText = selectedAction.attr("data-success-text");
                    if ("del-confirm" === selectedAction.attr("open-type")) {
                        Utils.sweetConfirmDel(title, text, btnText, href, successTitle, successText, selectedAction);
                    } else {
                        Utils.sweetConfirm(title, text, btnText, href, successTitle, successText, selectedAction);
                    }
                }
            }
        }
    });
}

function initConfirmOpenType() {
    $('[open-type="confirm"]').on('click', function (e) {
        e.preventDefault();
        var href = $(this).attr("href");
        var title = $(this).attr("data-title");
        var text = $(this).attr("data-text");
        var btnText = $(this).attr("data-btn-text");
        var successTitle = $(this).attr("data-success-title");
        var successText = $(this).attr("data-success-text");
        Utils.sweetConfirm(title, text, btnText, href, successTitle, successText, $(this));
    });

    $('[open-type="del-confirm"]').on('click', function (e) {
        e.preventDefault();
        var href = $(this).attr("href");
        var title = $(this).attr("data-title");
        var text = $(this).attr("data-text");
        var btnText = $(this).attr("data-btn-text");
        var successTitle = $(this).attr("data-success-title");
        var successText = $(this).attr("data-success-text");
        Utils.sweetConfirmDel(title, text, btnText, href, successTitle, successText, $(this));
    });
}


function initAjaxOpenType() {
    $('[open-type="ajax"]').on('click', function (e) {
        e.preventDefault();
        var href = $(this).attr("href");
        var ajaxMethod = $(this).attr("data-ajax-method");
        var submitInputs = $(this).attr("data-submit-inputs");

        var successFun = $(this).attr('data-success-function');
        var successGoto = $(this).attr('data-success-goto');
        var successMsg = $(this).attr('data-success-message');

        var failFun = $(this).attr('data-fail-function');
        var failMsg = $(this).attr('data-fail-message');

        var binds = $(this).attr('data-binds');

        var okFunction = function (result) {
            // 数据绑定
            if (binds) {
                var bindArrays = binds.split(",");
                var i = 0;
                for (; i < bindArrays.length; i++) {
                    var query = bindArrays[i].split(":")[0].trim();
                    var attr = bindArrays[i].split(":")[1].trim();
                    $(query).val(result[attr]);
                    $(query).valid();
                }
            }

            if (successFun) {
                eval(successFun)(result);
                return;
            }

            if (successMsg) {
                Utils.showMessage(successMsg, successGoto);
                return;
            }

            if (successGoto) {
                location.href = successGoto;
                return
            }

            if (result && result.message) {
                Utils.showMessage(result.message);
                return;
            }

        };

        var failFunction = function (result) {
            if (failFun) {
                eval(failFun)(result);
                return;
            }

            if (failMsg) {
                Utils.showErrorMessage(failMsg);
                return
            }

            if (result.message) {
                Utils.showErrorMessage(result.message);
            } else {
                Utils.showErrorMessage('操作失败。')
            }
        };


        if (ajaxMethod && ajaxMethod.toLowerCase() == "post") {
            var postData = {};
            if (submitInputs && submitInputs != "") {
                var intputArray = submitInputs.split(",");
                var x = 0;
                for (; x < intputArray.length; x++) {
                    var name = $(intputArray[x].trim()).attr("name");
                    var val = $(intputArray[x].trim()).val();
                    postData[name] = val;
                }
            }
            Utils.ajaxPost(href, postData, okFunction, failFunction);
        } else {
            Utils.ajaxGet(href, okFunction, failFunction);
        }
    });
}





function initInputActions() {
    $('.input-action>input').on('keyup', function () {
        var val = $(this).val();
        if (val && val != "") {
            $(this).siblings().removeClass("fa-grip-horizontal");
            $(this).siblings().addClass("fa-times");
        } else {
            $(this).siblings().removeClass("fa-times");
            $(this).siblings().addClass("fa-grip-horizontal");
        }
    });


    $('.input-action>span').on('click', function () {
        if ($(this).hasClass("fa-times")) {
            $(this).siblings().val("");
            $(this).removeClass("fa-times");
            $(this).addClass("fa-grip-horizontal");
        }
        // open layer
        else if ($(this).hasClass("fa-grip-horizontal")) {
            layer.data = null;
            var dataset = $(this).data();//.dataset;
            var options = {
                type: dataset.layerType || 2,
                title: dataset.layerTitle || '内容',
                anim: dataset.layerAnim || 2,
                shadeClose: dataset.layerShadeClose ? (/^true$/i).test(dataset.layerShadeClose) : true,
                shade: dataset.layerShade || 0.5,
                area: dataset.layerArea ? eval(dataset.layerArea) : ['80%', '80%'],
                content: dataset.layerContent || $(this).attr('href'),
                end: function () {

                    // 数据绑定
                    if (layer.data && dataset.layerBinds) {
                        var bindArrays = dataset.layerBinds.split(",");
                        var i = 0;
                        for (; i < bindArrays.length; i++) {
                            var query = bindArrays[i].split(":")[0].trim();
                            var attr = bindArrays[i].split(":")[1].trim();
                            $(query).val(layer.data[attr]);
                            $(query).valid();
                        }
                    }

                }
            };
            layer.open(options);
        }
    });

    var val = $('.input-action>input').val();
    if (val && val != "") {
        $('.input-action>span').addClass("fa-times");
    } else {
        $('.input-action>span').addClass("fa-grip-horizontal");
    }

}




function initShowTabByParas() {
    var showTab = Utils.getPara("showtab");
    if (showTab && showTab != "") {
        $('#' + showTab).tab('show');
    }
}


function initPagenationPagesize() {
    $('.pagination-pagesize').on('change', function () {
        var pagesize = $(this).find(":selected").val();
        var pathName = window.location.pathname;
        document.cookie = "pagesize=" + pagesize;
        var url = pathName;
        var query = window.location.search.substring(1);
        if (query && query != "") {
            var vars = query.split("&");
            for (var i = 0; i < vars.length; i++) {
                var pair = vars[i].split("=");
                if (pair[0] != "pagesize" && pair[0] != "page") {
                    if (url.indexOf("?") == -1) {
                        url += "?"
                    }
                    url = url + pair[0] + "=" + pair[1];
                }
            }
        }
        window.location.href = url;
    });
}

$(document).ready(function () {

    initStringMethods();

    initSidebarActive();

    initLayerComponent();

    initTooltip();

    initBackButton();

    initDatePicker();

    initToastr();
    initValidate();
    initAjaxSubmitForms();
    initAjaxOpenType();

    initSelect2();
    initResetBtn();

    initBatchExecBtn();

    initConfirmOpenType();

    initInputActions();

    initDatatableCheckBox();

    initShowTabByParas();

    initPagenationPagesize();

});