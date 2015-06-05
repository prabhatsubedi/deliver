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
            <a href="/organizer/dashboard"><img src="${pageContext.request.contextPath}/resources/images/logo.png" class="img-responsive" /></a>
        </div>
        <div class="sidebar_menu">
            <ul class="nav nav-stacked">
                <li><a href="/accountant/merchants" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Merchants"><span class="delivricon delivricon-merchant"></span><span class="menu_text">Merchants</span></a></li>
                <li><a href="/accountant/courier_staff/list" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Shopper"><span class="delivricon delivricon-courier-staff"></span><span class="menu_text">Shopper</span></a></li>
                <sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_ACCOUNTANT')">
                    <li><a href="/accountant/invoices" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Statements"><span class="delivricon delivricon-invoice"></span><span class="menu_text">Statements</span></a></li>
                    <li><a href="/accountant/fund_transfer" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Fund Tranfer"><span class="delivricon delivricon-invoice"></span><span class="menu_text">Fund Tranfer</span></a></li>
                </sec:authorize>
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