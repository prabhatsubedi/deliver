/**
 * Created by Lunek on 12/3/2014.
 */


if(typeof(Manager) == "undefined") var Manager = {};

(function ($){

    Manager.getMerchants = function() {

        var callback = function (status, data) {

            console.log(data);
            if(!data.success){
                alert(data.message);
                return;
            }
            var merchants = data.params.merchants;
            var tdata = [];

            for(i = 0; i < merchants.length; i++){
                var merchant = merchants[i];

                var merchantId = merchant.id;
                var userId = merchant.user.id;
                var status = merchant.userStatus;
                var link_activation = "", link_profile = "";

                if(status == "VERIFIED") {
                    link_activation = '<a href="#" data-id="' + merchantId + '"  data-status="' + status + '"  data-toggle="modal" data-target="#modal_activation">Activate</a>';
                } else if(status == "ACTIVE") {
                    link_activation = '<a class="trigger_activation" href="#" data-id="' + userId + '"  data-status="' + status + '" >Deactivate</a>';
                } else if (status == "INACTIVE") {
                    link_activation = '<a class="trigger_activation" href="#" data-id="' + userId + '" data-status="' + status + '" >Activate</a>';
                }
                link_profile = '<a href="/merchant/profile/' + merchantId  + '">Profile</a>';
                var action = '<div class="action_links">' + link_profile + link_activation + "</div>";
                var link_merchant = '<a href="/merchant/store/list/' + merchantId  + '">' + merchant.businessTitle + '</a>';

                var row = [merchantId, link_merchant, merchant.user.fullName, merchant.user.emailAddress, merchant.user.mobileNumber, Main.ucfirst(status), action];
                tdata.push(row);
            }

            Main.createDataTable("#merchants_table", tdata);

            $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

        };

        callback.loaderDiv = "body";
        callback.requestType = "GET";

        Main.request('/organizer/get_merchants', {}, callback);

    };

     Manager.loadMerchantActivation = function() {

         $('#partnership').selectpicker();

         $.validator.setDefaults({
             errorPlacement : function(error, element){
                 $('#error_container').html(error);
             },
             ignore: []
         });

         $.validator.addMethod("notEqual", function(value, element, arg){
             var result = value != arg;
             if($(element).is('select')) {
                 if(!result) {
                     $(element).siblings('.bootstrap-select').children('.form-control').addClass('error');
                 } else {
                     $(element).siblings('.bootstrap-select').children('.form-control').removeClass('error');
                 }
             }
             return result;
         }, "Please select any option.");

         $('#modal_activation').on('shown.bs.modal', function (e) {
             $("form", this).attr('data-id', $(e.relatedTarget).attr('data-id'));
         });

         $('#form_activation').validate({
             submitHandler: function(form) {

                 var chk_confirm = confirm('Are you sure you want to activate this merchant?');
                 if(!chk_confirm) return false;

                 var data = {};

                 data.id = $(form).attr('data-id');
                 data.partnershipStatus = $('#partnership').val();
                 data.commissionPercentage = $('#commission').val();
                 data.serviceFee = $('#service_fee').val();

                 Manager.merchantActivation(data);

                 return false;

             }
         });
         $('#partnership').rules('add', {notEqual: "none"});
         $('#commission').rules('add', {required: true, digits: true, min: 0, max: 100});
         $('#service_fee').rules('add', {required: true, digits: true, min: 0});

         $('.trigger_activation').live('click', function(){
             var statusCheck = $(this).attr('data-status') == 'INACTIVE';

             var chk_confirm = confirm('Are you sure you want to ' + (statusCheck ? "activate" : "deactivate") + ' this merchant?');
             if(!chk_confirm) return false;

             var data = {};
             data.id = $(this).attr('data-id');
             data.verifiedStatus = "" + statusCheck ;
             Manager.changeUserStatus(data);
         });

     };

    Manager.merchantActivation = function(data){

        $("button[type='submit']").attr("disabled",true);

        var callback = function (status, data) {
            $("button[type='submit']").removeAttr("disabled");

            alert(data.message);
            if (data.success == true) {
                $('#modal_activation').modal('hide');
                Manager.getMerchants();
            }
        };

        callback.loaderDiv = ".modal-dialog";

        Main.request('/organizer/activate_merchant', data, callback);

    };

    Manager.changeUserStatus = function(data){

        var callback = function (status, data) {
            alert(data.message);
            if (data.success == true) {
                Manager.getMerchants();
            }
        };

        callback.loaderDiv = "body";
        callback.requestType = "PUT";

        Main.request('/organizer/change_user_status', data, callback);

    };

    Manager.getCourierStaffs = function() {

        var callback = function (status, data) {

            console.log(data);
            if(!data.success){
                alert(data.message);
                return;
            }
            var courierStaffs = data.params.deliveryBoys;
            var tdata = [];

            for(i = 0; i < courierStaffs.length; i++){
                var courierStaff = courierStaffs[i];

                var id = courierStaff.id;
                var link_courier_staff = '<a href="/organizer/courier_staff/profile/' + id  + '">' + courierStaff.user.fullName + '</a>';
                var number = courierStaff.user.mobileNumber;
                var order_no = 0;
                var order_name = 0;
                var job_status = 0;
                var balance = 0;
                var action = '<div class="action_links">' +
                    '<a href="#" data-toggle="modal" class="view_courier_boy_map" data-cbid = "'+id+'">View on Map</a>' +
                    '<a href="#" data-toggle="modal" class="update_courier_boy_account"  data-cbid = "'+id+'" data-target="#modal_account">Update Accounts</a>' +
                    '</div>';

                var row = [id, link_courier_staff, number, order_no, order_name, job_status, balance, action];
                tdata.push(row);
            }

            Main.createDataTable("#courier_staff_table", tdata);

            $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

        };

        callback.loaderDiv = "body";
        callback.requestType = "GET";

        Main.request('/organizer/get_dboys', {}, callback);

    };

   Manager.getCourierBoyMap = function(){
         $('body').delegate('.view_courier_boy_map', 'click', function(){
             $('#modal_map').modal('show');
             var id = $(this).data("cbid");
             setTimeout(function(){
                 noEditInitialise();
                //if(!noEditInitialised) noEditInitialise(); else google.maps.event.trigger(map, 'resize');
                 var callback = function(status, data){
                     if(!data.success){
                         alert(data.message);
                         return;
                     }
                     var courierStaff = data.params.deliveryBoy;
                     var srclatlng = new google.maps.LatLng(courierStaff.latitude, courierStaff.longitude);
                     var destlatlang =  new google.maps.LatLng("27.6891424", "85.324561");
                     map.setZoom(12);
                     map.setCenter(srclatlng);

                     new google.maps.Marker({
                         position: srclatlng,
                         map: map
                         //draggable: true
                     });

                    new google.maps.Marker({
                         position: destlatlang,
                         map: map
                         //draggable: true
                     });

                     var request = {
                         origin: srclatlng,
                         destination: destlatlang,
                         travelMode: google.maps.DirectionsTravelMode.DRIVING
                     };

                     var directionsService = new google.maps.DirectionsService();
                     var directionsDisplay = new google.maps.DirectionsRenderer();

                     directionsService.route (request, function (result, status) {
                         console.log(status);
                         console.log(result);
                         if (status == google.maps.DirectionsStatus.OK) {
                             directionsDisplay.setDirections(result);
                         } else {
                             alert ("Directions was not successful because " + status);
                         }
                     });

                     directionsDisplay.setMap(map);

                 }
                 callback.loaderDiv = ".view_courier_boy_map";
                 callback.requestType = "GET";
                 var headers = {};
                 headers.id = id;
                 Main.request('/organizer/get_dboy', {}, callback, headers);
             }, 300);
         });
   }

    Manager.getCourierBoyAccount = function(){
        $('body').delegate('.update_courier_boy_account', 'click', function(){
            var id = $(this).data("cbid");
            $('#modal_account').data('cbid', id);
            var callback = function(status, data){
                if(!data.success){
                    alert(data.message);
                    return;
                }
                var courierStaff = data.params.deliveryBoy;
                console.log(courierStaff);
                $(".due_amount").text(courierStaff.previousDue);
                $("#due_amount_val").val(courierStaff.previousDue)
                $(".available_balance").text(courierStaff.walletAmount+courierStaff.bankAmount);
                $(".to_be_submitted").text(courierStaff.walletAmount);
                $("#to_be_submitted_val").val(courierStaff.walletAmount);
                $('#modal_account').data('cbname', courierStaff.user.fullName);

            }
            callback.loaderDiv = ".update_courier_boy_account";
            callback.requestType = "GET";
            var headers = {};
            headers.id = id;
            Main.request('/organizer/get_dboy', {}, callback, headers);
        });
    }

    Manager.updateCourierBoyAccount = function(){
        $('body').delegate('#advance_amount_val', 'keypress', function(event){
            if($('#advance_amount_val').hasClass('error')){
                $('#advance_amount_val').removeClass('error');
            }
            var cbid = $('#modal_account').data('cbid');
            var keycode = (event.keyCode ? event.keyCode : event.which);
            if(keycode == '13'){
                if($(this).val() == null)
                    return;

                if(isNaN($('#advance_amount_val').val())) {
                    $('#advance_amount_val').addClass('error');
                    return;
                }

                var conf = confirm("Are you sure you want to add advance amount for '"+$('#modal_account').data('cbname')+"'?");
                if(!conf) {
                    $('#advance_amount_val').val('');
                    return;
                }

                var callback = function(status, data){
                    if(!data.success){
                        alert(data.message);
                        return;
                    }
                    var courierStaff = data.params.deliveryBoy;
                    $(".due_amount").text(courierStaff.previousDue);
                    $("#due_amount_val").val(courierStaff.previousDue)
                    $(".available_balance").text(courierStaff.walletAmount+courierStaff.bankAmount);
                    $(".to_be_submitted").text(courierStaff.walletAmount);
                    $("#to_be_submitted_val").val(courierStaff.walletAmount);
                    $('#advance_amount_val').val('');
                }
                callback.loaderDiv = "body";
                callback.requestType = "POST";
                var headers = {};
                headers.id = cbid;
                Main.request('/accountant/update_dboy_account', {advanceAmount:$(this).val()}, callback, headers);
            }

        });
    }

    Manager.submitCourierBoyPreviousAmount = function(){
        $('body').delegate('#ack', 'click', function(){
            var cbid = $('#modal_account').data('cbid');
            if($(this).prop("checked")){
                 var conf =  confirm("Are you sure you want to acknowledge  RS."+$("#due_amount_val").val()+" from '"+$('#modal_account').data('cbname')+"'?");
                if(!conf) {
                    $(this).prop("checked", false);
                    return;
                }

                if($("#due_amount_val").val() == 0 || $("#due_amount_val").val() == null){
                    $(this).prop("checked", false);
                    return;
                }

                var callback = function(status, data){
                    if(!data.success){
                        alert(data.message);
                        return;
                    }
                    var courierStaff = data.params.deliveryBoy;
                    $(".due_amount").text(courierStaff.previousDue);
                    $("#due_amount_val").val(courierStaff.previousDue)
                    $(".available_balance").text(courierStaff.walletAmount+courierStaff.bankAmount);
                    $(".to_be_submitted").text(courierStaff.walletAmount);
                    $("#to_be_submitted_val").val(courierStaff.walletAmount);
                    $(this).prop("checked", false);
                }
                callback.loaderDiv = "body";
                callback.requestType = "POST";
                var headers = {};
                headers.id = cbid;
                Main.request('/accountant/dboy_ack_payment', {submittedAmount:$("#due_amount_val").val()}, callback, headers);

            }
        });
    }

    Manager.submitCourierBoyWalletAmount = function(){
        $('body').delegate('#submit', 'click', function(){
            var cbid = $('#modal_account').data('cbid');
            if($(this).prop("checked")){
                var conf =  confirm("Are you sure you want to submit RS."+$("#to_be_submitted_val").val()+" from '"+$('#modal_account').data('cbname')+"'?");
                if(!conf) {
                    $(this).prop("checked", false);
                    return;
                }

                if($("#to_be_submitted_val").val() == 0 || $("#to_be_submitted_val").val() == null) {
                    $('#submit').prop("checked", false);
                    return;
                }

                var callback = function(status, data){
                    if(!data.success){
                        alert(data.message);
                        return;
                    }
                    var courierStaff = data.params.deliveryBoy;
                    $(".due_amount").text(courierStaff.previousDue);
                    $("#due_amount_val").val(courierStaff.previousDue)
                    $(".available_balance").text(courierStaff.walletAmount+courierStaff.bankAmount);
                    $(".to_be_submitted").text(courierStaff.walletAmount);
                    $("#to_be_submitted_val").val(courierStaff.walletAmount);
                    $('#submit').prop("checked", false);
                }
                callback.loaderDiv = "body";
                callback.requestType = "POST";
                var headers = {};
                headers.id = cbid;
                Main.request('/accountant/dboy_wallet_payment', {submittedAmount:$("#to_be_submitted_val").val()}, callback, headers);

            }
        });
    }


})(jQuery);