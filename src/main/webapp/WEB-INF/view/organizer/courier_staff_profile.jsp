<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Courier Staff Profile</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="/resources/js/bootstrap-select.js"></script>
    <script type="text/javascript" src="/resources/js/map.js"></script>
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp"></script>

    <script type="text/javascript" src="/resources/js/html2canvas.js"></script>
    <script type="text/javascript" src="/resources/js/jquery.Jcrop.js"></script>
    <script type="text/javascript" src="/resources/js/image.js"></script>
    <script type="text/javascript" src="/resources/js/courier_staff.js"></script>

    <link rel="stylesheet" href="/resources/css/jquery.Jcrop.css" type="text/css"/>
    <link rel="stylesheet" href="/resources/css/jcrop.css" type="text/css"/>
    <script>
        $(document).ready(function () {
            CourierStaff.getCourierStaffProfile();
            CourierStaff.loadEditCourierStaff();
        });
    </script>

</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">
<%@include file="../includes/header.jsp" %>
<div class="body">
    <div class="heading clearfix">
        <h1 class="pull-left">Courier Staff Profile</h1>
    </div>
    <div class="main_content">
        <div class="profile_container">
            <div class="form_container">
                <form role="form" id="courier_boy_form" method="POST" action="">
                    <div class="profile_header">
                        <div class="profile_header_image">
                            <div id="drop_zone"
                                 class="form-group profile_image unselectable text-center image_selected">
                            </div>
                            <input id="id" type="hidden">
                            <input id="imageInput" class="hidden" type="file" onchange="Image.readURL(this)">
                        </div>
                        <div class="profile_header_info">
                            <div class="name"></div>
                            <div class="title"></div>
                        </div>
                        <div class="action">
                            <a class="btn btn_green pull-right edit_btn none_editable">Edit</a>

                            <div class="action_buttons editable">
                                <a class="btn pull-right btn_green save_btn editable">Save</a>
                                <a class="btn pull-right btn_green cancel_btn editable">Cancel</a>
                            </div>
                        </div>
                    </div>
                    <div class="profile_body">
                        <div class="profile_info">
                            <div class="profile_sub_title">INFORMATION</div>
                            <div class="info_block name">
                                <div class="label"> Name</div>
                                <div class="val">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" class="form-control" id="fullName" name="fullName">
                                    </div>
                                </div>
                            </div>
                            <div class="info_block email">
                                <div class="label"> Email:</div>
                                <div class="val">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <div class="form-group">
                                            <input type="text" class="form-control" id="email" name="email">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="info_block mobile">
                                <div class="label"> Mobile:</div>
                                <div class="val">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <div class="form-group">
                                            <input type="text" class="form-control" id="mobile" name="mobile"></div>
                                    </div>
                                </div>
                            </div>
                            <div class="info_block password editable">
                                <div class="label"> Password:</div>
                                <div class="val">
                                    <div class="info_edit editable">
                                        <div class="form-group">
                                            <input type="password" class="form-control" id="password"></div>
                                    </div>
                                </div>
                            </div>
                            <div class="info_block gender">
                                <div class="label"> Gender:</div>
                                <div class="val">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <select class="form-control" type="text" id="gender" name="gender">
                                            <option value="0">Select Gender</option>
                                            <option value="MALE">Male</option>
                                            <option value="FEMALE">Female</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="info_block vehicle_type">
                                <div class="label"> Vehicle Type:</div>
                                <div class="val">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <select class="form-control" type="text" id="vehicleType" name="vehicleType">
                                            <option value="0">Select Vehicle Type</option>
                                            <option value="ON_FOOT">On Foot</option>
                                            <option value="BICYCLE">Bicycle</option>
                                            <option value="MOTORBIKE">Motorbike</option>
                                            <option value="CAR">Car</option>
                                            <option value="TRUCK">Truck</option>
                                            <option value="OTHERS">Others</option>
                                        </select>

                                    </div>
                                </div>
                            </div>
                            <div class="info_block state vehicle_no">
                                <div class="label"> Vehicle No:</div>
                                <div class="val">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <div class="form-group">
                                            <input type="text" class="form-control" id="vehicleNo" name="vehicleNo"></div>
                                    </div>
                                </div>
                            </div>
                            <div class="info_block state license_no">
                                <div class="label"> License No:</div>
                                <div class="val">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <div class="form-group">
                                            <input type="text" class="form-control" id="licenseNo" name="licenseNo"></div>
                                    </div>
                                </div>
                            </div>
                            <div class="info_block street">
                                <div class="label"> Street:</div>
                                <div class="val">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable"><input type="text" class="form-control" id="street" name="street">
                                    </div>
                                </div>
                            </div>
                            <div class="info_block city">
                                <div class="label"> City:</div>
                                <div class="val">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <div class="form-group">
                                            <input type="text" class="form-control" id="city" name="city"></div>
                                    </div>
                                </div>
                            </div>
                            <div class="info_block state">
                                <div class="label"> State:</div>
                                <div class="val">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <div class="form-group">
                                            <input type="text" class="form-control" id="state" name="state"></div>
                                    </div>
                                </div>
                            </div>
                            <div class="info_block country">
                                <div class="label"> Country:</div>
                                <div class="val">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <div class="form-group">
                                            <input type="text" class="form-control" id="country"  name="country" disabled="disabled">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="info_block status">
                                <div class="label"> Status:</div>
                                <div class="val">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <select class="form-control" type="text" id="status">
                                            <option value="ACTIVE">Active</option>
                                            <option value="INACTIVE">Inactive</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="profile_map">
                            <div class="profile_sub_title">STATUS IN GOOGLE MAP</div>
                            <div class="profile_map_body">
                                <div id="no-edit-map-canvas"></div>
                            </div>
                            <div class="profile_map_info">

                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="crop_img_modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-body">

                <img id="jcrop_target"/>

                <div class="preview-pane preview-pane-org">
                    <div class="preview-container">
                        <img id="jcrop_preview"/>
                    </div>
                </div>
                <div class="preview-pane">
                    <div class="preview-head">Preview</div>
                    <div class="preview-container-dup">
                        <img id="jcrop_preview_dup"/>
                    </div>
                    <div class="preview-options">
                        <div class="aspect_ratio">
                            <label style="font-weight: normal; line-height: 30px; margin-bottom: 0px;"><input
                                    type="checkbox" id="ar_lock" class="check"
                                    style="float: left; margin-right: 5px; margin-top: 9px;"/>Maintain Aspect
                                ratio</label>
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
