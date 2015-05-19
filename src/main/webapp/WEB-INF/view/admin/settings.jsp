<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Settings</title>

    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/html2canvas.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.Jcrop.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/image.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/admin.js"></script>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery.Jcrop.css" type="text/css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jcrop.css" type="text/css" />


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

<div class="form_select_template hidden">
    <div class="form-group clearfix">
        <label class="col-lg-4 floated_label"></label>
        <div class="col-lg-8">
            <div class="form-control info_display none_editable"></div>
            <div class="info_edit editable hidden">
                <select class="col-xs-12 no_pad no_margin selectpicker" data-style="form-control">
                </select>
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

<div class="image_template hidden">
    <div class="form-group clearfix">
        <label class="col-lg-4 floated_label"></label>
        <div class="col-lg-8">
            <div class="col-sm-6 image_wrapper no_pad">
                <div id="brand_image" class="image_container drop_zone unselectable text-center maintain_ratio" mr-width="400" mr-height="400">
                    <div class="drop_info">Drop image file <br /> (or click to browse) <br /> Min Size: <span class="image_size">400x400</span></div>
                </div>
                <input type="file" onchange="Image.readURL(this)" data-dimension="400x400" id="brand_image_input" name="brand_image_input" class="hidden image_input" />
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="crop_img_modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-body">

                <img id="jcrop_target" />

                <div class="preview-pane preview-pane-org">
                    <div class="preview-container">
                        <img id="jcrop_preview"  />
                    </div>
                </div>
                <div class="preview-pane">
                    <div class="preview-head">Preview</div>
                    <div class="preview-container-dup">
                        <img id="jcrop_preview_dup" />
                    </div>
                    <div class="preview-options">
                        <div class="aspect_ratio">
                            <label style="font-weight: normal; line-height: 30px; margin-bottom: 0px;"><input type="checkbox" id="ar_lock" class="check" style="float: left; margin-right: 5px; margin-top: 9px;"/>Maintain Aspect ratio</label>
                        </div>
                    </div>
                </div>

            </div>
            <div class="modal-footer">
                <div class="col-lg-6 no_pad">
                    <button type="button" id="cancel_preview" class="btn btn-primary">Cancel</button>
                </div>
                <div class="col-lg-6 no_pad">
                    <button type="button" id="apply_preview" class="btn btn-primary">Crop Image</button>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
