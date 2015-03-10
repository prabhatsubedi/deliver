<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Invoices</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/orders.js"></script>

</head>
<script>
    $(document).ready(function () {
        Order.getInvoices();
        Order.getOrdersItems();
    });
</script>
<body>

<%@include file="../includes/sidebar_merchant.jsp" %>

<div class="main_container">
    <%@include file="../includes/header.jsp" %>
    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Invoices</h1>
        </div>
        <div class="main_content">
            <div class="table-view">
                <table id="invoices_table">
                    <thead>
                        <tr>
                            <th>Id</th>
                            <th>Invoice</th>
                            <th>Store Name & Address</th>
                            <th>Generated Date</th>
                            <th>From Date</th>
                            <th>To Date</th>
                            <th>Paid Date</th>
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
