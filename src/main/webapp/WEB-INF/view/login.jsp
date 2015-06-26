<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
<title>Koolkat Login</title>

    <%@include file="includes/head.jsp" %>
    
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.validate.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/login.js"></script>

</head>
<body>

<div class="login_container" id="modal_login">
    <div class="logo_block">
        <img src="${pageContext.request.contextPath}/resources/images/logo.png" class="img-responsive center-block"></div>
    <div class="login_block">
        <div class="login_head">Login to Koolkat</div>
        <div class="login_form">
            <form role="form" id="login_form" method="POST" action="${pageContext.request.contextPath}/j_spring_security_check">
                <div class="form-group">
                    <input type="email" class="form-control" id="email" name="email" placeholder="Email">
                </div>
                <div class="form-group">
                    <input type="password" class="form-control" id="password" name="password" placeholder="Password">
                </div>
                <div class="clearfix">
                    <button type="submit" class="btn btn-default form-control">SIGN IN</button>
                </div>
            </form>
        </div>
    </div>
    <div class="foot_block text-center"><a href="/assistance/">Need Help?</a></div>

</div>

</body>
</html>