<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Orders</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/map.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/maps.css" type="text/css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/dataTables.tableTools.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/dataTables.tableTools.css" rel="stylesheet" type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/orders.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/maps.css" rel="stylesheet" type="text/css" media="screen"/>
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp"></script>

</head>
<script>
    $(document).ready(function () {
        Order.loadOrderFn();
        Order.getOrdersItems();
        Order.getCourierBoyMap();
    });
</script>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Orders</h1>
        </div>
        <div class="main_tabs">
            <ul class="nav nav-pills">
                <li class="col-xs-4"><a href="#tab_inroute" data-status="PENDING" data-id="order_inroute_table" data-toggle="tab">Live</a></li>
                <li class="col-xs-4"><a href="#tab_success" data-status="SUCCESSFUL" data-id="order_successful_table" data-toggle="tab">Successful</a></li>
                <li class="col-xs-4"><a href="#tab_cancelled" data-status="CANCELLED" data-id="order_canceled_table" data-toggle="tab">Cancelled</a></li>
            </ul>
        </div>
        <div class="main_content tab-content">
            <div class="table-view order_table inroute tab-pane" id="tab_inroute">
                <table id="order_inroute_table">
                    <thead>
                        <tr>
                            <th>SN</th>
                            <th><div class="width_150">Order Date & Time</div></th>
                            <th><div class="width_50">Order No.</div></th>
                            <th><div class="width_120">Customer Name</div></th>
                            <th><div class="width_200">Store Name & Address</div></th>
                            <th><div class="width_120">Drop of Location</div></th>
                            <th><div class="width_100">Verification Code</div></th>
                            <th class="no_sort"><div class="width_80">Discount from Store</div></th>
                            <th><div class="width_80">Paid to Merchant</div></th>
                            <th><div class="width_150">Merchant Bill</div></th>
                            <th><div class="width_80">Received From Customer</div></th>
                            <th><div class="width_120">Shopper</div></th>
                            <th><div class="width_80">Shopper Earning</div></th>
                            <th><div class="width_100">Time Assigned</div></th>
                            <th><div class="width_100">Time Taken</div></th>
                            <th><div class="width_100">Status</div></th>
                            <th class="no_sort"><div class="width_350">Action</div></th>
                        </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
              </div>
            <div class="table-view order_table successful tab-pane" id="tab_success">
                <table id="order_successful_table">
                    <thead>
                    <tr>

                        <th rowspan="2">SN</th>
                        <th rowspan="2"><div class="width_150">Order Date & Time</div></th>
                        <th rowspan="2"><div class="width_50">Order No.</div></th>
                        <th rowspan="2"><div class="width_120">Customer Name</div></th>
                        <th rowspan="2"><div class="width_200">Store Name & Address</div></th>
                        <th rowspan="2"><div class="width_120">Drop of Location</div></th>
                        <th rowspan="2" class="no_sort"><div class="width_80">Discount from Store</div></th>
                        <th rowspan="2"><div class="width_80">Paid to Merchant</div></th>
                        <th rowspan="2"><div class="width_150">Merchant Bill</div></th>
                        <th rowspan="2"><div class="width_80">Received From Customer</div></th>
                        <th rowspan="2"><div class="width_120">Shopper</div></th>
                        <th rowspan="2"><div class="width_80">Shopper Earning</div></th>
                        <th rowspan="2"><div class="width_100">Time Assigned</div></th>
                        <th rowspan="2"><div class="width_100">Time Taken</div></th>
                        <th rowspan="2"><div class="width_100">Customer Bill & Receipt</div></th>
                        <th colspan="2">Feedback To Shopper</th>
                        <th colspan="2">Feedback To Customer</th>
                        <th rowspan="2" class="no_sort"><div class="width_250">Action</div></th>
                    </tr>
                    <tr>
                        <th class="no_sort">Rating</th>
                        <th class="no_sort"><div class="width_150">Issue | Comment</div></th>
                        <th class="no_sort">Rating</th>
                        <th class="no_sort"><div class="width_150">Issue | Comment</div></th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
            <div class="table-view order_table canceled tab-pane" id="tab_cancelled">
                <table id="order_canceled_table">
                    <thead>
                    <tr>
                        <th rowspan="2">SN</th>
                        <th rowspan="2"><div class="width_150">Order Date & Time</div></th>
                        <th rowspan="2"><div class="width_50">Order No.</div></th>
                        <th rowspan="2"><div class="width_120">Customer Name</div></th>
                        <th rowspan="2"><div class="width_200">Store Name & Address</div></th>
                        <th rowspan="2"><div class="width_120">Drop of Location</div></th>
                        <th rowspan="2" class="no_sort"><div class="width_80">Discount from Store</div></th>
                        <th rowspan="2"><div class="width_80">Paid to Merchant</div></th>
                        <th rowspan="2"><div class="width_150">Merchant Bill</div></th>
                        <th rowspan="2"><div class="width_80">Received From Customer</div></th>
                        <th rowspan="2"><div class="width_120">Shopper</div></th>
                        <th rowspan="2"><div class="width_80">Shopper Earning</div></th>
                        <th rowspan="2"><div class="width_100">Time Assigned</div></th>
                        <th rowspan="2"><div class="width_100">Time Taken</div></th>
                        <th colspan="2">Feedback To Customer</th>
                        <th rowspan="2" class="no_sort">Cancel Reason</th>
                        <th rowspan="2" class="no_sort"><div class="width_250">Action</div></th>
                    </tr>
                    <tr>
                        <th class="no_sort">Rating</th>
                        <th class="no_sort">Issue | Comment</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>

        </div>
    </div>
</div>

<div class="shopper_preview_container hidden"></div>

<div class="modal fade" id="order_items_modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <a class="close" data-dismiss="modal">x</a>
                <h3>Orders Items</h3>
            </div>
            <div class="table-view modal-body">
                <table id="orders_items_table">
                    <thead>
                    <tr>
                        <th class="no_sort">SN</th>
                        <th class="no_sort">Item Name</th>
                        <th class="no_sort">Qty</th>
                        <th class="no_sort">Service Charge</th>
                        <th class="no_sort">VAT</th>
                        <th class="no_sort">Item Price</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                    <tfoot>
                    <tr>
                        <td colspan="5" class="text-right"><strong>Sub Total</strong></td>
                        <td class="sub_total"></td>
                    </tr>
                    <tr>
                        <td colspan="5" class="text-right"><strong>Discount from Store</strong></td>
                        <td class="discount"></td>
                    </tr>
                    <tr>
                        <td colspan="5" class="text-right"><strong>Total Service Charge</strong></td>
                        <td class="total_sc"></td>
                    </tr>
                    <tr>
                        <td colspan="5" class="text-right"><strong>Total VAT</strong></td>
                        <td class="total_vat"></td>
                    </tr>
                    <tr>
                        <td colspan="5" class="text-right"><strong>Grand Total</strong></td>
                        <td class="grand_total"></td>
                    </tr>
                    </tfoot>
                </table>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="modal_map" tabindex="-1" role="dialog" aria-labelledby="mapLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="mapLabel">Shopper Location</h4>
            </div>
            <div class="modal-body">

                <div class="map-container">
                    <div id="custom_map_controls" class="clearfix">
                        <div id="scroll_zoom" class="pull-left">Enable Scroll Zoom</div>
                    </div>
                    <div id="map-canvas"></div>
                </div>

            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="order_details" tabindex="-1" role="dialog" aria-labelledby="mapLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
        </div>
    </div>
</div>


<div class="modal_content_template hidden">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title"></h4>
    </div>
    <div class="modal-body">

    </div>
</div>

<div class="order_details_row hidden">
    <div class="dRow clearfix">
        <div class="col-xs-6 no_pad order_label"></div>
        <div class="col-xs-6 no_pad order_value"></div>
    </div>
</div>

<div class="modal fade" id="order_bills" tabindex="-1" role="dialog" aria-labelledby="mapLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Bills</h4>
            </div>
            <div class="modal-body">
            </div>
        </div>
    </div>
</div>

<div class="infowindow_template hidden">
    <div class="infowindow">
        <p><strong class="name_line"></strong></p>
        <p class="address_line"></p>
    </div>
</div>

<div class="assigned_shoppers_view invisible"></div>

</body>
</html>
