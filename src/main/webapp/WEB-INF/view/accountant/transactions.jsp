<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Shopper</title>

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
            Manager.updateCourierBoyAccount();
            Manager.submitCourierBoyPreviousAmount();
            Manager.submitCourierBoyWalletAmount();
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
                <div class="table-responsive" style="margin-bottom: 15px;">
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
                            <td>Today's Amount to be submitted to Account</td>
                            <td class="to_be_submitted"></td>
                            <td><input type="checkbox" id="submit"></td>
                        </tr>
                        <tr class="even">
                            <th>4</th>
                            <td>Advance Amount</td>
                            <td class="advance_amount form_container"><input type="text" id="advance_amount_val" class="form-control"></td>
                            <td></td>
                        </tr>
                        </tbody>
                    </table>
                </div>

                <div class="heading clearfix" style="margin: 0px -15px 15px; border-top: 1px solid #dbdada;">
                    <h1 class="pull-left">Transaction History</h1>
                </div>
                <table id="detail_account_table">
                    <thead>
                    <tr>
                        <th>SN</th>
                        <th>Issued Date</th>
                        <th>Type</th>
                        <th>Amount</th>
                    </tr>
                    </thead>
                    <tbody></tbody>
                </table>

            </div>

        </div>
    </div>
</div>

</body>
</html>
