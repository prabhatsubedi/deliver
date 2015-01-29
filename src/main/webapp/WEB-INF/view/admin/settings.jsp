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
            $('.current_page').click(function(e){
                e.preventDefault();
            });
            Admin.loadSettings();
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
        <div class="settings_menu">
            <ul class="nav nav-pills">
                <li class="col-xs-4 current_page"><a href="/admin/settings">General</a></li>
                <li class="col-xs-4"><a href="/admin/algorithm">Algorithm</a></li>
                <li class="col-xs-4"><a href="/admin/view_category">Categories</a></li>
            </ul>
        </div>
        <div class="main_content">

            <div class="row">
                <div class="form_container form_editable form_section full_width">
                    <form id="form_settings" action="" method="POST" role="form">
                        <div class="form_head">All Settings

                            <div class="detail_options pull-right">
                                <a class="pull-left btn btn_green edit_btn none_editable glyphicon glyphicon-edit"></a>
                                <div class="pull-left action_buttons editable hidden">
                                    <a class="btn btn_green save_btn glyphicon glyphicon-floppy-disk"></a>
                                    <a class="btn btn_green cancel_btn glyphicon glyphicon-remove"></a>
                                </div>
                            </div>

                        </div>
                        <div class="form_content">
                            <div class="row display_settings">
                            </div>
                        </div>

<%--                        <div class="form_head">Currency & Tax</div>
                        <div class="form_content">
                            <div class="row">
                                <div class="form-group clearfix">
                                    <label for="currency" class="col-lg-4 floated_label">Currency</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_currency none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <select id="currency" name="currency" class="currency col-xs-12 no_pad no_margin" data-style="form-control">
                                                <option value="$">$</option>
                                                <option value="Rs.">Rs.</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label for="system_vat" class="col-lg-4 floated_label">System VAT</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_system_vat none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="system_vat" id="system_vat" class="form-control">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="form_head">Reward Configuration</div>
                        <div class="form_content">
                            <div class="row">
                                <div class="form-group clearfix">
                                    <label for="signup_reward" class="col-lg-4 floated_label">User Signup Reward</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_signup_reward none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="signup_reward" id="signup_reward" class="form-control">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label for="reward_first_user" class="col-lg-4 floated_label">Successful Referral Reward to First User</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_reward_first_user none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="reward_first_user" id="reward_first_user" class="form-control">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label for="reward_second_user" class="col-lg-4 floated_label">Referral Reward to Second User</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_reward_second_user none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="reward_second_user" id="reward_second_user" class="form-control">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label for="referral_limit" class="col-lg-4 floated_label">Referral Limit</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_referral_limit none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="referral_limit" id="referral_limit" class="form-control">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="form_head">Email Configuration</div>
                        <div class="form_content">
                            <div class="row">
                                <div class="form-group clearfix">
                                    <label for="admin_email" class="col-lg-4 floated_label">Admin Email Address</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_admin_email none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="admin_email" id="admin_email" class="form-control">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label for="account_email" class="col-lg-4 floated_label">Account Email Address</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_account_email none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="account_email" id="account_email" class="form-control">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label for="support_email" class="col-lg-4 floated_label">Support Email Address</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_support_email none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="support_email" id="support_email" class="form-control">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="form_head">Company Information</div>
                        <div class="form_content">
                            <div class="row">
                                <div class="form-group clearfix">
                                    <label for="company_name" class="col-lg-4 floated_label">Company Name</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_company_name none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="company_name" id="company_name" class="form-control">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label for="logo" class="col-lg-4 floated_label">Logo</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_logo none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="logo" id="logo" class="form-control">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label for="address" class="col-lg-4 floated_label">Address</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_address none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="address" id="address" class="form-control">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label for="contact_no" class="col-lg-4 floated_label">Contact No</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_contact_no none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="contact_no" id="contact_no" class="form-control">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label for="email" class="col-lg-4 floated_label">Email</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_email none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="email" id="email" class="form-control">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label for="url" class="col-lg-4 floated_label">Website</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_url none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="url" id="url" class="form-control">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label for="vat_no" class="col-lg-4 floated_label">VAT No</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_vat_no none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="vat_no" id="vat_no" class="form-control">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label for="reg_no" class="col-lg-4 floated_label">Registration No.</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_reg_no none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="reg_no" id="reg_no" class="form-control">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label for="support_phone" class="col-lg-4 floated_label">Customer Support Phone No.</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_support_phone none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="support_phone" id="support_phone" class="form-control">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="form_head">Version Configuration</div>
                        <div class="form_content">
                            <div class="row">
                                <div class="form-group clearfix">
                                    <label for="web_version" class="col-lg-4 floated_label">Web Application Version No.</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_web_version none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="web_version" id="web_version" class="form-control">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group clearfix">
                                    <label for="android_version" class="col-lg-4 floated_label">Android Application Version No.</label>
                                    <div class="col-lg-8">
                                        <div class="form-control info_display val_android_version none_editable"></div>
                                        <div class="info_edit editable hidden">
                                            <input type="text" name="android_version" id="android_version" class="form-control">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>--%>
                    </form>
                </div>
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

</body>
</html>
