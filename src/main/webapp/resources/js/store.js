if(typeof(Store) == "undefined") var Store = {};

(function ($){

    Store.loadAddStore = function() {

        $('label.check_label .checkbox').removeAttr("checked");

        $('label.check_label .checkbox').live('click', function(){
            if($(this).prop('checked')) {
                $(this).siblings('.check_span').addClass("icon_full");
            } else {
                $(this).siblings('.check_span').removeClass("icon_full");
            }
        });

        $("label.check_label").live('mouseover', function ( event ) {
            $('.check_span', this).addClass("icon_semi");
        });

        $("label.check_label").live('mouseout', function ( event ) {
            $('.check_span', this).removeClass("icon_semi");
        });

        $('#vat_no').keyup(function(){
            $('#pan_no').val('');
            $('.vat_show').removeClass('hidden');
        });
        $('#pan_no').keyup(function(){
            $('#vat_no').val('');
            $('.vat_show').addClass('hidden');
        });

        form_submit = false;

        var action = Main.getURLvalue(3);
        var storeId = Main.getURLvalue(4);

        $('option:selected').removeAttr('selected');
        Image.dropZone('#brand_image_input', '#brand_image');
        if(action != 'edit') Image.dropZone('#brand_logo_input', '#brand_logo');

        $('.submit_store').click(function(){
            $('#form_brand').submit();
        });
        $('.cancel_edit').click(function(){

            var button1 = function() {
                form_submit = true;
                window.location = Main.modifyURL('/merchant/store/view/' + Main.getURLvalue(4));
            };

            button1.text = "Yes";
            var button2 = "No";

            var buttons = [button1, button2];
            Main.popDialog('', 'Are you sure you want to cancel updates?', buttons);

        });

        var cat_callback = function (status, data) {
            console.log(data);
            var categories = data.params.categories;
            var category_options = "";
            for(var i = 0; i < categories.length; i++) {
                category_options += '<option value="' + categories[i].id + '">' + categories[i].name + '</option>';
            }
            $('#store_categories').append(category_options);
            $('#store_categories').selectpicker('refresh');
        };
        cat_callback.requestType = "GET";
        Main.request('/merchant/get_parent_categories', {}, cat_callback);

        $('.save_marker, .cancel_marker, .marker_nav', '#form_store').attr('disabled', 'disabled');

        $.validator.setDefaults({
            errorPlacement : function(error, element){
                $('#error_container').html(error);
            },
            ignore: []
        });

        $.validator.addMethod("contactNumber", function(value, element, arg){
            return this.optional(element) || /[0-9+-]+$/.test(value);
        }, "Only +, - and numbers are allowed.");

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

        $.validator.addMethod("minSelection", function(value, element, arg){
            var result = value != null && (value.length >= arg || value == 'All');
            if($(element).is('select')) {
                if(!result) {
                    $(element).siblings('.bootstrap-select').children('.form-control').addClass('error');
                } else {
                    $(element).siblings('.bootstrap-select').children('.form-control').removeClass('error');
                }
            }
            return result;
        }, $.validator.format("Please select at least {0} options."));

        $.validator.addMethod("imageRequired", function(value, element, arg){
            var result = true;
            var container = $(element).prev('.drop_zone');
            if($('img', container).attr('src') == undefined || $('img', container).attr('src') == "") {
                result = false;
                container.addClass('error');
            } else {
                container.removeClass('error');
            }
            return result;
        }, "Only numbers are allowed.");

        $('#store_name, #street, #city, #state, #country, #contact_no, #contact_person').val('');

        $('select.haveall').live('change', function(){
            if($(this).val() != null && $('option', this).length == $(this).val().length + 1)
                $(this).selectpicker('selectAll');
        });

        $('select.compelselection').live('change', function(){
            if($(this).val() == null) $(this).selectpicker('selectAll');
        });

        if(!initialized) initialize('add_store'); else google.maps.event.trigger(map, 'resize');
        $('#store_categories').selectpicker({
            noneSelectedText: 'Select Store Categories',
            size: 5
        });

        var timeSelect = "";
        for(i = 0; i < 24; i++) {
            var time = i + "";
            time = (time.length == 1 ? "0" + time : time) + ":00:00";
            timeSelect += '<option value="' + time + '">' + time + '</option>';
        }
        $('#open_time, #close_time').append(timeSelect);
        $('#open_time').val("07:00:00");
        $('#close_time').val("22:00:00");

        $('#open_time, #close_time, #partnership, #vat_status').selectpicker({size: 5});

        function updateGeoPoints(showAlert) {

            if(markers.length > 0) {
                var location_valid = true;
                var stores = [];
                var geoPoint;
                var geoKey;
                for(geoKey in arrGeoPoints) {
                    geoPoint = arrGeoPoints[geoKey];
//                        if(geoPoint.name == ""){
//                            location_valid = false;
//                        }
                    geoPoint.name = $('#brand_name').val();
                    geoPoint.status = "ACTIVE";
                    if(geoPoint.street == ""){
                        location_valid = false;
                    }
                    if(geoPoint.city == ""){
                        location_valid = false;
                    }
                    if(geoPoint.state == ""){
                        location_valid = false;
                    }
                    if(geoPoint.country == ""){
                        location_valid = false;
                    }
                    if(geoPoint.contactNo == ""){
                        location_valid = false;
                    }
                    if(geoPoint.contactPerson == ""){
                        location_valid = false;
                    }
                    if(geoPoint.email == ""){
                        location_valid = false;
                    }
                    stores.push(geoPoint);
                    if(location_valid == false) break;
                }

                if(!location_valid) {
                    var smCallback = function () {
                        var current_index = Object.keys(arrGeoPoints).indexOf(geoKey);
                        google.maps.event.trigger(markers[current_index], 'click');
                        map.panTo(markers[current_index].position);
                    };
                    if(showAlert != false)
                        Main.popDialog('', 'All fields of all store locations are required.', smCallback);
                    else
                        smCallback();
                }

                return {valid: location_valid, stores: stores};

            } else {
                Main.popDialog('', "Please add at least 1 store location.", function () {
                    return {valid: false};
                });
            }

        };

        $('#form_store input').change(function(){
            $('#form_store').submit();
        });

        $('#form_store').submit(function(e) {

            e.preventDefault();

            var geoKeyObject = arrGeoPoints[$(".save_marker").eq(0).attr('data-id')];
            var geoParent = '#form_store';

            var address_name = $('#store_name', geoParent).val();
            var address_street_name = $('#street', geoParent).val();
            var address_city = $('#city', geoParent).val();
            var address_state = $('#state', geoParent).val();
            var address_country = $('#country', geoParent).val();
            var address_contact_number = $('#contact_no', geoParent).val();
            var address_contact_person = $('#contact_person', geoParent).val();
            var address_email = $('#email', geoParent).val();
            var address_email_subscription = $('#email_subscription', geoParent).prop('checked');

            geoKeyObject.name = address_name;
            geoKeyObject.street = address_street_name;
            geoKeyObject.city = address_city;
            geoKeyObject.state = address_state;
            geoKeyObject.country = address_country;
            geoKeyObject.contactNo = address_contact_number;
            geoKeyObject.contactPerson = address_contact_person;
            geoKeyObject.email = address_email;
            geoKeyObject.sendEmail = address_email_subscription;

            return false;
        });
        $('#form_store').validate();

/*        $('#form_store').validate({
            submitHandler: function() {

                var geoKeyObject = arrGeoPoints[$(".save_marker").eq(0).attr('data-id')];
                var geoParent = '#form_store';

                var address_name = $('#store_name', geoParent).val();
                var address_street_name = $('#street', geoParent).val();
                var address_city = $('#city', geoParent).val();
                var address_state = $('#state', geoParent).val();
                var address_country = $('#country', geoParent).val();
                var address_contact_number = $('#contact_no', geoParent).val();
                var address_contact_person = $('#contact_person', geoParent).val();
                var address_email = $('#email', geoParent).val();
                var address_email_subscription = $('#email_subscription', geoParent).prop('checked');

                geoKeyObject.name = address_name;
                geoKeyObject.street = address_street_name;
                geoKeyObject.city = address_city;
                geoKeyObject.state = address_state;
                geoKeyObject.country = address_country;
                geoKeyObject.contactNo = address_contact_number;
                geoKeyObject.contactPerson = address_contact_person;
                geoKeyObject.email = address_email;
                geoKeyObject.sendEmail = address_email_subscription;

                return false;
            }
        });*/

//        $('#store_name').rules('add', {required: true});
        $('#street').rules('add', {required: true});
        $('#city').rules('add', {required: true});
        $('#state').rules('add', {required: true});
        $('#country').rules('add', {required: true});
        $('#contact_no').rules('add', {required: true, contactNumber: true});
        $('#contact_person').rules('add', {required: true});
        $('#email').rules('add', {required: true, email: true});

        $('#form_brand').validate({
            submitHandler: function() {
                var geoPoints = updateGeoPoints();
                console.log(geoPoints);
                if(geoPoints.valid) {

                    var button1 = function() {

                        var data = {};
                        var stores_brand = {};

                        stores_brand.id = $('.submit_store').attr('data-id');
                        stores_brand.brandName = $('#brand_name').val();
                        stores_brand.minOrderAmount = $('#min_amount').val();
                        stores_brand.openingTime = $('#open_time').val();
                        stores_brand.closingTime = $('#close_time').val();
                        stores_brand.brandLogo = $('#brand_logo img').attr('data-new') ? $('#brand_logo img').attr('src') : undefined;
                        stores_brand.brandImage = $('#brand_image img').attr('data-new') ? $('#brand_image img').attr('src') : undefined;
                        stores_brand.brandUrl = $('#brand_url').val();
                        stores_brand.status = "ACTIVE";
                        stores_brand.partnershipStatus = $('#partnership').val();
                        stores_brand.processingCharge = $('#processing_charge').val();
                        stores_brand.vatNo = $('#vat_no').val();
                        stores_brand.panNo = $('#pan_no').val();
                        stores_brand.deliveryFee = $('#delivery_fee').val();
                        stores_brand.discountInDeliveryFee = $('#delivery_discount').val();
                        stores_brand.vatInclusive = $('#vat_status').val();
                        stores_brand.defaultCommissionPcn = $('#default_commission').val();
                        stores_brand.deliveryFeeLimit = $('#delivery_fee_limit').val();


                        var categories = $('#store_categories').val();
                        var arr_categories = [];
                        if(categories == "All") {
                            $('#store_categories option').not('option[value="All"]').each(function(){
                                arr_categories.push($(this).val());
                            });
                            categories = arr_categories;
                        }
                        data.stores = geoPoints.stores;
                        data.storesBrand = stores_brand;
                        data.categories = categories;
                        Store.addStore(data, {merchantId: Main.getFromLocalStorage('mid')}, $('.submit_store').attr('data-action'));
                    };

                    button1.text = "Yes";
                    var button2 = "No";

                    var buttons = [button1, button2];
                    Main.popDialog('', 'Are you sure you want to ' + (action == 'edit' ? 'update' : 'add') + ' store?', buttons);

                };
                return false;
            }
        });

        $('#brand_name').rules('add', {required: true});
        $('#brand_image_input').rules('add', {imageRequired: true});
        $('#brand_logo_input').rules('add', {imageRequired: true});
        $('#min_amount').rules('add', {required: true, number: true, min: 0});
        $('#open_time').rules('add', {notEqual: "none"});
        $('#close_time').rules('add', {notEqual: "none"});
//        $('#service_distance').rules('add', {required: true, number: true});
        $('#brand_url').rules('add', {url: true});
        $('#store_categories').rules('add', {minSelection: 1});
        $('#partnership').rules('add', {notEqual: "none"});
        $('#processing_charge').rules('add', {required: true, number: true, min: 0});
        $('#delivery_fee').rules('add', {required: true, number: true, min: 0});
        $('#delivery_discount').rules('add', {required: true, number: true, min: 0, max: 100});
        $('#default_commission').rules('add', {required: true, number: true, min: 0, max: 100});
        $('#delivery_fee_limit').rules('add', {required: true, number: true, min: 0});

        $('.cancel_marker').live('click', function(){

            var geoKey = $(".save_marker").eq(0).attr('data-id');
            for(var i in markers) {
                if('geoKey' in markers[i] && markers[i].geoKey == geoKey) {
                    google.maps.event.trigger(markers[i], 'rightclick');
                    break;
                }
            }

        });

        $('.marker_nav').click(function(){
            var marker_index = $(this).attr('data-index');
            if(marker_index != undefined && $.isNumeric(marker_index) && marker_index >= 0 && marker_index < markers.length) {
                google.maps.event.trigger(markers[marker_index], 'click');
                map.panTo(markers[marker_index].position);
            }
        });

        if(action == "edit") {

            $('.heading h1').html('Store Edit');
            document.title = 'Store Edit';
            $('.submit_store').attr({'data-action': 'update'}).html('Update Store');
            $('.cancel_edit').removeClass('hidden');
            $('#brand_name').attr('disabled', 'disabled');

            if(storeId != undefined) {

                var callback = function (status, data) {

                    if (data.success == true) {

                        var storeBrand = data.params.storesBrand;
                        console.log(storeBrand);

                        $('#brand_image').html('<img src="' + storeBrand.brandImage + '" style="height: 100%;" class="img-responsive" />');
                        $('#brand_logo').html('<img src="' + storeBrand.brandLogo + '" style="height: 100%;" class="img-responsive" />');
                        $('.submit_store').attr({'data-id': storeBrand.id});
                        $('#brand_name').val(storeBrand.brandName);
                        $('#min_amount').val(storeBrand.minOrderAmount == undefined ? 0 : storeBrand.minOrderAmount);
                        $('#open_time').val(storeBrand.openingTime);
                        $('#open_time').selectpicker('refresh');
                        $('#close_time').val(storeBrand.closingTime);
                        $('#close_time').selectpicker('refresh');
                        $('#brand_url').val(storeBrand.brandUrl);
                        $('#partnership').val(storeBrand.partnershipStatus + '');
                        $('#partnership').selectpicker('refresh');
                        $('#processing_charge').val(storeBrand.processingCharge);
                        $('#vat_no').val(storeBrand.vatNo);
                        $('#pan_no').val(storeBrand.panNo);
                        $('#delivery_fee').val(storeBrand.deliveryFee);
                        $('#delivery_discount').val(storeBrand.discountInDeliveryFee);
                        $('#vat_status').val(storeBrand.vatInclusive + '');
                        $('#vat_status').selectpicker('refresh');
                        $('#default_commission').val(storeBrand.defaultCommissionPcn);
                        $('#delivery_fee_limit').val(storeBrand.deliveryFeeLimit);


                        if(storeBrand.panNo == undefined || storeBrand.panNo == "") {
                            $('.vat_show').removeClass('hidden');
                        }

                        var cat_callback = function (status, data) {

                            if (data.success == true) {

                                var brandsCategory = data.params.categories;

                                var storeCatsArr = [];
                                for(var i = 0; i < brandsCategory.length; i++) {
                                    var itemCat = brandsCategory[i];
                                    storeCatsArr.push(itemCat.id);
                                }
                                $('#store_categories').val(storeCatsArr);
                                $('#store_categories').selectpicker('refresh');

                            }

                        };

                        cat_callback.loaderDiv = "body";
                        cat_callback.requestType = "GET";

                        Main.request('/merchant/get_brands_parent_categories', {}, cat_callback, {id: storeId});

                        var stores = storeBrand.store;
                        for(var i = 0; i < stores.length; i++) {
                            var store = stores[i];
                            var location = latLngToLocation(store.latitude, store.longitude);;
                            var geoPointData = {};
                            geoPointData.id = store.id;
                            geoPointData.name = store.name;
                            geoPointData.street = store.street;
                            geoPointData.city = store.city;
                            geoPointData.state = store.state;
                            geoPointData.country = store.country;
                            geoPointData.contactNo = store.contactNo;
                            geoPointData.contactPerson = store.contactPerson;
                            geoPointData.email = store.email;
                            geoPointData.sendEmail = store.sendEmail;
                            geoPointData.latitude = store.latitude;
                            geoPointData.longitude = store.longitude;
                            location.geoPointData = geoPointData;
                            addStoreMarker(location);
                        }

                    } else {

                        Main.popDialog('', data.message);
                    }

                };

                callback.loaderDiv = "body";
                callback.requestType = "GET";

                Main.request('/merchant/get_store_detail', {}, callback, {id: storeId});

            }

        }

    };

    Store.addStore = function(data, headers, data_action) {

        $("button[type='submit']").attr("disabled", true);

        var callback = function (status, data) {
            $("button[type='submit']").removeAttr("disabled");

            Main.popDialog('', data.message, function () {
                if (data.success == true) {
                    form_submit = true;
                    if(data_action == 'update')
                        window.location = Main.modifyURL('/merchant/store/view/' + Main.getURLvalue(4));
                    else
                        window.location = Main.modifyURL("/merchant/store/list");
                }
            });
        };

        callback.loaderDiv = "body";

        if(data_action == 'update') {
            Main.request('/merchant/update_store', data, callback);
        } else {
            Main.request('/merchant/save_store', data, callback, headers);
        }

    };

    Store.listStores = function(headers) {

        var callback = function (status, data) {

            if (data.success == true) {

                var storeBrands = data.params.storesBrand;
                var active_store_list = "";
                var inactive_store_list = "";

                if(storeBrands.length > 0) {

                    for(var i = 0; i < storeBrands.length; i++) {
                        var elem = $('.block_store_template').clone();

                        var storeBrand = storeBrands[i];
                        $('.item_image img', elem).attr('src', storeBrand.brandImage);
                        $('.item_name', elem).html('<a href="' + Main.modifyURL('/merchant/item/list/' + storeBrand.id) + '">' + storeBrand.brandName + '</a>');
                        $('.add_items', elem).attr('href', Main.modifyURL('/merchant/item/form/create/' + storeBrand.id));
                        if(Main.getFromLocalStorage('userStatus') == "INACTIVE") $('.add_items', elem).addClass('disabled');
                        $('.view_store', elem).attr('href', Main.modifyURL('/merchant/store/view/' + storeBrand.id));

                        if(storeBrand.featured == true) $('.item_image', elem).append('<div class="special_item">Featured</div>');
                        if(storeBrand.priority != undefined) $('.item_image', elem).append('<div class="special_item">Priority : ' + storeBrand.priority + '</div>');

                        if(storeBrand.status == "ACTIVE") {
                            active_store_list += elem.html();
                        } else {
                            $('.add_items', elem).remove();
                            inactive_store_list += elem.html();
                        }
                    }

                }

                if(active_store_list == "" && inactive_store_list == "") {
                    $('.items_container').html("No stores are available.");
                } else {
                    if(active_store_list != "") {
                        $('.active_stores.form_content').html(active_store_list);
                        $('.active_stores').removeClass('hidden');
                    }
                    if(inactive_store_list != "") {
                        $('.inactive_stores.form_content').html(inactive_store_list);
                        $('.inactive_stores').removeClass('hidden');
                    }
                }

                Main.elemRatio(function() {
                    $('.items_container .item_container').removeClass('invisible');
                    $(window).trigger('resize');
                });


            } else {
                Main.popDialog('', data.message);
            }

        };

        callback.loaderDiv = "body";
        callback.requestType = "GET";

        Main.request('/merchant/get_stores', {}, callback, headers);

    };

    Store.loadStore = function(storeId){

        $('label.check_label .checkbox').removeAttr("checked");

        $('label.check_label .checkbox').live('click', function(){
            if($(this).prop('checked')) {
                $(this).siblings('.check_span').addClass("icon_full");
            } else {
                $(this).siblings('.check_span').removeClass("icon_full");
            }
        });

        $("label.check_label").live('mouseover', function ( event ) {
            $('.check_span', this).addClass("icon_semi");
        });

        $("label.check_label").live('mouseout', function ( event ) {
            $('.check_span', this).removeClass("icon_semi");
        });

        function showEdit() {
            $('.store_status_edit').addClass('hidden');
            $('.store_status_save').removeClass('hidden');
        }
        function hideEdit() {
            $('.store_status_edit').removeClass('hidden');
            $('.store_status_save').addClass('hidden');
        }
        $('.btns_change').click(showEdit);
        $('.btns_cancel').click(hideEdit);
        $('.btns_save').click(function(){

            var callback = function (status, data) {
                if(data.success) {
                    hideEdit();
                    $('.store_location .block_store').each(function() {

                        if($('input.checkbox', this).prop('checked'))
                            $('.inactive_store', this).addClass('hidden');
                        else
                            $('.inactive_store', this).removeClass('hidden');

                    });
                }
            };
            callback.loaderDiv = "body";
            var data = {};
            data.className= 'Store';

            // active stores
            if($('.store_location .checkbox:checked').length > 0) {
                data.statusId= 2;
                var headers = {};
                var ids = [];
                $('.store_location .checkbox:checked').each(function(){
                    ids.push($(this).attr('data-id'));
                });
                headers.id = ids.join(',');
                Main.request('/merchant/change_status', data, callback, headers);
            }

            // inactive stores
            if($('.store_location .checkbox').not(':checked').length > 0) {
                data.statusId= 3;
                var headers = {};
                var ids = [];
                $('.store_location .checkbox').not(':checked').each(function(){
                    ids.push($(this).attr('data-id'));
                });
                headers.id = ids.join(',');
                Main.request('/merchant/change_status', data, callback, headers);
            }
        });

        $('.btn_edit').attr('href', Main.modifyURL('/merchant/store/form/edit/' + storeId));
        $('.add_items').attr('href', Main.modifyURL('/merchant/item/form/create/' + storeId));
        $('.view_items').attr('href', Main.modifyURL('/merchant/item/list/' + storeId));
        if(!initialized) initialize('readonly', true); else google.maps.event.trigger(map, 'resize');

        var dragged = false;
        var cancel_drag = false;
        function toggleSwitch(value, elem) {

            var chk_confirm = false;
            if(!cancel_drag) {

                var button1 = function() {

                    var callback = function (status, data) {
                        Main.popDialog('', data.message, function () {
                            if (data.success != true) {
                                value = value == 'on' ? 'off' : 'on';
                            }

                            if(value == 'on') {
                                elem.css({left: 0}).removeClass('off').addClass('on');
                            } else {
                                elem.css({left: 30}).removeClass('on').addClass('off');
                            }
                            dragged = false;
                            cancel_drag = false;
                        });
                    };

                    callback.loaderDiv = "body";
                    callback.requestType = "POST";

                    Main.request('/merchant/change_status', {className:"Brand", statusId: value == 'on' ? 2 : 3}, callback, {id: Main.getURLvalue(3)});

                };
                var button2 = function() {
                    value = value == 'on' ? 'off' : 'on';

                    if(value == 'on') {
                        elem.css({left: 0}).removeClass('off').addClass('on');
                    } else {
                        elem.css({left: 30}).removeClass('on').addClass('off');
                    }
                    dragged = false;
                    cancel_drag = false;
                };

                button1.text = "Yes";
                button2.text = "No";

                var buttons = [button1, button2];
                Main.popDialog('', 'Are you sure you want to ' + (value == 'on' ? 'activate' : 'deactivate') + ' store?', buttons);

            }
        }
        $('.switch_activation .btn_switch').bind('contextmenu', function(e) {
            return false;
        });
        $('.switch_activation .btn_switch').on('mouseup', function(e){
            if(!dragged && e.which == 1) toggleSwitch($(this).hasClass('on') ? 'off' : 'on', $(this));
        });
        $( ".btn_switch" ).draggable({
            containment: "parent",
            start: function( event, ui) {
                dragged = true;
            },
            stop: function( event, ui) {
                if(Math.abs(ui.originalPosition.left - ui.position.left) < 15) {
                    cancel_drag = true;
                }
                toggleSwitch(ui.position.left > 15 ? 'off' : 'on', ui.helper);
            }
        });

        var callback = function (status, data) {

            if (data.success == true) {

                var storeBrand = data.params.storesBrand;
                console.log(storeBrand);

                if(storeBrand.brandImage != undefined)
                    $('#brand_image').html('<img src="' + storeBrand.brandImage + '" style="height: 100%;" class="img-responsive" />');
                if(storeBrand.brandLogo != undefined)
                    $('#brand_logo').html('<img src="' + storeBrand.brandLogo + '" style="height: 100%;" class="img-responsive" />');
                $('.store_name').html(storeBrand.brandName);
                if(storeBrand.status == 'ACTIVE')
                    $('.btn_switch').removeClass('off').addClass('on');
                else
                    $('.btn_switch').removeClass('on').addClass('off');
                document.title = storeBrand.brandName;
                $('.open_time').html(storeBrand.openingTime + ' - ' + storeBrand.closingTime);
                $('.min_amount').html(storeBrand.minOrderAmount == undefined ? 0 : storeBrand.minOrderAmount);
                $('.partnership').html(storeBrand.partnershipStatus == undefined ? "Non Partner" : (storeBrand.partnershipStatus ? 'Partner' : 'Non Partner'));
                $('.processing_charge').html(storeBrand.processingCharge == undefined ? "0" : storeBrand.processingCharge);
                $('.vat_no').html(storeBrand.vatNo == undefined ? "" : storeBrand.vatNo);
                $('.pan_no').html(storeBrand.panNo == undefined ? "" : storeBrand.panNo);
                $('.delivery_fee').html(storeBrand.deliveryFee == undefined ? "0" : storeBrand.deliveryFee);
                $('.delivery_discount').html((storeBrand.discountInDeliveryFee == undefined ? "0" : storeBrand.discountInDeliveryFee) + "%");
                $('.vat_status').html(storeBrand.vatInclusive == undefined ? "Excluded" : (storeBrand.vatInclusive ? 'Included' : 'Excluded'));
                $('.default_commission').html((storeBrand.defaultCommissionPcn == undefined ? "0" : storeBrand.defaultCommissionPcn)  + "%");
                $('.delivery_fee_limit').html((storeBrand.deliveryFeeLimit == undefined ? "0" : storeBrand.deliveryFeeLimit));

                if(storeBrand.vatNo != undefined && storeBrand.vatNo != "")
                    $('.vat_show').removeClass('hidden');
                else if(storeBrand.panNo != undefined && storeBrand.panNo != "")
                    $('.pan_show').removeClass('hidden');


                var store_location = '';
                var stores = storeBrand.store;
                for(var i = 0; i < stores.length; i++) {
                    var store = stores[i];
                    var elem = $('.block_store_container_template').clone();

                    $('.checkbox', elem).attr('data-id', store.id);
                    if(store.status == "ACTIVE") {
                        $('.check_span', elem).addClass('icon_full');
                        $('.checkbox', elem).attr('checked', 'checked');
                    } else {
                        $('.inactive_store', elem).removeClass('hidden');
                    }

                    $('.street', elem).html(store.street);
                    $('.city', elem).html(store.city + ', ' + store.state + ', ' + store.country);
                    $('.contact_person', elem).html(store.contactPerson);
                    $('.contact_no', elem).html(store.contactNo);
                    $('.contact_email', elem).html(store.email + (store.sendEmail ? ' <button class="btn-link inactive_store" type="button">(Subscribed)</button>' : ''));
                    var location = latLngToLocation(store.latitude, store.longitude);
                    $('.btn_view_map', elem).attr('data-id', locationToKey(location));
                    location.readOnly = true;
                    addStoreMarker(location);
                    store_location += elem.html();
                }
                $('.store_location').html(store_location);

                $('.btn_view_map').live('click', function(){
                    var geoKey = $(this).attr('data-id');
                    if(geoKey != undefined) {
                        var current_index = Object.keys(arrGeoPoints).indexOf(geoKey);
                        google.maps.event.trigger(markers[current_index], 'click');
                        map.panTo(markers[current_index].position);
                    }
                });

            } else {
                Main.popDialog('', data.message);
            }

            var cat_callback = function (status, data) {

                if (data.success == true) {

                    var brandsCategory = data.params.categories;

                    if(brandsCategory.length > 0) {

                        var item_categories = "";
                        for(var i = 0; i < brandsCategory.length; i++) {
                            var itemCat = brandsCategory[i];
                            item_categories +='<li>' + itemCat.name + '</li>';
                        }
                        $('.categories .detail_list').html(item_categories);

                    }

                }

            };

            cat_callback.loaderDiv = "body";
            cat_callback.requestType = "GET";

            Main.request('/merchant/get_brands_parent_categories', {}, cat_callback, {id: storeId});

            var stores_callback = function (status, data) {

                if (data.success == true) {

                    var storeBrands = data.params.storesBrand;
                    var store_list = "";

                    if(storeBrands.length > 0) {

                        var count = 0;
                        for(var i = 0; i < storeBrands.length; i++) {

                            var storeBrand = storeBrands[i];
                            if(storeBrand.id != storeId) {
                                var elem = $('.block_store_template').clone();

                                $('.item_image img', elem).attr('src', storeBrand.brandImage);
                                $('.item_name', elem).html('<a href="' + Main.modifyURL('/merchant/item/list/' + storeBrand.id) + '">' + storeBrand.brandName + '</a>');
                                $('.add_items', elem).attr('href', Main.modifyURL('/merchant/item/form/create/' + storeBrand.id));
                                if(Main.getFromLocalStorage('userStatus') == "INACTIVE") $('.add_items', elem).addClass('disabled');
                                $('.view_store', elem).attr('href', Main.modifyURL('/merchant/store/view/' + storeBrand.id));

                                if(storeBrand.featured == true) $('.item_image', elem).append('<div class="special_item">Featured</div>');
                                if(storeBrand.priority != undefined) $('.item_image', elem).append('<div class="special_item">Priority : ' + storeBrand.priority + '</div>');

                                store_list += elem.html();
                                count++;
                            }

                            if(count == 4) break;

                        }

                        $('.items_container .form_content').html(store_list);
                        $('.items_container').removeClass('hidden');

                        Main.elemRatio(function() {
                            $('.items_container .item_container').removeClass('invisible');
                            $(window).trigger('resize');
                        });

                    }

                } else {
                    Main.popDialog('', data.message);
                }

            };

            stores_callback.loaderDiv = "body";
            stores_callback.requestType = "GET";

            Main.request('/merchant/get_stores', {}, stores_callback, {merchantId: Main.getFromLocalStorage('mid')});

        };

        callback.loaderDiv = "body";
        callback.requestType = "GET";

        Main.request('/merchant/get_store_detail', {}, callback, {id: storeId});

    }

})(jQuery);