<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>

    <%@include file="../includes/head.jsp" %>


</head>
<body>

<div class="sidebar">
    <div class="sidebar_logo">
        <img src="/resources/images/delivr-logo.png" class="img-responsive" />
    </div>
    <div class="sidebar_menu">
        <ul class="nav nav-stacked">
            <li><a href="#" class="active">Merchants</a></li>
            <li><a href="#">Courier Boy</a></li>
            <li><a href="#">Invoices</a></li>
            <li><a href="#">Purchase History</a></li>
        </ul>
    </div>
</div>
<div class="main_container">
    <div class="header clearfix">
        <div class="item_search col-lg-8">
            <form role="form" id="item_search" class="form-inline" method="POST" action="">
                <div class="form-group">
                    <input type="text" class="form-control" id="item_name" name="item_name" placeholder="Search Items">
                </div>
                <div class="form-group">
                    <select id="item_categories" name="item_categories" class="form-control">
                        <option>All Categories</option>
                        <option>2</option>
                        <option>3</option>
                        <option>4</option>
                        <option>5</option>
                    </select>
                </div>
                <div class="form-group">
                    <select id="item_stores" name="item_stores" class="form-control">
                        <option>All Stores</option>
                        <option>2</option>
                        <option>3</option>
                        <option>4</option>
                        <option>5</option>
                    </select>
                </div>
                <button type="submit" class="btn btn-default">Search</button>
            </form>
        </div>
        <div class="user_menu col-lg-4 no_pad_right">
            <div class="pull-right user-nav">
                <div class="dropdown">
                    <a href="#" class="user_image" data-toggle="dropdown" data-hover="dropdown" data-delay="1000" data-close-others="false"><img src="/resources/images/user-icon.png" class="img-responsive" /> </a>
                    <ul class="dropdown-menu pull-right">
                        <li><a href="#">Settings</a></li>
                        <li><a href="#">Profile</a></li>
                        <li><a href="#">Change Password</a></li>
                        <li><a href="#" onclick="Main.doLogout()">Logout</a></li>
                    </ul>
                </div>
            </div>
            <div class="pull-right user_name">
                Manager
            </div>
        </div>
    </div>
    <div class="body">
        body
    </div>
</div>

</body>
</html>
