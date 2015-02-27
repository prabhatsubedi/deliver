<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Orders</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/orders.js"></script>

</head>
<script>
    $(document).ready(function () {
        Order.getOrders();
        setInterval(Order.getOrders, 60000);
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
        <div class="settings_menu">
            <ul class="nav nav-pills">
                <li class="col-xs-4 current_page" data-ref="inroute"><a href="#">Live</a></li>
                <li class="col-xs-4" data-ref="successful"><a href="#">Successful</a></li>
                <li class="col-xs-4" data-ref="canceled"><a href="#">Cancelled</a></li>
            </ul>
        </div>
        <div class="main_content">
            <div class="table-view order_table inroute">
                <table id="order_inroute_table">
                    <thead>
                        <tr>
                            <th>SN</th>
                            <th>Customer Name</th>
                            <th>Store Name & Address</th>
                            <th>Order No</th>
                            <th>Total Bill Amount</th>
                            <th>Courier Boy</th>
                            <th>Bill</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
              </div>
            <div class="table-view order_table successful hidden">
                <table id="order_successful_table">
                    <thead>
                    <tr>
                        <th rowspan="2">SN</th>
                        <th rowspan="2">Customer Name</th>
                        <th rowspan="2">Store Name & Address</th>
                        <th rowspan="2">Order No</th>
                        <th rowspan="2">Total Bill Amount</th>
                        <th rowspan="2">Courier Boy</th>
                        <th rowspan="2">Bill</th>
                        <th colspan="2">Customer Feedback</th>
                        <th colspan="2">Courier Boy Feedback</th>
                    </tr>
                    <tr>
                        <th>Rating</th>
                        <th>Comment</th>
                        <th>rating</th>
                        <th>Comment</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
            <div class="table-view order_table canceled hidden">
                <table id="order_canceled_table">
                    <thead>
                    <tr>
                        <th rowspan="2">SN</th>
                        <th rowspan="2">Customer Name</th>
                        <th rowspan="2">Store Name & Address</th>
                        <th rowspan="2">Order No</th>
                        <th rowspan="2">Total Bill Amount</th>
                        <th rowspan="2">Courier Boy</th>
                        <th rowspan="2">Bill</th>
                        <th colspan="2">Courier Boy Feedback</th>
                        <th rowspan="2">Cancel Reason</th>
                    </tr>
                    <tr>
                        <th>rating</th>
                        <th>Comment</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>

        </div>
    </div>
</div>

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
