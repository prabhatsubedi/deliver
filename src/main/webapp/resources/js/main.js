/**
* Created by Lunek on 11/25/2014.
*/

if(typeof(Main) == "undefined") var Main = {};

var form_submit = true;
$(window).bind('beforeunload', function() { if(!form_submit) return 'Your data will not be saved. Are you sure to continue?'; });

(function ($){

    Main.saveInSessionStorage = function (key, value){
        sessionStorage.setItem(key, value);
    }

    Main.getFromSessionStorage = function (key){
        return sessionStorage.getItem(key);
    }

    Main.clearSessionStorage = function (key){
        if(key == undefined)
            sessionStorage.clear();
        else
            sessionStorage.removeItem(key);
    }

    Main.saveInLocalStorage = function (key, value){
        localStorage.setItem(key, value);
    }

    Main.getFromLocalStorage = function (key){
        return localStorage.getItem(key);
    }

    Main.clearLocalStorage = function (key){
        if(key == undefined)
            localStorage.clear();
        else
            localStorage.removeItem(key);
    }

    Main.country = "Nepal";
    Main.docHeight = window.innerHeight;
    Main.docWidth = window.innerWidth;
    Main.ajax = undefined;
    Main.pageContext = document.getElementById('pageContext').getAttribute('data-context');
    Main.userRole = Main.getFromLocalStorage('userRole');

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
                Main.saveInLocalStorage('userRole', data.params.userDetails.authorities[0].authority)
                Main.saveInLocalStorage('userTitle', data.params.userDetails.businessName == undefined? data.params.userDetails.fullName : data.params.userDetails.businessName)
                Main.saveInLocalStorage('profileImage', data.params.userDetails.profileImage);
                Main.saveInLocalStorage('currency', data.params.currency);
                window.location = Main.modifyURL(data.params.url);
            } else {
                alert(data.message);
            }
        };

        callback.loaderDiv = "#modal_login .login_block";

        Main.request('/j_spring_security_check', data, callback);
    };

    Main.doLogout = function (data) {
        var callback = function (status, data) {
            Main.clearLocalStorage();
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
                for (var i in merchants) {
                    sess_merchants[merchants[i].id] = {businessTitle: merchants[i].businessTitle, status: merchants[i].status};
                }
                Main.saveInLocalStorage('merchants', JSON.stringify(sess_merchants));
            }

        };

        if(sess_merchants == undefined) {
            callback.requestType = "GET";
            Main.request('/accountant/get_all_merchants', {}, callback);
        } else {
            callback('', {params: {merchants: sess_merchants}, success: true});
        }

    };

    if(Main.getFromLocalStorage('merchants') == undefined && (Main.userRole == 'ROLE_ADMIN' || Main.userRole == 'ROLE_MANAGER')) Main.saveMerchants();

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

    Main.createDataTable = function (selector, dataFilter, colindex, sortorder, hideCols){

        var headers = {};
        if(dataFilter.headers != undefined)
            headers = dataFilter.headers;

        headers['Content-Type'] = 'application/json';

        if(typeof TableTools != "undefined") {
            TableTools.DEFAULTS.sSwfPath = Main.modifyURL("/resources/js/copy_csv_xls_pdf.swf");
            TableTools.DEFAULTS.aButtons = [ "csv" ];
        }

        $.extend($.fn.dataTable.defaults, {
            sDom: 'T<"clearfix"lf><"table-responsive jscrollpane_div"t><"clearfix"ip>',
            columnDefs: [
                {
//                    targets: hideCols,
//                    visible: false,
                    bSortable: false,
                    aTargets: ['no_sort']
                }
            ],
            columns: dataFilter.columns,
            language: {
                paginate: {
                    next: '&raquo;',
                    previous: '&laquo'
                }
            },
            order: dataFilter.order == undefined ? [[ 0, 'desc' ]] : dataFilter.order,
            fnDrawCallback: typeof(colindex) == 'function' ? colindex : null,
            processing: true,
            serverSide: true,
            ajax: {
                url: Main.modifyURL(dataFilter.url),
                type: dataFilter.requestType == undefined ? "POST" : dataFilter.requestType,
                headers: headers,
                data: function(data){
                    $('body').addClass('loader_div').append('<div class="loader"></div>');
                    console.log(data);
                    var request = {}
                    var page = {};
                    if(typeof(dataFilter.params) == "object") request = dataFilter.params;
                    page.pageNumber = parseInt((data.start/data.length) + 1);
                    page.pageSize = data.length;
                    page.searchFor = data.search.value;
                    var sortColName = data.columns[data.order[0].column].name;
                    if(sortColName != "") {
                        page.sortBy = sortColName;
                        page.sortOrder = data.order[0].dir;
                    }
                    request.page = page;
                    return JSON.stringify(request);
                },
                dataFilter : function(data, type) {
                    $('body').removeClass('loader_div').children('.loader').hide().remove();
                    var jsonData = dataFilter(JSON.parse(data), type);
                    console.log(jsonData);
                    return JSON.stringify(jsonData);
                }
            }
        });

        if($.fn.dataTable.fnIsDataTable(selector))
            $(selector).DataTable().clear().destroy();
        $(selector).dataTable();

/*
            selector = "#merchants_table";
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
            },
            fnDrawCallback: typeof(colindex) == 'function' ? colindex : null
        });
        dataTable = $(selector).dataTable();
        dataTable.fnClearTable();
        if(data.length > 0) {
            dataTable.fnAddData(data);
            if(colindex != undefined && sortorder != undefined) dataTable.fnSort( [ [colindex, sortorder] ] );
        }
        dataTable.fnDraw();*/
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
    };

    Main.getURLvalue = function(index) {
        if(index == undefined) return false;

        var pathname = window.location.pathname;
        var path_arr = pathname.split('/');
        path_arr = Main.remove_value(path_arr, "");
        if(Main.pageContext != "" && Main.pageContext !=  undefined) path_arr.splice(0, 1);
        return path_arr[index];

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
        if(Main.getFromLocalStorage('menu') == 'closed' || Main.docWidth <= 1024) {
            $('body').removeClass('menu_opened').addClass('menu_closed');
        } else {
            $('body').removeClass('menu_closed').addClass('menu_opened');
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
            if($('#sidebar_menu').is(':animated')) return false;
            var target_menu = $($(this).attr('data-menu'));
            var target_body = $($(this).attr('data-body'));
            if($('body').hasClass('menu_opened')) {
                if(Main.docWidth <= 1024) {
                    $('#menu_overlay').fadeOut();
                }
                $('.menu_text', target_menu).fadeOut(100);
                $(target_body).animate({marginLeft: close_width});
                $('.sidebar_logo', target_menu).animate({paddingTop: close_logo_pad, paddingBottom: close_logo_pad, width: 22});
                $('.sidebar_menu a', target_menu).animate({paddingLeft: close_menu_link_pad_left});
                $('.sidebar_menu li li', target_menu).animate({paddingLeft: 0});
                $('.sidebar_inner', target_menu).animate({paddingLeft: close_menu_pad, paddingRight: close_menu_pad});
                $(target_menu).animate({width: close_width, padding: close_menu_pad}, function(){
                    $('body').removeClass('menu_opened').addClass('menu_closed');
                    Main.saveInLocalStorage('menu', 'closed');
                    $(window).trigger('resize');
                });
            } else {
                if(Main.docWidth <= 1024) {
                    $('#menu_overlay').fadeIn();
                }
                $(target_body).animate({marginLeft: open_width});
                $('.sidebar_logo', target_menu).css('width', '100%');
                $('.sidebar_logo', target_menu).animate({paddingTop: open_logo_pad, paddingBottom: open_logo_pad});
                $('.sidebar_menu a', target_menu).animate({paddingLeft: open_menu_link_pad_left});
                $('.sidebar_menu li li', target_menu).animate({paddingLeft: open_menu_link_pad_left});
                $('.sidebar_inner', target_menu).animate({paddingLeft: open_menu_pad, paddingRight: open_menu_pad});
                $(target_menu).animate({width: open_width, padding: open_menu_pad}, function(){
                    $('body').removeClass('menu_closed').addClass('menu_opened');
                    Main.saveInLocalStorage('menu', 'opened');
                    $('.menu_text', target_menu).fadeIn(100);
                    $(window).trigger('resize');
                });
            }
        });

        $('#menu_overlay').click(function(){
            if($('body').hasClass('menu_opened') && !$('#sidebar_menu').is(':animated')) {
                $('.menu_toggle').trigger('click');
            }
        });
    };

    Main.resizableCatMenu = function(){

        $('.cat_resize_controller').css({left : $('.categories_container').width() - 5}).removeClass('hidden');
        var minWidth = parseInt($('.categories_container').css('min-width'));
        var maxWidth = parseInt($('.categories_container').css('max-width'));
        $('.cat_resize_controller').draggable({
            axis: "x",
            drag: function( event, ui ) {
                var posLeft = ui.position.left;
                if(minWidth > posLeft || maxWidth < posLeft ) return false;
                $('.categories_container').width(posLeft + 5);
            }
        });

    };

    Main.convertMin = function(valueMin) {

        var min = valueMin % 60;
        var hour = ((valueMin - min) / 60) % 24;
        var days = ((valueMin - (min + hour * 60)) / 60) / 24;
        var time = [];
        if(days > 0) time.push(days + "d");
        if(hour > 0) time.push(hour + "h");
        if(min > 0) time.push(min + "m");
        return time.join(" ");

    }

    Main.popDialog = function (title, content, buttons) {

/*      usage
        var button1 = function() {
            alert('you clicked button 1');
        };
        button1.text = "Test Button1";
        var button2 = function() {
            alert('you clicked button 2');
        };
        button2.text = "Test Button2";
        var buttons = [button1, button2];
        Main.popDialog("Test Title", "Test content", buttons);
        */

        if(!buttons) buttons = [];
        if($('#popDialog').length == 0) {
            $('body').append('\
                <div class="modal fade" id="popDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">\
                    <div class="modal-dialog">\
                        <div class="modal-content">\
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close" style="position: absolute; right: 15px; top: 12px; z-index: 1;"><span aria-hidden="true">&times;</span></button>\
                            <div class="modal-header">\
                                <h4 class="modal-title" id="myModalLabel"></h4>\
                            </div>\
                            <div class="modal-body">\
                            </div>\
                            <div class="modal-footer">\
                            </div>\
                        </div>\
                    </div>\
                </div>');
        }

        window.dialogButtons = buttons;
        var popElem = $('#popDialog');
        var buttonsElem = '';
        for(var i = 0; i < buttons.length; i++) {
            var button = '<button class="btn btn-primary" type="button">' + (typeof buttons[i] == 'string' ? buttons[i] : buttons[i].text) + '</button>';
            buttonsElem += button;
        }
        if(title == '')
            $('.modal-header', popElem).addClass('hidden').children('.modal-title').html('');
        else
            $('.modal-header', popElem).removeClass('hidden').children('.modal-title').html(title);

        $('.modal-body', popElem).html(content);

        if(buttons.length == 0)
            $('.modal-footer', popElem).addClass('hidden').html('');
        else
            $('.modal-footer', popElem).removeClass('hidden').html(buttonsElem);
        popElem.modal('show');
    };

})(jQuery);

$(document).ready(function(){
    var preventCustomTab = false;
    $(document).keyup(function(e){
        var target = $(e.target);
        if(e.keyCode == 9 && target.hasClass('selectpicker') && !preventCustomTab) {
            target.attr('aria-expanded', true);
            target.parent('.bootstrap-select').addClass('open');
        }
    });
    $(document).keydown(function(e){
        if(e.keyCode == 9 && $(':focus').hasClass('selectpicker')) {
            preventCustomTab = true;
        } else {
            preventCustomTab = false;
        }
    });

    Main.checkURL();
    Main.elemRatio();
    Main.fullHeight();
    Main.toggleMainMenu();
    $('.elem_tooltip').tooltip();

    var sidebar_hammer = document.getElementById('sidebar_menu');
    var overlay_hammer = document.getElementById('menu_overlay');
    if(sidebar_hammer != undefined) {
        var hammer_sidebar = new Hammer(sidebar_hammer);
        var hammer_overlay = new Hammer(overlay_hammer);
        hammer_sidebar.on('swiperight', function() {
            if($('body').hasClass('menu_closed'))
                $('.menu_toggle').trigger('click');
        });
        hammer_sidebar.on('swipeleft', function() {
            if($('body').hasClass('menu_opened'))
                $('.menu_toggle').trigger('click');
        });
        hammer_overlay.on('swipeleft', function() {
            if($('body').hasClass('menu_opened'))
                $('.menu_toggle').trigger('click');
        });
    }
    /*
    var hammertime = new Hammer(myElement, myOptions);
    hammertime.on('pan', function(ev) {
        console.log(ev);
    });*/

    $('#popDialog .modal-footer button').live('click', function() {
        var btnFn = window.dialogButtons[$(this).index()];
        if(typeof btnFn == 'function') btnFn();
        $('#popDialog').modal('hide');
    });

});
$(window).resize(function(){
    Main.elemRatio();
    Main.docWidth = window.innerWidth;
});