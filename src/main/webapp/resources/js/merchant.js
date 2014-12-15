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

})(jQuery);