/*! Verify&admin MIT License by anji-plus*/
/*! J2eeFAST 优化兼容IE浏览器*/

;(function($, window, document,undefined) {

	  // 初始话 uuid 
		uuid()
		function uuid() {
			var s = [];
			var hexDigits = "0123456789abcdef";
			for (var i = 0; i < 36; i++) {
					s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
			}
			s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
			s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
			s[8] = s[13] = s[18] = s[23] = "-";
	
			var slider = 'slider'+ '-'+s.join("");
			var point = 'point'+ '-'+s.join("");
			// 判断下是否存在 slider
			console.log(localStorage.getItem('slider'))
			if(!localStorage.getItem('slider')) {
				localStorage.setItem('slider', slider)
			}
			if(!localStorage.getItem('point')) {
				localStorage.setItem("point",point);
			}
		}

	var startX,startY;
 
		document.addEventListener("touchstart",function(e){
		
				startX = e.targetTouches[0].pageX;
				startY = e.targetTouches[0].pageY;
		});
		
		document.addEventListener("touchmove",function(e){
		
				var moveX = e.targetTouches[0].pageX;
				var moveY = e.targetTouches[0].pageY;
				
				if(Math.abs(moveX-startX)>Math.abs(moveY-startY)){
						e.preventDefault();
				}
		},{passive:false});
		
    //请求图片get事件
    function getPictrue(data,baseUrl,resolve,reject){
		$.ajax({
			type : "post",
			contentType: "application/json;charset=UTF-8",
			url : baseUrl + "/captcha/get",
			data :JSON.stringify(data),
			cache: false,
      crossDomain: true == !(document.all),
			success:function(res){
				resolve(res)
			},
			fail: function(err) {
				reject(err)
			}
		})
	}
	//验证图片check事件
	function checkPictrue(data,baseUrl,resolve,reject){
		$.ajax({
			type : "post",
			contentType: "application/json;charset=UTF-8",
			url : baseUrl + "/captcha/check",
			data :JSON.stringify(data),
			cache: false,
      crossDomain: true == !(document.all),
			success:function(res){
				resolve(res)
			},
			fail: function(err) {
				reject(err)
			}
		})
	}
   
    //定义Slide的构造函数
    var Slide = function(ele, opt) {
		this.$element = ele,
		this.backToken = null,
		this.moveLeftDistance = 0,
		this.secretKey = '',
        this.defaults = {
			baseUrl:"https://captcha.anji-plus.com/captcha-api",
			containerId:'',
        	captchaType:"blockPuzzle",
        	mode : 'fixed',	//弹出式pop，固定fixed
        	vOffset: 5,
            vSpace : 5,
            explain : '向右滑动完成验证',
            imgSize : {
	        	width: '310px',
	        	height: '155px',
			},
			blockSize : {
	        	width: '50px',
	        	height: '50px',
	        },
            circleRadius: '10px',
	        barSize : {
	        	width : '310px',
	        	height : '50px',
			},
			beforeCheck:function(){ return true},
            ready : function(){},
        	success : function(){},
            error : function(){}
            
        },
        this.options = $.extend({}, this.defaults, opt)
    };
    
    
    //定义Slide的方法
    Slide.prototype = {
        init: function() {
			var _this = this;
        	//加载页面
        	this.loadDom();
			_this.refresh();
        	this.options.ready();
        	
        	this.$element[0].onselectstart = document.body.ondrag = function(){ 
				return false; 
			};
        	
        	if(this.options.mode == 'pop')	{

				_this.$element.find('.verifybox-close').on('click', function() {
					_this.$element.find(".mask").css("display","none");
					_this.refresh();
				});
				
				var clickBtn = document.getElementById(this.options.containerId);
				clickBtn && (clickBtn.onclick = function(){
					if (_this.options.beforeCheck()) {
						_this.$element.find(".mask").css("display","block");
					}
				})
        	}
        	
					//按下
        	this.htmlDoms.move_block.on('touchstart', function(e) {
        		_this.start(e);
        	});
        	
        	this.htmlDoms.move_block.on('mousedown', function(e) {
        		_this.start(e);
        	});

            this.htmlDoms.sub_block.on('mousedown', function(e) {
                e.stopPropagation()
            });

        	//拖动
            window.addEventListener("touchmove", function(e) {
            	_this.move(e);
            });

            window.addEventListener("mousemove", function(e) {
            	_this.move(e);
            });
            
            //鼠标松开
            window.addEventListener("touchend", function() {
            	_this.end();
            });
            window.addEventListener("mouseup", function() {
            	_this.end();
            });
            
            //刷新
            _this.$element.find('.verify-refresh').on('click', function() {
				_this.refresh();
            });
        },
        
        //初始化加载
        loadDom : function() {
			this.status = false;	//鼠标状态
        	this.isEnd = false;		//是够验证完成
			this.setSize = this.resetSize(this);	//重新设置宽度高度
			this.plusWidth = 0;
			this.plusHeight = 0;
            this.x = 0;
            this.y = 0;
        	var panelHtml = '';
        	var wrapHtml = '';
			this.lengthPercent = (parseInt(this.setSize.img_width)-parseInt(this.setSize.block_width)- parseInt(this.setSize.circle_radius) - parseInt(this.setSize.circle_radius) * 0.8)/(parseInt(this.setSize.img_width)-parseInt(this.setSize.bar_height));
			
			wrapStartHtml = '<div class="mask">'+
								'<div class="verifybox" style="width:'+(parseInt(this.setSize.img_width)+30)+'px">'+
									'<div class="verifybox-top">'+
										'请完成安全验证'+
										'<span class="verifybox-close">'+
											'<i class="iconfont icon-close"></i>'+
										'</span>'+
									'</div>'+
									'<div class="verifybox-bottom" style="padding:15px">'+
										'<div style="position: relative;">';

			if (this.options.mode == 'pop') {
				panelHtml = wrapStartHtml
			}
			panelHtml += '<div class="verify-img-out">'+
							'<div class="verify-img-panel">'+
								'<div class="verify-refresh" style="z-index:3">'+
									'<i class="iconfont icon-refresh"></i>'+
								'</div>'+
								'<span class="verify-tips"  class="suc-bg"></span>'+
								'<img src="" class="backImg" style="width:100%;height:100%;display:block">'+
							'</div>'+
						'</div>';

			this.plusWidth = parseInt(this.setSize.block_width) + parseInt(this.setSize.circle_radius) * 2 - parseInt(this.setSize.circle_radius) * 0.2;
			this.plusHeight = parseInt(this.setSize.block_height) + parseInt(this.setSize.circle_radius) * 2 - parseInt(this.setSize.circle_radius) * 0.2;
			
			panelHtml +='<div class="verify-bar-area" style="width:'+this.setSize.img_width+',height:'+this.setSize.bar_height+',line-height:'+this.setSize.bar_height+'">'+
									'<span  class="verify-msg">'+this.options.explain+'</span>'+
									'<div class="verify-left-bar">'+
										'<span class="verify-msg"></span>'+
										'<div  class="verify-move-block">'+
											'<i  class="verify-icon iconfont icon-right"></i>'+
											'<div class="verify-sub-block">'+
												'<img src="" class="bock-backImg" alt=""  style="width:100%;height:100%;display:block">'+
											'</div>'+
										'</div>'+
									'</div>'+
								'</div>';
			wrapEndHtml = '</div></div></div></div>';
			if (this.options.mode == 'pop') {
				panelHtml += wrapEndHtml
			}

        	this.$element.append(panelHtml);
        	this.htmlDoms = {
        		tips: this.$element.find('.verify-tips'),
        		sub_block : this.$element.find('.verify-sub-block'),
        		out_panel : this.$element.find('.verify-img-out'),
        		img_panel : this.$element.find('.verify-img-panel'),
				img_canvas : this.$element.find('.verify-img-canvas'),
        		bar_area : this.$element.find('.verify-bar-area'),
        		move_block : this.$element.find('.verify-move-block'),
        		left_bar : this.$element.find('.verify-left-bar'),
        		msg : this.$element.find('.verify-msg'),
        		icon : this.$element.find('.verify-icon'),
        		refresh :this.$element.find('.verify-refresh')
        	};
        	
        	this.$element.css('position', 'relative');

			this.htmlDoms.sub_block.css({'height':this.setSize.img_height,'width':Math.floor(parseInt(this.setSize.img_width)*47/310)+ 'px',
									'top':-(parseInt(this.setSize.img_height) + this.options.vSpace) + 'px'})
			this.htmlDoms.out_panel.css('height', parseInt(this.setSize.img_height) + this.options.vSpace + 'px');
			this.htmlDoms.img_panel.css({'width': this.setSize.img_width, 'height': this.setSize.img_height});
			this.htmlDoms.bar_area.css({'width': this.setSize.img_width, 'height': this.setSize.bar_height, 'line-height':this.setSize.bar_height});
        	this.htmlDoms.move_block.css({'width': this.setSize.bar_height, 'height': this.setSize.bar_height});
        	this.htmlDoms.left_bar.css({'width': this.setSize.bar_height, 'height': this.setSize.bar_height});
        },
        

        //鼠标按下
        start: function(e) {
					if(!e.originalEvent.targetTouches) {    //兼容移动端
						var x = e.clientX;
					}else {     //兼容PC端
							var x = e.originalEvent.targetTouches[0].pageX;
					}
					// if(!e.touches) {    //兼容移动端
					// 	var x = e.clientX;
					// }else {     //兼容PC端
					// 		var x = e.touches[0].pageX;
					// }
			this.startLeft = Math.floor(x - this.htmlDoms.bar_area[0].getBoundingClientRect().left);
			this.startMoveTime = new Date().getTime();
        	if(this.isEnd == false) {
	        	this.htmlDoms.msg.text('');
	        	this.htmlDoms.move_block.css('background-color', '#337ab7');
	        	this.htmlDoms.left_bar.css('border-color', '#337AB7');
	        	this.htmlDoms.icon.css('color', '#fff');
	        	e.stopPropagation();
	        	this.status = true;
        	}
        },
        
        //鼠标移动
        move: function(e) {
        	if(this.status && this.isEnd == false) {
	            if(!e.touches) {    //兼容移动端
	                var x = e.clientX;
	            }else {     //兼容PC端
	                var x = e.touches[0].pageX;
	            }
				var bar_area_left = this.htmlDoms.bar_area[0].getBoundingClientRect().left;
				var move_block_left = x - bar_area_left; //小方块相对于父元素的left值
				if(move_block_left >= (this.htmlDoms.bar_area[0].offsetWidth - parseInt(this.setSize.bar_height) + parseInt(parseInt(this.setSize.block_width)/2) - 2) ) {
					move_block_left = (this.htmlDoms.bar_area[0].offsetWidth - parseInt(this.setSize.bar_height) + parseInt(parseInt(this.setSize.block_width)/2)- 2);
				}
	            if(move_block_left <= parseInt(parseInt(this.setSize.block_width)/2)) {
            		move_block_left = parseInt(parseInt(this.setSize.block_width)/2);
            	}
				//拖动后小方块的left值
	            this.htmlDoms.move_block.css('left', move_block_left-this.startLeft + "px");
	            this.htmlDoms.left_bar.css('width', move_block_left-this.startLeft + "px");
				this.htmlDoms.sub_block.css('left', "0px");
				this.moveLeftDistance = move_block_left - this.startLeft
	        }
        },
        
        //鼠标松开
        end: function() {
			this.endMovetime = new Date().getTime();
        	var _this = this;
        	//判断是否重合
        	if(this.status  && this.isEnd == false) {
				var vOffset = parseInt(this.options.vOffset);
				this.moveLeftDistance = this.moveLeftDistance * 310/ parseInt(this.setSize.img_width)
				//图片滑动

				var data = {
					captchaType:this.options.captchaType,
					"pointJson": this.secretKey ? aesEncrypt(JSON.stringify({x:this.moveLeftDistance,y:5.0}),this.secretKey):JSON.stringify({x:this.moveLeftDistance,y:5.0}),
					"token":this.backToken,
					clientUid: localStorage.getItem('slider'), 
					ts: Date.now()
				}
				var captchaVerification = this.secretKey ? aesEncrypt(this.backToken+'---'+JSON.stringify({x:this.moveLeftDistance,y:5.0}),this.secretKey):this.backToken+'---'+JSON.stringify({x:this.moveLeftDistance,y:5.0})
				checkPictrue(data,this.options.baseUrl,function(res){
					// 请求反正成功的判断
					if (res.repCode=="0000") {
						_this.htmlDoms.move_block.css('background-color', '#5cb85c');
						_this.htmlDoms.left_bar.css({'border-color': '#5cb85c', 'background-color': '#fff'});
						_this.htmlDoms.icon.css('color', '#fff');
						_this.htmlDoms.icon.removeClass('icon-right');
						_this.htmlDoms.icon.addClass('icon-check');
						//提示框
						_this.htmlDoms.tips.addClass('suc-bg').removeClass('err-bg')
						// _this.htmlDoms.tips.css({"display":"block",animation:"move 1s cubic-bezier(0, 0, 0.39, 1.01)"});
						_this.htmlDoms.tips.animate({"bottom":"0px"});
						_this.htmlDoms.tips.text(((_this.endMovetime-_this.startMoveTime)/1000).toFixed(2) + 's验证成功');
						_this.isEnd = true;
						setTimeout(function(){
							_this.$element.find(".mask").css("display","none");
							// _this.htmlDoms.tips.css({"display":"none",animation:"none"});
							_this.htmlDoms.tips.animate({"bottom":"-35px"});
							_this.refresh();
						},1000)
						_this.options.success({'captchaVerification':captchaVerification});
					}else{
						_this.htmlDoms.move_block.css('background-color', '#d9534f');
						_this.htmlDoms.left_bar.css('border-color', '#d9534f');
						_this.htmlDoms.icon.css('color', '#fff');
						_this.htmlDoms.icon.removeClass('icon-right');
						_this.htmlDoms.icon.addClass('icon-close');

						_this.htmlDoms.tips.addClass('err-bg').removeClass('suc-bg')
						// _this.htmlDoms.tips.css({"display":"block",animation:"move 1.3s cubic-bezier(0, 0, 0.39, 1.01)"});
						_this.htmlDoms.tips.animate({"bottom":"0px"});
						_this.htmlDoms.tips.text(res.repMsg)
						setTimeout(function () { 
							_this.refresh();
							_this.htmlDoms.tips.animate({"bottom":"-35px"});
						}, 1000);

						// setTimeout(function () {
						// 	// _this.htmlDoms.tips.css({"display":"none",animation:"none"});
						// },1300)
						_this.options.error(this);
					}
				})
	            this.status = false;
        	}
		},
		
        resetSize : function(obj) {
        	var img_width,img_height,bar_width,bar_height,block_width,block_height,circle_radius;	//图片的宽度、高度，移动条的宽度、高度
        	var parentWidth = obj.$element.parent().width() || $(window).width();
        	var parentHeight = obj.$element.parent().height() || $(window).height();
        	
       		if(obj.options.imgSize.width.indexOf('%')!= -1){
        		img_width = parseInt(obj.options.imgSize.width)/100 * parentWidth + 'px';
		　　}else {
				img_width = obj.options.imgSize.width;
			}
		
			if(obj.options.imgSize.height.indexOf('%')!= -1){
        		img_height = parseInt(obj.options.imgSize.height)/100 * parentHeight + 'px';
		　　}else {
				img_height = obj.options.imgSize.height;
			}
		
			if(obj.options.barSize.width.indexOf('%')!= -1){
        		bar_width = parseInt(obj.options.barSize.width)/100 * parentWidth + 'px';
		　　}else {
				bar_width = obj.options.barSize.width;
			}
		
			if(obj.options.barSize.height.indexOf('%')!= -1){
        		bar_height = parseInt(obj.options.barSize.height)/100 * parentHeight + 'px';
		　　}else {
				bar_height = obj.options.barSize.height;
			}
			
			if(obj.options.blockSize) {
				if(obj.options.blockSize.width.indexOf('%')!= -1){
					block_width = parseInt(obj.options.blockSize.width)/100 * parentWidth + 'px';
			　　}else {
					block_width = obj.options.blockSize.width;
				}
				
			
				if(obj.options.blockSize.height.indexOf('%')!= -1){
					block_height = parseInt(obj.options.blockSize.height)/100 * parentHeight + 'px';
			　　}else {
					block_height = obj.options.blockSize.height;
				}
			}

			if(obj.options.circleRadius) {
				if(obj.options.circleRadius.indexOf('%')!= -1){
					circle_radius = parseInt(obj.options.circleRadius)/100 * parentHeight + 'px';
			　　}else {
					circle_radius = obj.options.circleRadius;
				}
			}
		
			return {img_width : img_width, img_height : img_height, bar_width : bar_width, bar_height : bar_height, block_width : block_width, block_height : block_height, circle_radius : circle_radius};
       	},

        //刷新
        refresh: function() {
			var _this = this;
        	this.htmlDoms.refresh.show();
        	this.$element.find('.verify-msg:eq(1)').text('');
        	this.$element.find('.verify-msg:eq(1)').css('color', '#000');
        	this.htmlDoms.move_block.animate({'left':'0px'}, 'fast');
			this.htmlDoms.left_bar.animate({'width': parseInt(this.setSize.bar_height)}, 'fast');
			this.htmlDoms.left_bar.css({'border-color': '#ddd'});
			
			this.htmlDoms.move_block.css('background-color', '#fff');
			this.htmlDoms.icon.css('color', '#000');
			this.htmlDoms.icon.removeClass('icon-close');
			this.htmlDoms.icon.addClass('icon-right');
			this.$element.find('.verify-msg:eq(0)').text(this.options.explain);
			this.isEnd = false;
			getPictrue({captchaType:"blockPuzzle", clientUid: localStorage.getItem('slider'), ts: Date.now()},this.options.baseUrl,function (res) {
				if (res.repCode=="0000") {
					_this.$element.find(".backImg")[0].src = 'data:image/png;base64,'+res.repData.originalImageBase64
					_this.$element.find(".bock-backImg")[0].src = 'data:image/png;base64,'+res.repData.jigsawImageBase64
					_this.secretKey = res.repData.secretKey
					_this.backToken = res.repData.token
				} else {
					_this.$element.find(".backImg")[0].src = 'images/default.jpg'
					_this.$element.find(".bock-backImg")[0].src = ''
					_this.htmlDoms.tips.addClass('err-bg').removeClass('suc-bg')
					_this.htmlDoms.tips.animate({"bottom":"0px"});
					_this.htmlDoms.tips.text(res.repMsg)
					setTimeout(function () { 
							_this.htmlDoms.tips.animate({"bottom":"-35px"});
						}, 1000);
					}
			});
			this.htmlDoms.sub_block.css('left', "0px");
        },
    };


    //定义Points的构造函数
    var Points = function(ele, opt) {
		this.$element = ele,
		this.backToken = null,
		this.secretKey = '',
        this.defaults = {
			baseUrl:"https://captcha.anji-plus.com/captcha-api",
			captchaType:"clickWord",
			containerId:'',
        	mode : 'fixed',	//弹出式pop，固定fixed
		    checkNum : 3,	//校对的文字数量
		    vSpace : 5,	//间隔
        	imgSize : {
	        	width: '310px',
	        	height: '155px',
	        },
	        barSize : {
	        	width : '310px',
	        	height : '50px',
			},
			beforeCheck: function(){ return true},
	        ready : function(){},
        	success : function(){},
            error : function(){}
        },
        this.options = $.extend({}, this.defaults, opt)
    };
    
    //定义Points的方法
    Points.prototype = {
    	init : function() {
			var _this = this;
			//加载页面
        	_this.loadDom();
        	 
        	_this.refresh();
        	_this.options.ready();
        	
        	this.$element[0].onselectstart = document.body.ondrag = function(){ 
				return false; 
			};
			
			if(this.options.mode == 'pop')	{
				
				_this.$element.find('.verifybox-close').on('click', function() {
					_this.$element.find(".mask").css("display","none");
				});
				
				var clickBtn = document.getElementById(this.options.containerId);
				clickBtn && (clickBtn.onclick = function(){
					if (_this.options.beforeCheck()) {
						_this.$element.find(".mask").css("display","block");
					}
				})
				
        	}
		 	// 注册点击验证事件
        	_this.$element.find('.back-img').on('click', function(e) {
        		
				_this.checkPosArr.push(_this.getMousePos(this, e));
				
				if(_this.num == _this.options.checkNum) {
					_this.num = _this.createPoint(_this.getMousePos(this, e));
					 //按比例转换坐标值
					 _this.checkPosArr = _this.pointTransfrom(_this.checkPosArr,_this.setSize);
					setTimeout(function(){
						var data = {
							captchaType:_this.options.captchaType,
							"pointJson":_this.secretKey ? aesEncrypt(JSON.stringify(_this.checkPosArr),_this.secretKey):JSON.stringify(_this.checkPosArr),
							"token":_this.backToken,
							clientUid: localStorage.getItem('point'), 
							ts: Date.now()
						}
						var captchaVerification = _this.secretKey ? aesEncrypt(_this.backToken+'---'+JSON.stringify(_this.checkPosArr),_this.secretKey):_this.backToken+'---'+JSON.stringify(_this.checkPosArr)
						checkPictrue(data, _this.options.baseUrl,function(res){
							if (res.repCode=="0000") {
								_this.$element.find('.verify-bar-area').css({'color': '#4cae4c', 'border-color': '#5cb85c'});
								_this.$element.find('.verify-msg').text('验证成功');
								// _this.$element.find('.verify-refresh').hide();
								_this.$element.find('.verify-img-panel').unbind('click');
								setTimeout(function(){
									_this.$element.find(".mask").css("display","none");
									_this.refresh();
								},1000)
								_this.options.success({'captchaVerification':captchaVerification});
							}else{
								_this.options.error(_this);
								_this.$element.find('.verify-bar-area').css({'color': '#d9534f', 'border-color': '#d9534f'});
								_this.$element.find('.verify-msg').text('验证失败');
								setTimeout(function () { 
									_this.$element.find('.verify-bar-area').css({'color': '#000','border-color': '#ddd'});
									_this.refresh();
								}, 400);
							}
						})
					}, 400);
					
				}
				if(_this.num < _this.options.checkNum) {
					_this.num = _this.createPoint(_this.getMousePos(this, e));
				}
        	});
        	
        	 //刷新
            _this.$element.find('.verify-refresh').on('click', function() {
            	_this.refresh();
            });
        	
    	},
    	
    	//加载页面
    	loadDom : function() {
    		
    		this.fontPos = [];	//选中的坐标信息
    		this.checkPosArr = [];	//用户点击的坐标
    		this.num = 1;	//点击的记数
    		
			var panelHtml = '';
			var wrapStartHtml = '';
        	
			this.setSize = Slide.prototype.resetSize(this);	//重新设置宽度高度
			
			wrapStartHtml = '<div class="mask">'+
								'<div class="verifybox" style="width:'+(parseInt(this.setSize.img_width)+30)+'px">'+
									'<div class="verifybox-top">'+
										'请完成安全验证'+
										'<span class="verifybox-close">'+
											'<i class="iconfont icon-close"></i>'+
										'</span>'+
									'</div>'+
									'<div class="verifybox-bottom" style="padding:15px">'+
										'<div style="position: relative;">';

			if (this.options.mode == 'pop') {
				panelHtml = wrapStartHtml
			}
        	
			panelHtml += '<div class="verify-img-out">'+
							'<div class="verify-img-panel">'+
								'<div class="verify-refresh" style="z-index:3">'+
									'<i class="iconfont icon-refresh"></i>'+
								'</div>'+
								'<img src="" class="back-img" width="'+this.setSize.img_width+'" height="'+this.setSize.img_height+'">'+
							'</div>'+
						'</div>'+
						'<div class="verify-bar-area" style="width:'+this.setSize.img_width+',height:'+this.setSize.bar_height+',line-height:'+this.setSize.bar_height+'">'+
							'<span  class="verify-msg"></span>'+
						'</div>';
			
			wrapEndHtml = '</div></div></div></div>';

			if (this.options.mode == 'pop') {
				panelHtml += wrapEndHtml
			}
        	
        	this.$element.append(panelHtml);
        	
        	this.htmlDoms = {
				back_img : this.$element.find('.back-img'),
        		out_panel : this.$element.find('.verify-img-out'),
        		img_panel : this.$element.find('.verify-img-panel'),
        		bar_area : this.$element.find('.verify-bar-area'),
        		msg : this.$element.find('.verify-msg'),
        	};
        	
        	this.$element.css('position', 'relative');

        	this.htmlDoms.out_panel.css('height', parseInt(this.setSize.img_height) + this.options.vSpace + 'px');
    		this.htmlDoms.img_panel.css({'width': this.setSize.img_width, 'height': this.setSize.img_height, 'background-size' : this.setSize.img_width + ' '+ this.setSize.img_height, 'margin-bottom': this.options.vSpace + 'px'});
    		this.htmlDoms.bar_area.css({'width': this.setSize.img_width, 'height': this.setSize.bar_height, 'line-height':this.setSize.bar_height});
    		
    	},
    	
    	//获取坐标
    	getMousePos :function(obj, event) {
            var e = event || window.event;
            var scrollX = document.documentElement.scrollLeft || document.body.scrollLeft;
            var scrollY = document.documentElement.scrollTop || document.body.scrollTop;
            var x = e.clientX - ($(obj).offset().left - $(window).scrollLeft());
    		var y = e.clientY - ($(obj).offset().top - $(window).scrollTop());
    		
            return {'x': x, 'y': y};
     	},
     	
       	//创建坐标点
       	createPoint : function (pos) {
			   this.htmlDoms.img_panel.append('<div class="point-area" style="background-color:#1abd6c;color:#fff;z-index:9999;width:20px;height:20px;text-align:center;line-height:20px;border-radius: 50%;position:absolute;'
			   										+'top:'+parseInt(pos.y-10)+'px;left:'+parseInt(pos.x-10)+'px;">'+this.num+'</div>');
       		return ++this.num;
       	},
 
       	//刷新
        refresh: function() {
        	var _this = this;
        	this.$element.find('.point-area').remove();
        	this.fontPos = [];
        	this.checkPosArr = [];
        	this.num = 1;
			getPictrue({captchaType:"clickWord", clientUid: localStorage.getItem('point'), ts: Date.now()},_this.options.baseUrl,function(res){
				if (res.repCode=="0000") {
					_this.htmlDoms.back_img[0].src ='data:image/png;base64,'+ res.repData.originalImageBase64;
					_this.backToken = res.repData.token;
					_this.secretKey = res.repData.secretKey;
					var text = '请依次点击【' + res.repData.wordList.join(",") + '】';
					_this.$element.find('.verify-msg').text(text);
				} else {
					_this.htmlDoms.back_img[0].src = 'images/default.jpg';
					_this.$element.find('.verify-msg').text(res.repMsg);
				}
			})
        
		},
		pointTransfrom:function(pointArr,imgSize){
			var newPointArr = pointArr.map(function(p){
				var x = Math.round(310 * p.x/parseInt(imgSize.img_width))
				var y =Math.round(155 * p.y/parseInt(imgSize.img_height))
				return {'x':x,'y':y}
			})
			return newPointArr
		}
    };
    //在插件中使用slideVerify对象  初始化与是否弹出无关 ,不应该耦合
    $.fn.slideVerify = function(options, callbacks) {
		var slide = new Slide(this, options);
		if (slide.options.mode=="pop") {
			slide.init();
		}else if (slide.options.mode=="fixed") {
			slide.init();
		}
    };
    
    //在插件中使用clickVerify对象
    $.fn.pointsVerify = function(options, callbacks) {
        var points = new Points(this, options);
		if (points.options.mode=="pop") {
			points.init();
		}else if (points.options.mode=="fixed") {
			points.init();
		}
    };
   
})(jQuery, window, document);
