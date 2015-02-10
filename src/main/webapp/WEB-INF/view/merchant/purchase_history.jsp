<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Purchase History</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/orders.js"></script>

</head>
<script>
    $(document).ready(function () {
        Order.getPurchaseHistory();
    });
</script>
<body>

<%@include file="../includes/sidebar.jsp" %>

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
                            <th>Total Bill Amount</th>
                            <th>Courier Boy</th>
                            <th>Bill</th>
                            <th>Action</th>
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
