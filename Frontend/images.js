/*
 * Author: Adin Hultin
 * Purpose: Connects the frontend to the backend image endpoints.
 *          Handles uploading images to reports and displaying them.
 * Last Modified: 03/26/2026
 */

const IMAGE_API = "/api/images";

// Get the JWT token from localStorage (set during login)
function getToken() {
    return localStorage.getItem("token");
}

// Upload an image file to a specific report
// POST /api/images/{reportId} — matches ImageController.java
async function uploadImage(reportId, imageFile) {
    const token = getToken();

    if (!token) {
        throw new Error("You must be logged in to upload images.");
    }

    if (!imageFile) {
        throw new Error("No image file selected.");
    }

    // FormData lets us send the file as a multipart upload
    const formData = new FormData();
    formData.append("image", imageFile);

    const response = await fetch(`${IMAGE_API}/${reportId}`, {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token
        },
        body: formData
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || data.error || "Image upload failed.");
    }

    return data;
}

// Get all images for a report
// GET /api/images/report/{reportId} — matches ImageController.java
async function getReportImages(reportId) {
    const response = await fetch(`${IMAGE_API}/report/${reportId}`);
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.error || "Failed to fetch images.");
    }

    return data;
}

// Display an image in a given HTML element
// Takes the image URL from the report data and puts it in the container
function displayImage(containerElement, imageUrl) {
    containerElement.innerHTML = "";

    if (!imageUrl) {
        containerElement.textContent = "No image attached";
        return;
    }

    const img = document.createElement("img");
    img.src = imageUrl;
    img.alt = "Report photo";
    img.style.width = "100%";
    img.style.height = "100%";
    img.style.objectFit = "cover";
    img.style.borderRadius = "10px";
    containerElement.appendChild(img);
}

// Hook up the file input on the submission page
// Call this when the submission page loads
function setupImageUpload(fileInputSelector, reportId) {
    const fileInput = document.querySelector(fileInputSelector);

    if (!fileInput) return;

    fileInput.addEventListener("change", async function () {
        const file = fileInput.files[0];

        if (!file) return;

        // Check file type before sending to server
        const allowed = ["image/jpeg", "image/png", "image/webp"];
        if (!allowed.includes(file.type)) {
            alert("Only JPEG, PNG, and WEBP images are allowed.");
            fileInput.value = "";
            return;
        }

        // Check file size (5MB max, same as backend limit)
        if (file.size > 5 * 1024 * 1024) {
            alert("Image must be under 5MB.");
            fileInput.value = "";
            return;
        }

        try {
            const result = await uploadImage(reportId, file);
            alert("Image uploaded successfully!");
            console.log("Upload result:", result);
        } catch (error) {
            alert("Upload failed: " + error.message);
            console.error("Upload error:", error);
        }
    });
}