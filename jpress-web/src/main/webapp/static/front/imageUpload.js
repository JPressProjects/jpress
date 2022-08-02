$(function(){
    var userAgent = navigator.userAgent; //用于判断浏览器类型
    $("body .uploadList").on("change",".bsForm-upload-file", function(){
        //获取选择图片的对象
        let fileCodeId = $(this).attr("id");
        let name = $(this).attr("name");
        let currentObj = $(this)[0];
        let imageDiv= $(this).parents(".uploadList"); // 放置图片的容器
        let fileList = currentObj.files;  //得到所有的图片文件



        // 上传到后台
        let fd = new FormData();
        fd.append(name,currentObj.files[0]);
        let uploading = false;
        if (uploading) {
            alert("文件正在上传中，请稍候!");
            return false;
        }
        $.ajax({
            url:'/form/upload ',
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
                    imageDiv.prepend(imageHtml);
                    let imgObjPreview = document.getElementById("img" +fileCodeId + fileList[i].name);
                    if (fileList && fileList[i]) {
                        imgObjPreview.style.display = 'block';
                        imgObjPreview.style.width = '100px';
                        imgObjPreview.style.height = '100px';
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
            },
            success: function (result) {
                uploading = false;
                if (result.state == "ok") {
                    console.log('上传成功');
                    //定义模板
                    let input = '<input type="hidden" name="'+name+'" value="' + result.src + '" />';
                    //添加模板
                    $(this).parents(".uploadList").append(input);
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
    });
})
