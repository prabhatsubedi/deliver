<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Delivr</title>
    <%@include file="includes/head.jsp" %>
    <script type="text/javascript" src="/resources/js/fb.js"></script>
    <script type="text/javascript" src="/resources/js/date.format.js"></script>

    <style>
        body{
            padding: 0;
            font-family: 'Open Sans', sans-serif;
            min-width: 240px;
        }
        .container{
            margin-top: 30px;
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