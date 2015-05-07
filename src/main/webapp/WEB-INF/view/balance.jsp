<%--
  Created by IntelliJ IDEA.
  User: Lunek
  Date: 5/7/2015
  Time: 3:56 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Balance</title>

    <%@include file="includes/head.jsp" %>
    <script type="text/javascript">
        $(document).ready(function(){

            var callback = function(a, b) {
                console.log(a);
                console.log(b);
            };
            callback.func = function(data) {
                console.log(data);
            };

            Main.request('http://192.168.1.204:8080/client/transactions/add_fund/fbId/789233697779841', {}, callback, {accessToken: "dfsdfds", "Access-Control-Allow-Origin":"*"})

        });
    </script>
</head>
<body>

</body>
</html>
