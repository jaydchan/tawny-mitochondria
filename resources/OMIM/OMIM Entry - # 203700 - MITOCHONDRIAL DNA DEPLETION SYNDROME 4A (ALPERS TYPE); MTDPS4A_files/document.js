////////////////////////////////////////////////
//
// Document Functions
//


$(document).ready(function(){


    // sets up toggles
    $(".toggle_content").hide();
    $(".toggle").addClass("closed");
    placeToggleIcons();


    // toggle toggle content
    $(".toggle").click(function(){
        var toggleRel = $(this).attr("rel");
        $("#" + toggleRel).slideToggle("fast");
        if ( $(this).hasClass("open") ) {
            $(this).removeClass("open").addClass("closed");
        }
        else {
            $(this).removeClass("closed").addClass("open");
        }
        placeToggleIcons();
    });


    // drawer
    function autosizeDrawer(){
        var accordionHeight = $(".accordion").height();
        var drawerHeight = accordionHeight - 20;
        $(".drawer").css("height", drawerHeight + "px");
    }


    // open & closed icons
    function placeToggleIcons(){
        $(".toggle span").empty();
        $(".toggle.open").prepend("<span>&#x25BE;</span> ");
        $(".toggle.closed").prepend("<span>&#x25B8;</span> ");
    }

    function placeAccordionIcons(){
        $(".accordion .ui-accordion-header .ui-icon").empty();
        $(".accordion .ui-accordion-header .ui-icon-triangle-1-e").append("&#x25B8;");
        $(".accordion .ui-accordion-header .ui-icon-triangle-1-s").append("&#x25BE;");
        $(".accordion .ui-accordion-header .external").siblings().empty();
    }


    // sets up jQueryUI accordion
    $(".accordion" ).accordion({
        collapsible: true,
        active: 0,
        autoHeight: false,
        navigation: true,
        changestart: function(event, ui) {
            placeAccordionIcons();
        },
        change: function(event, ui) {
            placeAccordionIcons();
            autosizeDrawer();
        }
    });
    autosizeDrawer();
    placeAccordionIcons();


$("#mendeliangenomics").off("click");

$("#mendeliangenomics").click(function(event) {
//     console.log("break " + $(this).attr("href"));
    event.stopPropagation();
});



    

// console.dir( jQuery('#mendeliangenomics').data('events') );
  

});
