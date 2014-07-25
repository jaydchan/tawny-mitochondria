////////////////////////////////////////////////
//
// Site-Wide Functions
//


$(document).ready(function(){


    ////////////////////////////////////////////////
    //
    // Generic
    //

    // Definition qtip - this is used for any definitions, like 
    // prefixes, inheritance, external link information
    $('.definition').qtip({
        position: {
//             corner: {
//                 target: 'rightMiddle',
//                 tooltip: 'leftMiddle'
//             },
            adjust: {
                screen: true
            }
        },
        style: {
            name: 'cream',
            border: {
                width: 1,
                radius: 0
            },
            title: {
                'font-size': '12px'
            },
            'font-size': '12px',
            tip: 'leftMiddle'
        },
        show: {
            delay: 140,
            solo: true
        },
        hide: {
            fixed: true,
            delay: 1000
        }
    });




    // Adds hover class for drop-down menus in IE
    $(".nav>li").hover(
        function() { $(this).addClass("hover"); },
        function() { $(this).removeClass("hover"); }
    );




    ////////////////////////////////////////////////
    //
    // Result/Document Pages
    //

    // Sets up highlights class
    $("#results span.highlight").addClass("highlighted");
    $("#toggle_highlights").addClass("highlighted");
  
    // Toggles highlight
    $("#toggle_highlights").click(function(){
        if ($("#toggle_highlights").hasClass("highlighted") ) {
            $("#toggle_highlights").removeClass("highlighted");
            $("#results span.highlight").removeClass("highlighted");
//             console.log("removeClass highlighted");
        }
        else {
            $("#toggle_highlights").addClass("highlighted");
            $("#results span.highlight").addClass("highlighted");
//             console.log("addClass highlighted");
        }
    });

  
    // Sets up changes class
    $("#results div.change").addClass("changed");
    $("#results td.change").addClass("changed");
    $("#results span.change").addClass("changed");
    $("#toggle_changes").addClass("changed");
  
    // Toggles change
    $("#toggle_changes").click(function(){
        if ($("#toggle_changes").hasClass("changed") ) {
            $("#toggle_changes").removeClass("changed");
            $("#results div.change").removeClass("changed");
            $("#results td.change").removeClass("changed");
            $("#results span.change").removeClass("changed");
//             console.log("removeClass changed");
        }
        else {
            $("#toggle_changes").addClass("changed");
            $("#results div.change").addClass("changed");
            $("#results td.change").addClass("changed");
            $("#results span.change").addClass("changed");
//             console.log("addClass changed");
        }
    });

  
 
    $("#toggle_clinicalids").addClass("toggle_clinicalids");

    // Toggles clinical IDs
    $("#toggle_clinicalids").click(function(){
        if ($("#toggle_clinicalids").hasClass("toggle_clinicalids") ) {
            $("#toggle_clinicalids").removeClass("toggle_clinicalids");
            $("span.toggle_clinicalids").removeClass("hidden");
//             console.log("removeClass highlighted");
        }
        else {
            $("#toggle_clinicalids").addClass("toggle_clinicalids");
            $("span.toggle_clinicalids").addClass("hidden");
//             console.log("addClass highlighted");
        }
    });

  
    // Sets up search result links module
    $(".dialog").dialog({
        autoOpen:   false,
        closeText:  " [x]",
        width:      215
//         width:      215
    });
  
    // Opens search result links module
    $(".open_dialog").click(function(){
        var offset = $(this).offset();
        var dialogLeft = offset.left - 154;
        var dialogRel = $(this).attr("rel");
        $(".dialog").dialog("close");
        $("#" + dialogRel).dialog("option", "position", [dialogLeft,offset.top]);
        $("#" + dialogRel).dialog("open");
        return false;
    });


  
  
    // show & hide drawer
    $(".floating-menu h3 a").click(function(){
        $(".drawer.shown").animate({
            marginLeft: "0"
        }).fadeOut().css("overflow", "hidden").removeClass("shown");
        $(".toggle_drawer.active").removeClass("active");
    });



    // Save the drawer rel to prevent shortened timeout
    var savedDrawerRel = null;

    // show & hide drawer
    $(".toggle_drawer").click(function(){
        
        if ( $(this).hasClass("active") ) {
            var drawerRel = $(this).attr("rel");
            $(this).removeClass("active");
            $("#" + drawerRel).animate({
                marginLeft: "0"
            }).fadeOut().css("overflow", "hidden").removeClass("shown");
        
            // And destroy the idle timer
            $("#" + drawerRel).idleTimer('destroy');
            savedDrawerRel = null;

        }
        else {
            var drawerRel = $(this).attr("rel");
            $(".drawer.shown").animate({
                marginLeft: "0"
            }).fadeOut().css("overflow", "hidden").removeClass("shown");
            $(".toggle_drawer").removeClass("active");
            $(this).addClass("active");

            if ( $(this).hasClass("details") ) {
                $("#" + drawerRel).css({
                    'position': 'fixed',
                    'left': $('.floating-menu > div:first').offset().left,
                    'top': $('.floating-menu > div:first').offset().top + 15
                });
                $("#" + drawerRel).fadeIn().animate({
                    'left': $('.floating-menu > div:first').offset().left - $('.floating-menu > div:first').innerWidth() + 12,
                    'top': $('.floating-menu > div:first').offset().top + 15
                }).css("overflow", "auto").addClass("shown");
            }
            else {
                $("#" + drawerRel).fadeIn().animate({
                    marginLeft: "-180px"
                }).css("overflow", "auto").addClass("shown");
            }

            if ( savedDrawerRel ) {
                $("#" + savedDrawerRel).idleTimer('destroy');
            }

            // Set a 5 second timeout
            $("#" + drawerRel).idleTimer(5000);
    
            // Set the idle timer to mark all drawers as active and close them all when it times out
            $("#" + drawerRel).bind("idle.idleTimer", function(){
                
                $(".toggle_drawer").addClass("active")
                $(".toggle_drawer").click();
                
                // And destroy the idle timer
                $("#" + drawerRel).idleTimer('destroy');
                savedDrawerRel = null;
       
            });
            
            savedDrawerRel = drawerRel;
        }

    });
  



    ////////////////////////////////////////////////
    //
    // Entry Result/Document Page
    //
    
    // autofill configuration
    $(".autofill #entrySearch").autofill({
        value:              "Search OMIM",
        activeTextColor:    "#111",
        defaultTextColor:   "#999"
    });


    // Autocomplete entry selection
    function onEntrySelect(item) {
        
        // Get the item value
        var value = item.value;
        
        // Match on 'mim number - title', we just want the mim numbers
        var matcher = /^(\d{6}) - .*$/;
        var found = value.match(matcher);
        
        // Set the value if the search field
        if ( found ) {
            $("#entrySearch").val(found[1]);
        }
        else {
            $("#entrySearch").val(value);
        }

    };

    // Autocomplete - https://github.com/dyve/jquery-autocomplete
    $("#entrySearch").autocomplete({url: '/ajax/autocomplete', useCache: false, matchSubset: false, preventDefaultReturn: false, extraParams: {'index': 'entry', 'limit': '10'}, onItemSelect: onEntrySelect});

    // Entry reference qtip
    $('.entry-reference').qtip({
        position: {
            corner: {
                target: 'bottomMiddle',
                tooltip: 'topMiddle'
            },
            adjust: {
                screen: true
            }
        },
        style: {
            name: 'light',
            background: '#F0F0F0',
            border: {
                width: 1,
                radius: 0,
                color: '#878787'
            },
            width: {
                max: 300
            },
            title: {
                'font-size': '12px'
            },
            'font-size': '12px',
            tip: 'topMiddle'
        },
        show: {
            delay: 140,
            solo: true
        },
        hide: {
            fixed: true,
            delay: 1000
        }
    });






    ////////////////////////////////////////////////
    //
    // Clinical Synopsis Result/Document Page
    //
  
    // autofill configuration
    $(".autofill #clinicalSynopsisSearch").autofill({
        value:              "Search clinical synopses",
        activeTextColor:    "#111",
        defaultTextColor:   "#999"
    });
  
    // Autocomplete clinical synopsis selection
    function onClinicalSynopsisSelect(item) {
        
        // Get the item value
        var value = item.value;
        
        // Match on 'mim number - title', we just want the mim numbers
        var matcher = /^(\d{6}) - .*$/;
        var found = value.match(matcher);
        
        // Set the value if the search field
        if ( found ) {
            $("#entrySearch").val(found[1]);
        }
        else {
            $("#entrySearch").val(value);
        }

    };

    // Autocomplete - https://github.com/dyve/jquery-autocomplete
    $("#clinicalSynopsisSearch").autocomplete({url: '/ajax/autocomplete', useCache: false, matchSubset: false, preventDefaultReturn: false, extraParams: {'index': 'clinicalSynopsis', 'limit': '10'}, onItemSelect: onClinicalSynopsisSelect});

    // Clinical feature qtip
    $('.clinical-feature').qtip({
        position: {
            corner: {
                target: 'bottomMiddle',
                tooltip: 'topMiddle'
            },
            adjust: {
                screen: true
            }
        },
        style: {
            name: 'light',
            background: '#F0F0F0',
            border: {
                width: 1,
                radius: 0,
                color: '#878787'
            },
            width: {
                max: 300
            },
            title: {
                'font-size': '12px'
            },
            'font-size': '12px',
            tip: 'topMiddle'
        },
        show: {
            delay: 140,
            solo: true
        },
        hide: {
            fixed: true,
            delay: 1000
        }
    });


    // Clinical image qtip
    $('.clinical-image').qtip({
        position: {
            corner: {
                target: 'rightMiddle',
                tooltip: 'leftMiddle'
            }
        },
        style: {
            name: 'light',
            background: '#FAFAFA',
            border: {
                width: 1,
                radius: 0,
                color: '#878787'
            },
            width: {
                min:  270,
                max:  800
            },
            title: {
                'font-size': '12px'
            },
            'font-size': '12px',
            tip: 'leftMiddle'
        },
        show: {
            delay: 140,
            solo: true
        },
        hide: {
            fixed: true,
            delay: 1000
        }
    });




    ////////////////////////////////////////////////
    //
    // Gene Map Result Page
    //
  
    // autofill configuration
    $(".autofill #geneMapSearch").autofill({
        value:              "Search gene map",
        activeTextColor:    "#111",
        defaultTextColor:   "#999"
    });
  
    // Autocomplete gene map selection
    function onGeneMapSelect(item) {
        
        // Get the item value
        var value = item.value;
        
        // Match on 'mim number - title', we just want the mim numbers
        var matcher = /^(\d{6}) - .*$/;
        var found = value.match(matcher);
        
        // Set the value if the search field
        if ( found ) {
            $("#entrySearch").val(found[1]);
        }
        else {
            $("#entrySearch").val(value);
        }

    };

    // Autocomplete - https://github.com/dyve/jquery-autocomplete
    $("#geneMapSearch").autocomplete({url: '/ajax/autocomplete', useCache: false, matchSubset: false, preventDefaultReturn: false, extraParams: {'index': 'geneMap', 'limit': '10'}, onItemSelect: onGeneMapSelect});



    // Scrolling search to fixed
    $('.searchScrollToFixed').scrollToFixed();


    $(".sectionJump" ).click(function(){
        var sectionID = $(this).attr("sectionID");
        $.scrollTo("#" + sectionID, {offset: -160});
    });


});



$(window).on("load", function(){

// console.log('window.location [' + window.location + ']');
// console.log('window.location.href [' + window.location.href + ']');
// console.log('document.location [' + document.location + ']');
// console.log('document.location.href [' + document.location.href + ']');
// console.log('document.URL [' + document.URL + ']');
    
    if ( document.location.href.indexOf('#') != -1 ) {
        var sectionID = document.location.href.substr(document.location.href.indexOf('#') + 1);
        console.log("sectionID [" + sectionID + "]");
        $.scrollTo("#" + sectionID, {offset: -160});
        console.log("scrollTo [" + sectionID + "]");
    }

});



