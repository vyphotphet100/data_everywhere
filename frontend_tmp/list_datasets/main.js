var token = "";
var user = null;
if (Base.getCookie("user") != null) {
    user = JSON.parse(Base.getCookie("user"));
    if (user != null) {
        token = user.token;
    }
}

var datasetCollectionColumnStr = `
<tr>
    <td><a href="__href__">__id__<a></td>
    <td>__name__</td>
    <td>__short_description__</td>
    <td>__description__</td>
    <td>__picture__</td>
    <td>__preview__</td>
    <td>__amount__</td>
</tr>
`;

var command = {
    "page" : 1,
    "size": 5,
}
$("#page").html(command.page);
this.getPaginatedDatasetCollectionList(command);

function getPaginatedDatasetCollectionList(command) {
    if (command.page == null || command.size == null) {
        return;
    }

    $.ajax({
        url: Base.baseUrl + '/api/dataset_collection/',
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

            getPaginatedDatasetCollectionListSuccess(result);
            return result;
        }
    });
}

function getPaginatedDatasetCollectionListSuccess(result) {
    if (result.payload == null || result.payload.items == null) return;
    for (var i = 0; i<result.payload.items.length; i++) {
        var datasetCollection = result.payload.items[i];
        var datasetCollectionColumnStrTmp = datasetCollectionColumnStr;
        
        datasetCollectionColumnStrTmp = datasetCollectionColumnStrTmp.replace("__id__", datasetCollection.id);
        datasetCollectionColumnStrTmp = datasetCollectionColumnStrTmp.replace("__name__", datasetCollection.name);
        datasetCollectionColumnStrTmp = datasetCollectionColumnStrTmp.replace("__short_description__", datasetCollection.short_description);
        datasetCollectionColumnStrTmp = datasetCollectionColumnStrTmp.replace("__description__", datasetCollection.description);
        datasetCollectionColumnStrTmp = datasetCollectionColumnStrTmp.replace("__picture__", datasetCollection.picture);
        datasetCollectionColumnStrTmp = datasetCollectionColumnStrTmp.replace("__preview__", datasetCollection.preview);
        datasetCollectionColumnStrTmp = datasetCollectionColumnStrTmp.replace("__amount__", datasetCollection.amount);
        datasetCollectionColumnStrTmp = datasetCollectionColumnStrTmp.replace("__href__", Base.originUrl + "/dataset_collection_detail/?id=" + datasetCollection.id);        
        $("#dataset_container_body").html($("#dataset_container_body").html() + datasetCollectionColumnStrTmp);
    }
} 

function nextPage() {
    $("#dataset_container_body").html("")
    command.page++;
    $("#page").html(command.page);
    getPaginatedDatasetCollectionList(command);
}

function prevPage() {
    if (command.page == 1) return;

    $("#dataset_container_body").html("")
    command.page--;
    $("#page").html(command.page);
    getPaginatedDatasetCollectionList(command);
}

function searchByKeyword() {
    var keyword = $("#keyword").val();
    if (keyword == null || keyword.trim() == "") {
        command.page = 1;
        if (command.keyword != null) {
            command.keyword = null;
        }

        $("#dataset_container_body").html("")
        $("#page").html(command.page);
        this.getPaginatedDatasetCollectionList(command);
        return;
    }

    command.page = 1;
    command.keyword = keyword;
    $("#dataset_container_body").html("")
    $("#page").html(command.page);
    this.getPaginatedDatasetCollectionList(command);
}