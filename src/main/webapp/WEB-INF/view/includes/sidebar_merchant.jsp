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
            <li><a href="/merchant/dashboard" class="merchant_menu">Dashboard</a></li>
            <li><a href="/merchant/store/list" class="merchant_menu">Stores</a></li>
            <li><a href="/merchant/item/list" class="merchant_menu">Items</a></li>
            <li><a href="#" class="merchant_menu">Purchase History</a></li>
            <li><a href="#" class="merchant_menu">Invoices</a></li>
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
        function remove_value(value, remove) {
            if(value.indexOf(remove) > -1) {
                value.splice(value.indexOf(remove), 1);
                remove_value(value, remove);
            }
            return value;
        }
        var path_arr = pathname.split('/');
        path_arr = remove_value(path_arr, "");
        var path_arr = ["merchant", "dashboard", "idm_2", "idm_2", "idm_2"];
        var idm_ind = 0;
        var idm_ind = -1;
        for(i = 0; i < path_arr.length; i++) {
            if(/idm_/g.test(path_arr[i])) {
                idm_ind = i;
                break;
            }
        }
        if(idm_ind != -1) {
            // update menu url
        }

    });

</script>