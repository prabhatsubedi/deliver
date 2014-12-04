/**
 * Created by Lunek on 12/3/2014.
 */


if(typeof(Merchant) == "undefined") var Merchant = {};

(function ($){

    Merchant.signUp = function(data, headers) {

        $("button[type='submit']","#signup_form").attr("disabled", true);

        var callback = function (status, data) {
            $("button[type='submit']","#signup_form").removeAttr("disabled");

            if (data.success == true) {
                alert(data.message);
                $('#modal_signup').modal('hide');
                $('#signup_form').trigger('reset');
                $('#drop_zone').html('');
                deleteMarkers();
            } else {
                alert(data.message);
            }
        };

        callback.loaderDiv = "#modal_signup .modal-dialog";

        Main.request('/anon/save_merchant', data, callback, headers);

    };

})(jQuery);