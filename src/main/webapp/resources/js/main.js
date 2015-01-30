/**
* Created by Lunek on 11/25/2014.
*/

if(typeof(Main) == "undefined") var Main = {};

var form_submit = true;
$(window).bind('beforeunload', function() { if(!form_submit) return 'Your data will not be saved. Are you sure to continue?'; });

(function ($){

    Main.country = "Nepal";
    Main.docHeight = window.innerHeight;
    Main.ajax = undefined;
    Main.pageContext = document.getElementById('pageContext').getAttribute('data-context');

    Main.modifyURL = function(url) {
        if(url.indexOf("#") != 0) {
            if(url.indexOf("/") != 0) url = "/" + url;
            if(Main.pageContext != "" && Main.pageContext != undefined) {
                if(url.indexOf(Main.pageContext) > -1)
                    return url;
                else
                    return Main.pageContext + url;
            }
            return url;
        }
    };

    Main.checkURL = function() {
        $('a[href]').not('[href="#"], [href="javascript:;"]').each(function(){
            if($(this).attr('href').indexOf(Main.pageContext) < 0)
                $(this).attr('href', Main.modifyURL($(this).attr('href')));
        });
    };

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
            url: Main.modifyURL(url),
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
                window.location = Main.modifyURL(data.params.url);
            } else {
                alert(data.message);
            }
        };

        callback.loaderDiv = "#modal_login .modal-dialog";

        Main.request('/j_spring_security_check', data, callback);
    };

    Main.doLogout = function (data) {
        var callback = function (status, data) {
            window.location = Main.modifyURL("/");
        };
        callback.loaderDiv = "body";
        Main.request('/j_spring_security_logout', {}, callback);
    };

    Main.saveMerchants = function(sess_merchants) {

        var callback = function (status, data) {

            if (data.success) {
                var merchants = data.params.merchants;

                var sess_merchants = {};
                for (i = 0; i < merchants.length; i++) {
                    sess_merchants[merchants[i].id] = merchants[i].businessTitle;
                }
                Main.saveInLocalStorage('merchants', JSON.stringify(sess_merchants));
            }

        };

        if(sess_merchants == undefined) {
            callback.requestType = "GET";
            Main.request('/organizer/get_merchants', {}, callback);
        } else {
            callback('', {params: {merchants: sess_merchants}});
        }

    };
    if(Main.getFromLocalStorage('merchants') == undefined) Main.saveMerchants();

    Main.assistance = function(data, headers) {

        $("button[type='submit']").attr("disabled",true);

        var callback = function (status, data) {
            $("button[type='submit']").removeAttr("disabled");

            if (data.success == true) {
                alert(data.message);
                window.location = Main.modifyURL("/");
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
        if(Main.pageContext != "" && Main.pageContext !=  undefined) path_arr.splice(0, 1);
        return path_arr[index];

    };

    Main.getAllStores = function() {

        var callback = function (status, data) {

            if (data.success == true) {
                var brands = data.params.brands;
                if(brands.length > 0) {
                    var store_options = '';
                    for(var i = 0; i < brands.length; i++) {
                        store_options += '<option value="' + brands[i].id + '">' + brands[i].brandName + '</option>';
                    }
                    $('#item_stores').append(store_options);
                    $('#item_stores').selectpicker('refresh');
                }
            } else {
            }
        };
        callback.requestType = "GET";

        Main.request('/merchant/get_brands', {}, callback);

    };

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
        setTimeout(elem_ratio, 500);
    };

    Main.fullHeight = function() {
        $('.full_height').each(function() {
            var offsetTop = $(this).offset().top;
            $(this).height(Main.docHeight - offsetTop);
        });
    };

    Main.toggleMainMenu = function(){
        if(Main.getFromLocalStorage('menu') == 'closed') {
            $('#sidebar_menu, .main_container').removeClass('menu_opened').addClass('menu_closed');
        } else {
            $('#sidebar_menu, .main_container').removeClass('menu_closed').addClass('menu_opened');
        }
        var open_width = 320;
        var close_width = 40;
        var open_menu_pad = 30;
        var close_menu_pad = 10;
        var open_logo_pad = 50;
        var close_logo_pad = 10;
        var open_menu_link_pad_left = 15;
        var close_menu_link_pad_left = 5;
        $('.menu_toggle').click(function(){
            if($(this).is(':animated')) return false;
            var target_menu = $($(this).attr('data-menu'));
            var target_body = $($(this).attr('data-body'));
            if(target_menu.hasClass('menu_opened')) {
                $('.menu_text', target_menu).fadeOut(100);
                $(target_body).animate({marginLeft: close_width});
                $('.sidebar_logo', target_menu).animate({paddingTop: close_logo_pad, paddingBottom: close_logo_pad});
                $('.sidebar_menu a', target_menu).animate({paddingLeft: close_menu_link_pad_left});
                $('.sidebar_menu li li', target_menu).animate({paddingLeft: 0});
                $(target_menu).animate({width: close_width, padding: close_menu_pad}, function(){
                    $(this).removeClass('menu_opened').addClass('menu_closed');
                    Main.saveInLocalStorage('menu', 'closed');
                });
            } else {
                $('.menu_text', target_menu).fadeIn(100);
                $(target_body).animate({marginLeft: open_width});
                $('.sidebar_logo', target_menu).animate({paddingTop: open_logo_pad, paddingBottom: open_logo_pad});
                $('.sidebar_menu a', target_menu).animate({paddingLeft: open_menu_link_pad_left});
                $('.sidebar_menu li li', target_menu).animate({paddingLeft: open_menu_link_pad_left});
                $(target_menu).animate({width: open_width, padding: open_menu_pad}, function(){
                    $(this).removeClass('menu_closed').addClass('menu_opened');
                    Main.saveInLocalStorage('menu', 'opened');
                });
            }
        });
    };

})(jQuery);

$(document).ready(function(){
    Main.checkURL();
    Main.elemRatio();
    Main.fullHeight();
    Main.toggleMainMenu();
    $('.elem_tooltip').tooltip();
});
$(window).resize(function(){
    Main.elemRatio();
});