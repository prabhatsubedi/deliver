if(typeof(Item) == "undefined") var Item = {};

var storesById = {};
var data_categories_id = 0;
var data_categories_names = [];

(function ($){


    Item.loadAddItem = function() {

        $('option:selected').removeAttr('selected');
        Image.dropZone('#product_image1_input', '#product_image1');
        Image.dropZone('#product_image2_input', '#product_image2');
        Image.dropZone('#product_image3_input', '#product_image3');

        $('select.haveall').live('change', function(){
            if($(this).val() != null && $('option', this).length == $(this).val().length + 1)
                $(this).selectpicker('selectAll');
        });

        $('select.compelselection').live('change', function(){
            if($(this).val() == null) $(this).selectpicker('selectAll');
        });

        var timeSelect = "";
        for(i = 0; i < 24; i++) {
            var time = i + "";
            time = (time.length == 1 ? "0" + time : time) + ":00:00";
            timeSelect += '<option value="' + time + '">' + time + '</option>';
        }
        $('#available_start_time, #available_end_time').append(timeSelect);

        $('#item_brand, .category_options, #available_start_time, #available_end_time').selectpicker({
            size: 5
        });

        function add_categories(category_options) {
            var elem = $('#category_container .category_options').eq(0);
            elem.html(category_options);
            elem.selectpicker('refresh');
        }
        function get_brand_categories(brandId) {
            var category_options = '<option value="none">Select Category</option>';
            if(brandId != undefined) {
                var callback = function(status, data) {

                    var categories = data.params.categories;
                    for(var i = 0; i < categories.length; i++) {
                        category_options += '<option value="' + categories[i].id + '">' + categories[i].name + '</option>';
                    }
                    add_categories(category_options)

                };
                callback.requestType = "GET";
                Main.request('/merchant/get_brands_categories', {}, callback, {id: brandId});
            } else {
                add_categories(category_options)
            }
        }

        function get_stores(headers) {
            var callback = function(status, data) {

                if (data.success == true) {

                    var brandList = '';
                    var storeBrands = data.params.storesBrand;
                    for(var i = 0; i < storeBrands.length; i++) {
                        var storeBrand = storeBrands[i];
                        storesById[storeBrand.id] = storeBrand.store;
                        brandList += '<option value="' + storeBrand.id + '">' + storeBrand.brandName + '</option>';
                    }
                    $('#item_brand').append(brandList);
                    $('#item_brand').selectpicker('refresh');

                } else {
                    alert(data.message);
                }

            };
            callback.requestType = "GET";
            Main.request('/merchant/get_stores', {}, callback, headers);
        }
        get_stores({merchantId: Main.getFromLocalStorage('mid')});

        $('#item_brand').live('change', function(){
            var brandId = $(this).val();
            var storeLocations = storesById[brandId];
            var stores_html = "";
            if(storeLocations != undefined) {
                for(var i = 0; i < storeLocations.length; i++) {
                    var storeLocation = storeLocations[i];
                    var ind_store = $('.item_store_template').clone();
                    $('.name_store', ind_store).html(storeLocation.street + ', ' + storeLocation.city + ', ' + storeLocation.state + ', ' + storeLocation.country);
                    $('.checkbox', ind_store).attr('data-id', storeLocation.id);
                    stores_html += ind_store.html();
                }
            } else {
                brandId = undefined;
            }
            get_brand_categories(brandId);
            $('#item_store_container').html(stores_html);
            $('#item_store_container .check_label').addClass('icon_full');
            $('#item_store_container .checkbox').attr('checked', 'checked');
        });

        $('#category_container .category_options').live('change', function(){

            var cat_id = $(this).val();
            var store_id = $('#item_brand').val();
            $(this).parent('.form-group').nextAll('.form-group').remove();

            if(cat_id != "none" && store_id != "none" && $('option:selected', this).attr('data-new') != "true") {

                var callback = function(status, data) {

                    if (data.success == true) {
                        console.log(data);
                        var categories = data.params.categories;
                        if(categories != null) {
                            var subCategory = $('.select_category_template').clone();
                            var cat_list = '';
                            for(var i = 0; i < categories.length; i++) {
                                cat_list += '<option value="' + categories[i].id + '">' + categories[i].name + '</option>';
                            }
                            $('select', subCategory).addClass('category_options').append(cat_list);
                            $('#category_container').append(subCategory.html());
                            $('#category_container .category_options').selectpicker('refresh');
                        }

                    } else {
                        alert(data.message);
                    }

                };
                Main.request('/merchant/get_child_categories', {parentCategoryId: cat_id, categoryStoreId: store_id}, callback);

            } else {

            }

        });
        $('#add_category').click(function(){
            $('.add_categories, #new_category, #new_subcategory').addClass('hidden');
            $('.add_categories, #new_category').removeClass('hidden');
        });
        $('#add_subcategory').click(function(){
            $('.add_categories, #new_category, #new_subcategory').addClass('hidden');
            $('.add_categories, #new_subcategory').removeClass('hidden');
        });
        $('#save_category').click(function(){
            if(!$('#new_category').hasClass('hidden')) {
                if(!$('#new_category').valid()) return false;
                $('#category_container .form-group').last().children('select.category_options').append('<option value="' + $('#new_category').val() + '" selected="selected" data-new="true">' + $('#new_category').val() + '</option>');
                $('.error', $('#category_container .form-group').last()).removeClass('error');
            } else {
                if(!$('#new_subcategory').valid()) return false;
                var subCategory = $('.select_category_template').clone();
                $('select', subCategory).addClass('category_options').append('<option value="' + $('#new_subcategory').val() + '" selected="selected" data-new="true">' + $('#new_subcategory').val() + '</option>');
                $('#category_container').append(subCategory.html());
            }
            $('#category_container .category_options').selectpicker('refresh');
            $('.add_categories, #new_category, #new_subcategory').addClass('hidden');
            $('#new_category, #new_subcategory').removeClass('error');
        });
        $('#cancel_category').click(function(){
            $('.add_categories, #new_category, #new_subcategory').addClass('hidden');
            $('#new_category, #new_subcategory').removeClass('error');
        });

        $('label.check_label .checkbox').removeAttr("checked");

        $('label.check_label .checkbox').live('click', function(){
            if($(this).prop('checked')) {
                $(this).parent('label').addClass("icon_full");
            } else {
                $(this).parent('label').removeClass("icon_full");
            }
        });

        $("label.check_label").live('mouseover', function ( event ) {
            $(this).addClass("icon_semi");
        });

        $("label.check_label").live('mouseout', function ( event ) {
            $(this).removeClass("icon_semi");
        });

        $('#add_attr_type').live('click', function(){
            $('.item_attributes').append($('.block_attr_template').html());
        });

        $('.add_attr').live('click', function(){
            $('.attr_list', $(this).parents('.block_attr')).append($('.attr_list_template').html());
        });

        $('.remove_attr_block').live('click', function(){
            $(this).parents('.block_attr').eq(0).remove();
        });

        $('.remove_attr').live('click', function(){
            if($('.attr_list .form-group', $(this).parents('.block_attr').eq(0)).length > 1) {
                $(this).parents('.form-group').eq(0).remove();
            } else {
                $(this).parents('.block_attr').eq(0).remove();
            }
        });

        function addAttr(container) {
            container.append($('.attr_list_template').html());
        }

        $.validator.setDefaults({
            errorPlacement : function(error, element){
                $('#error_container').html(error);
            },
            ignore: '.new_category_fields input'
        });

        $.validator.addMethod("categories", function(value, element, arg){
            var result = true;
            $('#category_container .category_options').each(function(){
                if($(this).val() == 'none') {
                    result = false;
                    $(this).siblings('.bootstrap-select').children('.form-control').addClass('error');
                } else {
                    $(this).siblings('.bootstrap-select').children('.form-control').removeClass('error');
                }
            });
            return result;
        }, "Please select any option.");

        function fieldRequired( element ) {
            return $.trim(element.val()).length > 0;
        }

        function fieldNumber( element ) {
            return /^-?(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/.test(element.val());
        }

        $.validator.addMethod("attributes", function(value, element, arg){
            var result = true;
            $('input[name="attr_type"], input[name="attr_label"]', '.item_attributes').each(function(){
                var sub_result = fieldRequired($(this));
                if(!sub_result) {
                    result = sub_result;
                    $(this).addClass('error');
                } else {
                    $(this).removeClass('error');
                }
            });
            $('input[name="attr_val"]', '.item_attributes').each(function(){
                var sub_result = fieldRequired($(this)) && fieldNumber($(this));
                if(!sub_result) {
                    result = sub_result;
                    $(this).addClass('error');
                } else {
                    $(this).removeClass('error');
                }
            });
            return result;
        }, "Please select any option.");

        $('input[name="attr_type"], input[name="attr_label"], input[name="attr_val"]', '.item_attributes').live('blur', function() {
            $('#validate_attributes').valid();
        });

        $.validator.addMethod("stores", function(value, element, arg){
            var result = $('#item_store_container .checkbox:checked').length > 0;
            if($('#item_brand').valid()) {
                if(!result) {
                    $('#item_store_container').addClass('error');
                } else {
                    $('#item_store_container').removeClass('error');
                }
            }
            return result;
        }, "Please select any option.");

        $('#item_store_container .checkbox').live('click', function() {
            $('#validate_stores').valid();
        });

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

/*        $.validator.addMethod("imageRequired", function(value, element, arg){
            var result = true;
            var container = $(element).prev('.drop_zone');
            if($('img', container).attr('src') == undefined || $('img', container).attr('src') == "") {
                result = false;
                container.addClass('error');
            } else {
                container.removeClass('error');
            }
            return result;
        }, "Only numbers are allowed.");*/

        $('#form_item').validate({
            submitHandler: function() {

                var data = {};
                var item = {};
                var itemCategories = [];
                var itemStores = [];
                var itemAttributesTypes = [];
                var itemImages = [];

                item.name = $('#name_item').val();
                item.description = $('#description').val();
                item.availableStartTime = $('#available_start_time').val();
                item.availableEndTime = $('#available_end_time').val();
                item.availableQuantity = 0;
                item.maxOrderQuantity = $('#max_order').val();
                item.minOrderQuantity = $('#min_order').val();
                item.unitPrice = $('#price').val();
                item.additionalOffer = $('#additional_offer').val();
                item.approxSize = "";
                item.approxWeight = "";
                item.returnPolicy = $('#return_policy').val();
                item.deliveryFee = $('#delivery_fee').val();
                item.promoCode = "";
                item.vat = $('#vat').val();
                item.serviceCharge = $('#service_charge').val();

/*                $('#category_container select.category_options').each(function(){
                    var cat_key = $('option:selected', this).attr('data-new') == "true" ? "name" : "id";
                    var category= {};
                    category[cat_key] = $(this).val();
                    itemCategories.push(category);
                });*/

                $('#category_container select.category_options').each(function(){
                    var category= {};
                    if($('option:selected', this).attr('data-new') == "true") {
                        category.name = $(this).val();
                        itemCategories.push(category);
                        data_categories_names.push($(this).val());
                    } else {
                        data_categories_id = $(this).val();
                        data_categories_names = [];
                        itemCategories = [];
                        category.id = $(this).val();
                        itemCategories.push(category);
                    }
                });

                $('#item_store_container .checkbox:checked').each(function(){
                    itemStores.push($(this).attr('data-id'));
                });

                $('.item_attributes .block_attr').each(function(){
                    var itemAttribute = {};
                    var itemAttributes = [];
                    itemAttribute.type = $('input[name="attr_type"]', this).val();
                    itemAttribute.multiSelect =  $('.checkbox', this).prop('checked');
                    $('.attr_list .attr_li', this).each(function(){
                        var attributes = {};
                        attributes.attribute = $('input[name="attr_label"]', this).val();
                        attributes.unitPrice = $('input[name="attr_val"]', this).val();
                        itemAttributes.push(attributes);
                    });
                    itemAttribute.itemsAttribute = itemAttributes;
                    itemAttributesTypes.push(itemAttribute);
                });

                $('.product_images img').each(function(){
                    if($(this).attr('src') != undefined || $(this).attr('src') != "") {
                        itemImages.push($(this).attr('src'));
                    }
                });

                data.item = item;
                data.itemCategories = itemCategories;
                data.itemStores = itemStores;
                data.itemAttributesTypes = itemAttributesTypes;
                data.itemImages = itemImages;

                Item.addItem(data, {id: $('#item_brand').val()});

                return false;
            }
        });

//        $('#product_image1_input').rules('add', {imageRequired: true});
//        $('#product_image2_input').rules('add', {imageRequired: true});
//        $('#product_image3_input').rules('add', {imageRequired: true});
        $('#name_item').rules('add', {required: true});
        $('#description').rules('add', {required: true});
//        $('#additional_offer').rules('add', {required: true});
        $('#item_brand').rules('add', {notEqual: 'none'});
        $('#validate_stores').rules('add', {stores: true});
        $('#validate_categories').rules('add', {categories: true});
        $('#validate_attributes').rules('add', {attributes: true});
        $('#available_start_time').rules('add', {notEqual: 'none'});
        $('#available_end_time').rules('add', {notEqual: 'none'});
        $('#min_order').rules('add', {required: true, digits: true, min: 1});
        $('#min_order').live('change', function(){
            $('#max_order').rules('add', {required: true, digits: true, min: eval($('#min_order').val()), messages: {min: "Please enter a value greater than or equal to min order."}});
        });
        $('#max_order').rules('add', {required: true, digits: true, min: eval($('#min_order').val()), messages: {min: "Please enter a value greater than or equal to min order."}});
//        $('#return_policy').rules('add', {required: true});
        $('#delivery_fee').rules('add', {required: true, number: true, min: 0});
        $('#vat').rules('add', {required: true, number: true, min: 0, max: 100});
        $('#service_charge').rules('add', {required: true, number: true, min: 0, max: 100});
        $('#price').rules('add', {required: true, number: true, min: 0});
        $('#new_category').rules('add', {required: true});
        $('#new_subcategory').rules('add', {required: true});

        $('select').live('change', function(){
            $(this).siblings('.bootstrap-select').children('.form-control').removeClass('error');
        });

        Item.addItem = function(data, headers) {

            $("button[type='submit']").attr("disabled", true);

            var callback = function (status, data) {
                $("button[type='submit']").removeAttr("disabled");

                if (data.success == true) {
                    alert(data.message);

                    $('#category_container select.category_options').each(function(){
                        if($('option:selected', this).attr('data-new') == "true") {
                            $(this).parent('.form-group').remove();
                        }
                    });

                    var j = 0;
                    function update_subcat(parent_id) {

                        Main.request('/merchant/get_child_categories', {parentCategoryId: parent_id, categoryStoreId: $('#item_brand').val()}, function(status, data) {

                            if (data.success == true) {
                                console.log(data);
                                var categories = data.params.categories;
                                if(categories != null) {
                                    var subCategory = $('.select_category_template').clone();
                                    var cat_list = '';
                                    for(var i = 0; i < categories.length; i++) {
                                        console.log(categories[i].name + ' ==== ' +data_categories_names[j]);
                                        cat_list += '<option value="' + categories[i].id + '" ' + (categories[i].name == data_categories_names[j] ? 'selected="selected"' : '') + '>' + categories[i].name + '</option>';
                                        if(categories[i].name == data_categories_names[j]) {
                                            data_categories_id = categories[i].id;
                                        }
                                    }
                                    $('select', subCategory).addClass('category_options').append(cat_list);
                                    $('#category_container').append(subCategory.html());
                                    $('#category_container .category_options').selectpicker('refresh');
                                    j++;
                                    update_subcat(data_categories_id);
                                }

                            } else {
                                alert(data.message);
                            }

                        });

                    }
                    update_subcat(data_categories_id);


                } else {
                    alert(data.message);
                }
            };

            callback.loaderDiv = "body";

            Main.request('/merchant/save_item', data, callback, headers);

        };

    };

})(jQuery);