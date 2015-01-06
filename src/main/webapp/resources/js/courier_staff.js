if (typeof(CourierStaff) == "undefined") var CourierStaff = {};

(function ($) {

    CourierStaff.loadAddCourierStaff = function () {

        Image.dropZone('#image_input');
        $('#country').val(Main.country);

        $('#ar_lock').change(function (e) {
            jcrop_api.setOptions(this.checked ? { aspectRatio: chk_w / chk_h } : { aspectRatio: 0 });
            jcrop_api.focus();
        });

        $('#apply_preview').click(function (e) {
            alert('test');
            html2canvas($('.preview-container'), {
                onrendered: function (canvas) {
                    var strImage = canvas.toDataURL('image/jpeg');
                    console.log(strImage);
                    $('#drop_zone').addClass('image_selected').removeClass('error').html('<img src="' + strImage + '" style="height: 100%;" />');
                    $('#crop_img_modal').modal('hide');
                }
            });
        });

        $('#cancel_preview').click(function (e) {
            $('#crop_img_modal').modal('hide');
        });

        $('#crop_img_modal').on('show.bs.modal', function (e) {
            $('#ar_lock').prop('checked', true);
        });

        $('#crop_img_modal').on('hidden.bs.modal', function (e) {
            $('#crop_img_modal img').attr('src', '');
        });

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


        $.validator.addMethod("imageRequired", function (value, element, arg) {
            var result = true;
            if ($('#drop_zone img').attr('src') == undefined || $('#drop_zone img').attr('src') == "") {
                result = false;
                $('#drop_zone').addClass('error');
            } else {
                $('#drop_zone').removeClass('error');
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

                var chk_confirm = confirm('Are you sure you want to add delivery boy?');
                if (!chk_confirm) return false;

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
                user.profileImage = $('#drop_zone img').attr('src');
                user.addresses = [address];

                data.vehicleType = $('#vehicle_type').val();
                data.vehicleNumber = $('#vehicle_no').val();
                data.licenseNumber = $('#license_no').val();
                data.user = user;

                var headers = {};
                headers.username = $('#mobile').val();
                headers.password = $('#password').val();

                CourierStaff.addCourierStaff(data, headers);

                return false;

            }
        });
        $('#image_input').rules('add', {imageRequired: true});
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

            if (data.success == true) {
                alert(data.message);
                $('#courier_boy_form').trigger('reset');
                $('#drop_zone').html('');
            } else {
                alert(data.message);
            }
        };

        callback.loaderDiv = ".main_content";

        Main.request('/organizer/save_dboy', data, callback, headers);

    };


    CourierStaff.getCourierStaffProfile = function () {
        var id = Main.getURLvalue(3);
        noEditInitialise();
        //if(!noEditInitialised) noEditInitialise(); else google.maps.event.trigger(map, 'resize');
        var callback = function (status, data) {
            if (!data.success) {
                alert(data.message);
                return;
            }
            var courierStaff = data.params.deliveryBoy;

            $(".profile_header_image #drop_zone").html("<img id='profileImage' src='" + courierStaff.user.profileImage + "' class='img-responsive'>");

            $(".profile_header_info .name").html(courierStaff.user.fullName);
            $(".profile_header_info .title").html(courierStaff.availabilityStatus);

            $(".info_block.name .info_display").html(courierStaff.user.fullName);
            $(".info_block.email .info_display").html(courierStaff.user.email);
            $(".info_block.mobile .info_display").html(courierStaff.user.mobileNumber);
            $(".info_block.gender .info_display").html(courierStaff.user.gender);
            $(".info_block.street .info_display").html(courierStaff.user.addresses[0].street);
            $(".info_block.city .info_display").html(courierStaff.user.addresses[0].city);
            $(".info_block.state .info_display").html(courierStaff.user.addresses[0].state);
            $(".info_block.country .info_display").html(courierStaff.user.addresses[0].country);
            $(".info_block.vehicle_type .info_display").html(courierStaff.vehicleType);
            $(".info_block.vehicle_no .info_display").html(courierStaff.vehicleNumber);
            $(".info_block.license_no .info_display").html(courierStaff.licenseNumber);

            $("#id").val(courierStaff.id);
            $("#fullName").val(courierStaff.user.fullName);
            $("#email").val(courierStaff.user.email);
            $("#mobile").val(courierStaff.user.mobileNumber);
            $("#password").val(courierStaff.user.password);
            $("#gender option[value=" + courierStaff.user.gender + "]").attr("selected", "selected");
            $("#street").val(courierStaff.user.addresses[0].street);
            $("#city").val(courierStaff.user.addresses[0].city);
            $("#state").val(courierStaff.user.addresses[0].state);
            $("#country").val(courierStaff.user.addresses[0].country);
            $("#vehicleType option[value=" + courierStaff.vehicleType + "]").attr("selected", "selected");
            $("#status option[value=" + courierStaff.verifiedStatus + "]").attr("selected", "selected");
            $("#vehicleNo").val(courierStaff.vehicleNumber);
            $("#licenseNo").val(courierStaff.licenseNumber);


            var srclatlng = new google.maps.LatLng(courierStaff.latitude, courierStaff.longitude);
            var destlatlang = new google.maps.LatLng("27.6891424", "85.324561");
            map.setZoom(12);
            map.setCenter(srclatlng);

            new google.maps.Marker({
                position: srclatlng,
                map: map
                //draggable: true
            });

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
                if (status == google.maps.DirectionsStatus.OK) {
                    directionsDisplay.setDirections(result);
                } else {
                    alert("Directions was not successful because " + status);
                }
            });

            directionsDisplay.setMap(map);

        }
        callback.loaderDiv = ".view_courier_boy_map";
        callback.requestType = "GET";
        var headers = {};
        headers.id = id;
        Main.request('/organizer/get_dboy', {}, callback, headers);
    }

    CourierStaff.loadEditCourierStaff = function () {
        $(".editable").addClass('hidden');

        $('.edit_btn').click(function () {
            $(".editable").removeClass('hidden');
            $(".none_editable").addClass('hidden');
            Image.dropZone('#image_input');
        });

        $('.cancel_btn').click(function () {
            $(".none_editable").removeClass('hidden');
            $(".editable").addClass('hidden');
        });

        /*$('.save_btn').click(function(){
         $(".none_editable").removeClass('hidden');
         $(".editable").addClass('hidden');
         });*/
        $("#vehicleType").selectpicker();
        $("#gender").selectpicker();
        $("#status").selectpicker();

        $('#ar_lock').change(function (e) {
            jcrop_api.setOptions(this.checked ? { aspectRatio: chk_w / chk_h } : { aspectRatio: 0 });
            jcrop_api.focus();
        });

        $('#apply_preview').click(function (e) {
            alert('test');
            html2canvas($('.preview-container'), {
                onrendered: function (canvas) {
                    var strImage = canvas.toDataURL('image/jpeg');
                    console.log(strImage);
                    $('#drop_zone').addClass('image_selected').removeClass('error').html('<img src="' + strImage + '" style="height: 100%;" />');
                    $('#crop_img_modal').modal('hide');
                }
            });
        });

        $('#cancel_preview').click(function (e) {
            $('#crop_img_modal').modal('hide');
        });

        $('#crop_img_modal').on('show.bs.modal', function (e) {
            $('#ar_lock').prop('checked', true);
        });

        $('#crop_img_modal').on('hidden.bs.modal', function (e) {
            $('#crop_img_modal img').attr('src', '');
        });

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

        $.validator.addMethod("imageRequired", function (value, element, arg) {
            var result = true;
            if ($('#drop_zone img').attr('src') == undefined || $('#drop_zone img').attr('src') == "") {
                result = false;
                $('#drop_zone').addClass('error');
            } else {
                $('#drop_zone').removeClass('error');
            }
            return result;
        }, "Only numbers are allowed.");

        $.validator.setDefaults({
            errorPlacement: function (error, element) {
                $('#error_container').html(error);
            },
            ignore: []
        });

        $('#gender, #vehicle_type').change(function () {
            $(this).valid();
        });


        $('#courier_boy_form').validate();
        $('.save_btn').click(function () {
            var chk_confirm = confirm('Are you sure you want to edit delivery boy?');
            if (!chk_confirm) return false;

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
            user.verifiedStatus = $("#status").val();
            image_src = $('#drop_zone img').attr('src');

            if (image_src.indexOf("https://") > -1) {
                user.profileImage = '';
            } else {
                user.profileImage = $('#drop_zone img').attr('src');
            }
            console.log(user.profileImage);
            user.addresses = [address];

            data.id = $('#id').val();
            data.vehicleType = $('#vehicleType').val();
            data.vehicleNumber = $('#vehicleNo').val();
            data.licenseNumber = $('#licenseNo').val();
            data.user = user;

            var headers = {};
            headers.username = $('#mobile').val();
            headers.password = $('#password').val();
            CourierStaff.editCourierStaff(data, headers);
            console.log(data);
            return false;
        });
        $('#imageInput').rules('add', {imageRequired: true});
        $('#fullName').rules('add', {required: true});
        $('#email').rules('add', {email: true});
        $('#mobile').rules('add', {required: true, mobileNumber: true, minlength: 10, maxlength: 10});
        $('#password').rules('add', {required: true, minlength: 6});
        $('#gender').rules('add', {notEqual: 0});
        $('#street').rules('add', {required: true});
        $('#city').rules('add', {required: true});
        $('#state').rules('add', {required: true});
        $('#country').rules('add', {required: true});
        $('#vehicleType').rules('add', {notEqual: 0});

    }

    CourierStaff.editCourierStaff = function (data, headers) {
        $("a.save_btn").attr("disabled", true);
        var callback = function (status, data) {
            $("a.save_btn").removeAttr("disabled");

            if (data.success == true) {
                $(".none_editable").removeClass('hidden');
                $(".editable").addClass('hidden');
                CourierStaff.getCourierStaffProfile();
            } else {
                alert(data.message);
            }
        };
        callback.loaderDiv = ".main_content";
        callback.requestType = 'PUT';
        Main.request('/organizer/update_dboy', data, callback, headers);
    };

})(jQuery);