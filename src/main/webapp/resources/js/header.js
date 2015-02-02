if(typeof(Header) == "undefined") var Header = {};
(function ($) {

    Header.loadSearch = function(){

       var callBack = function(success, jsondata){

           if(!jsondata.success){
               alert(jsondata.message);
               return;
           }
           var brands = '';
           for(var i =0; i<jsondata.params.brands.length; i++){
               var store = jsondata.params.brands[i];
               brands+='<option value="'+store.id+'">'+store.brandName+'</option>';
           }
           $('#item_stores').html(brands);
           $('#item_stores').selectpicker('refresh');

       }

        callBack.requestType = "GET";
        callBack.loaderDiv = 'body';
        Main.request("/merchant/get_brands", {}, callBack);


        var callBackCat = function(success, jsondata){

            if(!jsondata.success){
                alert(jsondata.message);
                return;
            }
            var categories = '';
            for(var i =0; i<jsondata.params.categories.length; i++){
                var category = jsondata.params.categories[i];
                categories+='<option value="'+category.id+'">'+category.name+'</option>';
            }
            $('#item_categories').html(categories);
            $('#item_categories').selectpicker('refresh');

        }

        callBackCat.requestType = "GET";
        callBackCat.loaderDiv = 'body';

        Main.request("/merchant/get_search_categories", {}, callBackCat);



       $("body").delegate("#item_stores", 'change',  function(){
           var callBack = function(success, jsondata){

               if(!jsondata.success){
                   alert(jsondata.message);
                   return;
               }
               var categories = '';
               for(var i =0; i<jsondata.params.categories.length; i++){
                   var category = jsondata.params.categories[i];
                   categories+='<option value="'+category.id+'">'+category.name+'</option>';
               }
               $('#item_categories').html(categories);
               $('#item_categories').selectpicker('refresh');

           }

           callBack.requestType = "GET";
           callBack.loaderDiv = 'body';

           Main.request("/merchant/get_search_categories", {}, callBack, {"id":$(this).val()});
       });

       Header.searchItem = function(pageNumber, pageSize){
           if($("#item_name").val() == ''){
               alert("query is null")
               return;
           }

           var callBack = function(success, data){
               if(!data.success){
                   alert(data.message);
                   return;
               }

               var page_number = data.params.pageNumber;
               var page_size= data.params.pageSize;
               var sort_order = data.params.order;
               if(page_number ==  undefined) page_number = 1;
               if(page_size ==  undefined) page_size = pageSize;
               if(page_size == 0) page_size = undefined;
               if(sort_order ==  undefined) sort_order = 'desc';
               var pageOptions = {pageNumber: page_number, pageSize: page_size, sortOrder: sort_order};
               $(".body").html('<div class="main_content"><div class="item_listing"><div class="table_div"><div class="items_container td_div"></div></div></div></div>');

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
               console.log(total_items+' '+pageSize);
               if(total_items > pageSize) {
                   console.log(total_items);
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

               var item_list = '<div class="form_section">';
               item_list += '<div class="form_content clearfix">';
               for(var j = 0; j < items.data.length; j++) {
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

               item_list += '</div>';
               item_list += '</div>';

               $('.items_container').html(item_list);
               $('select.select_num_items').selectpicker("refresh");

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
                    //toggleSwitch(ui.position.left > 15 ? 'off' : 'on', ui.helper);
                    }
                    });
               });

           }

           callBack.loaderDiv = 'body';

           var data = {};

           data.brands = $("#item_stores").val();
           data.categories = $("#item_categoires").val();
           data.searchString = $("#item_name").val();
           var page = {};
           page.pageNumber = pageNumber;
           page.pageSize = pageSize;
           page.sortOrder = "desc";
           data.page = page;

           Main.request("/merchant/search_item", data, callBack);
       }

        $(".btn_search").click(function(event){
            event.preventDefault();
            Header.searchItem(1, 8);
        });

        $('.pagination a').live('click', function(e){
            e.preventDefault();
        });

        $('.pagination li:not(".disabled") a').live('click', function(){
            var pageSize = $('.select_num_items').val();
            console.log(pageSize);
            if(pageSize == 0){
                pageSize = 8;
            }
            console.log(pageSize);
            Header.searchItem($(this).attr('pageno'),  pageSize);
        });
   }

})(jQuery);