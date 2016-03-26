$(function(){
    $(".jp-onmouse").mouseover(function(){
        $(this).find(".row-actions").show();
    }).mouseout(function(){  $(".row-actions").hide()})


  /*
  * 设置
  * */
    $(".jp-title-btn-show").click(function(){
        $(".jp-title-table-show").show();
        $(".jp-title-help").hide();
        $(".jp-title-btn-help").css("visibility","hidden");
        var $box =$(this).parents(".box").first();

        if( $box.find(".fa").hasClass("fa-sort-desc")){
            $box.find(".box-body").slideDown("200",function(){
                $box.find(".fa").removeClass("fa-sort-desc").addClass("fa-sort-up");})
        }else{
            $box.find(".box-body").slideUp("200",function(){
                $(".jp-title-table-show").hide();
                $(".jp-title-btn-help").css("visibility","visible");
                $box.find(".fa").removeClass("fa-sort-up").addClass("fa-sort-desc");

            })
        }
    });
    $(".jp-title-btn-help").click(function(){
        $(".jp-title-help").show();
        $(".jp-title-table-show").hide();
        $(".jp-title-btn-show").css("visibility","hidden");
        var $box =$(this).parents(".box").first();

        if( $box.find(".fa").hasClass("fa-sort-desc")){
            $box.find(".box-body").slideDown("200",function(){
                $box.find(".fa").removeClass("fa-sort-desc").addClass("fa-sort-up");})
        }else{
            $box.find(".box-body").slideUp("200",function(){
                $(".jp-title-help").hide();
                $(".jp-title-btn-show").css("visibility","visible");
                $box.find(".fa").removeClass("fa-sort-up").addClass("fa-sort-desc");

            })
        }
    });

    /**
     * 发布编辑
     */
    $(".jp-release-draft").click(function(){
        var $obj = $(this).parents(".jp-release-set").first();
        $obj.find(".jp-display-none").show();
        $(this).hide();
//        if($obj.find(".jp-display-none").css("display")=="none"){
//
//        }else{ $obj.find(".jp-display-none").hide();}
    })
    $(".jp-release-cancel-draft").click(function(){
        var $obj = $(this).parents(".jp-release-set").first();
        $obj.find(".jp-release-draft").show();
        $obj.find(".jp-display-none").hide();
    })

    $(".jp-time").click(function(){
         var d = new Date();
        $("#yy").val(d.getFullYear());
//        $("#mm").val(d.getMonth());
        $("#mm").find("option[text='+d.getMonth()+']").attr("selected",true);
        $("#dd").val(d.getDate());
        $("#hh").val(d.getHours());
        $("#mn").val(d.getMinutes());
    })

    $(".jp-add-new-project").click(function(){
        if($(".jp-build-new-project").css("display")=="none"){
            $(".jp-build-new-project").show();
        }else{
            $(".jp-build-new-project").hide();
        }
    })

})
/*
 * 全部文章复选框
 * */
function check_all(checkbox){
	  var items = document.getElementsByName("dataItem");
	  for(var i=0;i<items.length;i++){
	      items[i].checked=checkbox.checked;
	  }
}
