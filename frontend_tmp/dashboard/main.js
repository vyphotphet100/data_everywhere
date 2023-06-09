var token = null;
var user = null;
if (Base.getCookie("user") != null) {
    user = JSON.parse(Base.getCookie("user"));
    if (user != null) {
        token = user.token;
    }
}

if (user != null) {
    $("#log_in_btn").remove();
    $("#sign_up_btn").remove();
} else {
    $("#see_purchased_dataset").remove();
    $("#edit_user_details").remove();
    $("#log_out_btn").remove();
}

function logOut() {
    Base.setCookie("user", null, 0);
    window.location.href = Base.originUrl + "/log_in/index.html";
}