////////////////////////////////////////////////
//
// Lookup Functions
//


$(document).ready(function(){



    ////////////////////////////////////////////////
    //
    // Lookup
    //


    // Saved lookup ID and html
    var savedLookupID = null;
    var savedLookupHtml = null;

    // Lookup class
    //
    // http://stackoverflow.com/questions/5421892/getselection-not-working-in-ie
    // http://www.quirksmode.org/dom/range_intro.html
    //
    $(".lookup").mouseup(function(event){

        // Do nothing for MSIE 8.0 and less
        if ( $.browser.msie && ($.browser.version < 9.0) ) {
           return;
        }

    
        // Get the target ID
        var id = $(event.target).attr("id");

//         console.log("id: [" + id + ", " + $(event.target).attr("text") + "]");

        // Do nothing if we are clicking on the lookup icon
        if ( id == 'lookup' ) {
            return;
        }


        // And destroy the idle timer
        $("#lookup").idleTimer('destroy');

        // Webkit (safari & chrome)
        if ( $.browser.webkit ) {

            // Restore the saved lookup html 
            if ( savedLookupID ) {
// console.log("savedLookupID: [" + savedLookupID + "]");
                $("#" + savedLookupID).html(savedLookupHtml);
                if ( savedLookupID == 'lookupParent' ) {
                    $("#" + savedLookupID).attr("id", null);
                }
                savedLookupID = null;
                savedLookupHtml = null;
            }
        }
        
        // MSIE, Mozilla, Opera?
        else {

            // Remove current lookup span
            $("#lookup").remove();
        }

        
        // Get the selection
        var selection = window.getSelection();
// console.log("selection: [" + selection.toString() + "]");


        // Process actual selection
        if ( !selection.isCollapsed ) {    

            // Get the range
            var range = selection.getRangeAt(0); 
// console.log("range: [" + range.toString() + "]");


            // Webkit (safari & chrome)
            if ( $.browser.webkit ) {

                // Save the lookup html
                var myParentElement = $(range.startContainer).parent();

                if ( !myParentElement.attr("id") ) {
                    myParentElement.attr("id", "lookupParent");
                }
                savedLookupID = myParentElement.attr("id");
                savedLookupHtml = myParentElement.html();
// console.log("savedLookupID: [" + savedLookupID + "]");
// console.log("savedLookupHtml: [" + savedLookupHtml + "]");
            }


            // Create a new span
            var span = document.createElement("span");
            span.id = "lookup";
            span.style.margin = "-20px 0 0 20px";
            span.style.position = "absolute";
            span.style.background = "url(/static/icons/tooltip-define.gif)";
            span.style.width = "46px";
            span.style.height = "26px";
            span.style.cursor = "pointer";
            span.setAttribute("text", selection.toString());
             
            // MSIE
            if ( $.browser.msie ) {
                span.style.setProperty("_background-image", "none", "important");
                span.style.setProperty("filter", 'progid:DXImageTransform.Microsoft.AlphaImageLoader(src="/static/icons/tooltip-define.gif", sizingMethod="image")', "important");
            }
           
            // Insert the span in the range
            range.insertNode(span);
        
            // Webkit (safari & chrome)
            if ( $.browser.webkit ) {
                // Reselect the range so it stays highlighted
                selection.addRange(range);
            }
        
            // Add the click handler to the lookup
            $("#lookup").on("click", function(event) {
//                 var url = 'http://www.merriam-webster.com/medlineplus/' + $("#lookup").attr("text");
                var url = 'http://medical-dictionary.thefreedictionary.com/' + $("#lookup").attr("text");
                window.open(url, '_blank');
            });


            // Set a 5 second timeout
            $("#lookup").idleTimer(5000);
    
            // Set the idle timer to remove the lookup when it times out
            $("#lookup").bind("idle.idleTimer", function(){
        
                // Webkit (safari & chrome)
                if ( $.browser.webkit ) {
        
                    // Restore the saved lookup html 
                    if ( savedLookupID ) {
                       $("#" + savedLookupID).html(savedLookupHtml);
                        if ( savedLookupID == 'lookupParent' ) {
                            $("#" + savedLookupID).attr("id", null);
                        }
                        savedLookupID = null;
                        savedLookupHtml = null;
                    }
                }
                
                // MSIE, Mozilla, Opera?
                else {
        
                    // Remove current lookup span
                    $("#lookup").remove();
        
                }
 
                // And destroy the idle timer
                $("#lookup").idleTimer('destroy');
       
            });

        }

    });






});
