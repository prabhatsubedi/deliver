/**
 * Created by Lunek on 1/28/2015.
 */


var Search = function() {

    return {

        getAllStores : function(params) {

            var callback = function (status, data) {

                if (data.success == true) {
                    var brands = data.params.brands;
                    Main.saveInLocalStorage('stores', JSON.stringify(data));
                    if(brands.length > 0) {
                        var store_options = '';
                        for(var i = 0; i < brands.length; i++) {
                            store_options += '<option value="' + brands[i].id + '">' + brands[i].brandName + '</option>';
                        }
                        $('#item_stores').append(store_options);
                        $('#item_stores').selectpicker('refresh');
                    }
                }
            };
            callback.requestType = "GET";

            if(params == 'load' && Main.getFromLocalStorage('stores') != undefined) {
                callback('', JSON.parse(Main.getFromLocalStorage('stores')))
            } else {
                Main.request('/merchant/get_brands', {}, callback, {merchantId: 11});
            }

        },

        getAllCategories : function(params) {

            var callback = function (status, data) {

                if (data.success == true) {
                    var categories = data.params.categories;
                    Main.saveInLocalStorage('categories', JSON.stringify(data));
                    if(categories.length > 0) {
                        var categories_options = '';
                        for(var i = 0; i < categories.length; i++) {
                            categories_options += '<option value="' + categories[i].id + '">' + categories[i].name + '</option>';
                        }
                        $('#item_categories').append(categories_options);
                        $('#item_categories').selectpicker('refresh');
                    }
                }
            };
            callback.requestType = "GET";


            if(params == 'load' && Main.getFromLocalStorage('categories') != undefined) {
                callback('', JSON.parse(Main.getFromLocalStorage('categories')))
            } else {
                Main.request("/merchant/get_search_categories", {}, callback);
            }

        },
        loadSearch : function(postParams) {
            $('#item_name').val(postParams.item_name);
            $('#item_stores').val(postParams.item_stores);
            $('#item_categories').val(postParams.item_categories);
            $('#item_stores').selectpicker('refresh');
            $('#item_categories').selectpicker('refresh');
            this.searchItems({});

            $('.item_container').live('mouseover', function(){
                $('.switch_container', this).removeClass('hidden');
            });

            $('.item_container').live('mouseout', function(){
                $('.switch_container', this).addClass('hidden');
            });

            $('.switch_activation .btn_switch').live('mouseup', function(e){
                if(!dragged && e.which == 1) toggleSwitch($(this).hasClass('on') ? 'off' : 'on', $(this));
            });

            $('.pagination a').live('click', function(e){
                e.preventDefault();
            });

            $('.pagination li:not(".disabled") a').live('click', function(){
                Search.searchItems({pageNumber: $(this).attr('pageno'), pageSize: $('.items_container .select_num_items').val()});
            });

            $('.select_num_items').live('change', function(){
                Search.searchItems({pageNumber: 1, pageSize: $('.items_container .select_num_items').val()});
            });

            $('#item_search').submit(function(e){
                e.preventDefault();
                Search.searchItems({});
            });

        },
        searchItems : function(params) {

            var pageSize = 8;
            var page_number = params.pageNumber;
            var page_size= params.pageSize;
            var sort_order = params.order;
            if(page_number ==  undefined) page_number = 1;
            if(page_size ==  undefined) page_size = pageSize;
            if(page_size == 0) page_size = undefined;
            if(sort_order ==  undefined) sort_order = 'desc';
            var pageOptions = {pageNumber: page_number, pageSize: page_size, sortOrder: sort_order};

            var callback = function(status, data) {

                console.log(data);

                if (data.success == true) {

                    var item_list = '';
                    var items = data.params.items;

                    var total_items = items.numberOfRows;
                    var listed_items = items.data.length;
                    var page_number = parseInt(pageOptions.pageNumber);
                    var page_size = pageOptions.pageSize;
                    if(page_size == undefined) page_size = listed_items;
                    var total_pages = Math.ceil(total_items/page_size);
                    var page_list = '<li class="prev_page"><a href="#" pageno="' + (page_number - 1) + '">&laquo;</a></li>';
                    for(var i = 1; i <= total_pages; i++) {
                        page_list += '<li ' + (i == page_number ? 'class="active"' : '') + '><a href="#" pageno="' + i + '">' + i + '</a></li>';
                    }
                    page_list += '<li class="next_page"><a href="#" pageno="' + (page_number + 1) + '">&raquo;</a></li>';

                    var elem_pag = $('.pagination_template').clone();
                    $('.pagination', elem_pag).html(page_list);
                    if(page_number == 1) {
                        $('.pagination .prev_page', elem_pag).addClass('disabled');
                    }
                    if(page_number == total_pages) {
                        $('.pagination .next_page', elem_pag).addClass('disabled');
                    }

                    var page_size_list = '';
                    var ts_ceil = Math.ceil(total_items/5);
                    var ts_mod = ts_ceil % 5;
                    var page_num_multiplier = ts_ceil;
                    if(ts_mod > 0) page_num_multiplier = ts_ceil + (5 - ts_mod);
                    if(total_items > pageSize) {
                        var i = pageSize;
                        page_size_list += '<option value="' + i + '" ' + (page_size == i ? "selected=\"selected\"" : "") + '>' + i + '</option>';
                        i = page_num_multiplier * 1;
                        if(i < total_items && i > pageSize)
                            page_size_list += '<option value="' + i + '" ' + (page_size == i ? "selected=\"selected\"" : "") + '>' + i + '</option>';
                        i = page_num_multiplier * 2;
                        if(i < total_items && i > pageSize)
                            page_size_list += '<option value="' + i + '" ' + (page_size == i ? "selected=\"selected\"" : "") + '>' + i + '</option>';
                        i = page_num_multiplier * 3;
                        if(i < total_items && i > pageSize)
                            page_size_list += '<option value="' + i + '" ' + (page_size == i ? "selected=\"selected\"" : "") + '>' + i + '</option>';
                    }
                    i = 0;
                    page_size_list += '<option value="' + i + '" ' + (page_size == total_items ? "selected=\"selected\"" : "") + '>All</option>';
                    $('select.select_num_items', elem_pag).html(page_size_list);

                    if(listed_items > 0) {
                        for(var j = 0; j < listed_items; j++) {
                            var item = items.data[j];
                            var elem = $('.item_container_template').clone();
                            if(item.itemsImage.length > 0) $('.item_image img', elem).attr('src', item.itemsImage[0].url);
                            $('.item_name a', elem).attr('href', Main.modifyURL('/merchant/item/view/' + item.id)).html(item.name);
                            $('.item_price span', elem).html(item.unitPrice);
                            $('.btn_switch', elem).attr('data-id', item.id);
                            if(item.status == 'ACTIVE')
                                $('.btn_switch', elem).removeClass('off').addClass('on');
                            else
                                $('.btn_switch', elem).removeClass('on').addClass('off');
                            item_list += elem.html();
                        }
                        item_list += elem_pag.html();
                    } else {
                        item_list += 'No data available.';
                    }

                    $('.items_container').html(item_list);
                    $('.items_container select.select_num_items').selectpicker();

                    Main.elemRatio(function() {
                        $('.items_container .item_container').removeClass('invisible');
                        $(window).trigger('resize');

                        $('.switch_activation .btn_switch').bind('contextmenu', function(e) {
                            return false;
                        });
                        $( ".btn_switch" ).draggable({
                            containment: "parent",
                            start: function( event, ui) {
                                dragged = true;
                            },
                            stop: function( event, ui) {
                                if(Math.abs(ui.originalPosition.left - ui.position.left) < 15) {
                                    cancel_drag = true;
                                }
                                toggleSwitch(ui.position.left > 15 ? 'off' : 'on', ui.helper);
                            }
                        });

                    });

                } else {
                    alert(data.message);
                }

            };

            var searchParams = {};
            if($("#item_stores").val() != "All") searchParams.brands = $("#item_stores").val();
            if($("#item_categoires").val() != "All") searchParams.categories = $("#item_categoires").val();
            searchParams.searchString = $("#item_name").val();
            searchParams.page = pageOptions;

            callback.loaderDiv = "body";
            Main.request('/merchant/search_item', searchParams, callback);
        }

    };

}();

$(document).ready(function(){
    Search.getAllStores('load');
    Search.getAllCategories('load');
});