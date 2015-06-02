<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
    <title>Items</title>

    <%@include file="../includes/head.jsp" %>


    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/html2canvas.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.Jcrop.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/image.js"></script>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery.Jcrop.css" type="text/css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jcrop.css" type="text/css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/item.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            Item.loadListItems();
        });
    </script>

</head>
<body class="page_item_list">

<%@include file="../includes/sidebar_merchant.jsp" %>

<div class="main_container">

    <%@include file="../includes/header.jsp" %>

    <div class="body loader_div">
        <div class="heading clearfix">
            <h1 class="pull-left brand_container">
                <select id="item_brand" name="item_brand" class="col-xs-12 no_pad no_margin hidden" data-style="form-control">
                </select>
            </h1>
            <a class="btn btn_green pull-right" href="/merchant/item/form/create/">Add Item</a>
        </div>
        <div class="main_content">
            <div class="item_listing">
                <div class="table_div">
                    <div class="categories_container td_div full_height">
                        <div class="cateogry_list"></div>
                    </div>
                    <div class="items_container td_div"></div>
                    <div class="cat_resize_controller hidden"></div>
                </div>
            </div>
        </div>
        <div class="loader"></div>
    </div>
</div>

<div class="item_container_template hidden">
    <div class="item_container items_list col-lg-3 col-xs-6 invisible">
        <div class="block_item">
            <div class="item_image">
                <div class="item_drop_zone drop_zone unselectable maintain_ratio" mr-height="400" mr-width="400">
                    <img class="img-responsive no_image">
                </div>
                <div class="switch_container hidden">
                    <div class="switch switch_activation">
                        <div class="btn_switch on"></div>
                    </div>
                </div>
                <button type="button" class="btn btn_green btn_add_tags">Add Tags</button>
            </div>
            <div class="item_infos">
                <p class="item_name"><a href="#"></a></p>
                <p class="item_price">Rs. <span></span></p>
            </div>
        </div>
    </div>
</div>
<div class="pagination_template hidden">
    <div class="pagination_list col-lg-12">
        <ul class="pagination pull-left">
        </ul>
        <div class="num_items pull-right">
            Show per Page
            <select class="select_num_items" name="select_num_items" data-width="auto">
                <option value="0">All</option>
            </select>

        </div>
    </div>
</div>

<input type="file" onchange="Image.readURL(this)" data-dimension="400x400" id="item_image_input" name="item_image_input" class="hidden" />

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

<div class="modal fade modal_form" id="modal_tags">
    <div class="modal-dialog">
        <form role="form" id="form_tags" method="POST" action="">
            <div class="modal-content">
                <div class="modal-header text-center">
                    Tags
                </div>
                <div class="modal-body body_padding">
                    <div class="form-group">
                        <textarea class="form-control" id="tags" name="tags" placeholder="Tags"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="col-lg-6 no_pad">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    </div>
                    <div class="col-lg-6 no_pad">
                        <button type="submit" class="btn btn-default next">Submit</button>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

</body>
</html>
