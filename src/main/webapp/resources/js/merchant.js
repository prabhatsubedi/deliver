if(typeof(Merchant) == "undefined") var Merchant = {};

var merchantProfile;

(function ($){

    Merchant.signUp = function(data, headers) {

        $("button[type='submit']","#signup_form").attr("disabled", true);

        var callback = function (status, data) {
            $("button[type='submit']","#signup_form").removeAttr("disabled");

            Main.popDialog('', data.message, function () {

                if (data.success == true) {
                    $('#signup_form').trigger('reset');
                    $('#drop_zone').html('');
                    $('#signup_form .nav li').eq(0).children('a').tab('show');
                    $("#signup_form .nav li").removeClass('passed_tab');
                    $("#signup_form .nav li").eq(0).nextAll('li').addClass('disabled_tab');
                    $('#modal_signup').modal('hide');
                    deleteMarkers();
                } else {
                    if(data.code == "VLD026") {
                        $('#signup_form .nav li').eq(1).children('a').tab('show');
                        $('#contact_email').addClass('error');
                    }
                }

            });
        };

        callback.loaderDiv = "#modal_signup .modal-dialog";

        Main.request('/anon/save_merchant', data, callback, headers);

    };

    Merchant.loadMerchant = function(merchant_profile) {

        var callback = function (status, data) {
            console.log(data);
            merchantProfile = data;
            if (data.success == true) {

                if(!initialized) initialize(); else google.maps.event.trigger(map, 'resize');
                var merchant = data.params.merchant;

                $('#brand_logo').html('<img src="' + merchant.businessLogo + '" class="img-responsive" style="height: 100%;" />')
                $('.business_name').html(merchant.businessTitle);
                $('.partner_status').html(merchant.partnershipStatus ? 'PARTNER' : 'NON-PARTNER');

                if(merchantRole == true) {
                    $('.val_partnership, .val_commission, .val_service_fee').removeClass('none_editable');
                    $('#partnership, #commission, #service_fee').parent().removeClass('editable');
                }

                $('.val_business_name').html(merchant.businessTitle);
                $('.val_url').html(merchant.website);
                $('.val_contact_person').html(merchant.user.fullName);
                $('.val_contact_email').html(merchant.user.emailAddress);
                $('.val_contact_no').html(merchant.user.mobileNumber);
                $('.val_registration_no').html(merchant.companyRegistrationNo);
                $('.val_vat').html(merchant.vatNo);
                $('.val_pan').html(merchant.panNo);
                $('.val_partnership').html(merchant.partnershipStatus ? 'Partner' : 'Non Partner');
                if(merchant.user.status == "ACTIVE" || merchant.user.status == "INACTIVE") {
                    $('.val_commission').html(merchant.commissionPercentage);
                    $('.val_service_fee').html(merchant.serviceFee);
                    $('.commission_group').removeClass('hidden');
                    $('.processing_group').removeClass('hidden');
                }
                $('.val_status').html(Main.ucfirst(merchant.user.status));

                $('#business_name').val(merchant.businessTitle);
                $('#url').val(merchant.website);
                $('#contact_person').val(merchant.user.fullName);
                $('#contact_email').val(merchant.user.emailAddress);
                $('#contact_no').val(merchant.user.mobileNumber);
                $('#registration_no').val(merchant.companyRegistrationNo);
                $('#vat').val(merchant.vatNo);
                $('#pan').val(merchant.panNo);
                $('#partnership').val("" + merchant.partnershipStatus);
                $('#commission').val(merchant.commissionPercentage);
                $('#service_fee').val(merchant.serviceFee);

                $('#partnership').selectpicker();

                var address = merchant.user.addresses[0];

                var location = latLngToLocation(address.latitude, address.longitude);
                var geoPointData = {};
                geoPointData.id = address.id;
                geoPointData.name = merchant.businessTitle;
                geoPointData.street = address.street;
                geoPointData.city = address.city;
                geoPointData.state = address.state;
                geoPointData.country = address.country;
                geoPointData.countryCode = address.countryCode;
                geoPointData.latitude = address.latitude;
                geoPointData.longitude = address.longitude;
                location.geoPointData = geoPointData;
                geoMerchantId = address.id;
                geoMerchantName = merchant.businessTitle;
                addMarker(location);
                disableMapEdit = true;

            } else {
                Main.popDialog('', data.message);
            }
        };

        callback.loaderDiv = "body";
        callback.requestType = "GET";

        if(merchant_profile == undefined)
            Main.request('/merchant/get_merchant', {}, callback, {merchantId: Main.getURLvalue(2)});
        else
            callback('', merchant_profile);

    };

    Merchant.loadEditMerchant = function() {

        $('.edit_btn').click(function () {
            $(".editable").removeClass('hidden');
            $(".none_editable").addClass('hidden');

            $.validator.addMethod("contactNumber", function(value, element, arg){
                return this.optional(element) || /[0-9+-]+$/.test(value);
            }, "Only +, - and numbers are allowed.");

            $.validator.setDefaults({
                errorPlacement : function(error, element){
                    $('#error_container').html(error);
                }
            });

            $('#merchant_form').validate();
            $('#url').rules('add', {required: true, url: true, messages : {required: "URL is required."}});
            $('#contact_person').rules('add', {required: true, messages : {required: "Contact person is required."}});
            $('#contact_no').rules('add', {required: true, contactNumber: true, messages : {required: "Contact number is required."}});
            $('#commission').rules('add', {required: true, digits: true, min: 0, max: 100});
            $('#service_fee').rules('add', {required: true, digits: true, min: 0});
            disableMapEdit = false;
            for(var i in markers) {
                markers[i].setDraggable(true);
            }

        });

        $('.cancel_btn').click(function () {
            $(".none_editable").removeClass('hidden');
            $(".editable").addClass('hidden');
            Merchant.loadMerchant(merchantProfile);
            disableMapEdit = true;
            for(var i in markers) {
                markers[i].setDraggable(false);
            }
        });

        $('.save_btn').click(function () {
            if ($('#merchant_form').valid()) {

/*                if($('#commission').val() == 0 && $('#service_fee').val() == 0) {
                    Main.popDialog('', 'Both commission percent and processing charge cannot be 0.');
                    return false;
                }*/

                var address = arrGeoPoints[Object.keys(arrGeoPoints)[0]];
                if(address == undefined) {
                    Main.popDialog('', "Please add a marker to set address.");
                } else if(address.name == "" || address.street == "" || address.city == "" || address.state == "") {
                    Main.popDialog('', "Please fill up all fields of info window and save.");
                } else {

                    var button1 = function() {

                        var data = {};
                        var user = {};
                        var objAddress = {};

                        objAddress.id = address.id;
                        objAddress.street = address.street;
                        objAddress.city = address.city;
                        objAddress.state = address.state;
                        objAddress.country = address.country;
                        objAddress.countryCode = "00977";
                        objAddress.latitude = address.latitude;
                        objAddress.longitude = address.longitude;

                        data.website = $('#url').val();
                        data.companyRegistrationNo = $('#registration_no').val();
                        data.vatNo = $('#vat').val();
                        data.panNo = $('#pan').val();
                        if(merchantRole != true) {
                            data.partnershipStatus = $('#partnership').val();
                            data.commissionPercentage = $('#commission').val();
                            data.serviceFee = $('#service_fee').val();
                        }

                        user.fullName = $('#contact_person').val();
                        user.mobileNumber = $('#contact_no').val();
                        user.addresses = [objAddress];

                        data.user = user;

                        Merchant.updateMerchant(data, {merchantId: Main.getURLvalue(2)});
                    };

                    button1.text = "Yes";
                    var button2 = "No";

                    var buttons = [button1, button2];
                    Main.popDialog('', 'Are you sure you want to update Merchant?', buttons);

                }

            }
        });

    }

    Merchant.updateMerchant = function (data, headers) {
        $("a.save_btn").attr("disabled", true);
        var callback = function (status, data) {
            $("a.save_btn").removeAttr("disabled");

            Main.popDialog('', data.message, function () {
                if (data.success == true) {
                    $(".none_editable").removeClass('hidden');
                    $(".editable").addClass('hidden');
                    Merchant.loadMerchant();
                    for(var i in markers) {
                        markers[i].setDraggable(false);
                    }
                }
            });
        };
        callback.loaderDiv = "body";
        Main.request('/merchant/update_merchant', data, callback, headers);
    };

})(jQuery);