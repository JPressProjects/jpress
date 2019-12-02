$(document).ready(function () {

    $("form").each(function () {
        if ($(this).find("input[name=csrf_token]").length == 0) {
            var token = getCookie("csrf_token");
            if (token) {
                $(this).append("<input type='hidden' name='csrf_token' value='" + token + "'/>");
            }
        }
    });


    $(document).ajaxSend(function (event, request, option) {
        var token = getCookie("csrf_token");
        if (token) {
            var url = option.url;

            if (url.indexOf("?") == -1) {
                url = url + "?csrf_token=" + token;
            } else {
                if (url.indexOf("csrf_token=") == -1) {
                    url = url + "&csrf_token=" + token;
                }
            }

            option.url = url;
        }
    });

});

function getCookie(name) {
    var cookieString = document.cookie;
    var cookies = cookieString.split("; ");
    for (var i = 0; i < cookies.length; i++) {
        var arr = cookies[i].split("=");
        if (arr[0] == name) {
            return arr[1];
        }
    }
    return null;
}