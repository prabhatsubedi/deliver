<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Add Courier Staff</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/html2canvas.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.Jcrop.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/image.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/courier_staff.js"></script>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery.Jcrop.css" type="text/css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jcrop.css" type="text/css" />

    <script type="text/javascript">

        $(document).ready(function(){

            CourierStaff.loadAddCourierStaff();

        });

    </script>

</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Add Courier Staff</h1>
        </div>
        <div class="main_content">

            <div class="form_container">
                <form role="form" id="courier_boy_form" method="POST" action="">
                    <div class="courier_staff_profile_pic">
                        <div id="profile_image" class="drop_zone disabled unselectable text-center maintain_ratio" mr-width="200" mr-height="200">
                            <div class="drop_info">Drop image file <br /> (or click to browse)</div>
                            <div class="drop_title">Profile Picture</div>
                        </div>
                        <input type="file" onchange="Image.readURL(this)" id="profile_image_input" name="profile_image_input" class="hidden" />

                        <div class="profile_pic_left">
                            <div class="form-group">
                                <input type="text" class="form-control" id="full_name" name="full_name" placeholder="Name of Courier Staff">
                            </div>
                            <div class="form-group">
                                <input type="text" class="form-control" id="email" name="email" placeholder="Email Address (Optional)">
                            </div>
                            <div class="form-group">
                                <input type="text" class="form-control" id="mobile" name="mobile" placeholder="Mobile No.">
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <input type="password" class="form-control" id="password" name="password" placeholder="Password">
                    </div>
                    <div class="form-group">
                        <select id="gender" name="gender" class="gender col-xs-12 no_pad no_margin" data-style="form-control">
                            <option value="0">Select Gender</option>
                            <option value="MALE">Male</option>
                            <option value="FEMALE">Female</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" id="street" name="street" placeholder="Street">
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" id="city" name="city" placeholder="City">
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" id="state" name="state" placeholder="State">
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" id="country" name="country" placeholder="Country">
                    </div>
                    <div class="form-group">
                        <select id="vehicle_type" name="vehicle_type" class="vehicle_type col-xs-12 no_pad no_margin" data-style="form-control">
                            <option value="0">Select Vehicle Type</option>
                            <option value="ON_FOOT">On Foot</option>
                            <option value="BICYCLE">Bicycle</option>
                            <option value="MOTORBIKE">Motorbike</option>
                            <option value="CAR">Car</option>
                            <option value="TRUCK">Truck</option>
                            <option value="OTHERS">Others</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" id="vehicle_no" name="vehicle_no" placeholder="Vehicle No.">
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" id="license_no" name="license_no" placeholder="License No.">
                    </div>
                    <div class="clearfix">
                        <button class="btn btn_green pull-right" type="submit">Add</button>
                        <a class="btn btn_green pull-right cancel" href="/organizer/courier_staff/list">Cancel</a>
                    </div>
                </form>
            </div>

        </div>
    </div>
</div>

<div class="modal fade" id="crop_img_modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-body">

                <img id="jcrop_target" />

                <div class="preview-pane preview-pane-org">
                    <div class="preview-container">
                        <img id="jcrop_preview"  />
                    </div>
                </div>
                <div class="preview-pane">
                    <div class="preview-head">Preview</div>
                    <div class="preview-container-dup">
                        <img id="jcrop_preview_dup" />
                    </div>
                    <div class="preview-options">
                        <div class="aspect_ratio">
                            <label style="font-weight: normal; line-height: 30px; margin-bottom: 0px;"><input type="checkbox" id="ar_lock" class="check" style="float: left; margin-right: 5px; margin-top: 9px;"/>Maintain Aspect ratio</label>
                        </div>
                    </div>
                </div>

            </div>
            <div class="modal-footer">
                <div class="col-lg-6 no_pad">
                    <button type="button" id="cancel_preview" class="btn btn-primary">Cancel</button>
                </div>
                <div class="col-lg-6 no_pad">
                    <button type="button" id="apply_preview" class="btn btn-primary">Set Profile Image</button>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
