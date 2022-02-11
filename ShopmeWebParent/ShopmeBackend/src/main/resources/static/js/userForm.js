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
    const url = "[[@{/users/check_email}]]";

    // because we used th:field=*{email} so we know is a field
    const userEmail = $("#email").val();

    const csrfValue = $("input[name='_csrf']").val(); // get the csrf value for

    const userId = $("#id").val();

    const params = {id: userId, email: userEmail, _csrf: csrfValue};

    $.post(url, params, function (res) {
        if (res === "OK") {
            form.submit(); // submit the form if the response is OK
        } else if (res === "Duplicated") {
            showModalDialog("Warning", "This email already exists");
        } else {
            showModalDialog("Error", "Unknown response from server.")
        }
    }).fail(function () { // if fails to get the connection
        showModalDialog("Error", "Could not connect to the server");
    })
    return false;
}

function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal();
}
