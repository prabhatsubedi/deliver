<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Shopper Order History</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/resources/css/jquery-ui.css" rel="stylesheet" type="text/css"
          media="screen"/>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.ui.datepicker.css" rel="stylesheet" type="text/css"
          media="screen"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/orders.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.ui.datepicker.js"></script>

</head>
<script>
    $(document).ready(function () {
        Order.courierBoyOrderHistory();
        $( "#from_date_val" ).datepicker({
            changeMonth: true,
            changeYear: true,
            numberOfMonths: 1,
            dateFormat: 'yy-mm-dd',
//            minDate: 'Today',
            hideIfNoPrevNext: true,
            onSelect: function( selectedDate ) {
                $( "#to_date_val" ).datepicker( "option", "minDate", selectedDate );
                if($("#from_date_val").val() != null && $("#to_date_val").val() != '')
                    Order.courierBoyOrderHistory({fromDate: $("#from_date_val").val(), toDate: $("#to_date_val").val()});
            }
        });

        $( "#to_date_val" ).datepicker({
            changeMonth: true,
            changeYear: true,
            numberOfMonths: 1,
            dateFormat: 'yy-mm-dd',
//            minDate: 'Today',
            hideIfNoPrevNext: true,
            onSelect: function( selectedDate ) {
                $( "#from_date_val" ).datepicker( "option", "maxDate", selectedDate );
                if($("#from_date_val").val() != null && $("#to_date_val").val() != '')
                    Order.courierBoyOrderHistory({fromDate: $("#from_date_val").val(), toDate: $("#to_date_val").val()});
            }
        });

        $("#selectAllToPay").change(function(e) {
            var ischecked= $(this).is(':checked');
            if(!ischecked)
                $(".pay_row").prop("checked", false);
            else
                $(".pay_row").prop("checked", true);
        });

        $("#pay_button").click(function(){
            var idString = "";
            $(".pay_row").each(function(){
                if($(this).prop("checked"))
                    idString+=$(this).data("id")+",";
            });
            if(idString == ""){
                alert("Please select order(s) to pay the shopper");
            }else{
                var pay = confirm("Are you sure you want to pay the shopper");
                if(pay){
                    var callback = function(success, data){
                        alert(data.message);
                        return;
                    }

                    callback.loaderDiv = "#payLoader";
                    callback.requestType = "POST";
                    var header = {};
                    header.id = idString;

                    Main.request('/accountant/pay_dboy', {}, callback, header);
                }
            }
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
            <div class="ratings pull-right">
                <ul class="nav">
                    <li></li>
                    <li></li>
                    <li></li>
                    <li></li>
                    <li></li>
                </ul>
            </div>
            <div class="date_filter pull-right form_container form-inline">
                <div class="date_wrapper">
                    <div class="date_label">From:</div>
                    <input type="text" id="from_date_val" name="from_date" class="date_input form-control">
                    <div id="from_date" class="cal hidden"></div>
                </div>
                <div class="date_wrapper">
                    <div class="date_label">To:</div>
                    <input type="text" id="to_date_val" name="to_date" class="date_input form-control">
                    <div id="to_date" class="cal hidden"></div>
                </div>
            </div>
        </div>
        <div class="main_content">
            <div class="table-view order_table inroute">
                <table id="courier_history_table">
                    <thead>
                    <tr>
                        <th rowspan="2">SN</th>
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
                        <th rowspan="2" class="no_sort">Select All
                            <span style="margin-left: 10px;">
                                <input type="checkbox" id="selectAllToPay" name="selectAllToPay"/>
                            </span>
                        </th>
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
                <button type="submit" id="pay_button" class="btn btn-primary clearfix action_button pull-right">
                    <span class="pull-left">Pay Shopper</span>
                    <span class="pull-right" id="payLoader"></span>
                </button>
            </div>
        </div>
    </div>
</div>

</body>
</html>
