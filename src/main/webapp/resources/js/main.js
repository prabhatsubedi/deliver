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
        var loaderDiv = callback["loaderDiv"];
        if (loaderDiv != undefined) {
            $(loaderDiv).addClass('loader_div').append('<div class="loader"></div>');
        }

        function hideLoader() {
            if (loaderDiv != undefined) {
                $(loaderDiv).removeClass('loader_div').children('.loader').hide();
            }
        }

        $.ajax({
            url: url,
            type: "POST",
            data: parameter,
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
                return callback("error", {success: false, message: errorThrown});
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
            }
        };

        callback.loaderDiv = "#modal_login .modal-dialog";

        Main.request('/j_spring_security_check', data, callback);
    }

})(jQuery);