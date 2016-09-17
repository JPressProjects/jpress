$(function() {
	$(".jp-onmouse").mouseover(function() {
		$(this).find(".row-actions").show();
	}).mouseout(function() {
		$(".row-actions").hide()
	})
});

function checkAll(checkbox) {
	var items = document.getElementsByName("dataItem");
	for (var i = 0; i < items.length; i++) {
		items[i].checked = checkbox.checked;
	}
}


jQuery.jp = { 
	alert : function(){
		window.alert("test");
	},
	
	submit : function (formId,resultFunc){
		formId = formId || "form";
		resultFunc = resultFunc || function(){
			toastr.success(data.message,'操作成功');
		}
		
		$(formId).ajaxSubmit({
			type : "post", 
			dataType : "json", 
			success : function(data) { 
				resultFunc();
			},
			error : function() {
				toastr.success('信息提交错误','错误');
			}
		});
	},
	
	ajax : function(){
		
	}
}

