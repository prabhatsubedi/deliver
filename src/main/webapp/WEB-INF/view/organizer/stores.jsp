<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Stores</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="/resources/js/manager.js"></script>
    <script type="text/javascript" src="/resources/js/jquery-ui.js"></script>


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
                <div class="form_content clearfix" id="other_stores"></div>
            </div>
        </div>
    </div>
</div>


<div class="block_store_template hidden">
    <div class="item_container unselectable col-lg-2">
        <div class="block_item">
            <div class="item_image">
                <img src="/resources/images/720x400.jpg" class="img-responsive">
            </div>
            <div class="item_infos">
                <p class="item_name">Item Name</p>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">

    var draggable_initialized = false;
    $(document).ready(function(){

        var sortableParam = {
            revert: true,
            tolerance: 'pointer',
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
                Main.request('/organizer/update_special_brands', arrdata)
            }
        };

        $('#other_stores').sortable(sortableParam);
        $('#featured_stores').sortable(sortableParam);
        $('#prioritized_stores').sortable(sortableParam);

        Manager.stores({}, true);
        Manager.stores({pageNumber: 1, pageSize: 12});

    });

</script>

</body>
</html>
