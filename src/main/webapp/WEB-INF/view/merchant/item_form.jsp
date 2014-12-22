<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Add Item</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="/resources/js/jquery.validate.js"></script>
    <script type="text/javascript" src="/resources/js/html2canvas.js"></script>
    <script type="text/javascript" src="/resources/js/jquery.Jcrop.js"></script>
    <script type="text/javascript" src="/resources/js/image.js"></script>

    <link rel="stylesheet" href="/resources/css/jquery.Jcrop.css" type="text/css" />
    <link rel="stylesheet" href="/resources/css/jcrop.css" type="text/css" />

    <script type="text/javascript" src="/resources/js/item.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            Item.loadAddItem();
        });
    </script>


</head>
<body>

<%@include file="../includes/sidebar_merchant.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Add Item</h1>
        </div>
        <div class="main_content">
            <div class="form_container full_width clearfix">
                <div class="row">
                    <div class="form_section">
                        <div class="col-lg-6">
                            <div class="form_head">Category</div>
                            <div class="form_content">
                                <div class="form-group">
                                    <select id="item_category" name="item_category" class="col-xs-12 no_pad no_margin" data-style="form-control">
                                        <option value="none">Select Item Category</option>
                                        <option value="cat1">cat1</option>
                                        <option value="cat2">cat2</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form_head">Stores</div>
                            <div class="form_content">
                                <div class="form-group">
                                    <select id="item_brand" name="item_brand" class="col-xs-12 no_pad no_margin" data-style="form-control">
                                        <option value="none">Select Store Brand</option>
                                        <option value="brand1">brand1</option>
                                        <option value="brand2">brand2</option>
                                    </select>
                                </div>
                                <div id="item_store_container" class="form-group">
                                    <div class="item_store table_div">
                                        <div class="image_store td_div"> <img src="/resources/images/delivr-logo.png" /> </div>
                                        <div class="name_store td_div"> Store Address </div>
                                        <div class="check_store td_div"> <label class="glyphicon glyphicon-ok"><input type="checkbox" class="checkbox" /></label> </div>
                                    </div>
                                    <div class="item_store table_div">
                                        <div class="image_store td_div"> <img src="/resources/images/delivr-logo.png" /> </div>
                                        <div class="name_store td_div"> Store Address </div>
                                        <div class="check_store td_div"> <label class="glyphicon glyphicon-ok"><input type="checkbox" class="checkbox" /></label> </div>
                                    </div>
                                    <div class="item_store table_div">
                                        <div class="image_store td_div"> <img src="/resources/images/delivr-logo.png" /> </div>
                                        <div class="name_store td_div"> Store Address </div>
                                        <div class="check_store td_div"> <label class="glyphicon glyphicon-ok"><input type="checkbox" class="checkbox" /></label> </div>
                                    </div>
                                </div>
                            </div>
                            <div class="form_head">Basic Info</div>
                            <div class="form_content">
                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label">Item Name</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="item_name" name="item_name">
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label">Description</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="description" name="description">
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label">Size</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="item_size" name="item_size">
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label">Weight</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="item_weight" name="item_weight">
                                    </div>
                                </div>
                            </div>
                            <div class="form_head">Images</div>
                            <div class="form_content">
                                <div class="product_images form-group clearfix">
                                    <div class="product_image col-lg-4">
                                        <div id="product_image1" class="drop_zone unselectable text-center maintain_ratio" mr-width="720" mr-height="400">
                                            <div class="drop_info">Drop image file <br /> (or click to browse)</div>
                                        </div>
                                        <input type="file" onchange="Image.readURL(this)" data-dimension="720x400" id="product_image1_input" name="product_image1_input" class="hidden" />
                                    </div>
                                    <div class="product_image col-lg-4">
                                        <div id="product_image2" class="drop_zone unselectable text-center maintain_ratio" mr-width="720" mr-height="400">
                                            <div class="drop_info">Drop image file <br /> (or click to browse)</div>
                                        </div>
                                        <input type="file" onchange="Image.readURL(this)" data-dimension="720x400" id="product_image2_input" name="product_image2_input" class="hidden" />
                                    </div>
                                    <div class="product_image col-lg-4">
                                        <div id="product_image3" class="drop_zone unselectable text-center maintain_ratio" mr-width="720" mr-height="400">
                                            <div class="drop_info">Drop image file <br /> (or click to browse)</div>
                                        </div>
                                        <input type="file" onchange="Image.readURL(this)" data-dimension="720x400" id="product_image3_input" name="product_image3_input" class="hidden" />
                                    </div>
                                </div>
                            </div>
                            <div class="form_head">Additional Info</div>
                            <div class="form_content">
                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label">Available Start Time</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="available_start_time" name="available_start_time">
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label">Available End Time</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="available_end_time" name="available_end_time">
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label">Available Quantity</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="quantity" name="quantity">
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label">Max Order Quantity</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="min_order" name="min_order">
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label">Min Order Quantity</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="max_order" name="max_order">
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label">Additional Offer</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="additional_offer" name="additional_offer">
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label">Return Policy</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="return_policy" name="return_policy">
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label">Delivery Fee</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="delivery_fee" name="delivery_fee">
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label">Promo Code</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="promo_code" name="promo_code">
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label">VAT</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="vat" name="vat">
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label class="col-lg-4 floated_label">Service Charge</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="service_charge" name="service_charge">
                                    </div>
                                </div>
                            </div>
                            <div class="form_head">Attributes</div>
                            <div class="form_content">
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6">

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
