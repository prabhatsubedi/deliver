<ul class="nav nav-stacked">
    <sec:authorize access="hasRole('ROLE_ADMIN')">
        <li><a href="/organizer/dashboard">Admin Dashboard</a></li>
    </sec:authorize>
    <li><a href="/merchant/dashboard" class="merchant_link">Dashboard</a></li>
    <li><a href="/merchant/store/list" class="merchant_link">Stores</a></li>
    <li><a href="/merchant/item/list" class="merchant_link">Items</a></li>
    <li><a href="#">Purchase History</a></li>
    <li><a href="#">Invoices</a></li>
</ul>