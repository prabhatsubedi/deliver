<div class="sidebar">
    <div class="sidebar_logo">
        <a href="/merchant/dashboard"><img src="${pageContext.request.contextPath}/resources/images/delivr-logo.png" class="img-responsive" /></a>
    </div>
    <div class="sidebar_menu">
        <sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')">
            <ul class="nav nav-stacked">
                <li><a href="/organizer/dashboard">Admin Dashboard</a></li>
                <li><a class="merchant_name ignore_focus" href="javascript:;">Merchant Name</a>
        </sec:authorize>
                    <ul class="nav nav-stacked">
                        <sec:authorize access="!hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')">
                        <%--<li><a class="merchant_name ignore_focus" href="javascript:;">Merchant Name</a></li>--%>
                        </sec:authorize>
                        <li><a href="/merchant/store/list">Stores</a></li>
                        <li><a href="/merchant/item/list">Items</a></li>
                        <li><a href="#">Purchase History</a></li>
                        <li><a href="#">Invoices</a></li>
                    </ul>
        <sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')">
                </li>
            </ul>
        </sec:authorize>
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

        $('.sidebar_menu li a').not('.ignore_focus').click(function(){
            $('.sidebar_menu li a').removeClass('active');
            $(this).addClass('active');
        });

        if(Main.getURLvalue(2) != undefined && $.isNumeric(Main.getURLvalue(2)) && Main.getURLvalue(1) == 'profile') {
            Main.saveInLocalStorage('mid', Main.getURLvalue(2));
        }

        if(Main.getURLvalue(3) != undefined && $.isNumeric(Main.getURLvalue(3)) && (Main.getURLvalue(1) == 'store' && Main.getURLvalue(2) == 'list')) {
            Main.saveInLocalStorage('mid', Main.getURLvalue(3));
        }

        var sess_merchants = JSON.parse(Main.getFromLocalStorage('merchants'));
        $('.sidebar .merchant_name').html(sess_merchants[Main.getFromLocalStorage('mid')]);

        $('.sidebar_menu a[href]').not('[href="#"], [href="javascript:;"]').each(function(){
            $(this).attr('href', Main.modifyURL($(this).attr('href')));
        });

    });

</script>