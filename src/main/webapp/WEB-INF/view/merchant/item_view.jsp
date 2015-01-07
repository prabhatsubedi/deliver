<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Item Detail</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="/resources/js/jquery-ui.js"></script>

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

                                <div class="form_head">Item Information
                                    <div class="pull-right">
                                        <div class="switch_container pull-left">
                                            <div class="switch switch_activation">
                                                <div class="btn_switch on"></div>
                                            </div>
                                        </div>
                                        <a class="btn btn_green pull-left btn_edit" href="/merchant/item/form/edit/">Edit</a>
                                    </div>
                                </div>
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

                                    <div class="other_info">
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
                                            <div class="detail_value available_stores"><div class="brand_name"></div><ul class="brand_stores"></ul></div>
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
                                                    <thead>
                                                        <tr>
                                                            <th colspan="3">Attributes</th>
                                                            <th rowspan="2">Price<br/>(Rs.)</th>
                                                        </tr>
                                                        <tr>
                                                            <th>Type</th>
                                                            <th>Selection</th>
                                                            <th>Name</th>
                                                        </tr>
                                                    </thead>
                                                </table>
                                            </div>
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

                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="item_container_template hidden">
    <div class="item_container col-lg-6">
        <div class="block_item">
            <div class="item_image">
                <img class="img-responsive">
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
