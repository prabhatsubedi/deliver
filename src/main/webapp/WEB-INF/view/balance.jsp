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

            $('#payment').submit(function(e){
                e.preventDefault();

                var callback = function(status, data) {
                    if(data.success) {
                        var paymentInfo = data.params.paymentGatewayInfo;
                        $('#form').attr('action', paymentInfo.pgrequestURL);
                        $('#data').val(paymentInfo.data);
                        $('#seal').val(paymentInfo.seal);
                        $('#interfaceVersion').val(paymentInfo.interfaceVersion);
                        $('#form').submit();
                    }
                };

                Main.request('/client/transactions/add_fund/fbId/789233697779841', { "amount":$('#amount').val() }, callback, {accessToken: "dfsdfds"});

                return false;
            });

        });
    </script>
</head>
<body>


<form method="post" id="payment">
    <input type="text" name="amount" id="amount">
    <input type="submit">
</form>

<form method="post" id="form" class="hidden">
    <input type="text" name="data" id="data">
    <input type="text" name="seal" id="seal">
    <input type="text" name="interfaceVersion" id="interfaceVersion">
</form>

</body>
</html>
