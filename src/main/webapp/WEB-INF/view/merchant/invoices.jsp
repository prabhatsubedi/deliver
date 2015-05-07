<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Statements</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/dataTables.tableTools.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/dataTables.tableTools.css" rel="stylesheet" type="text/css" media="screen"/>
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
                    Order.getInvoices({fromDate: $("#from_date_val").val(), toDate: $("#to_date_val").val()});
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
                    Order.getInvoices({fromDate: $("#from_date_val").val(), toDate: $("#to_date_val").val()});
            }
        });

        function calcUnpaid() {
            var unpaid_total = 0;
            $('.unpaid_amount').each(function(){
                unpaid_total += parseFloat($(this).html());
            });
            $('.unpaid_total').html(unpaid_total.toFixed(2));
        }

        $("#selectAllToPay").change(function() {
            var ischecked= $(this).is(':checked');
            if(!ischecked) {
                $(".pay_row").prop("checked", false);
                $('.paid_status').addClass('unpaid_amount');
            } else {
                $(".pay_row").prop("checked", true);
                $('.paid_status').removeClass('unpaid_amount');
            }
            calcUnpaid();
        });

        $('.pay_row').live('change', function(){
            var this_amount = $('.paid_status', $(this).parents('tr').eq(0));
            if($(this).prop('checked')) {
                this_amount.removeClass('unpaid_amount');
            } else {
                this_amount.addClass('unpaid_amount');
            }
            calcUnpaid();
        });

        $("#pay_button").click(function(){
            var idString = "";
            $(".pay_row").each(function(){
                if($(this).prop("checked"))
                    idString+=$(this).data("id")+",";
            });
            if(idString == ""){
                Main.popDialog('', 'Please select invoice(s) to pay');
            }else{
                var button1 = function() {

                    var callback = function(success, data){
                        Main.popDialog('Invoice Payment', data.message);
                        Order.getInvoices();
                        return;
                    }

                    callback.loaderDiv = "#payLoader";
                    callback.requestType = "POST";
                    var header = {};
                    header.id = idString;

                    Main.request('/accountant/pay_invoice', {}, callback, header);
                };

                button1.text = "Yes";
                var button2 = "No";

                var buttons = [button1, button2];
                Main.popDialog('', "Are you sure you want to pay the invoice(s)", buttons);

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
            <h1 class="pull-left">Statements</h1>
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
            <div class="table-view">
                <table id="invoices_table">
                    <thead>
                    <tr>
                        <th>SN</th>
                        <th><div class="width_150">Store Name & Address</div></th>
                        <th><div class="width_120">Generated Date</div></th>
                        <th>Payable Amount</th>
                        <th><div class="width_80">From Date</div></th>
                        <th><div class="width_80">To Date</div></th>
                        <th><div class="width_120">Paid Date</div></th>
                        <th class="no_sort"><div class="width_80">Statements</div></th>
                        <th class="no_sort">Select All<span style="margin-left: 10px;"><input type="checkbox" id="selectAllToPay"
                                                                              name="selectAllToPay"/></span></th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                    <tfoot>
                    <tr>
                        <th colspan="3">Total</th>
                        <th class="unpaid_total"></th>
                        <th colspan="5">
                            <button type="submit" id="pay_button" class="btn btn-primary clearfix action_button pull-right">
                                <span class="pull-left">Pay Invoice</span>
                                <span class="pull-right" id="payLoader"></span>
                            </button>
                        </th>
                    </tr>
                    </tfoot>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
