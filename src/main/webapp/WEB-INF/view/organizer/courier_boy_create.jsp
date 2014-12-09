<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Add Courier Boy</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="/resources/js/jquery.validate.js"></script>
    <script type="text/javascript" src="/resources/js/courier_boy.js"></script>

    <script type="text/javascript">

        $(document).ready(function(){

            CourierBoy.loadAddCourierBoy();

        });

    </script>

</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Add Courier Boy</h1>
            <a class="btn btn_head pull-right" href="/courier_boy/create" id="add_courier_boy" >Save</a>
        </div>
        <div class="main_content">

            <div class="form_container">
                <form role="form" id="courier_boy_form" method="POST" action="">
                    <div class="form-group">
                        <input type="text" class="form-control" id="full_name" name="full_name" placeholder="Name of Courier Boy">
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" id="email" name="email" placeholder="Email Address (Optional)">
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" id="mobile" name="mobile" placeholder="Mobile No.">
                    </div>
                    <div class="form-group">
                        <select id="gender" name="gender" class="gender col-xs-12 no_pad no_margin" data-style="form-control">
                            <option>Select Gender</option>
                            <option>Male</option>
                            <option>Female</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" id="address" name="address" placeholder="Address">
                    </div>
                    <div class="form-group">
                        <select id="vehicle_type" name="vehicle_type" class="vehicle_type col-xs-12 no_pad no_margin" data-style="form-control">
                            <option>Select Vehicle Type</option>
                            <option>On Foot</option>
                            <option>Bicycle</option>
                            <option>Motorbike</option>
                            <option>Car</option>
                            <option>Truck</option>
                            <option>Others</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" id="vehicle_no" name="vehicle_no" placeholder="Vehicle No.">
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" id="license_no" name="license_no" placeholder="License No.">
                    </div>
                    <div class="form-group">
                        <input type="password" class="form-control" id="password" name="password" placeholder="Password">
                    </div>
                </form>
            </div>

        </div>
    </div>
</div>

</body>
</html>
