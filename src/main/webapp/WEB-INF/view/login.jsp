<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<html>
<head>
<title>Login Page</title>
    <script src="<c:url value="/resources/js/jquery-2.1.1.min.js" />"></script>
<style>
.error {
	padding: 15px;
	margin-bottom: 20px;
	border: 1px solid transparent;
	border-radius: 4px;
	color: #a94442;
	background-color: #f2dede;
	border-color: #ebccd1;
}

.msg {
	padding: 15px;
	margin-bottom: 20px;
	border: 1px solid transparent;
	border-radius: 4px;
	color: #31708f;
	background-color: #d9edf7;
	border-color: #bce8f1;
}

#login-box {
	width: 300px;
	padding: 20px;
	margin: 100px auto;
	background: #fff;
	-webkit-border-radius: 2px;
	-moz-border-radius: 2px;
	border: 1px solid #000;
}
</style>
</head>
<body onload='document.loginForm.username.focus();'>

	<h1>Spring Security Login Form (Database + Hibernate Authentication)</h1>

	<div id="login-box">

		<h3>Login with Username and Password</h3>

		<c:if test="${not empty error}">
			<div class="error">${error}</div>
		</c:if>
		<c:if test="${not empty msg}">
			<div class="msg">${msg}</div>
		</c:if>

		<form name='loginForm'
			action="<c:url value='/j_spring_security_check' />" id="login_form" method='POST'>

			<table>
				<tr>
					<td>User:</td>
					<td><input id="username" type='text' name='username'></td>
				</tr>
				<tr>
					<td>Password:</td>
					<td><input id="password" type='password' name='password' /></td>
				</tr>
				<tr>
					<td colspan='2'><input name="submit" type="submit"
						value="submit" /></td>
				</tr>
			</table>

			<input id="csrf_token" type="hidden" name="${_csrf.parameterName}"
                                                value="${_csrf.token}" />

		</form>
	</div>
    <script>
        $(document).ready(function(){
            $('#login_form').submit(function(event){
                event.preventDefault();
                var user_pass = $("#password").val();
                var user_name = $("#username").val();
                var tokenName = $('#csrf_token').attr('name');
                var tokenValue = $('#csrf_token').attr('value');
                var jsonData = {};
                jsonData.username = user_name;
                jsonData.password = user_pass;
                jsonData[""+tokenName+""] = tokenValue;
                console.log(jsonData);
//Ajax login - we send credentials to j_spring_security_check (as in form based login
                $.ajax({
                    url: "/j_spring_security_check",
                    data: jsonData,
                    type: "POST",
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader("X-Ajax-call", "true");
                    },
                    success: function(result) {
                        console.log(result);
                        window.location.replace(result.params.url);
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown){

                    }
                });
            });
        });
    </script>
</body>
</html>