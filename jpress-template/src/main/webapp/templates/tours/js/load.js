$(function(){
	
	/*顶部nav*/
	var swiper = new Swiper('.nav-container', {
        slidesPerView: 'auto',
        paginationClickable: true
    });
	
    $(".nav-ul .swiper-slide").click(function(){
    	$(this).addClass("active-li").siblings().removeClass("active-li");
    	$('.fall-box').html("");
    	pageIndex = 1;
    	category = $(this).attr("category");
    	loadRoute();
    });
    
    /*瀑布流初始化设置*/
	var $grid = $('.grid').masonry({
		itemSelector : '.grid-item',
		gutter:10
    });
	
    // layout Masonry after each image loads
	$grid.imagesLoaded().done( function() {
		$grid.masonry('layout');
	});
	
	loadRoute();
	
   var pageIndex = 1 ; var dataFall = [];
   var totalItem = 10;
   var category = "";
   $(window).scroll(function(){
		$grid.masonry('layout');
        var scrollTop = $(this).scrollTop();
        var scrollHeight = $(document).height();
        var windowHeight = $(this).height();  
        if(scrollTop + windowHeight == scrollHeight) {
        	loadRoute();
        }
	});
        
	function appendFall(){
	    $.each(dataFall, function(index ,value) {
          	var dataLength = dataFall.length;
          	$grid.imagesLoaded().done( function() {
          		$grid.masonry('layout');
          	});
          	
          	var detailUrl;
          	var $griDiv = $('<div class="grid-item item">');
          	
          	var $img = $("<img class='item-img'>");
          	$img.attr('src', value.thumbnail).appendTo($griDiv);
          	
          	var url = '';
          	
          	var $section = $('<section class="section-p">');
          	$section.appendTo($griDiv);
          	var $p1 = $("<p class='title-p'>");
          	$p1.html(value.route_name).appendTo($section);
          	
          	var $p2 = $("<p class='date-p'>");
          	$p2.html(value.leaveDate).appendTo($section);
          	var $p3 = $("<p class='price-p'>");
          	$p3.html("起售价：" + value.price).appendTo($section);
          	
          	var $items = $griDiv;
          	$items.imagesLoaded().done(function(){
          		$grid.masonry('layout');
          		$grid.append( $items ).masonry('appended', $items);
          	})
       });
    }
	
	function loadRoute() {
		$.ajax({
            method: 'post',
            url: '/api',
            data: {method: 'queryRoutes', p: pageIndex, pageSize: 10, category: category},
            dataType: 'json',
            beforeSend: function() {
                Utils.openLoadingWin();
            },
            success: function(res) {
                //$('.fall-box').empty();
                dataFall = res.data;
            	setTimeout(function(){
            		appendFall();
            		Utils.closeLoadingWin();
            		pageIndex ++ ;
            	},500);
            },
            error: function(res) {
                Utils.closeLoadingWin();
            }
		});
	}
})
