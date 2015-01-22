var selectedCountry = Main.country;
var initialized = false;
var noEditInitialised = false;
var geocoder;
var infowindow;
var map;
var markers = [];
var marker;
var arrGeoPoints = {};
var redeem_location = [];
var input;

// functions
var initialize;
var noEditInitialise;
var locationToKey;
var addMarker;
var addStoreMarker;
var latLngToLocation;
var removeAnimation;

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

    function geo_coding(address, noBounds, noCenter)
    {
        geocoder = new google.maps.Geocoder();
        geocoder.geocode( { 'address': address}, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                if(typeof map != 'undefined'){
                    if(noCenter != true)
                        map.setCenter(results[0].geometry.location);

                    if(noBounds != true)
                        map.fitBounds(results[0].geometry.bounds);
                }
            } else {
                alert("Geocode was not successful for the following reason: " + status);
            }
        });
    }

    initialize = function(page, scrollwheel) {
        var myOptions = {
            zoom: 4,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            scrollwheel: scrollwheel == true ? true : false
        }
        map = new google.maps.Map(document.getElementById("map-canvas"), myOptions);

        deleteMarkers();

        geo_coding(selectedCountry);

        infowindow = new google.maps.InfoWindow();

        // Create the search box and link it to the UI element.
        input = $('#pac-input')[0];
        var custom_map_controls = $('#custom_map_controls').clone()[0]
        map.controls[google.maps.ControlPosition.TOP_RIGHT].push(custom_map_controls);

        if(input != undefined) {

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
                if(page == "add_store")
                    addStoreMarker(foucsedplace.geometry.location, foucsedplace.name);
                else
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

        }

        if(page != 'readonly') {
            google.maps.event.addListener(map, 'click', function(event) {
                if(page == "add_store")
                    addStoreMarker(event.latLng);
                else
                    addMarker(event.latLng);
            });
            $('#clear_markers').live('click', function(){
                deleteMarkers();
            });
        }

        $('#custom_map_controls').show();
        //input.style.display = 'block';


        $('#pac-input').keypress(function(e) {
            if(e.keyCode == 13) {
                e.preventDefault();
            }
        });

        initialized = true;
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

    noEditInitialise = function(){
        var myOptions = {
            zoom: 10,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            scrollwheel: false
        }
        map = new google.maps.Map(document.getElementById("no-edit-map-canvas"), myOptions);

        geo_coding(selectedCountry, true, true);

        infowindow = new google.maps.InfoWindow();

        noEditInitialised = true;
    }


    locationToKey = function(location) {
        var latitude = location.latitude == undefined ? location.lat() : location.latitude ;
        var longitude = location.longitude == undefined ? location.lng() : location.longitude ;
        return (latitude+ '_' + longitude).replace(/[.-]/g, '_');
    }

    latLngToLocation = function(latitude, longitude) {
        return new google.maps.LatLng(latitude, longitude);
    }

    addMarker = function(location, name) {
        if(disableMapEdit) return false;
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

                        var streetNumber, sublocality, locality, city, postalCode, state, region, country, addressComponents;

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
                            var geoPointData = location.geoPointData;
                            if(geoPointData != undefined) {
                                geoObj.latitude = geoPointData.latitude;
                                geoObj.longitude = geoPointData.longitude;
                                geoObj.id = geoPointData.id;
                                geoObj.name = geoPointData.name;
                                geoObj.street = geoPointData.street;
                                geoObj.city = geoPointData.city;
                                geoObj.state = geoPointData.state;
                                geoObj.country = geoPointData.country;
                                geoObj.countryCode = geoPointData.countryCode;
                            } else {
                                geoObj.latitude = location.lat();
                                geoObj.longitude = location.lng();
                                geoObj.name = name === undefined ? $('#business_name').val() : name;
                                geoObj.street = sublocality === undefined ? '' : sublocality;
                                geoObj.city = city === undefined ? '' : city;
                                geoObj.postalCode = postalCode === undefined ? '' : postalCode;
                                geoObj.state = state === undefined ? '' : state;
                                geoObj.country = country;
                            }

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
                            $('.address_name .val_address_value', marker_address).html(address_name);
                            $('.address_street_name .val_address_value', marker_address).html(address_street_name);
                            $('.address_postal_code .val_address_value', marker_address).html(address_postal_code);
                            $('.address_city .val_address_value', marker_address).html(address_city);
                            $('.address_state .val_address_value', marker_address).html(address_state);
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
                                $('.address_name .val_address_value', marker_address).html(address_name);
                                $('.address_street_name .val_address_value', marker_address).html(address_street_name);
                                $('.address_postal_code .val_address_value', marker_address).html(address_postal_code);
                                $('.address_city .val_address_value', marker_address).html(address_city);
                                $('.address_state .val_address_value', marker_address).html(address_state);
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
                    setTimeout(function(){ addMarker(location, name); }, 500);
//                    alert("Marker placing failed. Please click again to place marker.");
                }
            });
        }
        if(input != undefined) input.blur();

    }

    $('.infowindow .save_marker').live('click', function(){
        if(disableMapEdit) return false;
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

    removeAnimation = function() {
        for(var i in markers) {
            markers[i].setAnimation(null);
        }
    }

    function markerPrevNext(marker) {
        var current_index = markers.indexOf(marker);
        var markers_length = markers.length;
        var prev = $(".marker_prev");
        var next = $(".marker_next");
        prev.attr('data-index', current_index - 1);
        next.attr('data-index', current_index + 1);
        if(markers_length > 1) {
            if(current_index == 0){
                prev.attr('disabled', 'disabled');
            } else {
                prev.removeAttr('disabled', 'disabled');
            }
            if(current_index == markers_length - 1) {
                next.attr('disabled', 'disabled');
            } else {
                next.removeAttr('disabled', 'disabled');
            }
        } else {
            prev.attr('disabled', 'disabled');
            next.attr('disabled', 'disabled');
        }
    }

    addStoreMarker = function (location, name) {
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

                        var streetNumber, sublocality, locality, city, postalCode, state, region, country, addressComponents;

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
                            var geoPointData = location.geoPointData;
                            if(geoPointData != undefined) {
                                geoObj.id = geoPointData.id;
                                geoObj.latitude = geoPointData.latitude;
                                geoObj.longitude = geoPointData.longitude;
                                geoObj.name = geoPointData.name;
                                geoObj.street = geoPointData.street;
                                geoObj.city = geoPointData.city;
//                            geoObj.postalCode = geoPointData.postalCode;
                                geoObj.state = geoPointData.state;
                                geoObj.country = geoPointData.country;
                                geoObj.contactNo = geoPointData.contactNo;
                                geoObj.contactPerson = geoPointData.contactPerson;
                            } else {
                                geoObj.latitude = location.lat();
                                geoObj.longitude = location.lng();
                                geoObj.name = name === undefined ? $('#brand_name').val() : name;
                                geoObj.street = sublocality === undefined ? '' : sublocality;
                                geoObj.city = city === undefined ? '' : city;
//                            geoObj.postalCode = postalCode === undefined ? '' : postalCode;
                                geoObj.state = state === undefined ? '' : state;
                                geoObj.country = country;
                                geoObj.contactNo = "";
                                geoObj.contactPerson = "";
                            }

                            removeAnimation();
                            marker = new google.maps.Marker({
                                position: location,
                                map: map
                                //draggable: true
                            });
                            var jsonObj = geoObj;
                            markers.push(marker);
                            arrGeoPoints[locationToKey(location)] = jsonObj;
                            var marker_address = '#store_section';
                            var infoWindowData = arrGeoPoints[locationToKey(location)];
                            var address_name = infoWindowData.name;
                            var address_street_name = infoWindowData.street;
//                            var address_postal_code = infoWindowData.postalCode;
                            var address_city = infoWindowData.city;
                            var address_state = infoWindowData.state;
                            var address_country = infoWindowData.country;
                            var address_contact_number = infoWindowData.contactNo;
                            var address_contact_person = infoWindowData.contactPerson;
                            $('#store_name', marker_address).attr('value', address_name);
                            $('#street', marker_address).attr('value', address_street_name);
//                            $('.address_postal_code .address_value', marker_address).attr('value', address_postal_code);
                            $('#city', marker_address).attr('value', address_city);
                            $('#state', marker_address).attr('value', address_state);
                            $('#country', marker_address).attr('value', address_country);
                            $('#contact_no', marker_address).attr('value', address_contact_number);
                            $('#contact_person', marker_address).attr('value', address_contact_person);
                            $('.save_marker', marker_address).removeAttr('disabled').attr({'data-id': locationToKey(location)});
                            $('.cancel_marker', marker_address).removeAttr('disabled');
                            marker.setAnimation(google.maps.Animation.BOUNCE);
                            markerPrevNext(marker);
                            google.maps.event.addListener(marker, 'click', function () {
                                removeAnimation();
                                var infoWindowData = arrGeoPoints[locationToKey(this.getPosition())];
                                var address_name = infoWindowData.name;
                                var address_street_name = infoWindowData.street;
                                var address_city = infoWindowData.city;
                                var address_state = infoWindowData.state;
                                var address_country = infoWindowData.country;
                                var address_contact_number = infoWindowData.contactNo;
                                var address_contact_person = infoWindowData.contactPerson;
                                $('#store_name', marker_address).attr('value', address_name);
                                $('#street', marker_address).attr('value', address_street_name);
                                $('#city', marker_address).attr('value', address_city);
                                $('#state', marker_address).attr('value', address_state);
                                $('#country', marker_address).attr('value', address_country);
                                $('#contact_no', marker_address).attr('value', address_contact_number);
                                $('#contact_person', marker_address).attr('value', address_contact_person);
                                $('.save_marker', marker_address).removeAttr('disabled').attr({'data-id': locationToKey(location)});
                                $('.cancel_marker', marker_address).removeAttr('disabled');
                                this.setAnimation(google.maps.Animation.BOUNCE);
                                markerPrevNext(this);
                                $('#form_store').valid();
                            });
                            if(location.readOnly != true) {
                                google.maps.event.addListener(marker, 'rightclick', function () {
                                    this.setMap(null);
                                    var current_index = markers.indexOf(this);
                                    markers.splice(current_index, 1);
                                    delete arrGeoPoints[locationToKey(this.getPosition())];
                                    var markers_length = markers.length;
                                    if(markers_length > 0){
                                        current_index = current_index == 0 ? 0 : current_index - 1;
                                        google.maps.event.trigger(markers[current_index], 'click');
                                        map.panTo(markers[current_index].position);
                                    } else {
                                        $('#store_name, #street, #city, #state, #country, #contact_no, #contact_person').val('');
                                        $('.save_marker', marker_address).attr('disabled', 'disabled').removeAttr('data-id');
                                        $('.cancel_marker', marker_address).attr('disabled', 'disabled');
                                    }
                                });
                            }

                        }
                        else
                        {
                            alert('Deal redeem location doesn\'t lie within selected country boundary.');
                        }
                    } else {
                        alert('No results found');
                    }
                } else {
                    setTimeout(function(){ addStoreMarker(location, name); }, 500);

//                    alert("Marker placing failed. Please click again to place marker.");
                }
            });
        }
        if(input != undefined) input.blur();

    }

});