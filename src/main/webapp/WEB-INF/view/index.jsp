<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<head>
    <title>Hello World!</title>
    <script src="<c:url value="/resources/js/jquery-2.1.1.min.js" />"></script>

    <meta name="_csrf" content="${_csrf.token}"/>
    <!-- default header name is X-CSRF-TOKEN -->
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

</head>
<body>
<h2>Hello World!</h2>
<form method="#" action="#" name="form" id="form">
     Name <input type="text" name="name" id="name"> <br/>
     Password <input type="text" name = "password" id="password"><br/>
     <input type="submit" value="Submit"/>
</form>

<hr width="100"/>
<input type="button" name="userList" id = "userList" value="User List" />

</body>
</html>
<script>
    $(document).ready(function(){

    });

    $('form').submit(function () {
        alert('submitting the form');

        var data = {
            name : $('#name').val(),
            password : $('#password').val()
        };

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/admin/create',
            type: 'POST',
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token),
                xhr.setRequestHeader("xname", "Yeti Tech Follow")
            },
            //data: JSON.stringify($('#form').serializeArray()),
            data: JSON.stringify(data),

            contentType: 'application/json',
            success: function (data) {
                alert(data.message);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert('An error has occured!! :-(')
            }
        })

        return false
    })

    $('#userList').click(function (){
        $.ajax({
            url: '/admin/getUsers',
            type: 'GET',
            //data: JSON.stringify($('#form').serializeArray()),
            data: '{}',
            contentType: 'application/json',
            success: function (data) {
                alert(data.message);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert('An error has occured!! :-(')
            }
        })
        return false;

    })
</script>