<div class="tooltip_conatiner"></div>
<div class="sidebar" id="sidebar_menu">
    <div class="clearfix">
        <button type="button" class="navbar-toggle collapsed menu_toggle" data-menu="#sidebar_menu" data-body=".main_container">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
    </div>
    <div class="sidebar_inner">
        <div class="sidebar_logo">
            <a href="/merchant/store/list"><img src="${pageContext.request.contextPath}/resources/images/logo.png" class="img-responsive" /></a>
        </div>
        <div class="sidebar_menu">
            <sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')">
                <ul class="nav nav-stacked">
                    <li><a href="/organizer/dashboard" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Admin Dashboard"><span class="delivricon delivricon-dashboard"></span><span class="menu_text">Admin Dashboard</span></a></li>
                    <li><a href="javascript:;" class="ignore_focus elem_tooltip merchant_title" data-container=".tooltip_conatiner" data-placement="right" title="Merchant Name"><span class="delivricon delivricon-merchant"></span><span class="menu_text merchant_name"></span></a>
            </sec:authorize>
            <sec:authorize ifAnyGranted="ROLE_ACCOUNTANT">
                    <ul class="nav nav-stacked">
                    <li><a href="/accountant/merchants" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Accountant Merchants"><span class="delivricon delivricon-merchant"></span><span class="menu_text">Merchants</span></a>
            </sec:authorize>
                        <ul class="nav nav-stacked">
                            <%--<li><a class="merchant_name ignore_focus" href="javascript:;">Merchant Name</a></li>--%>
                            <li><a href="/merchant/store/list" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Stores"><span class="delivricon delivricon-store"></span><span class="menu_text">Stores</span></a></li>
                            <li><a href="/merchant/item/list" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Items"><span class="delivricon delivricon-item"></span><span class="menu_text">Items</span></a></li>
                            <%--<li><a href="/merchant/orders" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Orders"><span class="delivricon delivricon-orders"></span><span class="menu_text">Orders</span></a></li>--%>
                            <li><a href="/merchant/invoices" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Statements"><span class="delivricon delivricon-invoice"></span><span class="menu_text">Statements</span></a></li>
                            <li><a href="/merchant/purchase_history" class="elem_tooltip" data-container=".tooltip_conatiner" data-placement="right" title="Purchase History"><span class="delivricon delivricon-purchase-history"></span><span class="menu_text">Purchase History</span></a></li>
                        </ul>
            <sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_ACCOUNTANT')">
                    </li>
                </ul>
            </sec:authorize>
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
        if(Main.getFromLocalStorage('mid') != undefined) {
            if(!sess_merchants[Main.getFromLocalStorage('mid')]) {
                Main.clearLocalStorage('merchants');
                window.location.reload();
            }
            var btitile = sess_merchants[Main.getFromLocalStorage('mid')].businessTitle;
            $('.sidebar .merchant_name').html(btitile);
            $(".sidebar .merchant_title").attr({"data-original-title": btitile});

            Main.saveInLocalStorage('userStatus', sess_merchants[Main.getFromLocalStorage('mid')].status)

            if(sess_merchants[Main.getFromLocalStorage('mid')].status == "INACTIVE") {

                if(Main.getURLvalue(3) == 'create' && (Main.getURLvalue(1) == 'item' || Main.getURLvalue(1) == 'store')) {
                    form_submit = true;
                    window.location = Main.modifyURL('merchant/' + Main.getURLvalue(1) + '/list');
                }
                $('a[href*="/merchant/store/form/create"], a[href*="/merchant/item/form/create"]', '.body').addClass('disabled');

            }
        }

        $('.sidebar_menu a[href]').not('[href="javascript:;"]').each(function(){
            $(this).attr('href', Main.modifyURL($(this).attr('href')));
        });

    });

</script>