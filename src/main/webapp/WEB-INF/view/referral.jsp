<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Delivr</title>

    <%@include file="includes/head.jsp" %>


</head>
<body>

<div class="logo_container">
    <img src="/resources/images/login-logo.png" class="img-responsive">
</div>

<div class="delivr_info container">
      <div class="fb_signup">Sign Up with Facebook</div>
      <div id="fb-login-error"></div>
</div>



<div class="login_foot">
    <div class="foot_links">
        <ul class="nav nav-pills text-center">
            <li><a href="#">Delivr</a></li>
            <li><a href="#">Terms of use</a></li>
            <li><a href="#">Privacy Policy</a></li>
        </ul>
    </div>
</div>

</body>
</html>