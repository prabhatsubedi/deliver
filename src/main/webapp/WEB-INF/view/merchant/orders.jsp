<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Orders</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/dataTables.tableTools.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/dataTables.tableTools.css" rel="stylesheet" type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/orders.js"></script>

</head>
<script>
    $(document).ready(function () {
        Order.loadOrderFn();
        //setInterval(Order.getOrders, 60000);
        Order.getOrdersItems();
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
                            <th><div class="width_150">Customer Name</div></th>
                            <th><div class="width_200">Store Name & Address</div></th>
                            <th><div class="width_80">Order No</div></th>
                            <th><div class="width_120">Order Date</div></th>
                            <th><div class="width_80">Verification Code</div></th>
                            <th><div class="width_100">Total Bill Amount</div></th>
                            <th><div class="width_150">Shopper</div></th>
                            <th class="no_sort"><div class="width_80">Bill</div></th>
                            <th><div class="width_100">Status</div></th>
                            <th class="no_sort"><div class="width_100">Action</div></th>
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
                        <th rowspan="2"><div class="width_150">Customer Name</div></th>
                        <th rowspan="2"><div class="width_200">Store Name & Address</div></th>
                        <th rowspan="2"><div class="width_80">Order No</div></th>
                        <th rowspan="2"><div class="width_120">Order Date</div></th>
                        <th rowspan="2"><div class="width_80">Verification Code</div></th>
                        <th rowspan="2"><div class="width_100">Total Bill Amount</div></th>
                        <th rowspan="2"><div class="width_150">Shopper</div></th>
                        <th rowspan="2" class="no_sort"><div class="width_80">Bill</div></th>
                        <th colspan="2">Customer Feedback</th>
                        <th colspan="2">Shopper Feedback</th>
                    </tr>
                    <tr>
                        <th class="no_sort">Rating</th>
                        <th class="no_sort"><div class="width_150">Comment</div></th>
                        <th class="no_sort">rating</th>
                        <th class="no_sort"><div class="width_150">Comment</div></th>
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
                        <th rowspan="2"><div class="width_150">Customer Name</div></th>
                        <th rowspan="2"><div class="width_200">Store Name & Address</div></th>
                        <th rowspan="2"><div class="width_80">Order No</div></th>
                        <th rowspan="2"><div class="width_120">Order Date</div></th>
                        <th rowspan="2"><div class="width_80">Verification Code</div></th>
                        <th rowspan="2"><div class="width_100">Total Bill Amount</div></th>
                        <th rowspan="2"><div class="width_150">Shopper</div></th>
                        <th rowspan="2" class="no_sort"><div class="width_80">Bill</div></th>
                        <th colspan="2">Shopper Feedback</th>
                        <th rowspan="2" class="no_sort">Cancel Reason</th>
                    </tr>
                    <tr>
                        <th class="no_sort">rating</th>
                        <th class="no_sort">Comment</th>
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
                        <th>SN</th>
                        <th>Item Name</th>
                        <th>Quantity</th>
                        <th>Service Charge</th>
                        <th>Vat</th>
                        <th>Grand Total</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
