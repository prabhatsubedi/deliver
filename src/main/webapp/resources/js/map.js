var selectedCountry = Main.country;
var initialized = false;
var geocoder;
var infowindow;
var map;
var markers = [];
var marker;
var arrGeoPoints = {};
var redeem_location = [];

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
                            geoObj.latitude = location.lat();
                            geoObj.longitude = location.lng();
                            geoObj.name = name === undefined ? $('#business_name').val() : name;
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

});