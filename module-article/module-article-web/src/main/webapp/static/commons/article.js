function initCommentComponent() {

    $('#jpress-comment-form').on('submit', function () {
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
                        alert('发布评论成功');
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


$(document).ready(function(){

    initCommentComponent();

});