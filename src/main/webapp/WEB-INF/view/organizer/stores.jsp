<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Stores</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-ui.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen" />

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/manager.js"></script>


</head>
<body class="page_stores">

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Stores</h1>
            <div class="pull-right">
                <div class="switch_view">
                    <ul class="nav nav-pills">
                        <li>
                            <button id="show_table" class="btn btn-lg glyphicon glyphicon-align-justify selected_view" type="button"></button>
                        </li>
                        <li>
                            <button id="show_block" class="btn btn-lg glyphicon glyphicon-th" type="button"></button>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="main_content">

            <div class="items_container table-view row hidden not_loaded">

                <table id="stores_table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th><div class="width_150"> Store Name </div></th>
                        <th> No. of Stores </th>
                        <th> Featured </th>
                        <th> Priority </th>
                        <th> Status </th>
                        <th><div class="width_150"> Action </div></th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>

            </div>

            <div class="items_container thumbnail-view form_section row">
                <div class="form_head">Featured Stores</div>
                <div id="featured_stores" class="form_content clearfix"></div>
                <div class="form_head">Prioritized Stores</div>
                <div id="prioritized_stores" class="form_content clearfix"></div>
                <div class="form_head">Other Stores</div>
                <div class="form_content other_stores clearfix">
                    <div class="clearfix" id="other_stores"></div>
                    <div class="pagination_list col-lg-12">
                        <ul class="pagination pull-left"></ul>
                        <div class="num_items pull-right">
                            Show per Page
                            <select data-width="auto" name="select_num_items" class="select_num_items"></select>

                        </div>
                    </div>
                </div>
                <div class="form_head inactive_stores hidden">Inactive Stores</div>
                <div class="form_content inactive_stores clearfix hidden">
                    <div class="clearfix" id="inactive_stores"></div>
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
    <div class="item_container unselectable col-lg-2 col-md-3 col-sm-4 col-xs-6 invisible">
        <div class="block_item">
            <div class="item_image maintain_ratio" mr-height="400" mr-width="720">
                <img class="img-responsive no_image">
                <div class="item_buttons">
                    <a class="btn btn-default btn_logins view_store">View Store</a>
                    <a class="btn btn-default btn_logins add_items">Add Item</a>
                </div>
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
                var elem = $('.items_container .item_container');
                elem.width(elem.eq(0).width() - 1);
                elem.height(elem.eq(0).height());
                re_calculate_width = false;
            }
        });
        $(window).resize(function() {
            $('.items_container .item_container').removeAttr('style');
            $('.items_container .item_container .item_image').removeAttr('style');
            re_calculate_width = true;
        });

        $('.menu_toggle').click(function(){
            $('.items_container .item_container').removeAttr('style');
            $('.items_container .item_container .item_image').removeAttr('style');
            re_calculate_width = true;
        });

        $('.item_container a').live('mousedown', function(e) {
//            e.preventDefault();
            var elem_parent = $(this).parents('.item_container');
            Main.saveInLocalStorage('mid', elem_parent.attr('data-mid'));
//            window.location = $(this).attr('href');
        });

        $('#stores_table a[data-mid]').live('mousedown', function(e) {
//            e.preventDefault();
            Main.saveInLocalStorage('mid', $(this).attr('data-mid'));
//            window.location = $(this).attr('href');
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
        Manager.stores({pageNumber: 1, pageSize: pageSize}, 'inactive');

        $('.pagination a').live('click', function(e){
            e.preventDefault();
        });

        $('.pagination li:not(".disabled, .active") a').live('click', function(){
            var parent_elem = $(this).parents('.form_content');
            $('.items_container .item_container').removeAttr('style');
            re_calculate_width = true;
            Manager.stores({pageNumber: $(this).attr('pageno'), pageSize: $('.select_num_items', parent_elem).val()}, parent_elem.hasClass('inactive_stores') ? 'inactive' : false);
        });

        $('.select_num_items').live('change', function(){
            var parent_elem = $(this).parents('.form_content');
            $('.items_container .item_container').removeAttr('style');
            re_calculate_width = true;
            Manager.stores({pageNumber: 1, pageSize: $(this).val()}, parent_elem.hasClass('inactive_stores') ? 'inactive' : false);
        });


        $('#show_table').click(function(){
            if($('.table-view').hasClass('not_loaded')) {
                Manager.listStores();
                $('.not_loaded').removeClass('not_loaded');
            }
            $('.table-view').removeClass('hidden');
            $('.thumbnail-view').addClass('hidden');
        });

        $('#show_block').click(function(){
            $('.table-view').addClass('hidden');
            $('.thumbnail-view').removeClass('hidden');
        });

    });

</script>

</body>
</html>
