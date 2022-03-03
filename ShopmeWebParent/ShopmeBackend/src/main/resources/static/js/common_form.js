/****** user_form ******/
$(document).ready(function () {

    $("#buttonCancel").on("click", function () {
        window.location = moduleURL;
    })

    $("#fileImage").on("click", function () {

        const fileSize = this.files[0].size;
        alert("File size: " + fileSize);

        if (fileSize > 102400) {
            this.setCustomValidity("You must choose an image that is less than 100KB!")
            this.reportValidity();
        } else {
            this.setCustomValidity("")
            showImageThumbnail(this);
        }
    })
});

function showImageThumbnail(fileInput) {
    const file = fileInput.files[0];
    const reader = new FileReader();

    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result);
    }
    reader.readAsDataURL(file);
}

function checkEmailUnique(form) {
    const url = checkEmailURL;

    // because we used th:field=*{email} so we know is a field
    const userEmail = $("#email").val();

    const csrfValue = $("input[name='_csrf']").val(); // get the csrf value for

    const userId = $("#id").val();

    const params = {id: userId, email: userEmail, _csrf: csrfValue};

   $.post(url, params, function(response) {
       if(response === "OK") {
           form.submit();
       } else if(response === "Duplicated") {
           showWarningModal("This email already exists " + userEmail);
       } else {
           showErrorModal("Unknown response from server");
       }

   }).fail(function() {
       showErrorModal("Could not connect to the server");
   })


    return false;
}

function checkUnique(form) {
    const catId = $("#id").val();
    const catName = $("#name").val();
    const catAlias = $("#alias").val();

    const csrfValue = $("input[name='_csrf']").val();

    const categoryURL = checkCategoryURL;

    const params = {id: catId, name: catName, alias: catAlias, _csrf: csrfValue};

    $.post(categoryURL, params, function(response) {
        if(response === "OK") {
            form.submit();
        } else if(response === "Duplicated Name") {
            showWarningModal("There is another category having the same name " + "'" + catName + "'");
        } else if(response === "Duplicated Alias") {
            showWarningModal("There is another category having the same alias " + "'" + catAlias + "'");
        } else {
            showErrorModal("Unknown response from server");
        }
    }).fail(function() {
        showErrorModal("Could not connect to the server");
    });

    return false;
}

function brandCheckUnique(form) {
    const brandId = $("#id").val();
    const brandName = $("#name").val();

    const csrfValue = $("input[name='_csrf']").val();

    const categoryURL = brandChekUniqueURL;

    const params = {id: brandId, name: brandName, _csrf: csrfValue};

    $.post(categoryURL, params, function(response) {
        if(response === "OK") {
            form.submit();
        } else if(response === "Duplicate") {
            showWarningModal("There is another brand having the same name " + "'" + brandName + "'");
        } else {
            showErrorModal("Unknown response from server");
        }
    }).fail(function() {
        showErrorModal("Could not connect to the server");
    });

    return false;
}

function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal();
}

function showErrorModal(message) {
    showModalDialog("Error", message);
}

function showWarningModal(message) {
    showModalDialog("Warning", message);
}
