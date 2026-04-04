// Since this script is loaded at the end of the body, we can just call it immediately!
fetchRealReports();

async function fetchRealReports() {
    const listContainer = document.querySelector('.content') || document.body;

    try {
        const token = localStorage.getItem('jwt');

<<<<<<< HEAD
    // Buttons and elements
const viewReportBtn = document.querySelector(".report-card__btn");
const cancelButtons = document.querySelectorAll(".cancel-btn"); // ✅ ADD THIS
const reportName = document.querySelector(".report-card__info p:first-of-type");
const reportDescription = document.querySelector(".report-card__info p:nth-of-type(2)");
const progressFill = document.querySelector(".progress__fill");
const profileCircle = document.querySelector(".sidebar__profile-circle");
const userInfo = document.querySelector(".sidebar__nav li");
    
    // Maybe sometype of message element for displaying status could be used here 
    let messageDiv = document.querySelector(".report-message");
    if (!messageDiv) {
        messageDiv = document.createElement("div");
        messageDiv.className = "report-message";
        messageDiv.style.marginTop = "10px";
        messageDiv.style.padding = "10px";
        messageDiv.style.borderRadius = "4px";
        
        // Find where to insert the message
        const reportCard = document.querySelector(".report-card");
        if (reportCard) {
            reportCard.appendChild(messageDiv);
        }
    }

    // Display for user information in the sidebar
    if (userInfo) {
        // This will be user's session or a backend API call
        const userName = "No Name"; // This will come from login data
        const userEmail = "noname@unknown.net"; // This will come from login data
        userInfo.textContent = `${userName} (${userEmail})`;
    }

    // Setting progress based on the status of the report 
    if (progressFill) {
        // This would come from backen data about the report's current status
        const reportStatus = "Under Review"; // Example status
        const progressStages = ["Submitted", "Processed", "Under Review", "Completed"];
        const currentStageIndex = progressStages.indexOf(reportStatus);
        
        if (currentStageIndex !== -1) {
            // Calculate progress percentage (25% per stage)
            const progressPercentage = (currentStageIndex + 1) * 25;
            progressFill.style.width = progressPercentage + "%";
            
            // Report status with colors  & Used randmon colors for each stage, this could be changed later if needed
            if (progressPercentage <= 25) {
                progressFill.style.backgroundColor = "#ff6b6b"; // Red for submitted
            } else if (progressPercentage <= 50) {
                progressFill.style.backgroundColor = "#ffd93d"; // Yellow for processed
            } else if (progressPercentage <= 75) {
                progressFill.style.backgroundColor = "#6b9cff"; // Blue for under review
            } else {
                progressFill.style.backgroundColor = "#51cf66"; // Green for completed
=======
        const response = await fetch('/reports', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
>>>>>>> servlet-refactor
            }
        });

        if (response.ok) {
            const data = await response.json();
            
            // Wipe out placeholder report cards but keep the title and divider
            const title = document.querySelector('.content__title');
            const divider = document.querySelector('.content__divider');
            listContainer.innerHTML = '';
            if (title) listContainer.appendChild(title);
            if (divider) listContainer.appendChild(divider);

            if (data.length === 0) {
                const emptyMsg = document.createElement('p');
                emptyMsg.innerText = "No reports found in the database.";
                listContainer.appendChild(emptyMsg);
                return;
            }

            // Loop through the real database rows and render them
            data.forEach(report => {
                const div = document.createElement('div');
                div.className = 'report-card';
                div.innerHTML = `
                    <div class="report-card__image">
                        <!-- We can put an image here if needed, for now just a placeholder -->
                        ${report.imageUrl ? `<img src="${report.imageUrl}" width="100" />` : 'No Image'}
                    </div>

                    <div class="report-card__info">
                        <p><strong>${report.title || 'Untitled Report'}</strong></p>
                        <p>${report.description || 'No description provided.'}</p>
                        
                        <p><strong>Category:</strong> ${report.category || 'N/A'} | <strong>Severity:</strong> ${report.severity || 'N/A'}</p>

                        <div class="progress">
                            <div class="progress__labels">
                                <span style="color: black;">Reported: ${(function(d){if(!d)return new Date().toLocaleDateString();if(Array.isArray(d))return new Date(d[0],d[1]-1,d[2]).toLocaleDateString();return new Date(d).toLocaleDateString()})(report.createdAt)}</span>
                                <span style="font-weight: bold; color: #d32f2f;">Status: ${report.status || 'Open'}</span>
                            </div>
                        </div>

                        <button class="report-card__btn" onclick="alert('Viewing report ID: ${report.id}')">View Report</button>
                    </div>
                `;
                listContainer.appendChild(div);
            });

        } else {
            const errDiv = document.createElement('p');
            errDiv.style.color = 'red';
            errDiv.innerText = 'Failed to load reports. Are you logged in?';
            listContainer.appendChild(errDiv);
        }
    } catch (err) {
        console.error('Error fetching reports:', err);
    }
}


<<<<<<< HEAD
    // Maybe Add some type of functionality to navigation links later pn 
    const navLinks = document.querySelectorAll(".sub-nav a");
    navLinks.forEach(link => {
        link.addEventListener("click", function (event) {
            // Analytics could be added here (backend tracking)
            console.log("Navigating to:", link.textContent);
        });
    });

    /* cancel button functionality (event delegation) --cw*/

document.addEventListener("click", function (e) {
    if (e.target.classList.contains("cancel-btn")) {

        const card = e.target.closest(".report-card");

        const confirmDelete = confirm("Are you sure you want to cancel this report?");

        if (confirmDelete && card) {
            card.remove();
        }
    }
});

    // Initialize page with sample data
    function initializePageData() {
        // This will fetch data from backend 
        console.log("Reports page initialized");
        
        // Example: You could fetch list of reports for this user (Backend API call)

    }
    
    // Call initialization
    initializePageData();

});
=======
>>>>>>> servlet-refactor
