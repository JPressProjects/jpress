var productInfo = {
    spec : null
};

/*
添加到购物车
 */
function addProductToCart(productId, productSpec, okFunction, failFunction) {
    ajaxPost(getContextPaht() + '/product/doAddCart', {
            id: productId,
            spec: productSpec
        },
        okFunction ? okFunction : function () {
            alert('成功添加到购物车。')
        },
        failFunction ? failFunction : function (data) {
            alert('添加到购物车失败：' + data.message)
        })
}

/*
添加商品到收藏夹
 */
function addProductToFavorite(productId, okFunction, failFunction) {
    ajaxPost(getContextPaht() + '/product/doAddFavorite', {
            id: productId,
            spec: productSpec
        },
        okFunction ? okFunction : function () {
            alert('成功添加到收藏夹。')
        },
        failFunction ? failFunction : function (data) {
            alert('添加到收藏夹失败：' + data.message)
        })
}



/*
购买产品
 */
function buyPrudct(productId, okFunction, failFunction) {
    ajaxPost(getContextPaht() + '/product/doBuy', {
            id: productId,
            spec: productInfo.spec
        },
        okFunction ? okFunction : function (data) {
            if (data.gotoUrl) {
                location.href = data.gotoUrl;
            }
        },
        failFunction ? failFunction : function () {
            if (data.gotoUrl) {
                location.href = data.gotoUrl;
            } else {
                alert('无法进行购买：' + data.message)
            }
        })
}

/*
选择商品规格
 */
function selectProductSpec(spec) {
    productInfo.spec = spec;
}

function getContextPaht() {
    return typeof jpress != "undefined" && jpress.cpath ? jpress.cpath : "";
}

function ajaxPost(url, data, okFunction, failFunction) {
    $.post(url, data,function (result) {
        if (result.state == 'ok') {
            okFunction(result);
        } else {
            failFunction(result);
        }
    });
}