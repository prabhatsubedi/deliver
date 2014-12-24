if(typeof(Item) == "undefined") var Item = {};

var storesById = {};

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
                    stores_html += ind_store.html();
                }
            } else {
                brandId = undefined;
            }
            get_brand_categories(brandId);
            $('#item_store_container').html(stores_html);
        });

        $('#category_container .category_options').live('change', function(){

            var cat_id = $(this).val();
            var store_id = $('#item_brand').val();
            $(this).parent('.form-group').nextAll('.form-group').remove();

            if(cat_id != "none" && store_id != "none") {

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
                $('#category_container .form-group').last().children('select.category_options').append('<option value="' + $('#new_category').val() + '" selected="selected">' + $('#new_category').val() + '</option>');
            } else {
                var subCategory = $('.select_category_template').clone();
                $('select', subCategory).addClass('category_options').append('<option value="' + $('#new_subcategory').val() + '" selected="selected"g>' + $('#new_subcategory').val() + '</option>');
                $('#category_container').append(subCategory.html());
            }
            $('#category_container .category_options').selectpicker('refresh');
            $('.add_categories, #new_category, #new_subcategory').addClass('hidden');
        });
        $('#cancel_category').click(function(){
            $('.add_categories, #new_category, #new_subcategory').addClass('hidden');
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

/*        $(".image_action.has_image").hover(function ( event ) {
            $(".change_image, .remove_image",this).removeClass("hidden");
        }, function ( event ) {
            $(".change_image, .remove_image",this).addClass("hidden");
        });*/

    };

})(jQuery);