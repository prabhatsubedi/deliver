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
            <li><a href="/organizer/dashboard"><span class="glyphicon glyphicon-home"></span><span class="menu_text">Dashboard</span></a></li>
            <li><a href="/organizer/merchants"><span class="glyphicon glyphicon-credit-card"></span><span class="menu_text">Merchants</span></a></li>
            <li><a href="/organizer/stores"><span class="glyphicon glyphicon-log-in"></span><span class="menu_text">Stores</span></a></li>
            <li><a href="/organizer/courier_staff/list"><span class="glyphicon glyphicon-user"></span><span class="menu_text">Courier Staff</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-list-alt"></span><span class="menu_text">Invoices</span></a></li>
            <li><a href="#"><span class="glyphicon glyphicon-globe"></span><span class="menu_text">Purchase History</span></a></li>
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