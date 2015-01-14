if(typeof(Merchant) == "undefined") var Merchant = {};

(function ($){

    Merchant.signUp = function(data, headers) {

        $("button[type='submit']","#signup_form").attr("disabled", true);

        var callback = function (status, data) {
            $("button[type='submit']","#signup_form").removeAttr("disabled");

            if (data.success == true) {
                alert(data.message);
                $('#signup_form').trigger('reset');
                $('#drop_zone').html('');
                $('#signup_form .nav li').eq(0).children('a').tab('show');
                $("#signup_form .nav li").removeClass('passed_tab');
                $("#signup_form .nav li").eq(0).nextAll('li').addClass('disabled_tab');
                $('#modal_signup').modal('hide');
                deleteMarkers();
            } else {
                alert(data.message);
            }
        };

        callback.loaderDiv = "#modal_signup .modal-dialog";

        Main.request('/anon/save_merchant', data, callback, headers);

    };

    Merchant.loadMerchant = function() {

        var callback = function (status, data) {
            console.log(data);
            if (data.success == true) {

                if(!initialized) initialize('readonly', false); else google.maps.event.trigger(map, 'resize');
                var merchant = data.params.merchant;

                $('#brand_logo').html('<img src="' + merchant.businessLogo + '" class="img-responsive" data-new="true" />')
                $('.business_name').html(merchant.businessTitle);
                $('.contact_person').html(merchant.user.fullName);
                $('#business_name').val(merchant.businessTitle);
                $('#url').val(merchant.website);
                $('#contact_person').val(merchant.user.fullName);
                $('#contact_email').val(merchant.user.emailAddress);
                $('#contact_no').val(merchant.user.mobileNumber);
                $('#registration_no').val(merchant.businessTitle);
                $('#vat').val(merchant.companyRegistrationNo);

                var store = merchant.user.addresses[0];
                store.latitude = "27.710668";
                store.longitude = "85.31727799999999";

                var location = latLngToLocation(store.latitude, store.longitude);;
                var geoPointData = {};
                geoPointData.name = merchant.businessTitle;
                geoPointData.street = store.street;
                geoPointData.city = store.city;
                geoPointData.state = store.state;
                geoPointData.country = store.country;
                geoPointData.latitude = store.latitude;
                geoPointData.longitude = store.longitude;
                location.geoPointData = geoPointData;
                addMarker(location);


            } else {
                alert(data.message);
            }
        };

        callback.loaderDiv = "body";
        callback.requestType = "GET";

        Main.request('/merchant/get_merchant', {}, callback, {merchantId: Main.getURLvalue(2)});

    };

})(jQuery);