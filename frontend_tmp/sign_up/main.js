function signUp() {
    $.ajax({
        url: Base.baseUrl + '/api/user/sign_up',
        type: 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify({
            "username": $("#username").val(),
            "password": $("#password").val(),
            "confirm_password": $("#confirm_password").val(),
            "full_name": $("#full_name").val(),
            "birthday": $("#birthday").val(),
            "address": $("#address").val()
        }),
        dataType: 'json',
        success: function(result) {
            if (result == null || result.status == null || result.status.http_status == null) {
                alert("Something went wrong: " + JSON.stringify(result));
                return;
            }

            if (result.status.http_status != "OK") {
                alert("Sign up failed: " + result.status.exception_code);
                return;
            }

            signUpSuccess(result);
            return result;
        }
    });
}

function signUpSuccess(result) {
    if (result == null || 
        result.payload == null || 
        result.payload.id == null) {
            alert("Something went wrong");
            return;
    }

    alert("Sign up successfully!");
}