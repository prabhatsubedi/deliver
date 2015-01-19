/**
 * Created by Lunek on 11/25/2014.
 */

if(typeof(Main) == "undefined") var Main = {};

(function ($){

    Main.country = "Nepal";
    Main.docHeight = window.innerHeight;
    Main.ajax = undefined;
    Main.subFolder = "";

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

        var hideLoader = function(){};
        if(callback != undefined) {
            var loaderDiv = callback.async == false ? 'body' : callback["loaderDiv"];
            var requestType = callback["requestType"];
            if (loaderDiv != undefined && $('.loader', loaderDiv).length == 0) {
                $(loaderDiv).addClass('loader_div').append('<div class="loader"></div>');
            }

            hideLoader = function () {
                if (loaderDiv != undefined) {
                    $(loaderDiv).removeClass('loader_div').children('.loader').hide().remove();
                }
            }
        }

        Main.ajax = $.ajax({
            url: url,
            type: requestType != undefined ? requestType : "POST",
            data: parameter.stringify == false ? parameter : JSON.stringify(parameter),
            headers: headers,
            async: callback != undefined && callback.async == false ? false : true,
            statusCode: {
            },
            success: function (data) {
                if (callback != undefined) return callback("success", data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (callback != undefined && errorThrown != "abort") return callback("error", {success: false, message: XMLHttpRequest.getResponseHeader("errorMessage"), code: XMLHttpRequest.getResponseHeader("errorCode")});
            },
            complete: function() {
                setTimeout(hideLoader, 1000)
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

    Main.changePassword = function(headers) {

        $("button[type='submit']").attr("disabled",true);

        var callback = function (status, data) {
            $("button[type='submit']").removeAttr("disabled");

            if (data.success == true) {
                alert(data.message);
                $('#modal_password').modal('hide');
            } else {
                alert(data.message);
            }
        };

        callback.loaderDiv = "#modal_password .modal-content";

        Main.request('/anon/change_password', {}, callback, headers);

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

    Main.remove_value = function(value, remove) {
        if(value.indexOf(remove) > -1) {
            value.splice(value.indexOf(remove), 1);
            Main.remove_value(value, remove);
        }
        return value;
    }

    Main.getURLvalue = function(index) {
        if(index == undefined) return false;

        var pathname = window.location.pathname;
        var path_arr = pathname.split('/');
        path_arr = Main.remove_value(path_arr, "");
        if(path_arr[0] == Main.subFolder) path_arr.splice(0, 1);
        return path_arr[index];

    };

    Main.modifyURL = function(url) {
        if(Main.subFolder != "" && Main.subFolder != undefined)
            return '/' + Main.subFolder + url;
        return url;
    }

/*    Main.getURLParameter = function(key, keyvalue) {
        if(key == undefined) return false;
        key = key + "_";

        var pathname = window.location.pathname;
        var path_arr = pathname.split('/');
        path_arr = Main.remove_value(path_arr, "");
        var idm_param = undefined;
        for(var i = 0; i < path_arr.length; i++) {
            if(/idm_/g.test(path_arr[i])) {
                idm_param = path_arr[i];
                break;
            }
        }
        if(keyvalue)
            return idm_param;
        else
            return idm_param.replace(key, '');

    };

    Main.updateLinks = function(parameter, target, callback) {

        var param = Main.getURLParameter(parameter, true);
        if(param != undefined) {
            $(target).each(function(){
                var href = $(this).attr('href');
                $(this).attr('href', href + (href.lastIndexOf('/') == href.length - 1 ? "" : "/") + param);
            });
            if(callback != undefined) callback();
        }

    };*/

    Main.elemRatio = function(callback) {
        function elem_ratio() {
            $('.maintain_ratio').each(function(){
                var mthis = $(this);
                mthis.height((mthis.attr('mr-height')/mthis.attr('mr-width'))*mthis.width());
            });
            if(callback != undefined) callback();
        }
        $(window).resize(function(){
            elem_ratio();
        });
        setTimeout(elem_ratio, 500);
    };

    Main.fullHeight = function() {
        $('.full_height').each(function() {
            var offsetTop = $(this).offset().top;
            $(this).height(Main.docHeight - offsetTop);
        });
    };

})(jQuery);

$(document).ready(function(){
    Main.elemRatio();
    Main.fullHeight();
});