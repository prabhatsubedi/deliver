<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Refill Wallet</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/admin.js"></script>

    <script type="text/javascript">

        $(document).ready(function(){

            Admin.refillWallet();

        });

    </script>

</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Refill Wallet</h1>
        </div>
        <div class="main_content">
            <div class="form_container">
                <form role="form" id="form" method="POST" action="" class="form-inline">
                    <div class="form-group">
                        <input type="text" class="form-control" id="userID" name="userID" placeholder="Facebook user ID">
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" id="amount" name="amount" placeholder="Amount">
                    </div>
                    <div class="form-group">
                        <button class="btn btn_green" type="submit">Refill</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>
