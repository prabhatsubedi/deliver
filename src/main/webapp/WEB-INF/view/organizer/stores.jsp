<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Stores</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/manager.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-ui.js"></script>


</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Stores</h1>
        </div>
        <div class="main_content">
            <div class="items_container form_section row">
                <div class="form_head">Featured Stores</div>
                <div id="featured_stores" class="form_content clearfix"></div>
                <div class="form_head">Prioritized Stores</div>
                <div id="prioritized_stores" class="form_content clearfix"></div>
                <div class="form_head">Other Stores</div>
                <div class="form_content clearfix">
                    <div class="clearfix" id="other_stores"></div>
                    <div class="pagination_list col-lg-12">
                        <ul class="pagination pull-left"></ul>
                        <div class="num_items pull-right">
                            Show per Page
                            <select data-width="auto" name="select_num_items" class="select_num_items"></select>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="block_store_template hidden">
    <div class="item_container unselectable col-lg-2 col-md-3 col-xs-4 invisible">
        <div class="block_item">
            <div class="item_image maintain_ratio" mr-height="400" mr-width="720">
                <img class="img-responsive no_image">
                <a class="btn btn-default btn_logins view_store">View Store</a>
                <a class="btn btn-default btn_logins add_items">Add Item</a>
            </div>
            <div class="item_infos">
                <p class="item_name"></p>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">

    var pageSize = 12;
    $(document).ready(function(){

        $('.select_num_items').selectpicker();

        var re_calculate_width = true;
        $('.item_container').live('mouseover', function(){
            if(re_calculate_width == true) {
                $('.items_container .item_container').width($('.item_container').eq(0).width() - 1);
                $('.items_container .item_container').height($('.item_container').eq(0).height());
                re_calculate_width = false;
            }
        });
        $(window).resize(function() {
            $('.items_container .item_container').removeAttr('style');
            re_calculate_width = true;
        });

        $('.item_container a').live('click', function(e) {
            e.preventDefault();
            var elem_parent = $(this).parents('.item_container');
            Main.saveInLocalStorage('mid', elem_parent.attr('data-mid'));
            window.location = $(this).attr('href');
        });

        var sortableParam = {
            revert: true,
            tolerance: 'pointer',
            containment: '.body',
            stop: function( event, ui ) {
                var arrdata = [];
                $('#featured_stores .item_container').each(function(){
                    arrdata.push({id: $(this).attr('data-id'), featured: true});
                });
                $('#prioritized_stores .item_container').each(function(){
                    arrdata.push({id: $(this).attr('data-id'), priority: $(this).index() + 1});
                });
                var featured_count = $('#featured_stores .item_container').length;
                var prioritized_count = $('#prioritized_stores .item_container').length;
                Manager.check_store_type(featured_count, prioritized_count);
                Main.request('/organizer/update_special_brands', arrdata);
            }
        };

        $('#other_stores').sortable(sortableParam);
        $('#featured_stores').sortable(sortableParam);
        $('#prioritized_stores').sortable(sortableParam);

        Manager.stores({}, true);
        Manager.stores({pageNumber: 1, pageSize: pageSize});

        $('.pagination a').live('click', function(e){
            e.preventDefault();
        });

        $('.pagination li:not(".disabled") a').live('click', function(){
            $('.items_container .item_container').removeAttr('style');
            re_calculate_width = true;
            Manager.stores({pageNumber: $(this).attr('pageno'), pageSize: $('.select_num_items').val()});
        });

        $('.select_num_items').live('change', function(){
            $('.items_container .item_container').removeAttr('style');
            re_calculate_width = true;
            Manager.stores({pageNumber: 1, pageSize: $('.select_num_items').val()});
        });

    });

</script>

</body>
</html>
