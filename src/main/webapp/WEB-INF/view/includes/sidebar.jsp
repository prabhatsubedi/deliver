<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div class="sidebar">
    <div class="sidebar_logo">
        <a href="/organizer/dashboard"><img src="/resources/images/delivr-logo.png" class="img-responsive" /></a>
    </div>
    <div class="sidebar_menu">

        <sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')">
            <%@include file="../includes/sidebar_organizer.jsp" %>
        </sec:authorize>
        <sec:authorize access="hasRole('ROLE_MERCHANT')">
            <%@include file="../includes/sidebar_merchant.jsp" %>
        </sec:authorize>

    </div>
</div>


<script type="text/javascript">

    $(document).ready(function(){

        var pathname = window.location.pathname;
        $('.sidebar_menu li a[href="' + pathname + '"]').addClass('active');

        $('.sidebar_menu li a').click(function(){
            $('.sidebar_menu li a').removeClass('active');
            $(this).addClass('active');
        });

    });

</script>