<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Categories</title>

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
                <li class="col-xs-4"><a href="/admin/algorithm">Algorithm</a></li>
                <li class="col-xs-4 current_page"><a href="/admin/view_category">Categories</a></li>
            </ul>
        </div>
        <div class="main_content">Content</div>
    </div>
</div>

<script type="text/javascript">
    $('.current_page').click(function(e){
        e.preventDefault();
    });
</script>

</body>
</html>
