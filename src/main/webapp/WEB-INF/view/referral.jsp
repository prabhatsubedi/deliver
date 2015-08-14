<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Koolkat</title>
    <%@include file="includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/fb.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/date.format.js"></script>

    <script type="text/javascript">

        $(document).ready(function () {

            $.validator.setDefaults({
                errorPlacement: function (error, element) {
                    $('#error_container').html(error);
                }
            });

            $('#refer_koolkat_form').validate({
                submitHandler: function () {
                    var url = "/anon/register_mobile_customer";

                    var callback = function (status, data) {
                        $("#refer_form").hide();
                        if (data.message == "User does not exist or inactive") {
                            $("#new_account_msg").show();
                        }
                        else {
                            $("#old_account_msg").show();
                        }
                    };

                    var userInfo = {
                        "mobileNumber": $("#refer_number").val(),
                        "role": {
                            "role": "ROLE_CUSTOMER"
                        }
                    };

                    var href = window.location.href;
                    var header = {};
                    header.id = href.split("/").reverse()[0];

                    Main.request(url, userInfo, callback, header);
                    return false;
                }
            });
            $('#refer_number').rules('add', {required: true, number: true});

        });
    </script>

    <style>
        body {
            padding: 0;
            font-family: 'Open Sans', sans-serif;
            background: #F58220 url('../../resources/images/fbs-bg.png') repeat left top;
            min-width: 320px;
        }

        .container {
            margin-top: 80px;
        }

        .hero-unit {
            max-width: 520px;
            margin: 50px auto 0;
        }

        .welcome {
            font-size: 50px;
            color: #FFF;
            margin-bottom: 20px;
        }

        p.info {
            font-size: 27px;
        }

        h3.info {
            font-size: 21px;
        }

        .info {
            color: #FFFFFF;
            margin-bottom: 40px;
        }

        .well {
            background-color: rgba(0, 0, 0, 0.4);
            border: 0 none;
            border-radius: 10px;
            padding: 70px 20px;
        }

        .well h3 {
            margin-bottom: 25px;
            margin-top: 0;
            color: #FFFFFF;
        }

        .well h4 {
            color: #FFFFFF;
        }

        #old_account_msg, #unknown_msg, #new_account_msg {
            display: none;
        }

        #fbLoginBtn {
            background: transparent url("../../resources/images/fbs_btn.png") repeat-x scroll center center;
            border: 0 none;
            border-radius: 5px;
            color: white;
            font-size: 18px;
            height: 58px;
            overflow: hidden;
            padding: 0 40px 0 0;
            width: 357px;
            vertical-align: top;
        }

        #fbLoginBtn > img,
        #fbLoginBtn > span {
            float: left;
        }

        #fbLoginBtn > span {
            margin: 17px 0 0 52px;
        }

        #fbLoginBtn > img {
            margin-top: -1px;
        }

        img {
            border: 0;
        }

        img.play_store {
            height: 60px;
            margin-top: 40px;
        }

        .refer_koolkat {
            background-color: #e5e5e5;
            border-radius: 5px;
            height: 58px;
            width: 357px;
            font-size: 0;
        }

        .refer_koolkat > .form-group {
            height: 58px;
            overflow: hidden;
        }

        .refer_koolkat,
        .refer_koolkat_code,
        .refer_koolkat_no,
        .refer_koolkat_btn {
            display: inline-block;
        }

        .refer_koolkat_code,
        .refer_koolkat_no {
            padding: 10px 15px;
            font-size: 14px;
        }

        .refer_koolkat_code {
            color: #919191;
            border-right: 1px solid #ff9235;
            vertical-align: middle;
        }

        .refer_koolkat_no {
            background-color: transparent;
            border: 0 none;
            box-shadow: none;
            border-radius: 0;
            color: #676767;
            padding: 10px 20px;
            vertical-align: middle;
            width: 225px !important;
        }

        .refer_koolkat_no:focus {
            outline: 0 none;
            box-shadow: none;
        }

        .refer_koolkat_btn {
            padding: 0;
            background-color: transparent;
            border-radius: 0;
            border: 0 none;
        }

        .divider {
            color: #fff;
            font-size: 21px;
            display: inline-block;
            padding: 0 50px;
            vertical-align: text-top;
        }

        .divider > span {
            position: relative;
        }

        .divider > span:before,
        .divider > span:after {
            content: "";
            border: 1px solid #fff;
            height: 35px;
            position: absolute;
            left: 50%;
        }

        .divider > span:before {
            top: -40px;
        }

        .divider > span:after {
            bottom: -40px;
        }

        .refer_note {
            display: block;
            color: #fff;
            margin: 50px 0 0;
        }

        @media (max-width: 480px) {

            .custom_alert {
                left: auto;
                right: auto;
                margin-left: 5%;
                margin-right: 5%;
                width: 90%;
            }

            #fbLoginBtn {
                padding: 0 20px 0 0;
            }

            #fbLoginBtn img {
                /*margin-right: 20px;*/
            }

        }

        @media (max-width: 300px) {

            #fbLoginBtn {
                font-size: 12px;
            }

        }

        @media (max-width: 991px) {

            #fbLoginBtn,
            .refer_koolkat,
            .divider {
                display: block;
                margin: 25px;
            }

            .divider > span:before,
            .divider > span:after {
                height: 0;
                width: 60px;
                top: 50%;
            }

            .divider > span:before {
                left: -80px;
            }

            .divider > span:after {
                left: 45px;
            }
        }

        @media (max-width: 550px) {

            .hero-unit {
                width: 90%;
            }

        }

        @media (max-width: 485px) {

            .refer_koolkat,
            #fbLoginBtn {
                max-width: 357px;
                min-width: 270px;
            }

            .refer_koolkat_no {
                max-width: 62%;
            }

        }

    </style>
</head>
<body>

<div class="container">
    <div class=row>
        <div class="span12" style="padding:0 20px;">
            <img src="${pageContext.request.contextPath}/resources/images/fbs-logo.png"
                 class="img-responsive center-block">
        </div>
    </div>
    <div class="hero-unit text-center">
        <p class="welcome">Welcome to Koolkat!</p>

        <p class="info">
            A quick and better way to get anything delivered at your doorstep.
        </p>

        <h3 class="info">Your friend thinks that you are missing out.</h3>
    </div>
    <div class="well">
        <center>
            <div id="refer_form">
                <button onClick="Fb.fbLogin()" id="fbLoginBtn" class="btn btn-default btn-lg"><img
                        src="${pageContext.request.contextPath}/resources/images/fbs_fb.png">
                    <span> Sign In with Facebook </span></button>

                <div class="divider"><span> Or </span></div>

                <form id="refer_koolkat_form" method="POST" class="form-inline refer_koolkat text-right">
                    <div class="form-group clearfix">
                        <span class="refer_koolkat_code"> +977 </span>
                        <input type="text" class="form-control refer_koolkat_no" id="refer_number" name="refer_number"
                               required placeholder="Enter Your Mobile Number">
                        <button type="submit" class="btn refer_koolkat_btn">
                            <img src="${pageContext.request.contextPath}/resources/images/btn_arrow.png"/>
                        </button>
                    </div>
                </form>
            </div>

            <div id="new_account_msg">
                <h4>Thank you for signing up to Koolkat.</h4>
                <h4> Do download Koolkat from Google Play Store</h4>
                <a href="https://play.google.com/store/apps/details?id=com.yetistep.delivr&hl=en"><img
                        src="${pageContext.request.contextPath}/resources/images/play_store_black.png"
                        class="play_store"/></a>
                <a href="https://itunes.apple.com/np/app/id999306211"><img
                        src="${pageContext.request.contextPath}/resources/images/app_store_black.png"
                        class="play_store"/></a>
            </div>
            <div id="old_account_msg">
                <h4>Your account has already been created. </h4>
                <h4>If you have not downloaded Koolkat yet, feel free to download Koolkat from Google Play Store.</h4>
                <a href="https://play.google.com/store/apps/details?id=com.yetistep.delivr&hl=en"><img
                        src="${pageContext.request.contextPath}/resources/images/play_store_black.png"
                        class="play_store"/></a>
                <a href="https://itunes.apple.com/np/app/id999306211"><img
                        src="${pageContext.request.contextPath}/resources/images/app_store_black.png"
                        class="play_store"/></a>
            </div>
            <h4 id="unknown_msg">
                Sorry! Facebook says, you are not authorized to use this app.
            </h4>
        </center>
    </div>

    <center>
        <em class="refer_note"> Note: If your register with Facebook or Phone number then you must login with same
            method in mobile app to give back bonus to your friend who reffered you. </em>
    </center>

</div>

</body>
</html>