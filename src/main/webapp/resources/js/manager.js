/**
 * Created by Lunek on 12/3/2014.
 */


if (typeof(Manager) == "undefined") var Manager = {};

(function ($) {

    Manager.getMerchants = function () {

        var callback = function (status, data) {

            console.log(data);
            if (!data.success) {
                alert(data.message);
                return;
            }
            var merchants = data.params.merchants;
            var tdata = [];

            var sess_merchants = {};
            for (i = 0; i < merchants.length; i++) {
                var merchant = merchants[i];

                var merchantId = merchant.id;
                var userId = merchant.user.id;
                var status = merchant.status;
                var link_activation = "", link_profile = "";

                if (status == "VERIFIED" || status == "UNVERIFIED") {
                    link_activation = '<a href="#" data-id="' + merchantId + '"  data-status="' + status + '"  data-toggle="modal" data-target="#modal_activation">Activate</a>';
                } else if (status == "ACTIVE") {
                    link_activation = '<a class="trigger_activation" href="#" data-id="' + userId + '"  data-status="' + status + '" >Deactivate</a>';
                } else if (status == "INACTIVE") {
                    link_activation = '<a class="trigger_activation" href="#" data-id="' + userId + '" data-status="' + status + '" >Activate</a>';
                }
                link_profile = '<a href="' + Main.modifyURL('/merchant/profile/' + merchantId) + '">Profile</a>';
                var action = '<div class="action_links">' + link_profile + link_activation + "</div>";
                var link_merchant = '<a href="' + Main.modifyURL('/merchant/store/list/' + merchantId) + '">' + merchant.businessTitle + '</a>';

                sess_merchants[merchantId] = merchant.businessTitle;
                var row = [merchantId, link_merchant, merchant.partnershipStatus ? 'Partner' : 'Non Partner', merchant.user.fullName, merchant.user.emailAddress, merchant.user.mobileNumber, Main.ucfirst(status), action];
                tdata.push(row);
            }
            Main.saveInLocalStorage('merchants', JSON.stringify(sess_merchants));

            Main.createDataTable("#merchants_table", tdata);

            $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

        };

        callback.loaderDiv = "body";
        callback.requestType = "GET";

        Main.request('/organizer/get_merchants', {}, callback);

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
        $('#commission').rules('add', {required: true, digits: true, min: 0, max: 100});
        $('#service_fee').rules('add', {required: true, digits: true, min: 0});

        $('.trigger_activation').live('click', function () {
            var statusCheck = $(this).attr('data-status') == 'INACTIVE';

            var chk_confirm = confirm('Are you sure you want to ' + (statusCheck ? "activate" : "deactivate") + ' this merchant?');
            if (!chk_confirm) return false;

            var data = {};
            data.id = $(this).attr('data-id');
            data.verifiedStatus = "" + statusCheck;
            Manager.changeUserStatus(data);
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

    Manager.changeUserStatus = function (data) {

        var callback = function (status, data) {
            alert(data.message);
            if (data.success == true) {
                Manager.getMerchants();
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
        if (prioritized) {
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
                    $('.pagination').html(page_list);
                    if(page_number == 1) {
                        $('.pagination .prev_page').addClass('disabled');
                    }
                    if(page_number == total_pages) {
                        $('.pagination .next_page').addClass('disabled');
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
                    $('select.select_num_items').html(page_size_list);
                    $('select.select_num_items').selectpicker('refresh');

                    stores = stores.data;
                    var store_list = '';
                    for (var i = 0; i < stores.length; i++) {
                        var elem = $('.block_store_template').clone();
                        var storeBrand = stores[i];
                        $('.item_container', elem).attr('data-id', storeBrand.id);
                        $('.item_container', elem).attr('data-mid', storeBrand.merchantId);
                        $('.item_image img', elem).attr('src', storeBrand.brandImage);
                        $('.item_name', elem).html('<a href="' + Main.modifyURL('/merchant/item/list/' + storeBrand.id) + '">' + storeBrand.brandName + '</a>');
                        $('.add_items', elem).attr('href', Main.modifyURL('/merchant/item/form/create/' + storeBrand.id));
                        $('.view_store', elem).attr('href', Main.modifyURL('/merchant/store/view/' + storeBrand.id));
                        store_list += elem.html();
                    }
                    $('#other_stores').html(store_list);

                } else {

                    var store_list_featured = '';
                    var store_list_prirotized = '';
                    for (var i = 0; i < stores.length; i++) {
                        var storeBrand = stores[i];
                        storeBrand.featured ? featured_count++ : prioritized_count++;
                        var elem = $('.block_store_template').clone();
                        $('.item_container', elem).attr('data-id', storeBrand.id);
                        $('.item_container', elem).attr('data-mid', storeBrand.merchantId);
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

                }

            }
            Manager.check_store_type(featured_count, prioritized_count);

            Main.elemRatio(function() {
                $('.items_container .item_container').removeClass('invisible');
                $(window).trigger('resize');
            });

        };


        callback.loaderDiv = "body";

        Main.request(url, params, callback);

    };

    Manager.getCourierStaffs = function () {

        var callback = function (status, data) {

            console.log(data);
            if (!data.success) {
                alert(data.message);
                return;
            }
            var courierStaffs = data.params.deliveryBoys;
            var tdata = [];

            for (i = 0; i < courierStaffs.length; i++) {
                var courierStaff = courierStaffs[i];

                var id = courierStaff.id;
                var link_courier_staff = '<a href="' + Main.modifyURL('/organizer/courier_staff/order_history/' + id) + '">' + courierStaff.user.fullName + '</a>';
                var number = courierStaff.user.mobileNumber;
                var order_no = 0;
                var order_name = 0;
                var job_status = courierStaff.availabilityStatus;
                var balance = courierStaff.walletAmount + courierStaff.bankAmount;
                var action = '<div class="action_links">' +
                    '<a href="#" data-toggle="modal" class="view_courier_boy_map" data-cbid = "' + id + '">View on Map</a>' +
                    '<a href="#" data-toggle="modal" class="update_courier_boy_account"  data-cbid = "' + id + '" data-target="#modal_account">Update Accounts</a>' +
                    '<a href="' + Main.modifyURL('/organizer/courier_staff/profile/' + id) + '">View Profile</a>' +
                    '</div>';

                var row = [id, link_courier_staff, number, order_no, order_name, job_status, balance, action];
                tdata.push(row);
            }

            Main.createDataTable("#courier_staff_table", tdata);

            $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

        };

        callback.loaderDiv = "body";
        callback.requestType = "GET";

        Main.request('/organizer/get_dboys', {}, callback);

    };

    Manager.getCourierBoyMap = function () {
        $('body').delegate('.view_courier_boy_map', 'click', function () {
            $('#modal_map').modal('show');
            var id = $(this).data("cbid");
            setTimeout(function () {
                noEditInitialise();
                //if(!noEditInitialised) noEditInitialise(); else google.maps.event.trigger(map, 'resize');
                var callback = function (status, data) {
                    if (!data.success) {
                        alert(data.message);
                        return;
                    }
                    var courierStaff = data.params.deliveryBoy;
                    var srclatlng = new google.maps.LatLng(courierStaff.latitude, courierStaff.longitude);
                    //var destlatlang =  new google.maps.LatLng("27.6891424", "85.324561");
                    map.setZoom(12);
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
                    }

                }
                callback.loaderDiv = ".view_courier_boy_map";
                callback.requestType = "GET";
                var headers = {};
                headers.id = id;
                Main.request('/organizer/get_dboy', {}, callback, headers);
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
                $(".due_amount").text(courierStaff.previousDue);
                $("#due_amount_val").val(courierStaff.previousDue)
                $(".available_balance").text(courierStaff.walletAmount + courierStaff.bankAmount);
                $(".to_be_submitted").text(courierStaff.walletAmount);
                $("#to_be_submitted_val").val(courierStaff.walletAmount);
                $('#modal_account').data('cbname', courierStaff.user.fullName);

            }
            callback.loaderDiv = ".update_courier_boy_account";
            callback.requestType = "GET";
            var headers = {};
            headers.id = id;
            Main.request('/organizer/get_dboy', {}, callback, headers);
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
                    $(".due_amount").text(courierStaff.previousDue);
                    $("#due_amount_val").val(courierStaff.previousDue)
                    $(".available_balance").text(courierStaff.walletAmount + courierStaff.bankAmount);
                    $(".to_be_submitted").text(courierStaff.walletAmount);
                    $("#to_be_submitted_val").val(courierStaff.walletAmount);
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
                    $(".due_amount").text(courierStaff.previousDue);
                    $("#due_amount_val").val(courierStaff.previousDue)
                    $(".available_balance").text(courierStaff.walletAmount + courierStaff.bankAmount);
                    $(".to_be_submitted").text(courierStaff.walletAmount);
                    $("#to_be_submitted_val").val(courierStaff.walletAmount);
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
                    $(".due_amount").text(courierStaff.previousDue);
                    $("#due_amount_val").val(courierStaff.previousDue)
                    $(".available_balance").text(courierStaff.walletAmount + courierStaff.bankAmount);
                    $(".to_be_submitted").text(courierStaff.walletAmount);
                    $("#to_be_submitted_val").val(courierStaff.walletAmount);
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


})(jQuery);