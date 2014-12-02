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

        var isValid = /\.(jpg|jpeg|png)$/i.test(input.value);
        if(!isValid) {
            input.value == '';
            return false;
        } else {
            var reader = new FileReader();
            reader.onload = function (e) {
                return e.target.result;
            };
            reader.readAsDataURL(input.files[0]);
        }

    }
}

function readFile(file) {
    var reader = new FileReader();
    reader.onload = function (e) {
        return e.target.result;
    };
    reader.readAsDataURL(input.files[0]);
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
    var obj = $("#drop_zone");
    obj.on('dragenter', function (e)
    {
        $(this).addClass("entered");
    });
    obj.on('drop', function (e)
    {
        $(this).addClass("dropped");
        var files = e.originalEvent.dataTransfer.files;
    });


});