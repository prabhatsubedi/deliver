/**
 * Created by Lunek on 12/1/2014.
 */

var selectedCountry = Main.country;
var initialized = false;
var geocoder;
var infowindow;
var map;
var markers = [];
var marker;
var arrGeoPoints = {};
var redeem_location = [];
var jcrop_api, xsize, ysize, xsize_dup, ysize_dup, $preview, $pcnt, $pimg, $pcnt_dup, $pimg_dup, chk_w, chk_h;

function updatePreview(c)
{
    if (parseInt(c.w) > 0)
    {
        var bounds = jcrop_api.getBounds();
        boundx = bounds[0];
        boundy = bounds[1];
        var rx = xsize / c.w;
        var ry = ysize / c.h;
        var rx_dup = xsize_dup / c.w;
        var ry_dup = ysize_dup / c.h;

        $pimg.css({
            width: Math.round(rx * boundx) + 'px',
            height: Math.round(ry * boundy) + 'px',
            marginLeft: '-' + Math.round(rx * c.x) + 'px',
            marginTop: '-' + Math.round(ry * c.y) + 'px'
        });

        $pimg_dup.css({
            width: Math.round(rx_dup * boundx) + 'px',
            height: Math.round(ry_dup * boundy) + 'px',
            marginLeft: '-' + Math.round(rx_dup * c.x) + 'px',
            marginTop: '-' + Math.round(ry_dup * c.y) + 'px'
        });
    }
};

function readURL(input) {

    if (input.files && input.files[0]) {
        var tempInput = input;
        var isValid = /\.(jpg|jpeg|png)$/i.test(input.value);
        input.value == '';
        if(!isValid) {
            return false;
        } else {
            readFile(tempInput.files[0]);
        }
    }
}

chk_w = chk_h = 200;
function readFile(file) {

    var reader = new FileReader();
    reader.onload = function (e) {
        var src_url = e.target.result;
        var image = new Image();
        image.src = src_url;
        image.onload = function() {
            var w = this.width,
                h = this.height;
            var dup_width = $('.preview-pane .preview-container-dup').width();
            var dup_height = (chk_h/chk_w)*dup_width;
            $('.preview-pane .preview-container-dup').height(dup_height);
            $('.preview-pane-org .preview-container').width(chk_w);
            $('.preview-pane-org .preview-container').height(chk_h);

            if(w != chk_w || h != chk_h) {
                if(w >= chk_w && h >= chk_h) {
                    var jcrop_target = document.getElementById('jcrop_target');
                    jcrop_target.src = src_url;
                    jcrop_target.onload = function(){
                        var src_url = document.getElementById('jcrop_target').src;
                        var jcrop_preview = document.getElementById('jcrop_preview');
                        jcrop_preview.src = src_url;
                        var jcrop_preview_dup = document.getElementById('jcrop_preview_dup');
                        jcrop_preview_dup.src = src_url;
                        $('#crop_img_modal').modal('show');

                        if(jcrop_api  != undefined) {

                            $preview = $('.preview-pane');
                            $pcnt = $('.preview-pane .preview-container');
                            $pimg = $('.preview-pane .preview-container img');
                            $pcnt_dup = $('.preview-pane .preview-container-dup');
                            $pimg_dup = $('.preview-pane .preview-container-dup img');

                            xsize = $pcnt.width();
                            ysize = $pcnt.height();
                            xsize_dup = $pcnt_dup.width();
                            ysize_dup = $pcnt_dup.height();

                            jcrop_api.setImage(src_url);
                            jcrop_api.setOptions({
                                aspectRatio: chk_w/chk_h,
                                minSize: [chk_w, chk_h]
                            });
                            jcrop_api.setSelect([0,0,chk_w,chk_h]);

                        } else {

                            // Grab some information about the preview pane
                            $preview = $('.preview-pane');
                            $pcnt = $('.preview-pane .preview-container');
                            $pimg = $('.preview-pane .preview-container img');
                            $pcnt_dup = $('.preview-pane .preview-container-dup');
                            $pimg_dup = $('.preview-pane .preview-container-dup img');

                            xsize = $pcnt.width();
                            ysize = $pcnt.height();
                            xsize_dup = $pcnt_dup.width();
                            ysize_dup = $pcnt_dup.height();
                            var aspectRatio = xsize / ysize,
                                minWidth = xsize,
                                minHeight = ysize,
                                xpostion = 0,
                                yposition = 0;

                            // console.log('init',[xsize,ysize]);

                            $('#jcrop_target').Jcrop({
                                onChange: updatePreview,
                                onSelect: updatePreview,
                                aspectRatio: aspectRatio,
                                minSize: [minWidth, minHeight],
                                bgOpacity: 0.5,
                                bgColor: 'white',
                                addClass: 'jcrop-light',
                                boxWidth: 450
                            },function(){
                                // Use the API to get the real image size
                                /*                                    var bounds = this.getBounds();
                                 boundx = bounds[0];
                                 boundy = bounds[1];*/
                                // Store the API in the jcrop_api variable
                                jcrop_api = this;
                                jcrop_api.setSelect([xpostion,yposition,xpostion+minWidth,yposition+minHeight]);
                                jcrop_api.setOptions({ bgFade: true });
                                jcrop_api.ui.selection.addClass('jcrop-selection');
                                $('#ar_lock').attr('checked', true);

                                // Move the preview into the jcrop container for css positioning
                                $preview.appendTo(jcrop_api.ui.holder);
                            });

                        }

                    };
                } else {
                    alert("Please upload an image of size greater than 200px X 200px.");
                    return false;
                }
            } else {
                document.getElementById('jcrop_target').removeAttribute('src');
                document.getElementById('jcrop_preview').removeAttribute('src');
                document.getElementById('jcrop_preview_dup').removeAttribute('src');
                $('#drop_zone').addClass('image_selected').html('<img src="' + src_url + '" style="height: 100%;" />');
            }
        }
    };
    reader.readAsDataURL(file);
}

// Sets the map on all markers in the array.
function setAllMap(map) {
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(map);
    }
}

// Removes the markers from the map, but keeps them in the array.
function clearMarkers() {
    setAllMap(null);
}

// Shows any markers currently in thearray.
function showMarkers() {
    setAllMap(map);
}

// Deletes all markers in the array by removing references to them.
function deleteMarkers() {
    clearMarkers();
    markers = [];
    arrGeoPoints = {};
}

$(document).ready(function(){

    $.validator.addMethod("contactNumber", function(value, element, arg){
        return this.optional(element) || /[0-9+-]+$/.test(value);
    }, "Only +, - and numbers are allowed.");

    $('#login_form').validate({
        submitHandler: function() {
            var data = {username: $('#email').val(), password: $('#password').val()};
            Main.doLogin(data);
            return false;
        },
        wrapper : 'div'
    });
    $('#email').rules('add', {required: true, email: true, messages : {required: "Email is required."}});
    $('#password').rules('add', {required: true, minlength: 6, messages : {required: "Password is required."}});

    $('#signup_form').validate({
        submitHandler: function() {
            var data = {username: $('#email').val(), password: $('#password').val()};
            Main.doLogin(data);
            return false;
        }
    });
    $('#business_name').rules('add', {required: true, messages : {required: "Business name is required."}});
    $('#url').rules('add', {required: true, url: true, messages : {required: "URL is required."}});
    $('#contact_person').rules('add', {required: true, messages : {required: "Contact person is required."}});
    $('#contact_email').rules('add', {required: true, email: true, messages : {required: "Contact email is required."}});
    $('#contact_no').rules('add', {required: true, contactNumber: true, messages : {required: "Contact number is required."}});
//    $('#registration_no').rules('add', {required: true, messages : {required: "Registration number is required."}});
//    $('#vat').rules('add', {required: true, messages : {required: "VAT is required."}});

    $('#modal_login').on('shown.bs.modal', function (e) {
        $('#email').focus();
    });
    $('#modal_signup').on('shown.bs.modal', function (e) {
        $('#business_name').focus();
        $('body').addClass('modal-open');
    });
    $('#modal_login').on('hide.bs.modal', function (e) {
        $('#login_form').validate().resetForm();
    });
    $('#modal_signup').on('hide.bs.modal', function (e) {
        $('#signup_form').validate().resetForm();
    });

    $(document).on('dragover drop', function (e)
    {
        e.stopPropagation();
        e.preventDefault();
    });
    var dropZone = $("#drop_zone");
    dropZone.on('dragover', function (e)
    {
        $(this).addClass("entered");
    });
    dropZone.on('dragleave', function (e)
    {
        $(this).removeClass("entered");
    });
    dropZone.on('drop', function (e)
    {
        if(e.originalEvent.dataTransfer.files[0] != undefined) {
            $(this).removeClass("entered").addClass("dropped");
            readFile(e.originalEvent.dataTransfer.files[0]);
        } else {
            $(this).removeClass("entered dropped");
        }
    });
    dropZone.mousedown('', function(){
        $(this).addClass("entered");
    }).mouseleave(function(){
        $(this).removeClass("entered");
    }).mouseup(function(){
        $(this).removeClass("entered");
        $('#logo_input').trigger('click');
    });

    $('#ar_lock').change(function(e) {
        jcrop_api.setOptions(this.checked ? { aspectRatio: chk_w/chk_h }: { aspectRatio: 0 });
        jcrop_api.focus();
    });

    $('#apply_preview').click(function(e){
        html2canvas($('.preview-container'), {
            onrendered: function(canvas) {
                var strImage = canvas.toDataURL('image/jpeg');
                $('#drop_zone').addClass('image_selected').html('<img src="' + strImage + '" style="height: 100%;" />');
                $('#crop_img_modal').modal('hide');
            }
        });
    });

    $('#cancel_preview').click(function(e){
        $('#crop_img_modal').modal('hide');
    });

    $('#crop_img_modal').on('show.bs.modal', function(e){
        $('#ar_lock').prop('checked', true);
    });

    $('#crop_img_modal').on('hidden.bs.modal', function(e){
        $('#crop_img_modal img').attr('src', '');
        $("body").addClass('modal-open');
    });

    function geo_coding(address)
    {
        geocoder = new google.maps.Geocoder();
        geocoder.geocode( { 'address': address}, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                if(typeof map != 'undefined'){
                    map.setCenter(results[0].geometry.location);
                    map.fitBounds(results[0].geometry.bounds);
                }
            } else {
                alert("Geocode was not successful for the following reason: " + status);
            }
        });
    }

    function initialize() {
        var myOptions = {
            zoom: 4,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            scrollwheel: false
        }
        map = new google.maps.Map(document.getElementById("map-canvas"), myOptions);

        geo_coding(selectedCountry);

        infowindow = new google.maps.InfoWindow();

        // Create the search box and link it to the UI element.
        input = document.getElementById('pac-input');
        var custom_map_controls = $('#custom_map_controls').clone()[0]
        map.controls[google.maps.ControlPosition.TOP_RIGHT].push(custom_map_controls);

        var searchBox = new google.maps.places.SearchBox(input);

        /*    var marker1 = new google.maps.Marker({
         map: map
         });*/

        // [START region_getplaces]
        // Listen for the event fired when the user selects an item from the
        // pick list. Retrieve the matching places for that item.
        google.maps.event.addListener(searchBox, 'places_changed', function(){

            var places = searchBox.getPlaces();
            var foucsedplace;


            //for (var i = 0, place; place = places[i]; i++) {
            foucsedplace = places[0];
            //}
            addMarker(foucsedplace.geometry.location, foucsedplace.name);
            map.setCenter(foucsedplace.geometry.location);
            if(foucsedplace.geometry.viewport == undefined) {
                map.setZoom(19);
            } else {
                map.fitBounds(foucsedplace.geometry.viewport);
            }

            input.blur();

        });
        // [END region_getplaces]


        // Bias the SearchBox results towards places that are within the bounds of the
        // current map's viewport.
        google.maps.event.addListener(map, 'bounds_changed', function() {
            var bounds = map.getBounds();
            searchBox.setBounds(bounds);
        });


        google.maps.event.addListener(map, 'click', function(event) {
            addMarker(event.latLng);
        });
        document.getElementById('custom_map_controls').style.display = 'block';
        //input.style.display = 'block';


        $('#pac-input').keypress(function(e) {
            if(e.keyCode == 13) {
                e.preventDefault();
            }
        });

        initialized = true;
        $('#clear_markers').live('click', function(){
            deleteMarkers();
        });
        $('#scroll_zoom').live('click', function(){
            if(map.scrollwheel) {
                map.setOptions({scrollwheel: false});
                $(this).html("Enable Scroll Zoom");
            } else {
                map.setOptions({scrollwheel: true});
                $(this).html("Disable Scroll Zoom");
            }
        });

    }

    function locationToKey(location) {
        return (location.lat()+ '_' + location.lng()).replace(/[.-]/g, '_');
    }

    function addMarker(location, name) {
        deleteMarkers();
        var location_check = false;
        for(var i in arrGeoPoints) {
            if(location.lat() == arrGeoPoints[i].latitude && location.lng() == arrGeoPoints[i].longitude) {
                location_check = true;
            }
        }

        if(!location_check) {
            geocoder.geocode({'latLng': location}, function(results, status) {
                if (status == google.maps.GeocoderStatus.OK) {
                    if (results[0]) {

                        var streetNumber, sublocality, locality, city, postalCode, district, state, region, country, addressComponents;

                        var compCountry = results[results.length-1].address_components;
                        for(var i in compCountry) {
                            if(compCountry[i].types[0] == 'country') {
                                country = compCountry[i].long_name;
                                break;
                            }
                        }

                        if(country == selectedCountry)
                        {
                            addressComponents = results[0].address_components;
                            for (var i in addressComponents)
                            {
                                for(var j in addressComponents[i].types)
                                {
                                    if(addressComponents[i].types[j] == "street_number")
                                    {
                                        streetNumber = results[0].address_components[i].long_name;
                                    }
                                    if(addressComponents[i].types[j] == "route")
                                    {
                                        sublocality = results[0].address_components[i].long_name;
                                    }
                                    if(addressComponents[i].types[j] == "sublocality")
                                    {
                                        locality = results[0].address_components[i].long_name;
                                    }
                                    if(addressComponents[i].types[j] == "locality")
                                    {
                                        city = results[0].address_components[i].long_name;
                                    }
                                    if(addressComponents[i].types[j] == "postal_code")
                                    {
                                        postalCode = results[0].address_components[i].long_name;
                                    }
                                    if(addressComponents[i].types[j] == "administrative_area_level_2")
                                    {
                                        state = results[0].address_components[i].long_name;
                                    }
                                    if(addressComponents[i].types[j] == "administrative_area_level_1")
                                    {
                                        region = results[0].address_components[i].long_name;
                                    }
                                }
                            }

                            var geoObj = {};
                            geoObj.latitude = location.lat();
                            geoObj.longitude = location.lng();
                            geoObj.name = name === undefined ? $('#company_name').val() : name;
                            geoObj.street = sublocality === undefined ? '' : sublocality;
                            geoObj.city = city === undefined ? '' : city;
                            geoObj.postalCode = postalCode === undefined ? '' : postalCode;
                            geoObj.state = state === undefined ? '' : state;
                            geoObj.country = country;

                            marker = new google.maps.Marker({
                                position: location,
                                map: map
                                //draggable: true
                            });
                            var jsonObj = geoObj;
                            markers.push(marker);
                            arrGeoPoints[locationToKey(location)] = jsonObj;
                            var marker_address = $('.infowindow_template').clone();
                            var infoWindowData = arrGeoPoints[locationToKey(location)];
                            var address_name = infoWindowData.name;
                            var address_street_name = infoWindowData.street;
                            var address_postal_code = infoWindowData.postalCode;
                            var address_city = infoWindowData.city;
                            var address_state = infoWindowData.state;
                            $('.address_name .address_value', marker_address).attr('value', address_name);
                            $('.address_street_name .address_value', marker_address).attr('value', address_street_name);
                            $('.address_postal_code .address_value', marker_address).attr('value', address_postal_code);
                            $('.address_city .address_value', marker_address).attr('value', address_city);
                            $('.address_state .address_value', marker_address).attr('value', address_state);
                            $('.address_lines .save_marker', marker_address).attr('id', locationToKey(location));
                            infowindow.setContent(marker_address.html());
                            infowindow.open(map, marker);
                            google.maps.event.addListener(marker, 'click', function () {
                                var infoWindowData = arrGeoPoints[locationToKey(this.getPosition())];
                                var address_name = infoWindowData.name;
                                var address_street_name = infoWindowData.street;
                                var address_postal_code = infoWindowData.postalCode;
                                var address_city = infoWindowData.city;
                                var address_state = infoWindowData.state;
                                $('.address_name .address_value', marker_address).attr('value', address_name);
                                $('.address_street_name .address_value', marker_address).attr('value', address_street_name);
                                $('.address_postal_code .address_value', marker_address).attr('value', address_postal_code);
                                $('.address_city .address_value', marker_address).attr('value', address_city);
                                $('.address_state .address_value', marker_address).attr('value', address_state);
                                $('.address_lines .save_marker', marker_address).attr('id', locationToKey(location));
                                infowindow.setContent(marker_address.html());
                                infowindow.open(map, this);
                            });
                            google.maps.event.addListener(marker, 'rightclick', function () {
                                this.setMap(null);
                                markers.splice(markers.indexOf(this), 1);
                                delete arrGeoPoints[locationToKey(this.getPosition())];
                            });

                        }
                        else
                        {
                            alert('Deal redeem location doesn\'t lie within selected country boundary.');
                            return false;
                        }
                    } else {
                        alert('No results found');
                    }
                } else {
                    alert("Marker placing failed. Please click again to place marker.");
                }
            });
        }
        input.blur();

    }


    $('.infowindow .save_marker').live('click', function(){

        var geoKeyObject = arrGeoPoints[$(this).attr('id')];
        var geoParent = $(this).parent().parent();

        var address_name = $('.address_name .address_value', geoParent).val();
        var address_street_name = $('.address_street_name .address_value', geoParent).val();
        var address_postal_code = $('.address_postal_code .address_value', geoParent).val();
        var address_city = $('.address_city .address_value', geoParent).val();
        var address_state = $('.address_state .address_value', geoParent).val();


        geoKeyObject.name = address_name;
        geoKeyObject.street = address_street_name;
        geoKeyObject.postalCode = address_postal_code;
        geoKeyObject.city = address_city;
        geoKeyObject.state = address_state;

        infowindow.close(map);

    });

    $('#signup_form .nav a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        $(this).parent().removeClass('disabled_tab');
        $(this).parent().prevAll().addClass('passed_tab');
        $(this).parent().nextAll().removeClass('passed_tab');
    });

    $('#signup_form .nav a[href="#step_3"]').on('shown.bs.tab', function (e) {
        if(!initialized) initialize(); else google.maps.event.trigger(map, 'resize');
    });
    $('button.next').click(function(e){
        var tabelem = $('#signup_form .nav .active').next('li').children('a');
        if(tabelem.length > 0 ) {
            e.stopPropagation();
            e.preventDefault();
            tabelem.tab('show');
        }
    });

    $('#signup_form .nav a[data-toggle="tab"]').on('show.bs.tab', function(element) {
        var id = element.target.hash;
        var tab_parent = $('#signup_form .nav a[href="' + id + '"]').parent('li');
        var index = tab_parent.index();
        var bool_0 = true;
        var bool_1 = true;

        if(index > 0) {
            if(!$('#business_name').valid()) {
                bool_0 = false;
            }
            if(!$('#url').valid()) {
                bool_0 = false;
            }
        }

        if(index > 1) {
            if(!$('#contact_person').valid()) {
                bool_1 = false;
            }
            if(!$('#contact_email').valid()) {
                bool_1 = false;
            }
            if(!$('#contact_no').valid()) {
                bool_1 = false;
            }
            if(!$('#registration_no').valid()) {
                bool_1 = false;
            }
            if(!$('#vat').valid()) {
                bool_1 = false;
            }
        }

        if(bool_0 == false) {
            $('#signup_form .nav li').eq(0).children('a').tab('show');
            return false;
        } else if(bool_1 == false) {
            $('#signup_form .nav li').eq(1).children('a').tab('show');
            return false;
        }
    });


});