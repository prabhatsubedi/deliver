<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Merchant Profile</title>

    <%@include file="../includes/head.jsp" %>


<%--
    latitude

    27.483258464149618

    longitude

    85.58349609375--%>

</head>
<body>

<%@include file="../includes/sidebar_merchant.jsp" %>

<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/html2canvas.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.Jcrop.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/image.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/map.js"></script>
<script src="https://maps.googleapis.com/maps/api/js?v=3&key=AIzaSyDY0kkJiTPVd2U7aTOAwhc9ySH6oHxOIYM&sensor=false&libraries=places" type="text/javascript"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/maps.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery.Jcrop.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jcrop.css" type="text/css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/merchant.js"></script>

<script type="text/javascript">
    var merchantRole = false;
    $(document).ready(function(){
        Merchant.loadMerchant();
        Merchant.loadEditMerchant();
    });
</script>
<sec:authorize access="hasRole('ROLE_MERCHANT')">
    <script type="text/javascript">
        merchantRole = true;
    </script>
</sec:authorize>


<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Merchant Profile</h1>
        </div>
        <div class="main_content">

            <div class="profile_container">
                <form role="form" id="merchant_form" method="POST" action="">
                    <div class="profile_header clearfix">
                        <div class="profile_header_image pull-left">
                            <div id="brand_logo" class="drop_zone user_image disabled unselectable text-center maintain_ratio" mr-width="200" mr-height="200">
<%--                                <div class="drop_info">Drop image file <br /> (or click to browse) <br /> Min Size: 200x200</div>
                                <div class="drop_title">Brand Logo</div>--%>
                            </div>
                            <input type="file" onchange="Image.readURL(this)" id="brand_logo_input" name="brand_logo_input" class="hidden" />
                        </div>
                        <div class="profile_header_info pull-left">
                            <div class="info1 business_name"></div>
                            <div class="info2 partner_status"></div>
                        </div>
                        <div class="action detail_options pull-right">
                            <a class="pull-left btn btn_green edit_btn none_editable glyphicon glyphicon-edit"></a>
                            <div class="pull-left action_buttons editable hidden">
                                <a class="btn btn_green save_btn glyphicon glyphicon-floppy-disk"></a>
                                <a class="btn btn_green cancel_btn glyphicon glyphicon-remove"></a>
                            </div>
                        </div>
                    </div>
                    <div class="profile_body">
                        <div class="form_container form_editable full_width clearfix">
                            <div class="profile_info col-lg-5">
                                <div class="profile_sub_title">INFORMATION</div>

                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label no_pad">Business Name</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_business_name"></div>
                                    </div>
                                </div>

                                <div class="form-group clearfix">
                                    <label for="url" class="col-lg-4 floated_label no_pad">URL</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_url none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="url" id="url" class="form-control">
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group clearfix">
                                    <label for="contact_person" class="col-lg-4 floated_label no_pad">Contact Person</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_contact_person none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="contact_person" id="contact_person" class="form-control">
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label no_pad">Contact Email</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_contact_email"></div>
                                    </div>
                                </div>

                                <div class="form-group clearfix">
                                    <label for="contact_no" class="col-lg-4 floated_label no_pad">Contact No.</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_contact_no none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="contact_no" id="contact_no" class="form-control">
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group clearfix">
                                    <label for="registration_no" class="col-lg-4 floated_label no_pad">Company Registration No.</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_registration_no none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="registration_no" id="registration_no" class="form-control">
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group clearfix">
                                    <label for="vat" class="col-lg-4 floated_label no_pad">VAT</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_vat none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="vat" id="vat" class="form-control">
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group clearfix">
                                    <label for="pan" class="col-lg-4 floated_label no_pad">PAN</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_pan none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="pan" id="pan" class="form-control">
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group clearfix">
                                    <label for="partnership" class="col-lg-4 floated_label no_pad">Partnership Status</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_partnership none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <select id="partnership" name="partnership" class="partnership col-xs-12 no_pad no_margin" data-style="form-control">
                                                <option value="true">Partner</option>
                                                <option value="false">Non Partner</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group clearfix">
                                    <label for="commission" class="col-lg-4 floated_label no_pad">Commission Percent</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_commission none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="commission" id="commission" class="form-control">
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group clearfix">
                                    <label for="service_fee" class="col-lg-4 floated_label no_pad">Processing Charge</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_service_fee none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="full_name" id="service_fee" class="form-control">
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label no_pad">Status</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_status"></div>
                                    </div>
                                </div>

                            </div>

                            <div class="profile_map form-group col-lg-7">
                                <div class="profile_sub_title">BUSINESS LOCATION</div>
                                <div class="profile_map_body">
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
</div>



<div class="infowindow_template hidden">
    <div class="infowindow">
        <div class="address_name address_lines">
            <span class="address_span"></span>
            <div class="val_address_value form-control info_display none_editable"></div>
            <input type="text" name="address_name" class="address_value form-control info_edit editable hidden" placeholder="Location Name" >
        </div>
        <div class="address_street_name address_lines">
            <span class="address_span"></span>
            <div class="val_address_value form-control info_display none_editable"></div>
            <input type="text" name="address_street_name" class="address_value form-control info_edit editable hidden" placeholder="Street Name">
        </div>
        <div class="address_city address_lines">
            <span class="address_span"></span>
            <div class="val_address_value form-control info_display none_editable"></div>
            <input type="text" name="address_city" class="address_value form-control info_edit editable hidden" placeholder="City">
        </div>
        <div class="address_state address_lines">
            <span class="address_span"></span>
            <div class="val_address_value form-control info_display none_editable"></div>
            <input type="text" name="address_state" class="address_value form-control info_edit editable hidden" placeholder="State">
        </div>
        <div class="address_lines">
            <button type="button" class="save_marker <%--glyphicon glyphicon-floppy-disk--%> btn btn-primary">Save</button>
        </div>
    </div>
</div>

</body>
</html>
