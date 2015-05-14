<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Shopper's Transaction</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/dataTables.tableTools.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/dataTables.tableTools.css" rel="stylesheet" type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/manager.js"></script>

    <script type="text/javascript">
        var cbid = Main.getURLvalue(2);
        $(document).ready(function () {
            Manager.getCourierBoyAccount();
            Manager.getStatements(cbid);
            Manager.updateCourierBoyAccount();
            Manager.submitCourierBoyPreviousAmount();
            Manager.submitCourierBoyWalletAmount();

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
                            Main.popDialog('', data.message);
                            Manager.getStatements();
                            return;
                        }

                        callback.loaderDiv = "body";
                        callback.requestType = "GET";
                        var header = {};
                        header.id = idString;

                        Main.request('/accountant/pay_dboy_statement', {}, callback, header);
                    };

                    button1.text = "Yes";
                    var button2 = "No";

                    var buttons = [button1, button2];
                    Main.popDialog('', "Are you sure you want to pay the statement(s)", buttons);

                }
            });
        });
    </script>


</head>
<body>

<sec:authorize ifAnyGranted="ROLE_ACCOUNTANT">
    <%@include file="../includes/sidebar_accountant.jsp" %>
</sec:authorize>
<sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')">
    <%@include file="../includes/sidebar.jsp" %>
</sec:authorize>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left"><span class="cbname">Shopper</span>'s Transaction</h1>
        </div>
        <div class="main_content">

            <div class="table-view">
                <div class="table-responsive" style="margin-bottom: 15px; max-width: 800px;">
                    <input type="hidden" id="due_amount_val">
                    <input type="hidden" id="to_be_submitted_val">
                    <table id="accountTable">
                        <thead>
                        <tr>
                            <th>SN</th>
                            <th><div class="width_300">Title</div></th>
                            <th><div class="width_120">Amount</div></th>
                            <th class="no_sort">Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr class="odd">
                            <th>1</th>
                            <td>Previous Day Due Amount</td>
                            <td class="due_amount"></td>
                            <td><input type="checkbox" id="ack"></td>
                        </tr>
                        <tr class="even">
                            <th>2</th>
                            <td>Current Available Balance</td>
                            <td class="available_balance"></td>
                            <td></td>
                        </tr>
                        <tr class="odd">
                            <th>3</th>
                            <td>Item Available Balance</td>
                            <td class="item_available_balance"></td>
                            <td></td>
                        </tr>
                        <tr class="even">
                            <th>4</th>
                            <td>Cash Available Balance</td>
                            <td class="cash_available_balance"></td>
                            <td></td>
                        </tr>
                        <tr class="odd">
                            <th>5</th>
                            <td>Today's Amount to be submitted to Account</td>
                            <td class="to_be_submitted"></td>
                            <td><input type="checkbox" id="submit"></td>
                        </tr>
                        <tr class="even">
                            <th>6</th>
                            <td>Advance Amount</td>
                            <td class="advance_amount form_container"><input type="text" id="advance_amount_val" class="form-control"></td>
                            <td></td>
                        </tr>
                        </tbody>
                    </table>
                </div>

                <div class="heading clearfix" style="margin: 15px -15px 15px; border-top: 1px solid #dbdada;">
                    <h1 class="pull-left">Transaction History</h1>
                </div>
                <table id="detail_account_table">
                    <thead>
                    <tr>
                        <th>SN</th>
                        <th><div class="width_120">Date and Time</div></th>
                        <th><div class="width_200">Description</div></th>
                        <th>Order ID</th>
                        <th><div class="width_80">DR</div></th>
                        <th><div class="width_80">CR</div></th>
                        <th><div class="width_80">Balance</div></th>
                        <th>Order Status</th>
                        <th>Remarks</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody></tbody>
                </table>

                <div class="heading clearfix" style="margin: 15px -15px 15px; border-top: 1px solid #dbdada;">
                    <h1 class="pull-left">Pay Statements</h1>
                </div>
                <table id="pay_statements">
                    <thead>
                    <tr>
                        <th rowspan="2">SN</th>
                        <th rowspan="2">Date</th>
                        <th colspan="2">Payment Period</th>
                        <th rowspan="2">Total Payable</th>
                        <th rowspan="2" class="no_sort">Statement</th>
                        <th rowspan="2">Paid Date</th>
                        <th rowspan="2" class="no_sort">Select All<span style="margin-left: 10px;"><input type="checkbox" id="selectAllToPay"
                                                                                                          name="selectAllToPay"/></span></th>
                    </tr>
                    <tr>
                        <th>From</th>
                        <th>To</th>
                    </tr>
                    </thead>
                    <tbody></tbody>
                    <tfoot>
                    <tr>
                        <th colspan="4">Total</th>
                        <th class="unpaid_total"></th>
                        <th colspan="3">
                            <button type="submit" id="pay_button" class="btn btn-primary clearfix action_button pull-right">
                                <span class="pull-left">pay</span>
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

<div class="modal fade modal_form" id="modal_note">
    <div class="modal-dialog">
        <form role="form" id="form_note" method="POST" action="">
            <div class="modal-content">
                <div class="modal-header text-center">
                    Add Note
                </div>
                <div class="modal-body body_padding">
                    <div class="form-group">
                        <textarea class="form-control" id="note" name="note" placeholder="Note"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="col-lg-6 no_pad">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    </div>
                    <div class="col-lg-6 no_pad">
                        <button type="submit" class="btn btn-default next">Add</button>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

</body>
</html>
