﻿/* Author: Javier Garcia
Purpose: Javascript functionality for the updates page
Last Modified: 3/08/2026 */

// Wait until the page is fully loaded
document.addEventListener("DOMContentLoaded", initUpdatesPage);

async function initUpdatesPage() {
    const container = document.getElementById("updates-container");
    if (!container) return;

    // Remove the temporary template cards from the HTML
    clearTempCards(container);

    const token = localStorage.getItem('jwt');
    if (!token) {
        container.innerHTML += "<p style='color:red'>You must be logged in to view updates.</p>";
        return;
    }

    try {
        const response = await fetch('/reports', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const reports = await response.json();
            
            // To simulate "updates", we'll just render the reports differently here!
            reports.forEach(report => {
                const card = createUpdateCard(report);
                container.appendChild(card);
            });

            if(reports.length === 0) {
                 const msg = document.createElement("p");
                 msg.innerText = "No recent updates to display from the database.";
                 container.appendChild(msg);
            }
        } else {
             container.innerHTML += "<p style='color:red'>Network error, cannot fetch database updates.</p>";
        }
    } catch(err) {
         console.error("Updates page error:", err);
    }
}

/*
CREATE UPDATE CARD
Builds the HTML structure from the real report data fetched from the DB
*/
function createUpdateCard(report) {

    const card = document.createElement("div");
    card.className = "update-card";

    const info = document.createElement("div");
    info.className = "update-card__info";

    const title = document.createElement("h3");
    title.textContent = `Report: ${report.title || 'Untitled'}`;

    const statusChange = document.createElement("p");
    statusChange.innerHTML = `Most recent severity: <strong>${report.severity || 'Unrated'}</strong>`;

    const date = document.createElement("span");
    date.className = "update-card__date";
    // fallback if createdDate is missing
    const reportDate = report.createdAt || Date.now();
    date.textContent = `Date: ${(function(d){if(Array.isArray(d))return new Date(d[0],d[1]-1,d[2]).toLocaleDateString();return new Date(d).toLocaleDateString()})(reportDate)}`;

    info.appendChild(title);
    info.appendChild(statusChange);
    info.appendChild(date);

    const statusBox = document.createElement("div");
    statusBox.className = "update-card__status status--review";
    statusBox.textContent = `Status: ${report.status || 'Open'}`;

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