function setSection(to, from)
  {
    if (!(to.hasClass("active")))
    {
      from.animate({"left":"-100%"},100,'linear')
      to.animate({"left":"0%"},100,'linear',function()
      {
        from.css("left","100%");
        from.removeClass("active");
        to.addClass("active");
      });
      if (to.is("#one")) {
        $("#pending-tab").addClass("active-tab");
        $("#pending-alt").css("display", "inline");
        $("#pending").css("display", "none");
      }
      if (from.is("#one")) {
        $("#pending-tab").removeClass("active-tab");
        $("#pending").css("display", "inline");
        $("#pending-alt").css("display", "none");
      }
      if (to.is("#four")) {
        $("#complete-tab").addClass("active-tab");
        $("#complete-alt").css("display", "inline");
        $("#complete").css("display", "none");
      }
      if (from.is("#four")) {
        $("#complete-tab").removeClass("active-tab");
        $("#complete").css("display", "inline");
        $("#complete-alt").css("display", "none");
      }
      $('#new-div').animate({"top":"100%"},100,'linear');
    }
  }
  $(document).ready(function(){
    $('a').on('click', function(event)
    {
      var sectionId = $(this).attr("data-section"),
      $toSlide= $("#container section#"+sectionId),
      $fromSlide= $('.active');
      setSection($toSlide, $fromSlide);
    });
    $('#newbut').on('click', function(event)
    {
      $('#new-div').animate({"top":"0%"},100,'linear');
    });
    $('#hide-new').on('click', function(event)
    {
      $('#new-div').animate({"top":"100%"},100,'linear');
    });
    $('#hide-checklist').on('click', function(event)
    {
      setSection($('#one'), $('#two'));
    });
  });
  window.addEventListener('load', function() {
    FastClick.attach(document.body);
  }, false);