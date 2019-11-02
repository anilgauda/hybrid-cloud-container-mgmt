$(document).ready(function () {
    var url = window.location;
   $('#accordionSidebar a.nav-link').filter(function() {
         return this.href == url;
    }).parent().addClass('active');
});