function clearFilter() {
    window.location = moduleURL;
}

function showDeleteConfirmationModal(link, entityName) {
    const entityId = link.attr("entityId");

    $("#yesButton").attr("href", link.attr("href"));
    $("#confirmText").text("Are you sure you want to delete this " + entityName + " ID " + entityId + "?");
    $("#confirmModal").modal();
}