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

<script type="text/javascript" src="/resources/js/html2canvas.js"></script>
<script type="text/javascript" src="/resources/js/jquery.Jcrop.js"></script>
<script type="text/javascript" src="/resources/js/image.js"></script>
<script type="text/javascript" src="/resources/js/map.js"></script>
<script src="https://maps.googleapis.com/maps/api/js?v=3&key=AIzaSyDY0kkJiTPVd2U7aTOAwhc9ySH6oHxOIYM&sensor=false&libraries=places" type="text/javascript"></script>

<link rel="stylesheet" href="/resources/css/maps.css" type="text/css" />
<link rel="stylesheet" href="/resources/css/jquery.Jcrop.css" type="text/css" />
<link rel="stylesheet" href="/resources/css/jcrop.css" type="text/css" />

<script type="text/javascript" src="/resources/js/merchant.js"></script>

<script type="text/javascript">
    $(document).ready(function(){
        Merchant.loadMerchant();
    });
</script>


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
                                <div class="drop_info">Drop image file <br /> (or click to browse)</div>
                                <div class="drop_title">Brand Logo</div>
                            </div>
                            <input type="file" onchange="Image.readURL(this)" id="brand_logo_input" name="brand_logo_input" class="hidden" />
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
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="Business Name" name="business_name" id="business_name" class="form-control" disabled="disabled">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="URL" name="url" id="url" class="form-control">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="Contact Person" name="contact_person" id="contact_person" class="form-control">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="Contact Email" name="contact_email" id="contact_email" class="form-control" disabled="disabled">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="Contact No." name="contact_no" id="contact_no" class="form-control">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="Company Registration No." name="registration_no" id="registration_no" class="form-control">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="info_display none_editable"></div>
                                    <div class="info_edit editable">
                                        <input type="text" placeholder="VAT" name="vat" id="vat" class="form-control">
                                    </div>
                                </div>
                            </div>

                            <div class="profile_map form-group col-lg-7">
                                <div class="profile_sub_title">BUSINESS LOCATION</div>
                                <div class="profile_map_body">
                                    <div class="map-container">
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
