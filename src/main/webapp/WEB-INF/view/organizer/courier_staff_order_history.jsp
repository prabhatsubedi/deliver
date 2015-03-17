<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Shopper Order History</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/orders.js"></script>

</head>
<script>
    $(document).ready(function () {
        Order.courierBoyOrderHistory();
        $( "#from_date" ).datepicker({
            changeMonth: true,
            changeYear: true,
            numberOfMonths: 1,
            dateFormat: 'yy-mm-dd',
            minDate: 'Today',
            hideIfNoPrevNext: true,
            onSelect: function( selectedDate ) {
                $( "#to_date" ).datepicker( "option", "minDate", selectedDate );
                $("#from_date_val").val(selectedDate);
                $(this).addClass("hidden");
                Order.courierBoyOrderHistory();
            }
        });

        $( "#to_date" ).datepicker({
            changeMonth: true,
            changeYear: true,
            numberOfMonths: 1,
            dateFormat: 'yy-mm-dd',
            minDate: 'Today',
            hideIfNoPrevNext: true,
            onSelect: function( selectedDate ) {
                $( "#from_date, #selected-days" ).datepicker( "option", "maxDate", selectedDate );
                $("#to_date_val").val(selectedDate);
                $(this).addClass("hidden");
                Order.courierBoyOrderHistory();
            }
        });

        $("#from_date_val").focus(function(){
            $("#from_date").removeClass("hidden");
        });

        $("#to_date_val").focus(function(){
            $("#to_date").removeClass("hidden");
        });
    });
</script>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">
    <%@include file="../includes/header.jsp" %>
    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left"><span class="courier_name"></span> Order History</h1>
            <div class="date_filter pull-right">
                <div class="date_wrapper">
                    <div class="date_label">From:</div>
                    <input type="text" id="from_date_val" name="from_date" class="date_input">
                    <div id="from_date" class="cal hidden"></div>
                </div>
                <div class="date_wrapper">
                    <div class="date_label">To:</div>
                    <input type="text" id="to_date_val" name="to_date" class="date_input">
                    <div id="to_date" class="cal hidden"></div>
                </div>
            </div>
            <div class="ratings pull-right">
                <ul class="nav">
                    <li></li>
                    <li></li>
                    <li></li>
                    <li></li>
                    <li></li>
                </ul>
            </div>
        </div>
        <div class="main_content">
            <div class="table-view order_table inroute">
                <table id="courier_history_table">
                    <thead>
                    <tr>
                        <th rowspan="2">SN</th>
                        <th rowspan="2">Shopper</th>
                        <th rowspan="2">Date</th>
                        <th rowspan="2">Order No</th>
                        <th rowspan="2">Customer Name</th>
                        <th rowspan="2">Order Name</th>
                        <th rowspan="2">Distance Traveled</th>
                        <th rowspan="2">Delivery status</th>
                        <th rowspan="2">Amount Earned</th>
                        <th rowspan="2">Time Assigned</th>
                        <th rowspan="2">Time Taken</th>
                        <th colspan="2">Feedback By Customer</th>
                        <th colspan="2">Feedback By Shopper</th>
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
