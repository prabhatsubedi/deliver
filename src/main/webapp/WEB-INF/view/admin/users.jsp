<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen" />

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/admin.js"></script>

    <script type="text/javascript">

        $(document).ready(function(){

            Admin.loadUserManagement();

        });

    </script>


</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="main_tabs">
            <ul class="nav nav-pills">
                <li class="col-xs-12 col-sm-6 active"><a href="#managers" data-toggle="tab">Managers<button class="btn btn_green pull-right" data-user="Manager" data-role="ROLE_MANAGER" data-toggle="modal" data-target="#modal_user">Create</button></a></li>
                <li class="col-xs-12 col-sm-6"><a href="#accountants" data-toggle="tab">Accountants<button class="btn btn_green pull-right" data-user="Accountant" data-role="ROLE_ACCOUNTANT" data-toggle="modal" data-target="#modal_user">Create</button></a></li>
            </ul>
        </div>
        <div class="main_content">

            <div class="tab-content body_padding">
                <div class="tab-pane active" id="managers">
                    <div class="table-view">
                        <table id="manager_table">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th><div class="width_150"> Name </div></th>
                                <th><div class="width_150"> Email </div></th>
                                <th><div class="width_100"> Phone </div></th>
                                <th> Status </th>
                                <th><div class="width_150"> Actions </div></th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="tab-pane" id="accountants">
                    <div class="table-view">
                        <table id="accountant_table">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th><div class="width_150"> Name </div></th>
                                <th><div class="width_150"> Email </div></th>
                                <th><div class="width_100"> Phone </div></th>
                                <th> Status </th>
                                <th><div class="width_150"> Actions </div></th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>
<form role="form" id="form_user" method="POST" action="">
    <div class="modal-content">
        <div class="modal-header text-center"></div>
        <div class="modal-body body_padding">
            <div class="form-group">
                <input type="text" class="form-control" id="name" name="name" placeholder="Name">
            </div>
            <div class="form-group">
                <input type="email" class="form-control" id="email" name="email" placeholder="Email">
            </div>
            <div class="form-group">
                <input type="phone" class="form-control" id="phone" name="phone" placeholder="Phone">
            </div>
        </div>
        <div class="modal-footer">
            <div class="col-lg-6 no_pad">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            </div>
            <div class="col-lg-6 no_pad">
                <button type="submit" class="btn btn-default next">Create</button>
            </div>
        </div>
    </div>
</form>

<div class="modal fade modal_form" id="modal_user" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
    </div>
</div>

</body>
</html>
