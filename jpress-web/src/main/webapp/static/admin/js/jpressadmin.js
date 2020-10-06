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

/*
*计算金额时使用该方法来规避精度问题
* */
function initNumberCompute() {
    //除法函数，用来得到精确的除法结果
//说明：javascript的除法结果会有误差，在两个浮点数相除的时候会比较明显。这个函数返回较为精确的除法结果。
//调用：accDiv(arg1,arg2)
//返回值：arg1除以arg2的精确结果
    function accDiv(arg1, arg2) {
        var t1 = 0, t2 = 0, r1, r2;
        try {
            t1 = arg1.toString().split(".")[1].length
        } catch (e) {
        }
        try {
            t2 = arg2.toString().split(".")[1].length
        } catch (e) {
        }
        with (Math) {
            r1 = Number(arg1.toString().replace(".", ""));
            r2 = Number(arg2.toString().replace(".", ""));
            return (r1 / r2) * pow(10, t2 - t1);
        }
    }

//给Number类型增加一个div方法，调用起来更加方便。
    Number.prototype.div = function (arg) {
        return accDiv(this, arg);
    }

//乘法函数，用来得到精确的乘法结果
//说明：javascript的乘法结果会有误差，在两个浮点数相乘的时候会比较明显。这个函数返回较为精确的乘法结果。
//调用：accMul(arg1,arg2)
//返回值：arg1乘以arg2的精确结果
    function accMul(arg1, arg2) {
        var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
        try {
            m += s1.split(".")[1].length
        } catch (e) {
        }
        try {
            m += s2.split(".")[1].length
        } catch (e) {
        }
        return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m)
    }

//给Number类型增加一个mul方法，调用起来更加方便。
    Number.prototype.mul = function (arg) {
        return accMul(arg, this);
    }

//加法函数，用来得到精确的加法结果
//说明：javascript的加法结果会有误差，在两个浮点数相加的时候会比较明显。这个函数返回较为精确的加法结果。
//调用：accAdd(arg1,arg2)
//返回值：arg1加上arg2的精确结果
    function accAdd(arg1, arg2) {
        var r1, r2, m;
        try {
            r1 = arg1.toString().split(".")[1].length
        } catch (e) {
            r1 = 0
        }
        try {
            r2 = arg2.toString().split(".")[1].length
        } catch (e) {
            r2 = 0
        }
        m = Math.pow(10, Math.max(r1, r2));
        return (arg1 * m + arg2 * m) / m
    }

//给Number类型增加一个add方法，调用起来更加方便。
    Number.prototype.add = function (arg) {
        return accAdd(arg, this);
    };

    function accSub(arg1, arg2) {
        var r1, r2, m, n;
        try {
            r1 = arg1.toString().split(".")[1].length
        } catch (e) {
            r1 = 0
        }
        try {
            r2 = arg2.toString().split(".")[1].length
        } catch (e) {
            r2 = 0
        }
        m = Math.pow(10, Math.max(r1, r2));

//动态控制精度长度
        n = (r1 >= r2) ? r1 : r2;
        return ((arg1 * m - arg2 * m) / m).toFixed(n);
    }

    Number.prototype.sub = function (arg) {
        return accSub(arg, this);
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
            extend: 'jbootadmin/style.css',
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
 * 设置 layer-edit 组件
 */
function initLayerEdit() {
    if (typeof layer != "undefined") {

        layer.config({
            extend: 'jbootadmin/style.css',
            skin: 'layer-ext-jbootadmin'
        });

        $("[open-type='layer-edit']").each(function () {
            if ($(this).tagName == 'input' || $(this).tagName == 'textarea') {
                $(this).unbind("focus");//防止 html 动态添加调用 initLayerEdit 进行多次绑定
                $(this).on("focus", function (event) {
                    event.preventDefault();
                    _initLayerEdit($(this));
                })
            } else {
                $(this).unbind("click");//防止 html 动态添加调用 initLayerEdit 进行多次绑定
                $(this).on("click", function (event) {
                    event.preventDefault();
                    _initLayerEdit($(this));
                })
            }
        });
    }
}

function initLayerPassword() {
    if (typeof layer != "undefined") {

        layer.config({
            extend: 'jbootadmin/style.css',
            skin: 'layer-ext-jbootadmin'
        });

        $("[open-type='layer-password']").each(function () {
            $(this).unbind();
            if ($(this).tagName == 'input' || $(this).tagName == 'textarea') {
                $(this).unbind("focus");//防止 html 动态添加调用 initLayerPassword 进行多次绑定
                $(this).on("focus", function (event) {
                    event.preventDefault();
                    _initLayerPassword($(this));
                })
            } else {
                $(this).unbind("click");//防止 html 动态添加调用 initLayerPassword 进行多次绑定
                $(this).on("click", function (event) {
                    event.preventDefault();
                    _initLayerPassword($(this));
                })
            }
        });
    }
}

function _initLayerEdit(component) {
    layer.data = null;
    component = $(component);
    var dataset = component.data();
    var id = component.attr('data-value');
    var value = dataset.content || "";
    var options = {
        type: dataset.layerType || 1,
        title: dataset.layerTitle || '请输入',
        anim: dataset.layerAnim || 2,
        shadeClose: dataset.layerShadeClose ? (/^true$/i).test(dataset.layerShadeClose) : true,
        shade: dataset.layerShade || 0.5,
        area: dataset.layerArea ? eval(dataset.layerArea) : ['400px', '220px'],
        content: '<div style="padding:20px 20px 0 20px;"> <textarea rows="3" class="form-control" placeholder="请输入内容..." maxlength="1024" id="layer-edit">' + value + '</textarea> </div>',
        btn: ['确定', '取消'],
        yes: function (index, layerno) {
            var layerEditContent = top.$('#layer-edit').val();
            component.attr("data-content", layerEditContent);

            var layerSubmitUrl = dataset.submitUrl;
            var layerSubmitSuccessFunction = dataset.submitSuccessFunction;
            if (layerSubmitUrl) {
                Utils.ajaxPost(layerSubmitUrl, {content: layerEditContent, id: id}, function (data) {
                    if (layerSubmitSuccessFunction) {
                        eval(layerSubmitSuccessFunction)(data, component);
                    }
                });
            }

            var layerSyncTo = dataset.syncTo;
            if (layerSyncTo) {
                var layerSyncToArrays = layerSyncTo.split(",");
                layerSyncToArrays.forEach((item, index, array) => {
                    $(item).val(layerEditContent);
                    $(item).text(layerEditContent);
                })
            }

            var layerCloseFunction = dataset.closeFunction;
            if (layerCloseFunction) {
                eval(layerCloseFunction)(layerEditContent, component);
            }

            layer.close(index);
        }
    };

    layer.open(options);
}


function _initLayerPassword(component) {
    layer.data = null;
    component = $(component);
    let dataset = component.data();
    let id = component.attr('data-value');
    let value = dataset.content || "";
    let options = {
        type: dataset.layerType || 1,
        title: dataset.layerTitle || '请输入',
        anim: dataset.layerAnim || 2,
        shadeClose: dataset.layerShadeClose ? (/^true$/i).test(dataset.layerShadeClose) : true,
        shade: dataset.layerShade || 0.5,
        area: dataset.layerArea ? eval(dataset.layerArea) : ['450px', '200px'],
        content: '<div style="padding:20px 20px 0 20px;"> <input  type="password" class="form-control" placeholder="请输入密码..." id="layer-password" value=""></div>',
        btn: ['确定', '取消'],
        yes: function (index) {
            let layerPasswordContent = $('#layer-password').val();
            component.attr("data-content", layerPasswordContent);

            let layerSubmitUrl = dataset.submitUrl;
            let layerSubmitSuccessFunction = dataset.submitSuccessFunction;
            if (layerSubmitUrl) {
                Utils.ajaxPost(layerSubmitUrl, {content: layerPasswordContent, id: id}, function (data) {
                    if (layerSubmitSuccessFunction) {
                        eval(layerSubmitSuccessFunction)(data, component);
                    }
                });
            }

            let layerSyncTo = dataset.syncTo;
            if (layerSyncTo) {
                let layerSyncToArrays = layerSyncTo.split(",");
                layerSyncToArrays.forEach((item, index, array) => {
                    $(item).val(layerPasswordContent);
                    $(item).text(layerPasswordContent);
                })
            }

            let layerCloseFunction = dataset.closeFunction;
            if (layerCloseFunction) {
                eval(layerCloseFunction)(layerPasswordContent, component);
            }

            layer.close(index);
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
 * 设置 datatable 组件
 */
function initDatatable() {
    if ($('').DataTable) {
        try {
            var datatable = $('[data-render="datatable"]');

            var printTitle = datatable.attr("data-print-title");
            var excelTitle = datatable.attr("data-excel-title");
            var fixedColumns = datatable.attr("data-fixed");
            var fixedColumnsConfig = fixedColumns ? {
                leftColumns: parseInt(fixedColumns.split(',')[0].trim()),
                rightColumns: parseInt(fixedColumns.split(',')[1].trim())
            } : false;

            Utils.datatable = datatable.DataTable({
                "responsive": fixedColumns ? false : true,
                "autoWidth": false,
                "searching": false,
                "paging": false,
                "ordering": false,
                "info": false,
                "language": {
                    "emptyTable": "暂无数据"
                },
                buttons: [
                    {
                        extend: 'print',
                        title: printTitle || '数据内容',
                        'bom': true,
                        exportOptions: {
                            columns: "th:not(.no-export)",
                            rows: ".selected"
                        }
                    },
                    {
                        extend: 'excel',
                        title: excelTitle || '导出数据',
                        'bom': true,
                        exportOptions: {
                            columns: "th:not(.no-export)",
                            rows: ".selected"
                        }
                    }
                ],
                fixedColumns: fixedColumnsConfig,
                scrollX: fixedColumns ? true : false,
                scrollCollapse: fixedColumns ? true : false,
            });


            $('.select tr input,.select tr a').click(function (e) {
                //tr 里的input 和 a元素点击后，不让 tr 触发 click 事件
                e.stopPropagation();
            });

            $('.select tbody tr').on('click', function () {
                $(this).find('input[name="tableItem"]').click();
            })
        } catch (e) {

        }
    }
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
 * 设置 touchSpin 组件
 */
function initTouchSpin() {
    // https://www.virtuosoft.eu/code/bootstrap-touchspin/
    if ($('').TouchSpin) {
        $('[data-render="touchSpin"]').each(function () {
            var decimals = $(this).attr("data-decimals");
            if (!decimals) decimals = 0;
            $(this).TouchSpin({
                //    // initval: 0
                max: 1000000000,
                decimals: decimals,
                forcestepdivisibility: 'none',
                verticalbuttons: true,
                buttondown_class: "btn btn-default",
                buttonup_class: "btn btn-default"
            })
        });
    }
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
 *  设置 popover 组件
 */

var showPopoverComponent = null;

function initPopover() {
    //https://github.com/sandywalker/webui-popover
    //用来替代 bootstrap 的 popover,因为 bootstrap 的 popover 功能太弱啦，比如 不支持 bottom-right 的 placement , 自定义的 html 不支持链接  不支持设置 onClick 属性等等
    $("[data-toggle='popover'],[open-type='popover']").each(function () {
        var id = $(this).attr("data-content-id");
        var closeOnBlur = $(this).attr('data-close-on-blur');
        if (id) {
            $(this).attr("data-content", $('#' + id).html());
        }
        var that = $(this);
        $(this).webuiPopover({
            onShow: function (el) {
                showPopoverComponent = that;
            },
            onHide: function (el) {
                // console.log("onHide:",el);
                // $(el).click();
                $(el).remove();
            },
            trigger: 'manual',
        });

        $(this).on('focus', function () {
            var showValidateFunction = $(this).attr("data-show-validate");
            if (showValidateFunction) {
                var validateOk = eval(showValidateFunction)(that);
                if (validateOk) {
                    $(this).webuiPopover('show');
                }
            } else {
                $(this).webuiPopover('show');
            }
        });

        // if ("false" != closeOnBlur) {
        //     $(this).on('blur', function () {
        //         $(this).webuiPopover('hide');
        //     });
        // }
    });


    $(document).mouseup(function (e) {
        var popups = $("[data-toggle='popover']");
        popups.each(function () {
            var popComponent = $(this);
            var openedComponent = $(".webui-popover-inner");
            if (!$(popComponent).is(e.target) && !openedComponent.is(e.target) && openedComponent.has(e.target).length == 0) {
                popComponent.webuiPopover('hide');
            }
        });
        initSwitchTab();
    });

}

function initPressTab() {
    $(document).on('keyup', function (any) {
        let keycode = (any.keyCode ? any.keyCode : any.which);
        if (keycode == 9) {
            let attr = $(document.activeElement).attr('data-toggle');
            if (attr != "popover") {

                var popups = $("[data-toggle='popover']");
                popups.each(function () {
                    var popComponent = $(this);
                    var openedComponent = $(".webui-popover-inner");
                    popComponent.webuiPopover('hide');
                });

            }

        }

    });
}


function setPopoverData(value) {
    if (showPopoverComponent) {
        var popoverSetFunction = showPopoverComponent.attr('data-popover-set');
        if (popoverSetFunction) {
            eval(popoverSetFunction)(value, showPopoverComponent);
            showPopoverComponent.change().blur();
        } else {
            showPopoverComponent.val(value);
            showPopoverComponent.change().blur();

        }

        $(showPopoverComponent).webuiPopover('hide');
        showPopoverComponent = null;
    }
}

function initSwitchTab() {
    $('.nav-link.product').each(function () {
        $(this).hover(function () {
            $(this).tab('show');
        });
    });

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

        var datafor = elem.getAttribute("for");
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

function initCkEdtiorComponent() {
    $('[data-render="ckeditor"]').each(function () {
        var id = $(this).attr("id");
        var height = $(this).attr("data-height");
        if (!height) height = 350;
        initCkEdtior(id, height);
    });
}

var _dialogShowEvent;

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

    var myEditor = CKEDITOR.replace(editor, {
        autoUpdateElement: true,
        removePlugins: 'easyimage,cloudservices',
        extraPlugins: 'entities,codesnippet,uploadimage,flash,image,wordcount,notification,html5audio,html5video,widget,widgetselection,clipboard,lineutils',
        codeSnippet_theme: 'monokai_sublime',
        height: height,
        uploadUrl: Utils.getContextPath() + '/commons/ckeditor/upload',
        imageUploadUrl: Utils.getContextPath() + '/commons/ckeditor/upload',
        filebrowserUploadUrl: Utils.getContextPath() + '/commons/ckeditor/upload',
        // filebrowserBrowseUrl: Utils.getContextPath() + '/admin/attachment/browse',
        language: 'zh-cn'
    });


    myEditor.on('instanceReady', function () {
        myEditor.setKeystroke(CKEDITOR.ALT.CTRL + 83, 'save'); //  Ctrl+s
        myEditor.setKeystroke(1114195, 'save'); // mac command +s
        // 扩展CKEditor的 ctrl + s 保存命令,方便全屏编辑时快捷保存
        myEditor.addCommand('save', {
            exec: function () {
                var ds = window.doSubmit;
                ds && ds();
            }
        });
    });


    myEditor.on("dialogShow", function (event) {
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

    return myEditor;
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

/**
 *
 */
function initDataSync() {
    var syncObjects = {};

    $('[data-sync]').each(function () {

        $(this).on('input change', function () {
            var name = $(this).attr('data-sync');
            if (name) {
                var selector = '[data-sync="' + name + '"]';
                syncObjects[name] = Number(0);
                $(selector).each(function () {
                    if ($(this).val()) {
                        syncObjects[name] = syncObjects[name] + (Number($(this).val()));
                    }
                });

                var dataSyncTo = $(this).attr('data-sync');
                if (dataSyncTo) {
                    $(dataSyncTo).text(syncObjects[name]).change();
                    $(dataSyncTo).val(syncObjects[name]).change().blur();
                }
            }
        });

        /* var iName = $(this).attr('name');
         var value = $(this).val();
         if (iName && value) {
             if (!syncObjects[iName]) {
                 syncObjects[iName] = Number(value);
             } else {
                 syncObjects[iName] = syncObjects[iName] + (Number(value));
             }

             var dataSyncTo = $(this).attr('data-sync');
             if (dataSyncTo) {
                 $(dataSyncTo).text(syncObjects[iName]).change();
                 $(dataSyncTo).val(syncObjects[iName]).change().blur();
             }
         }*/
    });
}


function initEcharts() {
    if (typeof echarts != 'undefined') {
        $('.echarts').each(function () {
            var titleText = $(this).attr('data-title-text');
            var seriesName = $(this).attr('data-series-name');
            var seriesType = $(this).attr('data-series-type');

            var isPieType = "pie" == seriesType;


            var chart = echarts.init(this);

            var chartOption = {
                color: ['#08a4b3', '#9819e6', '#c0cdcb', '#d4af29', '#91c7ae', '#749f83', '#ca8622', '#bda29a', '#6e7074', '#546570', '#c4ccd3'],
                title: {
                    text: titleText
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        crossStyle: {
                            color: '#999'
                        }
                    }
                },
                legend: {
                    data: []
                },
                grid: {
                    left: '6%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                series: [{
                    name: seriesName,
                    type: seriesType || 'line',
                    showBackground: true,
                    backgroundStyle: {
                        color: 'rgba(220, 220, 220, 0.8)'
                    },
                    data: [],
                    itemStyle: {
                        //通常情况下：
                        normal: {
                            //每个柱子的颜色即为colorList数组里的每一项，如果柱子数目多于colorList的长度，则柱子颜色循环使用该数组
                            color: function (params) {
                                var colorList = ['#08a4b3', '#6e0ae6', '#c0cdcb', '#d4af29', '#91c7ae', '#749f83', '#ca8622', '#bda29a', '#6e7074', '#546570', '#c4ccd3']; //每根柱子的颜色
                                return colorList[params.dataIndex];
                            }
                        },
                        //鼠标悬停时：
                        emphasis: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }]
            };

            if (!isPieType) {
                chartOption.xAxis = {
                    type: 'category',
                    data: []
                };
                chartOption.yAxis = {
                    type: 'value'
                };
            }

            chart.setOption(chartOption);
            let option = {width: $(this).closest('.card-outline').width()};
            chart.resize(option);


            //显示 loading 动画
            chart.showLoading();

            var dataUrl = $(this).attr('data-url');

            Utils.ajaxGet(dataUrl, function (result) {
                if (result.data) {

                    //X轴的名称
                    var keys = [];

                    //Y轴的值
                    var values = [];

                    for (var key in result.data) {

                        keys.push(key);

                        if (isPieType) {
                            values.push({
                                name: key,
                                value: result.data[key]
                            })
                        } else {
                            values.push(result.data[key]);
                        }
                    }

                    //隐藏加载动画
                    chart.hideLoading();

                    //加载数据图表
                    if (isPieType) {
                        chart.setOption({
                            series: [{
                                data: values
                            }]
                        });
                    } else {
                        chart.setOption({
                            xAxis: [
                                {
                                    data: keys,
                                    type: 'category',
                                    axisPointer: {
                                        type: 'shadow'
                                    }
                                }
                            ],
                            series: [{
                                data: values
                            }]
                        });
                    }

                }
            }, function () {
                alert("数据加载失败");
                chart.hideLoading();
            });
        });
    }
}

function initMyeCharts() {
    if (typeof echarts != 'undefined') {
        $('.myeCharts').each(function () {
            let div = $(this);
            let titleText = $(this).attr('data-title-text');
            let type = $(this).attr('data-series-type');
            let Axis = $(this).attr('data-Axis-name');
            let unit = $(this).attr('data-Axis-unit');
            const ANNULUS = 'annulus';
            const BAR2 = 'bar2';
            let chart = echarts.init(this);
            let isAnnulusType = ANNULUS === type;
            let isBar2 = BAR2 === type;
            let chartOption;
            if (isAnnulusType) {
                chartOption = {
                    title: {
                        text: titleText
                    },
                    tooltip: {
                        trigger: 'item',
                    },
                    legend: {
                        orient: 'vertical',
                        left: 10,
                        data: []
                    },
                    series: [
                        {
                            name: '访问来源',
                            type: 'pie',
                            radius: ['50%', '70%'],
                            avoidLabelOverlap: false,
                            label: {
                                show: false,
                                position: 'center'
                            },
                            emphasis: {
                                label: {
                                    show: true,
                                    fontSize: '30',
                                    fontWeight: 'bold'
                                }
                            },
                            labelLine: {
                                show: false
                            },
                            data: []
                        }
                    ]

                }
            } else {
                chartOption = {
                    color: ['#08a4b3', '#9819e6', '#87cd87', '#d4af29', '#91c7ae', '#749f83', '#ca8622', '#bda29a', '#6e7074', '#546570', '#c4ccd3'],
                    title: {
                        text: titleText
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross',
                            crossStyle: {
                                color: '#999'
                            }
                        }
                    },
                    toolbox: {
                        feature: {
                            magicType: {show: true, type: ['line', 'bar']},
                            restore: {show: true},
                            saveAsImage: {show: true}
                        }
                    },
                    legend: {},
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    dataset: {
                        source: []
                    },
                    xAxis: [
                        {
                            type: 'category',
                            axisPointer: {
                                type: 'shadow'
                            }
                        }
                    ],
                    yAxis: [
                        {
                            type: 'value',
                            name: Axis,
                            axisLabel: {
                                formatter: unit ? '{value}' + unit : '{value}'
                            }
                        }
                    ],
                    series: []
                };
            }

            //显示 loading 动画
            chart.showLoading();

            chart.setOption(chartOption);
            let option = {width: $(this).closest('.card-outline').width()};
            chart.resize(option);

            let dataUrl = $(this).attr('data-url');

            Utils.ajaxGet(dataUrl, function (result) {
                    let data = result.data;
                    if (JSON.stringify(data) != '{}' && JSON.stringify(data) != '[]') {
                        //隐藏加载动画
                        chart.hideLoading();
                        if (isAnnulusType) {
                            let legend = [];
                            let seriesData = [];

                            for (let key in data) {

                                legend.push(key);
                                seriesData.push({
                                    name: key,
                                    value: data[key]
                                })
                            }

                            chart.setOption({
                                legend: {
                                    orient: 'vertical',
                                    left: 10,
                                    data: legend
                                },
                                series: [
                                    {
                                        name: '类型',
                                        data: seriesData
                                    }
                                ]
                            });
                        } else {
                            let source = [];
                            source = result.data;
                            let series = [];
                            if (source[0] != null) {
                                for (let i = 0; i < source[0].length - 1; i++) {
                                    if (isBar2) {
                                        series.push({
                                            type: 'bar',
                                        });
                                    } else {
                                        series.push({
                                            type: type,
                                            areaStyle: {},
                                        });
                                    }
                                }
                            }
                            if (isBar2) {
                                chart.setOption({
                                    dataset: {
                                        source: source
                                    },
                                    series: series,
                                    xAxis: [
                                        {
                                            type: 'value',
                                            name: Axis,
                                            axisLabel: {
                                                formatter: unit ? '{value}' + unit : '{value}'
                                            }
                                        }
                                    ],
                                    yAxis: [
                                        {
                                            type: 'category',
                                            axisPointer: {
                                                type: 'shadow'
                                            }
                                        }
                                    ],
                                });
                            } else {
                                chart.setOption({
                                    dataset: {
                                        source: source
                                    },
                                    series: series
                                });
                            }
                        }
                    } else {
                        div.html('<div  style="text-align: center;font-weight: bold;font-size:30px;line-height: 140px;color: #00B0E8">暂无可显示数据...</div>');
                        chart.hideLoading();
                    }
                },
                function () {
                    alert("数据加载失败");
                    chart.hideLoading();
                }
            );
        });
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
    initLayerEdit();
    initLayerPassword();

    initTooltip();


    initTouchSpin();
    initBackButton();

    initDatePicker();

    initToastr();
    initPopover();
    initValidate();
    initAjaxSubmitForms();
    initAjaxOpenType();

    initSelect2();
    initResetBtn();

    initBatchExecBtn();

    initConfirmOpenType();
    initSwitchery();

    initInputActions();
    initCkEdtiorComponent();
    initDataSync();
    initNumberCompute();


    initDatatableCheckBox();

    initEcharts();
    initMyeCharts();

    initDatatable();


    initShowTabByParas();
    initSwitchTab();
    initPressTab();


    initPagenationPagesize();

});