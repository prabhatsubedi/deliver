var appPermissions = 'email,user_friends';
var accessToken;
var clientId;
var getBasicInfo = true;
var apId;
var clientEmail;

window.fbAsyncInit = function() {
    var hostName = window.location.hostname;
    //set the FB AppId
    if(hostName=="localhost")
         apId = "979998678677787";
    else if(hostName=="idelivr.com" || hostName=="www.idelivr.com")
        apId = "910783542265968";
    else if(hostName=="test.idelivr.com" || hostName=="www.test.idelivr.com")
         apId = "979999898677665";
    else if(hostName=="test1.idelivr.com" || hostName=="www.test1.idelivr.com")
        apId = "978523412158647";


    // init the FB JS SDK                                                            n
    FB.init({
        version    : 'v2.1',
        appId      : apId,                        // App ID from the app dashboard
        status     : true,                                 // Check Facebook Login status
        cookie     : true, // enable cookies to allow the server to access the session
        xfbml      : true,
        oauth: true// Look for social plugins on the page
    });

    /*FB.getLoginStatus(function(response) {
        if (response.status === 'connected') {
            // the user is logged in and has authenticated your
            // app, and response.authResponse supplies
            // the user's ID, a valid access token, a signed
            // request, and the time the access token
            // and signed request each expire
            clientId = response.authResponse.userID;
            accessToken = response.authResponse.accessToken;
            if(getBasicInfo == false || typeof requestCouponId == 'undefined' )
                return false;
                FB.api('/me', Fb.signUpUser(accessToken, false));
                $(".fb-button").addClass('hidden');

        } else if (response.status === 'not_authorized') {
            // the user is logged in to Facebook,
            // but has not authenticated your app
        } else {
            // the user isn't logged in to Facebook.
        }
    });*/
};

// Load the SDK asynchronously
(function(d){
    var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
    if (d.getElementById(id)) {return;}
    js = d.createElement('script'); js.id = id; js.async = true;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    ref.parentNode.insertBefore(js, ref);
}(document));


if( typeof (Fb) == "undefined"){
    var Fb = new Object();
}

(function (){
    Fb.fbLogin = function (){
        var fbResponse = function (response){
            if(response.status === 'connected'){
                accessToken = response.authResponse.accessToken;
                FB.api('/me', Fb.signUpUser(accessToken));
            }else if(response.status ==='unknown'){
                $("#fb-login-error").text('An error occured.').removeClass('hidden');
            }
        }
        if( typeof FB != 'undefined' ) {
            FB.login(fbResponse, {scope: appPermissions});
            fbResponse.loaderDiv = '*';
        }else{
            alert('Internet connection problem.');
            return false;
        }

    }

    /*Fb.setRequestHeader = function(){
        Main.addRequestHeader('User-Access-Token', accessToken);
        Main.addRequestHeader('Client-Id', clientId);
    }*/


    Fb.signUpUser = function (accessToken){
        var signUpCallback  =   function (user){
                var href =  window.location.href;
                var referred_by = href.split("/").reverse()[0];
                if(!referred_by) {
                    alert('Invalid referral.');
                    return false;
                }
                console.log(user);
                var user_info = {
                    "fullName":user.name,
                    "gender": user.gender?user.gender.toUpperCase():null,
                    "emailAddress":(typeof user.email !='undefined')? user.email:null,
                    "customer":{
                        "profileUrl":user.link,
                        "facebookId":user.id
                    },
                    "role":{
                        "role":"ROLE_CUSTOMER"
                    }
                };


                var callback = function (status, data) {
                    console.log(status);
                    if (data.success == true) {
                        $('#fbLoginBtn').css({'display':'none'});
                        $('#new_account_msg').css({'display':'block'});
                    } else {
                        if(data.code == 'VLD010'){
                            $('#fbLoginBtn').css({'display':'none'});
                            $('#old_account_msg').css({'display':'block'});
                        } else{
                            alert(data.message);
                        }
                    }
                };

                var headers = {};
                headers.id = referred_by;
                headers.accessToken = accessToken;
                callback.loaderDiv = '.well';
                Main.request("/anon/register_customer", user_info, callback, headers);
        };
        signUpCallback.loaderDiv = 'body';
        return signUpCallback;
    }

})();
