<script src="<c:url value="${pageContext.request.contextPath}/resources/js/header.js" />"></script>
<div class="header clearfix">
    <div class="item_search col-lg-8">
        <form role="form" id="item_search" method="POST" action="${pageContext.request.contextPath}/merchant/search/item">
            <div class="col-lg-4 no_pad">
                <input type="text" class="form-control" id="item_name" name="item_name" placeholder="Search Items">
            </div>
            <div class="col-lg-8 no_pad">
                <div class="col-lg-9 no_pad">
                    <select id="item_stores" name="item_stores" class="item_stores col-lg-6 col-xs-12 no_pad" data-style="form-control" multiple="multiple">
                    </select>
                    <select id="item_categories" name="item_categories" class="item_categories col-lg-6 col-xs-12 no_pad" data-style="form-control" multiple="multiple">
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
                        <li><a href="/admin/settings">Settings</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')">
                        <li><a href="/organizer/profile">Profile</a></li>
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
            Manager
        </div>
    </div>
</div>

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

<div class="item_container_template hidden">
    <div class="item_container col-lg-3 invisible">
        <div class="block_item">
            <div class="item_image maintain_ratio" mr-height="400" mr-width="400">
                <img class="img-responsive no_image">
                <div class="switch_container hidden">
                    <div class="switch switch_activation">
                        <div class="btn_switch on"></div>
                    </div>
                </div>
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

<script type="text/javascript">
    $(document).ready(function(){
        Header.loadSearch();

        $('#modal_password').on('shown.bs.modal', function (e) {

            $.validator.setDefaults({
                errorPlacement : function(error, element){
                    $('#error_container').html(error);
                },
                ignore: []
            });
            $('#form_password').validate({
                submitHandler: function(form) {

                    var chk_confirm = confirm('Are you sure you want to change password?');
                    if(!chk_confirm) return false;

                    var headers = {};

                    headers.password = $('#change_old_password').val();
                    headers.newPassword = $('#change_password').val();

                    Main.changePassword(headers);

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

        $('#item_categories').selectpicker({noneSelectedText: 'Select Categories'});
        $('#item_stores').selectpicker({noneSelectedText: 'Select Stores'});

        $('select.haveall').live('change', function(){
            if($(this).val() != null && $('option', this).length == $(this).val().length + 1)
                $(this).selectpicker('selectAll');
        });

        $('select.compelselection').live('change', function(){
            if($(this).val() == null) $(this).selectpicker('selectAll');
        });

/*
        $.validator.addMethod("notEqual", function(value, element, arg){
            var result = value != arg;
            if($(element).is('select')) {
                if(!result) {
                    $(element).next('.bootstrap-select').children('.form-control').addClass('error');
                } else {
                    $(element).next('.bootstrap-select').children('.form-control').removeClass('error');
                }
            }
            return result;
        }, "Please select any option.");

        $.validator.addMethod("minSelection", function(value, element, arg){
            var result = value != null && (value.length >= arg || value == 'All');
            if($(element).is('select')) {
                if(!result) {
                    $(element).next('.bootstrap-select').children('.form-control').addClass('error');
                } else {
                    $(element).next('.bootstrap-select').children('.form-control').removeClass('error');
                }
            }
            return result;
        }, $.validator.format("Please select at least {0} options."));

        $.validator.setDefaults({
            errorPlacement : function(error, element){
                $('#error_container').html(error);
            },
            ignore: []
        });

        $('#item_search').validate({
            submitHandler: function(form) {
            }
        });
        $('#item_name').rules('add', {required: true});
        $('#item_stores').rules('add', {notEqual: "none"});
        $('#item_categories').rules('add', {minSelection: 1});

        Main.getAllStores();*/

    });
</script>