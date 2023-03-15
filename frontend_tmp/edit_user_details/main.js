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
    loadUserInfo();
}

function loadUserInfo() {
    $.ajax({
        url: Base.baseUrl + '/api/user/detail',
        type: 'GET',
        async: false,
        contentType: 'application/json',
        headers: {
            "Authorization": "Token " + token
        },
        dataType: 'json',
        success: function(result) {
            if (result == null || result.status == null || result.status.http_status == null) {
                alert("Something went wrong: " + JSON.stringify(result));
                return;
            }

            if (result.status.http_status != "OK") {
                alert("Failed: " + result.status.exception_code);
                return;
            }

            loadUserInfoSuccess(result);
            return result;
        }
    });
}

function loadUserInfoSuccess(result) {
    if (result == null || result.payload == null) {
        return;
    }

    $("#full_name").val(result.payload.full_name);
    $("#birthday").val(result.payload.birthday);
    $("#address").val(result.payload.address);
}

function save() {
    var fullName = $("#full_name").val();
    var birthday = $("#birthday").val();
    var address = $("#address").val();

    if (fullName == null || fullName.trim() == "" ||
        birthday == null || birthday.trim() == "" ||
        address == null || address.trim() == "") {
            alert("Missing param!");
            return;
    }

    $("#loading")[0].style = "display: block;";

    $.ajax({
        url: Base.baseUrl + '/api/user/update',
        type: 'POST',
        async: true,
        contentType: 'application/json',
        headers: {
            "Authorization": "Token " + token
        },
        data: JSON.stringify({
            "full_name": fullName,
            "birthday": birthday,
            "address": address
        }),
        dataType: 'json',
        success: function(result) {
            if (result == null || result.status == null || result.status.http_status == null) {
                alert("Something went wrong: " + JSON.stringify(result));
                return;
            }

            if (result.status.http_status != "OK") {
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
}

function changePassword() {
    window.location.href = Base.originUrl + "/change_password/index.html";
}



