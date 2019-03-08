(function() { // 闭包
function load_script(xyUrl, callback) {
	var head = document.getElementsByTagName('head')[0];
	var script = document.createElement('script');
	script.type = 'text/javascript';
	script.src = xyUrl;
	// 借鉴了jQuery的script跨域方法
	script.onload = script.onreadystatechange = function() {
		if ((!this.readyState || this.readyState === "loaded" || this.readyState === "complete")) {
			callback && callback();
			// Handle memory leak in IE
			script.onload = script.onreadystatechange = null;
			if (head && script.parentNode) {
				head.removeChild(script);
			}
		}
	};
	// Use insertBefore instead of appendChild to circumvent an IE6 bug.
	head.insertBefore(script, head.firstChild);
}

function translate(point, type, callback) {
	var callbackName = 'cbk_' + Math.round(Math.random() * 10000); // 随机函数名
	var xyUrl = "http://api.map.baidu.com/ag/coord/convert?from=" + type
		+ "&to=4&x=" + point.lng + "&y=" + point.lat
		+ "&callback=BMap.Convertor." + callbackName;
	
	// 动态创建script标签
	load_script(xyUrl);
	BMap.Convertor[callbackName] = function(xyResult) {
		delete BMap.Convertor[callbackName]; // 调用完需要删除改函数
		var point = new BMap.Point(xyResult.x, xyResult.y);
		callback && callback(point);
	}
}

window.BMap = window.BMap || {};
BMap.Convertor = {};
BMap.Convertor.translate = translate;
})();