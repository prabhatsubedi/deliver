<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>View Category</title>
    <%@include file="../includes/head.jsp" %>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/manager.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/html2canvas.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.Jcrop.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/image.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery.Jcrop.css" type="text/css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jcrop.css" type="text/css"/>
    <script type="text/javascript">
        $(document).ready(function () {
            Manager.getCategories();
            Manager.loadEditCategory();
            Manager.saveCategory();
            Manager.loadAddSubCategory();
        });
    </script>
</head>
<body>
<%@include file="../includes/sidebar.jsp" %>
<div class="main_container">
    <%@include file="../includes/header.jsp" %>
    <div class="body">
        <div class="settings_menu">
            <ul class="nav nav-pills">
                <li class="col-xs-4"><a href="/admin/settings">General</a></li>
                <li class="col-xs-4"><a href="/admin/algorithm">Algorithm</a></li>
                <li class="col-xs-4 current_page"><a href="/admin/view_category">Categories</a></li>
            </ul>
        </div>
        <div class="heading clearfix">
            <h1 class="pull-left">Category View</h1>
            <a class="pull-right btn btn_green add_root_btn">Add Root Category</a>
        </div>
        <div class="main_content">
            <div class="item_listing">
                <div class="table_div">
                    <div class="categories_container td_div full_height">
                        <a class="btn btn_green view_home">Category Home</a>
                        <div class="cateogry_list"></div>
                    </div>
                    <div class="category_detail col-lg-12 td_div hidden">
                        <div class="form_container">
                            <form role="form" id="category_form" method="POST" action="">
                                <div class="profile_header clearfix">
                                    <div class="profile_header_image col-lg-5 pull-left">
                                        <div id="category_image"
                                             class="drop_zone user_image disabled unselectable text-center maintain_ratio"
                                             mr-width="200" mr-height="200">
                                            <div class="drop_info">Drop image file <br/> (or click to browse)</div>
                                                <div class="drop_title">Category Image</div>
                                        </div>
                                        <input type="file" onchange="Image.readURL(this)" id="category_image_input"
                                               name="category_image_input" class="hidden"/>
                                    </div>
                                    <div class="profile_header_info pull-left">
                                        <div class="info1 business_name"></div>
                                        <div class="info2 partner_status"></div>
                                    </div>
                                    <div class="action detail_options pull-right">
                                        <a class="pull-left btn btn_green edit_btn none_editable glyphicon glyphicon-edit"></a>

                                        <div class="pull-left action_buttons editable hidden">
                                            <a class="btn btn_green save_btn glyphicon glyphicon-floppy-disk"></a>
                                            <a class="btn btn_green cancel_btn glyphicon glyphicon-remove"></a>
                                        </div>
                                        <a class="pull-left btn btn_green add_child_btn">Add Sub Category</a>
                                    </div>
                                </div>

                                <div class="profile_body">
                                    <div class="form-group clearfix">
                                        <input type="hidden" name="category_parent_id" id="category_parent_id">
                                        <input type="hidden" name="category_id" id="category_id">
                                        <label for="name" class="col-lg-2 floated_label no_pad">Name</label>

                                        <div class="col-lg-8">
                                            <div class="form-control name val_url none_editable"></div>
                                            <div class="info_edit editable hidden">
                                                <input type="text" name="name" id="name" class="form-control">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                    <div class="parent_category_list items_container col-lg-12 td_div full_height">

                    </div>
                </div>
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
