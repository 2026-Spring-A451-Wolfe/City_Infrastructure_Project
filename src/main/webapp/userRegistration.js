/* Author: Ellie Carroll
Purpose: Javascript functionality for the user registration page 
Last Modified: 3/9/2026 */

/* Still Required: 
- backend integration
- backend password and email formatting verification
- send login credentials to the backend for verification
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

         // check email formatting
        const emailPattern = /^[^ ]+@[^ ]+\.[a-z]{2,3}$/;
        if (!emailValue.match(emailPattern)) {
            message.textContent = "Please enter a valid email address.";
            return;
        }

        // password strength verification
        if (passwordValue.length < 10) {
            message.textContent = "Password must be at least 10 characters long.";
            return;
        }

        // password must include at least 2 numbers and 1 special character
        const passwordPattern = /^(?=(?:.*\d){2,})(?=.*[!@#$%^&*(),.?":{}|<>_\-\\[\]\/+=~`]).+$/;

        if (!passwordValue.match(passwordPattern)) {
            message.textContent = "Password must include at least 2 numbers and 1 special character.";
            return;
        }

        // check password formatting with eachother 
        if (passwordValue !== confirmPasswordValue) {
            message.textContent = "Passwords do not match.";
            return;
        }

        // successful registration
        message.style.color = "green";

        // no longer sending confirmation email
       // message.textContent = "A confirmation email will be sent to the provided email address.";

       // send data to the backend, all checks must be preformed again on the backend
        console.log("User registered:", emailValue);
    });

});

