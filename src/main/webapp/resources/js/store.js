if(typeof(Store) == "undefined") var Store = {};

(function ($){

    Store.loadAddStore = function() {

        Image.dropZone('#brand_image_input', '#brand_image');
        Image.dropZone('#brand_logo_input', '#brand_logo');

        $('#save_marker', '#form_store').attr('disabled', 'disabled');
        $('#cancel_marker', '#form_store').attr('disabled', 'disabled');

        $.validator.addMethod("contactNumber", function(value, element, arg){
            return this.optional(element) || /[0-9+-]+$/.test(value);
        }, "Only +, - and numbers are allowed.");

        $.validator.setDefaults({
            errorPlacement : function(error, element){
                $('#error_container').html(error);
            }
        });

        $('#store_name, #street, #city, #state, #country, #contact_no, #contact_person').val('');

        $('select.haveall').live('change', function(){
            if($(this).val() != null && $('option', this).length == $(this).val().length + 1)
                $(this).selectpicker('selectAll');
        });

        $('select.compelselection').live('change', function(){
            if($(this).val() == null) $(this).selectpicker('selectAll');
        });

        if(!initialized) initialize('add_store'); else google.maps.event.trigger(map, 'resize');
        $('#brand_categories').selectpicker({
            noneSelectedText: 'Select Brand Categories'
        });

        $('#form_store').validate({
            submitHandler: function() {
                var loaderDiv = "#store_section .store_content";
                $(loaderDiv).addClass('loader_div').append('<div class="loader"></div>');

                var geoKeyObject = arrGeoPoints[$("#save_marker").attr('data-id')];
                var geoParent = '#form_store';

                var address_name = $('#store_name', geoParent).val();
                var address_street_name = $('#street', geoParent).val();
                var address_city = $('#city', geoParent).val();
                var address_state = $('#state', geoParent).val();
                var address_country = $('#country', geoParent).val();
                var address_contact_number = $('#contact_no', geoParent).val();
                var address_contact_person = $('#contact_person', geoParent).val();

                geoKeyObject.name = address_name;
                geoKeyObject.street = address_street_name;
                geoKeyObject.city = address_city;
                geoKeyObject.state = address_state;
                geoKeyObject.country = address_country;
                geoKeyObject.contactNumber = address_contact_number;
                geoKeyObject.contactPerson = address_contact_person;

                setTimeout(function(){
                    $(loaderDiv).removeClass('loader_div').children('.loader').hide();
                }, 200);

                return false;
            }
        });

        $('#store_name').rules('add', {required: true});
        $('#street').rules('add', {required: true});
        $('#city').rules('add', {required: true});
        $('#state').rules('add', {required: true});
        $('#country').rules('add', {required: true});
        $('#contact_no').rules('add', {required: true, contactNumber: true});
        $('#contact_person').rules('add', {required: true});

        $('#cancel_marker').live('click', function(){
            var loaderDiv = "#store_section .store_content";
            $(loaderDiv).addClass('loader_div').append('<div class="loader"></div>');

            var geoKeyObject = arrGeoPoints[$("#save_marker").attr('data-id')];
            var geoParent = '#form_store';
            $('#store_name', geoParent).val(geoKeyObject.name);
            $('#street', geoParent).val(geoKeyObject.street);
            $('#city', geoParent).val(geoKeyObject.city);
            $('#state', geoParent).val(geoKeyObject.state);
            $('#country', geoParent).val(geoKeyObject.country);
            $('#contact_no', geoParent).val(geoKeyObject.contactNumber);
            $('#contact_person', geoParent).val(geoKeyObject.contactPerson);

            setTimeout(function(){
                $(loaderDiv).removeClass('loader_div').children('.loader').hide();
            }, 200);

        });

        $('#form_brand').validate({
            submitHandler: function() {


                return false;
            }
        });

    };

    Store.addStore = function(data, headers) {

        $("button[type='submit']").attr("disabled", true);

        var callback = function (status, data) {
            $("button[type='submit']").removeAttr("disabled");

            if (data.success == true) {
                alert(data.message);
            } else {
                alert(data.message);
            }
        };

        callback.loaderDiv = "body";

        Main.request('/merchant/save_store', data, callback, headers);

    };

})(jQuery);