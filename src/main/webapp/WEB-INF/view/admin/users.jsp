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
        <div class="main_tabs">
            <ul class="nav nav-pills">
                <li class="col-xs-6 active"><a href="#managers" data-toggle="tab">Managers</a></li>
                <li class="col-xs-6"><a href="#accountants" data-toggle="tab">Accountants</a></li>
            </ul>
        </div>
        <div class="main_content">


            <div class="tab-content body_padding">
                <div class="tab-pane active" id="managers">   managers
                </div>
                <div class="tab-pane" id="accountants"> accountants
                </div>
            </div>

        </div>
    </div>
</div>

</body>
</html>
