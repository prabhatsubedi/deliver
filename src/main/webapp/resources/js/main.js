/**
 * Created by Lunek on 11/25/2014.
 */

if(typeof(Main) == "undefined") var Main = {};

(function ($){

    Main.country = "Nepal";

    Main.saveInSessionStorage = function (key, value){
        sessionStorage.setItem(key, value);
    }

    Main.getFromSessionStorage = function (key){
        return sessionStorage.getItem(key);
    }

    Main.saveInLocalStorage = function (key, value){
        localStorage.setItem(key, value);
    }

    Main.getFromLocalStorage = function (key){
        return localStorage.getItem(key);
    }

    Main.request = function (url, parameter, callback, headers) {

        if(headers == undefined) var headers = {};

        if(parameter.stringify != false)
            headers['Content-Type'] = 'application/json';

        var loaderDiv = callback["loaderDiv"];
        var requestType = callback["requestType"];
        if (loaderDiv != undefined) {
            if (loaderDiv == "body") {
                $(loaderDiv).append('<div class="loader"></div>');
            } else {
                $(loaderDiv).addClass('loader_div').append('<div class="loader"></div>');
            }
        }

        function hideLoader() {
            if (loaderDiv != undefined) {
                if (loaderDiv == "body") {
                    $(loaderDiv).children('.loader').hide();
                } else {
                    $(loaderDiv).removeClass('loader_div').children('.loader').hide();
                }
            }
        }

        $.ajax({
            url: url,
            type: requestType != undefined ? requestType : "POST",
            data: parameter.stringify == false ? parameter : JSON.stringify(parameter),
            headers: headers,
            statusCode: {
            },
            success: function (data) {
//                setTimeout(function (){
                hideLoader();
                return callback("success", data);
//                }, 3000);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                hideLoader();
                return callback("error", {success: false, message: XMLHttpRequest.getResponseHeader("errorMessage")});
            }
        });
    };

    Main.doLogin = function (data) {
        $("button[type='submit']","#login_form").attr("disabled",true);

        var callback = function (status, data) {
            $("button[type='submit']","#login_form").removeAttr("disabled");

            if (data.success == true) {
                window.location.replace(data.params.url);
            } else {
                alert(data.message);
            }
        };

        callback.loaderDiv = "#modal_login .modal-dialog";

        Main.request('/j_spring_security_check', data, callback);
    };

    Main.doLogout = function (data) {
        var callback = function (status, data) {
            window.location = "/";
        };
        callback.loaderDiv = "body";
        Main.request('/j_spring_security_logout', {}, callback);
    };

    Main.assistance = function(data, headers) {

        $("button[type='submit']").attr("disabled",true);

        var callback = function (status, data) {
            $("button[type='submit']").removeAttr("disabled");

            if (data.success == true) {
                alert(data.message);
                window.location = "/";
            } else {
                alert(data.message);
            }
        };

        callback.loaderDiv = ".assist_containers";

        Main.request('/anon/password_assist', data, callback, headers);

    };

    Main.createDataTable = function (selector, data, colindex, sortorder, hideCols){
        $.extend($.fn.dataTable.defaults, {
            sDom: '<"clearfix"lf><"table-responsive jscrollpane_div"t><"clearfix"ip>',
            columnDefs: [
                {
                    targets: hideCols,
                    visible: false
                }
            ],
            language: {
                paginate: {
                    next: '&raquo;',
                    previous: '&laquo'
                }
            }
        });
        dataTable = $(selector).dataTable();
        dataTable.fnClearTable();
        if(data.length > 0) {
            dataTable.fnAddData(data);
            if(colindex != undefined && sortorder != undefined) dataTable.fnSort( [ [colindex, sortorder] ] );
        }
        dataTable.fnDraw();
    }

    Main.ucfirst = function(word){
        return word.substr(0, 1).toUpperCase() + word.substr(1).toLowerCase();
    };

})(jQuery);