<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Item Detail</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="/resources/js/html2canvas.js"></script>
    <script type="text/javascript" src="/resources/js/jquery.Jcrop.js"></script>
    <script type="text/javascript" src="/resources/js/image.js"></script>

    <link rel="stylesheet" href="/resources/css/jquery.Jcrop.css" type="text/css" />
    <link rel="stylesheet" href="/resources/css/jcrop.css" type="text/css" />

    <script type="text/javascript" src="/resources/js/item.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            var itemId = Main.getURLvalue(3);
            Item.loadItem(itemId);
        });
    </script>


</head>
<body class="page_item_view">

<%@include file="../includes/sidebar_merchant.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Item Detail</h1>
        </div>
        <div class="main_content">
            <div class="form_container full_width clearfix">
                <div class="row">
                    <form role="form" id="form_item" method="POST" action="">
                        <div class="form_section clearfix">
                            <div class="col-lg-6">

                                <div class="form_head">Item Information</div>
                                <div class="form_content item_info">
                                    <div class="product_images form-group clearfix">
                                        <div class="product_image col-lg-4">
                                            <div class="drop_zone unselectable text-center maintain_ratio" mr-width="400" mr-height="400">
                                                <div class="drop_info">no image available</div>
                                            </div>
                                        </div>
                                        <div class="product_image col-lg-4">
                                            <div class="drop_zone unselectable text-center maintain_ratio" mr-width="400" mr-height="400">
                                                <div class="drop_info">no image available</div>
                                            </div>
                                        </div>
                                        <div class="product_image col-lg-4">
                                            <div class="drop_zone unselectable text-center maintain_ratio" mr-width="400" mr-height="400">
                                                <div class="drop_info">no image available</div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="detail_row">
                                        <label class="detail_label">Item Name</label>
                                        <div class="detail_value item_name"></div>
                                    </div>

                                    <div class="detail_row">
                                        <label class="detail_label">Description</label>
                                        <div class="detail_value description"></div>
                                    </div>

                                    <div class="detail_row">
                                        <div class="detail_label">Additional Offer</div>
                                        <div class="detail_value additional_offer"></div>
                                    </div>

                                    <div class="detail_row">
                                        <label class="detail_label">Available Stores </label>
                                        <div class="detail_value"><div class="brand_name"></div><ul class="brand_stores"></ul></div>
                                    </div>

                                    <div class="detail_row">
                                        <label class="detail_label">Category </label>
                                        <div class="detail_value"><ul class="item_category"></ul></div>
                                    </div>

                                    <div class="detail_row">
                                        <label class="detail_label">Additional Information </label>
                                        <div class="detail_value">
                                            <div class="clearfix">
                                                <div class="sub_label">Available Time</div>
                                                <div class="sub_value available_time"></div>
                                            </div>
                                            <div class="clearfix">
                                                <div class="sub_label">Order Quantity</div>
                                                <div class="sub_value order_quantity"></div>
                                            </div>
                                            <div class="clearfix">
                                                <div class="sub_label">VAT</div>
                                                <div class="sub_value vat"></div>
                                            </div>
                                            <div class="clearfix">
                                                <div class="sub_label">Service Charge</div>
                                                <div class="sub_value service_charge"></div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="detail_row">
                                        <label class="detail_label">Pricing and Attributes </label>
                                        <div class="detail_value pricing_attributes">
                                            <table>
                                                <thead>
                                                    <tr>
                                                        <th colspan="2">Attributes</th>
                                                        <th rowspan="2">Price</th>
                                                    </tr>
                                                    <tr>
                                                        <th>Type</th>
                                                        <th>Name</th>
                                                    </tr>
                                                </thead>
                                                <tbody></tbody>
                                            </table>
                                        </div>
                                    </div>

                                </div>

                            </div>

                            <div class="col-lg-6">

                                <div class="form_head">Similar Items</div>
                                <div class="form_content">
                                    <div class="row">
                                        <div class="form-group clearfix">
                                            <label class="col-lg-4 floated_label" for="available_start_time">Available Time</label>
                                            <div class="col-lg-8">
                                                <div class="row">
                                                    <div class="col-lg-6">
                                                        <select id="available_start_time" name="available_start_time" class="col-xs-12 no_pad no_margin" data-style="form-control">
                                                            <option value="none">Select Opening Time</option>
                                                        </select>
                                                    </div>
                                                    <div class="col-lg-6">
                                                        <select id="available_end_time" name="available_end_time" class="col-xs-12 no_pad no_margin" data-style="form-control">
                                                            <option value="none">Select Closing Time</option>
                                                        </select>
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
                                        <div class="form-group clearfix hidden">
                                            <label class="col-lg-4 floated_label" for="return_policy">Return Policy</label>
                                            <div class="col-lg-8">
                                                <input type="text" class="form-control" id="return_policy" name="return_policy" value="N/A">
                                            </div>
                                        </div>
                                        <div class="form-group clearfix hidden">
                                            <label class="col-lg-4 floated_label" for="delivery_fee">Delivery Fee</label>
                                            <div class="col-lg-8">
                                                <input type="text" class="form-control" id="delivery_fee" name="delivery_fee" value="0">
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

                            </div>

                        </div>

                    </form>
                </div>
            </div>
        </div>
    </div>
</div>


<div class="select_category_template hidden">
    <div class="form-group">
        <select class="col-xs-12 no_pad no_margin" data-style="form-control">
            <option value="none">Select Category</option>
        </select>
    </div>
</div>

<div class="item_store_template hidden">
    <div class="item_store table_div">
        <div class="name_store td_div"> </div>
        <div class="check_store td_div"> <label class="glyphicon glyphicon-ok check_label"><input type="checkbox" class="checkbox" /></label> </div>
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
            <div class="form-group clearfix attr_li">
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
    <div class="form-group clearfix attr_li">
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
