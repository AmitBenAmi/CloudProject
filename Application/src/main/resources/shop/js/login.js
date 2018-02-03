
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
        dataType : 'json',
        contentType: "application/json"
    }).done(function(result) {
        print(result);
    })

}