<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Settings</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/admin.js"></script>


    <script type="text/javascript">
        $(document).ready(function(){
            $('.main_tabs .active').click(function(e){
                e.preventDefault();
            });
            Admin.loadSettings(1);
            Admin.loadEditSettings();
        });
    </script>

</head>
<body>

<%@include file="../includes/sidebar.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body">
<%--        <div class="heading clearfix">
            <h1 class="pull-left">Settings</h1>
        </div>--%>
        <div class="main_tabs">
            <ul class="nav nav-pills">
                <li class="col-xs-6 active"><a href="/admin/settings">General</a></li>
                <li class="col-xs-6"><a href="/admin/algorithm">Algorithm</a></li>
            </ul>
        </div>
        <div class="main_content">

            <div class="form_container form_editable form_section full_width">
                <form id="form_settings" action="" method="POST" role="form" class="display_settings">

                </form>
            </div>

        </div>
    </div>
</div>

<div class="form_field_template hidden">
    <div class="form-group clearfix">
        <label class="col-lg-4 floated_label"></label>
        <div class="col-lg-8">
            <div class="form-control info_display none_editable"></div>
            <div class="info_edit editable hidden">
                <input type="text" class="form-control">
            </div>
        </div>
    </div>
</div>

<div class="form_section_template hidden">
    <div class="form_group">
        <div class="form_head">
            <span class="section_title"></span>
            <div class="detail_options pull-right">
                <a class="pull-left btn btn_green edit_btn none_editable glyphicon glyphicon-edit"></a>
                <div class="pull-left action_buttons editable hidden">
                    <a class="btn btn_green save_btn glyphicon glyphicon-floppy-disk"></a>
                    <a class="btn btn_green cancel_btn glyphicon glyphicon-remove"></a>
                </div>
            </div>

        </div>
        <div class="form_content">
            <div class="row">
            </div>
        </div>
    </div>
</div>

</body>
</html>
