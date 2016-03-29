$(function(){
     $(".jp-navbar-toggle").click(function(e){
         $(".jp-collapse").toggle();
     })
  //去掉icon中的“-o”
    $(".jp-ms").mouseover(function(){
        $(this).css({"color":"#4f4f4f"});
        var $obj =  $(this).find(".fa");
        var arr = $(this).find(".fa").attr("class").split(" ");
        $.each(arr,function(i,val){
            if(this.contains("-o")){
             var $newClass =  arr[i].substring(0,arr[i].length-2)
                $obj.removeClass(arr[i]).addClass($newClass);
            }
        })
    }).mouseout(function(){
            $(this).css({"color":"#9d9d9d"});
            var $obj =  $(this).find(".fa");
            var arr = $(this).find(".fa").attr("class").split(" ");
            $.each(arr,function(i,val){
                if(this.contains("fa-")){
                    var $newClass =  arr[i]+"-o";
                    $obj.removeClass(arr[i]).addClass($newClass);
                }
            })
        })
    $(".fa-thumbs-o-up").mouseover(function(){
        $(this).css({"color":"#4f4f4f"});
        $(this).removeClass("fa-thumbs-o-up").addClass("fa-thumbs-up");
    }).mouseout(function(){
            $(this).css({"color":"#9d9d9d"});
            $(this).removeClass("fa-thumbs-up").addClass("fa-thumbs-o-up");
    })
    $(".jp-ms-color").mouseover(function(){
        $(this).css({"color":"#4f4f4f"});
    }).mouseout(function(){
            $(this).css({"color":"#9d9d9d"});
    })

    $(".layer").click(function(){
        var $data = $(this).attr("data-src") ;
        var $title = $(this).attr("data-title");
        layer.open({
            type: 2,
            title: $title,
            shadeClose: true,
            shade: 0.8,
            area: ['50%', '70%'],
            content: $data//iframe的url
        });
    })
//    var img = document.getElementById('img');
//    var path = img.value;
//    $("#img").click(function(){
//        var path =$("#img").val();
//        console.log(path+"aaakka");
//
//        $(".newimg").attr("src",path);
//    })




})






