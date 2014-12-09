if(typeof(CourierBoy) == "undefined") var CourierBoy = {};

(function ($){

    CourierBoy.loadAddCourierBoy = function() {

        $.validator.addMethod("mobileNumber", function(value, element, arg){
            return this.optional(element) || /[0-9]+$/.test(value);
        }, "Only numbers are allowed.");

        $.validator.setDefaults({
            errorPlacement : function(error, element){
                $('#error_container').html(error);
            }
        });

        $('#gender').selectpicker();
        $('#vehicle_type').selectpicker();

        $('#add_courier_boy').click(function(){
            $('#courier_boy_form').submit();
        });

        $('#courier_boy_form').validate({
            submitHandler: function() {

                var address = arrGeoPoints[Object.keys(arrGeoPoints)[0]];
                if(address == undefined) {
                    alert("Please add a marker to set address.");
                } else if(address.name == "" || address.street == "" || address.city == "" || address.state == "") {
                    alert("Please fill up all fields of info window and save.");
                } else {

                    var chk_confirm = confirm('Are you sure you want to add delivery boy?');
                    if(!chk_confirm) return false;

                    var data = {};
                    var user = {};

                    user.fullName = $('#contact_person').val();
                    user.street = address.street;
                    user.city = address.city;
                    user.state = address.state;
                    user.country = address.country;
                    user.countryCode = "00977";
                    user.mobileNumber = $('#contact_no').val();
                    user.mobileVerificationStatus = "true";
                    user.emailAddress = $('#contact_email').val();
                    user.profileImage = "";
                    user.blacklistStatus = "false";
                    user.verifiedStatus = "false";
                    user.token = "token";
                    user.subscribeNewsletter = "true";
                    user.role = {role: "ROLE_MERCHANT"};

                    data.type = "CORPORATE";
                    data.partnershipStatus = "true";
                    data.commissionPercentage = "0";
                    data.website = $('#url').val();
                    data.agreementDetail = "";
                    data.businessTitle = $('#business_name').val();
                    data.businessLogo = $('#drop_zone img').attr('src');
                    data.companyRegistrationNo = $('#registration_no').val();
                    data.vatNo = $('#vat').val();
                    data.user = user;

                    var headers = {};
                    headers.username = $('#contact_email').val();

                    Merchant.signUp(data, headers);

                }
                return false;

            }
        });
        $('#full_name').rules('add', {required: true});
        $('#email').rules('add', {required: true, email: true});
        $('#mobile').rules('add', {required: true, mobileNumber: true, minlength: 10, maxlength: 10});
        $('#vehicle_type').rules('add', {required: true});
        $('#vehicle_no').rules('add', {required: true});

    };

})(jQuery);