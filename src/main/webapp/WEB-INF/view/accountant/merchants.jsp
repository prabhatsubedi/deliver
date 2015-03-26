<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Merchants</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/dataTables.tableTools.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/dataTables.tableTools.css" rel="stylesheet" type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen" />

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/manager.js"></script>

    <script type="text/javascript">

        $(document).ready(function(){

            Manager.getMerchants();
            Manager.loadMerchantActivation();

        });

    </script>


</head>
<body>

<sec:authorize ifAnyGranted="ROLE_ACCOUNTANT">
    <%@include file="../includes/sidebar_accountant.jsp" %>
</sec:authorize>
<sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')">
    <%@include file="../includes/sidebar.jsp" %>
</sec:authorize>

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
                        <th>ID</th>
                        <th><div class="width_150"> Merchant Name </div></th>
                        <th><div class="width_100"> Type </div></th>
                        <th><div class="width_150"> Contact Person </div></th>
                        <th><div class="width_150"> Email </div></th>
                        <th><div class="width_100"> Contact No. </div></th>
                        <th> Status </th>
                        <th class="no_sort"><div class="width_150"> Action </div></th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<div class="modal fade modal_form" id="modal_activation" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <form role="form" id="form_activation" method="POST" action="">
            <div class="modal-content">
                <div class="modal-header text-center">
                    Merchant Activation
                </div>
                <div class="modal-body body_padding">
                    <div class="form-group">
                        <select id="partnership" name="partnership" class="partnership col-xs-12 no_pad no_margin" data-style="form-control">
                            <option value="none">Select Partnership Status</option>
                            <option value="true">Partner</option>
                            <option value="false">Non Partner</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" id="commission" name="commission" placeholder="Commission Percent">
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" id="service_fee" name="service_fee" placeholder="Service Fee">
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="col-lg-6 no_pad">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    </div>
                    <div class="col-lg-6 no_pad">
                        <button type="submit" class="btn btn-default next">Activate</button>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

</body>
</html>
