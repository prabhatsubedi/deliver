<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>KoolKat</title>
    <%@include file="includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/fb.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/date.format.js"></script>

    <style>
        body{
            padding: 0;
            font-family: 'Open Sans', sans-serif;
            min-width: 240px;
        }
        .container{
            margin-top: 30px;
        }

        .welcome{
            font-size: 21px;
        }

        blockquote {
            border-left: 5px solid #ccc;
        }

        #old_account_msg, #unknown_msg, #new_account_msg{
            display: none;
        }

        #old_account_msg, #new_account_msg{
            padding: 30px 0 80px 0;
        }

        #fbLoginBtn{
            padding: 15px 8px 15px 35px;
            background: #4a6ea8 url('../../resources/images/facebook-icon.png') no-repeat 6px -8px;
            color: white;
            font-size: 18px;
            line-height: 25px;
        }

        #fbLoginBtn:hover{
            background-color: #3b5996;
        }
        img{
            border: 0;
        }
        img.play_store{
            width: 200px;
            margin-top: 40px;
        }

        @media (max-width: 480px) {

            .custom_alert {
                left: auto;
                right: auto;
                margin-left: 5%;
                margin-right: 5%;
                width: 90%;
            }

        }
        @media (max-width: 300px) {

            #fbLoginBtn{
                font-size: 12px;
            }

        }
    </style>
</head>
<body>

<div class="container">
    <div class=row>
        <div class="span12">
            <center><img src="${companyLogo}" width="40%"/></center>
        </div>
    </div>
    <div class="hero-unit" style="margin-top: 50px;">
        <p class="welcome">Welcome to Koolkat!</p>
        <blockquote>
            <p class="info">
                A quick and better way to get anything delivered at your doorstep.
            </p>
        </blockquote>
    </div>
    <div class="well">
        <center>
            <h3>Your friend thinks that you are missing out.</h3>
            <button onClick="Fb.fbLogin()" id="fbLoginBtn" class="btn btn-default btn-lg">Sign Up with Facebook</button>

            <div id="new_account_msg">
                <h4>Thank you for signing up to Koolkat.</h4>
                <h4> Do download Koolkat from Google Play Store</h4>
                <a href="https://play.google.com/store/apps/details?id=com.yetistep.delivr&hl=en"><img src="${pageContext.request.contextPath}/resources/images/play_store_black.png" class="play_store"/></a>
            </div>
            <div id="old_account_msg">
                <h4>Your account has already been created. </h4 >
                <h4>If you have not downloaded Koolkat yet, feel free to download Koolkat from Google Play Store.</h4>
                <a href="https://play.google.com/store/apps/details?id=com.yetistep.delivr&hl=en"><img src="${pageContext.request.contextPath}/resources/images/play_store_black.png" class="play_store"/></a>
            </div>
            <h4 id="unknown_msg">
                Sorry! Facebook says, you are not authorized to use this app.
            </h4>
        </center>
    </div>




</div>

</body>
</html>