
/* Author: Ellie Carroll
Purpose: Javascript functionality for the user registration page 
Last Modified: 2/27/2026 */

/* Required: 
 - email
 - password
 - password confirmation
 - text field validation
 - signup button
 - display message

*/

document.addEventListener("DOMContentLoaded", function () {

    const email = document.getElementById("email");
    const password = document.getElementById("password");
    const confirmPassword = document.getElementById("confirmPassword");
    const signupBtn = document.getElementById("signupBtn");
    const message = document.getElementById("message");

    signupBtn.addEventListener("click", function () {

        const emailValue = email.value.trim();
        const passwordValue = password.value.trim();
        const confirmPasswordValue = confirmPassword.value.trim();

        // clearing the last entry
        message.textContent = "";
        message.style.color = "black";

        // check if text field has content
        if (!emailValue || !passwordValue || !confirmPasswordValue) {
            message.textContent = "Please fill in all fields.";
            return;
        }

         // check email format
         // what other checks do there need to be? does the email need to be functional?
        const emailPattern = /^[^ ]+@[^ ]+\.[a-z]{2,3}$/;
        if (!emailValue.match(emailPattern)) {
            message.textContent = "Please enter a valid email address.";
            return;
        }

        // check password - how secure do we want the passwords to be? more password strength verification?
        if (passwordValue.length < 8) {
            message.textContent = "Password must be at least 8 characters long.";
            return;
        }

        // check password formatting with eachother 
        if (passwordValue !== confirmPasswordValue) {
            message.textContent = "Passwords do not match.";
            return;
        }

        // successful registration
        message.style.color = "green";
        message.textContent = "A confirmation email will be sent to the provided email address.";

        console.log("User registered:", emailValue);

        // send data to the backend,, how and where is it going?
    });

});

