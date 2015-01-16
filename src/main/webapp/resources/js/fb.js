var appPermissions = 'email,user_birthday,user_friends';
var accessToken;
var clientId;
var getBasicInfo = true;
var apId;
var clientEmail;

window.fbAsyncInit = function() {
    var hostName = window.location.hostname;
    //set the FB AppId
    if(hostName=="localhost"){
         apId = "685480531477032";
    }else if(hostName=="delivr.com" || hostName=="www.delivr.com")
        apId = "645244498901500";
    else
         apId = "645244498901500";//764634910220474


    // init the FB JS SDK                                                            n
    FB.init({
        version    : 'v2.1',
        appId      : apId,                        // App ID from the app dashboard
        status     : true,                                 // Check Facebook Login status
        cookie     : true, // enable cookies to allow the server to access the session
        xfbml      : true,
        oauth: true// Look for social plugins on the page
    });

    if(typeof(logginCheck) != 'undefined' && logginCheck){
        FB.getLoginStatus(function(response) {
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
        });
    }
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
    Fb.fbLogin = function (referal){
        var fbResponse = function (response){
            if(response.status === 'connected'){
                accessToken = response.authResponse.accessToken;
                FB.api('/me', Fb.signUpUser(accessToken, referal));
            }else if(response.status ==='unknown'){
                $("#fb-login-error").text('An error occured.').removeClass('hidden');
            }
        }
        if(referal){
            if( referal && typeof FB != 'undefined' ) {
                FB.login(fbResponse, {scope: appPermissions});
                fbResponse.loaderDiv = '*';
            }else{
                Main.open_alert('', 'Internet connection problem.', 'ok');
                return false;
            }
        } else {
            Main.open_alert('', 'Invalid parameter.', 'ok');
            return false;
        }
    }

    Fb.setRequestHeader = function(){
        Main.addRequestHeader('User-Access-Token', accessToken);
        Main.addRequestHeader('Client-Id', clientId);
    }


    Fb.signUpUser = function (accessToken, referal){
        var signUpCallback  =   function (user){
            if(referal){
                var referred_by =   Fb.getQueryParam("referral");
                if(!referred_by) {
                    Main.open_alert('', 'Invalid referral.', 'ok');
                    return false;
                }

                var relationship_status =   user.relationship_status==null? "NA" : user.relationship_status;

                var user_info = {
                    "clientId":user.id,
                    "name":user.name,
                    "profileUrl":user.link,
                    "dob":(typeof user.birthday !='undefined')? new Date(user.birthday).format("yyyy-mm-dd"):null,
                    "gender": user.gender?user.gender.toUpperCase():null,
                    "email":(typeof user.email !='undefined')? user.email:null,
                    "referredBy": referred_by,
                    "maritalStatus":user.maritalStatus?user.maritalStatus:''
                };
                var requestParam = {"parameter": JSON.stringify(user_info)};
                Main.request("/referral","POST","json", requestParam, Fb.webSignUpResponse);
            }else{
                clientEmail = (typeof user.email !='undefined')? user.email:null;
                clientId = user.id;
                var userInfo = {
                    "userInfo": {
                        "city": user.city,
                        "country": user.country,
                        "dob": (typeof user.birthday !='undefined')? new Date(user.birthday).format("yyyy-mm-dd"):null,
                        "email": clientEmail,
                        "clientId": user.id,
                        "gender": user.gender?user.gender.toUpperCase():null,
                        "mobile": user.mobile,
                        "name": user.name,
                        "profileUrl": user.link,
                        "state": user.state,
                        "street": user.street,
                        "token": accessToken,
                        "maritalStatus": user.maritalStatus?user.maritalStatus:''
                    }
                }
                Fb.webSignInResponse.loaderDiv = '*';
                Fb.setRequestHeader();
                Main.mainServiceRequest("ClientService.clientLogin",  userInfo, "POST", Fb.webSignInResponse)
            }
        };
        signUpCallback.loaderDiv = '*';
        return signUpCallback;
    }

})();
