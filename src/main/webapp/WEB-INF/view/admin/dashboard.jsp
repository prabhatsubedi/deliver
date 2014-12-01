<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:url value="/j_spring_security_logout" var="logoutUrl" />
<head>
    <title>Hello World!</title>
    <script src="<c:url value="/resources/js/jquery-2.1.1.min.js" />"></script>

    <meta name="_csrf" content="${_csrf.token}"/>
    <!-- default header name is X-CSRF-TOKEN -->
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <meta id="_csrf_token" name="${_csrf.parameterName}" content="${_csrf.token}"/>

    <%--<form action="${logoutUrl}" method="post" id="logoutForm">
        <input type="hidden" name="${_csrf.parameterName}"
               value="${_csrf.token}" />
    </form>--%>
</head>
<body>
    <div class="logout">Logout</div>
    <p>This is the admin dashboard page</p>
    <script>
        $(document).ready(function(){
             $('.logout').click(function(event){
                 event.preventDefault();
                 var tokenName = $("meta[id='_csrf_token']").attr("name");
                 var tokenValue = $("meta[id='_csrf_token']").attr("content");
                 var jsonData = {};
                 jsonData[""+tokenName+""] = tokenValue;
                 $.ajax({
                     url: "${logoutUrl}",
                     data: jsonData,
                     type: "POST",
                     beforeSend: function (xhr) {
                         xhr.setRequestHeader("X-Ajax-call", "true");
                     },
                     success: function(result) {
                         console.log(result);
                     }
             });
          });
      });
    </script>
</body>
</html>
