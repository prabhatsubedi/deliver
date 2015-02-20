<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Courier Boy Order History</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/orders.js"></script>

</head>
<script>
    $(document).ready(function () {
        Order.courierBoyOrderHistory();
    });
</script>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">
    <%@include file="../includes/header.jsp" %>
    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left"><span class="courier_name"></span> Order History</h1>
        </div>
        <div class="main_content">
            <div class="table-view order_table inroute">
                <table id="courier_history_table">
                    <thead>
                    <tr>
                        <th rowspan="2">SN</th>
                        <th rowspan="2">Delivery boy</th>
                        <th rowspan="2">Date</th>
                        <th rowspan="2">Order No</th>
                        <th rowspan="2">Customer Name</th>
                        <th rowspan="2">Order Name</th>
                        <th rowspan="2">Distance Traveled</th>
                        <th rowspan="2">Delivery status</th>
                        <th rowspan="2">Amount Earned</th>
                        <th rowspan="2">Time Assigned</th>
                        <th rowspan="2">Time Taken</th>
                        <th colspan="2">Customer Feedback</th>
                        <th colspan="2">Courier Boy Feedback</th>
                    </tr>
                        <tr>
                            <th>Rating</th>
                            <th>Comment</th>
                            <th>Rating</th>
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

</body>
</html>
