var token = "";
var user = null;
if (Base.getCookie("user") != null) {
    user = JSON.parse(Base.getCookie("user"));
    if (user != null) {
        token = user.token;
    }
}

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
    var datasetCollection = result.payload;
    $("#id").html(datasetCollection.id);
    $("#name").html(datasetCollection.name);
    $("#short_description").html(datasetCollection.short_description);
    $("#description").html(datasetCollection.description);
    $("#picture").html(datasetCollection.picture);
    $("#preview").html(datasetCollection.preview);
    $("#amount").html(datasetCollection.amount);
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


