if(typeof(Item) == "undefined") var Item = {};

var storesById = {};
var data_categories_id = 0;
var data_categories_names = [];

(function ($){


    Item.loadAddItem = function() {

        var action = Main.getURLvalue(3);
        var addBrandId = Main.getURLvalue(4);
        var itemId = Main.getURLvalue(5);

        if(action == "edit") {
            itemId = Main.getURLvalue(4);
            $('.heading h1').html('Item Edit');
            document.title = 'Item Edit';
            $('#form_item button[type="submit"]').attr({'data-action': 'update'}).html('Update Item');
        }

        $('option:selected').removeAttr('selected');
        Image.dropZone('#product_image1_input', '#product_image1');
        Image.dropZone('#product_image2_input', '#product_image2');
        Image.dropZone('#product_image3_input', '#product_image3');

        $('.product_image').hover(function(){
            if($('img', this).length == 1)$('.remove_image', this).removeClass('hidden');
        }, function(){
            $('.remove_image', this).addClass('hidden');
        });
        $('.product_image .remove_image').click(function(){
            $(this).addClass('hidden').siblings('.drop_zone').html('<div class="drop_info">Drop image file <br> (or click to browse)</div>');
        });

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

        var loadPopulate = false;

        function populateEdit(itemId) {
            var callback = function(status, data) {

                console.log(data);

                if (data.success == true) {

                    var item = data.params.item;
                    var itemImages = item.itemsImage;
                    var storesBrand = item.storesBrand;
                    var itemStores = item.itemsStores;
                    var itemCategory = item.category;
                    var attributesTypes = item.attributesTypes;

                    var brandId = storesBrand.id;
                    $('#item_brand').val(brandId);
                    $('#item_brand').selectpicker('refresh');

                    var change_callback = function() {
                        var catId = itemCategory.id;
                        $('.category_options').val(catId);
                        $('.category_options').selectpicker('refresh');
                        function getChild(category) {
                            if(category.child.length == 1) {
                                var childCat = category.child[0];
                                catId = childCat.id;
                                var fnParams = {};
                                fnParams.elem = $('#category_container select.category_options').last();
                                fnParams.brandId = brandId;
                                fnParams.catId = category.id;
                                fnParams.currentCatId = catId;
                                getChildCats(fnParams);
                                getChild(childCat);
                            }
                        }
                        getChild(itemCategory);
                    };
                    change_callback.stores = itemStores;
                    brand_change(brandId, change_callback);

                    if(action == 'edit') {

                        for(var i = 0; i < itemImages.length; i++) {
                            $('.product_image .drop_zone').eq(i).attr('data-id', itemImages[i].id).html('<img src="' + itemImages[i].url + '" style="height: 100%;" class="img-responsive" />');
                        }

                        $('#form_item button[type="submit"]').attr({'data-id': item.id});
                        $('#name_item').val(item.name);
                        $('#description').val(item.description);
                        $('#additional_offer').val(item.additionalOffer);

                        $('#available_start_time').val(item.availableStartTime);
                        $('#available_end_time').val(item.availableEndTime);
                        $('#available_start_time').selectpicker('refresh');
                        $('#available_end_time').selectpicker('refresh');
                        $('#min_order').val(item.minOrderQuantity);
                        $('#max_order').val(item.maxOrderQuantity);
                        $('#vat').val(item.vat);
                        $('#service_charge').val(item.serviceCharge);
                        $('#price').val(item.unitPrice);

                        var attributes_types = "";
                        if(attributesTypes.length > 0) {
                            for(var i = 0; i < attributesTypes.length; i++) {
                                var attributesType = attributesTypes[i];
                                var itemAttributes = attributesType.itemsAttribute;
                                var itemAttribute = itemAttributes[0];
                                var mainElem = $('.block_attr_template').clone();

                                $('.check_span', mainElem).addClass(attributesType.multiSelect ? 'icon_full' : '');
                                $('.checkbox', mainElem).attr('checked', attributesType.multiSelect);
                                $('.remove_attr_block', mainElem).addClass('hidden');
                                $('input[name="attr_type"]', mainElem).attr({'data-id': attributesType.id, 'value': attributesType.type});
                                $('input[name="attr_label"]', mainElem).attr({'data-id': itemAttribute.id, 'value': itemAttribute.attribute});
                                $('input[name="attr_val"]', mainElem).attr('value', itemAttribute.unitPrice);
                                $('.remove_attr', mainElem).addClass('hidden');
                                $('.with_remove', mainElem).removeClass('with_remove');

                                for(var j = 1; j < itemAttributes.length; j++) {
                                    itemAttribute = itemAttributes[j];
                                    var childElem = $('.attr_list_template').clone();
                                    $('input[name="attr_label"]', childElem).attr({'data-id': itemAttribute.id, 'value': itemAttribute.attribute});
                                    $('input[name="attr_val"]', childElem).attr('value', itemAttribute.unitPrice);
                                    $('.remove_attr', childElem).addClass('hidden');
                                    $('.with_remove', childElem).removeClass('with_remove');
                                    $('.attr_list', mainElem).append(childElem.html());
                                }
                                attributes_types += mainElem.html();
                            }
                        }
                        $('.item_attributes').append(attributes_types);

                    }

                } else {
                    alert(data.message);
                }

            };
            callback.requestType = "GET";
            callback.loaderDiv = "body";
            Main.request('/merchant/get_items_detail', {}, callback, {id: itemId});
        }

        function add_categories(category_options, change_callback) {
            var elem = $('#category_container .category_options').eq(0);
            elem.parent('.form-group').nextAll('.form-group').remove();
            elem.html(category_options);
            elem.selectpicker('refresh');
            if(change_callback != undefined) change_callback();
        }
        function get_brand_categories(brandId, change_callback) {
            var category_options = '<option value="none">Select Category</option>';
            if(brandId != undefined) {
                var callback = function(status, data) {

                    var categories = data.params.categories;
                    for(var i = 0; i < categories.length; i++) {
                        category_options += '<option value="' + categories[i].id + '">' + categories[i].name + '</option>';
                    }
                    add_categories(category_options, change_callback)

                };
                callback.requestType = "GET";
                Main.request('/merchant/get_brands_categories', {}, callback, {id: brandId});
            } else {
                add_categories(category_options)
            }
        }

        function brand_change(brandId, change_callback) {

            var storeLocations = storesById[brandId];
            var stores_html = "";
            var brand_stores;
            if(change_callback != undefined) brand_stores = change_callback.stores;
            var stores_id = [];
            if(brand_stores != undefined) {
                for(var i = 0; i < brand_stores.length; i++) {
                    stores_id.push(brand_stores[i].store.id);
                }
            }
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
            get_brand_categories(brandId, change_callback);
            $('#item_store_container').html(stores_html);
            if(stores_id.length > 0) {
                $('#item_store_container .checkbox').each(function(){
                    if($.inArray(parseInt($(this).attr('data-id')), stores_id) > -1) {
                        $(this).attr('checked', 'checked');
                        $(this).siblings('.check_span').addClass('icon_full');
                    }
                });
            } else {
                $('#item_store_container .checkbox').attr('checked', 'checked');
                $('#item_store_container .check_span').addClass('icon_full');
            }

            $('#available_start_time').val($('option:selected', '#item_brand').attr('data-open'));
            $('#available_start_time').selectpicker('refresh');
            $('#available_end_time').val($('option:selected', '#item_brand').attr('data-close'));
            $('#available_end_time').selectpicker('refresh');

        }

        function get_stores(headers) {
            var callback = function(status, data) {
                var brand_id = undefined;

                if (data.success == true) {

                    var brandList = '';
                    var storeBrands = data.params.storesBrand;
                    for(var i = 0; i < storeBrands.length; i++) {
                        var storeBrand = storeBrands[i];
                        storesById[storeBrand.id] = storeBrand.store;
                        brandList += '<option value="' + storeBrand.id + '" data-open="' + storeBrand.openingTime + '" data-close="' + storeBrand.closingTime + '" >' + storeBrand.brandName + '</option>';
                    }
                    $('#item_brand').append(brandList);

                    if(addBrandId != undefined && $.isNumeric(addBrandId) && action == 'create') {
                        brand_id = addBrandId;
                        $('#item_brand').val(brand_id);
                        brand_change(brand_id);
                    }
                    $('#item_brand').selectpicker('refresh');

                    if((action == 'edit' || (itemId != undefined && $.isNumeric(itemId) && action == 'create')) && !loadPopulate){
                        populateEdit(itemId);
                        loadPopulate = true;
                    }

                } else {
                    alert(data.message);
                }

            };
            callback.requestType = "GET";
            Main.request('/merchant/get_stores', {}, callback, headers);
        }
        get_stores({merchantId: Main.getFromLocalStorage('mid')});

        $('#item_brand').live('change', function(){
            brand_change($(this).val());
        });

        function getChildCats(fnParams) {

            var elem = fnParams.elem;
            var brandId = fnParams.brandId;
            var catId = fnParams.catId;
            var currentCatId = fnParams.currentCatId;

            elem.parent('.form-group').nextAll('.form-group').remove();

            if(catId != "none" && brandId != "none" && $('option:selected', elem).attr('data-new') != "true") {

                var callback = function(status, data) {

                    if (data.success == true) {
                        console.log(data);
                        var categories = data.params.categories;
                        if(categories.length > 0) {
                            var subCategory = $('.select_category_template').clone();
                            var cat_list = '';
                            for(var i = 0; i < categories.length; i++) {
                                cat_list += '<option value="' + categories[i].id + '" ' + (currentCatId == categories[i].id ? 'selected="selected"' : '') + (categories[i].item == undefined ? '' : 'data-item="true"') + '>' + categories[i].name + '</option>';
                            }
                            $('select', subCategory).addClass('category_options').append(cat_list);
                            $('#category_container').append(subCategory.html());
                            $('#category_container .category_options').selectpicker('refresh');
                        }

                    } else {
                        alert(data.message);
                    }

                };

                if(currentCatId != undefined) callback.async = false;
                Main.request('/merchant/get_child_categories', {parentCategoryId: catId, categoryStoreId: brandId}, callback);

            } else {

            }

        }

        $('.category_options').on('show.bs.dropdown', function () {
            if(!$('#item_brand').valid()) {
                alert("Please select store first.");
                return false;
            }
        })

        $('#category_container .category_options').live('change', function(){
            var brandId = $('#item_brand').val();
            var catId = $(this).val();

            var fnParams = {};
            fnParams.elem = $(this);
            fnParams.brandId = brandId;
            fnParams.catId = catId;

            getChildCats(fnParams);
        });

        $('#add_category').click(function(){
            $('.add_categories, #new_category, #new_subcategory').addClass('hidden');
            $('.add_categories, #new_category').removeClass('hidden');
        });
        $('#add_subcategory').click(function(){
            var last_child = $('#category_container select.category_options').last();
            if($('option:selected', last_child).attr('data-item') == undefined) {
                $('.add_categories, #new_category, #new_subcategory').addClass('hidden');
                $('.add_categories, #new_subcategory').removeClass('hidden');
            } else {
                alert('Subcategory cannot be added to this category. Since, this category contain Items.');
            }
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

                if($('#category_container select.category_options').length < 2) {
                    alert('Item is not allowed to add to main category.');
                    return false;
                }

                var chk_confirm = confirm('Are you sure you want to ' + (action == 'edit' ? 'update' : 'add') + ' item?');
                if (!chk_confirm) return false;

                var data = {};
                var item = {};
                var itemCategories = [];
                var itemStores = [];
                var itemAttributesTypes = [];
                var itemImages = [];

                item.id = $('#form_item button[type="submit"]').attr('data-id');
                item.name = $('#name_item').val();
                if($('#description').val() != "") item.description = $('#description').val();
                item.availableStartTime = $('#available_start_time').val();
                item.availableEndTime = $('#available_end_time').val();
                //item.availableQuantity = 0;
                item.maxOrderQuantity = $('#max_order').val();
                item.minOrderQuantity = $('#min_order').val();
                item.unitPrice = $('#price').val();
                if($('#additional_offer').val() != "N/A" && $('#additional_offer').val() != "") item.additionalOffer = $('#additional_offer').val();
                //item.approxSize = "";
                //item.approxWeight = "";
                if($('#return_policy').val() != "N/A" && $('#return_policy').val() != "") item.returnPolicy = $('#return_policy').val();
                if($('#delivery_fee').val() != "0") item.deliveryFee = $('#delivery_fee').val();
                //item.promoCode = "";
                if($('#vat').val() != "0") item.vat = $('#vat').val();
                if($('#service_charge').val() != "0") item.serviceCharge = $('#service_charge').val();
                item.status = "ACTIVE";

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
                    itemAttribute.id = $('input[name="attr_type"]', this).attr('data-id');
                    itemAttribute.multiSelect =  $('.checkbox', this).prop('checked');
                    $('.attr_list .attr_li', this).each(function(){
                        var attributes = {};
                        attributes.id = $('input[name="attr_label"]', this).attr('data-id');
                        attributes.attribute = $('input[name="attr_label"]', this).val();
                        attributes.unitPrice = $('input[name="attr_val"]', this).val();
                        itemAttributes.push(attributes);
                    });
                    itemAttribute.itemsAttribute = itemAttributes;
                    itemAttributesTypes.push(itemAttribute);
                });

                $('.product_images img').each(function(){
                    if(action == 'edit') {
                        if($(this).attr('data-new') == 'true' && ($(this).attr('src') != undefined || $(this).attr('src') != "")) {
                            itemImages.push({id: $(this).parent('.drop_zone').attr('data-id'), url: $(this).attr('src')});
                        }
                    } else {
                        if($(this).attr('data-new') == 'true' && ($(this).attr('src') != undefined || $(this).attr('src') != "")) {
                            itemImages.push($(this).attr('src'));
                        }
                    }
                });

                data.item = item;
                data.itemCategories = itemCategories;
                data.itemStores = itemStores;
                data.itemAttributesTypes = itemAttributesTypes;
                if(action == 'edit')
                    data.editItemImages = itemImages;
                else
                    data.itemImages = itemImages;

                Item.addItem(data, {id: $('#item_brand').val()}, $('#form_item button[type="submit"]').attr('data-action'));

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

    };

    Item.addItem = function(data, headers, data_action) {

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
                            if(categories.length > 0) {
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

                $('.product_image .drop_zone').html('<div class="drop_info">Drop image file <br> (or click to browse)</div>');
                $('#name_item, #description, #min_order, #max_order, #price').val('');
                $('#additional_offer, #return_policy').val('N/A');
                $('#delivery_fee, #vat, #service_charge').val('0');
                $('.item_attributes').html('');

            } else {
                alert(data.message);
            }
        };

        callback.loaderDiv = "body";

        var callback_edit = function (status, data) {
            $("button[type='submit']").removeAttr("disabled");

            alert(data.message);
            if (data.success == true) {
                $('.product_images img[data-new="true"]').removeAttr('data-new');
            }
        };

        callback_edit.loaderDiv = "body";

        if(data_action == 'update') {
            Main.request('/merchant/update_item', data, callback_edit, headers);
        } else {
            Main.request('/merchant/save_item', data, callback, headers);
        }

    };

    Item.loadListItems = function() {

        $('#item_brand').selectpicker();
        Item.getBrands();

        $('#item_brand').live('change', function(e){
            Item.getBrandCategories($(this).val());
        });

        function treeAction(elem, action) {

            if(action == 'open') {
                elem.parent('a').siblings('ul').removeClass('hidden');
                elem.removeClass('glyphicon-plus').addClass('glyphicon-minus');
                $('.glyphicon-minus',elem.parent('a').parent('li').siblings('li')).removeClass('glyphicon-minus').addClass('glyphicon-plus');
                $('ul', elem.parent('a').parent('li').siblings('li')).addClass('hidden');
            } else {
                elem.parent('a').siblings('ul').addClass('hidden');
                elem.removeClass('glyphicon-minus').addClass('glyphicon-plus');
                $('.glyphicon-minus', elem.parent('a').parent('li')).removeClass('glyphicon-minus').addClass('glyphicon-plus');
                $('ul', elem.parent('a').parent('li')).addClass('hidden');
            }

        }

        $('.cateogry_list a').live('click', function(e){
            e.preventDefault();
            if(e.target == this) {

                var cat_name = $(this).text();

                treeAction($('.glyphicon', this), 'open');

                $('.current_category').removeClass('current_category');
                $(this).addClass('current_category');

                if($(this).siblings('ul').length == 1) {

                    var callback = function(status, data) {

                        console.log(data);

                        if (data.success == true) {

                            var item_list = '<div class="form_section">';
                            var categories = data.params.categories;
                            for(var i = 0; i < categories.length; i++) {
                                var category = categories[i];
                                if(category.item.length > 0) {
                                    item_list += '<div class="form_head">' + category.name + '</div>';
                                    item_list += '<div class="form_content clearfix">';

                                    for(var j = 0; j < category.item.length; j++) {
                                        var item = category.item[j];
                                        var elem = $('.item_container_template').clone();
                                        if(item.itemsImage.length > 0) $('.item_image img', elem).attr('src', item.itemsImage[0].url);
                                        $('.item_name a', elem).attr('href', '/merchant/item/view/' + item.id).html(item.name);
                                        $('.item_price span', elem).html(item.unitPrice);
                                        item_list += elem.html();
                                    }

                                    item_list += '<a href="#" class="view_more" data-id="' + category.id + '">View More >></a>';

                                    item_list += '</div>';
                                }
                            }
                            item_list += '</div>';

                            $('.items_container').html(item_list);

                        } else {
                            alert(data.message);
                        }

                    };

                    callback.loaderDiv = ".items_container";
                    Main.request('/merchant/get_parent_categories_items', {parentCategoryId: $(this).attr('data-id'), categoryStoreId: $('#item_brand').val()}, callback);

                } else {

                    var callback = function(status, data) {

                        console.log(data);

                        if (data.success == true) {

                            var item_list = '';
                            var items = data.params.items;

                            var item_list = '<div class="form_section">';
                            item_list += '<div class="form_head">' + cat_name + '</div>';
                            item_list += '<div class="form_content clearfix">';
                            for(var j = 0; j < items.length; j++) {
                                var item = items[j];
                                var elem = $('.item_container_template').clone();
                                if(item.itemsImage.length > 0) $('.item_image img', elem).attr('src', item.itemsImage[0].url);
                                $('.item_name a', elem).attr('href', '/merchant/item/view/' + item.id).html(item.name);
                                $('.item_price span', elem).html(item.unitPrice);
                                item_list += elem.html();
                            }
                            item_list += '</div>';
                            item_list += '</div>';

                            $('.items_container').html(item_list);

                        } else {
                            alert(data.message);
                        }

                    };

                    callback.loaderDiv = ".items_container";
                    Main.request('/merchant/get_categories_items', {parentCategoryId: $(this).attr('data-id'), categoryStoreId: $('#item_brand').val()}, callback);

                }

            }
        });

        $('a.view_more').live('click', function(){

            var elem = $('.cateogry_list a[data-id="' + $(this).attr('data-id') + '"]');
            elem.parents('ul.hidden').removeClass('hidden');
            elem.parents('ul.nav').siblings('a').children('.glyphicon').removeClass('glyphicon-plus').addClass('glyphicon-minus');
            $(elem).trigger('click');

        });

        $('.cateogry_list .glyphicon').live('click', function(){
            var elem = $(this);
            if(elem.hasClass('glyphicon-plus')) {
                treeAction(elem, 'open')
            } else if (elem.hasClass('glyphicon-minus')) {
                treeAction(elem, 'close')
            }
        });

    };

    Item.getBrands = function() {

        var callback = function(status, data) {

            if (data.success == true) {

                var brandList = '';
                var storeBrands = data.params.storesBrand;
                if(storeBrands.length > 0) {
                    for(var i = 0; i < storeBrands.length; i++) {
                        var storeBrand = storeBrands[i];
                        storesById[storeBrand.id] = storeBrand.store;
                        brandList += '<option value="' + storeBrand.id + '" ><img src="' + storeBrand.brandLogo + '" />' + storeBrand.brandName + '</option>';
                    }
                    $('#item_brand').html(brandList);
                    $('#item_brand').selectpicker('refresh');
                    $('.heading h1 .bootstrap-select').removeClass('hidden');

                    Item.getBrandCategories(storeBrands[0].id);
                }

            } else {
                alert(data.message);
            }

        };

        callback.requestType = "GET";
        Main.request('/merchant/get_stores', {}, callback, {merchantId: Main.getFromLocalStorage('mid')});

    };

    Item.getBrandCategories = function(brandId) {

        var callback = function(status, data) {

            var categories = data.params.categories;
            var category_list = '';
            var padding = 40;
            function categoryList(categories, padding, load) {
                category_list += '<ul class="nav nav-stacked ' + (load ? "" : "hidden") + '">';
                for(var i = 0; i < categories.length; i++) {
                    category_list += '<li><a href="#" data-id="' + categories[i].id + '" style="padding-left: ' + padding + 'px"><span class="glyphicon ' + (categories[i].child.length > 0 ? 'glyphicon-plus' : '') + '"></span>' + categories[i].name + '</a>';
                    if(categories[i].child.length > 0) categoryList(categories[i].child, padding + 20);
                    category_list += '</li>';
                }
                category_list += '</ul>';
            }
            categoryList(categories, padding, true);
            $('.cateogry_list').html(category_list);
            $('.cateogry_list > ul > li:first-child > a').trigger('click');

        };
        callback.requestType = "GET";
        callback.loaderDiv = ".body";
        Main.request('/merchant/get_brands_categories', {}, callback, {id: brandId});

    };

    Item.loadItem = function(itemId) {

        $('.btn_edit').attr('href', '/merchant/item/form/edit/' + itemId);

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

        var callback = function(status, data) {

            console.log(data);

            if (data.success == true) {

                var item = data.params.item;
                var itemImages = item.itemsImage;
                var storesBrand = item.storesBrand;
                var itemStores = item.itemsStores;
                var itemCategory = item.category;
                var attributesTypes = item.attributesTypes;

                document.title = item.name;
                $('.item_info .form_head .item_name').html(item.name);
                $('.item_info .form_head .item_price span').html(item.unitPrice);

                for(var i = 0; i < itemImages.length; i++) {
                    $('.product_image .drop_zone').eq(i).html('<img src="' + itemImages[i].url + '" style="height: 100%;" class="img-responsive" />');
                }

                $('.item_info .description').html(item.description);
                $('.item_info .additional_offer').html(item.additionalOffer);

                var item_stores = "";
                for(var i = 0; i < itemStores.length; i++) {
                    var storeLocation = itemStores[i].store;
                    item_stores +='<li>' + storeLocation.street + ', ' + storeLocation.city + ', ' + storeLocation.state + ', ' + storeLocation.country + '</li>';
                }
                $('.item_info .brand_name').html(storesBrand.brandName);
                var brandId = storesBrand.id;
                $('.item_info .detail_list').html(item_stores);

                var catName = itemCategory.name;
                var item_categories = '<li>' + catName + '</li>';
                var item_category = itemCategory.id;
                var child_cat_id;
                    function getChild(category) {
                    if(category.child.length == 1) {
                        var childCat = category.child[0];
                        child_cat_id = childCat.id;
                        catName = childCat.name;
                        item_categories += '<li>' + catName + '</li>';
                        getChild(childCat);
                    }
                }
                getChild(itemCategory);

                $('.heading h1').html(catName);
                $('.heading .btn').attr('href', '/merchant/item/form/create/' + brandId + '/' + itemId);
                $('.item_info .item_category').html(item_categories);

                $('.item_info .available_time').html(item.availableStartTime + " - " + item.availableEndTime);
                $('.item_info .order_quantity').html("Min " + item.minOrderQuantity + " - Max " + item.maxOrderQuantity);
                $('.item_info .vat').html(item.vat + " %");
                $('.item_info .service_charge').html(item.serviceCharge + " %");

                var attributes_types = "";
                if(attributesTypes.length > 0) {
                    for(var i = 0; i < attributesTypes.length; i++) {
                        var attributesType = attributesTypes[i];
                        var itemAttributes = attributesType.itemsAttribute;
                        attributes_types += '<thead>';
                        attributes_types += '<tr>';
                        attributes_types += '<th colspan="3">' + attributesType.type + '</th>';
                        attributes_types += '</tr>';
                        attributes_types += '</thead>';
                        attributes_types += '<tbody>';
                        for(var j = 0; j < itemAttributes.length; j++) {
                            var itemAttribute = itemAttributes[j];
                            attributes_types += '<tr>';
                            attributes_types += '<td class="select_option"><span class="' + (attributesType.multiSelect ? 'multi_select' : 'single_select') + '"></span></td>';
                            attributes_types += '<td class="attrib_name">' + itemAttribute.attribute + '</td>';
                            attributes_types += '<td>Rs. ' + itemAttribute.unitPrice + '</td>';
                            attributes_types += '</tr>';
                        }
                        attributes_types += '</tbody>';
                    }
                } else {
                    attributes_types += '<tbody><tr><td colspan="4" class="text-center">no attributes available</td></tr></tbody>';
                }
                $('.item_info .pricing_attributes table').append(attributes_types);

                var callback = function(status, data) {

                    console.log(data);

                    if (data.success == true) {

                        var item_list = '';
                        var items = data.params.items;
                        if(items.length > 0) {
                            for(var j = 0; j < items.length; j++) {
                                var item = items[j];
                                if(item.id !=itemId) {
                                    var elem = $('.item_container_template').clone();
                                    if(item.itemsImage.length > 0) $('.item_image img', elem).attr('src', item.itemsImage[0].url);
                                    $('.item_name a', elem).attr('href', '/merchant/item/view/' + item.id).html(item.name);
                                    $('.item_price span', elem).html(item.unitPrice);
                                    item_list += elem.html();
                                }
                            }

                            $('.items_container .form_content').html(item_list);
                            $('.items_container').removeClass('hidden');
                        }

                    }

                };

                Main.request('/merchant/get_categories_items', {parentCategoryId: child_cat_id, categoryStoreId: brandId}, callback);

            } else {
                alert(data.message);
            }

        };
        callback.requestType = "GET";
        callback.loaderDiv = "body";
        Main.request('/merchant/get_items_detail', {}, callback, {id: itemId});
    }

})(jQuery);