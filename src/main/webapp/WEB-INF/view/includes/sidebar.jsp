<div class="tooltip_conatiner"></div>
<div class="sidebar" id="sidebar_menu">
    <div class="clearfix menu_toggle_btn">
        <button type="button" class="navbar-toggle collapsed menu_toggle" data-menu="#sidebar_menu" data-body=".main_container">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
    </div>
    <div class="sidebar_inner">
        <div class="sidebar_logo">
            <a href="/organizer/dashboard"><img src="${pageContext.request.contextPath}/resources/images/delivr-logo.png" class="img-responsive" /></a>
        </div>
        <div class="sidebar_menu">
            <ul class="nav nav-stacked">
                <li><a href="/organizer/dashboard" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Dashboard"><span class="delivricon delivricon-dashboard"></span><span class="menu_text">Dashboard</span></a></li>
                <li><a href="/organizer/merchants" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Merchants"><span class="delivricon delivricon-merchant"></span><span class="menu_text">Merchants</span></a></li>
                <li><a href="/organizer/stores" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Stores"><span class="delivricon delivricon-store"></span><span class="menu_text">Stores</span></a></li>
                <li><a href="/organizer/courier_staff/list" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Shopper"><span class="delivricon delivricon-courier-staff"></span><span class="menu_text">Shopper</span></a></li>
                <li><a href="/merchant/orders" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Orders"><span class="delivricon delivricon-orders"></span><span class="menu_text">Orders</span></a></li>
                <li><a href="/organizer/customers" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Inactive Customers"><span class="delivricon delivricon-user"></span><span class="menu_text">Inactive Customers</span></a></li>
                <li><a href="/organizer/sms" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="SMS"><span class="delivricon delivricon-message"></span><span class="menu_text">SMS</span></a></li>
                <li><a href="/organizer/notification" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Send Notification"><span class="delivricon delivricon-notification"></span><span class="menu_text">Send Notification</span></a></li>
            </ul>
        </div>
    </div>
</div>

<script type="text/javascript">

    $(document).ready(function(){

        var pathname = window.location.pathname;
        $('.sidebar_menu li a').each(function(){
            if(pathname.indexOf($(this).attr('href')) > -1) {
                $(this).addClass('active');
                return false;
            }
        });

        $('.sidebar_menu li a').click(function(){
            $('.sidebar_menu li a').removeClass('active');
            $(this).addClass('active');
        });

        $('.sidebar_menu a[href]').not('[href="javascript:;"]').each(function(){
            $(this).attr('href', Main.modifyURL($(this).attr('href')));
        });


    });

</script>