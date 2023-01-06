/*!
* metismenu https://github.com/onokumus/metismenu#readme
*/
!function (e, t) {
    "object" == typeof exports && "undefined" != typeof module ? module.exports = t(require("jquery")) : "function" == typeof define && define.amd ? define(["jquery"], t) : (e = "undefined" != typeof globalThis ? globalThis : e || self).metisMenu = t(e.$)
}(this, (function (e) {
    "use strict";

    function t(e) {
        return e && "object" == typeof e && "default" in e ? e : {default: e}
    }

    var n = t(e);
    const i = (e => {
            const t = "transitionend", n = {
                TRANSITION_END: "mmTransitionEnd", triggerTransitionEnd(n) {
                    e(n).trigger(t)
                }, supportsTransitionEnd: () => Boolean(t)
            };

            function i(t) {
                let i = !1;
                return e(this).one(n.TRANSITION_END, (() => {
                    i = !0
                })), setTimeout((() => {
                    i || n.triggerTransitionEnd(this)
                }), t), this
            }

            return e.fn.mmEmulateTransitionEnd = i, e.event.special[n.TRANSITION_END] = {
                bindType: t,
                delegateType: t,
                handle(t) {
                    if (e(t.target).is(this)) return t.handleObj.handler.apply(this, arguments)
                }
            }, n
        })(n.default), s = "metisMenu", r = "jpressMenu", a = n.default.fn[s],
        o = {toggle: !0, preventDefault: !0, triggerElement: "a", parentTrigger: "li", subMenu: "ul"}, l = {
            SHOW: "show.jpressMenu",
            SHOWN: "shown.jpressMenu",
            HIDE: "hide.jpressMenu",
            HIDDEN: "hidden.jpressMenu",
            CLICK_DATA_API: "click.jpressMenu.data-api"
        }, d = "jpress-menu", g = "jpress-active", h = "jpress-show", u = "jpress-collapse", f = "jpress-collapsing";

    class c {
        constructor(e, t) {
            this.element = e, this.config = {...o, ...t}, this.transitioning = null, this.init()
        }

        init() {
            const e = this, t = this.config, i = n.default(this.element);
            i.addClass(d), i.find(`${t.parentTrigger}.${g}`).children(t.triggerElement).attr("aria-expanded", "true"), i.find(`${t.parentTrigger}.${g}`).parents(t.parentTrigger).addClass(g), i.find(`${t.parentTrigger}.${g}`).parents(t.parentTrigger).children(t.triggerElement).attr("aria-expanded", "true"), i.find(`${t.parentTrigger}.${g}`).has(t.subMenu).children(t.subMenu).addClass(`${u} ${h}`), i.find(t.parentTrigger).not(`.${g}`).has(t.subMenu).children(t.subMenu).addClass(u), i.find(t.parentTrigger).children(t.triggerElement).on(l.CLICK_DATA_API, (function (i) {
                const s = n.default(this);
                if ("true" === s.attr("aria-disabled")) return;
                t.preventDefault && "#" === s.attr("href") && i.preventDefault();
                const r = s.parent(t.parentTrigger), a = r.siblings(t.parentTrigger), o = a.children(t.triggerElement);
                r.hasClass(g) ? (s.attr("aria-expanded", "false"), e.removeActive(r)) : (s.attr("aria-expanded", "true"), e.setActive(r), t.toggle && (e.removeActive(a), o.attr("aria-expanded", "false"))), t.onTransitionStart && t.onTransitionStart(i)
            }))
        }

        setActive(e) {
            n.default(e).addClass(g);
            const t = n.default(e).children(this.config.subMenu);
            t.length > 0 && !t.hasClass(h) && this.show(t)
        }

        removeActive(e) {
            n.default(e).removeClass(g);
            const t = n.default(e).children(`${this.config.subMenu}.${h}`);
            t.length > 0 && this.hide(t)
        }

        show(e) {
            if (this.transitioning || n.default(e).hasClass(f)) return;
            const t = n.default(e), s = n.default.Event(l.SHOW);
            if (t.trigger(s), s.isDefaultPrevented()) return;
            if (t.parent(this.config.parentTrigger).addClass(g), this.config.toggle) {
                const e = t.parent(this.config.parentTrigger).siblings().children(`${this.config.subMenu}.${h}`);
                this.hide(e)
            }
            t.removeClass(u).addClass(f).height(0), this.setTransitioning(!0);
            t.height(e[0].scrollHeight).one(i.TRANSITION_END, (() => {
                this.config && this.element && (t.removeClass(f).addClass(`${u} ${h}`).height(""), this.setTransitioning(!1), t.trigger(l.SHOWN))
            })).mmEmulateTransitionEnd(350)
        }

        hide(e) {
            if (this.transitioning || !n.default(e).hasClass(h)) return;
            const t = n.default(e), s = n.default.Event(l.HIDE);
            if (t.trigger(s), s.isDefaultPrevented()) return;
            t.parent(this.config.parentTrigger).removeClass(g), t.height(t.height())[0].offsetHeight, t.addClass(f).removeClass(u).removeClass(h), this.setTransitioning(!0);
            const r = () => {
                this.config && this.element && (this.transitioning && this.config.onTransitionEnd && this.config.onTransitionEnd(), this.setTransitioning(!1), t.trigger(l.HIDDEN), t.removeClass(f).addClass(u))
            };
            0 === t.height() || "none" === t.css("display") ? r() : t.height(0).one(i.TRANSITION_END, r).mmEmulateTransitionEnd(350)
        }

        setTransitioning(e) {
            this.transitioning = e
        }

        dispose() {
            n.default.removeData(this.element, r), n.default(this.element).find(this.config.parentTrigger).children(this.config.triggerElement).off(l.CLICK_DATA_API), this.transitioning = null, this.config = null, this.element = null
        }

        static jQueryInterface(e) {
            return this.each((function () {
                const t = n.default(this);
                let i = t.data(r);
                const s = {...o, ...t.data(), ..."object" == typeof e && e ? e : {}};
                if (i || (i = new c(this, s), t.data(r, i)), "string" == typeof e) {
                    if (void 0 === i[e]) throw new Error(`No method named "${e}"`);
                    i[e]()
                }
            }))
        }
    }

    return n.default.fn[s] = c.jQueryInterface, n.default.fn[s].Constructor = c, n.default.fn[s].noConflict = () => (n.default.fn[s] = a, c.jQueryInterface), c
}));


function getContextPath() {
    if (typeof jpress == 'undefined') {
        return ""
    } else {
        return jpress.cpath;
    }
}

function getSitePath() {
    if (typeof jpress == 'undefined') {
        return ""
    } else {
        return jpress.spath;
    }
}


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


function initMenu() {
    if ($().metisMenu) {
        $(".jpress-menu").metisMenu();
    }
}


/**
 * 设置 form 的 ajax 自动提交
 */
function initAjaxSubmitForms() {
    $('.autoAjaxSubmit').on('submit', function () {
        var $form = $(this);

        if (window.currentCKEditor) {
            window.currentCKEditor.updateSourceElement();
        }

        var successFun = $form.attr('data-ok-function');
        var successGoto = $form.attr('data-ok-href');
        var successMsg = $form.attr('data-ok-message');

        var failFun = $form.attr('data-fail-function');
        var failMsg = $form.attr('data-fail-message');

        var binds = $form.attr('data-binds');

        $form.ajaxSubmit({
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

        return false;
    });
}


function initCommentComponent() {


    $('#jpress-comment-form').on('submit', function () {
        var commentContent = $('#jpress-comment-form').find('textarea[name="content"]').val();
        if (!commentContent || commentContent == "") {
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

                    if (data.html) {
                        if ($(".comment-page > div:first-child").length > 0) {
                            $(".comment-page > div:first-child").before(data.html);
                        } else {
                            $(".comment-page").html(data.html);
                        }
                        $('.comment-textarea textarea').val('');
                    } else {
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
                    else if (data.errorCode == 2) {
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


function initClipboardJSComponent() {
    if (typeof ClipboardJS != "undefined") {
        var clipboard = new ClipboardJS('.copy');
        clipboard.on('success', function (e) {
            alert("复制成功，可以去分享给您的朋友啦~~");
        });
    }
}


function initJPressVideo() {

    $('.jpress-video').each(function () {

        var containerId = $(this).attr("id");
        var id = $(this).attr("data-vid");

        $.ajax({
            url: getContextPath() + getSitePath() + "/commons/video/detail",
            type: "post",
            data: {id: id},

            success: function (result) {
                if (result.state == "ok") {
                    var cloudType = result.cloudType;

                    if (cloudType != null && cloudType != '' && cloudType == '1') {//阿里云

                        var vid = result.vid;
                        var playAuth = result.playAuth

                        loadCss("https://g.alicdn.com/de/prismplayer/2.9.3/skins/default/aliplayer-min.css")
                        loadJs("https://g.alicdn.com/de/prismplayer/2.9.3/aliplayer-min.js",
                            function () {
                                //阿里云
                                if (vid != "" && playAuth != "" && containerId != "") {
                                    var player = new Aliplayer({
                                            "id": containerId,
                                            "vid": vid,
                                            "playauth": playAuth,
                                            "videoWidth": "100%",
                                            "videoHeight": "100%",
                                            "autoplay": false,
                                            "isLive": false,
                                            // "cover": "缩略图",
                                            "rePlay": false,
                                            "playsinline": true,
                                            "preload": false,
                                            "controlBarVisibility": "hover",
                                            "useH5Prism": true
                                        }, function (player) {
                                            console.log("The aliyun player is created");
                                        }
                                    );
                                    return player;
                                }
                            })

                    } else if (cloudType != null && cloudType != '' && cloudType == '2') {//腾讯云

                        var vid = result.vid;
                        var aid = result.aid;

                        loadCss("https://web.sdk.qcloud.com/player/tcplayer/release/v4.5.2/tcplayer.min.css")

                        loadJs(["https://web.sdk.qcloud.com/player/tcplayer/release/v4.5.2/libs/hls.min.0.13.2m.js",
                                "https://web.sdk.qcloud.com/player/tcplayer/release/v4.5.2/tcplayer.v4.5.2.min.js"],
                            function () {
                                if (vid != "" && aid != "" && containerId != "") {
                                    new TCPlayer(containerId, {
                                        fileID: vid,
                                        appID: aid
                                    });
                                }
                            })
                    } else if (cloudType != null && cloudType != '' && cloudType == '4') {//本地视频

                        $("#" + containerId).attr("src", getContextPath() + getSitePath() + result.src);
                        $("#" + containerId).attr("controls", "controls");
                    }

                }
            }
        })

    })
}


function loadJs(scripts, callback) {
    if (typeof scripts === "string") {
        scripts = [scripts];
    }
    var count = scripts.length;

    function useCallback() {
        return function () {
            if (--count < 1) {
                callback();
            }
        };
    }

    function loadScript(url) {
        var scriptEl = document.createElement('script');
        scriptEl.setAttribute('src', url);
        scriptEl.setAttribute('type', "text/javascript");
        scriptEl.onload = useCallback();
        document.body.appendChild(scriptEl);
        // document.head.appendChild(s);
    }

    for (var script of scripts) {
        loadScript(script);
    }
}

function loadCss(links, callback) {
    if (typeof links === "string") {
        links = [links];
    }
    var count = links.length;

    function useCallback() {
        return function () {
            if (--count < 1 && callback) {
                callback();
            }
        };
    }

    function loadLink(url) {
        var linkEl = document.createElement("link");
        linkEl.rel = "stylesheet";
        linkEl.href = url;
        linkEl.onload = useCallback();
        document.head.appendChild(linkEl);
    }

    for (var script of links) {
        loadLink(script);
    }
}

//初始化表单 数据
function initFormCard() {

    $(".jpress-form-card").each(function () {

        var $this = $(this);
        $this.removeAttr('style')

        var formUUID = $this.attr('data-form-id');

        if (formUUID != null) {
            $.ajax({
                url: getContextPath() + getSitePath() + '/form/detail/' + formUUID,
                type: 'get',
                data: {},
                success: function (result) {
                    if (result.state == true && result.html) {
                        $this.html(result.html);
                        $this.show();

                        initJPressAJCaptcha();

                        /*图片组件*/
                        initBsFormImageComponent()

                        //表单文件选择回显
                        initBsFormFileComponent();

                    }
                }
            })
        }
    })
}

function initJPressAJCaptcha() {

    $('.jpress-captcha').each(function () {

        var containerId = $(this).attr("id");
        if (!containerId) {
            alert("请联系管理员，容器id未配置！")
            return;
        }

        var captchaType = $(this).attr("data-type");
        if (!captchaType) {
            captchaType = 'pointsVerify';//设置默认类型
        }

        var location = $(this).attr("data-location");
        if (!location) {
            location = 'body';
        }

        var validType = $(this).attr("data-valid-type");
        if (!validType) {
            validType = "ajax"
        }

        if (validType === "ajax") {
            var checkInputId = $(this).attr("data-value-id");
            if (!checkInputId) {
                alert("请联系管理员，数据id未配置！")
                return;
            }

            var ajaxUrl = $(this).attr("data-path");
            if (!ajaxUrl) {
                alert("请联系管理员，请求路径未配置！")
                return;
            }
        }

        var validFormUUID = $(this).attr("data-form-id");


        var ajaxGetSuccessToUrl = $(this).attr("data-url");
        var ajaxGetSuccessPoint = $(this).attr("data-point");

        var val = null;
        var option = validType === "ajax"
            ? getAjaxCaptachOption(containerId, checkInputId, ajaxUrl, ajaxGetSuccessToUrl, ajaxGetSuccessPoint)
            : getFormCaptachOption(containerId, validFormUUID);

        loadCss(getContextPath() + "/static/components/aj-captcha/css/verify.css");
        loadJs([
                getContextPath() + "/static/components/aj-captcha/js/crypto-js.js",
                getContextPath() + "/static/components/aj-captcha/js/ase.js",
                getContextPath() + "/static/components/aj-captcha/js/verify.js",
                getContextPath() + "/static/components/jquery/jquery.form.min.js",
            ],
            function () {

                if (captchaType === 'pointsVerify') {

                    var isRead = false;
                    $(location).pointsVerify(option);

                } else if (captchaType === 'slideVerify') {

                    var isRead = false;
                    $(location).slideVerify(option);
                }
            })

    })
}

function getAjaxCaptachOption(containerId, checkInputId, ajaxUrl, ajaxGetSuccessToUrl, ajaxGetSuccessPoint) {

    return {
        baseUrl: getContextPath() + getSitePath() + '/commons',  //服务器请求地址,
        containerId: containerId, //pop模式 必填 被点击之后出现行为验证码的元素id
        mode: 'pop',     //展示模式
        beforeCheck: function () {  //检验参数合法性的函数  mode ="pop" 有效

            val = $("#" + checkInputId).val();

            if (isRead && !val) {
                alert("数据不能为空");
                return false
            }
            return true;
        },
        //加载完毕的回调
        ready: function () {
            isRead = true;
        },
        //验证成功
        success: function (params) {

            var dataType = null;
            var regx = /^1(3|4|5|6|7|8|9)\d{9}$/;
            var isMobile = regx.test(val);
            //判断是否是手机号
            if (isMobile) {
                dataType = {mobile: val};
            } else if (!isMobile) {
                dataType = {email: val};
            }

            var data = $.extend(dataType, params);

            $.ajax({
                url: ajaxUrl,
                type: 'POST',
                data: JSON.stringify(data),
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success:
                    function (result) {
                        console.log(result);
                        if (result.state === "ok") {
                            if (ajaxGetSuccessToUrl == null || ajaxGetSuccessToUrl == '') {
                                var point = ajaxGetSuccessPoint == null || ajaxGetSuccessPoint == '' ? "验证成功" : ajaxGetSuccessPoint;
                                alert(point);
                            } else {
                                window.location.href = ajaxGetSuccessToUrl;
                            }

                        } else {
                            alert(result.message);
                        }
                    },
                error:
                    function (arg) {
                        console.log("error...", arg);
                        if (arg.responseJSON) {
                            alert(arg.responseJSON.message);
                        }
                    }
            });

        },

        error: function () {
            //失败的回调
            console.log("pointsVerify error...");
        }
    };

}

function getFormCaptachOption(containerId, formUUID) {

    return {
        baseUrl: getContextPath() + getSitePath() + '/commons',  //服务器请求地址,
        containerId: containerId,//pop模式 必填 被点击之后出现行为验证码的元素id
        mode: 'pop',     //展示模式
        beforeCheck: function () {  //检验参数合法性的函数  mode ="pop" 有效

            if (!formUUID) {
                alert("未设置表单ID");
                return false;
            } else {
                return true;
            }
        },
        //加载完毕的回调
        ready: function () {
            isRead = true;
        },
        //验证成功
        success: function (params) {

            let input = '<input type="hidden" id="captchaVO" name="captchaVO.captchaVerification" value="' + params.captchaVerification + '">'

            $("#" + formUUID).append(input);

            ajaxSubmit("#" + formUUID, function (result) {

                if (result.state == 'ok') {

                    //如果返回值中 有 message 属性
                    if (result.message) {
                        alert(result.message)
                    } else {
                        alert("提交成功");
                    }

                    //如果 在元素中 有设置 提价成功后需要跳转的 url
                    let path = $("#" + containerId).attr("data-result-url");
                    if (path) {
                        location.href = path;
                    } else {
                        location.reload();
                    }
                }


            }, function (result) {
                $("#captchaVO").remove();
                alert(result.message);
            })
        },

        error: function () {
            //失败的回调
            console.log("pointsVerify error...");
        }
    };

}


//job apply 页面 选择文件
function jobFileChoose() {

    $(".chooseFile").each(function () {

        var $this = $(this);

        let id = $this.attr("data-result-id");

        if (id != null && id != "" && id != undefined) {

            $this.bind('change', function () {

                let fileName = $this[0].files[0].name

                $("#" + id).html(fileName);
            })
        }

    })
}


//表单文件选择回显
function initBsFormFileComponent(){
    $("body .jpress-custom-file").on("change",'.jpress-file-input',function () {
        var fileName = $(this).val();
        if (fileName && fileName.lastIndexOf("\\") > 0){
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        }
        $(this).next('.jpress-file-label').html(fileName)
    })
}


//表单图片上传组件
function initBsFormImageComponent() {
    var userAgent = navigator.userAgent; //用于判断浏览器类型

    function genUuid() {
        var s = [];
        var hexDigits = "0123456789abcdef";
        for (var i = 0; i < 32; i++) {
            s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
        }
        s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
        s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
        s[8] = s[13] = s[18] = s[23];
        return s.join("");
    }

    $("body .uploadList").on("change", ".bsForm-upload-file", function () {
        //获取选择图片的对象
        let fileCodeId = $(this).attr("id");
        let currentObj = $(this)[0];

        let uploadListDiv = $(this).parents(".uploadList"); // 放置图片的容器
        let maxUploadLimit = uploadListDiv.data("count");



        let file = currentObj.files[0];  //得到所有的图片文件
        if (!file) {
            return;
        }

        let inputFieldName = uploadListDiv.attr("data-name");
        $(this).attr('name',inputFieldName);

        var uuid = genUuid();
        var imageHtml = "<div class='jpress-upload-item' id='" + uuid + "'>"
        imageHtml += "<img id='img" + fileCodeId + file.name + "'/>";
        imageHtml += "<p class='jpress-images-name'>" + file.name + "</p>";
        imageHtml += "<div class='file-delete'><i class='bi bi-trash'></i></div>";
        imageHtml += "</div>";
        uploadListDiv.prepend(imageHtml);

        let imgObjPreview = document.getElementById("img" + fileCodeId + file.name);
        imgObjPreview.style.display = 'block';
        imgObjPreview.style.width = '126px';
        imgObjPreview.style.height = '126px';
        imgObjPreview.style.objectFit = 'cover';
        if (userAgent.indexOf('MSIE') == -1) {
            //IE以外浏览器
            imgObjPreview.src = window.URL.createObjectURL(file); //获取上传图片文件的物理路径;
        } else {
            //IE浏览器
            if (currentObj.value.indexOf(",") != -1) {
                var srcArr = currentObj.value.split(",");
                imgObjPreview.src = srcArr[i];
            } else {
                imgObjPreview.src = currentObj.value;
            }
        }

        var uploadButton = $(this).closest('button');
        var outerHTML = uploadButton[0].outerHTML;

        uploadButton.addClass(uuid).hide();

        var currentUploadItemCount = uploadListDiv.find(".jpress-upload-item").length;

        if (currentUploadItemCount < maxUploadLimit) {
            uploadButton.after(outerHTML);
        }

    });


    $("body .uploadList").on("click", ".file-delete", function () {

        var uploadListDiv = $(this).closest(".uploadList"); // 放置图片的容器
        var maxUploadLimit = uploadListDiv.data("count");

        var deleteImage = $(this).parent(".jpress-upload-item");
        var deleteHideFileInput = $('.' + deleteImage.attr('id'));

        deleteHideFileInput.show().removeClass(deleteImage.attr('id'));
        var outerHtml = deleteHideFileInput[0].outerHTML;

        deleteImage.remove();
        deleteHideFileInput.remove();

        var currentItemCount = uploadListDiv.find(".jpress-upload-item").length;
        if (currentItemCount <= maxUploadLimit) {
            if (uploadListDiv.find(".jpress-upload-btn:last").attr('class') !== 'jpress-upload-btn'){
                uploadListDiv.append(outerHtml);
            }
        }
    });

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

    /*设置产品详情页的缩略图*/
    initSwiperComponent();

    /*设置产品粘贴板*/
    initClipboardJSComponent();

    /*初始化视频播放容器*/
    initJPressVideo();

    /*初始化表单数据*/
    initFormCard();

    /*初始化行为验证码容器*/
    initJPressAJCaptcha();

    /*job apply 页面 文件选择*/
    jobFileChoose();

    //表单文件选择回显
    initBsFormFileComponent();

    /*图片组件*/
    initBsFormImageComponent()


});

