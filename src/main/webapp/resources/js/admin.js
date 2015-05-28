/**
 * Created by Lunek on 1/28/2015.
 */


var Admin = function() {


    return {

        secSettings: undefined,
        secId: undefined,
        prepSelect: function(prefTitle, prefKey, prefValue, options) {
            var elem = $('.form_select_template').clone();
            $('label', elem).attr('for', prefKey).html(prefTitle);
            $('.info_display', elem).html(options[prefValue]);
            $('select', elem).attr({id: prefKey, name: prefKey});
            for(var i in options) {
                $('select', elem).append('<option value="' + i + '" ' + (prefValue == i ? 'selected="selected"' : '') + '>' + options[i] + '</option>');
            }
            return elem;
        },
        loadSettings : function(secSettings){

            var callback = function(status, data) {

                Admin.secSettings = data;

                var form_sections = '';
                var sections = data.params.preferences.section;
                for(var j = 0; j < sections.length; j++) {

                    var section = sections[j];
                    var sectionName = section.section;
                    var preferences = section.preference;
                    if(preferences.length <= 0) continue;
                    var form_fields = '';

                    for(var i = 0; i < preferences.length; i++) {
                        var pref = preferences[i];
                        var prefKey = pref.prefKey;
                        var prefValue = pref.value;
                        var prefTitle = pref.prefTitle;
                        if(prefKey == 'ENABLE_FREE_REGISTER') {
                            var elem = Admin.prepSelect(prefTitle, prefKey, prefValue, {0: 'Disable', 1: 'Enable'});
                        } else if(prefKey == 'PROFIT_CHECK_FLAG') {
                            var elem = Admin.prepSelect(prefTitle, prefKey, prefValue, {0: 'No', 1: 'Yes'});
                        } else if(prefKey == 'AIR_OR_ACTUAL_DISTANCE_SWITCH') {
                            var elem = Admin.prepSelect(prefTitle, prefKey, prefValue, {0: 'Air Distance', 1: 'Actual Distance'});
                        } else if(prefKey == 'COMPANY_LOGO' || prefKey == 'DEFAULT_IMG_ITEM' || prefKey == 'DEFAULT_IMG_CATEGORY' || prefKey == 'DEFAULT_IMG_SEARCH' || prefKey == 'REFERRAL_FACEBOOK_IMG' || prefKey == 'LOGO_FOR_PDF_EMAIL') {
                            var elem = $('.image_template').clone();
                            var imgWidth = '';
                            var imgHeight = '';

                            if(prefKey == 'COMPANY_LOGO') {
                                imgWidth = '120';
                                imgHeight = '120';
                            } else if(prefKey == 'DEFAULT_IMG_ITEM') {
                                imgWidth = '400';
                                imgHeight = '400';
                            } else if(prefKey == 'DEFAULT_IMG_CATEGORY') {
                                imgWidth = '720';
                                imgHeight = '160';
                            }  else if(prefKey == 'REFERRAL_FACEBOOK_IMG') {
                                imgWidth = '470';
                                imgHeight = '246';
                            }   else if(prefKey == 'LOGO_FOR_PDF_EMAIL') {
                                imgWidth = '720';
                                imgHeight = '185';
                            } else {
                                imgWidth = '750';
                                imgHeight = '500';
                            }

                            var imgSize = imgWidth + 'x' + imgHeight;
                            $('label', elem).attr('for', prefKey).html(prefTitle);
                            $('.image_container', elem).attr({id: prefKey, 'mr-width': imgWidth, 'mr-height': imgHeight});
                            $('.image_input', elem).attr({id: prefKey + '_input', name: prefKey + '_input', 'data-dimension': imgSize});
                            if(prefValue != "") {
                                $('.image_container', elem).html('<img src="' + prefValue + '" style="height: 100%;" class="img-responsive" />');
                            } else {
                                $('.drop_info .image_size', elem).html(imgSize);
                            }

                        } else {
                            var elem = $('.form_field_template').clone();
                            $('label', elem).attr('for', prefKey).html(prefTitle);
                            $('.info_display', elem).html(prefValue);
                            $('input.form-control', elem).attr({id: prefKey, name: prefKey, value: prefValue});
                        }
                        form_fields += elem.html();
                    }

                    var elem_section = $('.form_section_template').clone();
                    if(sectionName == "Default Image")
                        $('.form_head .detail_options', elem_section).remove();
                    $('.form_head .section_title', elem_section).html(sectionName);
                    $('.form_content .row', elem_section).html(form_fields);
                    form_sections += elem_section.html();

                }
                $('.display_settings').html(form_sections);
                $('.form_content .selectpicker').selectpicker('refresh');
                if($('.main_tabs li.active').index() == 0) {
                    Image.dropZone('#COMPANY_LOGO_input', '#COMPANY_LOGO');
                    Image.dropZone('#DEFAULT_IMG_ITEM_input', '#DEFAULT_IMG_ITEM');
                    Image.dropZone('#DEFAULT_IMG_CATEGORY_input', '#DEFAULT_IMG_CATEGORY');
                    Image.dropZone('#DEFAULT_IMG_SEARCH_input', '#DEFAULT_IMG_SEARCH');
                    Image.dropZone('#REFERRAL_FACEBOOK_IMG_input', '#REFERRAL_FACEBOOK_IMG');
                    Image.dropZone('#LOGO_FOR_PDF_EMAIL_input', '#LOGO_FOR_PDF_EMAIL');
                    $('#COMPANY_LOGO, #DEFAULT_IMG_ITEM, #DEFAULT_IMG_CATEGORY, #DEFAULT_IMG_SEARCH, #REFERRAL_FACEBOOK_IMG, #LOGO_FOR_PDF_EMAIL').bind('submitPref', function(){
                        Admin.uploadPrefImage({prefKey: $(this).attr('id'), imageString: $('img', this).attr('src')}, $(this));
                    });
                    Main.elemRatio();
                }

            };
            if(typeof secSettings != 'object') {
                callback.requestType = "GET";
                callback.loaderDiv = "body";
                Admin.secId = secSettings;
                Main.request('/admin/get_group_preferences', {}, callback, {id: secSettings});
            } else {
                callback('', secSettings);
            }

        },
        loadEditSettings: function() {
            $('.form_content .selectpicker').selectpicker();
            $('.form_content .selectpicker').live('change', function() {
                $(this).parent().siblings('.none_editable').html($('option:selected', this).html());
            });

            $('.edit_btn').live('click', function () {
                var parent = $(this).parents('.form_group').eq(0);
                $(".editable", parent).removeClass('hidden');
                $(".none_editable", parent).addClass('hidden');
            });

            $('.cancel_btn').live('click', function () {
                var parent = $(this).parents('.form_group').eq(0);
                $(".none_editable", parent).removeClass('hidden');
                $(".editable", parent).addClass('hidden');
                Admin.loadSettings(Admin.secSettings);
            });

            $('.save_btn').live('click', function () {

                var __this = $(this);
                var parent = __this.parents('.form_group').eq(0);


                if(parent[0] == $('#DBOY_PER_KM_CHARGE_UPTO_NKM').parents('.form_group')[0] || parent == $('#DBOY_MIN_AMOUNT').parents('.form_group')[0]) {
                    var cDistanceCharge = parseFloat($('#DBOY_PER_KM_CHARGE_UPTO_NKM').val());
                    var dDistanceCharge = parseFloat($('#DBOY_MIN_AMOUNT').val());
                    if(dDistanceCharge > cDistanceCharge) {
                        alert("\'Per KM Charge for Default Distance to Customer\' shouldn't be smaller than \'Minimum Amount Given to Shopper\'.");
                        return false;
                    }
                }

                var button1 = function() {

                    var preferences = [];
                    $('input.form-control, select', parent).each(function(){
                        var data = {prefKey: $(this).attr('id'), value: $(this).val()};
                        preferences.push(data);
                    });
                    Admin.updateSettings({preferences: preferences}, parent);
                };

                button1.text = "Yes";
                var button2 = "No";

                var buttons = [button1, button2];
                Main.popDialog('', 'Are you sure you want to update Settings?', buttons);

            });

        },
        updateSettings: function(updatedData, parent) {

            var callback = function(status, data) {

                Main.popDialog('', data.message, function() {
                    if(data.success) {
                        $(".none_editable", parent).removeClass('hidden');
                        $(".editable", parent).addClass('hidden');
                        Admin.loadSettings(Admin.secId);
                    }
                });

            };
            callback.loaderDiv = "body";
            Main.request('/admin/update_preferences', updatedData, callback);

        },
        uploadPrefImage: function(imgData, img_container) {

            var callback = function(status, data) {

                Main.popDialog('', data.message, function() {
                    if(data.success) {
                        $(".none_editable", parent).removeClass('hidden');
                        $(".editable", parent).addClass('hidden');
                        Admin.loadSettings(Admin.secId);
                    }
                });

            };
            callback.loaderDiv = img_container;
            Main.request('/admin/update_default_images', imgData, callback);

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

                    var button1 = function() {

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
                    };

                    button1.text = "Yes";
                    var button2 = "No";

                    var buttons = [button1, button2];
                    Main.popDialog('', 'Are you sure you want to ' + $('#modal_user .modal-header').html().toLowerCase() + '?', buttons);

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

            var dataFilter = function (data, type) {

                console.log(data);
                if (!data.success) {
                    Main.popDialog('', data.message);
                    return;
                }
                var responseRows = data.params.managers.numberOfRows;
                var managers = data.params.managers.data;
                var tdata = [];

                for (var i = 0; i < managers.length; i++) {
                    var manager = managers[i];
                    var id = manager.id;

                    var actions = '<div class="action_links">';
//                    actions += '<a href="/delivr/merchant/profile/1">Profile</a>';
                    actions += '<a href="#" data-target="#modal_user" data-toggle="modal" data-user="Manager" data-id="' + id + '">Edit</a>';
                    actions += '</div>';

                    var row = [id, manager.fullName, manager.emailAddress, manager.mobileNumber, manager.status == undefined ? 'Unverified' : Main.ucfirst(manager.status), actions];
                    row = $.extend({}, row);
                    tdata.push(row);
                }

                var response = {};
                response.data = tdata;
                response.recordsTotal = responseRows;
                response.recordsFiltered = responseRows;

                return response;

            };

            dataFilter.url = "/admin/get_managers";
            dataFilter.columns = [
                { "name": "id" },
                { "name": "fullName" },
                { "name": "emailAddress" },
                { "name": "mobileNumber" },
                { "name": "verifiedStatus" },
                { "name": "" }
            ];
            Main.createDataTable("#manager_table", dataFilter);

        },
        getAccountants: function() {

            var dataFilter = function (data, type) {

                console.log(data);
                if (!data.success) {
                    Main.popDialog('', data.message);
                    return;
                }

                var responseRows = data.params.accountants.numberOfRows;
                var accountants = data.params.accountants.data;
                var tdata = [];

                for (var i = 0; i < accountants.length; i++) {
                    var accountant = accountants[i];
                    var id = accountant.id;

                    var actions = '<div class="action_links">';
//                    actions += '<a href="/delivr/merchant/profile/1">Profile</a>';
                    actions += '<a href="#" data-target="#modal_user" data-toggle="modal" data-user="Accountant" data-id="' + id + '">Edit</a>';
                    actions += '</div>';

                    var row = [id, accountant.fullName, accountant.emailAddress, accountant.mobileNumber, accountant.status == undefined ? 'Unverified' : Main.ucfirst(accountant.status), actions];
                    row = $.extend({}, row);
                    tdata.push(row);
                }

                var response = {};
                response.data = tdata;
                response.recordsTotal = responseRows;
                response.recordsFiltered = responseRows;

                return response;

            };

            dataFilter.url = "/admin/get_accountants";
            dataFilter.columns = [
                { "name": "id" },
                { "name": "fullName" },
                { "name": "emailAddress" },
                { "name": "mobileNumber" },
                { "name": "verifiedStatus" },
                { "name": "" }
            ];
            Main.createDataTable("#accountant_table", dataFilter);

        },
        saveUser: function(params, headers) {

            $("button[type='submit']").attr("disabled", true);

            var callback = function (status, data) {
                $("button[type='submit']").removeAttr("disabled");

                Main.popDialog('', data.message, function() {
                    if (data.success == true) {
                        $('#modal_user').modal('hide');
                        if(params.role.role == 'ROLE_MANAGER')
                            Admin.getManagers();
                        else
                            Admin.getAccountants();
                    }
                });
            };

            callback.loaderDiv = ".modal-dialog";

            var url = '/admin/save_user'
            if(headers.id != undefined) url = '/admin/update_user';

            Main.request(url, params, callback, headers);

        },
        refillWallet: function() {

            $.validator.setDefaults({
                errorPlacement: function (error, element) {
                    $('#error_container').html(error);
                },
                ignore: []
            });

            $('#form').validate({
                submitHandler: function () {


                    var button1 = function() {

                        $("button[type='submit']").attr("disabled", true);

                        var callback = function (status, data) {
                            $("button[type='submit']").removeAttr("disabled");

                            Main.popDialog('', data.message, function() {
                                if(data.success)
                                    $('#form')[0].reset();
                            });
                        };

                        callback.loaderDiv = "body";

                        Main.request('/client/refill_wallet', {facebookId: $('#userID').val(), walletAmount:$('#amount').val()}, callback, {accessToken: "abc"});

                    };

                    button1.text = "Yes";
                    var button2 = "No";

                    var buttons = [button1, button2];
                    Main.popDialog('', 'Are you sure you want to refill wallet?', buttons);
                    return false;

                }
            });
            $('#amount').rules('add', {required: true, number: true});
            $('#userID').rules('add', {required: true, digits: true});

        }

    };

}();