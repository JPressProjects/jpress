/*!
* metismenu https://github.com/onokumus/metismenu#readme
*/
!function(e,t){"object"==typeof exports&&"undefined"!=typeof module?module.exports=t(require("jquery")):"function"==typeof define&&define.amd?define(["jquery"],t):(e="undefined"!=typeof globalThis?globalThis:e||self).metisMenu=t(e.$)}(this,(function(e){"use strict";function t(e){return e&&"object"==typeof e&&"default"in e?e:{default:e}}var n=t(e);const i=(e=>{const t="transitionend",n={TRANSITION_END:"mmTransitionEnd",triggerTransitionEnd(n){e(n).trigger(t)},supportsTransitionEnd:()=>Boolean(t)};function i(t){let i=!1;return e(this).one(n.TRANSITION_END,(()=>{i=!0})),setTimeout((()=>{i||n.triggerTransitionEnd(this)}),t),this}return e.fn.mmEmulateTransitionEnd=i,e.event.special[n.TRANSITION_END]={bindType:t,delegateType:t,handle(t){if(e(t.target).is(this))return t.handleObj.handler.apply(this,arguments)}},n})(n.default),s="metisMenu",r="metisMenu",a=n.default.fn[s],o={toggle:!0,preventDefault:!0,triggerElement:"a",parentTrigger:"li",subMenu:"ul"},l={SHOW:"show.metisMenu",SHOWN:"shown.metisMenu",HIDE:"hide.metisMenu",HIDDEN:"hidden.metisMenu",CLICK_DATA_API:"click.metisMenu.data-api"},d="jp-menu",g="jp-active",h="jp-show",u="jp-collapse",f="jp-collapsing";class c{constructor(e,t){this.element=e,this.config={...o,...t},this.transitioning=null,this.init()}init(){const e=this,t=this.config,i=n.default(this.element);i.addClass(d),i.find(`${t.parentTrigger}.${g}`).children(t.triggerElement).attr("aria-expanded","true"),i.find(`${t.parentTrigger}.${g}`).parents(t.parentTrigger).addClass(g),i.find(`${t.parentTrigger}.${g}`).parents(t.parentTrigger).children(t.triggerElement).attr("aria-expanded","true"),i.find(`${t.parentTrigger}.${g}`).has(t.subMenu).children(t.subMenu).addClass(`${u} ${h}`),i.find(t.parentTrigger).not(`.${g}`).has(t.subMenu).children(t.subMenu).addClass(u),i.find(t.parentTrigger).children(t.triggerElement).on(l.CLICK_DATA_API,(function(i){const s=n.default(this);if("true"===s.attr("aria-disabled"))return;t.preventDefault&&"#"===s.attr("href")&&i.preventDefault();const r=s.parent(t.parentTrigger),a=r.siblings(t.parentTrigger),o=a.children(t.triggerElement);r.hasClass(g)?(s.attr("aria-expanded","false"),e.removeActive(r)):(s.attr("aria-expanded","true"),e.setActive(r),t.toggle&&(e.removeActive(a),o.attr("aria-expanded","false"))),t.onTransitionStart&&t.onTransitionStart(i)}))}setActive(e){n.default(e).addClass(g);const t=n.default(e).children(this.config.subMenu);t.length>0&&!t.hasClass(h)&&this.show(t)}removeActive(e){n.default(e).removeClass(g);const t=n.default(e).children(`${this.config.subMenu}.${h}`);t.length>0&&this.hide(t)}show(e){if(this.transitioning||n.default(e).hasClass(f))return;const t=n.default(e),s=n.default.Event(l.SHOW);if(t.trigger(s),s.isDefaultPrevented())return;if(t.parent(this.config.parentTrigger).addClass(g),this.config.toggle){const e=t.parent(this.config.parentTrigger).siblings().children(`${this.config.subMenu}.${h}`);this.hide(e)}t.removeClass(u).addClass(f).height(0),this.setTransitioning(!0);t.height(e[0].scrollHeight).one(i.TRANSITION_END,(()=>{this.config&&this.element&&(t.removeClass(f).addClass(`${u} ${h}`).height(""),this.setTransitioning(!1),t.trigger(l.SHOWN))})).mmEmulateTransitionEnd(350)}hide(e){if(this.transitioning||!n.default(e).hasClass(h))return;const t=n.default(e),s=n.default.Event(l.HIDE);if(t.trigger(s),s.isDefaultPrevented())return;t.parent(this.config.parentTrigger).removeClass(g),t.height(t.height())[0].offsetHeight,t.addClass(f).removeClass(u).removeClass(h),this.setTransitioning(!0);const r=()=>{this.config&&this.element&&(this.transitioning&&this.config.onTransitionEnd&&this.config.onTransitionEnd(),this.setTransitioning(!1),t.trigger(l.HIDDEN),t.removeClass(f).addClass(u))};0===t.height()||"none"===t.css("display")?r():t.height(0).one(i.TRANSITION_END,r).mmEmulateTransitionEnd(350)}setTransitioning(e){this.transitioning=e}dispose(){n.default.removeData(this.element,r),n.default(this.element).find(this.config.parentTrigger).children(this.config.triggerElement).off(l.CLICK_DATA_API),this.transitioning=null,this.config=null,this.element=null}static jQueryInterface(e){return this.each((function(){const t=n.default(this);let i=t.data(r);const s={...o,...t.data(),..."object"==typeof e&&e?e:{}};if(i||(i=new c(this,s),t.data(r,i)),"string"==typeof e){if(void 0===i[e])throw new Error(`No method named "${e}"`);i[e]()}}))}}return n.default.fn[s]=c.jQueryInterface,n.default.fn[s].Constructor=c,n.default.fn[s].noConflict=()=>(n.default.fn[s]=a,c.jQueryInterface),c}));
//# sourceMappingURL=metisMenu.min.js.map


function getContextPath() {
    if (typeof jpress == 'undefined') {
        return ""
    } else {
        return jpress.cpath;
    }
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

function _initMenu(ulclass){
    $(document).ready(function(){
        $(ulclass+' li').hover(function(){
            $(this).children("ul").show(); //mouseover
        },function(){
            $(this).children("ul").hide(); //mouseout
        });
    });
}

function initMenu(){
    $(".jp-menu").metisMenu();
}



/**
 * 设置 form 的 ajax 自动提交
 */
function initAjaxSubmitForms() {
    $('.autoAjaxSubmit').on('submit', function () {
        __form = $(form);

        var successFun = __form.attr('data-ok-function');
        var successGoto = __form.attr('data-ok-href');
        var successMsg = __form.attr('data-ok-message');

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
                        showMessage(successMsg, successGoto);
                        return;
                    }

                    if (result.message) {
                        showMessage(result.message, successGoto);
                        return;
                    }

                    if (result.data && result.data.message) {
                        showMessage(result.data.message, successGoto);
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
                        showErrorMessage(failMsg);
                        return
                    }

                    if (result.message) {
                        showErrorMessage(result.message);
                        return;
                    }

                    if (result.data && result.data.message) {
                        showMessage(result.data.message);
                        return;
                    }

                    showErrorMessage('操作失败。')
                }
            },
            error: function () {
                showErrorMessage('系统错误，请稍后重试。');
            }
        });
    });
}

function initAjaxOpenType() {
    $('[open-type="ajax"]').on('click', function (e) {
        e.preventDefault();
        var href = $(this).attr("href");
        var ajaxMethod = $(this).attr("data-ajax-method");
        var submitInputs = $(this).attr("data-submit-inputs");

        var successFun = $(this).attr('data-ok-function');
        var successGoto = $(this).attr('data-ok-href');
        var successMsg = $(this).attr('data-ok-message');

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
                showMessage(successMsg, successGoto);
                return;
            }

            if (successGoto) {
                location.href = successGoto;
                return
            }

            if (result && result.message) {
                showMessage(result.message);
                return;
            }

        };

        var failFunction = function (result) {
            if (failFun) {
                eval(failFun)(result);
                return;
            }

            if (failMsg) {
                showErrorMessage(failMsg);
                return
            }

            if (result.message) {
                showErrorMessage(result.message);
            } else {
                showErrorMessage('操作失败。')
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
            ajaxPost(href, postData, okFunction, failFunction);
        } else {
            ajaxGet(href, okFunction, failFunction);
        }
    });
}



$(document).ready(function () {

    initStringMethods();
    initMenu();
    initAjaxSubmitForms();
    initAjaxOpenType();

});

// /*!
// * jpressmenu
// */
// !function(e,t){"object"==typeof exports&&"undefined"!=typeof module?module.exports=t(require("jquery")):"function"==typeof define&&define.amd?define(["jquery"],t):(e="undefined"!=typeof globalThis?globalThis:e||self).metisMenu=t(e.$)}(this,(function(e){"use strict";function t(e){return e&&"object"==typeof e&&"default"in e?e:{default:e}}var n=t(e);const i=(e=>{const t="transitionend",n={TRANSITION_END:"mmTransitionEnd",triggerTransitionEnd(n){e(n).trigger(t)},supportsTransitionEnd:()=>Boolean(t)};function i(t){let i=!1;return e(this).one(n.TRANSITION_END,(()=>{i=!0})),setTimeout((()=>{i||n.triggerTransitionEnd(this)}),t),this}return e.fn.mmEmulateTransitionEnd=i,e.event.special[n.TRANSITION_END]={bindType:t,delegateType:t,handle(t){if(e(t.target).is(this))return t.handleObj.handler.apply(this,arguments)}},n})(n.default),s="jpress-menu",r="jpress-menu",a=n.default.fn[s],o={toggle:!0,preventDefault:!0,triggerElement:"a",parentTrigger:"li",subMenu:"ul"},l={SHOW:"show.jpress-menu",SHOWN:"shown.jpress-menu",HIDE:"hide.jpress-menu",HIDDEN:"hidden.jpress-menu",CLICK_DATA_API:"click.jpress-menu.data-api"},d="jpress-menu",g="jp-active",h="jp-show",u="jp-collapse",f="jp-collapsing";class c{constructor(e,t){this.element=e,this.config={...o,...t},this.transitioning=null,this.init()}init(){const e=this,t=this.config,i=n.default(this.element);i.addClass(d),i.find(`${t.parentTrigger}.${g}`).children(t.triggerElement).attr("aria-expanded","true"),i.find(`${t.parentTrigger}.${g}`).parents(t.parentTrigger).addClass(g),i.find(`${t.parentTrigger}.${g}`).parents(t.parentTrigger).children(t.triggerElement).attr("aria-expanded","true"),i.find(`${t.parentTrigger}.${g}`).has(t.subMenu).children(t.subMenu).addClass(`${u} ${h}`),i.find(t.parentTrigger).not(`.${g}`).has(t.subMenu).children(t.subMenu).addClass(u),i.find(t.parentTrigger).children(t.triggerElement).on(l.CLICK_DATA_API,(function(i){const s=n.default(this);if("true"===s.attr("aria-disabled"))return;t.preventDefault&&"#"===s.attr("href")&&i.preventDefault();const r=s.parent(t.parentTrigger),a=r.siblings(t.parentTrigger),o=a.children(t.triggerElement);r.hasClass(g)?(s.attr("aria-expanded","false"),e.removeActive(r)):(s.attr("aria-expanded","true"),e.setActive(r),t.toggle&&(e.removeActive(a),o.attr("aria-expanded","false"))),t.onTransitionStart&&t.onTransitionStart(i)}))}setActive(e){n.default(e).addClass(g);const t=n.default(e).children(this.config.subMenu);t.length>0&&!t.hasClass(h)&&this.show(t)}removeActive(e){n.default(e).removeClass(g);const t=n.default(e).children(`${this.config.subMenu}.${h}`);t.length>0&&this.hide(t)}show(e){if(this.transitioning||n.default(e).hasClass(f))return;const t=n.default(e),s=n.default.Event(l.SHOW);if(t.trigger(s),s.isDefaultPrevented())return;if(t.parent(this.config.parentTrigger).addClass(g),this.config.toggle){const e=t.parent(this.config.parentTrigger).siblings().children(`${this.config.subMenu}.${h}`);this.hide(e)}t.removeClass(u).addClass(f).height(0),this.setTransitioning(!0);t.height(e[0].scrollHeight).one(i.TRANSITION_END,(()=>{this.config&&this.element&&(t.removeClass(f).addClass(`${u} ${h}`).height(""),this.setTransitioning(!1),t.trigger(l.SHOWN))})).mmEmulateTransitionEnd(350)}hide(e){if(this.transitioning||!n.default(e).hasClass(h))return;const t=n.default(e),s=n.default.Event(l.HIDE);if(t.trigger(s),s.isDefaultPrevented())return;t.parent(this.config.parentTrigger).removeClass(g),t.height(t.height())[0].offsetHeight,t.addClass(f).removeClass(u).removeClass(h),this.setTransitioning(!0);const r=()=>{this.config&&this.element&&(this.transitioning&&this.config.onTransitionEnd&&this.config.onTransitionEnd(),this.setTransitioning(!1),t.trigger(l.HIDDEN),t.removeClass(f).addClass(u))};0===t.height()||"none"===t.css("display")?r():t.height(0).one(i.TRANSITION_END,r).mmEmulateTransitionEnd(350)}setTransitioning(e){this.transitioning=e}dispose(){n.default.removeData(this.element,r),n.default(this.element).find(this.config.parentTrigger).children(this.config.triggerElement).off(l.CLICK_DATA_API),this.transitioning=null,this.config=null,this.element=null}static jQueryInterface(e){return this.each((function(){const t=n.default(this);let i=t.data(r);const s={...o,...t.data(),..."object"==typeof e&&e?e:{}};if(i||(i=new c(this,s),t.data(r,i)),"string"==typeof e){if(void 0===i[e])throw new Error(`No method named "${e}"`);i[e]()}}))}}return n.default.fn[s]=c.jQueryInterface,n.default.fn[s].Constructor=c,n.default.fn[s].noConflict=()=>(n.default.fn[s]=a,c.jQueryInterface),c}));
//
//
//

