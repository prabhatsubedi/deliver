<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Store Form</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/merchant.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/html2canvas.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.Jcrop.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/image.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/map.js"></script>
    <script src="https://maps.googleapis.com/maps/api/js?v=3&key=AIzaSyDY0kkJiTPVd2U7aTOAwhc9ySH6oHxOIYM&sensor=false&libraries=places" type="text/javascript"></script>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/maps.css" type="text/css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery.Jcrop.css" type="text/css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jcrop.css" type="text/css" />

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/store.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            Store.loadAddStore();
        });
    </script>

</head>
<body>

<%@include file="../includes/sidebar_merchant.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Add Store</h1>
        </div>
        <div class="main_content">
            <div class="form_container full_width clearfix">
                <div class="row">
                    <div id="brand_section" class="col-lg-6">
                        <div class="form_section">
                            <div class="form_head">Store</div>
                            <div class="form_content">
                                <form role="form" id="form_brand" method="POST" action="">
                                    <div class="form-group">
                                        <input type="text" class="form-control" id="brand_name" name="brand_name" placeholder="Store Name">
                                    </div>
                                    <div class="brand_images clearfix">
                                        <div id="brand_image" class="drop_zone disabled unselectable text-center maintain_ratio" mr-width="720" mr-height="400">
                                            <div class="drop_info">Drop image file <br /> (or click to browse)</div>
                                            <div class="drop_title">Brand Image</div>
                                        </div>
                                        <input type="file" onchange="Image.readURL(this)" data-dimension="720x400" id="brand_image_input" name="brand_image_input" class="hidden" />
                                        <div id="brand_logo" class="drop_zone disabled unselectable text-center maintain_ratio" mr-width="200" mr-height="200">
                                            <div class="drop_info">Drop image file <br /> (or click to browse)</div>
                                            <div class="drop_title">Brand Logo</div>
                                        </div>
                                        <input type="file" onchange="Image.readURL(this)" id="brand_logo_input" name="brand_logo_input" class="hidden" />
                                    </div>
                                    <div class="form-group clearfix store_open_close">
                                        <div class="col-lg-6">
                                            <select id="open_time" name="open_time" class="col-xs-12 no_pad no_margin" data-style="form-control">
                                            </select>
                                        </div>
                                        <div class="col-lg-6">
                                            <select id="close_time" name="close_time" class="col-xs-12 no_pad no_margin" data-style="form-control">
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <input type="text" class="form-control" id="min_amount" name="min_amount" placeholder="Minimum Order Amount">
                                    </div>
                                    <div class="form-group">
                                        <input type="text" class="form-control" id="brand_url" name="brand_url" placeholder="Store URL">
                                    </div>
                                    <div class="form-group">
                                        <select id="store_categories" name="store_categories" class="haveall compelselection col-xs-12 no_pad no_margin" data-style="form-control" multiple="multiple">
                                            <option value="All">All</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <button type="submit" class="btn btn_green">Add Store</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div id="store_section" class="col-lg-6">
                        <div class="form_section">
                            <div class="form_head">Store Location</div>
                            <div class="form_content clearfix">

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

                                <form role="form" id="form_store" method="POST" action="">
<%--                                    <div class="form-group clearfix">
                                        <button type="button" class="btn btn_green marker_nav marker_prev" disabled="disabled">Prev</button>
                                        <button type="button" class="btn btn_green marker_nav marker_next" disabled="disabled">Next</button>
                                        <button type="button" class="btn btn_green cancel_marker" disabled="disabled">Remove Marker</button>
                                        <button type="submit" class="btn btn_green save_marker" disabled="disabled">Save Address</button>
                                    </div>--%>
<%--                                    <div class="form-group">
                                        <input type="text" class="form-control" id="store_name" name="store_name" placeholder="Store Name">
                                    </div>--%>
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
                                        <input type="text" class="form-control" id="contact_no" name="contact_no" placeholder="Contact No. (Optional)">
                                    </div>
                                    <div class="form-group">
                                        <input type="text" class="form-control" id="contact_person" name="contact_person" placeholder="Contact Person (Optional)">
                                    </div>
                                    <div class="form-group clearfix">
                                        <button type="button" class="btn btn_green marker_nav marker_prev" disabled="disabled">Prev</button>
                                        <button type="button" class="btn btn_green marker_nav marker_next" disabled="disabled">Next</button>
                                        <button type="button" class="btn btn_green cancel_marker" disabled="disabled">Remove Marker</button>
                                        <button type="submit" class="btn btn_green save_marker hidden" disabled="disabled">Save Address</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
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
                    <button type="button" id="apply_preview" class="btn btn-primary">Crop Image</button>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
