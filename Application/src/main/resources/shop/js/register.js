
function register() {
    
    var password = $("#inputPassword1").val();
    var inputEmail = $("#input_email").val();

    $.ajax({
        type : 'POST',
        url : '/api/users/adduser',
        data : JSON.stringify({
            'username' : inputEmail,
            'password' : password,
            'email' : inputEmail
        }),
        dataType : 'json',
        contentType: "application/json"
    }).fail(function(result) {
        alert('error in register');
    }).done(function() {
        document.location.href="/";
    });
}