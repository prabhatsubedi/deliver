<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Courier Staffs</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="/resources/js/jquery.dataTables.min.js"></script>
    <link href="/resources/css/jquery.dataTables.css" rel="stylesheet" type="text/css" media="screen"/>
    <script type="text/javascript" src="/resources/js/manager.js"></script>
    <script type="text/javascript" src="/resources/js/map.js"></script>
    <script src="https://maps.googleapis.com/maps/api/js?v=3&key=AIzaSyDY0kkJiTPVd2U7aTOAwhc9ySH6oHxOIYM&sensor=false&libraries=places" type="text/javascript"></script>

    <script type="text/javascript">

        $(document).ready(function () {

            Manager.getCourierStaffs();
            Manager.getCourierBoyMap();

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
                <h4 class="modal-title" id="mapLabel">Courier staff map</h4>
            </div>
            <div class="modal-body">
                <div id="custom_map_controls" class="clearfix">
                    <div id="scroll_zoom" class="pull-left">Enable Scroll Zoom</div>
                    <div id="clear_markers" class="pull-left">Clear All Markers</div>
                </div>
                <div id="map-canvas">

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
                <table id="accountTable" class="dataTable no-footer" role="grid" aria-describedby="accountTable">
                    <tr>
                        <th>SN</th>
                        <th>Title</th>
                        <th>Amount</th>
                        <th>Action</th>
                    </tr>
                    <tr>
                        <td>Previous Day Due Amount</td>
                        <td class="due_amount"></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Current Available Balance</td>
                        <td class="available_balance"></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Today's Amount to be submitted to Account</td>
                        <td class="to_be_submitted"></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Advance Amount</td>
                        <td class="advance_amount"></td>
                        <td></td>
                        <td></td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Courier Staffs</h1>
            <a class="btn btn_green pull-right" href="/organizer/courier_staff/create">Add Courier Staff</a>
        </div>
        <div class="main_content">

            <div class="table-view">
                <table id="courier_staff_table">
                    <thead>
                    <tr>
                        <th rowspan="2">SN</th>
                        <th rowspan="2">
                            <div class="width_150"> Courier Staff Name</div>
                        </th>
                        <th rowspan="2">
                            <div class="width_100"> Contact No.</div>
                        </th>
                        <th colspan="3"> Current Job</th>
                        <th rowspan="2">
                            <div class="width_100"> Current Balance</div>
                        </th>
                        <th rowspan="2">
                            <div class="width_200"> Action</div>
                        </th>
                    </tr>
                    <tr>
                        <th>
                            <div class="width_100"> Order No.</div>
                        </th>
                        <th>
                            <div class="width_150"> Order Name</div>
                        </th>
                        <th>
                            <div class="width_150"> Job Status</div>
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

</body>
</html>
