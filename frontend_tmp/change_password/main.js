var token = null;
var user = null;

init();
function init() {
    if (Base.getCookie("user") == null) {
        window.location.href = Base.originUrl + "/log_in";
        return;
    }

    user = JSON.parse(Base.getCookie("user"));
    if (user == null) {
        window.location.href = Base.originUrl + "/log_in";
        return;
    }
    token = user.token;
}

function save() {
    var currPass = $("#current_password").val();
    var newPass = $("#new_password").val();
    var confirmPass = $("#confirm_password").val();

    if (currPass == null || currPass.trim() == "" ||
        newPass == null || newPass.trim() == "" ||
        confirmPass == null || confirmPass.trim() == "") {
            alert("Missing param!");
            return;
    }

    $("#loading")[0].style = "display: block;";

    $.ajax({
        url: Base.baseUrl + '/api/user/update_password',
        type: 'POST',
        async: true,
        contentType: 'application/json',
        headers: {
            "Authorization": "Token " + token
        },
        data: JSON.stringify({
            "current_password": currPass,
            "new_password": newPass,
            "confirm_password": confirmPass
        }),
        dataType: 'json',
        success: function(result) {
            if (result == null || result.status == null || result.status.http_status == null) {
                alert("Something went wrong: " + JSON.stringify(result));
                return;
            }

            if (result.status.http_status != "OK") {
                $("#loading")[0].style = "display: none;";
                alert("Failed: " + result.status.exception_code);
                return;
            }

            saveSuccess(result);
            return result;
        }
    });

}

function saveSuccess(result) {
    if (result == null || result.payload == null) {
        return;
    }

    $("#loading")[0].style = "display: none;";
    alert("Save successfully!");
    Base.setCookie("user", null, 0);
    window.location.href = Base.originUrl + "/log_in/index.html"
}