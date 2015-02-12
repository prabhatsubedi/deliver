<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/map.js"></script>
    <script src="https://maps.googleapis.com/maps/api/js?v=3&key=AIzaSyDY0kkJiTPVd2U7aTOAwhc9ySH6oHxOIYM&sensor=false&libraries=places" type="text/javascript"></script>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/maps.css" type="text/css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/manager.js"></script>

    <script type="text/javascript">
        var merchantRole = false;
        $(document).ready(function(){
            Manager.loadDashboard();
        });
    </script>


</head>
<body class="god_view">

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="clearfix">
            <div class="count_section col-sm-12">

                <div class="col-sm-3">
                    <div class="count_block new_orders">
                        <div class="count_head unselectable">
                            <div class="count_number">
                                <span class="count_span" data-get="newOrders">0</span>
                                <div class="show_more"></div>
                            </div>
                            <div class="count_title">New Orders</div>
                        </div>
                        <div class="more_data_cont">
                            <div class="more_data">
                                <div class="data_row">Baneshwor</div>
                                <div class="data_row">New Road</div>
                                <div class="data_row">Naxal</div>
                                <div class="data_row">Baneshwor</div>
                                <div class="data_row">New Road</div>
                                <div class="data_row">Naxal</div>
                                <div class="data_row">Baneshwor</div>
                                <div class="data_row">New Road</div>
                                <div class="data_row">Naxal</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-3">
                    <div class="count_block current_delivery">
                        <div class="count_head unselectable">
                            <div class="count_number">
                                <span class="count_span" data-get="currentDelivery">0</span>
                                <div class="show_more"></div>
                            </div>
                            <div class="count_title">Current Delivery</div>
                        </div>
                        <div class="more_data_cont">
                            <div class="more_data">
                                <div class="data_row"><span class="badge count_inRouteToDelivery">0</span>In Route to Delivery</div>
                                <div class="data_row"><span class="badge count_inRouteToPickUp">0</span>In Route to Pickup</div>
                                <div class="data_row"><span class="badge count_atStore">0</span>At Store</div>
                                <div class="data_row"><span class="badge count_orderAccepted">0</span>Order Picked</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-3">
                    <div class="count_block completed_today">
                        <div class="count_head unselectable">
                            <div class="count_number">
                                <span class="count_span" data-get="completedToday">0</span>
                                <div class="show_more"></div>
                            </div>
                            <div class="count_title">Completed Today</div>
                        </div>
                        <div class="more_data_cont">
                            <div class="more_data">
                                <div class="data_row"><span class="badge"><span class="count_ct_averageDeliveryTime">0</span>ms</span>Average Delivery time</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-3">
                    <div class="count_block served_till">
                        <div class="count_head unselectable">
                            <div class="count_number">
                                <span class="count_span" data-get="servedTillDate">0</span>
                                <div class="show_more"></div>
                            </div>
                            <div class="count_title">Served Till Date</div>
                        </div>
                        <div class="more_data_cont">
                            <div class="more_data">
                                <div class="data_row"><span class="badge"><span class="count_std_averageDeliveryTime">0</span>ms</span>Average Delivery time</div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <div class="map-container">
<%--            <div id="search_box">
                <input id="pac-input" class="controls" type="text" placeholder="Enter Postal/Zipcode to focus map & place marker. Click on marker to add address.">
            </div>--%>
            <div id="custom_map_controls" class="clearfix">
                <div id="scroll_zoom" class="pull-left">Enable Scroll Zoom</div>
                <%--<div id="clear_markers" class="pull-left">Clear All Markers</div>--%>
            </div>
            <div id="map-canvas"></div>
            <div class="toggle_map_view glyphicon glyphicon-resize-full"></div>
            <div class="block_counts col-sm-12">
                <div class="col-sm-3 count_block_cont">
                    <div class="count_cont table_div">
                        <span class="count_span td_div" data-get="onDutyDeliveryBoy">0</span> <span class="count_label td_div">On Duty Delivery Boy</span>
                    </div>
                </div>
                <div class="col-sm-3 count_block_cont">
                    <div class="count_cont table_div">
                        <span class="count_span td_div count_red" data-get="offDutyDeliveryBoy">0</span> <span class="count_label td_div">Off Duty Delivery Boy</span>
                    </div>
                </div>
            </div>
            <div class="top_blur"></div>
            <div class="bottom_blur"></div>
        </div>

        <div class="block_counts col-sm-12">
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="td_div"><span class="count_span" data-get="onTimeDelivery">0</span>%</span> <span class="count_label td_div">On Time Delivery</span>
                </div>
            </div>
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="td_div"><span class="count_span" data-get="successfulDelivery">0</span>%</span> <span class="count_label td_div">Successful Delivery</span>
                </div>
            </div>
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="count_span td_div" data-get="partnerStores">0</span> <span class="count_label td_div">Partner Stores</span>
                </div>
            </div>
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="count_span td_div" data-get="totalUserRegistered">0</span> <span class="count_label td_div">Total User Registered</span>
                </div>
            </div>
        </div>

        <div class="block_counts col-sm-12">
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="td_div count_red"><span class="count_span" data-get="timeExceededDelivery">0</span>%</span> <span class="count_label td_div">Exceed On Time</span>
                </div>
            </div>
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="td_div count_red"><span class="count_span" data-get="deliveryFailed">0</span>%</span> <span class="count_label td_div">Delivery Fails</span>
                </div>
            </div>
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="count_span td_div" data-get="totalActiveUser">0</span> <span class="count_label td_div">Total Active Users</span>
                </div>
            </div>
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="count_span td_div" data-get="newUserRegistered">0</span> <span class="count_label td_div">New User Registered</span>
                </div>
            </div>
        </div>

    </div>
</div>



<div class="infowindow_template hidden">
    <div class="infowindow">
        <p><strong class="name_line"></strong></p>
        <p class="address_line"></p>
    </div>
</div>

</body>
</html>
