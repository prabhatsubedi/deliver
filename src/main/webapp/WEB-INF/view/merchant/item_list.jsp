<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Items</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="/resources/js/item.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            Item.loadListItems();
        });
    </script>

</head>
<body>

<%@include file="../includes/sidebar_merchant.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body loader_div">
        <div class="heading clearfix">
            <h1 class="pull-left brand_container">
                <select id="item_brand" name="item_brand" class="col-xs-12 no_pad no_margin hidden" data-style="form-control">
                </select>
            </h1>
            <a class="btn btn_green pull-right" href="/merchant/item/form/create/">Add Item</a>
        </div>
        <div class="main_content">
            <div class="item_listing">
                <div class="table_div">
                    <div class="categories_container td_div full_height">
                        <div class="cateogry_list"></div>
                    </div>
                    <div class="items_container td_div"></div>
                </div>
            </div>
        </div>
        <div class="loader"></div>
    </div>
</div>


<div class="item_container_template hidden">
    <div class="item_container col-lg-3">
        <div class="block_item">
            <div class="item_image">
                <img class="img-responsive no_image maintain_ratio" mr-height="400" mr-width="400">
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
