<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Invoices</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css"
          media="screen"/>
    <link href="${pageContext.request.contextPath}/resources/css/jquery-ui.css" rel="stylesheet" type="text/css"
          media="screen"/>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.ui.datepicker.css" rel="stylesheet" type="text/css"
          media="screen"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/orders.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.ui.datepicker.js"></script>

</head>
<script>
    $(document).ready(function () {
        Order.getInvoices();
        Order.getOrdersItems();
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
                if($("#to_date_val").val() != null && $("#to_date_val").val() != '')
                    Order.getInvoices();
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
                if($("#from_date_val").val() != null && $("#from_date_val").val() != '')
                    Order.getInvoices();
            }
        });

        $("#from_date_val").focus(function(){
             $("#from_date").removeClass("hidden");
        });

        $("#to_date_val").focus(function(){
            $("#to_date").removeClass("hidden");
        });

        $("#selectAllToPay").change(function() {
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
                alert("Please select invoice(s) to pay");
            }else{
                var pay = confirm("Are you sure you want to pay the invoice(s)");
                if(pay){
                    var callback = function(success, data){
                        alert(data.message);
                        return;
                    }

                    callback.loaderDiv = "#payLoader";
                    callback.requestType = "POST";
                    var header = {};
                    header.id = idString;

                    Main.request('/accountant/pay_invoice', {}, callback, header);
                }
            }
        });
    });
</script>
<body>

<%@include file="../includes/sidebar_merchant.jsp" %>

<div class="main_container">
    <%@include file="../includes/header.jsp" %>
    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Invoices</h1>
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
        </div>
        <div class="main_content">
            <div class="table-view">
                <table id="invoices_table">
                    <thead>
                    <tr>
                        <th>Id</th>
                        <th>Store Name & Address</th>
                        <th>Generated Date</th>
                        <th>Invoice Amount</th>
                        <th>From Date</th>
                        <th>To Date</th>
                        <th>Paid Date</th>
                        <th>Invoice</th>
                        <th>Select All<span style="margin-left: 10px;"><input type="checkbox" id="selectAllToPay"
                                                                              name="selectAllToPay"/></span></th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
                <button type="submit" id="pay_button" class="btn btn-primary clearfix action_button pull-right">
                    <span class="pull-left">Pay Invoice</span>
                    <span class="pull-right" id="payLoader"></span>
                </button>
            </div>
        </div>
    </div>
</div>

</body>
</html>
