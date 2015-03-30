/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 2/6/15
 * Time: 9:33 AM
 * To change this template use File | Settings | File Templates.
 */
if(typeof(Order) == "undefined") var Order = {};

Order.loadOrderFn = function(){

    $('.refresh_orders').live('click', function(){
        $('.main_tabs .loaded').removeClass('loaded');
        var activeElem = $('.main_tabs .active a');
        activeElem.addClass('loaded');
        Order.getOrders("#" + activeElem.attr('data-id'), "/merchant/get_orders", {deliveryStatus: activeElem.attr('data-status')});
    });

    $('.main_tabs a[data-toggle="tab"]:not(".loaded")').on('shown.bs.tab', function() {

        $(this).addClass('loaded');
        Order.getOrders("#" + $(this).attr('data-id'), "/merchant/get_orders", {deliveryStatus: $(this).attr('data-status')});

    });
    $('.main_tabs a[data-toggle="tab"]').eq(0).tab('show');


    var postData = {};
    //postData.page = {pageNumber: 1, pageSize: 20, sortOrder: 'desc'};

/*    $("body").delegate("span.show_db_info", "mouseover", function(){
        $(this).siblings(".db_info").removeClass("hidden");
    });

    $("body").delegate(".db_td", "mouseleave", function(){
        $(this).find(".db_info").addClass("hidden");
    });
 */
    $("body").delegate("span.show_store_info", "mouseover", function(){
        $(this).siblings(".store_info").removeClass("hidden");
    });

    $("body").delegate(".store_info_td", "mouseleave", function(){
        $(this).find(".store_info").addClass("hidden");
    });

    $("body").delegate("span.view_bills", "mouseover", function(){
        $(this).siblings(".bill_list").removeClass("hidden");
    });

    $("body").delegate(".bill_td", "mouseleave", function(){
        $(this).find(".bill_list").addClass("hidden");
    });


    $('.db_td').live('mouseover', function(){
        $(this).addClass('currentPreview');
        var left = $(this).offset().left - 230;
        var top = $(this).offset().top - 130;
        var maxTop = ($(this).parents('.dataTable').eq(0).offset().top + $(this).parents('.dataTable').eq(0).height()) - 320;
        if(top > maxTop) top = maxTop;
        if($('.shopper_preview_container').css('top') == 'auto')
            $('.shopper_preview_container').css({left: left, top: top}).html($('.db_info', this).clone().html()).removeClass('hidden');
        else
            $('.shopper_preview_container').stop(false, false).animate({left: left, top: top}).html($('.db_info', this).clone().html()).removeClass('hidden');
    });
    $('.db_td').live('mouseout', function(e){
        if(!$(e.relatedTarget).hasClass('shopper_preview_container') && $(e.relatedTarget).parents('.shopper_preview_container').length == 0) {
            $('.currentPreview').removeClass('currentPreview');
            $('.shopper_preview_container').html('').addClass('hidden');
        }
    });
    $('.shopper_preview_container').live('mouseout', function(e){
        if(!$(e.relatedTarget).hasClass('currentPreview') && $(e.relatedTarget).parents('.shopper_preview_container').length == 0) {
            $('.currentPreview').removeClass('currentPreview');
            $('.shopper_preview_container').html('').addClass('hidden');
        }
    });
};

Order.getOrders = function(elemId, url, params){

    var dataFilter = function (data, type) {
        if (!data.success) {
            alert(data.message);
            return;
        }
        var responseRows = data.params.orders.numberOfRows;
        var orders = data.params.orders.data;
        var tdata = [];

        for (var i = 0; i < orders.length; i++) {
            var order = orders[i];

            var id = order.id;
            var link_attachments = '';
            if(order.attachments.length > 0){
                link_attachments += '<div class="bill_td"><span class="view_bills">View Bills</span><div class="bill_list hidden">';
                for(var j= 0; j<order.attachments.length; j++){
                    var nameArray = order.attachments[j].split("/");
                    var  nameArrayLength = nameArray.length;

                    link_attachments += '<p><a target="_blank" href="'+order.attachments[j]+'">'+nameArray[nameArrayLength-1]+'</a></p>';
                }
                link_attachments += '</div></div>';
            }
            var deliveryBoy = typeof(order.deliveryBoy) != 'undefined'?"<div class='db_td'><span class='show_db_info'>"+order.deliveryBoy.user.fullName+"</span>":'';

            if(typeof(order.deliveryBoy) != 'undefined') {
                deliveryBoy += "<div class='db_info hidden'><div class='db_image'><img src='"+order.deliveryBoy.user.profileImage+"' width='200' height='200'></div><div class='db_name'>"+order.deliveryBoy.user.fullName+"</div><div class='db_contact'>"+order.deliveryBoy.user.mobileNumber+"</div>";
                deliveryBoy += "<div class='ratings'><ul class=nav>";

                var rating = parseInt(order.deliveryBoy.averageRating);
                for(var j = 0; j < 5; j++) {
                    deliveryBoy += '<li ' + (rating > 0 ? 'class="active"' : '' ) + '></li>';
                    rating--;
                }

                deliveryBoy += "</ul></div>";
                deliveryBoy += "</div></div>";
            }

            var storeInfo = "<div class='store_info_td'><span class='show_store_info'>"+order.store.name+' - '+order.store.street+"</span>";

            var contactPerson = order.store.contactPerson != undefined?order.store.contactPerson:'';
            var contactNo = order.store.contactNo != undefined?order.store.contactNo:'';

            storeInfo += "<div class='store_info hidden'><div class='contact_person'>Contact Person: "+contactPerson+"</div><div class='contact_no'>Contact No: "+contactNo+"</div></div></div>";

            var view_items = '<span class="item_list" data-id="'+id+'" data-toggle="modal" data-target="#order_items_modal">View Item List</span>';
            var activeStatus = ["ORDER_PLACED", "ORDER_ACCEPTED", "IN_ROUTE_TO_PICK_UP", "AT_STORE", "IN_ROUTE_TO_DELIVERY"];

            var row;
            if(order.orderStatus == "CANCELLED"){
                var reason = '';
                if(order.orderCancel.reasonDetails != undefined) {
                    if(order.orderCancel.reasonDetails.id == 6){
                        reason = order.orderCancel.reason;
                    }  else{
                         reason = order.orderCancel.reasonDetails.cancelReason;
                    }
                }

                row = [i+1, order.customer.user.fullName, storeInfo, id, order.orderDate, order.orderVerificationCode, order.grandTotal != null?Main.getFromLocalStorage("currency")+order.grandTotal:'', deliveryBoy, link_attachments, (order.rating != undefined && order.rating.deliveryBoyRating != undefined)?order.rating.deliveryBoyRating:'', (order.rating != undefined && order.rating.deliveryBoyComment != undefined)?order.rating.deliveryBoyComment:'', reason, view_items];
            } else if(order.orderStatus == "DELIVERED") {
                row = [i+1, order.customer.user.fullName, storeInfo, id, order.orderDate, order.orderVerificationCode, order.grandTotal != null?Main.getFromLocalStorage("currency")+order.grandTotal:'', deliveryBoy, link_attachments, order.rating.customerRating != undefined?order.rating.customerRating:'', order.rating.customerComment != undefined?order.rating.customerComment:'', order.rating.deliveryBoyRating != undefined?order.rating.deliveryBoyRating:'', order.rating.deliveryBoyComment != undefined?order.rating.deliveryBoyComment:'', view_items];
            } else if($.inArray(order.orderStaus, activeStatus)) {
                row = [i+1, order.customer.user.fullName, storeInfo, id, order.orderDate, order.orderVerificationCode, order.grandTotal != null?Main.getFromLocalStorage("currency")+order.grandTotal:'', deliveryBoy, link_attachments, Main.ucfirst(order.orderStatus.split('_').join(' ').toLowerCase()), view_items];
            }
            row = $.extend({}, row);
            tdata.push(row)
        }

        var response = {};
        response.data = tdata;
        response.recordsTotal = responseRows;
        response.recordsFiltered = responseRows;

        return response;
    }

    dataFilter.url = url;
    dataFilter.params = params;
    if(elemId == '#order_inroute_table') {
        dataFilter.columns = [
            { "name": "" },
            { "name": "customer#user#fullName" },
            { "name": "store#name" },
            { "name": "id" },
            { "name": "orderDate" },
            { "name": "orderVerificationCode" },
            { "name": "grandTotal" },
            { "name": "deliveryBoy#user#fullName" },
            { "name": "" },
            { "name": "orderStatus" },
            { "name": "" }
        ];
    } else if(elemId == '#order_successful_table') {
        dataFilter.columns = [
            { "name": "" },
            { "name": "customer#user#fullName" },
            { "name": "store#name" },
            { "name": "id" },
            { "name": "orderDate" },
            { "name": "orderVerificationCode" },
            { "name": "grandTotal" },
            { "name": "deliveryBoy#user#fullName" },
            { "name": "" },
            { "name": "" },
            { "name": "" },
            { "name": "" },
            { "name": "" },
            { "name": "" }
        ];
    } else {
        dataFilter.columns = [
            { "name": "" },
            { "name": "customer#user#fullName" },
            { "name": "store#name" },
            { "name": "id" },
            { "name": "orderDate" },
            { "name": "orderVerificationCode" },
            { "name": "grandTotal" },
            { "name": "deliveryBoy#user#fullName" },
            { "name": "" },
            { "name": "" },
            { "name": "" },
            { "name": "" },
            { "name": "" }
        ];
    }
    Main.createDataTable(elemId, dataFilter);

    $('.dataTables_length select').attr('data-width', 'auto').selectpicker();
    if(elemId == '#order_inroute_table')
        $('#order_inroute_table_length').after('<button class="btn btn_green pull-right refresh_orders" style="line-height: 34px; padding: 0px 10px;">Refresh</button>');

};


Order.getPurchaseHistory = function(){

    var dataFilter = function (data, type) {
        if (!data.success) {
            alert(data.message);
            return;
        }

        var responseRows = data.params.orders.numberOfRows;
        var orders = data.params.orders.data;
        var tableData = [];

        for (var i = 0; i < orders.length; i++) {
            var order = orders[i];

            var id = order.id;
            var link_attachments = '';
            if(order.attachments.length > 0){
                 link_attachments += '<div class="bill_td"><span class="view_bills">View Bills</span><div class="bill_list hidden">';
                for(var j= 0; j<order.attachments.length; j++){
                    link_attachments += '<p><a href="'+order.attachments[j]+'">'+order.attachments[j]+'</a></p>';
                }
                link_attachments += '</div></div>';
            }
            var deliveryBoy = typeof(order.deliveryBoy) != 'undefined'?"<div class='db_td'><span class='show_db_info'>"+order.deliveryBoy.user.fullName+"</span>":'';
            var view_items = '<span class="item_list" data-id="'+id+'" data-toggle="modal" data-target="#order_items_modal">View Item List</span>';

            if(typeof(order.deliveryBoy) != 'undefined') {
                deliveryBoy += "<div class='db_info hidden'><div class='db_image'><img src='"+order.deliveryBoy.user.profileImage+"' width='200' height='200'></div><div class='db_name'>"+order.deliveryBoy.user.fullName+"</div><div class='db_contact'>"+order.deliveryBoy.user.mobileNumber+"</div>";
                deliveryBoy += "<div class='ratings'><ul class=nav>";

                var rating = parseInt(order.deliveryBoy.averageRating);
                for(var j = 0; j < 5; j++) {
                    deliveryBoy += '<li ' + (rating > 0 ? 'class="active"' : '' ) + '></li>';
                    rating--;
                }

                deliveryBoy += "</ul></div>";
                deliveryBoy += "</div></div>";
            }



            var row = [i+1, order.customer.user.fullName, order.store.name+' - '+order.store.street+'', id, order.orderDate, order.grandTotal != null?Main.getFromLocalStorage("currency")+order.grandTotal:'', deliveryBoy, link_attachments, view_items, ''];
            row = $.extend({}, row);
            tableData.push(row);

        }

        var response = {};
        response.data = tableData;
        response.recordsTotal = responseRows;
        response.recordsFiltered = responseRows;

        return response;

    }

    dataFilter.url = "/merchant/get_purchase_history";
    dataFilter.columns = [
        { "name": "" },
        { "name": "customer#user#fullName" },
        { "name": "store#name" },
        { "name": "id" },
        { "name": "orderDate" },
        { "name": "grandTotal" },
        { "name": "deliveryBoy#user#fullName" },
        { "name": "" },
        { "name": "" },
        { "name": "" }
    ];
    dataFilter.headers = {merchantId:Main.getFromLocalStorage('mid')};
    Main.createDataTable("#purchase_history_table", dataFilter);

    $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

/*   $("body").delegate("span.show_db_info", "mouseover", function(){
         $(this).siblings(".db_info").removeClass("hidden");
   });

    $("body").delegate(".db_td", "mouseleave", function(){
        $(this).find(".db_info").addClass("hidden");
    });
    $("body").delegate("span.view_bills", "mouseover", function(){
        $(this).siblings(".bill_list").removeClass("hidden");
    });

    $("body").delegate(".bill_td", "mouseleave", function(){
        $(this).find(".bill_list").addClass("hidden");
    });*/

    $('.db_td').live('mouseover', function(){
        $(this).addClass('currentPreview');
        var left = $(this).offset().left - 230;
        var top = $(this).offset().top - 130;
        var maxTop = ($(this).parents('.dataTable').eq(0).offset().top + $(this).parents('.dataTable').eq(0).height()) - 320;
        if(top > maxTop) top = maxTop;
        if($('.shopper_preview_container').css('top') == 'auto')
            $('.shopper_preview_container').css({left: left, top: top}).html($('.db_info', this).clone().html()).removeClass('hidden');
        else
            $('.shopper_preview_container').stop(false, false).animate({left: left, top: top}).html($('.db_info', this).clone().html()).removeClass('hidden');
    });
    $('.db_td').live('mouseout', function(e){
        if(!$(e.relatedTarget).hasClass('shopper_preview_container') && $(e.relatedTarget).parents('.shopper_preview_container').length == 0) {
            $('.currentPreview').removeClass('currentPreview');
            $('.shopper_preview_container').html('').addClass('hidden');
        }
    });
    $('.shopper_preview_container').live('mouseout', function(e){
        if(!$(e.relatedTarget).hasClass('currentPreview') && $(e.relatedTarget).parents('.shopper_preview_container').length == 0) {
            $('.currentPreview').removeClass('currentPreview');
            $('.shopper_preview_container').html('').addClass('hidden');
        }
    });

}


Order.getOrdersItems = function(){

    var dataFilter = function (data, type) {

        if (!data.success) {
            alert(data.message);
            return;
        }
        var responseRows = data.params.items.numberOfRows;
        var items = data.params.items.data;
        var tableData = [];
        for (var i = 0; i < items.length; i++) {
            var item = items[i];

            var row = [i+1, item.item.name, item.quantity, item.serviceCharge, item.vat, item.itemTotal];
            row = $.extend({}, row);
            tableData.push(row);

        }

        var response = {};
        response.data = tableData;
        response.recordsTotal = responseRows;
        response.recordsFiltered = responseRows;

        return response;
    };

    $("#order_items_modal").on('show.bs.modal', function(e){

        dataFilter.url = "/merchant/get_orders_items";
        dataFilter.columns = [
            { "name": "" },
            { "name": "item.name" },
            { "name": "quantity" },
            { "name": "serviceCharge" },
            { "name": "vat" },
            { "name": "itemTotal" }
        ];
        dataFilter.headers = {id:$(e.relatedTarget).data('id')};
        Main.createDataTable("#orders_items_table", dataFilter);

        $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

    });

}


Order.courierBoyOrderHistory = function(params){

    var dataFilter = function (data, type) {
        if (!data.success) {
            alert(data.message);
            return;
        }
        var responseRows = data.params.orders.numberOfRows;
        var orders = data.params.orders.data;

        var tableData = [];
        var unpaid_total = 0;
        if(orders.length > 0) {
            var rating = parseInt(orders[0].deliveryBoy.averageRating);

            $(".courier_name").html(orders[0].deliveryBoy.user.fullName != undefined? orders[0].deliveryBoy.user.fullName+' - ':'');
            for (var i = 0; i < orders.length; i++) {
                var order = orders[i];

                var action = '';
                var orderHistoryLength =  order.dBoyOrderHistories.length;
                var earned_amount = order.dBoyOrderHistories[orderHistoryLength-1].amountEarned;
                var time_taken;
                if(order.dBoyOrderHistories[orderHistoryLength-1].orderCompletedAt != undefined && order.dBoyOrderHistories[orderHistoryLength-1].jobStartedAt != undefined){
                    time_taken = ((order.dBoyOrderHistories[orderHistoryLength-1].orderCompletedAt - order.dBoyOrderHistories[orderHistoryLength-1].jobStartedAt)/1000/60).toFixed(0);
                }

                if(order.dBoyPaid != undefined){
                    var checkBox = '';
//                    var checkBox = '<input type="checkbox" checked data-id="'+order.id+'" class="pay_row">';
                }else{
                    var checkBox = '<input type="checkbox" data-id="'+order.id+'" class="pay_row">';
                    unpaid_total += earned_amount;
                }

                var row = [i+1, order.orderDate, order.id, order.customer.user.fullName, order.orderName, order.dBoyOrderHistories[orderHistoryLength-1].distanceTravelled+'KM', Main.ucfirst(order.deliveryStatus), '<span class="' + (order.dBoyPaid == undefined ? "paid_status unpaid_amount" : '') + '">' + earned_amount + '</span>', order.assignedTime+'Min', time_taken!=undefined?time_taken+'Min':'', order.rating.customerRating != undefined?order.rating.customerRating:'', order.rating.customerComment != undefined?order.rating.customerComment:'', order.rating.deliveryBoyRating != undefined?order.rating.deliveryBoyRating:'', order.rating.deliveryBoyComment != undefined?order.rating.deliveryBoyComment:'', order.dBoyPaidDate, checkBox];
                tableData.push(row);
            }
            $('.unpaid_total').html(unpaid_total.toFixed(2));
        } else {
            var rating = 0;
        }

        for(var i = 0; i < rating; i++) {
            $(".heading .ratings ul li").eq(i).addClass('active');
        }

        var response = {};
        response.data = tableData;
        response.recordsTotal = responseRows;
        response.recordsFiltered = responseRows;

        return response;
    }
    var headers = {};
    headers.id = Main.getURLvalue(3);

    dataFilter.url = "/dboy/get_dBoy_order_history";
    dataFilter.columns = [
        { "name": "" },
        { "name": "orderDate" },
        { "name": "id" },
        { "name": "customer#user#fullName" },
        { "name": "orderName" },
        { "name": "dBoyOrderHistories#distanceTravelled" },
        { "name": "deliveryStatus" },
        { "name": "dBoyOrderHistories#amountEarned" },
        { "name": "assignedTime" },
        { "name": "" },
        { "name": "" },
        { "name": "" },
        { "name": "" },
        { "name": "" },
        { "name": "dBoyPaidDate" },
        { "name": "" }
    ];
    dataFilter.headers = headers;
    if(params != undefined)
        dataFilter.params = {dateRange: params};
    Main.createDataTable("#courier_history_table", dataFilter);

    $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

}

Order.getInvoices = function(params){

    var dataFilter = function (data, type) {
        if (!data.success) {
            alert(data.message);
            return;
        }

        var responseRows = data.params.invoices.numberOfRows;
        var invoices = data.params.invoices.data;
        var unpaid_total = 0;

        var tableData = [];
        for (var i = 0; i < invoices.length; i++) {
            var invoice = invoices[i];
            var invoice_amount = invoice.amount;
            var link = '<a target="_blank" href="'+invoice.path+'">View Invoice</a>';
            if(invoice.invoicePaid != undefined){
                var checkBox = '';
//                var checkBox = '<input type="checkbox" checked data-id="'+invoice.id+'" class="pay_row">';
            }else{
                var checkBox = '<input type="checkbox" data-id="'+invoice.id+'" class="pay_row">';
                unpaid_total += invoice_amount;
            }

            var row = [invoice.id, invoice.store.storesBrand.brandName+"("+invoice.store.street+")", invoice.generatedDate, '<span class="' + (invoice.invoicePaid == undefined ? "paid_status unpaid_amount" : '') + '">' + invoice_amount + '</span>', invoice.fromDate, invoice.toDate, invoice.paidDate!=undefined?invoice.paidDate:'', link, checkBox];
            row = $.extend({}, row);
            tableData.push(row);
        }
        $('.unpaid_total').html(unpaid_total.toFixed(2));

        var response = {};
        response.data = tableData;
        response.recordsTotal = responseRows;
        response.recordsFiltered = responseRows;

        return response;
    }

    var header = {};
    header.merchantId = Main.getFromLocalStorage('mid');
    dataFilter.url = "/merchant/get_invoices";
    dataFilter.columns = [
        { "name": "id" },
        { "name": "store#storesBrand#brandName" },
        { "name": "generatedDate" },
        { "name": "amount" },
        { "name": "fromDate" },
        { "name": "toDate" },
        { "name": "paidDate" },
        { "name": "" },
        { "name": "" }
    ];
    dataFilter.headers = header;
    if(params != undefined)
        dataFilter.params = {dateRange: params};
    Main.createDataTable("#invoices_table", dataFilter);

    $('.dataTables_length select').attr('data-width', 'auto').selectpicker();
}