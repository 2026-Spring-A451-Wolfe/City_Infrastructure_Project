document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("loginForm");
    const email = document.getElementById("email");
    const password = document.getElementById("password");
    const message = document.getElementById("loginMessage");

    form.addEventListener("submit", async function (event) {
        event.preventDefault();

        message.textContent = "";
        message.style.color = "black";

        try {
            const response = await fetch("/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    emailOrPhone: email.value.trim(),
                    password: password.value.trim()
                })
            });

            const data = await response.json();

            if (response.ok && data.token) {
                localStorage.setItem("token", data.token);
                message.style.color = "green";
                message.textContent = "Login successful.";

                window.location.href = "/dashboard-page.html";
            } else {
                message.style.color = "red";
                message.textContent = data.message || "Login failed.";
            }
        } catch (error) {
            message.style.color = "red";
            message.textContent = "Could not connect to server.";
            console.error(error);
        }
    });
});