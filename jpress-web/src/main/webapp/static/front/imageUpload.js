$(function(){
    var userAgent = navigator.userAgent; //用于判断浏览器类型
    var count =0;
    var currentImgNumber = 0;
    $("body .uploadList").on("change",".bsForm-upload-file", function(){
        //获取选择图片的对象
        let fileCodeId = $(this).attr("id");
        let currentObj = $(this)[0];
        let number = $(this).parents(".uploadList").data("count");

        let uploadListDiv= $(this).parents(".uploadList"); // 放置图片的容器
        let name = uploadListDiv.attr("data-name");

        let fileList = currentObj.files;  //得到所有的图片文件
        for (let i = 0; i < fileList.length; i++) {
            count ++;
        }
        if(count>number){
            alert("最多只能上传"+number+"张");
            count = currentImgNumber;
            return false
        }

        // 上传到后台
        let fd = new FormData();
        fd.append(name,currentObj.files[0]);
        let uploading = false;
        if (uploading) {
            alert("文件正在上传中，请稍候!");
            return false;
        }
        $.ajax({
            url:'/form/upload/'+$(this).closest("form").attr("id"),
            type: "POST",
            data: fd,
            processData:false,
            // 不修改contentType属性，使用默认
            contentType:false,
            beforeSend: function () {
                uploading = true;
                // preview预览图片
                for (let i = 0; i < fileList.length; i++) {
                    //动态添加html元素
                    var imageHtml = "";
                    imageHtml += "<div class='jpress-upload-item'>"
                    imageHtml += "<img id='img" + fileCodeId+fileList[i].name + "'/>";
                    imageHtml += "<p class='jpress-images-name'>" + fileList[i].name + "</p>";
                    imageHtml += "<div class='file-delete'><i class='bi bi-trash'></i></div>";
                    imageHtml += "</div>";
                    uploadListDiv.prepend(imageHtml);
                    let imgObjPreview = document.getElementById("img" +fileCodeId + fileList[i].name);
                    if (fileList && fileList[i]) {
                        imgObjPreview.style.display = 'block';
                        imgObjPreview.style.width = '126px';
                        imgObjPreview.style.height = '126px';
                        imgObjPreview.style.objectFit = 'cover';
                        if (userAgent.indexOf('MSIE') == -1) {
                            //IE以外浏览器
                            imgObjPreview.src = window.URL.createObjectURL(currentObj.files[i]); //获取上传图片文件的物理路径;
                        } else {
                            //IE浏览器
                            if (currentObj.value.indexOf(",") != -1) {
                                var srcArr = currentObj.value.split(",");
                                imgObjPreview.src = srcArr[i];
                            } else {
                                imgObjPreview.src = currentObj.value;
                            }
                        }
                    }

                }
                currentImgNumber = $(".jpress-upload-item").length;
            },
            success: function (result) {
                uploading = false;
                if (result.state == "ok") {
                    //定义模板
                    let input = '<input type="hidden" name="'+name+'" value="' + result.src + '" />';
                    //添加模板
                    uploadListDiv.append(input);
                } else {

                }
            },
            error:function(data){
                console.log(data)
            }
        })


    });
    $("body .uploadList").on("click",".file-delete", function(){
        var _this = $(this);
        _this.parent(".jpress-upload-item").remove();
        count = count -1;
        currentImgNumber = currentImgNumber -1;
    });
    $(".btn-form-submit").attr("type","button");
    $("body .form-content").on("click",".btn-form-submit",function(){
        let url = $(".formInfo").attr("action");
        let formId = $(".formInfo").attr("id");
        $.ajax({
            url: url,
            type: "post",
            dataType: "json",
            data: {
                data : $('#'+formId).serialize()
            },
            success: function (result) {
                if(result.state == "ok"){
                    toastr.options = {
                        positionClass: "toast-center-center",
                        timeOut:1500 // 超时时间，即窗口显示的时间
                    }
                    toastr.success('保存成功!');
                }
            }
        })
    })


})
