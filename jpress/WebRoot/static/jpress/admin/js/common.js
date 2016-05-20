$(function() {

});

function check_all(checkbox) {
	var items = document.getElementsByName("dataItem");
	for (var i = 0; i < items.length; i++) {
		items[i].checked = checkbox.checked;
	}
}
