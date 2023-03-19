function logIn() {
    $.ajax({
        url: Base.chatbotUrl + '/api/user/login_from_data_everywhere',
        type: 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify({
            "username": $("#username").val(),
            "password": $("#password").val()
        }),
        dataType: 'json',
        success: function(result) {
            if (result == null || result.http_status == null) {
                alert("Something went wrong: " + JSON.stringify(result));
                return;
            }

            if (result.http_status != "OK") {
                alert("Login failed: " + result.exception_code);
                return;
            }

            logInSuccess(result);
            return result;
        }
    });
}

function logInSuccess(result) {
    window.close();
}


