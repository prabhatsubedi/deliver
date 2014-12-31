<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Items</title>

    <%@include file="../includes/head.jsp" %>


</head>
<body>

<%@include file="../includes/sidebar_merchant.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Items</h1>
            <a class="btn btn_green pull-right" href="/merchant/item/form/create/">Add Item</a>
        </div>
        <div class="main_content">
            <div class="item_listing">
                <div class="table_div">
                    <div class="categories_container td_div full_height">
                        <ul class="nav nav-stacked">
                            <li><a href="#">Electronics & Computers</a>
                                <ul class="nav nav-stacked">
                                    <li><a href="#">DSLR Cameras</a>
                                        <ul class="nav nav-stacked">
                                            <li><a href="#">Nikon Brands</a></li>
                                        </ul>
                                    </li>
                                </ul>
                            </li>
                            <li><a href="#">Home, garden & Tools</a></li>
                            <li><a href="#">Beauty, Health</a></li>
                            <li><a href="#">Grocery</a></li>
                            <li><a href="#">Toys, Kids & Baby</a></li>
                            <li><a href="#">Clothing, Shoes</a></li>
                        </ul>
                    </div>
                    <div class="items_container td_div">

                        <div class="item_container col-lg-3">
                            <div class="block_item">
                                <div class="item_image">
                                    <img src="/resources/images/720x400.jpg" class="img-responsive">
                                </div>
                                <div class="item_infos">
                                    <p class="item_name">Item Name</p>
                                    <p class="item_price">Rs. <span>86987</span></p>
                                </div>
                            </div>
                        </div>

                        <div class="item_container col-lg-3">
                            <div class="block_item">
                                <div class="item_image">
                                    <img src="/resources/images/720x400.jpg" class="img-responsive">
                                </div>
                                <div class="item_infos">
                                    <p class="item_name">Item Name</p>
                                    <p class="item_price">Rs. <span>86987</span></p>
                                </div>
                            </div>
                        </div>

                        <div class="item_container col-lg-3">
                            <div class="block_item">
                                <div class="item_image">
                                    <img src="/resources/images/720x400.jpg" class="img-responsive">
                                </div>
                                <div class="item_infos">
                                    <p class="item_name">Item Name</p>
                                    <p class="item_price">Rs. <span>86987</span></p>
                                </div>
                            </div>
                        </div>

                        <div class="item_container col-lg-3">
                            <div class="block_item">
                                <div class="item_image">
                                    <img src="/resources/images/720x400.jpg" class="img-responsive">
                                </div>
                                <div class="item_infos">
                                    <p class="item_name">Item Name</p>
                                    <p class="item_price">Rs. <span>86987</span></p>
                                </div>
                            </div>
                        </div>

                        <div class="item_container col-lg-3">
                            <div class="block_item">
                                <div class="item_image">
                                    <img src="/resources/images/720x400.jpg" class="img-responsive">
                                </div>
                                <div class="item_infos">
                                    <p class="item_name">Item Name</p>
                                    <p class="item_price">Rs. <span>86987</span></p>
                                </div>
                            </div>
                        </div>

                        <div class="item_container col-lg-3">
                            <div class="block_item">
                                <div class="item_image">
                                    <img src="/resources/images/720x400.jpg" class="img-responsive">
                                </div>
                                <div class="item_infos">
                                    <p class="item_name">Item Name</p>
                                    <p class="item_price">Rs. <span>86987</span></p>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


<div class="item_container_template hidden">
    <div class="item_container col-lg-3">
        <div class="block_item">
            <div class="item_image">
                <img src="/resources/images/720x400.jpg" class="img-responsive">
            </div>
            <div class="item_actions">
                <a href="#">Edit</a>
                <a href="#">View</a>
            </div>
        </div>
    </div>
</div>

</body>
</html>
