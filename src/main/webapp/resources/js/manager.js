/**
 * Created by Lunek on 12/3/2014.
 */


if (typeof(Manager) == "undefined") var Manager = {};

(function ($) {

    Manager.getCustomers = function () {

        var dataFilter = function (data, type) {

            console.log(data);
            if (!data.success) {
                alert(data.message);
                return;
            }
            var responseRows = data.params.users.numberOfRows;
            var users = data.params.users.data;
            var tdata = [];

            for (i = 0; i < users.length; i++) {
                var user = users[i];

                var id = user.id;
                var fullName = user.fullName;
                var emailAddress = !user.emailAddress ? '' : user.emailAddress;
                var mobileNumber = !user.mobileNumber ? '' : user.mobileNumber;
                var inactivatedCount = !user.inactivatedCount ? '0' : user.inactivatedCount;

                var row = [id, fullName, emailAddress, mobileNumber, inactivatedCount, '<a class="trigger_activation" href="#" data-id="' + id + '" >Activate</a>'];
                row = $.extend({}, row);
                tdata.push(row);
            }

            var response = {};
            response.data = tdata;
            response.recordsTotal = responseRows;
            response.recordsFiltered = responseRows;

            return response;

        };

        dataFilter.url = "/organizer/deactivated_customers";
        dataFilter.columns = [
            { "name": "id" },
            { "name": "fullName" },
            { "name": "emailAddress" },
            { "name": "mobileNumber" },
            { "name": "inactivatedCount" },
            { "name": "" }
        ];
        Main.createDataTable("#customers_table", dataFilter);

        $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

    };

    Manager.loadActivateCustomers = function() {

        var callback = function (status, data) {

            alert(data.message);
            if (data.success == true) {
                Manager.getCustomers();
            }
        };

        $('.trigger_activation').live('click', function(){

            var chk_confirm = confirm('Are you sure you want to activate this customer?');
            if (!chk_confirm) return false;
            callback.loaderDiv = "body";
            Main.request('/organizer/activate_user', {}, callback, {id: $(this).attr('data-id')});

        });

    };

    Manager.getMerchants = function () {

        var dataFilter = function (data, type) {

            console.log(data);
            if (!data.success) {
                alert(data.message);
                return;
            }
            var responseRows = data.params.merchants.numberOfRows;
            var merchants = data.params.merchants.data;
            var tdata = [];

            for (i = 0; i < merchants.length; i++) {
                var merchant = merchants[i];

                var merchantId = merchant.id;
                var userId = merchant.user.id;
                var status = merchant.user.status;
                if(!status) status = 'INACTIVE';
                var link_activation = "", link_profile = "";

                if (status == "VERIFIED" || status == "UNVERIFIED") {
                    link_activation = '<a href="#" data-id="' + merchantId + '"  data-status="' + status + '"  data-toggle="modal" data-target="#modal_activation">Activate</a>';
                } else if (status == "ACTIVE") {
                    link_activation = '<a class="trigger_activation" href="#" data-id="' + userId + '"  data-status="INACTIVE" >Deactivate</a>';
                } else if (status == "INACTIVE") {
                    link_activation = '<a class="trigger_activation" href="#" data-id="' + userId + '" data-status="ACTIVE" >Activate</a>';
                }
                link_profile = '<a href="' + Main.modifyURL('/merchant/profile/' + merchantId) + '">Profile</a>';
                var action = '<div class="action_links">' + link_profile + link_activation + "</div>";
                var link_merchant = '<a href="' + Main.modifyURL('/merchant/store/list/' + merchantId) + '">' + merchant.businessTitle + '</a>';

                var row = [merchantId, link_merchant, merchant.partnershipStatus ? 'Partner' : 'Non Partner', merchant.user.fullName, merchant.user.emailAddress, merchant.user.mobileNumber, Main.ucfirst(status), action];
                row = $.extend({}, row);
                tdata.push(row);
            }

            var response = {};
            response.data = tdata;
            response.recordsTotal = responseRows;
            response.recordsFiltered = responseRows;

            return response;

        };

        dataFilter.url = "/accountant/get_merchants";
        dataFilter.columns = [
            { "name": "id" },
            { "name": "businessTitle" },
            { "name": "partnershipStatus" },
            { "name": "user#fullName" },
            { "name": "user#emailAddress" },
            { "name": "user#mobileNumber" },
            { "name": "user#status" },
            { "name": "" }
        ];
        Main.createDataTable("#merchants_table", dataFilter);

        $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

    };

    Manager.loadMerchantActivation = function () {

        $('#partnership').selectpicker();

        $.validator.setDefaults({
            errorPlacement: function (error, element) {
                $('#error_container').html(error);
            },
            ignore: []
        });

        $.validator.addMethod("notEqual", function (value, element, arg) {
            var result = value != arg;
            if ($(element).is('select')) {
                if (!result) {
                    $(element).siblings('.bootstrap-select').children('.form-control').addClass('error');
                } else {
                    $(element).siblings('.bootstrap-select').children('.form-control').removeClass('error');
                }
            }
            return result;
        }, "Please select any option.");

        $('#modal_activation').on('shown.bs.modal', function (e) {
            $("form", this).attr('data-id', $(e.relatedTarget).attr('data-id'));
        });

        $('#form_activation').validate({
            submitHandler: function (form) {

                if($('#commission').val() == 0 && $('#service_fee').val() == 0) {
                    alert('Both commission percent and service fee cannot be 0.');
                    return false;
                }

                var chk_confirm = confirm('Are you sure you want to activate this merchant?');
                if (!chk_confirm) return false;

                var data = {};

                data.id = $(form).attr('data-id');
                data.partnershipStatus = $('#partnership').val();
                data.commissionPercentage = $('#commission').val();
                data.serviceFee = $('#service_fee').val();

                Manager.merchantActivation(data);

                return false;

            }
        });
        $('#partnership').rules('add', {notEqual: "none"});
        $('#commission').rules('add', {required: true, number: true, min: 0, max: 100});
        $('#service_fee').rules('add', {required: true, number: true, min: 0});

        $('.trigger_activation').live('click', function () {
            var statusCheck = $(this).attr('data-status');

//            var chk_confirm = confirm('Are you sure you want to ' + (statusCheck ? "activate" : "deactivate") + ' this merchant?');
//            if (!chk_confirm) return false;
            var data = {};
            data.id = $(this).attr('data-id');
            data.status = "" + statusCheck;
            var button1 = function() {
                Manager.changeUserStatus(data);
            };

            button1.text = "Yes";
            var button2 = "No";

            var buttons = [button1, button2];
            Main.popDialog('Merchant Activation', 'Are you sure you want to ' + (statusCheck ? "activate" : "deactivate") + ' this merchant?', buttons);
        });

    };

    Manager.merchantActivation = function (data) {

        $("button[type='submit']").attr("disabled", true);

        var callback = function (status, data) {
            $("button[type='submit']").removeAttr("disabled");

            alert(data.message);
            if (data.success == true) {
                $('#modal_activation').modal('hide');
                Manager.getMerchants();
            }
        };

        callback.loaderDiv = ".modal-dialog";

        Main.request('/organizer/activate_merchant', data, callback);

    };

    Manager.changeUserStatus = function (data, fnCallback) {

        var callback = function (status, data) {
//            alert(data.message);
            Main.popDialog('Merchant Activation', data.message, ['Close']);
            if (data.success == true) {
                window.location.reload();
//                Manager.getMerchants();
            }
        };

        callback.loaderDiv = "body";
        callback.requestType = "PUT";

        Main.request('/organizer/change_user_status', data, callback);

    };

    Manager.check_store_type = function (featured_count, prioritized_count) {

        var featured_check = featured_count < 6;
        var prioritized_check = prioritized_count < 6;

        $('#featured_stores, #prioritized_stores, #other_stores').sortable('option', {connectWith: false});
        if (featured_check && prioritized_check) {
            $('#featured_stores, #prioritized_stores, #other_stores').sortable('option', {connectWith: "#featured_stores, #prioritized_stores, #other_stores"});
        } else if (featured_check && !prioritized_check) {
            $('#featured_stores, #prioritized_stores, #other_stores').sortable('option', {connectWith: "#featured_stores, #other_stores"});
        } else if (!featured_check && prioritized_check) {
            $('#featured_stores, #prioritized_stores, #other_stores').sortable('option', {connectWith: "#prioritized_stores, #other_stores"});
        } else {
            $('#featured_stores, #prioritized_stores, #other_stores').sortable('option', {connectWith: "#other_stores"});
        }

    };

    Manager.stores = function (params, prioritized) {

        if(params.pageSize == 0) delete params.pageSize;

        var url = "/organizer/";
        if (prioritized == 'inactive') {
            url += 'get_inactive_brands';
        } else if (prioritized) {
            url += 'get_special_brands';
        } else {
            url += 'get_other_brands';
        }
        var callback = function (status, data) {

            var featured_count = 0;
            var prioritized_count = 0;
            if (data.success == true) {
                var stores = data.params.storeBrands;
                if ('data' in stores) {

                    var elem_cont = prioritized == 'inactive' ? '.form_content.inactive_stores' : '.form_content.other_stores';

                    var total_stores = stores.numberOfRows;
                    var listed_stores = stores.data.length;
                    var page_number = parseInt(params.pageNumber);
                    var page_size = params.pageSize;
                    if(page_size == undefined) page_size = listed_stores;
                    var total_pages = Math.ceil(total_stores/page_size);
                    var page_list = '<li class="prev_page"><a href="#" pageno="' + (page_number - 1) + '">&laquo;</a></li>';
                    for(var i = 1; i <= total_pages; i++) {
                        page_list += '<li ' + (i == page_number ? 'class="active"' : '') + '><a href="#" pageno="' + i + '">' + i + '</a></li>';
                    }
                    page_list += '<li class="next_page"><a href="#" pageno="' + (page_number + 1) + '">&raquo;</a></li>';
                    $('.pagination', elem_cont).html(page_list);
                    if(page_number == 1) {
                        $('.pagination .prev_page', elem_cont).addClass('disabled');
                    }
                    if(page_number == total_pages) {
                        $('.pagination .next_page', elem_cont).addClass('disabled');
                    }

                    var page_size_list = '';
                    var ts_ceil = Math.ceil(total_stores/5);
                    var ts_mod = ts_ceil % 5;
                    var page_num_multiplier = ts_ceil;
                    if(ts_mod > 0) page_num_multiplier = ts_ceil + (5 - ts_mod);
                    if(total_stores > pageSize) {
                        var i = pageSize;
                        page_size_list += '<option value="' + i + '" ' + (page_size == i ? "selected=\"selected\"" : "") + '>' + i + '</option>';
                        i = page_num_multiplier * 1;
                        if(i < total_stores && i > pageSize)
                            page_size_list += '<option value="' + i + '" ' + (page_size == i ? "selected=\"selected\"" : "") + '>' + i + '</option>';
                        i = page_num_multiplier * 2;
                        if(i < total_stores && i > pageSize)
                            page_size_list += '<option value="' + i + '" ' + (page_size == i ? "selected=\"selected\"" : "") + '>' + i + '</option>';
                        i = page_num_multiplier * 3;
                        if(i < total_stores && i > pageSize)
                            page_size_list += '<option value="' + i + '" ' + (page_size == i ? "selected=\"selected\"" : "") + '>' + i + '</option>';
                    }
                    i = 0;
                    page_size_list += '<option value="' + i + '" ' + (page_size == total_stores ? "selected=\"selected\"" : "") + '>All</option>';
                    $('select.select_num_items', elem_cont).html(page_size_list);
                    $('select.select_num_items', elem_cont).selectpicker('refresh');

                    stores = stores.data;
                    var store_list = '';
                    for (var i = 0; i < stores.length; i++) {
                        var elem = $('.block_store_template').clone();
                        var storeBrand = stores[i];
                        $('.item_container', elem).attr('data-id', storeBrand.id);
                        $('.item_container', elem).attr('data-mid', storeBrand.merchant.id);
                        $('.item_image img', elem).attr('src', storeBrand.brandImage);
                        $('.item_name', elem).html('<a href="' + Main.modifyURL('/merchant/item/list/' + storeBrand.id) + '">' + storeBrand.brandName + '</a>');
                        if(prioritized == 'inactive')
                            $('.add_items', elem).remove();
                        else
                            $('.add_items', elem).attr('href', Main.modifyURL('/merchant/item/form/create/' + storeBrand.id));

                        if(storeBrand.merchant.user.status == "INACTIVE") $('.add_items', elem).addClass('disabled');

                        $('.view_store', elem).attr('href', Main.modifyURL('/merchant/store/view/' + storeBrand.id));
                        store_list += elem.html();
                    }
                    if(prioritized == 'inactive') {
                        $('.inactive_stores').removeClass('hidden');
                        $('#inactive_stores').html(store_list);
                    } else {
                        $('#other_stores').html(store_list);
                    }

                } else {

                    var store_list_featured = '';
                    var store_list_prirotized = '';
                    for (var i = 0; i < stores.length; i++) {
                        var storeBrand = stores[i];
                        storeBrand.featured ? featured_count++ : prioritized_count++;
                        var elem = $('.block_store_template').clone();
                        $('.item_container', elem).attr('data-id', storeBrand.id);
                        $('.item_container', elem).attr('data-mid', storeBrand.merchant.id);
                        $('.item_image img', elem).attr('src', storeBrand.brandImage);
                        $('.item_name', elem).html('<a href="' + Main.modifyURL('/merchant/item/list/' + storeBrand.id) + '">' + storeBrand.brandName + '</a>');
                        $('.add_items', elem).attr('href', Main.modifyURL('/merchant/item/form/create/' + storeBrand.id));
                        $('.view_store', elem).attr('href', Main.modifyURL('/merchant/store/view/' + storeBrand.id));
                        if(storeBrand.featured)
                            store_list_featured += elem.html();
                        else
                            store_list_prirotized += elem.html();
                    }
                    $('#featured_stores').html(store_list_featured);
                    $('#prioritized_stores').html(store_list_prirotized);

                    Manager.check_store_type(featured_count, prioritized_count);

                }

            }

            Main.elemRatio(function() {
                $('.items_container .item_container').removeClass('invisible');
                $(window).trigger('resize');
            });

        };


        callback.loaderDiv = "body";

        Main.request(url, params, callback);

    };

    Manager.listStores = function() {

        var dataFilter = function (data, type) {

            console.log(data);
            if (!data.success) {
                alert(data.message);
                return;
            }
            var responseRows = data.params.brands.numberOfRows;
            var brands = data.params.brands.data;
            var tdata = [];
            var sessMerchants = JSON.parse(Main.getFromLocalStorage('merchants'));

            for (var i = 0; i < brands.length; i++) {
                var brand = brands[i];

                var brandId = brand.id;
                var brandName = '<a href="' + Main.modifyURL('/merchant/item/list/' + brandId) + '" data-mid="' + brand.merchantId + '">' + brand.brandName + '</a>';
                var merchantName = brand.merchant.businessTitle;
                var viewStore = '<a href="' + Main.modifyURL('/merchant/store/view/' + brandId) + '" data-mid="' + brand.merchantId + '">View Store</a>';
                var viewItem = '<a href="' + Main.modifyURL('/merchant/item/form/create/' + brandId) + '" data-mid="' + brand.merchantId + '">Add Item</a>';
                var actions = '<div class="action_links">' + viewStore + viewItem + '</div>';

                var row = [brandId, brandName, merchantName, brand.countStore, brand.featured ? "Featured" : "None", !brand.priority ? "None" : brand.priority, Main.ucfirst(brand.status), actions];
                row = $.extend({}, row);
                tdata.push(row);
            }

            var response = {};
            response.data = tdata;
            response.recordsTotal = responseRows;
            response.recordsFiltered = responseRows;

            return response;

        };

        dataFilter.url = "/merchant/get_brands";
        dataFilter.columns = [
            { "name": "id" },
            { "name": "brandName" },
            { "name": "merchant#user#fullName" },
            { "name": "" },
            { "name": "featured" },
            { "name": "priority" },
            { "name": "status" },
            { "name": "" }
        ];
        Main.createDataTable("#stores_table", dataFilter);

        $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

    };

    Manager.getCourierStaffs = function () {
        var dataFilter = function (data, type) {
            if (!data.success) {
                alert(data.message);
                return;
            }
            var responseRows = data.params.deliveryBoys.numberOfRows;
            var courierStaffs = data.params.deliveryBoys.data;
            var tdata = [];

            for (i = 0; i < courierStaffs.length; i++) {
                var courierStaff = courierStaffs[i];

                var id = courierStaff.id;
                var link_courier_staff = '<a href="' + Main.modifyURL('/organizer/courier_staff/order_history/' + id) + '">' + courierStaff.user.fullName + '</a>';
                var number = courierStaff.user.mobileNumber;
                var order_no = '<ul class="current_order_attrs">';
                var order_name = '<ul class="current_order_attrs">';
                var assigned_time = '<ul class="current_order_attrs">';
                var elapsed_time = '<ul class="current_order_attrs">';
                var job_status = '<ul class="current_order_attrs">';
                var order = courierStaff.order;
                var cntOrder;
                var exceed_time = false;

                 for(cntOrder=0; cntOrder < order.length; cntOrder++){
                     var exceed_time = order[cntOrder].elapsedTime > order[cntOrder].assignedTime;
                     order_no += "<li>"+order[cntOrder].id+"</li>";
                     order_name += "<li" + (exceed_time ? " class='color_error'" : "") + ">"+order[cntOrder].orderName+"</li>";
                     assigned_time += "<li>"+Main.convertMin(order[cntOrder].assignedTime)+"</li>";
                     elapsed_time += "<li>"+Main.convertMin(order[cntOrder].elapsedTime)+"</li>";
                     job_status += "<li>"+Main.ucfirst((order[cntOrder].orderStatus).split('_').join(' ').toLowerCase())+"</li>";
                 }

                if(job_status == '<ul class="current_order_attrs">'){

                    var arr_avail = courierStaff.availabilityStatus.split('_');
                    var new_arr_avail = [];
                    for(var j in arr_avail) {
                        new_arr_avail.push(Main.ucfirst(arr_avail[j]));
                    }

                    job_status = new_arr_avail.join(' ')+'</ul>';
                } else {
                    job_status+="</ul>";
                }
                order_no+="</ul>";
                order_name+="</ul>";

                var user_status = courierStaff.user.status;
                var balance = courierStaff.availableAmount;
                var status_link = '';
                if(user_status == 'ACTIVE')
                    status_link = '<a class="change_status elem_tooltip delivricon delivricon-remove" href="javascript:;" data-id="' + courierStaff.user.id + '" data-status="' + user_status + '" data-placement="left" title="Deactivate"></a>';
                else
                    status_link = '<a class="change_status elem_tooltip delivricon delivricon-ok" href="javascript:;" data-id="' + courierStaff.user.id + '" data-status="' + user_status + '" data-placement="left" title="Activate"></a>';

                var action = '<div class="action_links">' +
                    '<a href="#" data-toggle="modal" class="view_courier_boy_map elem_tooltip delivricon delivricon-map " data-cbid = "' + id + '" data-placement="left" title="View on Map"></a>' +
                    '<a href="#" data-toggle="modal" class="update_courier_boy_account elem_tooltip delivricon delivricon-wallet"  data-cbid = "' + id + '" data-target="#modal_account" data-placement="left" title="Update Accounts"></a>' +
                    '<a class="elem_tooltip delivricon delivricon-user" href="' + Main.modifyURL('/organizer/courier_staff/profile/' + id) + '" data-placement="left"  title="View Profile"></a>' + status_link
                    '</div>';

                var row = [id, link_courier_staff, number, order_no, order_name, job_status, assigned_time, elapsed_time, Main.ucfirst(user_status), Main.getFromLocalStorage("currency")+' '+balance, action];
                row = $.extend({}, row);
                tdata.push(row);
            }

            var response = {};
            response.data = tdata;
            response.recordsTotal = responseRows;
            response.recordsFiltered = responseRows;

            return response;

        };

        dataFilter.url = "/accountant/get_dboys";
        dataFilter.columns = [
            { "name": "id" },
            { "name": "user#fullName" },
            { "name": "user#mobileNumber" },
            { "name": "order#id" },
            { "name": "order#orderName" },
            { "name": "order#orderStatus" },
            { "name": "order#assignedTime" },
            { "name": "order#elapsedTime" },
            { "name": "user#status" },
            { "name": "availableAmount" },
            { "name": "" }
        ];
        Main.createDataTable("#courier_staff_table", dataFilter);

        $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

    };

    Manager.getCourierBoyMap = function () {

        $('.change_status').live('click', function(){

            var chk_confirm = confirm('Are you sure you want to ' + ($(this).attr('data-status') == 'ACTIVE' ? "deactivate" : "activate") + ' shopper?');
            if (!chk_confirm) return false;

            var callback = function(status, data) {
                if(data.success) Manager.getCourierStaffs();
            };

            var data = {};
            data.className = 'User';
            data.statusId = $(this).attr('data-status') == 'ACTIVE' ? '3' : '2';

            Main.request('/merchant/change_status', data, callback, {id: $(this).attr('data-id')});

        });

        $('#modal_map').on('hidden.bs.modal', function(){
            for (var i in godMarkers) {
                godMarkers[i].setMap(null);
            }
            godMarkers = {};
            mapBounds = new google.maps.LatLngBounds();
        });
        $('body').delegate('.view_courier_boy_map', 'click', function () {
            $('#modal_map').modal('show');
            var id = $(this).data("cbid");
            setTimeout(function () {

                disableMapEdit = true;
                selectedCountry = undefined;
                if(!initialized) initialize(); else google.maps.event.trigger(map, 'resize');

//                noEditInitialise();
                //if(!noEditInitialised) noEditInitialise(); else google.maps.event.trigger(map, 'resize');
                var callback = function (status, data) {
                    if (!data.success) {
                        alert(data.message);
                        return;
                    }
 /*                   var courierStaff = data.params.deliveryBoy;
                    var srclatlng = new google.maps.LatLng(courierStaff.latitude, courierStaff.longitude);
                    //var destlatlang =  new google.maps.LatLng("27.6891424", "85.324561");
                    map.setZoom(18);
                    map.setCenter(srclatlng);

                    new google.maps.Marker({
                        position: srclatlng,
                        map: map
                        //draggable: true
                    });

                    if (typeof(destlatlang) != 'undefined') {
                        new google.maps.Marker({
                            position: destlatlang,
                            map: map
                            //draggable: true
                        });

                        var request = {
                            origin: srclatlng,
                            destination: destlatlang,
                            travelMode: google.maps.DirectionsTravelMode.DRIVING
                        };

                        var directionsService = new google.maps.DirectionsService();
                        var directionsDisplay = new google.maps.DirectionsRenderer();

                        directionsService.route(request, function (result, status) {
                            console.log(status);
                            console.log(result);
                            if (status == google.maps.DirectionsStatus.OK) {
                                directionsDisplay.setDirections(result);
                            } else {
                                alert("Directions was not successful because " + status);
                            }
                        });

                        directionsDisplay.setMap(map);
                    }*/

                    var courierStaff = data.params.deliveryBoy;

                    var locCourierBoy = {};
                    var locStore = {};
                    var locCustomer = {};

                    if(courierStaff.latitude != undefined && courierStaff.longitude != undefined) {
                        locCourierBoy.name = courierStaff.user.fullName;
                        locCourierBoy.lat = courierStaff.latitude;
                        locCourierBoy.lang = courierStaff.longitude;
                    }

                    var orders = courierStaff.order;
                    for(var i = 0; i < orders.length; i++) {
                        if(orders[i].orderStatus != "ORDER_ACCEPTED") {
                            locStore.name = orders[i].store.name;
                            locStore.lat = orders[i].store.latitude;
                            locStore.lang = orders[i].store.longitude;
                            locStore.address = orders[i].store.street + ', ' + orders[i].store.city;
                            locCustomer.name = orders[i].customer.user.fullName;
                            locCustomer.lat = orders[i].customer.latitude;
                            locCustomer.lang = orders[i].customer.longitude;
                            break;
                        }
                    }

                    if(!$.isEmptyObject(locCourierBoy)) addGodMarker(locCourierBoy, "courier");
                    if(!$.isEmptyObject(locCustomer)) addGodMarker(locCustomer, "customer");
                    if(!$.isEmptyObject(locStore)) addGodMarker(locStore, "store");


                }
//                callback.loaderDiv = ".view_courier_boy_map";
                callback.requestType = "GET";
                var headers = {};
                headers.id = id;
                Main.request('/accountant/get_dboy', {}, callback, headers);
            }, 300);
        });
    }

    Manager.getCourierBoyAccount = function () {
        $('body').delegate('.update_courier_boy_account', 'click', function () {
            var id = $(this).data("cbid");
            $('#modal_account').data('cbid', id);
            var callback = function (status, data) {
                if (!data.success) {
                    alert(data.message);
                    return;
                }
                var courierStaff = data.params.deliveryBoy;
                console.log(courierStaff);
                /*$(".due_amount").text(courierStaff.previousDue);
                $("#due_amount_val").val(courierStaff.previousDue)
                $(".available_balance").text(courierStaff.walletAmount + courierStaff.bankAmount);
                $(".to_be_submitted").text(courierStaff.walletAmount);
                $("#to_be_submitted_val").val(courierStaff.walletAmount);*/
                Manager.fillDboyAccount(courierStaff);
                $('#modal_account').data('cbname', courierStaff.user.fullName);

            }
            callback.loaderDiv = ".update_courier_boy_account";
            callback.requestType = "GET";
            var headers = {};
            headers.id = id;
            Main.request('/accountant/get_dboy', {}, callback, headers);
        });
    }

    Manager.updateCourierBoyAccount = function () {
        $('body').delegate('#advance_amount_val', 'keypress', function (event) {
            if ($('#advance_amount_val').hasClass('error')) {
                $('#advance_amount_val').removeClass('error');
            }
            var cbid = $('#modal_account').data('cbid');
            var keycode = (event.keyCode ? event.keyCode : event.which);
            if (keycode == '13') {
                if ($(this).val() == null)
                    return;

                if (isNaN($('#advance_amount_val').val())) {
                    $('#advance_amount_val').addClass('error');
                    return;
                }

                var conf = confirm("Are you sure you want to add advance amount for '" + $('#modal_account').data('cbname') + "'?");
                if (!conf) {
                    $('#advance_amount_val').val('');
                    return;
                }

                var callback = function (status, data) {
                    if (!data.success) {
                        alert(data.message);
                        return;
                    }
                    var courierStaff = data.params.deliveryBoy;
                    /*$(".due_amount").text((courierStaff.previousDue).toFixed(2));
                    $("#due_amount_val").val((courierStaff.previousDue).toFixed(2))
                    $(".available_balance").text((courierStaff.walletAmount + courierStaff.bankAmount).toFixed(2));
                    $(".to_be_submitted").text((courierStaff.walletAmount).toFixed(2));
                    $("#to_be_submitted_val").val((courierStaff.walletAmount).toFixed(2));*/
                    Manager.fillDboyAccount(courierStaff);
                    $('#advance_amount_val').val('');
                }
                callback.loaderDiv = "body";
                callback.requestType = "POST";
                var headers = {};
                headers.id = cbid;
                Main.request('/accountant/update_dboy_account', {advanceAmount: $(this).val()}, callback, headers);
            }

        });
    }

    Manager.submitCourierBoyPreviousAmount = function () {
        $('body').delegate('#ack', 'click', function () {
            var cbid = $('#modal_account').data('cbid');
            if ($(this).prop("checked")) {
                var conf = confirm("Are you sure you want to acknowledge  RS." + $("#due_amount_val").val() + " from '" + $('#modal_account').data('cbname') + "'?");
                if (!conf) {
                    $(this).prop("checked", false);
                    return;
                }

                if ($("#due_amount_val").val() == 0 || $("#due_amount_val").val() == null) {
                    $(this).prop("checked", false);
                    return;
                }

                var callback = function (status, data) {
                    if (!data.success) {
                        alert(data.message);
                        return;
                    }
                    var courierStaff = data.params.deliveryBoy;
                    /*$(".due_amount").text((courierStaff.previousDue).toFixed(2));
                    $("#due_amount_val").val((courierStaff.previousDue).toFixed(2))
                    $(".available_balance").text((courierStaff.walletAmount + courierStaff.bankAmount).toFixed(2));
                    $(".to_be_submitted").text((courierStaff.walletAmount).toFixed(2));
                    $("#to_be_submitted_val").val((courierStaff.walletAmount).toFixed(2));*/
                    Manager.fillDboyAccount(courierStaff)
                    $(this).prop("checked", false);
                }
                callback.loaderDiv = "body";
                callback.requestType = "POST";
                var headers = {};
                headers.id = cbid;
                Main.request('/accountant/dboy_ack_payment', {submittedAmount: $("#due_amount_val").val()}, callback, headers);

            }
        });
    }

    Manager.submitCourierBoyWalletAmount = function () {
        $('body').delegate('#submit', 'click', function () {
            var cbid = $('#modal_account').data('cbid');
            if ($(this).prop("checked")) {
                var conf = confirm("Are you sure you want to submit RS." + $("#to_be_submitted_val").val() + " from '" + $('#modal_account').data('cbname') + "'?");
                if (!conf) {
                    $(this).prop("checked", false);
                    return;
                }

                if ($("#to_be_submitted_val").val() == 0 || $("#to_be_submitted_val").val() == null) {
                    $('#submit').prop("checked", false);
                    return;
                }

                var callback = function (status, data) {
                    if (!data.success) {
                        alert(data.message);
                        return;
                    }
                    var courierStaff = data.params.deliveryBoy;
                    /*$(".due_amount").text(courierStaff.previousDue).toFixed(2));
                    $("#due_amount_val").val(courierStaff.previousDue).toFixed(2))
                    $(".available_balance").text(courierStaff.walletAmount + courierStaff.bankAmount);
                    $(".to_be_submitted").text(courierStaff.walletAmount);
                    $("#to_be_submitted_val").val(courierStaff.walletAmount);*/
                    Manager.fillDboyAccount(courierStaff);
                    $('#submit').prop("checked", false);
                }
                callback.loaderDiv = "body";
                callback.requestType = "POST";
                var headers = {};
                headers.id = cbid;
                Main.request('/accountant/dboy_wallet_payment', {submittedAmount: $("#to_be_submitted_val").val()}, callback, headers);

            }
        });
    }

    Manager.fillDboyAccount = function(courierStaff){
        if(typeof courierStaff.previousDue != "undefined"){
            $(".due_amount").text(parseFloat(courierStaff.previousDue).toFixed(2));
            $("#due_amount_val").val(parseFloat(courierStaff.previousDue).toFixed(2));
        }
        if(typeof courierStaff.walletAmount != "undefined" && typeof courierStaff.bankAmount != "undefined"){
            $(".available_balance").text(parseFloat(courierStaff.walletAmount + courierStaff.bankAmount).toFixed(2));
        }
        if(typeof courierStaff.walletAmount != "undefined"){
            $(".to_be_submitted").text(parseFloat(courierStaff.walletAmount).toFixed(2));
            $("#to_be_submitted_val").val(parseFloat(courierStaff.walletAmount).toFixed(2));
        }

    }

    Manager.getCategories = function(){
        var callback = function (status, data) {
            if (!data.success) {
                alert(data.message);
                return;
            }
            var categories = data.params.categories;
            var category_list = '';
            var padding = 40;
            function categoryList(categories, padding, load) {
                category_list += '<ul class="nav nav-stacked ' + (load ? "" : "hidden") + '">';
                for(var i = 0; i < categories.length; i++) {
                    category_list += '<li><a href="#" data-id="' + categories[i].id + '" style="padding-left: ' + padding + 'px"><span class="glyphicon ' + ( (categories[i].child != undefined) && (categories[i].child.length > 0) ? 'glyphicon-plus' : '') + '"></span>' + categories[i].name + '</a>';
                    if((categories[i].child != undefined) && (categories[i].child.length > 0)) categoryList(categories[i].child, padding + 20);
                    category_list += '</li>';
                }
                category_list += '</ul>';
            }
            categoryList(categories, padding, true);
            $('.cateogry_list').html(category_list);

            function treeAction(elem, action) {
                var elem_parent = elem.parent('a');
                if(elem_parent.siblings('ul').length > 0) {
                    if(action == 'open') {
                        elem_parent.siblings('ul').removeClass('hidden');
                        elem.removeClass('glyphicon-plus').addClass('glyphicon-minus');
                        $('.glyphicon-minus',elem_parent.parent('li').siblings('li')).removeClass('glyphicon-minus').addClass('glyphicon-plus');
                        $('ul', elem_parent.parent('li').siblings('li')).addClass('hidden');
                    } else {
                        elem_parent.siblings('ul').addClass('hidden');
                        elem.removeClass('glyphicon-minus').addClass('glyphicon-plus');
                        $('.glyphicon-minus', elem_parent.parent('li')).removeClass('glyphicon-minus').addClass('glyphicon-plus');
                        $('ul', elem_parent.parent('li')).addClass('hidden');
                    }
                }

            }

            $('.cateogry_list a').live('click', function(e){
                e.preventDefault();
                if(e.target == this) {
                    treeAction($('.glyphicon', this), 'open');
                    Manager.getCategory($(this).data("id"));
                    $('.cateogry_list a').removeClass('current_category');
                    $(this).addClass('current_category');
                }
            });

            $('.cateogry_list .glyphicon').live('click', function(){
                var elem = $(this);
                if(elem.hasClass('glyphicon-plus')) {
                    treeAction(elem, 'open')
                } else if (elem.hasClass('glyphicon-minus')) {
                    treeAction(elem, 'close')
                }
            });
        }
        callback.loaderDiv = ".categories_container";
        callback.requestType = "GET";
        Main.request('/organizer/get_default_categories', {}, callback);

        var item_list = '';
        var callback1 = function(success, data){
            var categories = data.params.categories;
            for(var j = 0; j < categories.length; j++) {
                var category = categories[j];
                var elem = $('.item_container_template').clone();
                $('.item_container', elem).attr('data-id', category.id);
                $('.item_image img', elem).attr('src', category.imageUrl);
                $('.item_name a', elem).html(category.name);
                item_list += elem.html();
            }
            $('.parent_category_list').html(item_list);
            $('.parent_category_list .item_container').removeClass('invisible');
        }

        callback1.loaderDiv = ".parent_category_list";
        callback1.requestType = "GET";
        Main.request('/merchant/get_parent_categories', {}, callback1);
    }


    Manager.getCategory = function(id){
        var callback = function(success, data){
            if(!data.success) {
                alert(data.message);
                return;
            }
            $('.add_child_btn').removeClass('hidden');
            var category = data.params.category;
            $(".category_detail").removeClass("hidden");
            $(".parent_category_list").addClass("hidden");
            $('.category_detail #category_id').val(category.id);
            $('.category_detail #category_parent_id').val(category.id);
            $(".category_detail .name").html(category.name);
            $(".category_detail #name").val(category.name);
            if(category.imageUrl != null)
                $('#category_image').html('<img src="' + category.imageUrl + '" class="img-responsive" style="height: 100%;" />');
            else
                $('#category_image').html('<div class="drop_info">Drop image file <br/> (or click to browse)</div><div class="drop_title">Category Image</div>');

            Main.elemRatio();

        }
        var headers = {};
        headers.id = id;
        callback.loaderDiv = "body";
        callback.requestType = "GET";
        Main.request('/organizer/get_category', {}, callback, headers);
    }


    Manager.loadEditCategory = function(){

        var re_calculate_width = true;
        $('.item_container').live('mouseover', function(){
            if(re_calculate_width == true) {
                var elem = $('.items_container .item_container');
                elem.width(elem.eq(0).width() - 1);
                elem.height(elem.eq(0).height());
                re_calculate_width = false;
            }
        });
        $(window).resize(function() {
            $('.items_container .item_container').removeAttr('style');
            $('.items_container .item_container .item_image').removeAttr('style');
            re_calculate_width = true;
        });

        $('.menu_toggle').click(function(){
            $('.items_container .item_container').removeAttr('style');
            $('.items_container .item_container .item_image').removeAttr('style');
            re_calculate_width = true;
        });

        var sortableParam = {
            revert: true,
            tolerance: 'pointer',
            containment: 'parent',
            stop: function( event, ui ) {
                var arrdata = [];
                $('#parent_category_list .item_container').each(function(){
                    arrdata.push({id: $(this).attr('data-id'), priority: $(this).index() + 1});
                });
                Main.request('/organizer/change_category_priority', {categoryList: arrdata});
            }
        };

        $('#parent_category_list').sortable(sortableParam);

        $("a.view_home").click(function(){
            $(".parent_category_list").removeClass("hidden");
            $(".category_detail").addClass("hidden");
            Main.elemRatio();
        });

        Image.dropZone('#category_image_input', '#category_image');
        $("#category_image").addClass('disabled');
        $('.edit_btn').click(function () {

            $(".editable").removeClass('hidden');
            $(".none_editable").addClass('hidden');
            $("#category_image").removeClass('disabled');

            $.validator.setDefaults({
                errorPlacement : function(error, element){
                    $('#error_container').html(error);
                }
            });

            $('#category_form').validate();
            $('#name').rules('add', {required: true, messages : {required: "name is required."}});
        });

        $('.save_btn').removeClass('add').addClass('edit');

        $('.cancel_btn').click(function () {
            $(".none_editable").removeClass('hidden');
            $(".editable").addClass('hidden');
            $("#category_image").addClass('disabled');
        });
    }

    Manager.saveCategory = function(){
        function treeAction(elem, action) {
            var elem_parent = elem.parent('a');
            console.log(elem_parent);
            if(elem_parent.siblings('ul').length > 0) {
                if(action == 'open') {
                    elem_parent.siblings('ul').removeClass('hidden');
                    elem.removeClass('glyphicon-plus').addClass('glyphicon-minus');
                    $('.glyphicon-minus',elem_parent.parent('li').siblings('li')).removeClass('glyphicon-minus').addClass('glyphicon-plus');
                    $('ul', elem_parent.parent('li').siblings('li')).addClass('hidden');
                } else {
                    elem_parent.siblings('ul').addClass('hidden');
                    elem.removeClass('glyphicon-minus').addClass('glyphicon-plus');
                    $('.glyphicon-minus', elem_parent.parent('li')).removeClass('glyphicon-minus').addClass('glyphicon-plus');
                    $('ul', elem_parent.parent('li')).addClass('hidden');
                }
            }

        }

        $('.save_btn').click(function () {
            if ($('#category_form').valid()) {
               if($(this).hasClass('edit')){
                    var id = $('#category_id').val();
                    var chk_confirm = confirm('Are you sure you want to update Category?');
                    if (!chk_confirm) return false;

                    var data = {};
                    var callback = function(success, data){
                        console.log(data);
                        if (!data.success) {
                            alert(data.message);
                            return;
                        }
                        var category = data.params.category;

                        $('.category_detail #category_id').val(category.id);
                        //$('.category_detail #category_parent_id').val(category.id);
                        $(".category_detail .name").html(category.name);
                        $(".category_detail #name").val(category.name);
                        if(category.imageUrl != null)
                            $('#category_image').html('<img src="' + category.imageUrl + '" class="img-responsive" style="height: 100%;" />');

                        $(".none_editable").removeClass('hidden');
                        $(".editable").addClass('hidden');
                        $("#category_image").addClass('disabled');

                    }
                    var headers = {};
                    headers.id = id;
                    data.name = $('#name').val();
                    if(typeof $('#category_image').find('img').attr('src') != "undefined" &&  $('#category_image').find('img').attr('src').indexOf('https://') == -1)
                        data.imageUrl = $('#category_image').find('img').attr('src');
                    callback.loaderDiv = "body";
                    callback.requestType = "PUT";
                    Main.request('/organizer/update_category', data, callback, headers);
                }else if($(this).hasClass('add')){
                    var parent_id = $('#category_parent_id').val();
                    var chk_confirm = confirm('Are you sure you want to Add New Category?');
                    if (!chk_confirm) return false;

                    var data = {};
                    var callback = function(success, data){
                        if (!data.success) {
                            alert(data.message);
                            return;
                        }
                        //Manager.getCategories();
                        var category = data.params.category;

                        $('.cateogry_list a').removeClass("current_category");
                        $('.cateogry_list a[data-id='+category.id+']').addClass('current_category');
                        $('.category_detail #category_id').val(category.id);
                        $('.category_detail #category_parent_id').val(category.id);
                        $(".category_detail .name").html(category.name);
                        $(".category_detail #name").val(category.name);
                        if(category.imageUrl != null)
                            $('#category_image').html('<img src="' + category.imageUrl + '" class="img-responsive" style="height: 100%;" />');

                        $(".none_editable").removeClass('hidden');
                        $(".editable").addClass('hidden');
                        $("#category_image").addClass('disabled');

                        /*if(parent_id != ''){
                            $('.cateogry_list a[data-id='+parent_id+']').siblings('ul').removeClass('hidden');
                            $('.cateogry_list a[data-id='+parent_id+']').removeClass('glyphicon-plus').addClass('glyphicon-minus');
                            $('.glyphicon-minus',$('.cateogry_list a[data-id='+parent_id+']').parent('li').siblings('li')).removeClass('glyphicon-minus').addClass('glyphicon-plus');
                            $('ul', $('.cateogry_list a[data-id='+parent_id+']').parent('li').siblings('li')).addClass('hidden');
                        }else{
                            $('.cateogry_list a[data-id='+category.id+']').siblings('ul').removeClass('hidden');
                            $('.cateogry_list a[data-id='+category.id+']').removeClass('glyphicon-plus').addClass('glyphicon-minus');
                            $('.glyphicon-minus',$('.cateogry_list a[data-id='+category.id+']').parent('li').siblings('li')).removeClass('glyphicon-minus').addClass('glyphicon-plus');
                            $('ul', $('.cateogry_list a[data-id='+category.id+']').parent('li').siblings('li')).addClass('hidden');
                        }*/

                        $('.cateogry_list a').removeClass("current_category");
                        $('.cateogry_list a[data-id='+category.id+']').addClass('current_category');
                    }
                    var headers = {};
                    headers.id = parent_id;
                    data.name = $('#name').val();
                    if(typeof $('#category_image').find('img').attr('src') != "undefined")
                    data.imageUrl = $('#category_image').find('img').attr('src');

                    callback.loaderDiv = "body";
                    Main.request('/organizer/save_category', data, callback, headers);
                }
            }
        });
    }

    Manager.loadAddSubCategory = function(){

        $('body').delegate('a.add_child_btn, a.add_root_btn', 'click', function(){
            Main.elemRatio();
            $(".category_detail").removeClass('hidden');
            $(".parent_category_list").addClass('hidden');
            if($(this).hasClass('add_root_btn')){
                $('.add_child_btn').addClass('hidden');
            }
            var prevHtml =  $('#category_image').html();
            var prevId = $('.category_detail #category_id').val();
            $(".editable").removeClass('hidden');
            $(".none_editable").addClass('hidden');
            $("#category_image").removeClass('disabled');

            $('#category_image').html('<div class="drop_info">Drop image file <br/> (or click to browse) <br /> Min Size: 200x200</div><div class="drop_title">Category Image</div>');
            $('.category_detail #category_id').val('');
            if($(this).hasClass('add_root_btn')){
                $('.category_detail #category_parent_id').val('');
            }
            $(".category_detail #name").val('');
            $('.save_btn').removeClass('edit').addClass('add');
            $('.cancel_btn').removeClass('edit').addClass('add');
//            Image.dropZone('#category_image_input', '#category_image');

            $('.cancel_btn').click(function () {
                $(".none_editable").removeClass('hidden');
                $(".editable").addClass('hidden');
                 if($(this).hasClass('add')){
                     $('#category_image').html(prevHtml);
                     $('.category_detail #category_id').val(prevId);
                 }

            });
        });

    }

    Manager.loadDashboard = function(){

        disableMapEdit = true;
        selectedCountry = undefined;
        if(!initialized) initialize(); else google.maps.event.trigger(map, 'resize');

        $('.count_head').click(function(){
            $(this).next('.more_data_cont').stop().slideToggle(200);
        });
        $('body').click(function(e){
            var target = $(e.target);
            if(target.parents('.count_block').length == 0) $('.more_data_cont').stop().slideUp(200);
        });

        var callback = function(success, data){
            console.log(data);

            if(!data.success) {
                alert(data.message);
                return;
            }

            var godView = data.params.godsView[0];

            var currentDelivery = godView.currentDelivery;
            currentDelivery = currentDelivery[Object.keys(currentDelivery)[0]];

            var newOrders = godView.newOrders;
            newOrders = newOrders[Object.keys(newOrders)[0]];
            var data_row = '';
            for(var i in newOrders) {
                var newOrder = JSON.parse(newOrders[i]);

                var locCourierBoy = newOrder.courierBoy;
                var locStore = newOrder.store;
                var locCustomer = newOrder.customer;

                addGodMarker(locCourierBoy, "courier");
                addGodMarker(locCustomer, "customer");
                addGodMarker(locStore, "store");

                data_row += '<div class="data_row" data-store="' + locationToKey({latitude: locStore.lat, longitude: locStore.lang}) + '" data-customer="' + locationToKey({latitude: locCustomer.lat, longitude: locCustomer.lang}) + '" data-courierBoy="' + locationToKey({latitude: locCourierBoy.lat, longitude: locCourierBoy.lang}) + '">' + newOrder.store.name + ', ' + newOrder.store.address + ' &rarr; ' + newOrder.customer.address + '</div>';
            }
            $('.new_orders .more_data').html(data_row);

            $('.count_span').each(function(){
                $(this).html(Object.keys(godView[$(this).attr('data-get')])[0]);
            });

            $('.count_inRouteToDelivery').html(currentDelivery.inRouteToDelivery);
            $('.count_inRouteToPickUp').html(currentDelivery.inRouteToPickUp);
            $('.count_atStore').html(currentDelivery.atStore);
            $('.count_orderAccepted').html(currentDelivery.orderAccepted);
            $('.count_ct_averageDeliveryTime').html(godView.completedToday[Object.keys(godView.completedToday)[0]].averageDeliveryTime);
            $('.count_std_averageDeliveryTime').html(godView.servedTillDate[Object.keys(godView.servedTillDate)[0]].averageDeliveryTime);


        }
        callback.loaderDiv = "body";
        callback.requestType = "GET";
        Main.request('/organizer/gods_view', {}, callback);

        var bcallback = function(success, data){
            console.log(data);

            if(data.success && data.params.credit.creditsAvailable != undefined) {
                $('.remaining_balance').html(data.params.credit.creditsAvailable);
            }


        }
        bcallback.requestType = "GET";
        Main.request('/organizer/sms_credits', {}, bcallback);

        $('.toggle_map_view').click(function(){
            if($(this).hasClass('glyphicon-resize-full')) {
                $('body').addClass('menu_opened');
                $('html, body').animate({scrollTop: $('.count_head').eq(0).offset().top});
                $(this).addClass('glyphicon-resize-small').removeClass('glyphicon-resize-full');
                $('.menu_toggle').trigger('click');
                $('.map-container').animate({height: $(window).height() - 385}, function(){
                    google.maps.event.trigger(map, 'resize');
                    map.fitBounds(mapBounds);
                });
            } else {
                $('body').removeClass('menu_opened');
                $('html, body').animate({scrollTop: 0});
                $(this).addClass('glyphicon-resize-full').removeClass('glyphicon-resize-small');
//                $('.menu_toggle').trigger('click');
                $('.map-container').css('height', '');
                google.maps.event.trigger(map, 'resize');
                map.fitBounds(mapBounds);
            }
        });

        var flightPath;

        $('.new_orders .data_row').live('click', function(){
            removeAnimation(godMarkers);
            var mCustomer = godMarkers[$(this).attr('data-customer')];
            var mStore = godMarkers[$(this).attr('data-store')];
            var mCourierBoy = godMarkers[$(this).attr('data-courierBoy')];
            mCustomer.setAnimation(google.maps.Animation.BOUNCE);
            mStore.setAnimation(google.maps.Animation.BOUNCE);
            mCourierBoy.setAnimation(google.maps.Animation.BOUNCE);

            if(flightPath != undefined) flightPath.setMap(null);
            flightPath = new google.maps.Polyline({
                path: [mCustomer.getPosition(), mStore.getPosition(), mCourierBoy.getPosition()],
                geodesic: true,
                strokeColor: '#FF0000',
                strokeOpacity: 1.0,
                strokeWeight: 2
            });
            flightPath.setMap(map);

        });

        $('.open_chart').click(function(){

            $('.open_chart').removeClass('active_chart');
            $(this).addClass('active_chart');
            $('#modal_chart').modal('show');

        });

        $('#chart-date-interval button').click(function(){

            $('#chart-date-interval button').removeClass('btn-primary');
            $(this).addClass('btn-primary');
            Manager.getChart($('.active_chart').attr('data-action'), $('.active_chart').attr('data-title'), $(this).attr('data-period'));

        });

        $('#modal_chart').on('shown.bs.modal', function(){
            Manager.getChart($('.active_chart').attr('data-action'), $('.active_chart').attr('data-title'))
        });

        $('#modal_chart').on('hidden.bs.modal', function(){
            $('#chart_container').html('');
        });

    };

    Manager.getChart = function(action, title, period) {

        var callback = function (status, jsondata){

            if(!jsondata.success){
                alert(jsondata.message)
                return false;
            }

            var graphData = jsondata.params.graphData;
            if(action == 'get_new_user_graph') {

                var count1 = graphData;
                var chartData = [['Date', 'New User Registered']];

                for(var i in count1) {
                    var dateStr = new Date(i).format("mmm dd, yyyy");
                    chartData.push([dateStr, count1[i]]);
                }

                if(Object.keys(count1).length < 1) chartData.push([null, 0]);

            } else {

                var count1 = graphData[Object.keys(graphData)[0]];
                var count2 = graphData[Object.keys(graphData)[1]];
                var chartData = [];

                if(action == "get_on_time_delivery_graph") {
                    chartData = [['Date', 'Exceed On Time', 'On Time Delivery']];
                } else if (action == "get_delivery_success_graph") {
                    chartData = [['Date', 'Successful Delivery', 'Failed Delivery']];
                } else {
                    chartData = [['Date', 'Average Time', 'Order Count']];
                }

                for(var i in count1) {
                    var dateStr = new Date(i).format("mmm dd, yyyy");
                    if(action =='get_delivery_graph')
                        chartData.push([dateStr, count2[i] == 0 ? 0 : Math.ceil(count1[i]/count2[i]), count2[i]]);
                    else
                        chartData.push([dateStr, count1[i], count2[i]]);
                }

                if(Object.keys(count1).length < 1) chartData.push([null, 0, 0]);

            }

            title = title == undefined ? '' : title
            var showTextEvery = Math.ceil(Object.keys(count1).length/10);
            console.log(chartData.length);
            var options = {
                title: title,
                titlePosition: 'out',
                legend: {position: 'top'},
                orientation: 'horizontal',
                height: 280,
                pointSize: 5,
                hAxis: {showTextEvery: showTextEvery}
            };

            var chart = new google.visualization.AreaChart(document.getElementById('chart_container'));
            var dataTable = google.visualization.arrayToDataTable(chartData);

            chart.draw(dataTable, options);

        };
        callback.requestType = "GET";
        callback.loaderDiv = "#modal_chart .modal-content";

        if(!period) period = "1Month";

        Main.request("/organizer/" + action, {}, callback, {id: period});

    }

    Manager.getSmsCustomers = function () {

        var dataFilter = function (data, type) {

            console.log(data);
            if (!data.success) {
                alert(data.message);
                return;
            }
            var users = data.params.sendableSMSList;
            var responseRows = users.length;
            var tdata = [];

            for (i = 0; i < users.length; i++) {
                var user = users[i];

                var id = user.id;
                var fullName = user.fullName;
                var mobileNumber = user.mobileNo;
                var sentCount = user.totalSmsSend;
                var action = '<a class="trigger_activation" href="#" data-id="' + id + '"  data-mobile="' + mobileNumber + '" >Resend SMS</a>';

                var row = [id, fullName, mobileNumber, sentCount, action];
                row = $.extend({}, row);
                tdata.push(row);
            }

            var response = {};
            response.data = tdata;
            response.recordsTotal = responseRows;
            response.recordsFiltered = responseRows;

            return response;

        };

        dataFilter.url = "/organizer/send_sms_customer_list";
        dataFilter.columns = [
            { "name": "id" },
            { "name": "fullName" },
            { "name": "mobileNo" },
            { "name": "totalSmsSend" },
            { "name": "" }
        ];
        dataFilter.requestType = "GET";
        Main.createDataTable("#customers_table", dataFilter);

        $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

    };

    Manager.loadSmsFunctions = function() {

        var callback = function (status, data) {

            alert(data.message);
            if (data.success == true) {
                Manager.getSmsCustomers();
            }
        };

        $('.trigger_activation').live('click', function(){

            var chk_confirm = confirm('Are you sure you want to resend SMS?');
            if (!chk_confirm) return false;
            callback.loaderDiv = "body";
            Main.request('/organizer/send_sms', {id: $(this).attr('data-id'), mobileNo: $(this).attr('data-mobile')}, callback);

        });

    };

    Manager.loadNotification = function() {

        $('label.check_label .checkbox').removeAttr("checked");

        $('label.check_label.disabled').live('click', function(e){
            e.preventDefault();
        });

        $('label.check_label .checkbox').live('click', function(){
            if($(this).prop('checked')) {
                $(this).siblings('.check_span').addClass("icon_full");
            } else {
                $(this).siblings('.check_span').removeClass("icon_full");
            }
        });

        $("label.check_label").live('mouseover', function ( event ) {
            $('.check_span', this).addClass("icon_semi");
        });

        $("label.check_label").live('mouseout', function ( event ) {
            $('.check_span', this).removeClass("icon_semi");
        });

        $('#form_notification input[type="checkbox"]').click(function(){

            console.log($(this).prop('checked'));
            if($(this).prop('checked')) {
                if($(this).val() == 'DELIVERY_BOY') {
                    $('.check_customer').removeAttr('checked').siblings('.icon_full').removeClass('icon_full');
                } else {
                    $('.check_shopper').removeAttr('checked').siblings('.icon_full').removeClass('icon_full');
                }
            }

        });

        $.validator.setDefaults({
            errorPlacement : function(error, element){
                $('#error_container').html(error);
            },
            ignore: []
        });
        $('#form_notification').validate({
            submitHandler: function(form) {

                var checked = false;
                var notifyList = [];

                $('#form_notification input[type="checkbox"]').each(function(){
                    if($(this).prop('checked')) {
                        checked = true;
                        notifyList.push($(this).val());
                    }
                });

                if(!checked) {
                    alert('Please check shopper or customer.');
                    return false;
                }

                var chk_confirm = confirm('Are you sure you want to send notification?');
                if(!chk_confirm) return false;

                var callback = function (status, data) {

                    alert(data.message);
                    if (data.success == true) {
                        $('.check_customer, .check_shopper').removeAttr('checked').siblings('.icon_full').removeClass('icon_full');
                        $('#form_notification')[0].reset();
                    }
                };
                callback.loaderDiv = "body";

                var params = {};
                params.pushMessage = $('#message').val();
                params.notifyToList = notifyList;

                Main.request('/organizer/send_notification', params, callback);
                return false;

            }
        });

        $('#message').rules('add', {required: true});

    };

})(jQuery);