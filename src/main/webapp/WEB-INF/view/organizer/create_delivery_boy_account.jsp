<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<head>

    <meta name="_csrf" content="${_csrf.token}"/>
    <!-- default header name is X-CSRF-TOKEN -->
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <title>${title}</title>

    <script src="<c:url value="/resources/js/jquery-2.1.1.min.js" />"></script>



</head>
<body>

</body>
</html>
<script>
    $(document).ready(function(){
        /*$('#add_role_form').submit(function (event) {
         event.preventDefault();
         console.log('submitting the form');
         var data = {
         role : $('#role').val()
         };

         */
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var data = {};
        var user = data.user = {};

        user.password = "managerpassword";
        user.username = "sagar manager";
        user.fullName = "Sagar Sapkota";
        user.street = "balaju-16, Kathmandu";
        user.city = "city";
        user.state = "Bagmati";
        user.country = "Nepal";
        user.countryCode = "3466";
        user.mobileNumber = "9849540028";
        user.emailAddress = "dboytest1sapktoasagarmanager51@yahoo.com";
        user.subscribeNewsletter = true;


        $.ajax({
            url: '/organizer/save_delivery_boy',
            type: 'POST',
            beforeSend: function(xhr) {xhr.setRequestHeader(header, token)},
            //data: JSON.stringify($('#form').serializeArray()),
            data: JSON.stringify(data),

            contentType: 'application/json',
            success: function (response) {
                alert(response.message);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert('An error has occured!! :-(')
            }
        });

        /*  return false
         }); */
    });

</script>