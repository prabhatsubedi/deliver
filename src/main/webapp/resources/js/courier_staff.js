if(typeof(CourierStaff) == "undefined") var CourierStaff = {};

(function ($){

    CourierStaff.loadAddCourierStaff = function() {

        Image.dropZone('#image_input');
        $('#country').val(Main.country);

        $('#ar_lock').change(function(e) {
            jcrop_api.setOptions(this.checked ? { aspectRatio: chk_w/chk_h }: { aspectRatio: 0 });
            jcrop_api.focus();
        });

        $('#apply_preview').click(function(e){
            html2canvas($('.preview-container'), {
                onrendered: function(canvas) {
                    var strImage = canvas.toDataURL('image/jpeg');
                    $('#drop_zone').addClass('image_selected').removeClass('error').html('<img src="' + strImage + '" style="height: 100%;" />');
                    $('#crop_img_modal').modal('hide');
                }
            });
        });

        $('#cancel_preview').click(function(e){
            $('#crop_img_modal').modal('hide');
        });

        $('#crop_img_modal').on('show.bs.modal', function(e){
            $('#ar_lock').prop('checked', true);
        });

        $('#crop_img_modal').on('hidden.bs.modal', function(e){
            $('#crop_img_modal img').attr('src', '');
        });

        $.validator.addMethod("mobileNumber", function(value, element, arg){
            return this.optional(element) || /[0-9]+$/.test(value);
        }, "Only numbers are allowed.");

        $.validator.addMethod("notEqual", function(value, element, arg){
            var result = value != arg;
            if($(element).is('select')) {
                if(!result) {
                    $(element).siblings('.bootstrap-select').children('.form-control').addClass('error');
                } else {
                    $(element).siblings('.bootstrap-select').children('.form-control').removeClass('error');
                }
            }
            return result;
        }, "Please select any option.");


        $.validator.addMethod("imageRequired", function(value, element, arg){
            var result = true;
            if($('#drop_zone img').attr('src') == undefined || $('#drop_zone img').attr('src') == "") {
                result = false;
                $('#drop_zone').addClass('error');
            } else {
                $('#drop_zone').removeClass('error');
            }
            return result;
        }, "Only numbers are allowed.");


        $.validator.setDefaults({
            errorPlacement : function(error, element){
                $('#error_container').html(error);
            },
            ignore: []
        });

        $('#gender').selectpicker();
        $('#vehicle_type').selectpicker();
        $('#gender, #vehicle_type').change(function(){
            $(this).valid();
        });

        $('#courier_boy_form').validate({
            submitHandler: function() {

                var chk_confirm = confirm('Are you sure you want to add delivery boy?');
                if(!chk_confirm) return false;

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

    CourierStaff.addCourierStaff = function(data, headers) {

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

})(jQuery);