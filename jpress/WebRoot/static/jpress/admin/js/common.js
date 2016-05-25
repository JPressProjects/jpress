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
