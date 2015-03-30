$(document).ready(function(){

    if(window.location.hostname == "test.idelivr.com") {

        $('#login_form #email').val('admin@yetistep.com');
        $('#login_form #password').val('password');

    }

    Image.dropZone('#logo_input');

    $.validator.addMethod("contactNumber", function(value, element, arg){
        return this.optional(element) || /[0-9+-]+$/.test(value);
    }, "Only +, - and numbers are allowed.");

    $.validator.addMethod("checkRequired", function(value, element, arg){
        return $(element).prop("checked");
    }, "Please check the checkbox");

    $.validator.setDefaults({
        errorPlacement : function(error, element){
            $('#error_container').html(error);
        }
    });

    $('#login_form').validate({
        submitHandler: function() {
            var data = {username: $('#email').val(), password: $('#password').val(), stringify: false};
            Main.doLogin(data);
            return false;
        }/*,
        wrapper : 'div'*/
    });
    $('#email').rules('add', {required: true, email: true, messages : {required: "Email is required."}});
    $('#password').rules('add', {required: true, minlength: 6, messages : {required: "Password is required."}});

    $('#signup_form').validate({
        submitHandler: function() {

            var address = arrGeoPoints[Object.keys(arrGeoPoints)[0]];
            if(address == undefined) {
                alert("Please add a marker to set address.");
            } else if(address.name == "" || address.street == "" || address.city == "" || address.state == "") {
                alert("Please fill up all fields of info window and save.");
            } else {

                var chk_confirm = confirm('Are you sure you want to Sign up?');
                if(!chk_confirm) return false;

                var data = {};
                var user = {};
                var objAddress = {};

                objAddress.street = address.street;
                objAddress.city = address.city;
                objAddress.state = address.state;
                objAddress.country = address.country;
                objAddress.latitude = address.latitude;
                objAddress.longitude = address.longitude;
                objAddress.countryCode = "00977";

                data.website = $('#url').val();
                data.businessTitle = $('#business_name').val();
                data.businessLogo = $('#drop_zone img').attr('src');
                data.companyRegistrationNo = $('#registration_no').val();
                data.vatNo = $('#vat').val();
                data.panNo = $('#pan').val();

                user.status = 'ACTIVE';
                user.fullName = $('#contact_person').val();
                user.mobileNumber = $('#contact_no').val();
                user.emailAddress = $('#contact_email').val();
                user.addresses = [objAddress];

                data.user = user;

                var headers = {};
                headers.username = $('#contact_email').val();

                Merchant.signUp(data, headers);

            }
            return false;

        }
    });
    $('#business_name').rules('add', {required: true, messages : {required: "Business name is required."}});
    $('#url').rules('add', {required: true, url: true, messages : {required: "URL is required."}});
    $('#contact_person').rules('add', {required: true, messages : {required: "Contact person is required."}});
    $('#contact_email').rules('add', {required: true, email: true, messages : {required: "Contact email is required."}});
    $('#contact_no').rules('add', {required: true, contactNumber: true, messages : {required: "Contact number is required."}});
    $('#accept_terms').rules('add', {checkRequired: true});
//    $('#registration_no').rules('add', {required: true, messages : {required: "Registration number is required."}});
//    $('#vat').rules('add', {required: true, messages : {required: "VAT is required."}});

    $('#modal_login').on('shown.bs.modal', function (e) {
        $('#email').focus();
    });
    $('#modal_signup').on('shown.bs.modal', function (e) {
        $('#business_name').focus();
        $('body').addClass('modal-open');
    });
    $('#modal_login').on('hide.bs.modal', function (e) {
        $('#login_form').validate().resetForm();
    });
    $('#modal_signup').on('hide.bs.modal', function (e) {
        $('#signup_form').validate().resetForm();
    });

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
        $("body").addClass('modal-open');
    });

    $('#signup_form .nav a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        $(this).parent().removeClass('disabled_tab');
        $(this).parent().prevAll().addClass('passed_tab');
        $(this).parent().nextAll().removeClass('passed_tab');
        if($(this).attr('href') == "#step_3")
            $("button[type='submit']","#signup_form").html('Sign up');
        else
            $("button[type='submit']","#signup_form").html('Next');
    });

    $('#signup_form .nav a[href="#step_3"]').on('shown.bs.tab', function (e) {
        if(!initialized) initialize(); else google.maps.event.trigger(map, 'resize');
    });
    $('button.next').click(function(e){
        var tabelem = $('#signup_form .nav .active').next('li').children('a');
        if(tabelem.length > 0 ) {
            e.stopPropagation();
            e.preventDefault();
            tabelem.tab('show');
        }
    });

    $('#signup_form .nav a[data-toggle="tab"]').on('show.bs.tab', function(element) {
        var id = element.target.hash;
        var tab_parent = $('#signup_form .nav a[href="' + id + '"]').parent('li');
        var index = tab_parent.index();
        var bool_0 = true;
        var bool_1 = true;

        if(index > 0) {
            if(!$('#business_name').valid()) {
                bool_0 = false;
            }
            if($('#drop_zone img').attr('src') == undefined || $('#drop_zone img').attr('src') == "") {
                bool_0 = false;
                $('#drop_zone').addClass('error');
            } else {
                $('#drop_zone').removeClass('error');
            }
            if(!$('#url').valid()) {
                bool_0 = false;
            }
        }

        if(index > 1) {
            if(!$('#contact_person').valid()) {
                bool_1 = false;
            }
            if(!$('#contact_email').valid()) {
                bool_1 = false;
            }
            if(!$('#contact_no').valid()) {
                bool_1 = false;
            }
            if(!$('#registration_no').valid()) {
                bool_1 = false;
            }
            if(!$('#vat').valid()) {
                bool_1 = false;
            }
        }

        if(bool_0 == false) {
            $('#signup_form .nav li').eq(0).children('a').tab('show');
            return false;
        } else if(bool_1 == false) {
            $('#signup_form .nav li').eq(1).children('a').tab('show');
            return false;
        }
    });


});