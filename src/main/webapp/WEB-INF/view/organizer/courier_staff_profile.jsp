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
                <form role="form" id="courier_boy_form" method="POST" action="">
                    <div class="profile_header clearfix">
                        <div class="profile_header_image pull-left">
                            <div id="profile_image" class="drop_zone user_image disabled unselectable text-center maintain_ratio" mr-width="200" mr-height="200">
                                <div class="drop_info">Drop image file <br /> (or click to browse)</div>
                                <div class="drop_title">Brand Logo</div>
                            </div>
                            <input type="file" onchange="Image.readURL(this)" id="profile_image_input" name="profile_image_input" class="hidden" />
                        </div>
                        <div class="profile_header_info pull-left">
                            <div class="info1 business_name"></div>
                            <div class="info2 contact_person"></div>
                        </div>
                        <div class="action detail_options pull-right">
                            <a class="pull-left btn btn_green edit_btn none_editable glyphicon glyphicon-edit"></a>
                            <div class="pull-left action_buttons editable hidden">
                                <a class="btn btn_green save_btn editable glyphicon glyphicon-floppy-disk"></a>
                                <a class="btn btn_green cancel_btn editable glyphicon glyphicon-remove"></a>
                            </div>
                        </div>
                    </div>
                    <div class="profile_body">
                        <div class="form_container full_width clearfix">
                            <div class="profile_info col-lg-5">
                                <div class="profile_sub_title">INFORMATION</div>

                                <div class="form-group">
                                    <div class="info_display val_full_name none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="Name of Courier Staff" name="full_name" id="full_name" class="form-control">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class=" info_display val_email none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="Email Address (Optional)" name="email" id="email" class="form-control">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class=" info_display val_mobile none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="Mobile No." name="mobile" id="mobile" class="form-control">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class=" info_display val_password none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="password" placeholder="Password" name="password" id="password" class="form-control">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class=" info_display val_gender none_editable"></div>
                                    <div class="info_edit editable">
                                        <select id="gender" name="gender" class="gender col-xs-12 no_pad no_margin" data-style="form-control">
                                            <option value="0">Select Gender</option>
                                            <option value="MALE">Male</option>
                                            <option value="FEMALE">Female</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class=" info_display val_street none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="Street" name="street" id="street" class="form-control">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class=" info_display val_city none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="City" name="city" id="city" class="form-control">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class=" info_display val_state none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="State" name="state" id="state" class="form-control">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class=" info_display val_country none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="Country" name="country" id="country" class="form-control valid">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class=" info_display val_vehicle_type none_editable"></div>
                                    <div class="info_edit editable">
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
                                </div>

                                <div class="form-group">
                                    <div class=" info_display val_vehicle_no none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="Vehicle No." name="vehicle_no" id="vehicle_no" class="form-control">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class=" info_display val_license_no none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="License No." name="license_no" id="license_no" class="form-control">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class=" info_display val_status none_editable"></div>
                                    <div class="info_edit editable">
                                        <select id="status" name="status" class="status col-xs-12 no_pad no_margin" data-style="form-control">
                                            <option value="ACTIVE">Active</option>
                                            <option value="INACTIVE">Inactive</option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <div class="profile_map form-group col-lg-7">
                                <div class="profile_sub_title">Courier Staff Location</div>
                                <div class="profile_map_body">
                                    <div id="no-edit-map-canvas"></div>
                                </div>
                                <div class="profile_map_info">

                                </div>
                            </div>
                        </div>
                    </div>
                </form>
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
