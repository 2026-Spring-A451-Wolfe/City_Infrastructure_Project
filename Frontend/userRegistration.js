/* Author: Ellie Carroll
Purpose: Javascript functionality for the user registration page 
Last Modified: 3/9/2026 */

/* Still Required: 
- backend password and email formatting verification
- send login credentials to the backend for verification
*/

document.addEventListener("DOMContentLoaded", function () {

    const username = document.getElementById("username");
    const email = document.getElementById("email");
    const password = document.getElementById("password");
    const confirmPassword = document.getElementById("confirmPassword");
    const signupBtn = document.getElementById("signupButton");
    const message = document.getElementById("confirmation");

    signupBtn.addEventListener("click", async function () {

        const usernameValue = username.value.trim();
        const emailValue = email.value.trim();
        const passwordValue = password.value.trim();
        const confirmPasswordValue = confirmPassword.value.trim();

        // clearing the last entry
        message.textContent = "";
        message.style.color = "black";

        // check if text field has content
        if (!usernameValue || !emailValue || !passwordValue || !confirmPasswordValue) {
            message.textContent = "Please fill in all fields.";
            return;
        }

        // check username length
        if (usernameValue.length < 3) {
            message.textContent = "Username must be at least 3 characters.";
            return;
        }

        // check email formatting
        const emailPattern = /^[^ ]+@[^ ]+\.[a-z]{2,}$/i;
        if (!emailValue.match(emailPattern)) {
            message.textContent = "Please enter a valid email address.";
            return;
        }

        // password strength verification
        if (passwordValue.length < 8) {
            message.textContent = "Password must be at least 8 characters long.";
            return;
        }

        // password must include at least 1 uppercase, 1 number, and 1 special character
        if (!/[A-Z]/.test(passwordValue)) {
            message.textContent = "Password must contain at least one uppercase letter.";
            return;
        }

        if (!/[0-9]/.test(passwordValue)) {
            message.textContent = "Password must contain at least one number.";
            return;
        }

        if (!/[!@#$%^&*()]/.test(passwordValue)) {
            message.textContent = "Password must contain at least one special character.";
            return;
        }

        // check password formatting with each other 
        if (passwordValue !== confirmPasswordValue) {
            message.textContent = "Passwords do not match.";
            return;
        }

        try {
            const response = await fetch("/api/auth/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username: usernameValue,
                    emailOrPhone: emailValue,
                    password: passwordValue
                })
            });

            const data = await response.json();

            if (response.ok) {
                message.style.color = "green";
                message.textContent = "Registration successful. Redirecting to login...";
                setTimeout(function () {
                    window.location.href = "/login-page.html";
                }, 1500);
            } else {
                message.style.color = "red";
                message.textContent = data.message || "Registration failed.";
            }
        } catch (error) {
            message.style.color = "red";
            message.textContent = "Could not connect to server.";
            console.error(error);
        }

    });

});