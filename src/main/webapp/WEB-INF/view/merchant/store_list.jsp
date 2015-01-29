<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Stores</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/store.js"></script>


</head>
<body>

<%@include file="../includes/sidebar_merchant.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Stores</h1>
            <a class="btn btn_green pull-right" href="/merchant/store/form/create">Add Store</a>
        </div>
        <div class="main_content merchant_stores">
            <div class="items_container row form_section">
                <div class="form_head active_stores hidden">Active Stores</div>
                <div class="form_content active_stores hidden clearfix">
                </div>
                <div class="form_head inactive_stores hidden">Inactive Stores</div>
                <div class="form_content inactive_stores hidden clearfix">
                </div>
            </div>
        </div>
    </div>
</div>


<div class="block_store_template hidden">
    <div class="item_container unselectable col-lg-2 invisible">
        <div class="block_item">
            <div class="item_image maintain_ratio" mr-height="400" mr-width="720">
                <img class="img-responsive no_image">
                <a class="btn btn-default btn_logins view_store">View Store</a>
                <a class="btn btn-default btn_logins add_items">Add Item</a>
            </div>
            <div class="item_infos">
                <p class="item_name"></p>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">

    $(document).ready(function(){

        Store.listStores({merchantId: Main.getFromLocalStorage('mid')});

    });

</script>

</body>
</html>
