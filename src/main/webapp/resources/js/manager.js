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

                var userId = merchant.id;
                var status = merchant.userStatus;
                var link_activation = "", link_profile = "";

                if(status == "VERIFIED") {
                    link_activation = '<a href="#" data-id="' + userId + '"  data-status="' + status + '"  data-toggle="modal" data-target="#modal_activation">Activate</a>';
                } else if(status == "ACTIVE") {
                    link_activation = '<a class="trigger_activation" href="#" data-id="' + userId + '"  data-status="' + status + '" >Deactivate</a>';
                } else if (status == "INACTIVE") {
                    link_activation = '<a class="trigger_activation" href="#" data-id="' + userId + '" data-status="' + status + '" >Activate</a>';
                }
                link_profile = '<a href="/merchant/profile/' + userId  + '">Profile</a>';
                var action = '<div class="action_links">' + link_profile + link_activation + "</div>";
                var link_merchant = '<a href="/merchant/dashboard/' + userId  + '">' + merchant.businessTitle + '</a>';

                var row = [userId, link_merchant, merchant.user.fullName, merchant.user.emailAddress, merchant.user.mobileNumber, Main.ucfirst(status), action];
                tdata.push(row);
            }

            Main.createDataTable("#merchants_table", tdata);

            $('.dataTables_length select').attr('data-width', 'auto').selectpicker();

            Manager.loadMerchantActivation();

        };

        callback.loaderDiv = "body";
        callback.get = true;

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

                 Manager.merchantActivation(data);

                 return false;

             }
         });
         $('#partnership').rules('add', {notEqual: "none"});
         $('#commission').rules('add', {required: true, digits: true, min: 0, max: 100});

         $('.trigger_activation').live('click', function(){
             var statusCheck = $(this).attr('data-status') == 'INACTIVE';

             var chk_confirm = confirm('Are you sure you want to ' + (statusCheck ? "activate" : "deactivate") + ' this merchant?');
             if(!chk_confirm) return false;

             var data = {};
             data.id = $(this).attr('data-id');
             data.verifiedStatus = statusCheck ;
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
                Merchant.getMerchants();
            }
        };

        callback.loaderDiv = ".modal-dialog";

        Main.request('/organizer/activate_merchant', data, callback);

    };

    Manager.changeUserStatus = function(data){

        var callback = function (status, data) {
            alert(data.message);
            if (data.success == true) {
                Merchant.getMerchants();
            }
        };

        callback.loaderDiv = "body";

        Main.request('/organizer/change_user_status', data, callback);

    };

})(jQuery);