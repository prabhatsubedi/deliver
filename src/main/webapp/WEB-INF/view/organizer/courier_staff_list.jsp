<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Shopper</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/map.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/maps.css" type="text/css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/dataTables.tableTools.js"></script>
    <link href="${pageContext.request.contextPath}/resources/css/dataTables.tableTools.css" rel="stylesheet" type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/resources/css/maps.css" rel="stylesheet" type="text/css" media="screen"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/manager.js"></script>
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp"></script>

    <script type="text/javascript">
        $(document).ready(function () {
            Manager.getCourierStaffs();
            Manager.getCourierBoyMap();
            Manager.getCourierBoyAccount();
            Manager.updateCourierBoyAccount();
            Manager.submitCourierBoyPreviousAmount();
            Manager.submitCourierBoyWalletAmount();
        });
    </script>


</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="modal fade" id="modal_map" tabindex="-1" role="dialog" aria-labelledby="mapLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="mapLabel">Shopper Location</h4>
            </div>
            <div class="modal-body">

                    <div class="map-container">
                        <div id="custom_map_controls" class="clearfix">
                            <div id="scroll_zoom" class="pull-left">Enable Scroll Zoom</div>
                        </div>
                        <div id="map-canvas"></div>
                    </div>

            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="modal_account" tabindex="-1" role="dialog" aria-labelledby="accountLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="accountLabel">Update Account</h4>
            </div>
            <div class="modal-body">
                <div class="table-view">
                    <input type="hidden" id="due_amount_val">
                    <input type="hidden" id="to_be_submitted_val">
                    <table id="accountTable" class="dataTable no-footer" role="grid" aria-describedby="accountTable">
                        <tr>
                            <th>SN</th>
                            <th>Title</th>
                            <th>Amount</th>
                            <th>Action</th>
                        </tr>
                        <tr>
                            <th>1</th>
                            <td>Previous Day Due Amount</td>
                            <td class="due_amount"></td>
                            <td><input type="checkbox" id="ack"></td>
                        </tr>
                        <tr>
                            <th>2</th>
                            <td>Current Available Balance</td>
                            <td class="available_balance"></td>
                            <td></td>
                        </tr>
                        <tr>
                            <th>3</th>
                            <td>Today's Amount to be submitted to Account</td>
                            <td class="to_be_submitted"></td>
                            <td><input type="checkbox" id="submit"></td>
                        </tr>
                        <tr>
                            <th>4</th>
                            <td>Advance Amount</td>
                            <td class="advance_amount form_container"><input type="text" id="advance_amount_val" class="form-control"></td>
                            <td></td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Shopper</h1>
            <a class="btn btn_green pull-right" href="/organizer/courier_staff/create">Add Shopper</a>
        </div>
        <div class="main_content">

            <div class="table-view">
                <table id="courier_staff_table">
                    <thead>
                    <tr>
                        <th rowspan="2">SN</th>
                        <th rowspan="2">
                            <div class="width_150"> Shopper Name</div>
                        </th>
                        <th rowspan="2">
                            <div class="width_100"> Contact No.</div>
                        </th>
                        <th colspan="5"> Current Job</th>
                        <th rowspan="2"> Status</th>
                        <th rowspan="2">
                            <div class="width_100"> Current Balance</div>
                        </th>
                        <th rowspan="2">
                            <div class="width_150"> Action</div>
                        </th>
                    </tr>
                    <tr>
                        <th>
                            <div class="width_50"> Order No.</div>
                        </th>
                        <th>
                            <div class="width_250"> Order Name</div>
                        </th>
                        <th>
                            <div class="width_150"> Job Status</div>
                        </th>
                        <th>
                            <div class="width_100">Assigned Time</div>
                        </th>
                        <th>
                            <div class="width_100">Elapsed Time </div>
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>

        </div>
    </div>
</div>


<div class="infowindow_template hidden">
    <div class="infowindow">
        <p><strong class="name_line"></strong></p>
        <p class="address_line"></p>
    </div>
</div>

</body>
</html>
