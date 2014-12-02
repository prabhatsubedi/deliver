<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<!DOCTYPE html>
<html>
<head>
<title>Delivr</title>

    <%@include file="includes/head.jsp" %>
    
    <script type="text/javascript" src="<c:url value="/resources/js/jquery.validate.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/jquery.Jcrop.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/html2canvas.js" />"></script>
    <script src="<c:url value="/resources/js/jquery.validate.js" />"></script>

    <link rel="stylesheet" href="<c:url value="/resources/css/jquery.Jcrop.css" />" type="text/css" />
    <style type="text/css">

        #crop_img_modal .modal-dialog {
            width: 750px;
        }
        #crop_img_modal .modal-dialog .modal-header {
            border-radius: 6px 6px 0px 0px;
        }
        #crop_img_modal .modal-dialog .modal-body {
            min-height: 350px;
            overflow: hidden;
        }
        .jcrop-holder .preview-pane .preview-head {
            background-color: #ff9235;
            border-radius: 0;
            color: #ffffff;
            font-size: 20px;
            line-height: 45px;
            outline: 0 none;
            padding: 0 15px;
        }
        .jcrop-holder .preview-pane {
            background-color: white;
            display: block;
            float: right;
            margin-right: -260px;
            overflow: hidden;
        }
        .preview-pane .preview-container {
            width: 200px;
            height: 200px;
            overflow: hidden;
        }
        .preview-pane .preview-container-dup {
            width: 240px;
            height: 240px;
            overflow: hidden;
        }
        .preview2 .preview-container {
            width: 720px !important;
            height: 400px !important;
        }
        .preview2 .preview-container-dup {
            width: 230px !important;
            height: 128px !important;
        }
        .preview-pane-org {
            margin-right: 0 !important;
            position: absolute;
            right: -276px !important;
            top: -8px !important;
            z-index: -1 !important;
        }

    </style>

    <script type="text/javascript" src="<c:url value="/resources/js/login-page.js" />"></script>

</head>
<body>

<div class="logo_container">
    <img src="<c:url value="/resources/images/login-logo.png" />" class="img-responsive">
</div>

<div class="delivr_info container">
    <div class="info_head">
        Lorem Ipsum is simply dummy text of the printing
    </div>
    <div class="info_content">
        and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.
    </div>
</div>

<div class="login_buttons text-center">
    <button type="button" class="btn btn-default btn_login" data-toggle="modal" data-target="#modal_login">Login</button>
    <button type="button" class="btn btn-default btn_signup" data-toggle="modal" data-target="#modal_signup">Sign Up</button>
</div>

<div class="login_video">
    <div class="video_image">
        <img src="<c:url value="/resources/images/login-image.jpg" />" class="img-responsive">
        <div class="btn_play">
            <img src="<c:url value="/resources/images/btn-play.png" />" class="img-responsive">
        </div>
    </div>
    <div class="video_text">Lorem Ipsum is simply dummy text of the printing</div>
</div>

<div class="login_buttons text-center">
    <button type="button" class="btn btn-default btn_login" data-toggle="modal" data-target="#modal_login">Login</button>
    <button type="button" class="btn btn-default btn_signup" data-toggle="modal" data-target="#modal_signup">Sign Up</button>
</div>

<div class="login_foot">
    <div class="foot_logo"><img src="<c:url value="/resources/images/login-logo.png" />" class="img-responsive"></div>
    <div class="foot_links">
        <ul class="nav nav-pills text-center">
            <li><a href="#">Delivr</a></li>
            <li><a href="#">Terms of use</a></li>
            <li><a href="#">Privacy Policy</a></li>
        </ul>
    </div>
</div>

<div class="modal fade" id="modal_login" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-body">
                <div class="login_head text-center"> Lets do it</div>
                <div class="login_body">
                    <form role="form" id="login_form" method="POST" action="">
                        <div class="form-group">
                            <input type="text" class="form-control" id="email" name="email" placeholder="Email">
                        </div>
                        <div class="form-group">
                            <input type="password" class="form-control" id="password" name="password" placeholder="Password">
                        </div>
                        <div class="form-group">
                            <div class="clearfix">
                                <div class="col-lg-6 no_pad_left">
                                    <button type="button" class="btn btn-default btn_logins" data-dismiss="modal">Cancel</button>
                                </div>
                                <div class="col-lg-6 no_pad_right">
                                    <button type="submit" class="btn btn-default btn_logins">Login</button>
                                </div>
                            </div>
                            <a href="#">Need Help?</a>
                        </div>
                    </form>
                </div>
                <div class="login_foot text-center">
                    Are you new to <strong>Delivr</strong> ?
                    <button type="button" class="btn btn-default btn_logins" data-toggle="modal" data-target="#modal_signup">Sign Up</button>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="modal_signup" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <form role="form" id="signup_form" method="POST" action="">
            <div class="modal-content">
                <div class="modal-header text-center">
                    Merchant Sign Up
                </div>
                <div class="modal-body">
                    <!-- Nav tabs -->
                    <ul class="nav nav-pills">
                        <li class="col-xs-4 no_pad active"><a href="#step_1" data-toggle="tab">Step 1</a></li>
                        <li class="col-xs-4 no_pad disabled_tab"><a href="#step_2" data-toggle="tab">Step 2</a></li>
                        <li class="col-xs-4 no_pad disabled_tab"><a href="#step_3" data-toggle="tab">Step 3</a></li>
                    </ul>

                    <!-- Tab panes -->
                    <div class="tab-content">
                        <div class="tab-pane active" id="step_1">
                            <div class="form-group">
                                <input type="text" class="form-control" id="business_name" name="business_name" placeholder="Business Name">
                            </div>
                            <div class="form-group business_logo unselectable">
                                <div class="drop_info">Drop image file (or click to browse)</div>
                                <div class="logo_text">Business Logo</div>
                                <div id="drop_zone" class="text-center"></div>
                            </div>
                            <input type="file" onchange="readURL(this)" id="logo_input" class="hidden" />
                            <div class="form-group">
                                <input type="text" class="form-control" id="url" name="url" placeholder="URL">
                            </div>
                        </div>

                        <div class="tab-pane" id="step_2">
                            <div class="form-group">
                                <input type="text" class="form-control" id="contact_person" name="contact_person" placeholder="Contact Person">
                            </div>
                            <div class="form-group">
                                <input type="text" class="form-control" id="contact_email" name="contact_email" placeholder="Contact Email">
                            </div>
                            <div class="form-group">
                                <input type="text" class="form-control" id="contact_no" name="contact_no" placeholder="Contact No.">
                            </div>
                            <div class="form-group">
                                <input type="text" class="form-control" id="registration_no" name="registration_no" placeholder="Company Registration No.">
                            </div>
                            <div class="form-group">
                                <input type="text" class="form-control" id="vat" name="vat" placeholder="VAT">
                            </div>
                        </div>

                        <div class="tab-pane" id="step_3">3</div>
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="col-lg-6 no_pad">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    </div>
                    <div class="col-lg-6 no_pad">
                        <button type="submit" class="btn btn-default">Next</button>
                    </div>
                </div>
            </div>
        </form>
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
                    <button type="button" id="apply_preview" class="btn btn-primary">Set Logo</button>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>