/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 2/6/15
 * Time: 9:33 AM
 * To change this template use File | Settings | File Templates.
 */
if(typeof(Order) == "undefined") var Order = {};

Order.getOrders = function(){

    $(".main_tabs li").click(function(){
        $(".main_tabs li").removeClass("active");
        $(this).addClass("active");
        $(".table-view").addClass("hidden");
        $(".table-view."+$(this).data("ref")).removeClass("hidden");
    });

    var callback = function(success, data){
        if (!data.success) {
            alert(data.message);
            return;
        }
        var orders = data.params.orders.data;
        var tdataInRoute = [];
        var tdataSuccessful = [];
        var tdataCanceled = [];

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

            if(typeof(order.deliveryBoy) != 'undefined')
                deliveryBoy += "<div class='db_info hidden'><div class='db_image'><img src='"+order.deliveryBoy.user.profileImage+"' width='200' height='200'></div><div class='db_name'>Name: "+order.deliveryBoy.user.fullName+"</div><div class='db_contact'>Contact: "+order.deliveryBoy.user.mobileNumber+"</div></div></div>";

            var storeInfo = "<div class='store_info_td'><span class='show_store_info'>"+order.store.name+' - '+order.store.street+"</span>";

            var contactPerson = order.store.contactPerson != undefined?order.store.contactPerson:'';
            var contactNo = order.store.contactNo != undefined?order.store.contactNo:'';

            storeInfo += "<div class='store_info hidden'><div class='contact_person'>Contact Person: "+contactPerson+"</div><div class='contact_no'>Contact No: "+contactNo+"</div></div></div>";

            var view_items = '<span class="item_list" data-id="'+id+'">View Item List</span>';
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

                row = [i+1, order.customer.user.fullName, storeInfo, id, order.grandTotal != null?Main.getFromLocalStorage("currency")+order.grandTotal:'', deliveryBoy, link_attachments, (order.rating != undefined && order.rating.deliveryBoyRating != undefined)?order.rating.deliveryBoyRating:'', (order.rating != undefined && order.rating.deliveryBoyComment != undefined)?order.rating.deliveryBoyComment:'', reason];
                tdataCanceled.push(row);
            } else if(order.orderStatus == "DELIVERED") {
                row = [i+1, order.customer.user.fullName, storeInfo, id, order.grandTotal != null?Main.getFromLocalStorage("currency")+order.grandTotal:'', deliveryBoy, link_attachments, order.rating.customerRating != undefined?order.rating.customerRating:'', order.rating.customerComment != undefined?order.rating.customerComment:'', order.rating.deliveryBoyRating != undefined?order.rating.deliveryBoyRating:'', order.rating.deliveryBoyComment != undefined?order.rating.deliveryBoyComment:''];
                tdataSuccessful.push(row)
            } else if($.inArray(order.orderStaus, activeStatus)) {
                row = [i+1, order.customer.user.fullName, storeInfo, id, order.grandTotal != null?Main.getFromLocalStorage("currency")+order.grandTotal:'', deliveryBoy, link_attachments, Main.ucfirst(order.orderStatus.split('_').join(' ').toLowerCase()), view_items];
                tdataInRoute.push(row)
            }
        }

        Main.createDataTable("#order_inroute_table", tdataInRoute);
        Main.createDataTable("#order_canceled_table", tdataCanceled);
        Main.createDataTable("#order_successful_table", tdataSuccessful);
        $('.dataTables_length select').attr('data-width', 'auto').selectpicker();
    }
    callback.loaderDiv = ".main_content";
    var postData = {};
    //postData.page = {pageNumber: 1, pageSize: 20, sortOrder: 'desc'};

    $("body").delegate("span.show_db_info", "mouseover", function(){
        $(this).siblings(".db_info").removeClass("hidden");
    });

    $("body").delegate(".db_td", "mouseleave", function(){
        $(this).find(".db_info").addClass("hidden");
    });

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


    Main.request('/merchant/get_orders', postData, callback);
}


Order.getPurchaseHistory = function(){
    var callback = function(success, data){
        if (!data.success) {
            alert(data.message);
            return;
        }

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
            var view_items = '<span class="item_list" data-id="'+id+'">View Item List</span>';

            if(typeof(order.deliveryBoy) != 'undefined')
                deliveryBoy += "<div class='db_info hidden'><div class='db_image'><img src='"+order.deliveryBoy.user.profileImage+"' width='200' height='200'></div><div class='db_name'>Name: "+order.deliveryBoy.user.fullName+"</div><div class='db_contact'>Contact: "+order.deliveryBoy.user.mobileNumber+"</div></div></div>";



            var row = [i+1, order.customer.user.fullName, order.store.name+' - '+order.store.street+'', id, order.grandTotal != null?Main.getFromLocalStorage("currency")+order.grandTotal:'', deliveryBoy, link_attachments, view_items, ''];
            tableData.push(row);

        }

        Main.createDataTable("#purchase_history_table", tableData);

        $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

    }
    callback.loaderDiv = ".main_content";

    var postData = {};
    //postData.page = {pageNumber: 1, pageSize: 20, sortOrder: 'desc'};
    Main.request('/merchant/get_purchase_history', postData, callback, {merchantId:Main.getFromLocalStorage('mid')});



   $("body").delegate("span.show_db_info", "mouseover", function(){
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
    });

}


Order.getOrdersItems = function(){
    $("body").delegate(".item_list", "click", function(){
        var callback = function(success, data){
            if (!data.success) {
                alert(data.message);
                return;
            }
            var items = data.params.items;
            var tableData = [];
            for (var i = 0; i < items.length; i++) {
                var item = items[i];

                var row = [i+1, item.item.name, item.quantity, item.serviceCharge, item.vat, item.itemTotal];
                tableData.push(row);

            }

            Main.createDataTable("#orders_items_table", tableData);
            $('.dataTables_length select').attr('data-width', 'auto').selectpicker();
            $("#order_items_modal").modal("show");
        }

        callback.requestType = "GET";

        Main.request('/merchant/get_orders_items', {}, callback, {id:$(this).data('id')});
    });
}


Order.courierBoyOrderHistory = function(){
    var callback = function(success, data){
        if (!data.success) {
            alert(data.message);
            return;
        }
        var orders = data.params.orders;
        $(".courier_name").html(orders[0].deliveryBoy.user.fullName != undefined? orders[0].deliveryBoy.user.fullName+' - ':'');
        var tableData = [];
        for (var i = 0; i < orders.length; i++) {
            var order = orders[i];

            var action = '';
            var orderHistoryLength =  order.dBoyOrderHistories.length;
            var time_taken;
            if(order.dBoyOrderHistories[orderHistoryLength-1].orderCompletedAt != undefined && order.dBoyOrderHistories[orderHistoryLength-1].orderAcceptedAt != undefined){
                time_taken = ((order.dBoyOrderHistories[orderHistoryLength-1].orderCompletedAt - order.dBoyOrderHistories[orderHistoryLength-1].orderAcceptedAt)/1000/60).toFixed(0);
            }

            var row = [i+1, order.deliveryBoy.user.fullName, order.orderDate, order.id, order.customer.user.fullName, order.orderName, order.dBoyOrderHistories[orderHistoryLength-1].distanceTravelled+'KM', order.deliveryStatus, order.dBoyOrderHistories[orderHistoryLength-1].amountEarned, order.assignedTime+'Min', time_taken!=undefined?time_taken+'Min':'', order.rating.customerRating != undefined?order.rating.customerRating:'', order.rating.customerComment != undefined?order.rating.customerComment:'', order.rating.deliveryBoyRating != undefined?order.rating.deliveryBoyRating:'', order.rating.deliveryBoyComment != undefined?order.rating.deliveryBoyComment:''];
            tableData.push(row);
        }

        Main.createDataTable("#courier_history_table", tableData);
    }
    callback.loaderDiv = ".main_content";
    var header = {};
    header.id = Main.getURLvalue(3);

    Main.request('/dboy/get_dBoy_order_history', {}, callback, header);

}