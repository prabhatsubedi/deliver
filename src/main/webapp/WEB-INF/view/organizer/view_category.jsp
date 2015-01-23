<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>View Category</title>

    <%@include file="../includes/head.jsp" %>

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/manager.js"></script>

    <script type="text/javascript">
        $(document).ready(function () {
              Manager.getCategories();
        });
    </script>


</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
        <div class="heading clearfix">
            <h1 class="pull-left">Category View</h1>
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
    </div>
</div>

</body>
</html>
