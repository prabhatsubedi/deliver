if (typeof(CourierStaff) == "undefined") var CourierStaff = {};

var courierProfile;

(function ($) {

    CourierStaff.loadAddCourierStaff = function () {

        Image.dropZone('#profile_image_input', '#profile_image');
        $('#country').val(Main.country);

        $.validator.addMethod("mobileNumber", function (value, element, arg) {
            return this.optional(element) || /[0-9]+$/.test(value);
        }, "Only numbers are allowed.");

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

        $.validator.addMethod("imageRequired", function(value, element, arg){
            var result = true;
            var container = $(element).prev('.drop_zone');
            if($('img', container).attr('src') == undefined || $('img', container).attr('src') == "") {
                result = false;
                container.addClass('error');
            } else {
                container.removeClass('error');
            }
            return result;
        }, "Only numbers are allowed.");


        $.validator.setDefaults({
            errorPlacement: function (error, element) {
                $('#error_container').html(error);
            },
            ignore: []
        });

        $('#gender').selectpicker();
        $('#vehicle_type').selectpicker();
        $('#gender, #vehicle_type').change(function () {
            $(this).valid();
        });

        $('#courier_boy_form').validate({
            submitHandler: function () {

                var button1 = function() {

                    var data = {};
                    var user = {};
                    var address = {};

                    address.street = $('#street').val();
                    address.city = $('#city').val();
                    address.state = $('#state').val();
                    address.country = $('#country').val();
                    address.countryCode = "00977";

                    user.fullName = $('#full_name').val();
                    user.emailAddress = $('#email').val();
                    user.mobileNumber = $('#mobile').val();
                    user.gender = $('#gender').val();
                    user.profileImage = $('#profile_image img').attr('src');
                    user.addresses = [address];

                    data.bankAccountNumber = $('#account_no').val();
                    data.vehicleType = $('#vehicle_type').val();
                    data.vehicleNumber = $('#vehicle_no').val();
                    data.licenseNumber = $('#license_no').val();
                    data.user = user;

                    var headers = {};
                    headers.username = $('#mobile').val();
                    headers.password = $('#password').val();
                    CourierStaff.addCourierStaff(data, headers);
                };

                button1.text = "Yes";
                var button2 = "No";

                var buttons = [button1, button2];
                Main.popDialog('', 'Are you sure you want to add shopper?', buttons);

                return false;

            }
        });
        $('#profile_image_input').rules('add', {imageRequired: true});
        $('#full_name').rules('add', {required: true});
        $('#email').rules('add', {email: true});
        $('#mobile').rules('add', {required: true, mobileNumber: true, minlength: 10, maxlength: 10});
        $('#password').rules('add', {required: true, minlength: 6});
        $('#gender').rules('add', {notEqual: 0});
        $('#street').rules('add', {required: true});
        $('#city').rules('add', {required: true});
        $('#state').rules('add', {required: true});
        $('#country').rules('add', {required: true});
        $('#vehicle_type').rules('add', {notEqual: 0});
//        $('#vehicle_no').rules('add', {required: true});
//        $('#license_no').rules('add', {required: true});

    };

    CourierStaff.addCourierStaff = function (data, headers) {

        $("button[type='submit']").attr("disabled", true);

        var callback = function (status, data) {
            $("button[type='submit']").removeAttr("disabled");

            Main.popDialog('', data.message, function() {
                if (data.success == true) {
                    $('#courier_boy_form').trigger('reset');
                    $('#profile_image').html('<div class="drop_info">Drop image file <br /> (or click to browse) <br /> Min Size: 200x200</div><div class="drop_title">Profile Picture</div>');
                }
            });
        };

        callback.loaderDiv = ".main_content";

        Main.request('/organizer/save_dboy', data, callback, headers);

    };


    CourierStaff.getCourierStaffProfile = function (courier_profile) {
        var id = Main.getURLvalue(3);
//        var mapBounds = new google.maps.LatLngBounds();

        disableMapEdit = true;
        selectedCountry = undefined;
        if(!initialized) initialize(); else google.maps.event.trigger(map, 'resize');

        var callback = function (status, data) {
            courierProfile = data;
            if (!data.success) {
                Main.popDialog('', data.message);
                return;
            }
            var courierStaff = data.params.deliveryBoy;

            $(".profile_header_image #profile_image").html('<img src="' + courierStaff.user.profileImage + '" class="img-responsive" style="height: 100%;" />');

            $(".profile_header_info .info1").html(courierStaff.user.fullName);

            var arr_avail = courierStaff.availabilityStatus.split('_');
            var new_arr_avail = [];
            for(var j in arr_avail) {
                new_arr_avail.push(Main.ucfirst(arr_avail[j]));
            }

            $(".profile_header_info .info2").html(new_arr_avail.join(' '));
            var rating = parseInt(courierStaff.averageRating);
            for(var i = 0; i < rating; i++) {
                $(".profile_header_info .ratings ul li").eq(i).addClass('active');
            }

            $(".val_full_name").html(courierStaff.user.fullName);
            $(".val_email").html(courierStaff.user.emailAddress);
            $(".val_mobile").html(courierStaff.user.mobileNumber);
            $(".val_gender").html(Main.ucfirst(courierStaff.user.gender));
            $(".val_street").html(courierStaff.user.addresses[0].street);
            $(".val_city").html(courierStaff.user.addresses[0].city);
            $(".val_state").html(courierStaff.user.addresses[0].state);
            $(".val_country").html(courierStaff.user.addresses[0].country);
            $(".val_account_no").html(courierStaff.bankAccountNumber);
            $(".val_vehicle_type").html(Main.ucfirst(courierStaff.vehicleType));
            $(".val_vehicle_no").html(courierStaff.vehicleNumber);
            $(".val_license_no").html(courierStaff.licenseNumber);
            $(".val_status").html(Main.ucfirst(courierStaff.user.status));

            $("#full_name").val(courierStaff.user.fullName);
            $("#email").val(courierStaff.user.emailAddress);
            $("#mobile").val(courierStaff.user.mobileNumber);
            $("#gender").val(courierStaff.user.gender);
            $("#street").attr('data-id', courierStaff.user.addresses[0].id).val(courierStaff.user.addresses[0].street);
            $("#city").val(courierStaff.user.addresses[0].city);
            $("#state").val(courierStaff.user.addresses[0].state);
            $("#country").val(courierStaff.user.addresses[0].country);
            $("#account_no").val(courierStaff.bankAccountNumber);
            $("#vehicle_type").val(courierStaff.vehicleType);
            $("#vehicle_no").val(courierStaff.vehicleNumber);
            $("#license_no").val(courierStaff.licenseNumber);
            $("#status").val(courierStaff.user.status);

            $("#gender").selectpicker();
            $("#vehicle_type").selectpicker();
            $("#status").selectpicker();

            if(courier_profile == undefined) {

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

        }
        callback.loaderDiv = "body";
        callback.requestType = "GET";
        var headers = {};
        headers.id = id;
        if(courier_profile == undefined)
            Main.request('/accountant/get_dboy', {}, callback, headers);
        else
            callback('', courier_profile);
    }

    CourierStaff.loadEditCourierStaff = function () {

        $.validator.addMethod("mobileNumber", function (value, element, arg) {
            return this.optional(element) || /[0-9]+$/.test(value);
        }, "Only numbers are allowed.");

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

        $.validator.addMethod("imageRequired", function(value, element, arg){
            var result = true;
            var container = $(element).prev('.drop_zone');
            if($('img', container).attr('src') == undefined || $('img', container).attr('src') == "") {
                result = false;
                container.addClass('error');
            } else {
                container.removeClass('error');
            }
            return result;
        }, "Only numbers are allowed.");

        $.validator.setDefaults({
            errorPlacement: function (error, element) {
                $('#error_container').html(error);
            }
        });

        $('#gender, #vehicle_type').change(function () {
            $(this).valid();
        });

        Image.dropZone('#profile_image_input', '#profile_image');
        $("#profile_image").addClass('disabled');
        $('.edit_btn').click(function () {
            $(".editable").removeClass('hidden');
            $(".none_editable").addClass('hidden');
            $("#profile_image").removeClass('disabled');

            $('#courier_boy_form').validate();
            $('#profile_image_input').rules('add', {imageRequired: true});
            $('#full_name').rules('add', {required: true});
            $('#email').rules('add', {email: true});
            $('#password').rules('add', {minlength: 6});
            $('#mobile').rules('add', {required: true, mobileNumber: true, minlength: 10, maxlength: 10});
            $('#gender').rules('add', {notEqual: 0});
            $('#street').rules('add', {required: true});
            $('#city').rules('add', {required: true});
            $('#state').rules('add', {required: true});
            $('#country').rules('add', {required: true});
            $('#vehicle_type').rules('add', {notEqual: 0});

        });

        $('.cancel_btn').click(function () {
            $(".none_editable").removeClass('hidden');
            $(".editable").addClass('hidden');
            $("#profile_image").addClass('disabled');
            $("#password").val('');
            CourierStaff.getCourierStaffProfile(courierProfile);
        });

        $('.save_btn').click(function () {
            if ($('#courier_boy_form').valid()) {

                var button1 = function() {

                    var data = {};
                    var user = {};
                    var address = {};

                    address.id = $('#street').attr('data-id');
                    address.street = $('#street').val();
                    address.city = $('#city').val();
                    address.state = $('#state').val();
                    address.country = $('#country').val();
                    address.countryCode = "00977";

                    user.fullName = $('#full_name').val();
                    user.emailAddress = $('#email').val();
                    user.mobileNumber = $('#mobile').val();
                    user.gender = $('#gender').val();
                    user.status = $("#status").val();
                    elem_image = $('#profile_image img');

                    if (elem_image.attr('data-new') == 'true') {
                        user.profileImage = elem_image.attr('src');
                    }
                    user.addresses = [address];

                    data.id = Main.getURLvalue(3);
                    data.bankAccountNumber = $('#account_no').val();
                    data.vehicleType = $('#vehicle_type').val();
                    data.vehicleNumber = $('#vehicle_no').val();
                    data.licenseNumber = $('#license_no').val();
                    data.user = user;

                    var headers = {};
                    headers.username = $('#mobile').val();
                    headers.password = $('#password').val();
                    CourierStaff.editCourierStaff(data, headers);
                };

                button1.text = "Yes";
                var button2 = "No";

                var buttons = [button1, button2];
                Main.popDialog('', 'Are you sure you want to update shopper?', buttons);
                return false;
            }
        });

    }

    CourierStaff.editCourierStaff = function (data, headers) {
        $("a.save_btn").attr("disabled", true);
        var callback = function (status, data) {
            $("a.save_btn").removeAttr("disabled");

            Main.popDialog('', data.message, function() {
                if (data.success == true) {
                    $(".none_editable").removeClass('hidden');
                    $(".editable").addClass('hidden');
                    $("#profile_image").addClass('disabled');
                    CourierStaff.getCourierStaffProfile();
                }
            });
        };
        callback.loaderDiv = "body";
        callback.requestType = 'PUT';
        Main.request('/organizer/update_dboy', data, callback, headers);
    };

})(jQuery);