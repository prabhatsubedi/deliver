<div class="sidebar menu_opened" id="sidebar_menu">
    <div class="clearfix">
        <button type="button" class="navbar-toggle collapsed menu_toggle" data-menu="#sidebar_menu" data-body=".main_container">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
    </div>
    <div class="sidebar_logo">
        <a href="/organizer/dashboard"><img src="${pageContext.request.contextPath}/resources/images/delivr-logo.png" class="img-responsive" /></a>
    </div>
    <div class="sidebar_menu">
        <ul class="nav nav-stacked">
            <li><a href="/organizer/dashboard" class="elem_tooltip" data-placement="right" title="Dashboard"><span class="delivricon delivricon-dashboard"></span><span class="menu_text">Dashboard</span></a></li>
            <li><a href="/organizer/merchants" class="elem_tooltip" data-placement="right" title="Merchants"><span class="delivricon delivricon-merchant"></span><span class="menu_text">Merchants</span></a></li>
            <li><a href="/organizer/stores" class="elem_tooltip" data-placement="right" title="Stores"><span class="delivricon delivricon-store"></span><span class="menu_text">Stores</span></a></li>
            <li><a href="/organizer/courier_staff/list" class="elem_tooltip" data-placement="right" title="Courier Staff"><span class="delivricon delivricon-courier-staff"></span><span class="menu_text">Courier Staff</span></a></li>
            <li><a href="#" class="elem_tooltip" data-placement="right" title="Invoices"><span class="delivricon delivricon-invoice"></span><span class="menu_text">Invoices</span></a></li>
            <li><a href="#" class="elem_tooltip" data-placement="right" title="Purchase History"><span class="delivricon delivricon-purchase-history"></span><span class="menu_text">Purchase History</span></a></li>
        </ul>
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