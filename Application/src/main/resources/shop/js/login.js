
function login() {
    var password = $("#inputPassword").val();
    var inputEmail = $("#inputEmail").val();

    $.ajax({
        type : 'POST',
        url : '/api/users/checkuser',
        data : JSON.stringify({
            'username' : inputEmail,
            'password' : password
        }),
        contentType: "application/json"
    }).done(function(token) {
        setCookie('jwt', token, 0.5);
        document.location.href="/";
    }).fail(function(result) {
        alert('username/password invalid');
    })
}

function setHTMLUsername() {
    var jwt = getCookie('jwt');
    var username = "Please login";

    if (jwt) {
        var decoded = jwt_decode(jwt);
        username = decoded.username;
        $('.not-logged').hide();
    } 

    $('#username-location').text(username);

}

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function setCookie(name,value,days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days*24*60*60*1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "")  + expires + "; path=/";
}

$(document).ready(function() {
    setHTMLUsername();
});