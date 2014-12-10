<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Merchants</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="/resources/js/jquery.validate.js"></script>
    <script type="text/javascript" src="/resources/js/jquery.dataTables.min.js"></script>

    <script type="text/javascript" src="/resources/js/merchant.js"></script>


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
                <table id="advertisers_table">
                    <thead>
                    <tr>
                        <th>SN</th>
                        <th><div class="th_name_width"> Merchant Name </div></th>
                        <th><div class="th_name_width"> Contact Person </div></th>
                        <th><div class="th_name_width"> Contact No. </div></th>
                        <th><div class="th_email_width"> Status </div></th>
                        <th><div class="th_advertisers_action_width"> Action </div></th>
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
