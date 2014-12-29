<%--
  Created by IntelliJ IDEA.
  User: Lunek
  Date: 11/25/2014
  Time: 12:05 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<link href="/resources/css/bootstrap.css" rel="stylesheet" type="text/css" media="screen" />
<link href="/resources/css/bootstrap-select.min.css" rel="stylesheet" type="text/css" media="screen" />
<link href="/resources/css/style.css" rel="stylesheet" type="text/css" media="screen" />
<%--    <link rel="shortcut icon" href="/resources/images/favicon.ico" type="image/x-icon" />
    <link rel="icon" href="/resources/images/favicon.ico" type="image/x-icon" />--%>
<script type="text/javascript" src="/resources/js/jquery-2.1.1.min.js"></script>
<script type="text/javascript" src="/resources/js/jquery-migrate-1.2.1.min.js"></script>
<script type="text/javascript" src="/resources/js/jquery.validate.js"></script>
<script type="text/javascript" src="/resources/js/bootstrap.js"></script>
<script type="text/javascript" src="/resources/js/bootstrap-select.js"></script>
<script type="text/javascript" src="/resources/js/main.js"></script>
<script type="text/javascript">
    $(document).ready(function(){

        $('#item_categories').selectpicker();
        $('#item_stores').selectpicker();


/*        $('select.haveall').live('change', function(){
            if($(this).val() != null && $('option', this).length == $(this).val().length + 1)
                $(this).selectpicker('selectAll');
        });

        $('select.compelselection').live('change', function(){
            if($(this).val() == null) $(this).selectpicker('selectAll');
        });*/

    });
</script>
