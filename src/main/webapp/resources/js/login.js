$(document).ready(function(){

    if(window.location.hostname == "test.idelivr.com") {

        $('#login_form #email').val('admin@yetistep.com');
        $('#login_form #password').val('password');

    }

    $.validator.setDefaults({
        errorPlacement : function(error, element){
            $('#error_container').html(error);
        }
    });

    $('#login_form').validate({
        submitHandler: function() {
            var data = {username: $('#email').val(), password: $('#password').val(), stringify: false};
            Main.doLogin(data);
            return false;
        }/*,
        wrapper : 'div'*/
    });
    $('#email').rules('add', {required: true, email: true, messages : {required: "Email is required."}});
    $('#password').rules('add', {required: true, minlength: 6, messages : {required: "Password is required."}});

    $('#email').focus();

});