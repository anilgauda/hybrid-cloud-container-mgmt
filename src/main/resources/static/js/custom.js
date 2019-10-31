function setNavActive(elementId) {
	$('ul li').each(function(i, k) {
		if ($(k).hasClass("active")) {
			fetchedId=$(k).attr('id');
			if (!(typeof fetchedId == "undefined")&&(elementId != fetchedId)) {
				$("#"+fetchedId).removeClass("active");
				$("#"+elementId).addClass("active");
			}
		}
	});
}