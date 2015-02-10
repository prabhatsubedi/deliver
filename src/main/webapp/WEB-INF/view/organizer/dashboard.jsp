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
                                <span>9</span>
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
                                <span>10</span>
                                <div class="show_more"></div>
                            </div>
                            <div class="count_title">Current Delivery</div>
                        </div>
                        <div class="more_data_cont">
                            <div class="more_data">
                                <div class="data_row"><span class="badge">5</span>In Route</div>
                                <div class="data_row"><span class="badge">2</span>At Store</div>
                                <div class="data_row"><span class="badge">3</span>Order Picked</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-3">
                    <div class="count_block completed_today">
                        <div class="count_head unselectable">
                            <div class="count_number">
                                <span>20</span>
                                <div class="show_more"></div>
                            </div>
                            <div class="count_title">Completed Today</div>
                        </div>
                        <div class="more_data_cont">
                            <div class="more_data">
                                <div class="data_row"><span class="badge">30ms</span>Average Delivery time</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-3">
                    <div class="count_block served_till">
                        <div class="count_head unselectable">
                            <div class="count_number">
                                <span>223</span>
                                <div class="show_more"></div>
                            </div>
                            <div class="count_title">Served Till Date</div>
                        </div>
                        <div class="more_data_cont">
                            <div class="more_data">
                                <div class="data_row"><span class="badge">30ms</span>Average Delivery time</div>
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
                <div id="clear_markers" class="pull-left">Clear All Markers</div>
            </div>
            <div id="map-canvas"></div>
            <div class="block_counts col-sm-12">
                <div class="col-sm-3 count_block_cont">
                    <div class="count_cont table_div">
                        <span class="count_span td_div">08</span> <span class="count_label td_div">On Duty Delivery Boy</span>
                    </div>
                </div>
                <div class="col-sm-3 count_block_cont">
                    <div class="count_cont table_div">
                        <span class="count_span td_div">05</span> <span class="count_label td_div">Off Duty Delivery Boy</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="block_counts col-sm-12">
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="count_span td_div">88%</span> <span class="count_label td_div">On Time Delivery</span>
                </div>
            </div>
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="count_span td_div">99%</span> <span class="count_label td_div">Successful Delivery</span>
                </div>
            </div>
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="count_span td_div">82k</span> <span class="count_label td_div">Partner Stores</span>
                </div>
            </div>
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="count_span td_div">24k</span> <span class="count_label td_div">Total User Registered</span>
                </div>
            </div>
        </div>

        <div class="block_counts col-sm-12">
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="count_span td_div">10%</span> <span class="count_label td_div">Exceed On Time</span>
                </div>
            </div>
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="count_span td_div">02%</span> <span class="count_label td_div">Delivery Fails</span>
                </div>
            </div>
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="count_span td_div">02</span> <span class="count_label td_div">Total User Left App</span>
                </div>
            </div>
            <div class="col-sm-3 count_block_cont">
                <div class="count_cont table_div">
                    <span class="count_span td_div">25</span> <span class="count_label td_div">New User Registered</span>
                </div>
            </div>
        </div>

    </div>
</div>

</body>
</html>
