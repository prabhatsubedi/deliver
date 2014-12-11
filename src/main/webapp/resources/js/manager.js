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
                var link_activation = "", profile = "";

                if(status == "ACTIVE") {
                    link_activation = '<a href="#" data-id="' + userId + '"  data-status="' + status + '" >Deactivate</a>';
                } else if (status == "INACTIVE") {
                    link_activation = '<a href="#" data-id="' + userId + '" data-status="' + status + '" >Activate</a>';
                }
                profile = '<a href="/merchant/profile' + userId  + '">Profile</a>';
                var action = profile + link_activation;

                var row = [userId, merchant.businessTitle, merchant.user.fullName, merchant.user.emailAddress, merchant.user.mobileNumber, Main.ucfirst(status), action];
                tdata.push(row);
            }

            Main.createDataTable("#merchants_table", tdata);

            $('.dataTables_length select').attr('data-width', 'auto').selectpicker();


        };

        callback.loaderDiv = "body";
        callback.get = true;

        Main.request('/organizer/get_merchants', {}, callback);

    };

})(jQuery);