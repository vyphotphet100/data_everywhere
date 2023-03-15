var cartItemColumnStr = `
<tr>
    <td>__id__</td>
    <td>__name__</td>
    <td>__short_description__</td>
    <td>__description__</td>
    <td>__picture__</td>
    <td>__preview__</td>
    <td>__amount__</td>
</tr>
`;

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

    var paymentId = Base.getAllUrlParams().paymentId;
    if (paymentId == null) {
        alert("Something went wrong");
        // window.location.href = Base.originUrl + "/list_cart_items/index.html";
        return;
    }

    // Get data of cart items of this payment
    $.ajax({
        url: Base.baseUrl + '/api/payment/paypal/cart_items/' + paymentId,
        type: 'GET',
        async: false,
        headers: {
            "Authorization": "Token " + token
        },
        contentType: 'application/json',
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

            getCartItemsSuccess(result);
            return result;
        }
    });
}

function getCartItemsSuccess(result) {
    if (result == null || result.payload == null || result.payload == []) {
        return;
    }

    var total = 0;
    for (var i=0; i<result.payload.length; i++) {
        var cartItem = result.payload[i];
        var cartItemColumnStrTmp = cartItemColumnStr;

        if (cartItem.dataset_collection == null) continue;    
        
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__id__", cartItem.dataset_collection.id);
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__cart_item_id__", cartItem.id);
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__name__", cartItem.dataset_collection.name);
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__short_description__", cartItem.dataset_collection.short_description);
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__description__", cartItem.dataset_collection.description);
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__picture__", cartItem.dataset_collection.picture);
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__preview__", cartItem.dataset_collection.preview);
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__amount__", cartItem.dataset_collection.amount);   
        $("#cart_item_container_body").html($("#cart_item_container_body").html() + cartItemColumnStrTmp);
        total += cartItem.dataset_collection.amount;
    }

    $("#total").html(total);
}

function pay() {
    var paymentId = Base.getAllUrlParams().paymentId;
    var PayerID = Base.getAllUrlParams().PayerID;

    if (paymentId == null || PayerID == null) {
        alert("Missing param!");
        return;
    }

    $("#loading")[0].style = "display: block;";

    $.ajax({
        url: Base.baseUrl + '/api/payment/paypal/execute_payment',
        type: 'POST',
        async: true,
        headers: {
            "Authorization": "Token " + token
        },
        data: JSON.stringify({
            "payment_id": paymentId,
            "payer_id": PayerID
        }),
        contentType: 'application/json',
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

            paySuccess(result);
            return result;
        }
    });
}

function paySuccess(result) {
    if (result == null || result.payload == null || result.payload.transaction == null) {
        return;
    }

    alert("Pay successfully!");
    window.location.href = Base.originUrl + "/dashboard/";
}




