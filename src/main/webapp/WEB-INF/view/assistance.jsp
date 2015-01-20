<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Assistance</title>

    <%@include file="includes/head.jsp" %>


    <script type="text/javascript">

        var action = "";
        $(document).ready(function(){

            $('.btn_cancel').click(function(){
                window.location = Main.modifyURL("/");
            });

            $.validator.setDefaults({
                errorPlacement : function(error, element){
                    $('#error_container').html(error);
                }
            });

            function remove_value(value, remove) {
                if(value.indexOf(remove) > -1) {
                    value.splice(value.indexOf(remove), 1);
                    remove_value(value, remove);
                }
                return value;
            }
            if(Main.getURLvalue(1) == "forgot_password") {
                $('#container_email').removeClass('hidden');
                action = "FORGOT";
            } else if(Main.getURLvalue(1) == "resend_confirmation") {
                $('#container_email').removeClass('hidden');
                $('#container_email .modal-header').html('Resend Confirmation Mail');
                action = "RESEND";
            } else if(Main.getURLvalue(1) == "create_password") {
                $('#container_password').removeClass('hidden');
                action = "NEW";
            } else if(Main.getURLvalue(1) == "reset_password") {
                $('#container_password').removeClass('hidden');
                $('#container_password .modal-header').html('Reset Password');
                action = "RESET";
            } else {
                $('#container_assist').removeClass('hidden');
            }

            if(action == "NEW" || action == "RESET") {
                if(Main.getURLvalue(2) == undefined) {
                    window.location = Main.modifyURL("/");
                    return false;
                }
                $('#form_password').validate({
                    rules : {
                        password: {
                            required: true,
                            minlength: 6
                        },
                        re_password: {
                            required: true,
                            minlength: 6,
                            equalTo: "#password"
                        }
                    },
                    submitHandler: function() {
                        var data = {actionType: action};
                        var headers = {verificationCode: Main.getURLvalue(2), password: $('#password').val()};
                        Main.assistance(data, headers);
                        return false;
                    }
                });
            }

            if(action == "FORGOT" || action == "RESEND") {
                $('#form_email').validate({
                    rules : {
                        email: {
                            required: true,
                            email: true
                        }
                    },
                    submitHandler: function() {
                        var data = {actionType: action};
                        var headers = {username: $('#email').val()};
                        Main.assistance(data, headers);
                        return false;
                    }
                });
            }

        });

    </script>

</head>
<body class="assistance_page">

<div id="container_assist" class="assist_containers hidden">
    <div class="modal-content">
        <div class="modal-header text-center">
            Need help?
        </div>
        <div class="modal-footer">
            <a class="btn btn-default assist_buttons" href="/assistance/forgot_password/">Forgot Password</a>
            <a class="btn btn-default assist_buttons"  href="/assistance/resend_confirmation/">Resend Confirmation Email</a>
        </div>
    </div>
</div>

<div id="container_email" class="assist_containers hidden">
    <form role="form" id="form_email" method="POST" action="">
        <div class="modal-content">
            <div class="modal-header text-center">
                Forgot password
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <input type="text" class="form-control" id="email" name="email" placeholder="Email">
                </div>
            </div>
            <div class="modal-footer">
                <div class="col-lg-6 no_pad">
                    <button type="button" class="btn btn-default btn_cancel">Cancel</button>
                </div>
                <div class="col-lg-6 no_pad">
                    <button type="submit" class="btn btn-default">Continue</button>
                </div>
            </div>
        </div>
    </form>
</div>

<div id="container_password" class="assist_containers hidden">
    <form role="form" id="form_password" method="POST" action="">
        <div class="modal-content">
            <div class="modal-header text-center">
                Create Password
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <input type="password" class="form-control" id="password" name="password" placeholder="Password">
                </div>
                <div class="form-group">
                    <input type="password" class="form-control" id="re_password" name="re_password" placeholder="Retype Password">
                </div>
            </div>
            <div class="modal-footer">
                <div class="col-lg-6 no_pad">
                    <button type="button" class="btn btn-default btn_cancel">Cancel</button>
                </div>
                <div class="col-lg-6 no_pad">
                    <button type="submit" class="btn btn-default">Continue</button>
                </div>
            </div>
        </div>
    </form>
</div>

</body>
</html>