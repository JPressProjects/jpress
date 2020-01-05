var productInfo = {
    spec: null
};


/*
添加到购物车
 */
function addProductToCart(productId, productSpec, ok, fail) {
    ajaxPost(getContextPaht() + '/product/doAddCart', {
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
    ajaxPost(getContextPaht() + '/product/doAddFavorite', {
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
    ajaxPost(getContextPaht() + '/product/doBuy', {
            id: productId,
            spec: productInfo.spec
        },
        ok ? ok : function (data) {
            if (data.gotoUrl) {
                var ua = window.navigator.userAgent.toLowerCase();

                //微信页面不支持 window.open 直接跳转
                if (ua.match(/MicroMessenger/i) == 'micromessenger') {
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


function getContextPaht() {
    return typeof jpress != "undefined" && jpress.cpath ? jpress.cpath : "";
}


function ajaxPost(url, data, ok, fail) {
    $.post(url, data, function (result) {
        if (result.state == 'ok') {
            ok(result);
        } else {
            fail(result);
        }
    });
}

function setProductSpec(spec) {
    console.log("setProductSpec : " + spec)
    productInfo.spec = spec;
}

function initSwiperComponent() {

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

function initCommentComponent() {

    $('#jpress-comment-form').on('submit', function () {
        $(this).ajaxSubmit({
            type: "post",
            success: function (data) {

                $('#comment-pid').val("");
                $('#comment-captcha').val("");
                $('#comment-vcode').click();

                if (data.state == "ok") {
                    if (data.html) {
                        if ($(".comment-page > div:first-child").length > 0) {
                            $(".comment-page > div:first-child").before(data.html);
                        } else {
                            $(".comment-page").html(data.html);
                        }
                        $('.comment-textarea textarea').val('');
                    } else {
                        alert('发布评论成功');
                        location.reload();
                    }
                }
                //评论失败
                else {
                    alert('评论失败：' + data.message);
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
                        $('.comment-textarea textarea').val('');
                        $('#comment-vcode').click();
                    }
                }
            },
            error: function () {
                alert("网络错误，请稍后重试");
            }
        });
        return false;
    });


    $('body').on('click', '.toReplyComment', function () {
        $('#comment-pid').val($(this).attr('data-cid'));
        $('.comment-textarea textarea').val('回复 @' + $(this).attr('data-author') + " ：");
        $('.comment-textarea textarea').focus();
    });

}


$(document).ready(function () {

    $(".product-specs li").click(function () {
        setProductSpec($(this).text());
        $(this).addClass("active");
        $(this).siblings().removeClass("active");
    });

    $(".product-specs li:first").addClass("active");
    setProductSpec($(".product-specs li:first").text());

    initSwiperComponent();
    initCommentComponent();

});