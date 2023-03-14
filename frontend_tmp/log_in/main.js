function logIn(username, password) {
    $.ajax({
        url: Base.baseUrl + '/api/user/log_in',
        type: 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify({
            "username": $("#username").val(),
            "password": $("#password").val()
        }),
        dataType: 'json',
        success: function(result) {
            if (result == null || result.status == null || result.status.http_status == null) {
                alert("Something went wrong: " + JSON.stringify(result));
                return;
            }

            if (result.status.http_status != "OK") {
                alert("Login failed: " + result.status.exception_code);
                return;
            }

            logInSuccess(result);
            return result;
        }
    });
}

function logInSuccess(result) {
    if (result == null || 
        result.payload == null || 
        result.payload.id == null || 
        result.payload.token == null) {
            alert("Something went wrong");
            return;
    }

    Base.setCookie("user", JSON.stringify(result.payload), 60*24);
    alert("Login successfully!");
    window.location.href = Base.originUrl + "/dashboard/";
}