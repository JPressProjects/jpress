/*!
* metismenu https://github.com/onokumus/metismenu#readme
*/
!function(e,t){"object"==typeof exports&&"undefined"!=typeof module?module.exports=t(require("jquery")):"function"==typeof define&&define.amd?define(["jquery"],t):(e="undefined"!=typeof globalThis?globalThis:e||self).metisMenu=t(e.$)}(this,(function(e){"use strict";function t(e){return e&&"object"==typeof e&&"default"in e?e:{default:e}}var n=t(e);const i=(e=>{const t="transitionend",n={TRANSITION_END:"mmTransitionEnd",triggerTransitionEnd(n){e(n).trigger(t)},supportsTransitionEnd:()=>Boolean(t)};function i(t){let i=!1;return e(this).one(n.TRANSITION_END,(()=>{i=!0})),setTimeout((()=>{i||n.triggerTransitionEnd(this)}),t),this}return e.fn.mmEmulateTransitionEnd=i,e.event.special[n.TRANSITION_END]={bindType:t,delegateType:t,handle(t){if(e(t.target).is(this))return t.handleObj.handler.apply(this,arguments)}},n})(n.default),s="metisMenu",r="jpressMenu",a=n.default.fn[s],o={toggle:!0,preventDefault:!0,triggerElement:"a",parentTrigger:"li",subMenu:"ul"},l={SHOW:"show.jpressMenu",SHOWN:"shown.jpressMenu",HIDE:"hide.jpressMenu",HIDDEN:"hidden.jpressMenu",CLICK_DATA_API:"click.jpressMenu.data-api"},d="jpress-menu",g="jpress-active",h="jpress-show",u="jpress-collapse",f="jpress-collapsing";class c{constructor(e,t){this.element=e,this.config={...o,...t},this.transitioning=null,this.init()}init(){const e=this,t=this.config,i=n.default(this.element);i.addClass(d),i.find(`${t.parentTrigger}.${g}`).children(t.triggerElement).attr("aria-expanded","true"),i.find(`${t.parentTrigger}.${g}`).parents(t.parentTrigger).addClass(g),i.find(`${t.parentTrigger}.${g}`).parents(t.parentTrigger).children(t.triggerElement).attr("aria-expanded","true"),i.find(`${t.parentTrigger}.${g}`).has(t.subMenu).children(t.subMenu).addClass(`${u} ${h}`),i.find(t.parentTrigger).not(`.${g}`).has(t.subMenu).children(t.subMenu).addClass(u),i.find(t.parentTrigger).children(t.triggerElement).on(l.CLICK_DATA_API,(function(i){const s=n.default(this);if("true"===s.attr("aria-disabled"))return;t.preventDefault&&"#"===s.attr("href")&&i.preventDefault();const r=s.parent(t.parentTrigger),a=r.siblings(t.parentTrigger),o=a.children(t.triggerElement);r.hasClass(g)?(s.attr("aria-expanded","false"),e.removeActive(r)):(s.attr("aria-expanded","true"),e.setActive(r),t.toggle&&(e.removeActive(a),o.attr("aria-expanded","false"))),t.onTransitionStart&&t.onTransitionStart(i)}))}setActive(e){n.default(e).addClass(g);const t=n.default(e).children(this.config.subMenu);t.length>0&&!t.hasClass(h)&&this.show(t)}removeActive(e){n.default(e).removeClass(g);const t=n.default(e).children(`${this.config.subMenu}.${h}`);t.length>0&&this.hide(t)}show(e){if(this.transitioning||n.default(e).hasClass(f))return;const t=n.default(e),s=n.default.Event(l.SHOW);if(t.trigger(s),s.isDefaultPrevented())return;if(t.parent(this.config.parentTrigger).addClass(g),this.config.toggle){const e=t.parent(this.config.parentTrigger).siblings().children(`${this.config.subMenu}.${h}`);this.hide(e)}t.removeClass(u).addClass(f).height(0),this.setTransitioning(!0);t.height(e[0].scrollHeight).one(i.TRANSITION_END,(()=>{this.config&&this.element&&(t.removeClass(f).addClass(`${u} ${h}`).height(""),this.setTransitioning(!1),t.trigger(l.SHOWN))})).mmEmulateTransitionEnd(350)}hide(e){if(this.transitioning||!n.default(e).hasClass(h))return;const t=n.default(e),s=n.default.Event(l.HIDE);if(t.trigger(s),s.isDefaultPrevented())return;t.parent(this.config.parentTrigger).removeClass(g),t.height(t.height())[0].offsetHeight,t.addClass(f).removeClass(u).removeClass(h),this.setTransitioning(!0);const r=()=>{this.config&&this.element&&(this.transitioning&&this.config.onTransitionEnd&&this.config.onTransitionEnd(),this.setTransitioning(!1),t.trigger(l.HIDDEN),t.removeClass(f).addClass(u))};0===t.height()||"none"===t.css("display")?r():t.height(0).one(i.TRANSITION_END,r).mmEmulateTransitionEnd(350)}setTransitioning(e){this.transitioning=e}dispose(){n.default.removeData(this.element,r),n.default(this.element).find(this.config.parentTrigger).children(this.config.triggerElement).off(l.CLICK_DATA_API),this.transitioning=null,this.config=null,this.element=null}static jQueryInterface(e){return this.each((function(){const t=n.default(this);let i=t.data(r);const s={...o,...t.data(),..."object"==typeof e&&e?e:{}};if(i||(i=new c(this,s),t.data(r,i)),"string"==typeof e){if(void 0===i[e])throw new Error(`No method named "${e}"`);i[e]()}}))}}return n.default.fn[s]=c.jQueryInterface,n.default.fn[s].Constructor=c,n.default.fn[s].noConflict=()=>(n.default.fn[s]=a,c.jQueryInterface),c}));


function getContextPath() {
    if (typeof jpress == 'undefined') {
        return ""
    } else {
        return jpress.cpath;
    }
}


function isMobileBrowser(){
    if(window.navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i)) {
        return true; // 移动端
    }else{
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
        dataType: 'json',
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


function initMenu(){
    if ($.metisMenu){
        $(".jpress-menu").metisMenu();
    }
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


function initCkEdtiorComponent() {
    if (typeof ClassicEditor == "undefined") {
        return;
    }
    let index = 0;
    $('[data-render="ckeditor"]').each(function () {
        var id = $(this).attr("id");
        if (!id) {
            id = "ckeditor" + (index++);
            $(this).attr("id", id);
        }
        initCkEdtior('#' + id);
    });
}


function initCkEdtior(selector) {
    ClassicEditor
        .create(document.querySelector(selector), {
            toolbar: {
                items: [
                    'heading',
                    '|',
                    'bold',
                    'italic',
                    'underline',
                    'link',
                    'code',
                    'bulletedList',
                    'numberedList',
                    '|',
                    'fontColor',
                    'fontBackgroundColor',
                    'outdent',
                    'indent',
                    'removeFormat',
                    '|',
                    'blockQuote',
                    'imageInsert',
                    'insertTable',
                    'codeBlock',
                    '|',
                    'undo',
                    'redo'
                ]
            },
            simpleUpload: {
                uploadUrl: getContextPath() + '/commons/ckeditor5/upload',
            },
            language: 'zh-cn',
            image: {
                toolbar: [
                    'imageTextAlternative',
                    'imageStyle:full',
                    'imageStyle:side'
                ]
            },
            table: {
                contentToolbar: [
                    'tableColumn',
                    'tableRow',
                    'mergeTableCells',
                    'tableProperties'
                ]
            },
        })
        .then(editor => {
            window.currentCKEditor = editor;
        })
        .catch(error => {
            console.log(error);
        });
}



function initCommentComponent() {


    $('#jpress-comment-form').on('submit', function () {
        var commentContent = $('#jpress-comment-form').find('textarea[name="content"]').val();
        if (!commentContent || commentContent == ""){
            alert("评论内容不能为空");
            return false;
        }

        $(this).ajaxSubmit({
            type: "post",
            success: function (data) {
                if (data.state == "ok") {

                    $('#comment-pid').val("");
                    $('#comment-captcha').val("");
                    $('#comment-vcode').click();

                    if (data.html){
                        if ($(".comment-page > div:first-child").length > 0){
                            $(".comment-page > div:first-child").before(data.html);
                        }else {
                            $(".comment-page").html(data.html);
                        }
                        $('.comment-textarea textarea').val('');
                    }else {
                        alert('评论内容发布成功');
                        location.reload();
                    }
                }
                //评论失败
                else {
                    alert('评论失败：' + data.message);

                    //用户未登录
                    if (data.errorCode == 9 && data.gotoUrl) {
                        location.href = data.gotoUrl;
                    }
                    //验证码错误
                    else if (data.errorCode == 2){
                        $('#comment-vcode').click();
                        $('#comment-captcha').val("");
                        $('#comment-captcha').focus();
                    }
                    //其他
                    else {
                        $('#comment-vcode').click();
                        $('#comment-captcha').val("");
                        $('.comment-textarea textarea').val('');
                        $('.comment-textarea textarea').focus();
                    }
                }
            },
            error: function () {
                alert("网络错误，请稍后重试");
            }
        });
        return false;
    });


    $('body').on('click','.toReplyComment', function () {
        $('#comment-pid').val($(this).attr('data-cid'));
        $('.comment-textarea textarea').val('回复 @' + $(this).attr('data-author') + " ：");
        $('.comment-textarea textarea').focus();
    });

}


var productInfo = {
    spec: null
};


/*
添加到购物车
 */
function addProductToCart(productId, productSpec, ok, fail) {
    ajaxPost(getContextPath() + '/product/doAddCart', {
            id: productId,
            spec: productSpec
        },
        ok ? ok : function () {
            alert('成功添加到购物车。')
        },
        fail ? fail : function (data) {
            alert('添加到购物车失败：' + data.message)
            if (data.gotoUrl) {
                location.href = data.gotoUrl;
            }
        })
}

/*
添加商品到收藏夹
 */
function addProductToFavorite(productId, ok, fail) {
    ajaxPost(getContextPath() + '/product/doAddFavorite', {
            id: productId
        },
        ok ? ok : function () {
            alert('成功添加到收藏夹。')
        },
        fail ? fail : function (data) {
            alert('添加到收藏夹失败：' + data.message)
            if (data.gotoUrl) {
                location.href = data.gotoUrl;
            }
        })
}


/*
购买产品
 */
function buyProduct(productId, ok, fail) {
    ajaxPost(getContextPath() + '/product/doBuy', {
            id: productId,
            spec: productInfo.spec
        },
        ok ? ok : function (data) {
            if (data.gotoUrl) {
                if (isMobileBrowser()) {
                    location.href = data.gotoUrl;
                } else {
                    window.open(data.gotoUrl, '_blank')
                }
            }
        },
        fail ? fail : function (data) {
            alert('无法进行购买：' + data.message)
            if (data.gotoUrl) {
                location.href = data.gotoUrl;
            }
        })
}


function initProductSpec(){
    $(".product-specs li").click(function () {
        setProductSpec($(this).text());
        $(this).addClass("active");
        $(this).siblings().removeClass("active");
    });

    $(".product-specs li:first").addClass("active");
    setProductSpec($(".product-specs li:first").text());
}



function setProductSpec(spec) {
    productInfo.spec = spec;
}

function initSwiperComponent() {

    if (typeof Swiper == "undefined") {
        return;
    }

    var galleryThumbs = new Swiper('.gallery-thumbs', {
        spaceBetween: 10,
        slidesPerView: 5,
        freeMode: true,
        watchSlidesVisibility: true,
        watchSlidesProgress: true,

    });

    var galleryTop = new Swiper('.gallery-top', {
        spaceBetween: 10,
        navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev',
        },
        thumbs: {
            swiper: galleryThumbs
        }
    });
}


function initClipboardJSComponent(){
    if (typeof ClipboardJS != "undefined") {
        var clipboard = new ClipboardJS('.copy');
        clipboard.on('success', function(e) {
            alert("复制成功，可以去分享给您的朋友啦~~");
        });
    }
}




$(document).ready(function () {

    /*为String添加必要的扩张方法*/
    initStringMethods();

    /*设置网站菜单*/
    initMenu();

    /*设置自动提交的form，在登录等页面用到*/
    initAjaxSubmitForms();

    /*设置文章和产品评论*/
    initCommentComponent();

    /*设置产品规格*/
    initProductSpec();
    /*设置产品详情页的缩略图*/
    initSwiperComponent();
    /*设置产品粘贴板*/
    initClipboardJSComponent()
});

