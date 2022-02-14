// logout function
$(document).ready(function () {
    $("#logoutLink").on("click", function (e) {
        e.preventDefault();
        document.logoutForm.submit();
    });

    $("#buttonCancel").on("click", function () {
        window.location = moduleURL;
    })

    customizeDropdownMenu();
});

function customizeDropdownMenu() {

    // fixed animation for logout
    $(".navbar .dropdown").hover(function () {
            $(this).find('.dropdown-menu').first().stop(true, true).delay(250).slideDown();
        },
        function () {
            $(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();
        })


    $(".dropdown > a").click(function () {
        location.href = this.href;
    })
}

// show dropdown item logout button
$(".dropdown-toggle").on("click", function () {
    $(".dropdown-menu").toggle();
})


/****** account_form ******/
function checkPasswordMatch(confirmPassword) {
    if(confirmPassword.value !== $("password").val()) {
        confirmPassword.setCustomValidity("Passwords do not match. Please try again");
    } else {
        confirmPassword.setCustomValidity();
    }
}



