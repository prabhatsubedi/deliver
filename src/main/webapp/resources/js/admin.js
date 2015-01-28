/**
 * Created by Lunek on 1/28/2015.
 */


var Admin = function() {


    return {

        preferences: undefined,
        loadSettings : function(preferences){

            var callback = function(status, data) {

                console.log(data);
                var preferences = Admin.preferences = data.params.preferences;
                var form_fields = '';
                for(var i = 0; i < preferences.length; i++) {
                    var pref = preferences[i];
                    var elem = $('.form_field_template').clone();
                    var label = pref.prefKey;
                    var arr_label = label.split('_');
                    var new_arr_label = [];
                    for(var j in arr_label) {
                        new_arr_label.push(Main.ucfirst(arr_label[j]));
                    }
                    $('label', elem).attr('for', pref.prefKey).html(new_arr_label.join(' '));
                    $('.info_display', elem).html(pref.value);
                    $('input.form-control', elem).attr({id: pref.prefKey, name: pref.prefKey, value: pref.value});
                    form_fields += elem.html();
                }
                $('.display_settings').html(form_fields);

/*                $('#currency').val(settings.CURRENCY);
                $('#system_vat').val(settings.DELIVERY_FEE_VAT);
                $('#signup_reward').val(settings.abc);
                $('#reward_first_user').val(settings.abc);
                $('#reward_second_user').val(settings.abc);
                $('#referral_limit').val(settings.abc);
                $('#admin_email').val(settings.abc);
                $('#account_email').val(settings.abc);
                $('#support_email').val(settings.abc);
                $('#company_name').val(settings.abc);
                $('#address').val(settings.abc);
                $('#contact_no').val(settings.abc);
                $('#email').val(settings.abc);
                $('#url').val(settings.abc);
                $('#vat_no').val(settings.MERCHANT_VAT);
                $('#reg_no').val(settings.abc);
                $('#support_phone').val(settings.abc);
                $('#web_version').val(settings.abc);
                $('#android_version').val(settings.abc);*/

            };
            if(preferences == undefined) {
                callback.requestType = "GET";
                callback.loaderDiv = "body";
                Main.request('/admin/get_preferences', {}, callback);
            } else {
                callback('', {params: {preferences: preferences}});
            }

        },
        loadEditSettings: function() {

            $('.edit_btn').click(function () {
                $(".editable").removeClass('hidden');
                $(".none_editable").addClass('hidden');
            });

            $('.cancel_btn').click(function () {
                $(".none_editable").removeClass('hidden');
                $(".editable").addClass('hidden');
                Admin.loadSettings(Admin.preferences);
            });

            $('.save_btn').click(function () {
                var chk_confirm = confirm('Are you sure you want to update Settings?');
                if (!chk_confirm) return false;
                var preferences = [];
                $('.display_settings input.form-control').each(function(){
                    var data = {prefKey: $(this).attr('id'), value: $(this).val()};
                    preferences.push(data);
                });
                Admin.updateSettings({preferences: preferences});
            });

        },
        updateSettings: function(updatedData) {

            var callback = function(status, data) {

                alert(data.message);
                if(data.success) {
                    $(".none_editable").removeClass('hidden');
                    $(".editable").addClass('hidden');
                    Admin.loadSettings();
                }

            };
            callback.loaderDiv = "body";
            Main.request('/admin/update_preferences', updatedData, callback);

        }

    };

}();