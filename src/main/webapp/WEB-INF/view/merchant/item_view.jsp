<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Item Name</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-ui.js"></script>

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/item.js"></script>
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
            <h1 class="pull-left">Item Category</h1>
            <a class="btn btn_green pull-right" href="/merchant/item/form/create/">Add Item</a>
        </div>
        <div class="main_content">
            <div class="form_container full_width clearfix">
                <div class="row">
                    <div class="form_section item_info clearfix">
                        <div class="col-lg-6">

                            <div class="form_head clearfix">
                                <div class="item_name pull-left">Item Name</div>
                                <div class="item_price pull-right">Rs. <span></span></div>
                            </div>
                            <div class="form_content">
                                <div class="product_images form-group clearfix">
                                    <div class="product_image col-sm-4">
                                        <div class="drop_zone unselectable text-center maintain_ratio" mr-width="400" mr-height="400">
                                            <div class="drop_info">no image available</div>
                                        </div>
                                    </div>
                                    <div class="product_image col-sm-4">
                                        <div class="drop_zone unselectable text-center maintain_ratio" mr-width="400" mr-height="400">
                                            <div class="drop_info">no image available</div>
                                        </div>
                                    </div>
                                    <div class="product_image col-sm-4">
                                        <div class="drop_zone unselectable text-center maintain_ratio" mr-width="400" mr-height="400">
                                            <div class="drop_info">no image available</div>
                                        </div>
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
                                        <label class="detail_label">Description</label>
                                        <div class="detail_value description"></div>
                                    </div>

                                    <div class="detail_row">
                                        <label class="detail_label">Additional Offer</label>
                                        <div class="detail_value additional_offer"></div>
                                    </div>

                                    <div class="detail_row">
                                        <label class="detail_label">Available Stores </label>
                                        <div class="detail_value available_stores"><div class="brand_name"></div><ul class="detail_list"></ul></div>
                                    </div>

                                    <div class="detail_row">
                                        <label class="detail_label">Category </label>
                                        <div class="detail_value"><ul class="item_category breadcrumb"></ul></div>
                                    </div>

                                    <div class="detail_row">
                                        <label class="detail_label">Additional Information </label>
                                        <div class="detail_value additional_information">
                                            <div class="clearfix">
                                                <div class="sub_label pull-left">Available Time</div>
                                                <div class="sub_value pull-left available_time"></div>
                                            </div>
                                            <div class="clearfix">
                                                <div class="sub_label pull-left">Order Quantity</div>
                                                <div class="sub_value pull-left order_quantity"></div>
                                            </div>
                                            <div class="clearfix">
                                                <div class="sub_label pull-left">VAT</div>
                                                <div class="sub_value pull-left vat"></div>
                                            </div>
                                            <div class="clearfix">
                                                <div class="sub_label pull-left">Service Charge</div>
                                                <div class="sub_value pull-left service_charge"></div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="detail_row">
                                        <label class="detail_label">Pricing and Attributes </label>
                                        <div class="detail_value pricing_attributes">
                                            <table>
                                            </table>
                                        </div>
                                    </div>
                                </div>

                            </div>

                        </div>

                        <div class="col-lg-6">

                            <div class="other_contents items_container hidden">
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

<div class="item_container_template hidden">
    <div class="item_container col-lg-6 col-sm-4 col-xs-6 invisible">
        <div class="block_item">
            <div class="item_image maintain_ratio" mr-height="400" mr-width="400">
                <img class="img-responsive no_image">
            </div>
            <div class="item_infos">
                <p class="item_name"><a href="#"></a></p>
                <p class="item_price">Rs. <span></span></p>
            </div>
        </div>
    </div>
</div>

</body>
</html>
