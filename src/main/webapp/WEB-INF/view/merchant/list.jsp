<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Merchants</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="/resources/js/jquery.validate.js"></script>
    <script type="text/javascript" src="/resources/js/jquery.dataTables.min.js"></script>
    <link href="/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen" />

    <script type="text/javascript" src="/resources/js/manager.js"></script>

    <script type="text/javascript">

        $(document).ready(function(){

            Manager.getMerchants();

        });

    </script>


</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Merchants</h1>
        </div>
        <div class="main_content">
            <div class="table-view">
                <table id="merchants_table">
                    <thead>
                    <tr>
                        <th>SN</th>
                        <th><div class="width_150"> Merchant Name </div></th>
                        <th><div class="width_150"> Contact Person </div></th>
                        <th><div class="width_150"> Email </div></th>
                        <th><div class="width_100"> Contact No. </div></th>
                        <th> Status </th>
                        <th><div class="width_150"> Action </div></th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
