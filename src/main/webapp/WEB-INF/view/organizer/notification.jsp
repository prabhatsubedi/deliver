<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Send Notification</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/manager.js"></script>

    <script type="text/javascript">

        $(document).ready(function(){

            Manager.loadNotification();

        });

    </script>


</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Send Notification</h1>
        </div>
        <div class="main_content form_container">

            <form action="" method="POST" id="form_notification" role="form">
                <div class="form-group">
                    <label class="check_label">
                        <span class="glyphicon glyphicon-ok check_span"></span>
                        IOS Customers
                        <input type="checkbox" class="checkbox check_customer" value="CUSTOMER_IOS">
                    </label>
                </div>
                <div class="form-group">
                    <label class="check_label">
                        <span class="glyphicon glyphicon-ok check_span"></span>
                        Android Customers
                        <input type="checkbox" class="checkbox check_customer" value="CUSTOMER_ANDROID">
                    </label>
                </div>
                <div class="form-group">
                    <label class="check_label">
                        <span class="glyphicon glyphicon-ok check_span"></span>
                        Shoppers
                        <input type="checkbox" class="checkbox check_shopper" value="DELIVERY_BOY">
                    </label>
                </div>
                <div class="form-group">
                    <textarea placeholder="message" name="message" id="message" class="form-control" rows="3"></textarea>
                </div>
                <div class="clearfix">
                    <button type="submit" class="btn btn_green pull-left">Send Notification</button>
                </div>
            </form>

        </div>
    </div>
</div>

</body>
</html>
