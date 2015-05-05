<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Purchase History</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/dataTables.tableTools.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/dataTables.tableTools.css" rel="stylesheet" type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/orders.js"></script>

</head>
<script>
    $(document).ready(function () {
        Order.getPurchaseHistory();
        Order.getOrdersItems();
    });
</script>
<body>

<%@include file="../includes/sidebar_merchant.jsp" %>
<div class="main_container">
    <%@include file="../includes/header.jsp" %>
    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Purchase History</h1>
        </div>
        <div class="main_content">
            <div class="table-view order_table inroute">
                <table id="purchase_history_table">
                    <thead>
                        <tr>
                            <th>SN</th>
                            <th>Customer Name</th>
                            <th>Store Name & Address</th>
                            <th>Order No</th>
                            <th>Order Date & Time</th>
                            <th>Paid to Merchant</th>
                            <th>Shopper</th>
                            <th class="no_sort">Bill</th>
                            <th class="no_sort">Items</th>
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
                        <th class="no_sort">Total Item Price</th>
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

</body>
</html>
