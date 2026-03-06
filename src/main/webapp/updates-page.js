/* Author: Javier Garcia
Purpose: Javascript functionality for the updates page
Last Modified: 3/06/2026 */


// Wait until the page is fully loaded
document.addEventListener("DOMContentLoaded", initUpdatesPage);

function initUpdatesPage() {
    const container = document.getElementById("updates-container");

    // Remove the temporary template cards from the HTML
    clearTempCards(container);

    // Fetch report data (currently mocked — replace with DB call later)
    fetchReportUpdates()
        .then(reports => {
            reports.forEach(report => {
                const card = createUpdateCard(report);
                container.appendChild(card);
            });
        })
        .catch(error => {
            console.error("Error loading reports:", error);
        });
}

/*
DATABASE CONNECTION AREA
Replace this function with the actual database fetching once database is in place
*/

async function fetchReportUpdates() {

    // TEST DATA (temporary until database is ready)
    const testDatabaseResults = [
        {
            reportName: "Pothole Spotted",
            statusChange: "Under Review",
            statusLabel: "Processed",
            date: "February 20, 2026"
        },
        {
            reportName: "Broken Streetlight",
            statusChange: "Assigned to Maintenance",
            statusLabel: "In Progress",
            date: "February 25, 2026"
        },
        {
            reportName: "Flooded Drain",
            statusChange: "Inspection Scheduled",
            statusLabel: "Pending",
            date: "March 1, 2026"
        }
    ];

    // Simulate async database delay
    return new Promise(resolve => {
        setTimeout(() => {
            resolve(testDatabaseResults);
        }, 300);
    });
}


/*
CREATE UPDATE CARD
Builds the HTML structure from the report data
*/

function createUpdateCard(report) {

    const card = document.createElement("div");
    card.className = "update-card";

    const info = document.createElement("div");
    info.className = "update-card__info";

    const title = document.createElement("h3");
    title.textContent = `Report Name: ${report.reportName}`;

    const statusChange = document.createElement("p");
    statusChange.innerHTML = `Status changed to: <strong>${report.statusChange}</strong>`;

    const date = document.createElement("span");
    date.className = "update-card__date";
    date.textContent = `Date: ${report.date}`;

    info.appendChild(title);
    info.appendChild(statusChange);
    info.appendChild(date);

    const statusBox = document.createElement("div");
    statusBox.className = "update-card__status status--review";
    statusBox.textContent = `Status: ${report.statusLabel}`;

    card.appendChild(info);
    card.appendChild(statusBox);

    return card;
}


/*
UTILITY
Removes temporary cards that already exist in the HTML
*/

function clearTempCards(container) {
    const cards = container.querySelectorAll(".update-card");
    cards.forEach(card => card.remove());
}