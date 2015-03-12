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

        },
        loadUserManagement: function() {

            Admin.getManagers();

            $('#status').selectpicker();

            var accountsLoad = true;

            $('.main_tabs ul li a[href="#accountants"]').on('show.bs.tab', function (e) {
                if(accountsLoad) {
                    Admin.getAccountants();
                    accountsLoad = false;
                }
            });

            $('.main_tabs button').click(function(){
                $('#modal_user .modal-header').removeAttr('data-id').attr('data-role', $(this).attr('data-role')).html('Create ' + $(this).attr('data-user'));
                $('#modal_user button[type="submit"]').html('Create');
                $('#modal_user .edit_group').addClass('hidden');
            });

            $('.action_links a[data-target="#modal_user"]').live('click', function(){
                var parent = $(this).parents('tr').eq(0);

                $('#name').val($('td', parent).eq(1).html());
                $('#email').val($('td', parent).eq(2).html());
                $('#phone').val($('td', parent).eq(3).html());
                $('#status').val($('td', parent).eq(4).html().toUpperCase());
                $('#status').selectpicker('refresh');

                $('#modal_user .modal-header').attr('data-role', $('.main_tabs .active button').attr('data-role')).attr('data-id', $(this).attr('data-id')).html('Update ' + $(this).attr('data-user'));
                $('#modal_user button[type="submit"]').html('Update');
                $('#modal_user .edit_group').removeClass('hidden');

            });

            $.validator.setDefaults({
                errorPlacement: function (error, element) {
                    $('#error_container').html(error);
                },
                ignore: []
            });

            $('#form_user').validate({
                submitHandler: function (form) {

                    var chk_confirm = confirm('Are you sure you want to ' + $('#modal_user .modal-header').html().toLowerCase() + '?');
                    if (!chk_confirm) return false;

                    var data = {};

                    data.fullName = $('#name').val();
                    data.emailAddress = $('#email').val();
                    data.mobileNumber = $('#phone').val();

                    var header = {};
                    data.role = {role: $('#modal_user .modal-header').attr('data-role')};
                    if($('#modal_user .modal-header').attr('data-id') == undefined) {
                        header.username = $('#email').val();
                    } else {
                        header.id = $('#modal_user .modal-header').attr('data-id');
                        data.status = $('#status').val();
                    }

                    Admin.saveUser(data, header);

                    return false;

                }
            });
            $('#name').rules('add', {required: true});
            $('#email').rules('add', {required: true, email: true});
            $('#phone').rules('add', {required: true, minlength: 10, maxlength: 10});

            $('#modal_user').on('hidden.bs.modal', function(){
                $('#form_user')[0].reset();
                $('#status').selectpicker('refresh');
            });

        },
        getManagers: function() {

            var callback = function (status, data) {

                console.log(data);
                if (!data.success) {
                    alert(data.message);
                    return;
                }
                var managers = data.params.managers;
                var tdata = [];

                for (var i = 0; i < managers.length; i++) {
                    var manager = managers[i];
                    var id = manager.id;

                    var actions = '<div class="action_links">';
//                    actions += '<a href="/delivr/merchant/profile/1">Profile</a>';
                    actions += '<a href="#" data-target="#modal_user" data-toggle="modal" data-user="Manager" data-id="' + id + '">Edit</a>';
                    actions += '</div>';

                    var row = [id, manager.fullName, manager.emailAddress, manager.mobileNumber, manager.status == undefined ? 'Unverified' : Main.ucfirst(manager.status), actions];
                    tdata.push(row);
                }

                Main.createDataTable("#manager_table", tdata);

                $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

            };

            callback.loaderDiv = "body";
            callback.requestType = "GET";

            Main.request('/admin/get_managers', {}, callback);

        },
        getAccountants: function() {

            var callback = function (status, data) {

                console.log(data);
                if (!data.success) {
                    alert(data.message);
                    return;
                }

                var accountants = data.params.accountants;
                var tdata = [];

                for (var i = 0; i < accountants.length; i++) {
                    var accountant = accountants[i];
                    var id = accountant.id;

                    var actions = '<div class="action_links">';
//                    actions += '<a href="/delivr/merchant/profile/1">Profile</a>';
                    actions += '<a href="#" data-target="#modal_user" data-toggle="modal" data-user="Accountant" data-id="' + id + '">Edit</a>';
                    actions += '</div>';

                    var row = [id, accountant.fullName, accountant.emailAddress, accountant.mobileNumber, accountant.status == undefined ? 'Unverified' : Main.ucfirst(accountant.status), actions];
                    tdata.push(row);
                }

                Main.createDataTable("#accountant_table", tdata);

                $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

            };

            callback.loaderDiv = "body";
            callback.requestType = "GET";

            Main.request('/admin/get_accountants', {}, callback);

        },
        saveUser: function(params, headers) {

            $("button[type='submit']").attr("disabled", true);

            var callback = function (status, data) {
                $("button[type='submit']").removeAttr("disabled");

                alert(data.message);
                if (data.success == true) {
                    $('#modal_user').modal('hide');
                    if(params.role.role == 'ROLE_MANAGER')
                        Admin.getManagers();
                    else
                        Admin.getAccountants();
                }
            };

            callback.loaderDiv = ".modal-dialog";

            var url = '/admin/save_user'
            if(headers.id != undefined) url = '/admin/update_user';

            Main.request(url, params, callback, headers);

        }

    };

}();