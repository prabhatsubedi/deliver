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
        var role = data.role = {};

        data.password = "managerpassword";
        data.username = "sagar manager";
        data.fullName = "Sagar Sapkota";
        data.street = "balaju-16, Kathmandu";
        data.city = "city";
        data.state = "Bagmati";
        data.country = "Nepal";
        data.countryCode = "3466";
        data.mobileNumber = "9849540028";
        data.emailAddress = "managersapktoasagarmanager51@yahoo.com";
        data.subscribeNewsletter = true;
        role.role = "ROLE_MANAGER";

        $.ajax({
            url: '/admin/save_manager',
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