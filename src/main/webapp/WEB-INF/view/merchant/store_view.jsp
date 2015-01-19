<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Store Name</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="/resources/js/jquery-ui.js"></script>
    <script type="text/javascript" src="/resources/js/map.js"></script>
    <script src="https://maps.googleapis.com/maps/api/js?v=3&key=AIzaSyDY0kkJiTPVd2U7aTOAwhc9ySH6oHxOIYM&sensor=false&libraries=places"
            type="text/javascript"></script>

    <link rel="stylesheet" href="/resources/css/maps.css" type="text/css"/>

    <script type="text/javascript" src="/resources/js/store.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            var storeId = Main.getURLvalue(3);
            Store.loadStore(storeId);
        });
    </script>

</head>
<body>

<%@include file="../includes/sidebar_merchant.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Stores</h1>
            <a href="/merchant/store/form/create" class="btn btn_green pull-right">Add Store</a>
        </div>
        <div class="main_content">
            <div class="form_container full_width clearfix">
                <div class="row">
                    <div class="form_section store_info clearfix">
                        <div class="col-lg-6">

                            <div class="form_head">
                                <div class="store_name">Store Name</div>
                            </div>
                            <div class="form_content">
                                <div class="brand_images clearfix">
                                    <div id="brand_image" class="drop_zone disabled unselectable text-center maintain_ratio" mr-width="720" mr-height="400">
                                        <div class="drop_info">no image available</div>
                                    </div>
                                    <div id="brand_logo" class="drop_zone disabled unselectable text-center maintain_ratio" mr-width="200" mr-height="200">
                                        <div class="drop_info">no image available</div>
                                    </div>
                                </div>

                                <div class="detail_options clearfix">
                                    <a class="btn btn_green pull-right btn_edit glyphicon glyphicon-edit" href="/merchant/store/form/edit/"></a>
                                    <a href="/merchant/item/form/create" class="btn btn_green pull-right add_items">Add Item</a>
                                    <div class="switch_container pull-right">
                                        <div class="switch switch_activation">
                                            <div class="btn_switch on"></div>
                                        </div>
                                    </div>
                                </div>

                                <div class="other_info">

                                    <div class="detail_row">
                                        <label class="detail_label">Open Time</label>
                                        <div class="detail_value open_time"></div>
                                    </div>

                                    <div class="detail_row">
                                        <label class="detail_label">Categories</label>
                                        <div class="detail_value categories"><ul class="detail_list"></ul></div>
                                    </div>

                                    <div class="detail_row">
                                        <label class="detail_label">Store Location</label>
                                        <div class="detail_value">
                                            <div class="store_location clearfix">
                                            </div>
                                            <div class="map-container">
                                                <div id="map-canvas"></div>
                                            </div>
                                        </div>
                                    </div>

                                </div>

                            </div>

                        </div>

                        <div class="col-lg-6">

                            <div class="other_contents items_container hidden">
                                <div class="form_head">Other Stores</div>
                                <div class="form_content clearfix"></div>
                            </div>

                        </div>

                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<div class="block_store_container_template hidden">
    <div class="block_store_container col-lg-6">
        <div class="block_store">
            <p>
                <span class="street"></span>
                <span class="city"></span>
            </p>
            <p>
                <span class="contact_person"></span>
                <span class="contact_no"></span>
            </p>
            <button type="button" class="btn-link btn_view_map">View on Map</button>
        </div>
    </div>
</div>

<div class="block_store_template hidden">
    <div class="item_container unselectable col-lg-6 invisible">
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


</body>
</html>
