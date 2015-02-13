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

            var row = [i+1, order.customer.user.fullName, order.store.name+' - '+order.store.street+'', id, order.grandTotal != null?order.grandTotal:'', deliveryBoyName, link_attachments, action];
            if(order.deliveryStatus == "CANCELLED"){
                tdataCanceled.push(row);
            } else if(order.deliveryStatus == "SUCCESSFUL") {
                tdataSuccessful.push(row)
            } else if(order.deliveryStatus == "PENDING") {
                tdataInRoute.push(row)
            }
        }

        Main.createDataTable("#order_inroute_table", tdataInRoute);
        Main.createDataTable("#order_canceled_table", tdataCanceled);
        Main.createDataTable("#order_successful_table", tdataSuccessful);
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
                for(var j= 0; j<order.attachments.length; i++){
                    link_attachments += '<a href="'+order.attachments[j]+'">'+order.attachments[j]+'</a>';
                }
            }
            var deliveryBoyName = typeof(order.deliveryBoy) != 'undefined'?order.deliveryBoy.user.fullName:'';


            var row = [i+1, order.customer.user.fullName, order.store.name+' - '+order.store.street+'', id, order.grandTotal != null?order.grandTotal:'', deliveryBoyName, link_attachments, action];
            tableData.push(row);

        }

        Main.createDataTable("#purchase_history_table", tableData);
    }

    var postData = {};
    //postData.page = {pageNumber: 1, pageSize: 20, sortOrder: 'desc'};
    Main.request('/merchant/get_purchase_history', postData, callback);
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
        var tableData = [];
        for (var i = 0; i < orders.length; i++) {
            var order = orders[i];

            var id = order.id;
            var link_attachments = '';
            if(order.attachments.length > 0){
                for(var j= 0; j<order.attachments.length; i++){
                    link_attachments += '<a href="'+order.attachments[j]+'">'+order.attachments[j]+'</a>';
                }
            }
            var deliveryBoyName = typeof(order.deliveryBoy) != 'undefined'?order.deliveryBoy.user.fullName:'';
            var action = '';

            var row = [i+1, order.customer.user.fullName, order.store.name+' - '+order.store.street+'', id, order.grandTotal != null?order.grandTotal:'', deliveryBoyName, link_attachments, action];
            tableData.push(row);

        }

        Main.createDataTable("#courier_history_table", tableData);
    }
    var header = {};
    header.id = Main.getURLvalue(3);

    console.log(header);

    Main.request('/dboy/get_dBoy_order_history', {}, callback, header);
}