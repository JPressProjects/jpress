/*
添加到购物车
 */
function addToCart(productId, productSpec, okFunction, failFunction) {
    ajaxPost(jpress.cpath + '/product/doAddCart', {
            id: productId,
            spec: productSpec
        },
        okFunction ? okFunction : function () {
            showMessage('成功添加到购物车。')
        },
        failFunction ? failFunction : function (data) {
            showMessage('添加到购物车失败：' + data.message)
        })
}


/*
购买产品
 */
function buy(productId, productSpec, okFunction, failFunction) {
    ajaxPost(jpress.cpath + '/product/doBuy', {
            id: productId,
            spec: productSpec
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
                showMessage('无法进行购买：' + data.message)
            }
        })
}