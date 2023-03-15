var token = "";
var user = null;
if (Base.getCookie("user") != null) {
    user = JSON.parse(Base.getCookie("user"));
    if (user != null) {
        token = user.token;
    }
}

var datasetItemColumnStr = `
<tr>
    <td>__id__</td>
    <td>__name__</td>
    <td>__path__</td>
    <td><button onclick="download('__dataset_item_id__');">Download</button></td>
</tr>
`;

var datasetCollection = null;

var id = Base.getAllUrlParams().id;
if (id == null || id.trim() == "") {
    alert("id_null");
    history.back();
} 
this.getDatasetCollectionById(id);

function getDatasetCollectionById(id) {
    if (id == null || id.trim() == "") {
        return;
    } 

    $.ajax({
        url: Base.baseUrl + '/api/dataset_collection/' + id,
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

            getDatasetCollectionByIdSuccess(result);
            return result;
        }
    });
}

function getDatasetCollectionByIdSuccess(result) {
    if (result.payload == null) return;
    datasetCollection = result.payload;
    $("#id").html(datasetCollection.id);
    $("#name").html(datasetCollection.name);
    $("#short_description").html(datasetCollection.short_description);
    $("#description").html(datasetCollection.description);
    $("#picture").html(datasetCollection.picture);
    $("#preview").html(datasetCollection.preview);
    $("#amount").html(datasetCollection.amount);

    if (datasetCollection.purchased != null && 
        datasetCollection.purchased == true &&
        datasetCollection.dataset_items != null) {
            $("#add_to_cart_btn").remove();

            for (var i=0; i<datasetCollection.dataset_items.length; i++) {
                var datasetItemColumnStrTmp = datasetItemColumnStr;
                datasetItemColumnStrTmp = datasetItemColumnStrTmp.replace("__id__", datasetCollection.dataset_items[i].id);
                datasetItemColumnStrTmp = datasetItemColumnStrTmp.replace("__name__", datasetCollection.dataset_items[i].name);
                datasetItemColumnStrTmp = datasetItemColumnStrTmp.replace("__path__", datasetCollection.dataset_items[i].path);
                datasetItemColumnStrTmp = datasetItemColumnStrTmp.replace("__dataset_item_id__", datasetCollection.dataset_items[i].id);
                $("#dataset_item_container_body").html($("#dataset_item_container_body").html() + datasetItemColumnStrTmp);
            }
    } else {
        $("#dataset_item_container").remove();
    }
}

function seePreview() {
    window.location.href = Base.baseUrl + "/api/dataset_collection/preview/" + id;
}

function addToCart() {
    if (user == null) {
        alert("You need to log in to do this action");
        return;
    }

    $.ajax({
        url: Base.baseUrl + '/api/cart_item/add',
        type: 'POST',
        async: false,
        contentType: 'application/json',
        data: JSON.stringify({
            "dataset_collection_id": id
        }),
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

            addToCartSuccess(result);
            return result;
        }
    });
}

function addToCartSuccess(result) {
    if (result.payload == null) return;

    alert("Add to cart successfully!");
}

function download(datasetItemId) {
    if (datasetCollection == null || 
        datasetCollection.dataset_items == null || 
        datasetCollection.dataset_items == []) {
            return;
        }

    var path = null;
    for (var i=0; i<datasetCollection.dataset_items.length; i++) {
        if (datasetCollection.dataset_items[i].id == datasetItemId) {
            path = datasetCollection.dataset_items[i].path;
            break;
        }
    }

    if (path == null) return;
    window.location.href = Base.baseUrl + "/api/file/?path=" + path;
}
