$(document).ready(function () {

    initOptionSubmit();

});


function initOptionSubmit() {

    $('#form').on('submit', function() {
        $(this).ajaxSubmit({
            type: "post",
            success: function (data) {
                if (data.state == "ok") {
                    toastr.success('保存成功。');
                } else {
                    toastr.error(data.message, '操作失败');
                }
            },
            error: function () {
                alert("信息提交错误");
            }
        });
       return false;
    });

    // $("#submit").on("click", function () {
    //
    // })

}








