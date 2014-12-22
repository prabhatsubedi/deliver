if(typeof(Item) == "undefined") var Item = {};

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

        $('.check_store .checkbox').removeAttr("checked");

        $('.check_store .checkbox').live('click', function(){
            if($(this).prop('checked')) {
                $(this).parent('label').addClass("icon_full");
            } else {
                $(this).parent('label').removeClass("icon_full");
            }
        });

        $(".check_store label").live('mouseover', function ( event ) {
            $(this).addClass("icon_semi");
        });

        $(".check_store label").live('mouseout', function ( event ) {
            $(this).removeClass("icon_semi");
        });

        $(".image_action.has_image").hover(function ( event ) {
            $(".change_image, .remove_image",this).removeClass("hidden");
        }, function ( event ) {
            $(".change_image, .remove_image",this).addClass("hidden");
        });

    };

})(jQuery);