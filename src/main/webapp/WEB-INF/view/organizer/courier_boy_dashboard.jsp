<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Courier Boy Dashboard</title>

    <%@include file="../includes/head.jsp" %>


</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Courier Boy</h1>
            <a class="btn btn_head pull-right" href="/courier_boy/create">Add Courier Boy</a>
        </div>
        <div class="main_content">Content</div>
    </div>
</div>

</body>
</html>
