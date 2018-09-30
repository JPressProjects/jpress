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

    var reg = /([\s|\。|\，|\？|\、|\"|\“|\”|\‘|\'|\（|\）|\《|\》|\… |\～|\￥|\&|\*|\@|\#|\$||\%|\`|\.|\【|\】|\-])+/ig;

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

                $.get("/commons/pinyin/doGetPinyin/" + value, function (result) {
                    if ("ok" == result.state) {
                        var pinyin = result.data;
                        that.text(pinyin);
                        that.editable('setValue', pinyin);
                    }
                });
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