<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
<title>Koolkat Signup</title>

    <%@include file="includes/head.jsp" %>
    
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.validate.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/merchant.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/html2canvas.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.Jcrop.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/image.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/map.js"></script>
    <script src="https://maps.googleapis.com/maps/api/js?v=3&key=AIzaSyDY0kkJiTPVd2U7aTOAwhc9ySH6oHxOIYM&sensor=false&libraries=places" type="text/javascript"></script>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/maps.css" type="text/css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery.Jcrop.css" type="text/css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jcrop.css" type="text/css" />

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/signup.js"></script>

</head>
<body>

<div class="modal fade modal_form" id="modal_signup" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <form role="form" id="signup_form" method="POST" action="">
            <div class="modal-content">
                <div class="modal-header text-center">
                    Merchant Sign up
                </div>
                <div class="modal-body">
                    <!-- Nav tabs -->
                    <ul class="nav nav-pills">
                        <li class="col-xs-4 no_pad active"><a href="#step_1" data-toggle="tab">Step 1</a></li>
                        <li class="col-xs-4 no_pad disabled_tab"><a href="#step_2" data-toggle="tab">Step 2</a></li>
                        <li class="col-xs-4 no_pad disabled_tab"><a href="#step_3" data-toggle="tab">Step 3</a></li>
                    </ul>

                    <!-- Tab panes -->
                    <div class="tab-content body_padding">
                        <div class="tab-pane active" id="step_1">
                            <div class="form-group">
                                <input type="text" class="form-control" id="business_name" name="business_name" placeholder="Business Name">
                            </div>
                            <div id="drop_zone" class="form-group business_logo unselectable text-center">
                                <div class="drop_info">Drop image file <br /> (or click to browse) <br /> Min Size: 200x200</div>
                                <div class="drop_title">Business Logo</div>
                            </div>
                            <input type="file" onchange="Image.readURL(this)" id="logo_input" class="hidden" />
                            <div class="form-group">
                                <input type="text" class="form-control" id="url" name="url" placeholder="URL">
                            </div>
                        </div>

                        <div class="tab-pane" id="step_2">
                            <div class="form-group">
                                <input type="text" class="form-control" id="contact_person" name="contact_person" placeholder="Contact Person">
                            </div>
                            <div class="form-group">
                                <input type="text" class="form-control" id="contact_email" name="contact_email" placeholder="Contact Email">
                            </div>
                            <div class="form-group">
                                <input type="text" class="form-control" id="contact_no" name="contact_no" placeholder="Contact No.">
                            </div>
                            <div class="form-group">
                                <input type="text" class="form-control" id="registration_no" name="registration_no" placeholder="Company Registration No.">
                            </div>
                            <div class="form-group">
                                <input type="text" class="form-control" id="vat" name="vat" placeholder="VAT">
                            </div>
                            <div class="form-group">
                                <input type="text" class="form-control" id="pan" name="pan" placeholder="PAN">
                            </div>
                        </div>

                        <div class="tab-pane" id="step_3">
                            <div class="map-container">
                                <div id="search_box">
                                    <input id="pac-input" class="controls" type="text" placeholder="Enter Postal/Zipcode to focus map & place marker. Click on marker to add address.">
                                </div>
                                <div id="custom_map_controls" class="clearfix">
                                    <div id="scroll_zoom" class="pull-left">Enable Scroll Zoom</div>
                                    <div id="clear_markers" class="pull-left">Clear All Markers</div>
                                </div>
                                <div id="map-canvas"></div>
                            </div>
                            <div class="form-group checkbox">
                                <label>
                                    <input type="checkbox" id="accept_terms" name="accept_terms">
                                    I acknowledge that I have read the <a href="#">Terms of use</a> & <a href="#">Privacy Policy</a>.
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="col-xs-6 no_pad">
                        <a href="/" class="btn btn-default">Cancel</a>
                    </div>
                    <div class="col-xs-6 no_pad">
                        <button type="submit" class="btn btn-default next">Next</button>
                    </div>
                </div>
            </div>
        </form>
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
                    <button type="button" id="apply_preview" class="btn btn-primary">Set Logo</button>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="infowindow_template hidden">
    <div class="infowindow">
        <div class="address_name address_lines">
            <span class="address_span"></span>
            <input type="text" name="address_name" class="address_value form-control" placeholder="Location Name" >
        </div>
        <div class="address_street_name address_lines">
            <span class="address_span"></span>
            <input type="text" name="address_street_name" class="address_value form-control" placeholder="Street Name">
        </div>
        <div class="address_city address_lines">
            <span class="address_span"></span>
            <input type="text" name="address_city" class="address_value form-control" placeholder="City">
        </div>
        <div class="address_state address_lines">
            <span class="address_span"></span>
            <input type="text" name="address_state" class="address_value form-control" placeholder="State">
        </div>
        <div class="address_lines">
            <button type="button" class="save_marker <%--glyphicon glyphicon-floppy-disk--%> btn btn-primary">Save</button>
        </div>
    </div>
</div>

</body>
</html>