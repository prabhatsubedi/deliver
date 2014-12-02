/**
 * Created by Lunek on 12/1/2014.
 */


var jcrop_api, xsize, ysize, xsize_dup, ysize_dup, $preview, $pcnt, $pimg, $pcnt_dup, $pimg_dup, chk_w, chk_h, image_changed = false, attr_id, data_image_type='';

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

$(document).ready(function(){

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
    $('#business_name').rules('add', {required: true, messages : {required: "Email is required."}});
    $('#url').rules('add', {required: true, messages : {required: "Email is required."}});
    $('#contact_person').rules('add', {required: true, messages : {required: "Email is required."}});
    $('#contact_email').rules('add', {required: true, messages : {required: "Email is required."}});
    $('#contact_no').rules('add', {required: true, messages : {required: "Email is required."}});
    $('#registration_no').rules('add', {required: true, messages : {required: "Email is required."}});
    $('#vat').rules('add', {required: true, messages : {required: "Email is required."}});

    $('#modal_login').on('shown.bs.modal', function (e) {
        $('#email').focus();
    });
    $('#modal_signup').on('shown.bs.modal', function (e) {
        $('#business_name').focus();
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
    var logoCont = $("#modal_signup .business_logo");
    dropZone.on('dragenter', function (e)
    {
        logoCont.addClass("entered");
    });
    dropZone.on('dragleave', function (e)
    {
        logoCont.removeClass("entered");
    });
    dropZone.on('drop', function (e)
    {
        if(e.originalEvent.dataTransfer.files[0] != undefined) {
            logoCont.removeClass("entered").addClass("dropped");
            readFile(e.originalEvent.dataTransfer.files[0]);
        } else {
            logoCont.removeClass("entered dropped");
        }
    });
    dropZone.mousedown('', function(){
        logoCont.addClass("entered");
    }).mouseleave(function(){
        logoCont.removeClass("entered");
    }).mouseup(function(){
        logoCont.removeClass("entered");
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


});