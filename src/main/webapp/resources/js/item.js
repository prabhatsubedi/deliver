if(typeof(Item) == "undefined") var Item = {};

var storeBrands = undefined;

(function ($){


    Item.loadAddItem = function() {

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

        $('#item_category').selectpicker({
            size: 5
        });

        $('#item_brand').selectpicker({
            size: 5
        });

        function get_brand_categories() {
            var callback = function(status, data) {

            };
            callback.requestType = "GET";
            Main.request('/merchant/get_brands_categories', {id: "barndId"}, callback);
        }

        function get_stores(headers) {
            var callback = function(status, data) {

                if (data.success == true) {

                    var brandList = '';
                    storeBrands = data.params.storesBrand;
                    for(var i = 0; i < storeBrands.length; i++) {
                        brandList += '<option value="' + storeBrands[i].id + '">' + storeBrands[i].brandName + '</option>';
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