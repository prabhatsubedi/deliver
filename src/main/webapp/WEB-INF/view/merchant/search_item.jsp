<%@ page import="com.google.gson.Gson" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Item Search</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/item.js"></script>
    <script type="text/javascript">
        <%
            Gson gson = new Gson();
            String postParams = gson.toJson(request.getParameterMap());
        %>
        var postParams = <%=postParams %>;
        $(document).ready(function(){
            Item.searchItems();
        });
    </script>

</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Item Search</h1>
        </div>
        <div class="main_content">
            <div class="items_container row">

            </div>
        </div>
    </div>
</div>

<div class="item_container_template hidden">
    <div class="item_container col-lg-3 invisible">
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
