var imageIds = new Array();
var images = {
	localId : [],
	serverId : []
};

Utils = {
	context: '',
	devMode: true,
	pageSize: 10,
	ajax: function(url, param, callback) {
		$.ajax({
            type: "post",
            url: url,
            data: param(),
            dataType: 'json',
            success: function(res) {
            	if (callback)
            		callback(res);
            },
            error: function(res) {
            	$.toptip("信息提交错误", 'error');
            }
        });
	},
	ajaxSubmit: function($form, beforeSubmitCallback, successCallback) {
		$form.ajaxSubmit({ 
			type: "post", 
	        dataType: "json",
	        beforeSubmit: function() {
	        	return beforeSubmitCallback();
	        },
	        success: function(data) {
	        	successCallback(data);
	        },
	        error: function() {
	            $.toptip("信息提交错误", 'error');
	        }
		});
	},
	get: function(url) {
		$.get(url, function(res) {
            if (res.errorCode > 0) {
            	$.toptip(res.message, 'warning');
            } else {
            	$.toptip(res.message, 'success');
                location.reload();
            }
        });
	},
	confirm: function(title, text, url) {
		$.confirm({
            title: title,
            text: text,
            onOK: function() {
                $.get(url, function(res) {
		            if (res.errorCode > 0) {
		            	$.toptip(res.message, 'warning');
		            } else {
		                $.toptip(res.message, 'success');
		                location.reload();
		            }
		        });
            },
            onCancel: function() {}
        });
	},
	infinite: function($root, $loadmore, data, loading, callback) {
		$root = $root || $(window.body);
		$root.infinite().on("infinite", function() {
			console.log('infinite start');
			if ($loadmore) $loadmore.show();
			
			if (loading) return;
			loading = true;
			$.ajax({
	            type: "post",
	            url: '${CPATH}/api',
	            data: data,
	            dataType: 'json',
	            success: function(res) {
	            	if (callback)
	            		callback();
	            },
	            error: function(res) {
	            	$.toptip(res.message, 'error');
	            }
	        });
		});
	},
	openLoadingWin: function(tips) {
		layer.open({
			type: 2,
			content: tips || '保存中...'
		});
	},
	closeLoadingWin: function() {
		layer.closeAll();
	},
	initWxConfig: function(appId, timestamp, nonceStr, signature) {
		wx.config({
	        debug: false,
	        appId: appId,
	        timestamp: timestamp,
	        nonceStr: nonceStr,
	        signature: signature,
	        jsApiList: [
	            'checkJsApi',
	            'getLocation',
	            'getLocation',
	            'chooseImage',
	            'uploadImage',
	            'downloadImage',
	            "onMenuShareQQ", 
	            "onMenuShareWeibo",
	            'onMenuShareTimeline', 
	            'onMenuShareAppMessage'
	        ]
	    });
	},
	wxFunctions: function(callback) {
		wx.ready(function() {
			var localIds = new Array();
			$(document).on("click", ".chooseImage", function() {
				wx.chooseImage({
					count: 1, // 默认9
					sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
					sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
					success: function (res) {
						console.log(res.localIds)
						images.localId = res.localIds;
						alert('已选择 ' + res.localIds.length + ' 张图片');
						var localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
					}
				});
			});
			
			$(document).on("click", ".uploadImage", function() {
				if (images.localId.length == 0) {
					alert('请先使用 chooseImage 接口选择图片');
					return;
				}
				var i = 0, length = images.localId.length;
				images.serverId = [];
				function upload() {
					wx.uploadImage({
						localId : images.localId[i],
						isShowProgressTips: 1, // 默认为1，显示进度提示
						success : function(res) {
							alert(JSON.stringify(res));
							i++;
							alert('已上传：' + i + '/' + length);
							images.serverId.push(res.serverId);
							if (i < length) {
								upload();
							}
						},
						fail : function(res) {
							alert(JSON.stringify(res));
						}
					});
				}
				upload();
			});
			
			wx.getLocation({
                type: 'wgs84',
                success: function (res) {
                    var latitude = res.latitude;                      // 纬度，浮点数，范围为90 ~ -90
                    var longitude = res.longitude;                    // 经度，浮点数，范围为180 ~ -180。
                    var speed = res.speed;                            // 速度，以米/每秒计
                    var accuracy = res.accuracy;                      // 位置精度

                    var point = new BMap.Point(longitude, latitude);  // 将经纬度转化为百度经纬度
                    var geoc = new BMap.Geocoder();                   // 获取百度地址解析器

                    translateCallback = function (point) {            // 回调函数
                    	var lng = parseFloat(point.lng);
                    	var lat = parseFloat(point.lat);
                        geoc.getLocation(point, function(rs) {
                            var addComp = rs.addressComponents;
                            var address = addComp.province + addComp.city + addComp.district + addComp.street + addComp.streetNumber;
                            //$("#delivery-address")[0].value = addComp.province + " " + addComp.city + " " + addComp.district;
                            //$('#areaCode').val(addComp.provinceNumber + "," + addComp.cityNumber + "," + addComp.districtNumber);
                            //$('#areaName').val(addComp.province + "," + addComp.city + "," + addComp.district);
                            //$('#location-span').text(address);
                            //$('#location').val(address);
                            
                            callback(lng, lat, addComp.province, addComp.city, addComp.district, address);
                            
                        });
                     };
                     setTimeout(function() {
                         BMap.Convertor.translate(point, 0, translateCallback);//真实经纬度转成百度坐标
                     }, 100);
                },
                cancel: function (res) {
                    console.log('用户拒绝授权获取地理位置');
                },
                fail: function (res) {
                   console.log(JSON.stringify(res));
                }
	        });
		});
	},
	uploadImages: function($uploadInput, $uploaderFiles, $picNum, callback, num) {
		var imageArr = [];
		$uploadInput.on("change", function () {

            var files = this.files;
            var arrNum = imageArr.length;
            var num = arrNum + files.length;

            if ($uploaderFiles[0].children.length + 1 > num) {
                $.alert("最多上传2张图片");
                return false;
            }

            $picNum.text($uploaderFiles[0].children.length + 1 + '/' + num);
            
            for (var i = 0; i < files.length; i ++) {
                var file = files[i];
                //console.log(file.type)
                if (!/image\/\w+/.test(file.type)) {
                    $.alert("请确保文件为图像类型");
                    return false;
                }

                EXIF.getData(file, function() {
                    EXIF.getAllTags(this);
                    Orientation = EXIF.getTag(this, 'Orientation');
                });

                var reader = new FileReader();
                (function(x) {
                    reader.onload = function(e) {
                        var image = new Image();
                        image.src = e.target.result;
                        image.onload = function() {
                            var expectWidth = this.naturalWidth;
                            var expectHeight = this.naturalHeight;

                            if (this.naturalWidth > this.naturalHeight && this.naturalWidth > 480) {
                                expectWidth = 480;
                                expectHeight = expectWidth * this.naturalHeight / this.naturalWidth;
                            } else if (this.naturalHeight > this.naturalWidth && this.naturalHeight > 640) {
                                expectHeight = 640;
                                expectWidth = expectHeight * this.naturalWidth / this.naturalHeight;
                            }

                            var canvas = document.createElement("canvas");
                            var ctx = canvas.getContext("2d");
                            canvas.width = expectWidth;
                            canvas.height = expectHeight;
                            ctx.drawImage(this, 0, 0, canvas.width, canvas.height);

                            /*if(Orientation != "" && Orientation != 1){
                                switch(Orientation){
                                    case 6://需要顺时针（向左）90度旋转
                                        rotateImg(this,'left',canvas, canvas.width, canvas.height);
                                        break;
                                    case 8://需要逆时针（向右）90度旋转
                                        rotateImg(this,'right',canvas, canvas.width, canvas.height);
                                        break;
                                    case 3://需要180度旋转
                                        rotateImg(this,'right',canvas, canvas.width, canvas.height);//转两次
                                        rotateImg(this,'right',canvas, canvas.width, canvas.height);
                                        break;
                                }
                            }*/
                            
                            var blob = canvas.toDataURL("image/jpeg", 0.9);
                            imageArr.push({"pic":blob, "pic_name":x});
                            callback(blob, x, imageArr);
                        };
                    };
                })(file.name)
                reader.readAsDataURL(file);
            }
        });
	},
	rotateImg: function(img, direction,canvas, width, height) {
        //最小与最大旋转方向，图片旋转4次后回到原方向
        var min_step = 0;
        var max_step = 3;
        if (img == null) return;

        var step = 2;
        if (step == null) {
            step = min_step;
        }
        if (direction == 'right') {
            step++;
            //旋转到原位置，即超过最大值
            step > max_step && (step = min_step);
        } else {
            step--;
            step < min_step && (step = max_step);
        }
        //旋转角度以弧度值为参数
        var degree = step * 90 * Math.PI / 180;
        var ctx = canvas.getContext('2d');
        switch (step) {
            case 0:
                canvas.width = width;
                canvas.height = height;
                ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
                break;
            case 1:
                canvas.width = height;
                canvas.height = width;
                ctx.rotate(degree);
                ctx.drawImage(img, 0, -height, canvas.width, canvas.height);
                break;
            case 2:
                canvas.width = width;
                canvas.height = height;
                ctx.rotate(degree);
                ctx.drawImage(img, -width, -height, canvas.width, canvas.height);
                break;
            case 3:
                canvas.width = height;
                canvas.height = width;
                ctx.rotate(degree);
                ctx.drawImage(img, -width, 0, canvas.width, canvas.height);
                break;
        }
    },
    appendFall: function($grid, dataFall) {
    	var items = new Array();
    	$.each(dataFall, function(index, route) {

            $grid.imagesLoaded().done( function() {
                $grid.masonry('layout');
            });
            
            var obj = {};
            var $griDiv = $('<div class="grid-item item">');
            var $img = $("<img class='item-img'>");
            $img.attr('src', Utils.context + route.showImage).appendTo($griDiv);
            
            var url = Utils.context + '/route/' + route.id;
            var $a = $("<a>");
            $a.attr('href', url);
            
            var $section = $('<section class="section-p">');
            $section.appendTo($a);
            
            var $p1 = $("<p class='title-p'>");
            $p1.html(route.code + " " + route.title).appendTo($section);
            // $p1.html(route.title).appendTo($section);

            /** var $p2 = $("<p class='date-p txt-color-green'>");
            $p2.html("#date(route.departure_date, 'yyyy-MM-dd') 出发").appendTo($section);
            var $p3 = $("<p class='price-p'>");
            $p3.html("起售价：").appendTo($section);
            var $span = $("<span class='txt-color-red'>");
            $span.html(route.price + " 元起").appendTo($p3);*/
            
            $a.appendTo($griDiv);
            
            var $items = $griDiv;
            $items.imagesLoaded().done(function(){
                $grid.masonry('layout');
                $grid.append( $items ).masonry('appended', $items);
                $grid.masonry('reloadItems');
            });

            obj.image = Utils.context + route.showImage;
            obj.caption = route.title;
            
            items.push(obj);
    	});
        return items;
    }
}

function initTopHoverTree(hvtid, times, right, bottom) {
    $("#" + hvtid).css("right", right).css("bottom", bottom);
    
    $("#" + hvtid).on("click", function () { 
        goTopHovetree(times); 
    });
    
    $(window).scroll(function () {
        if ($(window).scrollTop() > 268) {
            $("#" + hvtid).fadeIn(100);
        }
        else {
            $("#" + hvtid).fadeOut(100);
        }
    });
}
//返回顶部动画
//goTop(500);//500ms内滚回顶部
function goTopHovetree(times) {
    if (!!!times) {
        $(window).scrollTop(0);
        return;
    }
    var sh = $('body').scrollTop();//移动总距离
    var inter = 13.333;//ms,每次移动间隔时间
    var forCount = Math.ceil(times / inter);//移动次数
    var stepL = Math.ceil(sh / forCount);//移动步长
    var timeId = null;
    
    function aniHovertree() {
        !!timeId && clearTimeout(timeId);
        timeId = null;
        //console.log($('body').scrollTop());
        if ($('body').scrollTop() <= 0 || forCount <= 0) {
        //移动端判断次数好些，因为移动端的scroll事件触发不频繁，有可能检测不到有<=0的情况
            $('body').scrollTop(0);
            return;
        }
        forCount--;
        sh -= stepL;
        $('body').scrollTop(sh);
        timeId = setTimeout(function () { aniHovertree(); }, inter);
    }

    aniHovertree();
}

function goHome() {
	location.href = "/";
}

$(document).on('click', '#gohome', function() {
	location.href = Utils.context || '/';
})