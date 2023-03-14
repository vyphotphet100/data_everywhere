var token = null;
var user = null;
if (Base.getCookie("user") != null) {
    user = JSON.parse(Base.getCookie("user"));
    if (user != null) {
        token = user.token;
    }
}

if (user != null) {
    $("#log_in_btn").remove()
    $("#sign_up_btn").remove()
}