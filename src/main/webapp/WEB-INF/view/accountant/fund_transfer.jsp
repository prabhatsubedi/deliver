<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Fund Transfer</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/dataTables.tableTools.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/dataTables.tableTools.css" rel="stylesheet" type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/manager.js"></script>

</head>
<script>
    $(document).ready(function () {
        Manager.loadTransfers();
    });
</script>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">
    <%@include file="../includes/header.jsp" %>
    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Fund Transfer</h1>
        </div>
        <div class="main_content">
            <div class="table-view">
                <table id="transfer_table">
                    <thead>
                    <tr>
                        <th rowspan="2">Order ID</th>
                        <th rowspan="2">Order Date</th>
                        <th rowspan="2"><div class="width_150">Shopper Name</div></th>
                        <th colspan="3">Amount</th>
                        <th rowspan="2"><div class="width_120">Account Number</div></th>
                        <th rowspan="2" class="no_sort"><div class="width_80">Action</div></th>
                    </tr>
                    <tr>
                        <th><div class="width_80">Requested</div></th>
                        <th><div class="width_80">Transferred</div></th>
                        <th><div class="width_80">Paid to Merchant</div></th>
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
