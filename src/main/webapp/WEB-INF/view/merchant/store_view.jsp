<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Store Name</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="/resources/js/merchant.js"></script>
    <script type="text/javascript" src="/resources/js/html2canvas.js"></script>
    <script type="text/javascript" src="/resources/js/jquery.Jcrop.js"></script>
    <script type="text/javascript" src="/resources/js/image.js"></script>
    <script type="text/javascript" src="/resources/js/map.js"></script>
    <script src="https://maps.googleapis.com/maps/api/js?v=3&key=AIzaSyDY0kkJiTPVd2U7aTOAwhc9ySH6oHxOIYM&sensor=false&libraries=places"
            type="text/javascript"></script>

    <link rel="stylesheet" href="/resources/css/maps.css" type="text/css"/>
    <link rel="stylesheet" href="/resources/css/jquery.Jcrop.css" type="text/css"/>
    <link rel="stylesheet" href="/resources/css/jcrop.css" type="text/css"/>

    <script type="text/javascript" src="/resources/js/store.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            Store.loadStore();
        });
    </script>

</head>
<body>

<%@include file="../includes/sidebar_merchant.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Stores</h1>
        </div>
        <div class="main_content">
            <div class="form_container full_width clearfix">
                <div class="row">
                    <div class="form_section store_info clearfix">
                        <div class="col-lg-6">

                            <div class="form_head">
                                <div class="store_name">Store Name</div>
                            </div>
                            <div class="form_content">
                                <div class="brand_images clearfix">
                                    <div id="brand_image" class="drop_zone unselectable text-center maintain_ratio" mr-width="720" mr-height="400">
                                        <div class="drop_info">no image available</div>
                                    </div>
                                    <div id="brand_logo" class="drop_zone unselectable text-center maintain_ratio" mr-width="200" mr-height="200">
                                        <div class="drop_info">no image available</div>
                                    </div>
                                </div>

                                <div class="detail_options clearfix">
                                    <a class="btn btn_green pull-right btn_edit glyphicon glyphicon-edit" href="/merchant/item/form/edit/"></a>
                                    <div class="switch_container pull-right">
                                        <div class="switch switch_activation">
                                            <div class="btn_switch on"></div>
                                        </div>
                                    </div>
                                </div>

                                <div class="other_info">

                                    <div class="detail_row">
                                        <label class="detail_label">Open Time</label>
                                        <div class="detail_value open_time"></div>
                                    </div>

                                    <div class="detail_row">
                                        <label class="detail_label">Categories</label>
                                        <div class="detail_value categories"></div>
                                    </div>

                                    <div class="detail_row">
                                        <label class="detail_label">Store Location</label>
                                        <div class="detail_value store_location"></div>
                                    </div>

                                </div>

                            </div>

                        </div>

                        <div class="col-lg-6">

                            <div class="similar_items items_container hidden">
                                <div class="form_head">Similar Items</div>
                                <div class="form_content clearfix"></div>
                            </div>

                        </div>

                    </div>

                </div>
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
                    <button type="button" id="apply_preview" class="btn btn-primary">Crop Image</button>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
