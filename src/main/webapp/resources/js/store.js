if(typeof(Store) == "undefined") var Store = {};

(function ($){

    Store.loadAddStore = function() {

        $('option:selected').removeAttr('selected');
        Image.dropZone('#brand_image_input', '#brand_image');
        Image.dropZone('#brand_logo_input', '#brand_logo');

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
            time = time.length == 1 ? "0" + time : time;
            timeSelect += '<option value="' + time + '">' + time + '</option>';
        }
        $('#open_time, #close_time').append(timeSelect);

        $('#open_time').selectpicker({size: 5});
        $('#close_time').selectpicker({size: 5});

        function updateGeoPoints(alert) {

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
                    if(alert) alert('All fields of all store locations are required.');
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
                if(geoPoints.valid) {

                    var data = {};
                    var stores_brand = {};

                    stores_brand.brandName = $('#brand_name').val();
                    stores_brand.openingTime = $('#open_time').val();
                    stores_brand.closingTime = $('#close_time').val();
                    stores_brand.brandLogo = $('#brand_logo img').attr('src');
                    stores_brand.brandImage = $('#brand_image img').attr('src');
                    stores_brand.brandUrl = $('#brand_url').val();


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

                    Store.addStore(data, {merchantId: Main.getFromLocalStorage('mid')});

                }
                return false;
            }
        });

        $('#brand_name').rules('add', {required: true});
        $('#brand_image_input').rules('add', {imageRequired: true});
        $('#brand_logo_input').rules('add', {imageRequired: true});
        $('#open_time').rules('add', {notEqual: "none"});
        $('#close_time').rules('add', {notEqual: "none"});
        $('#brand_url').rules('add', {required: true, url: true});
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

    };

    Store.addStore = function(data, headers) {

        $("button[type='submit']").attr("disabled", true);

        var callback = function (status, data) {
            $("button[type='submit']").removeAttr("disabled");

            if (data.success == true) {
                alert(data.message);
            } else {
                alert(data.message);
            }
        };

        callback.loaderDiv = "body";

        Main.request('/merchant/save_store', data, callback, headers);

    };

    Store.listStores = function(headers) {

        var callback = function (status, data) {

            if (data.success == true) {

                var storeBrands = data.params.storesBrand;

                var elem = $('.box_store_template');

                var store_list = "";

                for(var i = 0; i < storeBrands.length; i++) {

                    var storeBrand = storeBrands[i];
                    console.log(storeBrand);
                    $('.store_logo img', elem).attr('src', storeBrand.brandLogo);
                    $('.store_name', elem).html(storeBrand.brandName);
                    var stores = storeBrand.store;
                    var address_list = "";
                    for(var j in stores) {
                        var store = stores[j];
                        var address = [];
                        address.push(store.street);
                        address.push(store.city);
                        address.push(store.state);
                        address.push(store.country);
                        address_list += '<li>' + address.join(', ') + '</li>';
                    }
                    $('.store_address_list ul', elem).html(address_list);
                    $('.add_items').attr('href', '/merchant/item/form/create/' + storeBrand.id);

                    store_list += $('.box_store_template').html();

                }

                $('.main_content').append(store_list);


            } else {
                alert(data.message);
            }

        };

        callback.loaderDiv = "body";
        callback.requestType = "GET";

        Main.request('/merchant/get_stores', {}, callback, headers);

    };

})(jQuery);