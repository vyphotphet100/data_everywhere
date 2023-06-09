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


var cartItemColumnStr = `
<tr>
    <td><a href="__href__">__id__<a></td>
    <td>__name__</td>
    <td>__short_description__</td>
    <td>__description__</td>
    <td>__picture__</td>
    <td>__preview__</td>
    <td>__amount__</td>
    <td><input type="checkbox" id="checkbox___cart_item_id__" onchange="updateListCartItemIdToDoAction();"></td>
</tr>
`;

var command = {
    "page" : 1,
    "size": 5,
    "has_dataset_collection": true,
    "purchased": false,
    "sort": {
        "field": "id",
        "direction": "DESC"
    }
}
$("#page").html(command.page);
var originCartItemIds = [];
this.getPaginatedCartItemList(command);

function getPaginatedCartItemList(command) {
    if (command.page == null || command.size == null) {
        return;
    }

    $.ajax({
        url: Base.baseUrl + '/api/cart_item/',
        type: 'POST',
        async: false,
        contentType: 'application/json',
        headers: {
            "Authorization": "Token " + token
        },
        data: JSON.stringify(command),
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

            getPaginatedCartItemListSuccess(result);
            return result;
        }
    });
}

function getPaginatedCartItemListSuccess(result) {
    if (result.payload == null || result.payload.items == null) return;
    for (var i = 0; i<result.payload.items.length; i++) {
        var cartItem = result.payload.items[i];
        var cartItemColumnStrTmp = cartItemColumnStr;

        if (cartItem.dataset_collection == null) continue;    
        
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__id__", cartItem.dataset_collection.id);
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__cart_item_id__", cartItem.id);
        originCartItemIds.push(cartItem.id);
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__name__", cartItem.dataset_collection.name);
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__short_description__", cartItem.dataset_collection.short_description);
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__description__", cartItem.dataset_collection.description);
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__picture__", cartItem.dataset_collection.picture);
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__preview__", cartItem.dataset_collection.preview);
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__amount__", cartItem.dataset_collection.amount);
        cartItemColumnStrTmp = cartItemColumnStrTmp.replace("__href__", Base.originUrl + "/dataset_collection_detail/?id=" + cartItem.dataset_collection.id);        
        $("#cart_item_container_body").html($("#cart_item_container_body").html() + cartItemColumnStrTmp);
    }
} 

function nextPage() {
    $("#cart_item_container_body").html("");
    originCartItemIds = [];
    cartItemIds = [];
    command.page++;
    $("#page").html(command.page);
    getPaginatedCartItemList(command);
}

function prevPage() {
    if (command.page == 1) return;

    $("#cart_item_container_body").html("");
    originCartItemIds = [];
    cartItemIds = [];
    command.page--;
    $("#page").html(command.page);
    getPaginatedCartItemList(command);
}

function searchByKeyword() {
    var keyword = $("#keyword").val();
    if (keyword == null || keyword.trim() == "") {
        command.page = 1;
        if (command.keyword != null) {
            command.keyword = null;
        }

        $("#cart_item_container_body").html("");
        originCartItemIds = [];
        cartItemIds = [];
        $("#page").html(command.page);
        this.getPaginatedCartItemList(command);
        return;
    }

    command.page = 1;
    command.keyword = keyword;
    $("#cart_item_container_body").html("")
    $("#page").html(command.page);
    this.getPaginatedCartItemList(command);
}


var cartItemIds = [];
function updateListCartItemIdToDoAction() {
    cartItemIds = [];
    for (var i=0; i<originCartItemIds.length; i++) {
        if ($("#checkbox_" + originCartItemIds[i])[0].checked) {
            cartItemIds.push(originCartItemIds[i]);
        }
    }
}

function purchase() {
    if (cartItemIds.length == 0) {
        alert("Nothing to purchase!");
        return;
    }

    $("#loading")[0].style = "display: block;";

    $.ajax({
        url: Base.baseUrl + '/api/payment/paypal/authorize_payment',
        type: 'POST',
        async: true,
        contentType: 'application/json',
        headers: {
            "Authorization": "Token " + token
        },
        data: JSON.stringify({
            "cart_item_ids": cartItemIds
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

            purchaseSuccess(result);
            return result;
        }
    });
}

function purchaseSuccess(result) {
    if (result.payload == null || result.payload.approval_link == null) {
        return;
    }

    window.location.href = result.payload.approval_link;
}

function removeFromCart() {
    if (cartItemIds.length == 0) {
        alert("Nothing to remove!");
        return;
    }
    
    $("#loading")[0].style = "display: block;";

    $.ajax({
        url: Base.baseUrl + '/api/cart_item/remove',
        type: 'POST',
        async: true,
        contentType: 'application/json',
        headers: {
            "Authorization": "Token " + token
        },
        data: JSON.stringify({
            "cart_item_ids": cartItemIds
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

            removeFromCartSuccess(result);
            return result;
        }
    });
}

function removeFromCartSuccess(result) {
    if (result == null || result.payload == null || result.payload == false) {
        return;
    }

    alert("Remove successfully!");
    window.location.reload();
}


