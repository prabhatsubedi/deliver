<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Settings</title>

    <%@include file="../includes/head.jsp" %>


</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
<%--        <div class="heading clearfix">
            <h1 class="pull-left">Settings</h1>
        </div>--%>
        <div class="settings_menu">
            <ul class="nav nav-pills">
                <li class="col-xs-4"><a href="/admin/settings">General</a></li>
                <li class="col-xs-4 current_page"><a href="/admin/algorithm">Algorithm</a></li>
                <li class="col-xs-4"><a href="/admin/view_category">Categories</a></li>
            </ul>
        </div>
        <div class="main_content">

            <div class="form_container form_editable form_section full_width">
                <form id="form_settings" action="" method="POST" role="form">
                    <div class="form_head">Algorithm Selection</div>
                    <div class="form_content">
                        <div class="row">
                            <div class="form-group clearfix">
                                <label for="selected_algorithm" class="col-lg-4 floated_label">Algorithm Selected</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_selected_algorithm none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="selected_algorithm" id="selected_algorithm" class="form-control">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group clearfix">
                                <label for="air_distance_mf" class="col-lg-4 floated_label">Air distance multiplying factor</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_air_distance_mf none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="air_distance_mf" id="air_distance_mf" class="form-control">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="form_head">Time, Distance and Charge setting</div>
                    <div class="form_content">
                        <div class="row">
                            <div class="form-group clearfix">
                                <label for="distance_charge_cs" class="col-lg-4 floated_label">Default Charged Distance from customer to Store</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_distance_charge_cs none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="distance_charge_cs" id="distance_charge_cs" class="form-control">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group clearfix">
                                <label for="distance_charge_unpaid" class="col-lg-4 floated_label">Additional Unpaid KM for Shopper</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_distance_charge_unpaid none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="distance_charge_unpaid" id="distance_charge_unpaid" class="form-control">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group clearfix">
                                <label for="distance_charge_additional" class="col-lg-4 floated_label">Per KM charge for Additional KM given to Shopper</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_distance_charge_additional none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="distance_charge_additional" id="distance_charge_additional" class="form-control">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group clearfix">
                                <label for="distance_charge_upto" class="col-lg-4 floated_label">Per KM Charge up to or equal to [B$1] KM</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_distance_charge_upto none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="distance_charge_upto" id="distance_charge_upto" class="form-control">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group clearfix">
                                <label for="distance_charge_above" class="col-lg-4 floated_label">Per KM Charge above default distance KM</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_distance_charge_above none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="distance_charge_above" id="distance_charge_above" class="form-control">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group clearfix">
                                <label for="surge_factor" class="col-lg-4 floated_label">Surge Factor</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_surge_factor none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="surge_factor" id="surge_factor" class="form-control">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group clearfix">
                                <label for="time_taken" class="col-lg-4 floated_label">Time taken to travel per KM in min</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_time_taken none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="time_taken" id="time_taken" class="form-control">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group clearfix">
                                <label for="time_store" class="col-lg-4 floated_label">Time at store in min</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_time_store none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="time_store" id="time_store" class="form-control">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group clearfix">
                                <label for="order_time_deviation" class="col-lg-4 floated_label">Order Acceptance Deviation Time in min</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_order_time_deviation none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="order_time_deviation" id="order_time_deviation" class="form-control">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="form_head">Profit and Commission Setting</div>
                    <div class="form_content">
                        <div class="row">
                            <div class="form-group clearfix">
                                <label for="commission_reserved" class="col-lg-4 floated_label">Reserved Commission Percentage by system</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_commission_reserved none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="commission_reserved" id="commission_reserved" class="form-control">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group clearfix">
                                <label for="commission_courier" class="col-lg-4 floated_label">Shopper Commission %</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_commission_courier none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="commission_courier" id="commission_courier" class="form-control">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group clearfix">
                                <label for="min_amount_courier" class="col-lg-4 floated_label">Minimum Amount Given to Shopper</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_min_amount_courier none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="min_amount_courier" id="min_amount_courier" class="form-control">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group clearfix">
                                <label for="service_vat" class="col-lg-4 floated_label">Delivery Fee and Service fee VAT</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_service_vat none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="service_vat" id="service_vat" class="form-control">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group clearfix">
                                <label for="min_profit" class="col-lg-4 floated_label">Minimum Profit Percentage for Order Acceptance</label>
                                <div class="col-lg-8">
                                    <div class="form-control info_display val_min_profit none_editable"></div>
                                    <div class="info_edit editable hidden">
                                        <input type="text" name="min_profit" id="min_profit" class="form-control">
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

<script type="text/javascript">
    $('.current_page').click(function(e){
        e.preventDefault();
    });
</script>

</body>
</html>
