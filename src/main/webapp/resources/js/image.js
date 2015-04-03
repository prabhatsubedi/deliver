if(typeof(Image) == "undefined") var Image = {};
var jcrop_api, xsize, ysize, xsize_dup, ysize_dup, $preview, $pcnt, $pimg, $pcnt_dup, $pimg_dup, chk_w, chk_h, img_container;

(function ($) {

    Image.dropZone = function(target_input, custom_zone) {

        $(document).live('dragover drop', function (e)
        {
            e.stopPropagation();
            e.preventDefault();
        });
        var elem = custom_zone == undefined ? "#drop_zone" : custom_zone;
        var dropZone = $(elem);
        dropZone.removeClass('disabled');

        dropZone.live('dragover', function (e)
        {
            if(dropZone.hasClass('disabled')) return false;
            $(this).addClass("entered");
        });
        dropZone.live('dragleave', function (e)
        {
            if(dropZone.hasClass('disabled')) return false;
            $(this).removeClass("entered");
        });
        dropZone.live('drop', function (e)
        {
            if(dropZone.hasClass('disabled')) return false;
            if(e.originalEvent.dataTransfer.files[0] != undefined) {
                $(this).removeClass("entered").addClass("dropped");
                var file = e.originalEvent.dataTransfer.files[0];
                var isValid = /\.(jpg|jpeg|png)$/i.test(file.name);
                if(!isValid) {
                    Main.popDialog('Image Cropper', 'Only PNG, JPG and JPEG files are supported.');
                    return false;
                } else {
                    Image.readFile(file, target_input);
                    $('.active_img_container').removeClass('active_img_container');
                    $(this).addClass('active_img_container');
                }
            } else {
                $(this).removeClass("entered dropped");
            }
        });
        dropZone.live('mousedown', function(){
            if(dropZone.hasClass('disabled')) return false;
            $(this).addClass("entered");
        });
        dropZone.live('mouseleave', function(){
            if(dropZone.hasClass('disabled')) return false;
            $(this).removeClass("entered");
        });
        dropZone.live('mouseup', function(){
            if(dropZone.hasClass('disabled')) return false;
            $(this).removeClass("entered");
        });
        dropZone.live('click', function(){
            if(dropZone.hasClass('disabled')) return false;
            $(target_input).trigger('click');
            $('.active_img_container').removeClass('active_img_container');
            $(this).addClass('active_img_container');
        });

    };

    Image.updatePreview = function (c)
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

    Image.readURL = function (input) {

        if (input.files && input.files[0]) {
            var tempInput = input;
            var isValid = /\.(jpg|jpeg|png)$/i.test(input.value);
            input.value == '';
            if(!isValid) {
                Main.popDialog('Image Cropper', 'Only PNG, JPG and JPEG files are supported.');
                return false;
            } else {
                Image.readFile(tempInput.files[0], '#' + $(input).attr('id'));
            }
        }
    }

    Image.readFile = function (file, container_id) {

        var dimension = $(container_id).attr('data-dimension');
        img_container = $(container_id).prev('.drop_zone').attr('id');
        if(dimension != undefined) {
            var dimension_arr = dimension.split('x');
            chk_w = dimension_arr[0];
            chk_h = dimension_arr[1];
        } else {
            chk_w = chk_h = 200;
        }

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
                                    onChange: Image.updatePreview,
                                    onSelect: Image.updatePreview,
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
                                Image.loadJcropFunctions();

                            }

                        };

                    } else {
                        Main.popDialog('Image Cropper', 'Please upload an image of size greater than ' + chk_w + 'px X ' + chk_h + 'px.');
                        return false;
                    }
                } else {
                    document.getElementById('jcrop_target').removeAttribute('src');
                    document.getElementById('jcrop_preview').removeAttribute('src');
                    document.getElementById('jcrop_preview_dup').removeAttribute('src');
                    if(img_container == undefined && $('.active_img_container').length == 1) {

                        var callback = function (status, data) {

                            if (data.success == false) {
                                Main.popDialog('Image Cropper', data.message);
                            } else {
                                $('.active_img_container').eq(0).children('img').attr('src', src_url);
                            }

                        };

                        callback.loaderDiv = ".active_img_container";

                        Main.request('/merchant/add_items_images', {itemImages: [src_url]}, callback, {id: $('.active_img_container').eq(0).attr('data-id')});

                    } else {
                        $('#' + img_container).addClass('image_selected').removeClass('error').html('<img src="' + src_url + '" style="height: 100%;" class="img-responsive" data-new="true" />');
                    }
                }
            }
        };
        reader.readAsDataURL(file);
    }

    Image.loadJcropFunctions = function() {

        $('#ar_lock').change(function(e) {
            jcrop_api.setOptions(this.checked ? { aspectRatio: chk_w/chk_h }: { aspectRatio: 0 });
            jcrop_api.focus();
        });

        $('#apply_preview').click(function(e){
            html2canvas($('.preview-container'), {
                onrendered: function(canvas) {
                    var strImage = canvas.toDataURL('image/jpeg');
                    if(img_container == undefined && $('.active_img_container').length == 1) {

                        var callback = function (status, data) {

                            if (data.success == false) {
                                Main.popDialog('Image Cropper', data.message);
                            } else {
                                $('.active_img_container').eq(0).children('img').attr('src', strImage);
                            }

                        };

                        callback.loaderDiv = ".active_img_container";

                        Main.request('/merchant/add_items_images', {itemImages: [strImage]}, callback, {id: $('.active_img_container').eq(0).attr('data-id')});

                    } else {
                        $('#' + img_container).addClass('image_selected').removeClass('error').html('<img src="' + strImage + '" style="height: 100%;" class="img-responsive" data-new="true" />');
                    }
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
            $('.preview-pane.preview-pane-org').css({top: -8});
        });

        var offset_top;
        var scroll_top;
        var css_top;
        $('#crop_img_modal').scroll(function(e){
            offset_top = $('.jcrop-holder', this).offset().top;
            scroll_top = $('html').scrollTop();
            offset_top = scroll_top - offset_top;
            css_top = -16;
            if(offset_top > -16) {
                css_top = offset_top;
            }
            $('.preview-pane.preview-pane-org').css({top: css_top + 8});
        });

    }

})(jQuery);