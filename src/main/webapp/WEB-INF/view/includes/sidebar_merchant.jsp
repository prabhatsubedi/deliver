<div class="sidebar">
    <div class="sidebar_logo">
        <a href="/merchant/dashboard"><img src="/resources/images/delivr-logo.png" class="img-responsive" /></a>
    </div>
    <div class="sidebar_menu">
        <ul class="nav nav-stacked">
            <sec:authorize access="hasRole('ROLE_ADMIN')">
                <li><a href="/organizer/dashboard">Admin Dashboard</a></li>
            </sec:authorize>
            <%--<li><a href="/merchant/dashboard" class="merchant_link">Dashboard</a></li>--%>
            <li><a href="/merchant/store/list" class="merchant_link">Stores</a></li>
            <li><a href="/merchant/item/list" class="merchant_link">Items</a></li>
            <li><a href="#">Purchase History</a></li>
            <li><a href="#">Invoices</a></li>
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
        $('.sidebar_menu li a[href="' + pathname + '"]').addClass('active');

        $('.sidebar_menu li a').click(function(){
            $('.sidebar_menu li a').removeClass('active');
            $(this).addClass('active');
        });

        if(Main.getURLvalue(2) != undefined && $.isNumeric(Main.getURLvalue(2)) && Main.getURLvalue(1) == 'profile') {
            Main.saveInLocalStorage('mid', Main.getURLvalue(2));
        }

        if(Main.getURLvalue(3) != undefined && $.isNumeric(Main.getURLvalue(3)) && (Main.getURLvalue(1) == 'store' && Main.getURLvalue(2) == 'list')) {
            Main.saveInLocalStorage('mid', Main.getURLvalue(3));
        }

    });

</script>