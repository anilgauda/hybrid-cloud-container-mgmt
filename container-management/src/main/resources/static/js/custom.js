function setNavActive(url) {
   const el = $('#accordionSidebar a.nav-link').filter(function() {
         return this.href == url;
    });

    if (el.length) {
        el.parent().addClass('active');
        localStorage.setItem("lastVisitedUrl", url);
        return true;
    } else {
        return false;
    }
}

$(document).ready(function () {
    var url = window.location;
    if (!setNavActive(url) && localStorage.getItem("lastVisitedUrl") != null) {
        setNavActive(localStorage.getItem("lastVisitedUrl"));
    }

    $('button[name="delete"]').on('click', function(e) {
      var $form = $(this).closest('form');
      e.preventDefault();
      $('#confirm').modal({
          backdrop: 'static',
          keyboard: false
      })
      .on('click', '#delete', function(e) {
          $form.trigger('submit');
        });
    });
});