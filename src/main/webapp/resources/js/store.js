if(typeof(Store) == "undefined") var Store = {};

(function ($){

    Store.loadAddStore = function() {

        var action = Main.getURLvalue(3);
        var storeId = Main.getURLvalue(4);

        $('option:selected').removeAttr('selected');
        Image.dropZone('#brand_image_input', '#brand_image');
        if(action != 'edit') Image.dropZone('#brand_logo_input', '#brand_logo');

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

        $('#open_time').selectpicker({size: 5});
        $('#close_time').selectpicker({size: 5});

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
                    /*                    if(geoPoint.contactNo == ""){
                     location_valid = false;
                     }
                     if(geoPoint.contactPerson == ""){
                     location_valid = false;
                     }*/
                    stores.push(geoPoint);
                    if(location_valid == false) break;
                }

                if(!location_valid) {
                    if(showAlert != false) alert('All fields of all store locations are required.');
                    var current_index = Object.keys(arrGeoPoints).indexOf(geoKey);
                    google.maps.event.trigger(markers[current_index], 'click');
                    map.panTo(markers[current_index].position);
                }

                return {valid: location_valid, stores: stores};

            } else {
                alert("Please add at least 1 store location.");
                return {valid: false};
            }

        };

        $('#form_store').validate({
            submitHandler: function() {
                var loaderDiv = "#store_section .form_content";
                $(loaderDiv).addClass('loader_div').append('<div class="loader"></div>');

                var geoKeyObject = arrGeoPoints[$(".save_marker").eq(0).attr('data-id')];
                var geoParent = '#form_store';

                var address_name = $('#store_name', geoParent).val();
                var address_street_name = $('#street', geoParent).val();
                var address_city = $('#city', geoParent).val();
                var address_state = $('#state', geoParent).val();
                var address_country = $('#country', geoParent).val();
                var address_contact_number = $('#contact_no', geoParent).val();
                var address_contact_person = $('#contact_person', geoParent).val();

                geoKeyObject.name = address_name;
                geoKeyObject.street = address_street_name;
                geoKeyObject.city = address_city;
                geoKeyObject.state = address_state;
                geoKeyObject.country = address_country;
                geoKeyObject.contactNo = address_contact_number;
                geoKeyObject.contactPerson = address_contact_person;

                setTimeout(function(){
                    $(loaderDiv).removeClass('loader_div').children('.loader').hide();
                    updateGeoPoints(false);
                }, 500);

                return false;
            }
        });

//        $('#store_name').rules('add', {required: true});
        $('#street').rules('add', {required: true});
        $('#city').rules('add', {required: true});
        $('#state').rules('add', {required: true});
        $('#country').rules('add', {required: true});
//        $('#contact_no').rules('add', {required: true, contactNumber: true});
//        $('#contact_person').rules('add', {required: true});

        $('#form_brand').validate({
            submitHandler: function() {
                var geoPoints = updateGeoPoints();
                console.log(geoPoints);
                if(geoPoints.valid) {

                    var data = {};
                    var stores_brand = {};

                    stores_brand.id = $('#form_brand button[type="submit"]').attr('data-id');
                    stores_brand.brandName = $('#brand_name').val();
                    stores_brand.openingTime = $('#open_time').val();
                    stores_brand.closingTime = $('#close_time').val();
                    stores_brand.brandLogo = $('#brand_logo img').attr('data-new') ? $('#brand_logo img').attr('src') : undefined;
                    stores_brand.brandImage = $('#brand_image img').attr('data-new') ? $('#brand_image img').attr('src') : undefined;
                    stores_brand.brandUrl = $('#brand_url').val();
                    stores_brand.status = "ACTIVE";


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

                    Store.addStore(data, {merchantId: Main.getFromLocalStorage('mid')}, $('#form_brand button[type="submit"]').attr('data-action'));

                };
                return false;
            }
        });

        $('#brand_name').rules('add', {required: true});
        $('#brand_image_input').rules('add', {imageRequired: true});
        $('#brand_logo_input').rules('add', {imageRequired: true});
        $('#open_time').rules('add', {notEqual: "none"});
        $('#close_time').rules('add', {notEqual: "none"});
        $('#brand_url').rules('add', {url: true});
        $('#store_categories').rules('add', {minSelection: 1});

        $('.cancel_marker').live('click', function(){

            var current_index = Object.keys(arrGeoPoints).indexOf($(".save_marker").eq(0).attr('data-id'));
            google.maps.event.trigger(markers[current_index], 'rightclick');

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
            $('#form_brand button[type="submit"]').attr({'data-action': 'update'}).html('Update Store');
            $('#brand_name').attr('disabled', 'disabled');

            if(storeId != undefined) {

                var callback = function (status, data) {

                    if (data.success == true) {

                        var storeBrand = data.params.storesBrand;
                        console.log(storeBrand);

                        $('#brand_image').html('<img src="' + storeBrand.brandImage + '" style="height: 100%;" class="img-responsive" />');
                        $('#brand_logo').html('<img src="' + storeBrand.brandLogo + '" style="height: 100%;" class="img-responsive" />');
                        $('#form_brand button[type="submit"]').attr({'data-id': storeBrand.id});
                        $('#brand_name').val(storeBrand.brandName);
                        $('#open_time').val(storeBrand.openingTime);
                        $('#open_time').selectpicker('refresh');
                        $('#close_time').val(storeBrand.closingTime);
                        $('#close_time').selectpicker('refresh');
                        $('#brand_url').val(storeBrand.brandUrl);

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
                            geoPointData.name = store.name;
                            geoPointData.street = store.street;
                            geoPointData.city = store.city;
                            geoPointData.state = store.state;
                            geoPointData.country = store.country;
                            geoPointData.contactNo = store.contactNo;
                            geoPointData.contactPerson = store.contactPerson;
                            geoPointData.latitude = store.latitude;
                            geoPointData.longitude = store.longitude;
                            location.geoPointData = geoPointData;
                            addStoreMarker(location);
                        }

                    } else {
                        alert(data.message);
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

            if (data.success == true) {
                alert(data.message);
                window.location = "/merchant/store/list";
            } else {
                alert(data.message);
            }
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
                var store_list = "";

                if(storeBrands.length > 0) {

                    for(var i = 0; i < storeBrands.length; i++) {
                        var elem = $('.block_store_template').clone();

                        var storeBrand = storeBrands[i];
                        $('.item_image img', elem).attr('src', storeBrand.brandImage);
                        $('.item_name', elem).html('<a href="/merchant/store/view/' + storeBrand.id + '">' + storeBrand.brandName + '</a>');
                        $('.add_items', elem).attr('href', '/merchant/item/form/create/' + storeBrand.id);

                        if(storeBrand.featured == true) $('.item_image', elem).append('<div class="special_item">Featured</div>');
                        if(storeBrand.priority != undefined) $('.item_image', elem).append('<div class="special_item">Priority : ' + storeBrand.priority + '</div>');

                        store_list += elem.html();

                    }
                } else {
                    store_list += "No stores are available.";
                }

                $('.items_container').append(store_list);

                Main.elemRatio(function() {
                    $('.items_container .item_container').removeClass('invisible');
                });


            } else {
                alert(data.message);
            }

        };

        callback.loaderDiv = "body";
        callback.requestType = "GET";

        Main.request('/merchant/get_stores', {}, callback, headers);

    };

    Store.loadStore = function(storeId){

        $('.btn_edit').attr('href', '/merchant/store/form/edit/' + storeId);
        if(!initialized) initialize('readonly', true); else google.maps.event.trigger(map, 'resize');

        var dragged = false;
        function toggleSwitch(value, elem) {
            if(value == 'on') {
                elem.css({left: 0}).removeClass('off').addClass('on');
            } else {
                elem.css({left: 30}).removeClass('on').addClass('off');
            }
            dragged = false;
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
                document.title = storeBrand.brandName;
                $('.open_time').html(storeBrand.openingTime + ' - ' + storeBrand.closingTime);

                var store_location = '';
                var stores = storeBrand.store;
                for(var i = 0; i < stores.length; i++) {
                    var store = stores[i];
                    var elem = $('.block_store_container_template').clone();
                    $('.street', elem).html(store.street);
                    $('.city', elem).html(store.city + ', ' + store.state + ', ' + store.country);
                    $('.contact_person', elem).html(store.contactPerson);
                    $('.contact_no', elem).html(store.contactNo);
                    var location = latLngToLocation(store.latitude, store.longitude);
                    $('.btn_view_map', elem).attr('data-id', locationToKey(location));
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
                alert(data.message);
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

                        for(var i = 0; i < storeBrands.length; i++) {

                            var storeBrand = storeBrands[i];
                            if(storeBrand.id != storeId) {
                                var elem = $('.block_store_template').clone();

                                $('.item_image img', elem).attr('src', storeBrand.brandImage);
                                $('.item_name', elem).html('<a href="/merchant/store/view/' + storeBrand.id + '">' + storeBrand.brandName + '</a>');
                                $('.add_items', elem).attr('href', '/merchant/item/form/create/' + storeBrand.id);

                                if(storeBrand.featured == true) $('.item_image', elem).append('<div class="special_item">Featured</div>');
                                if(storeBrand.priority != undefined) $('.item_image', elem).append('<div class="special_item">Priority : ' + storeBrand.priority + '</div>');

                                store_list += elem.html();
                            }

                        }

                        $('.items_container .form_content').html(store_list);
                        $('.items_container').removeClass('hidden');

                        Main.elemRatio(function() {
                            $('.items_container .item_container').removeClass('invisible');
                        });

                    }

                } else {
                    alert(data.message);
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