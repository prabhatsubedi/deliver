<div class="sidebar">
    <div class="sidebar_logo">
        <a href="/organizer/dashboard"><img src="/resources/images/delivr-logo.png" class="img-responsive" /></a>
    </div>
    <div class="sidebar_menu">
        <ul class="nav nav-stacked">
            <li><a href="/organizer/dashboard">Dashboard</a></li>
            <li><a href="/organizer/merchants">Merchants</a></li>
            <li><a href="/organizer/stores">Stores</a></li>
            <li><a href="/organizer/courier_staff/list">Courier Staff</a></li>
            <li><a href="#">Invoices</a></li>
            <li><a href="#">Purchase History</a></li>
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

    });

</script>