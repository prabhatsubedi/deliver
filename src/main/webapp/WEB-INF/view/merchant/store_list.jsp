<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Stores</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="/resources/js/store.js"></script>

    <script type="text/javascript">

        $(document).ready(function(){

            Store.listStores({merchantId: Main.getFromSessionStorage('mid')});

        });

    </script>


</head>
<body>

<%@include file="../includes/sidebar_merchant.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Stores</h1>
            <a class="btn btn_green pull-right merchant_link" href="/merchant/store/form/create">Add Store</a>
        </div>
        <div class="main_content">

        </div>
    </div>
</div>


<div class="box_store_template hidden">
    <div class="box_store table_div">
        <div class="store_logo td_div">
            <img class="img-responsive" />
        </div>
        <div class="store_info td_div">
            <div class="store_name"></div>
            <div class="store_address_list">
                <ul>
                </ul>
            </div>
        </div>
        <div class="store_actions td_div">
            <a class="btn btn-default btn_logins add_items merchant_link">Add Item</a>
        </div>
    </div>
</div>

</body>
</html>
