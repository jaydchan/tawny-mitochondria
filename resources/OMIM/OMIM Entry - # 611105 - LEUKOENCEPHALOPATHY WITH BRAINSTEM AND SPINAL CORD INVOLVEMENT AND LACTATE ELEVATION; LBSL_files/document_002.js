////////////////////////////////////////////////
//
// Document Functions
//



$(document).ready(function(){


    ////////////////////////////////////////////////
    //
    // MIM Match Notification
    //


    // Get the csrftoken cookie
    var csrftoken = $.cookie('csrftoken');
console.log("from cookie - csrftoken [" + csrftoken + "]");


    // These HTTP methods do not require CSRF protection
    function csrfSafeMethod(method) {
        return (/^(GET|HEAD|OPTIONS|TRACE)$/.test(method));
    }

    // Set up the ajax call, add 'X-CSRFToken' cookie to the headers where needed
    $.ajaxSetup({
        crossDomain: false, // obviates need for sameOrigin test
        beforeSend: function(xhr, settings) {
            if (!csrfSafeMethod(settings.type)) {
                xhr.setRequestHeader("X-CSRFToken", csrftoken);
console.log("added as X-CSRFToken - csrftoken [" + csrftoken + "]");
            }
        }
    });



    // Entry update checkbox
    $(".entryUpdate").click(function(){
console.log("entryUpdate " + $(this).val() + " " + $(this).prop("checked"));
        var url = "/ajax/mimmatch/settings/entry";
        var data = "mimNumber=" + $(this).val() + "&entryUpdate=" + $(this).prop("checked");
        $.post(url, data, function(xml) {
//             console.log("entryUpdate - ok");
        })
        .error(function(xml) {
            $(this).attr("checked", !$(this).prop("checked"));
            alert("Failed to set the 'notify on update' flag for " + $(this).val() + ".");
        });
    });



    // Share my interest checkbox
    $(".entryShareInterest").click(function(){
console.log("entryShareInterest " + $(this).val() + " " + $(this).prop("checked"));
        var url = "/ajax/mimmatch/settings/entry";
        var data = "mimNumber=" + $(this).val() + "&entryShareInterest=" + $(this).prop("checked");
        $.post(url, data, function(xml) {
//             console.log("entryShareInterest - ok");
        })
        .error(function(xml) {
            $(this).attr("checked", !$(this).prop("checked"));
            alert("Failed to set the 'share my interest' flag for " + $(this).val() + ".");
        });
    });



    // Notify me on phenotypic series update checkbox
    $(".phenotypicSeriesUpdate").click(function(){
console.log("phenotypicSeriesUpdate " + $(this).val() + " " + $(this).prop("checked"));
        var url = "/ajax/mimmatch/settings/entry";
        var data = "mimNumber=" + $(this).val() + "&phenotypicSeriesUpdate=" + $(this).prop("checked");
        $.post(url, data, function(xml) {
//             console.log("phenotypicSeriesUpdate - ok");
        })
        .error(function(xml) {
            $(this).attr("checked", !$(this).prop("checked"));
            alert("Failed to set the 'Notify me on phenotypic series update' flag for " + $(this).val() + ".");
        });
    });



    // Notify on update global checkbox
    $("#entryUpdateGlobal").click(function(){
console.log("entryUpdateGlobal");

        if ( $(this).prop("checked") ) {

            var mimNumberArray = [];
            $(".entryUpdate").each(function(mimNumbers) {
                if ( !$(this).prop("checked") ) {
                    mimNumberArray.push($(this).val());
                }
            });
            
            if ( mimNumberArray.length == 0 ) {
                return;
            }
console.log("mimNumberArray "  + mimNumberArray);

            var url = "/ajax/mimmatch/settings/entry";
            var data = "mimNumber=" + mimNumberArray.join() + "&entryUpdate=true";

            $.post(url, data, function(xml) {
                $(".entryUpdate").each(function(mimNumbers) {
                    $(this).prop("checked", true);
                });
                $("#entryUpdateGlobalLabel").html(' Uncheck all Notifications ');
            })
            .error(function(xml) {
                $(this).prop("checked", false);
                alert("Failed to set the 'notify me on update' flags.");
            });
            
        }
        else {
            
            var mimNumberArray = [];
            $(".entryUpdate").each(function(mimNumbers) {
                if ( $(this).prop("checked") ) {
                    mimNumberArray.push($(this).val());
                }
            });
        
            if ( mimNumberArray.length == 0 ) {
                return;
            }

console.log("mimNumberArray "  + mimNumberArray);

            var url = "/ajax/mimmatch/settings/entry";
            var data = "mimNumber=" + mimNumberArray.join() + "&entryUpdate=false";

            $.post(url, data, function(xml) {
                $(".entryUpdate").each(function(mimNumbers) {
                    $(this).prop("checked", false);
                });
                $("#entryUpdateGlobalLabel").html(' Check all Notifications ');
            })
            .error(function(xml) {
                $(this).prop("checked", true);
                alert("Failed to clear the 'notify me on update' flag.");
            });
        
        }
    });




    // Share my interest global checkbox
    $("#entryShareInterestGlobal").click(function(){
console.log("entryShareInterestGlobal");

        if ( $(this).attr("checked") == "checked" ) {

            var mimNumberArray = [];
            $(".entryShareInterest").each(function(mimNumbers) {
                if ( !$(this).prop("checked") ) {
                    mimNumberArray.push($(this).val());
                }
            });
            
            if ( mimNumberArray.length == 0 ) {
                return;
            }
        
console.log("mimNumberArray "  + mimNumberArray);

            var url = "/ajax/mimmatch/settings/entry";
            var data = "mimNumber=" + mimNumberArray.join() + "&entryShareInterest=true";

            $.post(url, data, function(xml) {
                $(".entryShareInterest").each(function(mimNumbers) {
                    $(this).prop("checked", true);
                });
                $("#entryShareInterestGlobalLabel").html(' Uncheck all Shares ');
            })
            .error(function(xml) {
                $(this).prop("checked", false);
                alert("Failed to set the 'share my interest' flags.");
            });
            
        }
        else {
            
            var mimNumberArray = [];
            $(".entryShareInterest").each(function(mimNumbers) {
                if ( $(this).prop("checked") ) {
                    mimNumberArray.push($(this).val());
                }
            });
        
            if ( mimNumberArray.length == 0 ) {
                return;
            }

console.log("mimNumberArray "  + mimNumberArray);

            var url = "/ajax/mimmatch/settings/entry";
            var data = "mimNumber=" + mimNumberArray.join() + "&entryShareInterest=false";

            $.post(url, data, function(xml) {
                $(".entryShareInterest").each(function(mimNumbers) {
                    $(this).attr("checked", false);
                });
                $("#entryShareInterestGlobalLabel").html(' Check all Shares ');
            })
            .error(function(xml) {
                $(this).prop("checked", true);
                alert("Failed to clear the 'share my interest' flags.");
            });

        }
    });



});
