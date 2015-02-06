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

        for (i = 0; i < orders.length; i++) {
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
