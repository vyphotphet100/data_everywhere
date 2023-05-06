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
    // loadUserInfo();
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

function getExcelFile() {
    $.ajax({
        url: Base.chatbotUrl + '/api/pattern/export/excel/get_file/Training_data_09d531e7-d667-4a28-806f-e1fffc12ae1a_1682476917177xlsx',
        type: 'GET',
        async: true,
        contentType: 'application/json',
        headers: {
            "Authorization": "Token eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjp7InBhc3N3b3JkIjpudWxsLCJzZWNyZXRLZXkiOm51bGwsImlkIjpudWxsLCJmdWxsbmFtZSI6IlVzZXI3IiwidXNlcm5hbWUiOiJ1c2VyNyIsInRva2VuIjoiMTY4MDI4MjQxMDYzNyJ9fQ.a_XsrGk8a4OTukMe9CdaP6RAJPrQ-F4X2gCiO5bvUFs"
        },
        success: function(result) {
            downloadBase64File(result.base64, "abc.xlsx");
        }
    });
}

function downloadBase64File(contentBase64, fileName) {
    const linkSource = `data:application/pdf;base64,${contentBase64}`;
    const downloadLink = document.createElement('a');
    document.body.appendChild(downloadLink);

    downloadLink.href = linkSource;
    downloadLink.target = '_self';
    downloadLink.download = fileName;
    downloadLink.click(); 
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



