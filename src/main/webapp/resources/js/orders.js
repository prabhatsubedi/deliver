/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 2/6/15
 * Time: 9:33 AM
 * To change this template use File | Settings | File Templates.
 */
if(typeof(Order) == "undefined") var Order = {};

Order.getOrders = function(){

    $(".settings_menu li").click(function(){
        $(".settings_menu li").removeClass("current_page");
        $(this).addClass("current_page");
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
                for(var j= 0; j < order.attachments.length; j++){
                    if(order.attachments[j] != "")
                        link_attachments += '<p><a href="'+order.attachments[j]+'">'+order.attachments[j]+'</a></p>';
                }
            }
            var deliveryBoyName = typeof(order.deliveryBoy) != 'undefined'?order.deliveryBoy.user.fullName:'';
            var action = '<span class="item_list" data-id="'+id+'">View Item List</span>';
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

                row = [i+1, order.customer.user.fullName, order.store.name+' - '+order.store.street+'', id, order.grandTotal != null?Main.getFromLocalStorage("currency")+order.grandTotal:'', deliveryBoyName, link_attachments, (order.rating != undefined && order.rating.deliveryBoyRating != undefined)?order.rating.deliveryBoyRating:'', (order.rating != undefined && order.rating.deliveryBoyComment != undefined)?order.rating.deliveryBoyComment:'', reason];
                tdataCanceled.push(row);
            } else if(order.orderStatus == "DELIVERED") {
                row = [i+1, order.customer.user.fullName, order.store.name+' - '+order.store.street+'', id, order.grandTotal != null?Main.getFromLocalStorage("currency")+order.grandTotal:'', deliveryBoyName, link_attachments, order.rating.customerRating != undefined?order.rating.customerRating:'', order.rating.customerComment != undefined?order.rating.customerComment:'', order.rating.deliveryBoyRating != undefined?order.rating.deliveryBoyRating:'', order.rating.deliveryBoyComment != undefined?order.rating.deliveryBoyComment:''];
                tdataSuccessful.push(row)
            } else if($.inArray(order.orderStaus, activeStatus)) {
                row = [i+1, order.customer.user.fullName, order.store.name+' - '+order.store.street+'', id, order.grandTotal != null?Main.getFromLocalStorage("currency")+order.grandTotal:'', deliveryBoyName, link_attachments, Main.ucfirst(order.orderStatus.split('_').join(' ').toLowerCase()), action];
                tdataInRoute.push(row)
            }
        }

        Main.createDataTable("#order_inroute_table", tdataInRoute);
        Main.createDataTable("#order_canceled_table", tdataCanceled);
        Main.createDataTable("#order_successful_table", tdataSuccessful);
        $('.dataTables_length select').attr('data-width', 'auto').selectpicker();
    }

    var postData = {};
    //postData.page = {pageNumber: 1, pageSize: 20, sortOrder: 'desc'};


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
                 link_attachments += '<span class="view_bills">View Bills</span><div class="bill_list hidden">';
                for(var j= 0; j<order.attachments.length; j++){
                    link_attachments += '<p><a href="'+order.attachments[j]+'">'+order.attachments[j]+'</a></p>';
                }
                link_attachments += '</div>';
            }
            var deliveryBoyName = typeof(order.deliveryBoy) != 'undefined'?"<span class='show_db_info'>"+order.deliveryBoy.user.fullName+"</span>":'';
            var view_items = '<span class="item_list" data-id="'+id+'">View Item List</span>';

            var db_info = "";
            if(typeof(order.deliveryBoy) != 'undefined')
                 db_info = "<div class='db_info hidden'><div class='db_image'><img src='"+order.deliveryBoy.user.profileImage+"' width='200' height='200'></div><div class='db_name'>"+order.deliveryBoy.user.fullName+"</div><div class='db_contact'>Contact: "+order.deliveryBoy.user.mobileNumber+"</div></div>";
            else
                db_info = "<div class='db_info hidden'></div>";


            var row = [i+1, order.customer.user.fullName, order.store.name+' - '+order.store.street+'', id, order.grandTotal != null?Main.getFromLocalStorage("currency")+order.grandTotal:'', deliveryBoyName+db_info, link_attachments, view_items, ''];
            tableData.push(row);

        }

        Main.createDataTable("#purchase_history_table", tableData);

        $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

    }

    var postData = {};
    //postData.page = {pageNumber: 1, pageSize: 20, sortOrder: 'desc'};
    Main.request('/merchant/get_purchase_history', postData, callback, {merchantId:Main.getFromLocalStorage('mid')});



   $("body").delegate("span.show_db_info", "mouseover", function(){
         $(this).siblings(".db_info").removeClass("hidden");
         $(this).addClass("hidden");
   });

    $("body").delegate(".db_info", "mouseout", function(){
        $(this).siblings("span.show_db_info").removeClass("hidden");
        $(this).addClass("hidden");
    });

    $("body").delegate("span.view_bills", "mouseover", function(){
        $(this).siblings(".bill_list").removeClass("hidden");
        $(this).addClass("hidden");
    });

    $("body").delegate(".bill_list", "mouseout", function(){
        $(this).siblings("span.view_bills").removeClass("hidden");
        $(this).addClass("hidden");
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



            var row = [i+1, order.deliveryBoy.user.fullName, order.orderDate, order.id, order.customer.user.fullName, order.orderName, order.dBoyOrderHistories[orderHistoryLength-1].distanceTravelled+'KM', order.deliveryStatus, order.dBoyOrderHistories[orderHistoryLength-1].amountEarned, order.assignedTime+'Min', ((order.dBoyOrderHistories[orderHistoryLength-1].orderCompletedAt - order.dBoyOrderHistories[orderHistoryLength-1].orderAcceptedAt)/1000/60).toFixed(0)+'Min', order.rating.customerRating != undefined?order.rating.customerRating:'', order.rating.customerComment != undefined?order.rating.customerComment:'', order.rating.deliveryBoyRating != undefined?order.rating.deliveryBoyRating:'', order.rating.deliveryBoyComment != undefined?order.rating.deliveryBoyComment:'', view_items, action];
            tableData.push(row);
        }

        Main.createDataTable("#courier_history_table", tableData);
    }
    var header = {};
    header.id = Main.getURLvalue(3);

    console.log(header);

    Main.request('/dboy/get_dBoy_order_history', {}, callback, header);

}