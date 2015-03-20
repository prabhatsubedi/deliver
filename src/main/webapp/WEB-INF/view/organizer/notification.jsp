<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Send Notification</title>

    <%@include file="../includes/head.jsp" %>


</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Send Notification</h1>
            <a class="btn btn_green pull-right" href="/courier_staff/create">Button</a>
        </div>
        <div class="main_content form_container">

            <form action="" method="POST" id="form_password" role="form" novalidate="novalidate">
                <div class="form-group">
                    <textarea placeholder="message" name="message" id="message" class="form-control" rows="3"></textarea>
                </div>
            </form>

        </div>
    </div>
</div>

</body>
</html>
