/* Author: Ellie Carroll
Purpose: Javascript functionality for the user registration page
Last Modified: 3/27/2026 -CW */

/*
Still Required:
- Confirm exact backend request field names if backend does not accept { email, password }
*/

document.addEventListener("DOMContentLoaded", function () {
    const registrationForm = document.getElementById("registrationForm");
    const email = document.getElementById("email");
    const password = document.getElementById("password");
    const confirmPassword = document.getElementById("confirmPassword");
    const signupButton = document.getElementById("signupButton");
    const message = document.getElementById("confirmation");

    if (!registrationForm || !email || !password || !confirmPassword || !signupButton || !message) {
        console.error("Registration page is missing one or more required elements.");
        return;
    }

    registrationForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const emailValue = email.value.trim();
        const passwordValue = password.value.trim();
        const confirmPasswordValue = confirmPassword.value.trim();

        message.textContent = "";
        message.style.color = "black";
        signupButton.disabled = true;
        signupButton.textContent = "Signing up...";

        try {
            if (!emailValue || !passwordValue || !confirmPasswordValue) {
                message.textContent = "Please fill in all fields.";
                return;
            }

            const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailPattern.test(emailValue)) {
                message.textContent = "Please enter a valid email address.";
                return;
            }

            if (passwordValue.length < 10) {
                message.textContent = "Password must be at least 10 characters long.";
                return;
            }

            const passwordPattern = /^(?=(?:.*\d){2,})(?=.*[!@#$%^&*(),.?":{}|<>_\-\\[\]\/+=~`]).+$/;
            if (!passwordPattern.test(passwordValue)) {
                message.textContent = "Password must include at least 2 numbers and 1 special character.";
                return;
            }

            if (passwordValue !== confirmPasswordValue) {
                message.textContent = "Passwords do not match.";
                return;
            }

            const response = await fetch("/api/auth/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: emailValue,
                    password: passwordValue
                })
            });

            let data = {};
            const rawText = await response.text();

            try {
                data = rawText ? JSON.parse(rawText) : {};
            } catch (parseError) {
                data = { message: rawText || "Registration failed." };
            }

            if (!response.ok) {
                throw new Error(data.message || "Registration failed.");
            }

            message.style.color = "green";
            message.textContent = "Registration successful! Redirecting to login...";

            setTimeout(function () {
                window.location.href = "login-page.html";
            }, 1500);

        } catch (error) {
            console.error("Registration error:", error);
            message.style.color = "red";
            message.textContent = error.message || "Something went wrong during registration.";
        } finally {
            signupButton.disabled = false;
            signupButton.textContent = "Sign up!";
        }
    });
});