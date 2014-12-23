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

                            <div class="form_head">Available Stores</div>
                            <div class="form_content">
                                <div class="form-group">
                                    <select id="item_brand" name="item_brand" class="col-xs-12 no_pad no_margin" data-style="form-control">
                                        <option value="none">Select Store</option>
                                        <option value="brand1">brand1</option>
                                        <option value="brand2">brand2</option>
                                    </select>
                                </div>
                                <div id="item_store_container" class="form-group">
                                    <div class="item_store table_div">
                                        <div class="image_store td_div"> <img src="/resources/images/delivr-logo.png" /> </div>
                                        <div class="name_store td_div"> Store Address </div>
                                        <div class="check_store td_div"> <label class="glyphicon glyphicon-ok check_label"><input type="checkbox" class="checkbox" /></label> </div>
                                    </div>
                                    <div class="item_store table_div">
                                        <div class="image_store td_div"> <img src="/resources/images/delivr-logo.png" /> </div>
                                        <div class="name_store td_div"> Store Address </div>
                                        <div class="check_store td_div"> <label class="glyphicon glyphicon-ok check_label"><input type="checkbox" class="checkbox" /></label> </div>
                                    </div>
                                    <div class="item_store table_div">
                                        <div class="image_store td_div"> <img src="/resources/images/delivr-logo.png" /> </div>
                                        <div class="name_store td_div"> Store Address </div>
                                        <div class="check_store td_div"> <label class="glyphicon glyphicon-ok check_label"><input type="checkbox" class="checkbox" /></label> </div>
                                    </div>
                                </div>
                            </div>
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

                            <div class="form_head">Basic Info</div>
                            <div class="form_content">
                                <div class="row">
                                    <div class="form-group clearfix">
                                        <label class="col-lg-4 floated_label" for="name_item">Item Name</label>
                                        <div class="col-lg-8">
                                            <input type="text" class="form-control" id="name_item" name="name_item">
                                        </div>
                                    </div>
                                    <div class="form-group clearfix">
                                        <label class="col-lg-4 floated_label" for="description">Description</label>
                                        <div class="col-lg-8">
                                            <input type="text" class="form-control" id="description" name="description">
                                        </div>
                                    </div>
                                    <div class="form-group clearfix">
                                        <label class="col-lg-4 floated_label" for="item_size">Size</label>
                                        <div class="col-lg-8">
                                            <input type="text" class="form-control" id="item_size" name="item_size">
                                        </div>
                                    </div>
                                    <div class="form-group clearfix">
                                        <label class="col-lg-4 floated_label" for="item_weight">Weight</label>
                                        <div class="col-lg-8">
                                            <input type="text" class="form-control" id="item_weight" name="item_weight">
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>

                        <div class="col-lg-6">

                            <div class="form_head">Additional Info</div>
                            <div class="form_content">
                                <div class="row">
                                    <div class="form-group clearfix">
                                        <label class="col-lg-4 floated_label" for="available_start_time">Available Time</label>
                                        <div class="col-lg-8">
                                            <div class="row">
                                                <div class="col-lg-6">
                                                    <input type="text" class="form-control" id="available_start_time" name="available_start_time" placeholder="Start Time">
                                                </div>
                                                <div class="col-lg-6">
                                                    <input type="text" class="form-control" id="available_end_time" name="available_end_time" placeholder="End Time">
                                                </div>
                                            </div>
                                        </div>
                                    </div>
<%--                                    <div class="form-group clearfix">
                                        <label class="col-lg-4 floated_label">Available Quantity</label>
                                        <div class="col-lg-8">
                                            <input type="text" class="form-control" id="quantity" name="quantity">
                                        </div>
                                    </div>--%>
                                    <div class="form-group clearfix">
                                        <label class="col-lg-4 floated_label" for="min_order">Order Quantity</label>
                                        <div class="col-lg-8">
                                            <div class="row">
                                                <div class="col-lg-6">
                                                    <input type="text" class="form-control" id="min_order" name="min_order" placeholder="Min.">
                                                </div>
                                                <div class="col-lg-6">
                                                    <input type="text" class="form-control" id="max_order" name="max_order" placeholder="Max.">
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="form-group clearfix">
                                        <label class="col-lg-4 floated_label" for="additional_offer">Additional Offer</label>
                                        <div class="col-lg-8">
                                            <input type="text" class="form-control" id="additional_offer" name="additional_offer" value="N/A">
                                        </div>
                                    </div>
                                    <div class="form-group clearfix">
                                        <label class="col-lg-4 floated_label" for="return_policy">Return Policy</label>
                                        <div class="col-lg-8">
                                            <input type="text" class="form-control" id="return_policy" name="return_policy" value="N/A">
                                        </div>
                                    </div>
                                    <div class="form-group clearfix">
                                        <label class="col-lg-4 floated_label" for="delivery_fee">Delivery Fee</label>
                                        <div class="col-lg-8">
                                            <input type="text" class="form-control" id="delivery_fee" name="delivery_fee" value="N/A">
                                        </div>
                                    </div>
                                    <div class="form-group clearfix">
                                        <label class="col-lg-4 floated_label" for="vat">VAT</label>
                                        <div class="col-lg-8">
                                            <input type="text" class="form-control" id="vat" name="vat" value="0">
                                        </div>
                                    </div>
                                    <div class="form-group clearfix">
                                        <label class="col-lg-4 floated_label" for="service_charge">Service Charge</label>
                                        <div class="col-lg-8">
                                            <input type="text" class="form-control" id="service_charge" name="service_charge" value="0">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="form_head">Pricing & Attributes</div>
                            <div class="form_content">
                                <div class="form-group clearfix row">
                                    <label class="col-lg-4 floated_label" for="price">Price</label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" id="price" name="price">
                                    </div>
                                </div>
                                <input type="text" name="check_attr" id="check_attr" class="hidden">
                                <div class="item_attributes"></div>
                                <div class="form-group">
                                    <button type="button" id="add_attr_type" class="btn btn_green">Add Attribute Type</button>
                                </div>
                            </div>

                        </div>

                    </div>

                </div>
            </div>
        </div>
    </div>
</div>


<div class="block_attr_template hidden">
    <div class="block_attr form-group">
        <div class="attr_head form-group clearfix">
            <div class="row">
                <div class="col-lg-6">
                    <input type="text" class="form-control" name="attr_type" placeholder="Atribute Title">
                </div>
                <div class="col-lg-6">
                    <label for="multi_selection">Multi-selectable</label>
                    <label class="glyphicon glyphicon-ok check_label"><input type="checkbox" id="multi_selection" class="checkbox"></label>
                    <button type="button" class="btn btn_green btn_red remove_attr_block glyphicon glyphicon-remove"></button>
                </div>
            </div>
        </div>
        <div class="attr_list">
            <div class="form-group clearfix">
                <div class="col-lg-6">
                    <input type="text" class="form-control" name="attr_label" placeholder="Attribute Name">
                </div>
                <div class="col-lg-6 with_remove">
                    <div class="price_container">
                        <input type="text" class="form-control" name="attr_val" placeholder="Price">
                    </div>
                    <button type="button" class="btn btn_green btn_red remove_attr glyphicon glyphicon-remove"></button>
                </div>
            </div>
        </div>
        <div class="form-group clearfix">
            <div class="col-lg-12">
                <button type="button" class="btn btn_green add_attr">Add Attribute</button>
            </div>
        </div>
    </div>
</div>

<div class="attr_list_template hidden">
    <div class="form-group clearfix">
        <div class="col-lg-6">
            <input type="text" class="form-control" name="attr_label" placeholder="Attribute Name">
        </div>
        <div class="col-lg-6 with_remove">
            <div class="price_container">
                <input type="text" class="form-control" name="attr_val" placeholder="Price">
            </div>
            <button type="button" class="btn btn_green btn_red remove_attr glyphicon glyphicon-remove"></button>
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
