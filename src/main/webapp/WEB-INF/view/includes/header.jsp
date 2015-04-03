<div class="header clearfix">
    <div class="item_search col-lg-8">
        <form role="form" id="item_search" method="POST" action="${pageContext.request.contextPath}/merchant/search/item">
            <div class="col-lg-4 no_pad">
                <input type="text" class="form-control" id="item_name" name="item_name" placeholder="Search Items">
            </div>
            <div class="col-lg-8 no_pad">
                <div class="col-lg-9 no_pad">
                    <select id="item_stores" name="item_stores" class="compelselection haveall item_stores col-lg-6 col-xs-12 no_pad" data-style="form-control" multiple="multiple">
                        <option value="All" selected="selected">All Stores</option>
                    </select>
                    <select id="item_categories" name="item_categories" class="compelselection haveall item_categories col-lg-6 col-xs-12 no_pad" data-style="form-control" multiple="multiple">
                        <option value="All" selected="selected">All Categories</option>
                    </select>
                </div>
                <div class="col-lg-3 no_pad_right search_button">
                    <button type="submit" class="btn btn-default btn_search">SEARCH</button>
                </div>
            </div>
        </form>
    </div>
    <div class="user_menu col-lg-4 no_pad_right">
        <div class="pull-right user-nav">
            <div class="dropdown">
                <a href="#" class="user_image" data-toggle="dropdown" data-hover="dropdown" data-delay="1000" data-close-others="false"><img src="${pageContext.request.contextPath}/resources/images/user-icon.png" class="img-responsive" /> </a>
                <ul class="dropdown-menu pull-right">
                    <sec:authorize access="hasRole('ROLE_ADMIN')">
                        <li><a href="/admin/users">User Management</a></li>
                        <li><a href="/admin/settings">Settings</a></li>
                        <li><a href="/admin/view_category">Categories</a></li>
                    </sec:authorize>
                    <%--<sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')">--%>
                    <sec:authorize access="hasRole('ROLE_MANAGER')">
                        <li><a href="#">Profile</a></li>
                        <%--<li><a href="/organizer/profile">Profile</a></li>--%>
                    </sec:authorize>
                    <sec:authorize access="hasRole('ROLE_MERCHANT')">
                        <li><a href="/merchant/profile">Profile</a></li>
                    </sec:authorize>
                    <li><a href="#" data-target="#modal_password" data-toggle="modal">Change Password</a></li>
                    <li><a href="#" onclick="Main.doLogout()">Logout</a></li>
                </ul>
            </div>
        </div>
        <div class="pull-right user_name">
            <%--Manager--%>
        </div>
    </div>
</div>

<div id="menu_overlay"></div>

<div class="modal fade modal_form" id="modal_password" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <form role="form" id="form_password" method="POST" action="">
            <div class="modal-content">
                <div class="modal-header text-center">
                    Change Password
                </div>
                <div class="modal-body body_padding">
                    <div class="form-group">
                        <input type="password" class="form-control" id="change_old_password" name="change_old_password" placeholder="Old Password">
                    </div>
                    <div class="form-group">
                        <input type="password" class="form-control" id="change_password" name="change_password" placeholder="Password">
                    </div>
                    <div class="form-group">
                        <input type="password" class="form-control" id="change_re_password" name="change_re_password" placeholder="Retype Password">
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="col-lg-6 no_pad">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    </div>
                    <div class="col-lg-6 no_pad">
                        <button type="submit" class="btn btn-default next">Change</button>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function(){

        if(Main.getFromLocalStorage('profileImage') != "undefined" && Main.getFromLocalStorage('profileImage') != "" && Main.getFromLocalStorage('profileImage') != undefined) {
            $('.user_menu img').attr('src', Main.getFromLocalStorage('profileImage'));
        }
        $('.user_menu .user_name').html(Main.getFromLocalStorage('userTitle'));

        $('.user_menu .dropdown').hover(function(){
            $(this).addClass('open');
        }, function(){
            $(this).removeClass('open');
        });

        $('#modal_password').on('shown.bs.modal', function (e) {

            $.validator.setDefaults({
                errorPlacement : function(error, element){
                    $('#error_container').html(error);
                },
                ignore: []
            });
            $('#form_password').validate({
                submitHandler: function(form) {

                    var button1 = function() {

                        var headers = {};
                        headers.password = $('#change_old_password').val();
                        headers.newPassword = $('#change_password').val();

                        Main.changePassword(headers);
                    };

                    button1.text = "Yes";
                    var button2 = "No";

                    var buttons = [button1, button2];
                    Main.popDialog('Change Password', 'Are you sure you want to change password?', buttons);

                    return false;

                }
            });

            $('#modal_password').on('hidden.bs.modal', function(){
                $('#form_password')[0].reset();
                $('#form_password').validate().resetForm();
            });

            $('#change_old_password').rules('add', {required: true, minlength: 6});
            $('#change_password').rules('add', {required: true, minlength: 6});
            $('#change_re_password').rules('add', {required: true, minlength: 6, equalTo: '#change_password'});

        });

        $('.sidebar_menu a[href]').not('[href="javascript:;"]').each(function(){
            $(this).attr('href', Main.modifyURL($(this).attr('href')));
        });

        $('#item_categories').selectpicker({size: 5});
        $('#item_stores').selectpicker({size: 5});

        $('select.haveall').live('change', function(){
            if($(this).val() != null && $('option', this).length == $(this).val().length + 1)
                $(this).selectpicker('selectAll');
        });

        $('select.compelselection').live('change', function(){
            if($(this).val() == null) $(this).selectpicker('selectAll');
        });

    });
</script>