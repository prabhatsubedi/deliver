<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Courier Staffs</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="/resources/js/jquery.dataTables.min.js"></script>
    <link href="/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen" />
    <script type="text/javascript" src="/resources/js/manager.js"></script>

    <script type="text/javascript">

        $(document).ready(function(){

            Manager.getCourierStaffs();

        });

    </script>


</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Courier Staffs</h1>
            <a class="btn btn_green pull-right" href="/courier_staff/create">Add Courier Staff</a>
        </div>
        <div class="main_content">

            <div class="table-view">
                <table id="courier_staff_table">
                    <thead>
                    <tr>
                        <th rowspan="2">SN</th>
                        <th rowspan="2"><div class="width_150"> Courier Staff Name </div></th>
                        <th rowspan="2"><div class="width_100"> Contact No. </div></th>
                        <th colspan="3"> Current Job </th>
                        <th rowspan="2"><div class="width_100"> Current Balance </div></th>
                        <th rowspan="2"><div class="width_200"> Action </div></th>
                    </tr>
                    <tr>
                        <th><div class="width_100"> Order No. </div></th>
                        <th><div class="width_150"> Order Name </div></th>
                        <th><div class="width_150"> Job Status </div></th>
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
