<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div class="sidebar">
    <div class="sidebar_logo">
        <a href="/admin/dashboard"><img src="/resources/images/delivr-logo.png" class="img-responsive" /></a>
    </div>
    <div class="sidebar_menu">
        <ul class="nav nav-stacked">
            <sec:authorize access="hasRole('ROLE_ADMIN')">
                <li><a href="/admin/dashboard">Admin Dashboard</a></li>
            </sec:authorize>
            <li><a href="/merchant/dashboard" class="merchant_link">Dashboard</a></li>
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
        function selectMenu() {
            $('.sidebar_menu li a[href="' + pathname + '"]').addClass('active');

            $('.sidebar_menu li a').click(function(){
                $('.sidebar_menu li a').removeClass('active');
                $(this).addClass('active');
            });
        }
        selectMenu();
        var idm_param = Main.getURLParameter('idm');
        if(idm_param != undefined) {
            $('.merchant_link').each(function(){
                var href = $(this).attr('href');
                $(this).attr('href', href + (href.lastIndexOf('/') == href.length - 1 ? "" : "/") + idm_param);
            });
            selectMenu();
        }

    });

</script>